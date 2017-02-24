package com.easycodebox.login.config;

import com.easycodebox.common.cache.spring.redis.CustomRedisCacheManager;
import com.easycodebox.login.shiro.cache.spring.RedisTemplateCacheStats;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author WangXiaoJin
 */
@Configuration
public class ShiroCacheConfig {
	
	@Value("${redis.shiro.host}")
	private String redisHost;
	
	@Value("${redis.shiro.port}")
	private Integer redisPort;
	
	@Value("${redis.shiro.password}")
	private String redisPassword;
	
	@Value("${redis.shiro.pool.maxTotal}")
	private Integer redisMaxTotal;
	
	@Value("${redis.shiro.pool.maxIdle}")
	private Integer redisMaxIdle;
	
	@Value("${redis.shiro.pool.maxWaitMillis}")
	private Long redisMaxWaitMillis;
	
	@Value("${redis.shiro.pool.testOnBorrow}")
	private Boolean redisTestOnBorrow;
	
	/**
	 * Redis 连接池
	 */
	@Bean
	public JedisPoolConfig shiroRedisPoolConfig() {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(redisMaxTotal);
		config.setMaxIdle(redisMaxIdle);
		config.setMaxWaitMillis(redisMaxWaitMillis);
		config.setTestOnBorrow(redisTestOnBorrow);
		return config;
	}
	
	/**
	 * Jedis ConnectionFactory
	 */
	@Bean
	public JedisConnectionFactory shiroRedisConnectionFactory() {
		JedisConnectionFactory factory = new JedisConnectionFactory(shiroRedisPoolConfig());
		factory.setHostName(redisHost);
		factory.setPort(redisPort);
		return factory;
	}
	
	/*
		注：当部署到正式环境时，Shiro Session使用的Redis需要开启RDB、AOF达到持久化的目的，并且需要定时备份RDB文件。
            纯粹简单作为缓存使用的Redis，需要关闭RDB、AOF功能，因为持久化时是消耗性能的。
        结论：在生产环境部署两套Redis集群（集群的目的容灾、负载均衡。目前Jedis版本不支持读写分离，且读写都是走Master，
              有兴趣的朋友可以换成redisson，redisson实现了读写分离。），一套做持久化Session使用，另一套纯粹作为缓存。
              另外一个更重要的原因是：Shiro Session是提供给所有需要登录的服务使用的，必须使用同一个Redis集群，
              不然登录状态会乱套的。属于每个服务本身的业务数据基本上都是独立缓存在各自的Redis集群中的。
	 */
	
	/**
	 * 提供给Shiro缓存Session使用
	 */
	@Bean
	public RedisTemplate shiroRedisTemplate() {
		RedisTemplate template = new RedisTemplate();
		template.setConnectionFactory(shiroRedisConnectionFactory());
		return template;
	}
	
	@Bean
	public CustomRedisCacheManager shiroCacheManager() {
		CustomRedisCacheManager cacheManager = new CustomRedisCacheManager(shiroRedisTemplate());
		cacheManager.setUsePrefix(true);
		cacheManager.setDefaultExpiration(1800);
		return cacheManager;
	}
	
	@Bean
	public RedisTemplateCacheStats cacheStats() {
		RedisTemplateCacheStats cacheStats = new RedisTemplateCacheStats();
		cacheStats.setUsePrefix(true);
		return cacheStats;
	}
	
}
