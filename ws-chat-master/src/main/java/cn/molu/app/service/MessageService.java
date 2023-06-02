package cn.molu.app.service;

import cn.molu.app.pojo.MessageQuery;
import cn.molu.app.pojo.ResultMessage;

public interface MessageService {

    void addMessage(ResultMessage message);

    Object queryChatMessageList(MessageQuery messageQuery);
}
