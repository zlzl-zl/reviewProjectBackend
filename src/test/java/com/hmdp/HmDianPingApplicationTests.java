package com.hmdp;

import com.hmdp.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@SpringBootTest
class HmDianPingApplicationTests {


    @Resource
    private RedisTemplate redisTemplate;

    @Test
    public  void testJSon(){
        User user = new User();
        user.setNickName("John").setPassword("password").setPhone("phone").setCreateTime(LocalDateTime.now());
        redisTemplate.opsForValue().set("createTime", user);
    }


}
