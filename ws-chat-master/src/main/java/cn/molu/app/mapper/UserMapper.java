package cn.molu.app.mapper;

import cn.molu.app.pojo.User;
import cn.molu.app.pojo.UserQuery;
import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * (User)表数据库访问层
 *
 * @author 陌路
 * @since 2022-04-21 16:55:04
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    List<User> queryFriendsList(@Param("userId") Integer id);

    User selectByPhone(String phonenumber);

    Integer selectCount();

    List<User> selctAllUser( UserQuery userQuery);

    List<Map> queryAlreadyFriend(@Param("userId") Integer userId, @Param("friendId") Integer friendId);

    void deleteRelationship(Integer id);

}

