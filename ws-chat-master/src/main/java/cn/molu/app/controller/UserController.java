package cn.molu.app.controller;

import cn.molu.app.pojo.User;
import cn.molu.app.service.UserService;
import cn.molu.app.vo.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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
}
