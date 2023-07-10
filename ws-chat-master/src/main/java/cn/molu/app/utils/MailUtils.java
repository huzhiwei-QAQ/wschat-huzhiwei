package cn.molu.app.utils;


import cn.molu.app.pojo.Email;

import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class MailUtils {

    public static void sendMail(Email mail) {
        //创建邮件对象
        Properties properties = new Properties();
        //发送方邮件服务器地址，要根据邮箱的不同需要自行设置
        properties.put("mail.smtp.host", "smtp.163.com");
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.port", "25");
        //设置成需要邮件服务器认证
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.debug", "true");
        Session session = Session.getInstance(properties);
        session.setDebug(true);

        try {
            Message message = new MimeMessage(session);
            //设置发件人
            message.setFrom(new InternetAddress(mail.getSender()));
            //设置收件人
            message.addRecipient(RecipientType.TO, new InternetAddress(mail.getReceiver()));
            // 设置标题
            message.setSubject(mail.getSubject());
            //邮件内容
            message.setContent(mail.getMessage(), "text/html;charset=utf-8");
            //发送邮件
            Transport transport = session.getTransport("smtp");
            transport.connect("smtp.163.com", mail.getUserName(), mail.getPassword());//以smtp方式登录邮箱
            //发送邮件,其中第二个参数是所有已设好的收件人地址
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
