package com.hmdp.config;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @Author: zl
 * @Date: 2024-02-01 21:40
 */
public class FastJson2JsonRedisSerializer<T> implements RedisSerializer<T> {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

//    static {
//        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
//    }

    private final Class<T> clazz;

    public FastJson2JsonRedisSerializer(Class<T> clazz) {
        super();
        this.clazz = clazz;
    }
    /**
     * 序列化
     */
    @Override
    public byte[] serialize(Object o) throws SerializationException {
        if (null == o) {
            return new byte[0];
        }
        //SerializerFeature.WriteClassName  fastJSon1
        //JSONWriter.Feature.WriteClassName fastJSon2
        return JSON.toJSONString(o, JSONWriter.Feature.WriteClassName).getBytes(DEFAULT_CHARSET);
    }

    /**
     * 反序列化
     */
    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (null == bytes || bytes.length <= 0) {
            return null;
        }
        String str = new String(bytes, DEFAULT_CHARSET);
        return (T) JSON.parseObject(str, clazz);
    }
}
