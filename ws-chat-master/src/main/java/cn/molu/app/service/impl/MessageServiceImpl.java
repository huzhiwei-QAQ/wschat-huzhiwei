package cn.molu.app.service.impl;

import cn.molu.app.mapper.MessageMapper;
import cn.molu.app.pojo.Message;
import cn.molu.app.pojo.MessageQuery;
import cn.molu.app.pojo.ResultMessage;
import cn.molu.app.service.MessageService;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {


@Resource
  private MessageMapper messageMapper;

    public void addMessage(ResultMessage message){
         message.setId(IdWorker.getId());
        messageMapper.addMessage(message);
    }

    @Override
    public Object queryChatMessageList(MessageQuery messageQuery) {
        String sort=null;
        if(messageQuery.isAsc()){
            sort="asc";
        }else {
            sort="desc";
        }
        String orderBy= messageQuery.getOrderByField()+" "+sort;
        PageHelper.startPage(messageQuery.getPageNum(),messageQuery.getPageSize(),orderBy);
        List<Message> messageList= messageMapper.queryChatMessageList(messageQuery);
        PageInfo<Message> pageInfo = new PageInfo<>(messageList);
        return pageInfo;
    }


}
