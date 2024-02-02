package com.hmdp.utils;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.annotation.Resource;

/**
 * RedisUtil 缓存帮助类]
 * 参考文章 https://www.cnblogs.com/kenx/p/15506722.html
 * @Author: zl
 * @Date: 2024-02-02 21:13
 */
public class RedisUtil{
    //static 使用 @Resource 注入为null，没成功
    //@Resource注解注入RedisTemplate时遇到了null值的问题。由于静态变量的注入方式不正确导致的。
    // 通常情况下，静态变量不建议使用注解进行注入，因为它们不属于实例对象，而是属于类本身。
//    @Resource
    private  static   RedisTemplate<String,Object>  redisTemplate = SpringUtil.getBean("redisTemplate");

    /**
     * JSON字符串缓存放入,不带过期时间
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public static <T> Boolean set(String key, T value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static <T> Boolean set(String key, T value,Long time) {
        try {
            redisTemplate.opsForValue().set(key,value,time);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    /**
     * 普通缓存获取
     *
     *
     * ObjectMapper是Jackson库中的一个类，它提供了一种将Java对象序列化为JSON格式或将JSON格式反序列化为Java对象的方法。
     * Jackson是一个流行的Java库，用于处理JSON格式的数据。ObjectMapper类是Jackson库的核心部分，它提供了丰富的API来处理JSON数据，包括序列化、反序列化、数据绑定等功能。
     *
     * 通过ObjectMapper，
     * 您可以轻松地将Java对象转换为JSON格式的字符串，或者将JSON格式的字符串转换为Java对象。
     * 这在处理RESTful API、持久化数据到NoSQL数据库（如MongoDB）或与前端进行数据交换时非常有用
     * @param key 键
     * @return 值
     */
    // @JsonFormat 和  @DateTimeFormat理解不足
    //可以使用时间序列器，全都转为时间戳存储，在spring bean初始化前，进行序列化为时间戳，反序列化为时间类

    // bug#2 LocalDateTime 格式转换错误
    public static<T> T get(String key,Class<T> clazz) {
        Object value = key == null ? null : redisTemplate.opsForValue().get(key);
        String jsonString = JSON.toJSONString(value);
        ObjectMapper mapper = new ObjectMapper();
        try {
            return value == null ? null : mapper.readValue(jsonString, clazz);
        } catch (JsonProcessingException e) {
            e.printStackTrace();  // 处理异常，例如记录日志或抛出自定义异常
            return null;
        }
    }
}
