package cn.molu.app.controller;

import cn.molu.app.pojo.Message;
import cn.molu.app.pojo.User;
import cn.molu.app.pojo.UserQuery;
import cn.molu.app.service.UserService;
import cn.molu.app.vo.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    private static Logger logger= LoggerFactory.getLogger(UserController.class);

    @RequestMapping(value = "/queryFriendsList")
    public Object queryFriendsList(@RequestBody User user){
        List<User> userList=new ArrayList<>();
        try {
            userList= userService.queryFriendsList(user);
        }catch (Exception e){
            logger.error(e.getMessage());
        }
       return R.ok(userList);
    }

    @GetMapping(value = "/register")
    public ModelAndView register(){
        return new ModelAndView("chat/register");
    }

    @PostMapping("/registerUser")
    public Object registerUser(@RequestParam("username")String username,@RequestParam("phonenumber")String phonenumber,@RequestParam("pwd")String pwd,@RequestParam("verify")String verify,@RequestParam("mailbox")String mailbox){

        return userService.registerUser(username,phonenumber,pwd,verify,mailbox);

    }
    @PostMapping("/sendVerificationCode")
   public void sendVerificationCode(@RequestBody User user){
        userService.sendVerificationCode(user.getMailbox());
   }

    @PostMapping("/getUserList")
   public R getUserList(@RequestBody UserQuery userQuery){
        return userService.getUserList(userQuery);
   }

    @PostMapping("/addUser")
   public R addUser(@RequestBody Map map){
        return userService.addUser(map);
   }

    @PostMapping("/addUserMessage")
   public R addUserMessage(HttpServletRequest req){
        Message message=new Message();
        message.setMessage(req.getParameter("message"));
        message.setFromId(req.getParameter("fromId"));
        message.setFromName(req.getParameter("fromName"));
        message.setToId(req.getParameter("toId"));
        message.setToName(req.getParameter("toName"));
        return userService.addUserMessage(message);
   }

    @PostMapping("/queryAddUserMessage")
   public R queryAddUserMessage(HttpServletRequest req){
        return userService.queryAddUserMessage(req.getParameter("userId"));
    }

    @PostMapping("/queryAddUserMessageOne")
    public R queryAddUserMessageOne(@RequestBody User user){
        return userService.queryAddUserMessageOne(String.valueOf(user.getId()));
    }

    @PostMapping("/deleAddUserMessage")
    public R deleAddUserMessage(@RequestBody User user){
        return userService.deleAddUserMessage(String.valueOf(user.getId()));
    }
}
