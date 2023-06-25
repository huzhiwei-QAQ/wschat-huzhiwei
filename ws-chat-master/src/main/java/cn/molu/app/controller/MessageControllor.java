package cn.molu.app.controller;

import cn.molu.app.pojo.MessageQuery;
import cn.molu.app.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/message")
public class MessageControllor {

    @Autowired
    private MessageService messageService;

    private static Logger logger= LoggerFactory.getLogger(MessageControllor.class);

    @RequestMapping(value = "/queryChatMessageList")
    public Object queryChatMessageList(@RequestBody MessageQuery messageQuery){
      return    messageService.queryChatMessageList(messageQuery);
    }


}
