package com.easycodebox.auth.core.config;

import com.easycodebox.common.cache.spring.MethodArgsKeyGenerator;
import com.easycodebox.common.cache.spring.MultiKeyGenerator;
import com.easycodebox.common.cache.spring.redis.CustomRedisCacheManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.*;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 缓存配置
 * @author WangXiaoJin
 */
@Configuration
@EnableCaching
@SuppressWarnings("Duplicates")
public class CoreCacheConfig extends CachingConfigurerSupport {
	
	@Value("${redis.core.host}")
	private String redisHost;
	
	@Value("${redis.core.port}")
	private Integer redisPort;
	
	@Value("${redis.core.password}")
	private String redisPassword;
	
	@Value("${redis.core.pool.maxTotal}")
	private Integer redisMaxTotal;
	
	@Value("${redis.core.pool.maxIdle}")
	private Integer redisMaxIdle;
	
	@Value("${redis.core.pool.maxWaitMillis}")
	private Long redisMaxWaitMillis;
	
	@Value("${redis.core.pool.testOnBorrow}")
	private Boolean redisTestOnBorrow;
	
	/**
	 * Redis 连接池
	 */
	@Bean
	public JedisPoolConfig jedisPoolConfig() {
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
	@Primary
	public JedisConnectionFactory jedisConnectionFactory() {
		JedisConnectionFactory factory = new JedisConnectionFactory(jedisPoolConfig());
		factory.setHostName(redisHost);
		factory.setPort(redisPort);
		return factory;
	}
	
	/* ------------------ Sentinel Mode: --------------------------------------- */
	/*@Bean
	public RedisSentinelConfiguration redisSentinelConfiguration() {
		return new RedisSentinelConfiguration(
				"easycode",
				Collections.toSet("192.168.206.130:26379", "192.168.206.130:26380", "192.168.206.130:26381")
		);
	}
	@Bean
	public JedisConnectionFactory jedisConnectionFactory() {
		return new JedisConnectionFactory(redisSentinelConfiguration(), jedisPoolConfig());
	}*/
	
	/* ------------------ Cluster Mode: --------------------------------------- */
	/*  注：目前Jedis版本不支持读写分离，读写都是走Master，有兴趣的朋友可以换成redisson，redisson实现了读写分离。*/
	/*@Bean
	public RedisClusterConfiguration redisClusterConfiguration() {
		List<String> clusterNodes = Collections.toList("192.168.206.130:6379", "192.168.206.130:6380", "192.168.206.130:6381");
		RedisClusterConfiguration configuration = new RedisClusterConfiguration(clusterNodes);
		configuration.setMaxRedirects(5);
		return configuration;
	}
	@Bean
	public JedisConnectionFactory jedisConnectionFactory() {
		return new JedisConnectionFactory(redisClusterConfiguration(), jedisPoolConfig());
	}*/
	
	/**
	 * JSON序列化
	 */
	@Bean
	public GenericJackson2JsonRedisSerializer jacksonRedisSerializer() {
		return new GenericJackson2JsonRedisSerializer();
	}
	/**
	 * redis template definition
	 */
	@Bean
	@SuppressWarnings("unchecked")
	public RedisTemplate redisTemplate() {
		RedisTemplate template = new RedisTemplate();
		template.setConnectionFactory(jedisConnectionFactory());
		template.setValueSerializer(jacksonRedisSerializer());
		template.setHashValueSerializer(jacksonRedisSerializer());
		return template;
	}
	
	@Bean
	@Override
	public CustomRedisCacheManager cacheManager() {
		CustomRedisCacheManager cacheManager = new CustomRedisCacheManager(redisTemplate());
		cacheManager.setUsePrefix(true);
		return cacheManager;
	}
	/**
	 * 带方法名的KeyGenerator，适用于混合存储格式
	 */
	@Bean
	public MethodArgsKeyGenerator methodArgsKeyGenerator() {
		return new MethodArgsKeyGenerator();
	}
	/**
	 * 适用于批量删除缓存的情况
	 */
	@Bean
	public MultiKeyGenerator multiKeyGenerator() {
		return new MultiKeyGenerator();
	}
	
}
