package cn.molu.app.mapper;

import cn.molu.app.pojo.Message;
import cn.molu.app.pojo.MessageQuery;
import cn.molu.app.pojo.ResultMessage;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface MessageMapper {
    void addMessage(ResultMessage message);

    List<Message> queryChatMessageList(MessageQuery messageQuery);


}
