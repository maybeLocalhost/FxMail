package model;

public class Receive {
    private int id;
    private String nickname;
    private String theme;
    private String time;

    public Receive(){}

    public Receive(int id, String nickname, String theme, String time){
        this.id = id;
        this.nickname = nickname;
        this.theme = theme;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickname(){
        return nickname;
    }

    public void setNickname(String lname){
        this.nickname = lname;
    }

    public String getTheme(){
        return theme;
    }

    public void setTheme(String ltheme){
        this.theme = ltheme;
    }

    public String getTime(){
        return time;
    }

    public void setTime(String ltime){
        this.time = ltime;
    }

}
