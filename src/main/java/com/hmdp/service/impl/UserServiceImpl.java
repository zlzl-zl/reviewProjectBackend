package com.hmdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.LoginFormDTO;
import com.hmdp.dto.Result;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.User;
import com.hmdp.mapper.UserMapper;
import com.hmdp.service.IUserService;
import com.hmdp.utils.RedisConstants;
import com.hmdp.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result sendCode(String phone, HttpSession session) {
        //验证手机号码
        Assert.notNull(phone, "请输入手机号码");
        Assert.isTrue(!RegexUtils.isPhoneInvalid(phone), "请输入正确的手机号码");

        //生成验证码
        String code = RandomUtil.randomNumbers(6);
        //todo 将验证码保存进session
//        session.setAttribute(RedisConstants.LOGIN_CODE_KEY + phone, code);
        //将验证码放进redis
        stringRedisTemplate.opsForValue().set(RedisConstants.LOGIN_CODE_KEY + phone,code,RedisConstants.LOGIN_CODE_TTL, TimeUnit.MINUTES);

        // 发送验证码
        log.info("code:{}", code);
        return Result.ok();
    }

    @Override
    public Result login(LoginFormDTO loginForm, HttpSession session) {
        String phone = loginForm.getPhone();
        String code = loginForm.getCode();
        //验证手机号码
        Assert.notNull(phone, "请输入手机号码");
        Assert.isTrue(!RegexUtils.isPhoneInvalid(phone), "请输入正确的手机号码");

        //验证验证码
        Assert.notNull(code, "请输入验证码");
        Assert.isTrue(!RegexUtils.isCodeInvalid(code), "请输入正确的验证码");
        //todo 通过session获得验证码
//        String attribute = (String) session.getAttribute(RedisConstants.LOGIN_CODE_KEY + phone);
        String attribute = stringRedisTemplate.opsForValue().get(RedisConstants.LOGIN_CODE_KEY + phone);
        Assert.isTrue(attribute != null && ObjectUtil.equal(attribute, code), "验证码错误");

        //获取用户并保存进session
        LambdaQueryWrapper<User> lq = new LambdaQueryWrapper<>();
        lq.eq(User::getPhone, phone);
        User user = this.getOne(lq);
        //用户不存在，新建
        if (user == null) {
            user = new User();
            user.setPhone(phone);
            user.setNickName("zlkaif"+RandomUtil.randomString(6));
            user.setPassword("123");
            this.save(user);
            user = this.getOne(lq);
        }
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
//        session.setAttribute(RedisConstants.LOGIN_USER_KEY, userDTO);
        String token = UUID.randomUUID().toString();
        stringRedisTemplate.opsForHash().putAll(
                RedisConstants.LOGIN_USER_KEY+token, BeanUtil.beanToMap(userDTO,
                        new HashMap<String,Object>(),CopyOptions.create(UserDTO.class,true).setFieldValueEditor((fieldName,fieldValue)->{
                            return fieldValue.toString();
                        })));
        stringRedisTemplate.expire(RedisConstants.LOGIN_USER_KEY+token,RedisConstants.LOGIN_USER_TTL,TimeUnit.MINUTES);//10个小时免登录
        return Result.ok(token);
    }
}
