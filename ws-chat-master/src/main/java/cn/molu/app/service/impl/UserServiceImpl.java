package cn.molu.app.service.impl;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import cn.molu.app.mapper.UserMapper;
import cn.molu.app.pojo.User;
import cn.molu.app.service.UserService;
import cn.molu.app.utils.ObjectUtils;
import cn.molu.app.utils.RedisUtils;
import cn.molu.app.vo.R;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * (User)表服务实现类
 *
 * @author 陌路
 * @since 2022-04-21 16:55:04
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisUtils redisUtils;

    @Value("${jwt.secret}")
    private String secret;

    @Override
    public R login(String phone, String password, Map<String, Object> params, HttpServletResponse res) {
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("phone", phone).eq("deleted", "0"));
        ObjectUtils.checkNull(res, user, String.format("未获取到%s的数据信息!", phone));
        // 对用户输入的密码进行校验
        String md5Pwd = DigestUtils.md5Hex(secret + password);
        System.out.println(md5Pwd);
        if (!StringUtils.equals(md5Pwd, user.getPassword())) {
            return R.err("密码输入错误！");
        }
        user.setPassword("");
        String token = getToken(user);
        String userId = ObjectUtils.getStr(user.getId());
        // 有效期2天
        this.redisUtils.setObj(userId, user, Duration.ofDays(2));
        return R.ok().put("token", token);
    }

    /**
     * 解析token，获取user数据信息 对token进行检测，如果token存在，则解析出user数据信息 如果token不存在，则return null
     * 除注册和发送验证码外不需要检测token外，其他功能均需要检测token
     */
    @Override
    public User queryUserByToken(String token) {
        try {
            // 生成redisKey获取当前登陆人信息
            String redisTokenKey = "TOKEN_" + token;
            // 获取缓存数据
            String cacheData = ObjectUtils.getStr(redisUtils.getStr(redisTokenKey));
            if (StringUtils.isNotEmpty(cacheData)) {
                // 刷新时间
                this.redisUtils.setExpire(redisTokenKey, 1L, TimeUnit.HOURS);
                // 反序列化，将JSON数据转化为user对象返回
                return MAPPER.readValue(cacheData, User.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成Token,并把Token放到Redis中
     */
    @Override
    public String getToken(User user) {
        String token;
        Map<String, Object> claims = new HashMap<>(2);
        claims.put("id", user.getId());
        claims.put("name", user.getUsername());
        claims.put("phone", user.getPhone());
        claims.put("userCode", user.getUserCode());
        // 设置响应数据体,user数据,设置加密方法和加密盐
        token = Jwts.builder().setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();

        // 生成redis的key
        String redisTokenKey = "TOKEN_" + token;
        /* 把user数据放到redis中, 并设置token的有效时间 为3小时 */
        try {
            redisUtils.setStr(redisTokenKey, MAPPER.writeValueAsString(user), Duration.ofHours(3));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        String loginState = "LOGIN_STATE_" + user.getPhone();
        String stateVal = "1&@&" + redisTokenKey;
        // token在线状态有效期2天
        redisUtils.setStr(loginState, stateVal, Duration.ofDays(2));
        return token;
    }

    /**
     * 记住密码,设置cookie标志
     */
    @SuppressWarnings("unused")
    public void rememberPwd(String phone, String password, HttpServletResponse res) {
        Cookie cookie = new Cookie("flag", "true");
        String rKey = "REMEMBER_TRUE_" + phone;
        redisUtils.setStr(rKey, password, Duration.ofDays(7L));
        cookie.setMaxAge(7 * 24 * 60 * 60);
        cookie.setPath("/index/CheckData");
        res.addCookie(cookie);
    }
}
