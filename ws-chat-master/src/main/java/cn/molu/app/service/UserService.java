package cn.molu.app.service;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import cn.molu.app.pojo.User;
import cn.molu.app.vo.R;

/**
 * (User)表服务接口
 *
 * @author 陌路
 * @since 2022-04-21 16:55:04
 */
public interface UserService {

    /**
     * 用户登录
     * @return R
     */
    R login(String phone, String password, Map<String, Object> params, HttpServletResponse res);

    User queryUserByToken(String token);

    String getToken(User user) throws Exception;

}
