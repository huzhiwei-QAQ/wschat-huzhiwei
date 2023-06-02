package cn.molu.app.callable;

import cn.molu.app.pojo.ResultMessage;
import cn.molu.app.service.MessageService;

public class AddMessageRunnable implements Runnable{

    private ResultMessage message;

    private MessageService messageService;

    public AddMessageRunnable(ResultMessage message, MessageService messageService) {
        this.message = message;
        this.messageService = messageService;
    }

    @Override
    public void run() {
        try {
            messageService.addMessage(message);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
