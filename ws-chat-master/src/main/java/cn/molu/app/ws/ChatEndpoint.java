package cn.molu.app.ws;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.RemoteEndpoint.Basic;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import cn.molu.app.callable.AddMessageRunnable;
import cn.molu.app.config.GetHttpSessionConfigurator;
import cn.molu.app.mapper.UserMapper;
import cn.molu.app.pojo.Result;
import cn.molu.app.pojo.ResultMessage;
import cn.molu.app.pojo.User;
import cn.molu.app.service.MessageService;
import cn.molu.app.service.UserService;
import cn.molu.app.utils.ObjectUtils;
import cn.molu.app.utils.RedisUtils;
import cn.molu.app.utils.SpringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.molu.app.pojo.Message;

/**
 * @author 陌路
 * @Description 注解 @ServerEndpoint 配置对外暴露访问地址，外部访问格式为（ws://localhost:80/chat）
 * @date 2022-04-16 上午11:53:40
 */
@Component
@ServerEndpoint(value = "/webSocket/{userId}", configurator = GetHttpSessionConfigurator.class)
public class ChatEndpoint {
    private final static Logger LOGGER = LogManager.getLogger(ChatEndpoint.class);

    private HttpSession httpSession;

    public static Map<String, Session> onLineUser = new ConcurrentHashMap<>();

    @Resource
    private final RedisUtils redisUtils = SpringUtils.getBean(RedisUtils.class);

    private static MessageService messageService;

    @Autowired
    public void setMessageService(MessageService messageService){
        this.messageService=messageService;
    }

    private static ThreadPoolExecutor threadPoolExecutor;


    private static UserMapper userMapper;

    @Autowired
    public  void setUserMapper(UserMapper userMapper){
        this.userMapper=userMapper;
    }

    @Autowired
    public void setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor){
        this.threadPoolExecutor=threadPoolExecutor;
    }

    /**
     * 连接建立时 会调用该方法
     *
     * @Title onOpen
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId, EndpointConfig endpointConfig) {
        Map<String, Object> userProperties = endpointConfig.getUserProperties();
        HttpSession httpSession = (HttpSession) userProperties.get(HttpSession.class.getName());
        this.httpSession = httpSession;
        if (ObjectUtils.isBlank(httpSession) || ObjectUtils.isEmpty(httpSession.getId())) {
            return;
        }
        if (StringUtils.isBlank(userId)) {
            return;
        }
        Object obj = redisUtils.getObj(userId);
        if (obj == null) {
            return;
        }
        User user = JSON.parseObject(JSON.toJSONString(obj), User.class);
        if (ObjectUtils.isBlank(user)) {
            return;
        }
        String username = ObjectUtils.getStr(user.getUsername());
        Result res = new Result();
        res.setUserId(userId);
        res.setUsername(username);
        res.setDateStr();
        res.setMessage(String.format("用户%s上线了！", username));
        // 缓存数据
        List<Object> listAll = this.redisUtils.getListAll("onLineUsers");
        if (!onLineUser.containsKey(userId)) {
            if (!isContains(listAll, res)) {
                this.redisUtils.lPush("onLineUsers", res);
            }
        }
        this.redisUtils.setObj(httpSession.getId(), res);
        onLineUser.put(userId, session);
        // 获取未读数据条数


        List<User> userList=  userMapper.queryFriendsList(Integer.valueOf(userId));

        Set<String> ids=new HashSet<>();
        userList.forEach(it->ids.add(String.valueOf(it.getId())));
        Map<String, Integer> map = getUnReadCount(userId, ids);
        // 1. 获取消息，该消息为系统消息，推送给所有用户
        String message = getSysMessage(getusers(), map);
        // 2. 调用方法进行系统消息的推送
        broadcastAllUsers(message);
        LOGGER.info("{}	建立了连接。。。", username);
        LOGGER.info("系统消息推送。。。{}	", message);
    }

    /**
     * @Title isContains
     */
    private boolean isContains(List<Object> listAll, Result res) {
        if (ObjectUtils.isBlank(listAll) || ObjectUtils.isBlank(res)) {
            return false;
        }
        for (Object object : listAll) {
            Result obj = JSON.parseObject(JSON.toJSONString(object), Result.class);
            if (null != obj && res.getUserId().equals(obj.getUserId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 接收到客户端发送的数据时 会调用此方法
     *
     * @Title onMessage
     */
    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        LOGGER.info("来自客户端的消息：{}", message);
        if (StringUtils.isBlank(message)) {
            return;
        }
        if ("PING".equalsIgnoreCase(message)) {
            heartCheck(session);
            return;
        }
        Message msgObj = JSON.parseObject(message, Message.class);
        String toId = msgObj.getToId();
        String text = msgObj.getMessage();
        msgObj.setDateStr(new Date());
        Integer messageType=msgObj.getMessageType();
        if(messageType!=null&&messageType==0){
            // 获取推送指定用户数据
            String resultMessage = getMessage(msgObj, text);
            LOGGER.info("接收到好友发来的数据：{}", resultMessage);

            Session toSession = onLineUser.get(toId);
            if(toSession!=null){
                Basic basicRemote = toSession.getBasicRemote();
                basicRemote.sendText(resultMessage);
            }else {

            }
        }else {
            if (ObjectUtils.isBlank(toId)) {
                Map<String, String> map = new HashMap<>(2);
                map.put("sendErr", "消息发送失败，请稍后重试。。。");
                ObjectMapper objectMapper = new ObjectMapper();
                Session toSession = onLineUser.get(msgObj.getFromId());
                LOGGER.info("session对比{}，{}。。。", toSession.getId(), session.getId());
                if (toSession != null && toSession.isOpen()) {
                    Basic basicRemote = toSession.getBasicRemote();
                    basicRemote.sendText(objectMapper.writeValueAsString(map));
                    LOGGER.info(objectMapper.writeValueAsString(map));
                    return;
                }
            }
            // 获取推送指定用户数据
            String resultMessage = getMessage(msgObj, text);
            LOGGER.info("接收到好友发来的数据：{}", resultMessage);


            //储存发送的聊天记录
            AddMessageRunnable addMessageRunnable=new AddMessageRunnable(JSON.parseObject(resultMessage, ResultMessage.class),messageService);
            threadPoolExecutor.execute(addMessageRunnable);

            // 点对点发送数据（给指定用户发送消息）
            Session toSession = onLineUser.get(toId);
            if (toSession != null) {
                if (toSession.isOpen()) {
                    Basic basicRemote = toSession.getBasicRemote();
                    basicRemote.sendText(resultMessage);
                }
            }else {
                //发送离线消息，记录未读消息条数
                String fromId = msgObj.getFromId();
                String userId = msgObj.getToId();
                if (StringUtils.isNotBlank(fromId) && StringUtils.isNotBlank(userId)) {
                    try {
                        String redisCountKey = "UN_READ_MSG_COUNT_" + fromId + "_" + userId;
                        String count = this.redisUtils.getStr(redisCountKey);
                        if(StringUtils.isBlank(count)){
                            count="1";
                        }else {
                            count = ObjectUtils.getStr(Integer.parseInt(count) + 1);
                        }
                        this.redisUtils.setStr(redisCountKey, count, Duration.ofDays(7));
                        LOGGER.info("获取未读消息条数:{}...{}", count, redisCountKey);
                    } catch (Exception e) {
                        LOGGER.info("获取未读消息时出现异常...{}", e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 连接关闭时 调用此方法
     */
    @OnClose
    public void onClose(Session session) {
        if (httpSession == null || ObjectUtils.isBlank(httpSession.getId())) {
            return;
        }
        Object obj = redisUtils.getObj(httpSession.getId());
        if (obj == null) {
            return;
        }
        Result res = (Result) obj;
        String username = res.getUsername();
        String userId = res.getUserId();
        // 移除已关闭连接的用户
        onLineUser.remove(userId);
        this.redisUtils.removeList("onLineUsers", res);
        if (ObjectUtils.isBlank(onLineUser)) {
            this.redisUtils.remove("onLineUsers");
        }
        this.redisUtils.remove(httpSession.getId());
        LOGGER.info("{}关闭了连接:", username);
        Message msgObj = new Message();
        msgObj.setMessage(String.format("%s离线了！", username));
        msgObj.setFromId(userId);
        msgObj.setFromName(username);
        msgObj.setDateStr(new Date());
        Map<String, Integer> map = getUnReadCount(userId, getIds());
        String message = getSysMessage(getusers(), map);
        LOGGER.info("{}->关闭连接,推送内容:{}", username, message);
        broadcastAllUsers(message);
    }

    /**
     * 出现错误时调用改方法
     */
    @OnError
    public void onError(Session session, Throwable error) {
        LOGGER.info("连接出错了......{}", error);
    }

    /**
     * 获取所有的用户名
     */
    private Set<String> getIds() {
        return ChatEndpoint.onLineUser.keySet();
    }

    /**
     * 获取所有用户 如果是系统消息就返回这个
     */
    private Collection<Result> getusers() {
        List<Result> listAll = this.redisUtils.getListAlls("onLineUsers");
        return listAll;
    }

    /**
     * 消息的推送,将消息推送给所有用户
     */
    private void broadcastAllUsers(String message) {
        try {
            // 将消息推送给所有的客户端
            Set<String> ids = getIds();
            for (String id : ids) {
                Session session = onLineUser.get(id);
                // 判断用户是否是连接状态
                if (session.isOpen()) {
                    session.getBasicRemote().sendText(message);
                }
            }
            LOGGER.info("给{}推送了{}", getIds(), message);
        } catch (Exception e) {
            LOGGER.error("广播发送系统消息失败！{}", e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * @title 组织消息内容
     */
    public static String getMessage(Message msgData, String message) {
        ResultMessage resultMessage = new ResultMessage();
        resultMessage.setSystemMsgFlag(false);
        // 消息发送人id
        String fromId = msgData.getFromId();
        // 消息发送人姓名
        String fromName = msgData.getFromName();
        resultMessage.setFromId(fromId);
        resultMessage.setFromName(fromName);
        resultMessage.setToId(msgData.getToId());
        resultMessage.setToName(msgData.getToName());
        resultMessage.setMessage(message);
        resultMessage.setDateStr(new Date());
        resultMessage.setMessageType(msgData.getMessageType()!=null?msgData.getMessageType():1);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(resultMessage);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * @return String
     * @Title 组织系统推送数据信息
     */
    public static String getSysMessage(Collection<Result> collection, Map<String, Integer> map) {
        ResultMessage resultMessage = new ResultMessage();
        resultMessage.setSystemMsgFlag(true);
        resultMessage.setMessage(collection);
        resultMessage.setDateStr();
        resultMessage.setMap(map);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(resultMessage);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * @Title 心跳检测机制
     */
    public static void heartCheck(Session session) {
        try {
            Map<String, Object> params = new HashMap<>(2);
            params.put("type", "PONG");
            session.getAsyncRemote().sendText(JSON.toJSONString(params));
            LOGGER.info("应答客户端的消息：{}", JSON.toJSONString(params));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, Integer> getUnReadCount(String userId, Set<String> ids) {
        Map<String, Integer> map = new HashMap<>(2);
        if (!ObjectUtils.isBlank(ids) && !ObjectUtils.isBlank(userId)) {
            for (String id : ids) {
                // 主动刷新页面,获取未读数据条数
                String redisCountKey1 = "UN_READ_MSG_COUNT_" + id + "_" + userId;
                // 好友刷新页面,推送未读数据条数
                String redisCountKey2 = "UN_READ_MSG_COUNT_" + userId + "_" + id;
                if (this.redisUtils.isExists(redisCountKey1) || this.redisUtils.isExists(redisCountKey2)) {
                    // 主动刷新页面,获取未读数据条数
                    String count1 = ObjectUtils.getStr(this.redisUtils.getStr(redisCountKey1));
                    if (StringUtils.isNotBlank(count1)) {
                        map.put(id, Integer.valueOf(count1));
                    }
                    // 好友刷新页面,推送未读数据条数
                    String count2 = ObjectUtils.getStr(this.redisUtils.getStr(redisCountKey2));
                    if (StringUtils.isNotBlank(count2)) {
                        map.put(userId, Integer.valueOf(count2));
                    }
                }
            }
        }
        return map;
    }
}
