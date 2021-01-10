package com.bizmda.bizsip.integrator.config.magicapi;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@ConfigurationProperties(prefix="magic-api")
public class MagicAPIProperties {

	/**
	 * web页面入口
	 */
	private String web;

	/**
	 * 接口路径前缀
	 */
	private String prefix;

	/**
	 * 打印banner
	 */
	private boolean banner = true;

	/**
	 * 是否抛出异常
	 */
	private boolean throwException = false;

	/**
	 * 接口保存的数据源
	 */
	private String datasource;

	/**
	 * 自动导入的模块,多个用","分隔
	 * @since 0.3.2
	 */
	private String autoImportModule = "db";

	/**
	 * 可自动导入的包（目前只支持以.*结尾的通配符），多个用","分隔
	 * @since 0.4.0
	 */
	private String autoImportPackage;

	/**
	 * 自动刷新间隔，单位为秒，默认不开启
	 * @since 0.3.4
	 */
	private int refreshInterval = 0;

	/**
	 * 是否允许覆盖应用接口，默认为false
	 * @since 0.4.0
	 */
	private boolean allowOverride = false;

	/**
	 * 驼峰命名转换
	 */
	private boolean mapUnderscoreToCamelCase = true;

	/**
	 * 线程核心数，需要>0，<=0时采用默认配置，即CPU核心数 * 2
	 * @since 0.4.5
	 */
	private int threadPoolExecutorSize = 0;

	@NestedConfigurationProperty
	private PageConfig pageConfig = new PageConfig();

	@NestedConfigurationProperty
	private CacheConfig cacheConfig = new CacheConfig();

	public String getWeb() {
		if (StringUtils.isBlank(web)) {
			return null;
		}
		if (web.endsWith("/**")) {
			return web.substring(0, web.length() - 3);
		}
		if (web.endsWith("/*")) {
			return web.substring(0, web.length() - 2);
		}
		if (web.endsWith("/")) {
			return web.substring(0, web.length() - 1);
		}
		return web;
	}

	public void setWeb(String web) {
		this.web = web;
	}

	public boolean isMapUnderscoreToCamelCase() {
		return mapUnderscoreToCamelCase;
	}

	public void setMapUnderscoreToCamelCase(boolean mapUnderscoreToCamelCase) {
		this.mapUnderscoreToCamelCase = mapUnderscoreToCamelCase;
	}

	public boolean isBanner() {
		return banner;
	}

	public void setBanner(boolean banner) {
		this.banner = banner;
	}

	public PageConfig getPageConfig() {
		return pageConfig;
	}

	public void setPageConfig(PageConfig pageConfig) {
		this.pageConfig = pageConfig;
	}

	public boolean isThrowException() {
		return throwException;
	}

	public void setThrowException(boolean throwException) {
		this.throwException = throwException;
	}

	public CacheConfig getCacheConfig() {
		return cacheConfig;
	}

	public void setCacheConfig(CacheConfig cacheConfig) {
		this.cacheConfig = cacheConfig;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getDatasource() {
		return datasource;
	}

	public void setDatasource(String datasource) {
		this.datasource = datasource;
	}

	public String getAutoImportModule() {
		return autoImportModule;
	}

	public List<String> getAutoImportModuleList() {
		return Arrays.asList(autoImportModule.replaceAll("\\s","").split(","));
	}

	public void setAutoImportModule(String autoImport) {
		this.autoImportModule = autoImport;
	}

	public int getRefreshInterval() {
		return refreshInterval;
	}

	public void setRefreshInterval(int refreshInterval) {
		this.refreshInterval = refreshInterval;
	}

	public boolean isAllowOverride() {
		return allowOverride;
	}

	public void setAllowOverride(boolean allowOverride) {
		this.allowOverride = allowOverride;
	}

	public String getAutoImportPackage() {
		return autoImportPackage;
	}

	public void setAutoImportPackage(String autoImportPackage) {
		this.autoImportPackage = autoImportPackage;
	}

	public List<String> getAutoImportPackageList() {
		if(autoImportPackage == null){
			return Collections.emptyList();
		}
		return Arrays.asList(autoImportPackage.replaceAll("\\s","").split(","));
	}

	public int getThreadPoolExecutorSize() {
		return threadPoolExecutorSize;
	}

	public void setThreadPoolExecutorSize(int threadPoolExecutorSize) {
		this.threadPoolExecutorSize = threadPoolExecutorSize;
	}
}
