package com.easycodebox.login.config.redis;

import com.easycodebox.common.cache.spring.redis.CustomRedisCacheManager;
import com.easycodebox.login.config.redis.ShiroRedisProperties.Cluster;
import com.easycodebox.login.config.redis.ShiroRedisProperties.Sentinel;
import com.easycodebox.login.shiro.cache.spring.RedisTemplateCacheStats;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

import java.net.*;
import java.util.ArrayList;
import java.util.List;

@Configuration
@ConditionalOnClass({ JedisConnection.class, RedisOperations.class, Jedis.class })
@EnableConfigurationProperties(ShiroRedisProperties.class)
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class ShiroRedisAutoConfiguration {
	
	@Bean
	public CustomRedisCacheManager shiroCacheManager(RedisTemplate shiroRedisTemplate) {
		CustomRedisCacheManager cacheManager = new CustomRedisCacheManager(shiroRedisTemplate);
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
	
	@Configuration
	@ConditionalOnClass(GenericObjectPool.class)
	protected static class ShiroRedisConfiguration {
		
		private ShiroRedisProperties properties;
		
		public ShiroRedisConfiguration(ShiroRedisProperties properties) {
			this.properties = properties;
		}
		
		/*
			注：当部署到正式环境时，Shiro Session使用的Redis需要开启RDB、AOF达到持久化的目的，并且需要定时备份RDB文件。
				纯粹简单作为缓存使用的Redis，需要关闭RDB、AOF功能，因为持久化时是消耗性能的。
			结论：在生产环境部署两套Redis集群（集群的目的容灾、负载均衡。目前Jedis版本不支持读写分离，且读写都是走Master，
				  有兴趣的朋友可以换成redisson，redisson实现了读写分离。），一套做持久化Session使用，另一套纯粹作为缓存。
				  另外一个更重要的原因是：Shiro Session是提供给所有需要登录的服务使用的，必须使用同一个Redis集群，
				  不然登录状态会乱套的。属于每个服务本身的业务数据基本上都是独立缓存在各自的Redis集群中的。
		 */
		@Bean
		@ConditionalOnMissingBean(name = "shiroRedisTemplate")
		public RedisTemplate<Object, Object> shiroRedisTemplate(RedisConnectionFactory shiroRedisConnectionFactory)
				throws UnknownHostException {
			RedisTemplate<Object, Object> template = new RedisTemplate<>();
			template.setConnectionFactory(shiroRedisConnectionFactory);
			return template;
		}
		
		@Bean
		@ConditionalOnMissingBean(name = "shiroRedisConnectionFactory")
		public JedisConnectionFactory shiroRedisConnectionFactory()
				throws UnknownHostException {
			return applyProperties(createJedisConnectionFactory());
		}
		
		protected final JedisConnectionFactory applyProperties(
				JedisConnectionFactory factory) {
			configureConnection(factory);
			if (this.properties.isSsl()) {
				factory.setUseSsl(true);
			}
			factory.setDatabase(this.properties.getDatabase());
			if (this.properties.getTimeout() > 0) {
				factory.setTimeout(this.properties.getTimeout());
			}
			return factory;
		}
		
		private void configureConnection(JedisConnectionFactory factory) {
			if (StringUtils.hasText(this.properties.getUrl())) {
				configureConnectionFromUrl(factory);
			}
			else {
				factory.setHostName(this.properties.getHost());
				factory.setPort(this.properties.getPort());
				if (this.properties.getPassword() != null) {
					factory.setPassword(this.properties.getPassword());
				}
			}
		}
		
		private void configureConnectionFromUrl(JedisConnectionFactory factory) {
			String url = this.properties.getUrl();
			if (url.startsWith("rediss://")) {
				factory.setUseSsl(true);
			}
			try {
				URI uri = new URI(url);
				factory.setHostName(uri.getHost());
				factory.setPort(uri.getPort());
				if (uri.getUserInfo() != null) {
					String password = uri.getUserInfo();
					int index = password.lastIndexOf(":");
					if (index >= 0) {
						password = password.substring(index + 1);
					}
					factory.setPassword(password);
				}
			}
			catch (URISyntaxException ex) {
				throw new IllegalArgumentException("Malformed 'spring.redis.url' " + url,
						ex);
			}
		}
		
		protected final RedisSentinelConfiguration getSentinelConfig() {
			Sentinel sentinelProperties = this.properties.getSentinel();
			if (sentinelProperties != null) {
				RedisSentinelConfiguration config = new RedisSentinelConfiguration();
				config.master(sentinelProperties.getMaster());
				config.setSentinels(createSentinels(sentinelProperties));
				return config;
			}
			return null;
		}
		
		/**
		 * Create a {@link RedisClusterConfiguration} if necessary.
		 * @return {@literal null} if no cluster settings are set.
		 */
		protected final RedisClusterConfiguration getClusterConfiguration() {
			if (this.properties.getCluster() == null) {
				return null;
			}
			Cluster clusterProperties = this.properties.getCluster();
			RedisClusterConfiguration config = new RedisClusterConfiguration(
					clusterProperties.getNodes());
			
			if (clusterProperties.getMaxRedirects() != null) {
				config.setMaxRedirects(clusterProperties.getMaxRedirects());
			}
			return config;
		}
		
		private List<RedisNode> createSentinels(Sentinel sentinel) {
			List<RedisNode> nodes = new ArrayList<>();
			for (String node : StringUtils
					.commaDelimitedListToStringArray(sentinel.getNodes())) {
				try {
					String[] parts = StringUtils.split(node, ":");
					Assert.state(parts.length == 2, "Must be defined as 'host:port'");
					nodes.add(new RedisNode(parts[0], Integer.valueOf(parts[1])));
				}
				catch (RuntimeException ex) {
					throw new IllegalStateException(
							"Invalid redis sentinel " + "property '" + node + "'", ex);
				}
			}
			return nodes;
		}
		
		private JedisConnectionFactory createJedisConnectionFactory() {
			JedisPoolConfig poolConfig = this.properties.getPool() != null
					? jedisPoolConfig() : new JedisPoolConfig();
			
			if (getSentinelConfig() != null) {
				return new JedisConnectionFactory(getSentinelConfig(), poolConfig);
			}
			if (getClusterConfiguration() != null) {
				return new JedisConnectionFactory(getClusterConfiguration(), poolConfig);
			}
			return new JedisConnectionFactory(poolConfig);
		}
		
		private JedisPoolConfig jedisPoolConfig() {
			JedisPoolConfig config = new JedisPoolConfig();
			ShiroRedisProperties.Pool props = this.properties.getPool();
			config.setMaxTotal(props.getMaxActive());
			config.setMaxIdle(props.getMaxIdle());
			config.setMinIdle(props.getMinIdle());
			config.setMaxWaitMillis(props.getMaxWait());
			return config;
		}
	}
	
}
