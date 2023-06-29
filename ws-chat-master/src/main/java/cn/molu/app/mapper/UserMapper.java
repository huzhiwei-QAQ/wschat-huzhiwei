package cn.molu.app.mapper;

import cn.molu.app.pojo.User;
import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (User)表数据库访问层
 *
 * @author 陌路
 * @since 2022-04-21 16:55:04
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    List<User> queryFriendsList(@Param("userId") Integer id);

}

