package cn.molu.app.service.impl;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.map.MapUtil;
import cn.molu.app.mapper.UserMapper;
import cn.molu.app.pojo.Email;
import cn.molu.app.pojo.Message;
import cn.molu.app.pojo.User;
import cn.molu.app.pojo.UserQuery;
import cn.molu.app.service.UserService;
import cn.molu.app.utils.MailUtils;
import cn.molu.app.utils.ObjectUtils;
import cn.molu.app.utils.RedisUtils;
import cn.molu.app.vo.R;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
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

    @Autowired
    private RedisTemplate redisTemplate;

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


    @Override
    public List<User> queryFriendsList(User user) {
        List<User> userList=  userMapper.queryFriendsList(user.getId());
        return userList;
    }

    @Override
    public void sendVerificationCode(String mailbox) {
        StringBuilder str = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            str.append(random.nextInt(10));
        }
        Email mail = new Email();
        //发件人邮箱地址
        mail.setSender("huzhiwe1@163.com");
        //邮箱账号
        mail.setUserName("huzhiwe1");
        //邮箱客户端授权码
        mail.setPassword("1733296693qaz");

        mail.setSubject("用户注册");

        mail.setMessage("<p>尊敬的用户：</p><p>您好!</p><p>您正在尝试注册wsChat账号！"
                +"您的验证码是："+str+"，有效期10分钟。</p><p>请妥善保管您的账号和密码。</p>");

        mail.setReceiver(mailbox);

        MailUtils.sendMail(mail);

        redisTemplate.opsForValue().set(mailbox,str,10,TimeUnit.MINUTES);
    }

    @Override
    public R  registerUser(String username, String phonenumber, String pwd, String verify, String mailbox) {
       if( redisTemplate.opsForValue().get(mailbox)==null){
           return R.err("验证码失效");
       }
        if(!redisTemplate.opsForValue().get(mailbox).toString().equals(verify)){
            return R.err("验证码错误");
        }
        User userSelct=userMapper.selectByPhone(phonenumber);
        if(userSelct!=null){
            return R.err("用户已存在");
        }

        User user=new User();
        user.setUsername(username);
        user.setPhone(phonenumber);
        user.setPassword(DigestUtils.md5Hex(secret + pwd));
        user.setMailbox(mailbox);
        user.setUserCode(String.valueOf(userMapper.selectCount()+1));
        userMapper.insert(user);
        return R.ok("注册成功");
    }

    @Override
    public R getUserList( UserQuery userQuery) {
        String sort=null;
        if(userQuery.isAsc()){
            sort="asc";
        }else {
            sort="desc";
        }
        String orderBy= userQuery.getOrderByField()+" "+sort;
        PageHelper.startPage(userQuery.getPageNum(),userQuery.getPageSize(),orderBy);
         List<User> userList= userMapper.selctAllUser(userQuery);
        PageInfo<User> pageInfo=new PageInfo<>(userList);
        return R.ok(pageInfo);

    }

    @Override
    public R addUser(Map map) {
        Integer userId=null;
        Integer friendId=null;
         if(MapUtil.isNotEmpty(map)&&map.get("userId")!=null){
            userId=Integer.valueOf(map.get("userId").toString());
         }
        if(MapUtil.isNotEmpty(map)&&map.get("friendId")!=null){
            friendId=Integer.valueOf(map.get("friendId").toString());
        }
        if(userId!=null&&friendId!=null){
          List <Map> list=userMapper.queryAlreadyFriend(userId,friendId);
          if(list.size()==2){
              return R.err("已是好友！！！，请勿重复添加");
          }
            if(list.size()==1){
                userMapper.deleteRelationship(Integer.valueOf(list.get(0).get("id").toString()));
                return R.err("数据有误，请重试");
            }
            if(list.size()==0){
                User user = userMapper.selectById(friendId);
                return R.ok(user);
            }

        }
        return null;
    }

    @Override
    public R addUserMessage(Message message) {
        String addUserMessage="addUserMessage_"+message.getToId();
       redisTemplate.opsForList().leftPush(addUserMessage,message);
        return R.ok("添加成功");
    }

    @Override
    public R queryAddUserMessage(String userId) {
        List<Message> messageList = redisTemplate.opsForList().range("addUserMessage_" + userId, 0, -1);
        return R.ok(messageList);
    }

    @Override
    public R queryAddUserMessageOne(String userId) {

        List<Message> messageList = redisTemplate.opsForList().range("addUserMessage_" + userId, 0, 0);
        System.out.println(messageList);
        return  R.ok(messageList.get(0));
    }

    @Override
    public R deleAddUserMessage(String userId) {
        List<Message> messageList = redisTemplate.opsForList().range("addUserMessage_" + userId, 0, 0);
        List<Map> mapList=new ArrayList<>();

        HashMap<String, Integer> map = new HashMap<>();
        map.put("userId",Integer.valueOf(messageList.get(0).getFromId()));
        map.put("friendId",Integer.valueOf(messageList.get(0).getToId()));
        mapList.add(map);
        HashMap<String, Integer> map2 = new HashMap<>();
        map2.put("userId",Integer.valueOf(messageList.get(0).getToId()));
        map2.put("friendId",Integer.valueOf(messageList.get(0).getFromId()));
        mapList.add(map2);

        int i = userMapper.addFriendRelationShip(mapList);
        if(i!=0){
            redisTemplate.opsForList().leftPop("addUserMessage_" + userId);
        }
           return R.ok;
    }
}
