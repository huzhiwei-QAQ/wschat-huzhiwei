package cn.molu.app.pojo;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @Desc 浏览器发送给服务器的websocket数据.
 */

public class Message {

    private Long id;
    /**
     * 发送方姓名
     */
    private String fromName;
    /**
     * 发送方id
     */
    private String fromId;
    /**
     * 接收方
     */
    private String toName;
    /**
     * 接收方id
     */
    private String toId;
    /**
     * 发送的数据(接收到的数据)
     */
    private String message;
    /**
     * 未读消息的数量
     */
    private int count;
    /**
     * 接收到消息的日期时间
     */

    private Integer messageType;
    private String dateStr = dateFormat(new Date());

    public String getDateStr() {
        return dateStr;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getDateStr(Date date) {
        if (date == null) {
            date = new Date();
        }
        return dateFormat(date);
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public void setDateStr(Date date) {
        if (date == null) {
            date = new Date();
        }
        this.dateStr = dateFormat(date);
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Message(String toName, String message) {
        super();
        this.toName = toName;
        this.message = message;
    }

    public Message() {
        super();
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public Message(String fromName, String toName, String message) {
        super();
        this.fromName = fromName;
        this.toName = toName;
        this.message = message;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", fromName='" + fromName + '\'' +
                ", fromId='" + fromId + '\'' +
                ", toName='" + toName + '\'' +
                ", toId='" + toId + '\'' +
                ", message='" + message + '\'' +
                ", count=" + count +
                ", messageType=" + messageType +
                ", dateStr='" + dateStr + '\'' +
                '}';
    }

    private final static Logger LOGGER = LogManager.getLogger(Result.class);

    public static String dateFormat(Date date) {
        String str = "";
        if (null == date || StringUtils.isBlank(String.valueOf(date))) {
            date = new Date();
        }
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            str = format.format(date);
        } catch (Exception e) {
            LOGGER.info("调用ChatEndpoint类中的dateFormat方法时出错...{}", e.getMessage());
        }
        return str;
    }

}
