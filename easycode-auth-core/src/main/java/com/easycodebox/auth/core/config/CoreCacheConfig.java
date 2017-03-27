package com.easycodebox.auth.core.config;

import com.easycodebox.common.cache.spring.MethodArgsKeyGenerator;
import com.easycodebox.common.cache.spring.MultiKeyGenerator;
import com.easycodebox.common.cache.spring.redis.CustomRedisCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

/**
 * 缓存配置
 * @author WangXiaoJin
 */
@Configuration
@EnableCaching
@SuppressWarnings("Duplicates")
public class CoreCacheConfig extends CachingConfigurerSupport {
	
	@Autowired
	private RedisConnectionFactory redisConnectionFactory;
	
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
		template.setConnectionFactory(redisConnectionFactory);
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
