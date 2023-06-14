package cn.molu.app.controller;

import java.time.Duration;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.hutool.core.lang.Validator;
import cn.molu.app.pojo.User;
import cn.molu.app.service.UserService;
import cn.molu.app.utils.ObjectUtils;
import cn.molu.app.utils.RedisUtils;
import cn.molu.app.vo.R;

/**
 * @author dell
 */
@Controller
@RequestMapping("index")
public class LoginController {

    private final static Logger log = LogManager.getLogger(LoginController.class);

    @Resource
    private UserService userService;

    @Resource
    private RedisUtils redisUtils;

    /**
     * 处理用户登录请求.
     *
     * @param params 用户登录数据
     */
    @ResponseBody
    @PostMapping("/login")
    public R login(@RequestParam Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) {
        String remoteAddr = ObjectUtils.getIpAddr(req);
        log.info("获取remoteAddr..{}", remoteAddr);
        String phone = ObjectUtils.getStr(params.get("phone"));
        String pwd = ObjectUtils.getStr(params.get("password"));
        ObjectUtils.checkNull(res, phone, pwd, "手机号或密码输入错误...");
        boolean isMobile = Validator.isMobile(phone);
        if (!isMobile) {
            return R.err("手机号格式错误...");
        }
        return this.userService.login(phone, pwd, params, res);
    }

    /**
     * 登录成功后跳转到聊天页面.
     *
     * @return String 跳转到聊天室，如果没有登录，则返回登录页面
     * @Title toChatroom
     */
    @GetMapping("/toChatroom/{token}")
    public ModelAndView toChatroom(@PathVariable("token") String token, HttpServletRequest req) {
        ModelAndView mv;
        if (ObjectUtils.isEmpty(token)) {
            return new ModelAndView("chat/login");
        }
        User user = this.userService.queryUserByToken(token);
        // token过期
        if (ObjectUtils.isEmpty(user)) {
            return new ModelAndView("chat/login");
        }
        String userId = ObjectUtils.getStr(user.getId());
        String username = user.getUsername();
        mv = new ModelAndView("views/chat");
        mv.addObject("name", username);
        mv.addObject("id", userId);
        mv.addObject("sessionId", req.getSession().getId());
        redisUtils.setObj(userId, user, Duration.ofDays(1));
        return mv;
    }


    /**
     * 清空
     */
    @ResponseBody
    @PostMapping("/clearCount")
    public void setCount(HttpServletRequest req, HttpServletResponse res) {
        String fromId = req.getParameter("fromId");
        String userId = req.getParameter("userId");
        if (StringUtils.isNotBlank(fromId) && StringUtils.isNotBlank(userId)) {
            try {
                String redisCountKey = "UN_READ_MSG_COUNT_" + fromId + "_" + userId;
                if (this.redisUtils.isExists(redisCountKey)) {
                    this.redisUtils.remove(redisCountKey);
                }
                ObjectUtils.printJsonMsg(res, true, "清除成功", 0);
                log.info("清除未读消息...{}", redisCountKey);
            } catch (Exception e) {
                log.info("清除未读消息时出现异常...{}", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @ResponseBody
    @PostMapping("/setCount")
    public void getCount(HttpServletRequest req, HttpServletResponse res) {
        String fromId = req.getParameter("fromId");
        String userId = req.getParameter("userId");
        String count = ObjectUtils.getStr(req.getParameter("count"));
        log.info("接收到未读消息条数:...{}", count);
        if (StringUtils.isNotBlank(fromId) && StringUtils.isNotBlank(userId)) {
            try {
                count = ObjectUtils.getStr(Integer.parseInt(count) + 1);
                String redisCountKey = "UN_READ_MSG_COUNT_" + fromId + "_" + userId;
                this.redisUtils.setStr(redisCountKey, count, Duration.ofDays(7));
                ObjectUtils.printJsonMsg(res, true, "设置成功", count);
                log.info("获取未读消息条数:{}...{}", count, redisCountKey);
            } catch (Exception e) {
                log.info("获取未读消息时出现异常...{}", e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
