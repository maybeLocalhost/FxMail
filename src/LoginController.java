import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.User;


public class LoginController {
    @FXML
    private GridPane loginPane;

    @FXML
    private TextField userEmail;

    @FXML
    private PasswordField userpwd;

    @FXML
    private Button buttonLogin;

    public LoginController(){}

    @FXML
    private void initialize(){
        userEmail.setText("**********@qq.com");
        userpwd.setText("***授权码***");
    }

    public void login() throws Exception {
        //存储用户信息
        User account = new User(userEmail.getText(), userpwd.getText());
        System.out.println(userEmail.getText() + "已登录");

        //启动第二窗口
        Stage primaryStage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/view/SelectUI.fxml"));
        primaryStage.setTitle("Fx邮件客户端");
        primaryStage.getIcons().add(new Image("/view/img/Email.png"));
        primaryStage.setScene(new Scene(root, 900, 600));
        primaryStage.show();

        //退出当前窗口
        Stage stage = (Stage)buttonLogin.getScene().getWindow();
        stage.close();
    }

}
