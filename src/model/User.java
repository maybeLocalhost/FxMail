package model;

import javax.mail.internet.MimeMessage;
import java.util.Map;

public class User {
    private static String fromEmail = null;
    private static String frompwd = null;
    private static String filePath = null;
    private static String dirPath = null;
    private static String emailText = null;

    private static MimeMessage msg;

    private static Map<Integer, MimeMessage> map;

    public User(){}

    public User(String fromEmail, String frompwd){
        this.fromEmail = fromEmail;
        this.frompwd = frompwd;
    }

    //set
    public void setFromEmail(String fromEmail){
        this.fromEmail = fromEmail;
    }

    public void setFrompwd(String frompwd) {
        this.frompwd = frompwd;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    public static void setEmailText(String emailText) {
        User.emailText = emailText;
    }

    public static void setMsg(MimeMessage msg) {
        User.msg = msg;
    }

    public static void setMap(Map<Integer, MimeMessage> map) {
        User.map = map;
    }

    //get
    public String getFromEmail() {
        return fromEmail;
    }

    public String getFrompwd() {
        return frompwd;
    }

    public String getFilePath(){return filePath;}

    public String getDirPath() {
        return dirPath;
    }

    public static String getEmailText() {
        return emailText;
    }

    public static MimeMessage getMsg() {
        return msg;
    }

    public static Map<Integer, MimeMessage> getMap() {
        return map;
    }
}

