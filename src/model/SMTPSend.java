package model;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

public class SMTPSend {
    /**
     *
     * @param fromEmail 登录用户名
     * @param password 登录密码
     * @param fromNickname  发件人昵称
     * @param toEmail       收件人邮箱
     * @param toNickname    收件人昵称
     * @param toTheme         邮件主题
     * @param content       邮件内容
     * @param path          附件路径
     */
    public static void sendEmail(String fromEmail, String password, String fromNickname, String toEmail, String toNickname, String toTheme, String content, String path) {

        System.out.println("--------写信--------");

        //创建参数配置, 用于连接邮件服务器的参数配置
        Properties props = new Properties();                    // 参数配置
        props.setProperty("mail.transport.protocol", "smtp");   // 使用的协议（JavaMail规范要求）
        props.setProperty("mail.smtp.host", "smtp.qq.com");   // 发件人的邮箱的 SMTP 服务器地址
        props.setProperty("mail.smtp.auth", "true");            // 需要请求认证

        //创建会话
        Session session = Session.getDefaultInstance(props);

        try {
            //创建邮件
            //创建邮件对象
            MimeMessage message = new MimeMessage(session);

            //From: 发件人
            message.setFrom(new InternetAddress(fromEmail, fromNickname, "UTF-8"));

            //To: 收件人
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail, toNickname, "UTF-8"));

            //Subject: 邮件主题
            message.setSubject(toTheme, "UTF-8");

            //Content: 邮件内容
            //一个一个Multipart对象包含一个或多个bodypart对象，组成邮件正文
            MimeMultipart multipart = new MimeMultipart();
            //创建文本节点
            MimeBodyPart text = new MimeBodyPart();
            text.setContent(content, "text/html;charset=UTF-8");

            if (path != null){
                //创建附件结点
                MimeBodyPart file = new MimeBodyPart();
                DataHandler dataHandler = new DataHandler(new FileDataSource(path));
                file.setDataHandler(dataHandler);
                file.setFileName(MimeUtility.encodeText(dataHandler.getName()));

                //将text和file添加到multipat
                multipart.addBodyPart(text);
                multipart.addBodyPart(file);
                multipart.setSubType("mixed");  //混合关系
            }else {
                //将text添加到multipat
                multipart.addBodyPart(text);
                multipart.setSubType("mixed");  //混合关系
            }

            message.setContent(multipart);
            message.setSentDate(new Date());
            message.saveChanges();

            Transport transport = session.getTransport("smtp");
            //使用 邮箱账号 和 密码 连接邮件服务器
            transport.connect(fromEmail, password);

            //发送邮件
            transport.sendMessage(message, message.getAllRecipients());

            // 关闭连接
            transport.close();

            System.out.println("-------已发送-------");

        } catch (MessagingException | UnsupportedEncodingException m) {
            m.printStackTrace();
        }
    }

}
