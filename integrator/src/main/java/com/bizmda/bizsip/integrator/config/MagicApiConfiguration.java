package com.bizmda.bizsip.integrator.config;

import com.bizmda.bizsip.integrator.config.magicapi.CacheConfig;
import com.bizmda.bizsip.integrator.config.magicapi.MagicAPIProperties;
import com.bizmda.bizsip.integrator.config.magicapi.PageConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.ssssssss.magicapi.cache.DefaultSqlCache;
import org.ssssssss.magicapi.cache.SqlCache;
import org.ssssssss.magicapi.config.MagicDynamicDataSource;
import org.ssssssss.magicapi.functions.RedisFunctions;
import org.ssssssss.magicapi.functions.SQLExecutor;
import org.ssssssss.magicapi.provider.PageProvider;
import org.ssssssss.magicapi.provider.ResultProvider;
import org.ssssssss.magicapi.provider.impl.DefaultPageProvider;
import org.ssssssss.magicapi.provider.impl.DefaultResultProvider;
import javax.sql.DataSource;
import java.util.Map;

@Slf4j
@Configuration
//@AutoConfigureBefore(IntegratorConfiguration.class)
//@EnableAutoConfiguration
public class MagicApiConfiguration {
    @Autowired
    private MagicAPIProperties magicAPIProperties;

    /**
     * 注入数据库查询模块
     */
    @Bean
    public SQLExecutor magicSQLExecutor(MagicDynamicDataSource dynamicDataSource, ResultProvider resultProvider, PageProvider pageProvider, SqlCache sqlCache) {
        RowMapper<Map<String, Object>> rowMapper;
        // 下划线转驼峰命名
        if (magicAPIProperties.isMapUnderscoreToCamelCase()) {
            log.info("开启下划线转驼峰命名");
            rowMapper = new ColumnMapRowMapper() {
                @Override
                protected String getColumnKey(String columnName) {
                    if (columnName == null || !columnName.contains("_")) {
                        return columnName;
                    }
                    columnName = columnName.toLowerCase();
                    boolean upperCase = false;
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < columnName.length(); i++) {
                        char ch = columnName.charAt(i);
                        if (ch == '_') {
                            upperCase = true;
                        } else if (upperCase) {
                            sb.append(Character.toUpperCase(ch));
                            upperCase = false;
                        } else {
                            sb.append(ch);
                        }
                    }
                    return sb.toString();
                }
            };
        } else {
            rowMapper = new ColumnMapRowMapper();
        }
        SQLExecutor sqlExecutor = new SQLExecutor(dynamicDataSource);
        sqlExecutor.setResultProvider(resultProvider);
        sqlExecutor.setPageProvider(pageProvider);
        sqlExecutor.setRowMapper(rowMapper);
        sqlExecutor.setSqlCache(sqlCache);
        return sqlExecutor;
    }

    /**
     * 注入动态数据源
     */
    @Bean
    @ConditionalOnMissingBean(MagicDynamicDataSource.class)
    public MagicDynamicDataSource magicDynamicDataSource(DataSource dataSource) {
        MagicDynamicDataSource dynamicDataSource = new MagicDynamicDataSource();
        dynamicDataSource.put(dataSource);
        return dynamicDataSource;
    }

    /**
     * 注入结果构建方法
     */
    @ConditionalOnMissingBean(ResultProvider.class)
    @Bean
    public ResultProvider resultProvider() {
        return new DefaultResultProvider();
    }

    /**
     * 注入SQL缓存实现
     */
    @ConditionalOnMissingBean(SqlCache.class)
    @Bean
    public SqlCache sqlCache() {
        CacheConfig cacheConfig = magicAPIProperties.getCacheConfig();
        log.info("未找到SQL缓存实现，采用默认缓存实现(LRU+TTL)，缓存配置:(容量={},TTL={})", cacheConfig.getCapacity(), cacheConfig.getTtl());
        return new DefaultSqlCache(cacheConfig.getCapacity(), cacheConfig.getTtl());
    }

    @ConditionalOnMissingBean(PageProvider.class)
    @Bean
    public PageProvider pageProvider() {
        PageConfig pageConfig = magicAPIProperties.getPageConfig();
        log.info("未找到分页实现,采用默认分页实现,分页配置:(页码={},页大小={},默认首页={},默认页大小={})", pageConfig.getPage(), pageConfig.getSize(), pageConfig.getDefaultPage(), pageConfig.getDefaultSize());
        return new DefaultPageProvider(pageConfig.getPage(), pageConfig.getSize(), pageConfig.getDefaultPage(), pageConfig.getDefaultSize());
    }

//    @ConditionalOnBean(RedisConnectionFactory.class)
    @Bean
    public RedisFunctions redisFunctions(RedisConnectionFactory connectionFactory) {
        return new RedisFunctions(connectionFactory);
    }
}
