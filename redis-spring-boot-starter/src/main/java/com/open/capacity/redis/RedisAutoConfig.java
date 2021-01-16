package com.open.capacity.redis;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Sentinel;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.ReflectionUtils;

import com.open.capacity.redis.serializer.RedisObjectSerializer;
import com.open.capacity.redis.util.RedisUtil;

import io.lettuce.core.RedisClient;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;

/**
 * @author 作者 owen
 * @version 创建时间：2017年04月23日 下午20:01:06 类说明 redis自动装配
 */
@Configuration
@EnableCaching
@SuppressWarnings("all")
@AutoConfigureBefore(RedisTemplate.class)
@EnableConfigurationProperties(RedissonProperties.class)
public class RedisAutoConfig {

	@Autowired(required = false)
	private RedissonProperties redissonProperties;

	@Autowired
	private RedisProperties redisProperties;

	@Autowired
	private ApplicationContext ctx;

	@Bean(destroyMethod = "destroy")
	@ConditionalOnClass(RedisClient.class)
	public LettuceConnectionFactory lettuceConnectionFactory(GenericObjectPoolConfig genericObjectPoolConfig) {
		Method clusterMethod = ReflectionUtils.findMethod(RedisProperties.class, "getCluster");
		Method timeoutMethod = ReflectionUtils.findMethod(RedisProperties.class, "getTimeout");
		Object timeoutValue = ReflectionUtils.invokeMethod(timeoutMethod, redisProperties);
		RedisConfiguration redisConfiguration = null;
		LettuceClientConfiguration clientConfig = null;
		if (redisProperties.getSentinel() != null) {
			// 哨兵配置
			Method nodesMethod = ReflectionUtils.findMethod(Sentinel.class, "getNodes");
			Object nodesValue = ReflectionUtils.invokeMethod(nodesMethod, redisProperties.getSentinel());

			String[] nodes = null;
			Set<String> sentinelHostAndPorts = new HashSet<>();
			if (nodesValue instanceof String) {
				nodes = convert(Arrays.asList(((String) nodesValue).split(",")));
				sentinelHostAndPorts.addAll(Arrays.asList(((String) nodesValue).split(",")));
			} else {
				nodes = convert((List<String>) nodesValue);
				sentinelHostAndPorts.addAll((List<String>) nodesValue);
			}
			redisConfiguration = new RedisSentinelConfiguration(redisProperties.getSentinel().getMaster(),
					sentinelHostAndPorts);
			((RedisSentinelConfiguration) redisConfiguration)
					.setPassword(RedisPassword.of(redisProperties.getPassword()));
			((RedisSentinelConfiguration) redisConfiguration).setDatabase(redisProperties.getDatabase());
			clientConfig = LettucePoolingClientConfiguration.builder().commandTimeout(redisProperties.getTimeout())
					.poolConfig(genericObjectPoolConfig).build();

		} else if (clusterMethod != null && ReflectionUtils.invokeMethod(clusterMethod, redisProperties) != null) {
			// 集群配置
			List<String> clusterNodes = redisProperties.getCluster().getNodes();
			Set<RedisNode> nodes = new HashSet<RedisNode>();
			clusterNodes.forEach(address -> nodes
					.add(new RedisNode(address.split(":")[0].trim(), Integer.valueOf(address.split(":")[1]))));
			redisConfiguration = new RedisClusterConfiguration();
			((RedisClusterConfiguration) redisConfiguration).setClusterNodes(nodes);
			((RedisClusterConfiguration) redisConfiguration)
					.setPassword(RedisPassword.of(redisProperties.getPassword()));

			/**
			 * ClusterTopologyRefreshOptions配置用于开启自适应刷新和定时刷新。如自适应刷新不开启，
			 * Redis集群变更时将会导致连接异常！
			 */
			ClusterTopologyRefreshOptions topologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
					// 开启自适应刷新
					.enableAdaptiveRefreshTrigger(ClusterTopologyRefreshOptions.RefreshTrigger.MOVED_REDIRECT,
							ClusterTopologyRefreshOptions.RefreshTrigger.PERSISTENT_RECONNECTS)
					// 开启所有自适应刷新，MOVED，ASK，PERSISTENT都会触发
					// .enableAllAdaptiveRefreshTriggers()
					// 自适应刷新超时时间(默认30秒)
					.adaptiveRefreshTriggersTimeout(Duration.ofSeconds(25)) // 默认关闭开启后时间为30秒
					// 开周期刷新
					.enablePeriodicRefresh(Duration.ofSeconds(20)) // 默认关闭开启后时间为60秒
					// ClusterTopologyRefreshOptions.DEFAULT_REFRESH_PERIOD
					// 60 .enablePeriodicRefresh(Duration.ofSeconds(2)) =
					// .enablePeriodicRefresh().refreshPeriod(Duration.ofSeconds(2))
					.build();
			clientConfig = LettucePoolingClientConfiguration.builder().commandTimeout(redisProperties.getTimeout())
					.poolConfig(genericObjectPoolConfig)
					.clientOptions(
							ClusterClientOptions.builder().topologyRefreshOptions(topologyRefreshOptions).build())
					// 将appID传入连接，方便Redis监控中查看
					// .clientName(appName + "_lettuce")
					.build();

		} else {
			// 单机版配置
			redisConfiguration = new RedisStandaloneConfiguration();
			((RedisStandaloneConfiguration) redisConfiguration).setDatabase(redisProperties.getDatabase());
			((RedisStandaloneConfiguration) redisConfiguration).setHostName(redisProperties.getHost());
			((RedisStandaloneConfiguration) redisConfiguration).setPort(redisProperties.getPort());
			((RedisStandaloneConfiguration) redisConfiguration)
					.setPassword(RedisPassword.of(redisProperties.getPassword()));

			clientConfig = LettucePoolingClientConfiguration.builder().commandTimeout(redisProperties.getTimeout())
					.poolConfig(genericObjectPoolConfig).build();

		}

		if (redisProperties.isSsl()) {
			clientConfig.isUseSsl();
		}

		LettuceConnectionFactory factory = new LettuceConnectionFactory(redisConfiguration, clientConfig);
		return factory;
	}

	/**
	 * GenericObjectPoolConfig 连接池配置
	 */
	@Bean
	public GenericObjectPoolConfig genericObjectPoolConfig() {
		GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
		poolConfig.setMaxIdle(redisProperties.getLettuce().getPool().getMaxIdle());
		poolConfig.setMinIdle(redisProperties.getLettuce().getPool().getMinIdle());
		poolConfig.setMaxTotal(redisProperties.getLettuce().getPool().getMaxActive());
		poolConfig.setMaxWaitMillis(redisProperties.getLettuce().getPool().getMaxWait().getSeconds());
		Duration timeOut = redisProperties.getTimeout();
		Duration shutdownTimeout = redisProperties.getLettuce().getShutdownTimeout();
		return poolConfig;
	}

 
	@Bean 
	public CacheManager cacheManager(LettuceConnectionFactory lettuceConnectionFactory ) {
	 
		RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
		redisCacheConfiguration = redisCacheConfiguration.entryTtl(Duration.ofMinutes(30L)) // 设置缓存的默认超时时间：30分钟
				.disableCachingNullValues() // 如果是空值，不缓存
				.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string())) // 设置key序列化器
				.serializeValuesWith(
						RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.java())); // 设置value序列化器
		return RedisCacheManager.builder(RedisCacheWriter.nonLockingRedisCacheWriter(lettuceConnectionFactory))
				.cacheDefaults(redisCacheConfiguration).build();
	}

	/**
	 * 适配redis cluster单节点
	 */
	@Primary
	@Bean("redisTemplate")
	// 没有此属性就不会装配bean 如果是单个redis 将此注解注释掉
	@ConditionalOnProperty(name = "spring.redis.cluster.nodes", matchIfMissing = false)
	public RedisTemplate<String, Object> getRedisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
		redisTemplate.setConnectionFactory(lettuceConnectionFactory);
		RedisSerializer stringSerializer = new StringRedisSerializer();
		// RedisSerializer redisObjectSerializer = new RedisObjectSerializer();
		RedisSerializer redisObjectSerializer = new RedisObjectSerializer();
		redisTemplate.setKeySerializer(stringSerializer); // key的序列化类型
		redisTemplate.setHashKeySerializer(stringSerializer);
		redisTemplate.setValueSerializer(redisObjectSerializer); // value的序列化类型
		redisTemplate.setHashValueSerializer(redisObjectSerializer); // value的序列化类型
		redisTemplate.afterPropertiesSet();

		redisTemplate.opsForValue().set("hello", "wolrd");
		return redisTemplate;
	}

	/**
	 * 适配redis单节点
	 */
	@Primary
	@Bean("redisTemplate")
	@ConditionalOnProperty(name = "spring.redis.host", matchIfMissing = true)
	public RedisTemplate<String, Object> getSingleRedisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
		RedisSerializer redisObjectSerializer = new RedisObjectSerializer();
		redisTemplate.setConnectionFactory(lettuceConnectionFactory);
		redisTemplate.setKeySerializer(new StringRedisSerializer()); // key的序列化类型
		redisTemplate.setValueSerializer(redisObjectSerializer); // value的序列化类型
		redisTemplate.setHashValueSerializer(redisObjectSerializer);
		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}

	@Bean
	public HashOperations<String, String, String> hashOperations(StringRedisTemplate stringRedisTemplate) {
		return stringRedisTemplate.opsForHash();
	}

	/**
	 * redis工具类
	 */
	@Bean("redisUtil")
	public RedisUtil redisUtil(LettuceConnectionFactory lettuceConnectionFactory,
			StringRedisTemplate stringRedisTemplate, HashOperations<String, String, String> hashOperations) {
		RedisUtil redisUtil = new RedisUtil(lettuceConnectionFactory, stringRedisTemplate, hashOperations);
		return redisUtil;
	}

	@Bean(destroyMethod = "shutdown")
	@ConditionalOnProperty(name = "spring.redis.redisson.enable", matchIfMissing = false, havingValue = "true")
	@ConditionalOnMissingBean(RedissonClient.class)
	public RedissonClient redissonClient() throws IOException {
		Config config = null;
		Method clusterMethod = ReflectionUtils.findMethod(RedisProperties.class, "getCluster");
		Method timeoutMethod = ReflectionUtils.findMethod(RedisProperties.class, "getTimeout");
		Object timeoutValue = ReflectionUtils.invokeMethod(timeoutMethod, redisProperties);
		int timeout;
		if (null == timeoutValue) {
			timeout = 60000;
		} else if (!(timeoutValue instanceof Integer)) {
			Method millisMethod = ReflectionUtils.findMethod(timeoutValue.getClass(), "toMillis");
			timeout = ((Long) ReflectionUtils.invokeMethod(millisMethod, timeoutValue)).intValue();
		} else {
			timeout = (Integer) timeoutValue;
		}
		// spring.redis.redisson.config=classpath:redisson.yaml
		if (redissonProperties.getConfig() != null) {

			try {
				InputStream is = getConfigStream();
				config = Config.fromJSON(is);
			} catch (IOException e) {
				// trying next format
				try {
					InputStream is = getConfigStream();
					config = Config.fromYAML(is);
				} catch (IOException ioe) {
					throw new IllegalArgumentException("Can't parse config", ioe);
				}
			}
		} else if (redisProperties.getSentinel() != null) {
			// 哨兵配置
			Method nodesMethod = ReflectionUtils.findMethod(Sentinel.class, "getNodes");
			Object nodesValue = ReflectionUtils.invokeMethod(nodesMethod, redisProperties.getSentinel());

			String[] nodes;
			if (nodesValue instanceof String) {
				nodes = convert(Arrays.asList(((String) nodesValue).split(",")));
			} else {
				nodes = convert((List<String>) nodesValue);
			}

			config = new Config();
			config.useSentinelServers().setMasterName(redisProperties.getSentinel().getMaster())
					.addSentinelAddress(nodes).setDatabase(redisProperties.getDatabase()).setConnectTimeout(timeout)
					.setPassword(redisProperties.getPassword());
		} else if (clusterMethod != null && ReflectionUtils.invokeMethod(clusterMethod, redisProperties) != null) {
			// 集群配置
			Object clusterObject = ReflectionUtils.invokeMethod(clusterMethod, redisProperties);
			Method nodesMethod = ReflectionUtils.findMethod(clusterObject.getClass(), "getNodes");
			List<String> nodesObject = (List) ReflectionUtils.invokeMethod(nodesMethod, clusterObject);
			String[] nodes = convert(nodesObject);
			config = new Config();
			config.useClusterServers().addNodeAddress(nodes).setConnectTimeout(timeout)
					.setPassword(redisProperties.getPassword());
		} else {
			// 单机redssion默认配置
			config = new Config();
			String prefix = "redis://";
			Method method = ReflectionUtils.findMethod(RedisProperties.class, "isSsl");
			if (method != null && (Boolean) ReflectionUtils.invokeMethod(method, redisProperties)) {
				prefix = "rediss://";
			}

			config.useSingleServer().setAddress(prefix + redisProperties.getHost() + ":" + redisProperties.getPort())
					.setConnectTimeout(timeout).setDatabase(redisProperties.getDatabase())
					.setPassword(redisProperties.getPassword());

		}

		return Redisson.create(config);
	}

	private String[] convert(List<String> nodesObject) {
		List<String> nodes = new ArrayList<String>(nodesObject.size());
		for (String node : nodesObject) {
			if (!node.startsWith("redis://") && !node.startsWith("rediss://")) {
				nodes.add("redis://" + node);
			} else {
				nodes.add(node);
			}
		}
		return nodes.toArray(new String[nodes.size()]);
	}

	private InputStream getConfigStream() throws IOException {
		Resource resource = ctx.getResource(redissonProperties.getConfig());
		InputStream is = resource.getInputStream();
		return is;
	}

}
