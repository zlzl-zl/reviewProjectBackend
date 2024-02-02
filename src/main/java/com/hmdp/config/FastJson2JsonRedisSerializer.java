package com.hmdp.config;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.filter.Filter;
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


    //autoTypeFilter 自动类型转换过滤
    //当打开AutoTypeSupport，
    // 虽然内置了一个比较广泛的黑名单，但仍然是不够安全的。
    // 下面有一种办法是控制当前调用的AutoType支持范围，避免全局打开，这个更安全。
    static final Filter autoTypeFilter = JSONReader.autoTypeFilter(
            // 按需加上需要支持自动类型的类名前缀，范围越小越安全
            "com.hmdp"
    );

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

    //bug 1 反序列化失败
    //bug 1 解决 参考文章 https://github.com/alibaba/fastjson2/issues/2178
    /**
     * 反序列化
     */
    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (null == bytes || bytes.length <= 0) {
            return null;
        }
        String str = new String(bytes, DEFAULT_CHARSET);
        return (T) JSON.parseObject(str, clazz, autoTypeFilter,
                JSONReader.Feature.FieldBased
        );
    }
}
