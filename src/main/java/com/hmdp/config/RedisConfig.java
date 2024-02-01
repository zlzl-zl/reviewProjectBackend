package com.hmdp.config;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 配置Redis序列化方式  //参考文章https://www.cnblogs.com/kenx/p/15506722.html
 * @Author: zl
 * @Date: 2024-02-01 21:15
 */
@SpringBootConfiguration
public class RedisConfig {
    //GenericFastJsonRedisSerializer 可以自动转换对象类型
    // FastJsonRedisSerializer需要自定义转换需要的类型。
    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        FastJson2JsonRedisSerializer<Object> fastJsonRedisSerializer = new FastJson2JsonRedisSerializer<Object>(Object.class);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);

        // value序列化方式采用fastJson
        template.setValueSerializer(fastJsonRedisSerializer);
        // hash的value序列化方式采用fastJson
        template.setHashValueSerializer(fastJsonRedisSerializer);

        template.afterPropertiesSet();
        return template;
    }


}
