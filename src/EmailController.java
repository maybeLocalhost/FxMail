import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import model.User;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;

import static model.POP3Receive.isContainAttachment;
import static model.POP3Receive.saveAttachment;
import static model.User.getEmailText;


public class EmailController {
    @FXML
    private GridPane EmailPane;

    @FXML
    private Button closeEmail, downloadFile;

    @FXML
    private TextArea EmailArea;


    public EmailController(){}


    @FXML
    private void initialize() throws IOException, MessagingException {
        EmailArea.setText(getEmailText());
        //closeEmail.setGraphic(new ImageView(new Image("/view/img/Return.png")));
        //存在附件，显示附件下载按钮
        if (isContainAttachment(User.getMsg())){
            downloadFile.setVisible(true);
            downloadFile.setGraphic(new ImageView(new Image("/view/img/Download.png")));
        }
    }

    //退出当前窗口
    public void closeEmailPane(ActionEvent actionEvent) {
        Stage stage = (Stage)closeEmail.getScene().getWindow();
        stage.close();
    }

    public void download(ActionEvent actionEvent) throws IOException, MessagingException {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("选择保存的文件夹");
        Stage selectFile = new Stage();
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File file = directoryChooser.showDialog(selectFile);
        if (file != null) {
            String dirPath = file.getAbsolutePath();
            //附件下载
            saveAttachment(User.getMsg(), dirPath);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("附件下载成功!");
            alert.setHeaderText(null);
            alert.setContentText("附件已保存到：" + dirPath);
            alert.showAndWait();

            System.out.println("文件已保存到：" + dirPath);
        }
    }
}
