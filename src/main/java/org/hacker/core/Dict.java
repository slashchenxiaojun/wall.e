package org.hacker.core;

/**
 * Global constants definition extends jfinal Const
 * 
 * @version 1.0
 * @author Mr.J.(slashchenxiaojun@sina.com)
 * 
 * @date 2015-03-21
 * */
public class Dict {

	//Cache namespace
	public static String CACHE_TGT      		 = "TGT";
	public static String CACHE_ST      		 	 = "ST";
	public static String CACHE_USER_SYSTEM 	     = "System";
	public static String CACHE_WECHAT_ACCESSTOKEN= "WECHAT_ACCESSTOKEN";
	
	
	//Config Properties namespace
	public static String CONFIG_PMR_MODE 		 = "application.parameter.model";
	public static String CONFIG_AUT_MODE 		 = "application.authentication.model";
	public static String CONFIG_AUT_APP          = "application.authentication.app";
	public static String CONFIG_AUT_LOGIN 	     = "application.authentication.login";
	
	public static String CONFIG_APPCODE   	     = "application.appid";
	public static String CONFIG_APPSECRET 	     = "application.appsecret";
	public static String CONFIG_APPKEY    	     = "application.secretkey";
	
	public static String CONFIG_DB_TYPE          = "db.type"; //Mysql
	public static String CONFIG_JDBC_URL         = "jdbc.url";
	public static String CONFIG_JDBC_USERNAME    = "jdbc.user";
	public static String CONFIG_JDBC_PASSWORD    = "jdbc.password";
	
	public static String CONFIG_EMAIL_HOST       = "email.host";
	public static String CONFIG_EMAIL_FROM 		 = "email.from";
	public static String CONFIG_EMAIL_USERNAME 	 = "email.username";
	public static String CONFIG_EMAIL_PASSWORD 	 = "email.password";
	
	public static String CONFIG_DRUID_INITSIZE   = "druid.initialSize";
	public static String CONFIG_DRUID_MINIDLE    = "druid.minIdle";
	public static String CONFIG_DRUID_MAXACTIVE  = "druid.maxActive";
	
	public static String CONFIG_REDIS_IP  						= "cache.redis.ip";
	public static String CONFIG_REDIS_PORT  					= "cache.redis.port";
	public static String CONFIG_REDIS_MAXTOTAL  				= "cache.redis.pool.maxTotal";
	public static String CONFIG_REDIS_MAXIDLE  					= "cache.redis.pool.maxIdle";
	public static String CONFIG_REDIS_MAXWAIT_MILLIS  			= "cache.redis.pool.maxWaitMillis";
	public static String CONFIG_REDIS_MINEVIC_MILLIS  			= "cache.redis.pool.minEvictableIdleTimeMillis";
	public static String CONFIG_REDIS_SOFTMINEVIC_MILLIS  		= "cache.redis.pool.softMinEvictableIdleTimeMillis";
	public static String CONFIG_REDIS_TIMEBETWEENEVICRUN_MILLIS = "cache.redis.pool.timeBetweenEvictionRunsMillis";
	
	public static String CONFIG_JFINAL_MODE      = "jfinal.devMode";
	
	//主数据源配置名称(ConfigName)
	public static String DB_DATASOURCE_MAIN = "main";
}