package cn.molu.app.service;

import cn.molu.app.pojo.MessageQuery;
import cn.molu.app.pojo.ResultMessage;
import com.github.pagehelper.PageInfo;

public interface MessageService {

    void addMessage(ResultMessage message);

    PageInfo queryChatMessageList(MessageQuery messageQuery);
}
