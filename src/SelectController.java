import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.web.HTMLEditor;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Receive;
import model.SMTPSend;
import model.User;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.util.*;

import static model.POP3Receive.*;
import static model.User.*;

public class SelectController {
    @FXML
    private SplitPane MasterPane;

    @FXML
    private GridPane OptionPane, MinorPane, WritePane;

    @FXML
    private AnchorPane ReceivePane;

    @FXML
    private ToggleButton ReceiveEmail, WriteEmail, PlatExit;

    @FXML
    private TableView<Receive> EmailList;

    @FXML
    private TableColumn<Receive, String> IdColumn, NicknameColumn, ThemeColumn, TimeColumn;

    @FXML
    private TableColumn DownloadColumn, DeleteColumn;

    @FXML
    private HTMLEditor EmailEditor;

    @FXML
    private TextField fromNickname, toNickname, toEmail, toTheme;

    @FXML
    private Button send, addFile;

    public SelectController(){}

    @FXML
    public void initialize() {
        //给按钮设置图标
        ReceiveEmail.setGraphic(new ImageView(new Image("/view/img/ReceiveEmail.png")));
        WriteEmail.setGraphic(new ImageView(new Image("/view/img/WriteEmail.png")));
        PlatExit.setGraphic(new ImageView(new Image("/view/img/Exit.png")));
    }


    //添加表格内容
    public Map<Integer, MimeMessage> getReceive() throws MessagingException, UnsupportedEncodingException {

        Map<Integer, Receive> mapList = new HashMap<Integer, Receive>();
        Map<Integer, MimeMessage> messageList = new HashMap<Integer, MimeMessage>();


        //准备连接服务器的会话信息
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "pop3");		// 协议
        props.setProperty("mail.pop3.port", "110");				// 端口
        props.setProperty("mail.pop3.host", "pop.qq.com");	// pop3服务器

        User user = new User();

        // 创建Session实例对象
        Session session = Session.getInstance(props);
        Store store = session.getStore("pop3");
        store.connect(user.getFromEmail(), user.getFrompwd());

        // 获得收件箱
        Folder folder = store.getFolder("INBOX");
        //Folder.READ_ONLY：只读权限
        //Folder.READ_WRITE：可读可写（可以修改邮件的状态）
        folder.open(Folder.READ_WRITE);	//打开收件箱

        // 获得收件箱中的邮件总数
        System.out.println("邮件总数: " + folder.getMessageCount());

        // 得到收件箱中的所有邮件,并解析
        Message[] messages = folder.getMessages();

        if (messages == null || messages.length < 1){
            throw new MessagingException("未找到要解析的邮件!");
        }

        // 解析所有邮件
        for (int i = 0, count = messages.length; i < count; i++){
            MimeMessage msg = (MimeMessage) messages[i];
            Receive receive = new Receive(msg.getMessageNumber(), getFrom(msg), getSubject(msg), getSentDate(msg, null));

            mapList.put(msg.getMessageNumber(), receive);
            messageList.put(msg.getMessageNumber(), msg);
        }

        this.EmailList.getItems().clear();
        Iterator<Integer> iterator = mapList.keySet().iterator();
        while (iterator.hasNext()){
            int key = iterator.next();
            this.EmailList.getItems().add(mapList.get(key));
        }

        //添加删除按钮
        DeleteColumn.setCellFactory((col) -> {
            TableCell<Receive, String> cell = new TableCell<Receive, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    this.setGraphic(null);

                    if (!empty) {
                        Button delBtn = new Button("删除");
                        this.setGraphic(delBtn);
                        delBtn.setGraphic(new ImageView(new Image("/view/img/Delete.png")));
                        delBtn.setOnMouseClicked((me) -> {
                            Receive clickedRec = this.getTableView().getItems().get(this.getIndex());
                            String delNickname = clickedRec.getNickname();
                            String delTheme = clickedRec.getTheme();
                            int delflag = clickedRec.getId();
                            //设置删除标记
                            try {
                                messages[delflag-1].setFlag(Flags.Flag.DELETED, true);
                                //释放资源
                                folder.close(true);
                                store.close();
                            } catch (MessagingException e) {
                                e.printStackTrace();
                            }

                            System.out.println("删除了" + delNickname+ "的“" + delTheme + "”邮件\n");
                        });
                    }
                }
            };
            return cell;
        });

        return messageList;
    }


    //显示表格
    public void showEmailTable(Map<Integer, MimeMessage> messageMap){
        IdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        NicknameColumn.setCellValueFactory(new PropertyValueFactory<>("nickname"));
        ThemeColumn.setCellValueFactory(new PropertyValueFactory<>("theme"));
        TimeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));

        //添加下载按钮
        DownloadColumn.setCellFactory((col) -> {
            TableCell<Receive, String> cell = new TableCell<Receive, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    this.setGraphic(null);

                    if (!empty) {
                        Button downBtn = new Button("下载");
                        this.setGraphic(downBtn);
                        downBtn.setGraphic(new ImageView(new Image("/view/img/Download.png")));
                        downBtn.setOnMouseClicked((me) -> {
                            Receive clickedRec = this.getTableView().getItems().get(this.getIndex());
                            DirectoryChooser directoryChooser = new DirectoryChooser();
                            directoryChooser.setTitle("选择保存的文件夹");
                            Stage selectFile = new Stage();
                            directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
                            File file = directoryChooser.showDialog(selectFile);
                            if (file != null){
                                String dirPath = file.getAbsolutePath();
                                try {
                                    File fp = new File(dirPath + "\\" + getMsg().getSubject()+ ".txt");
                                    PrintWriter pfp = new PrintWriter(fp);
                                    pfp.print(getEmailText());
                                    pfp.close();

                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("邮件下载成功!");
                                    alert.setHeaderText(null);
                                    alert.setContentText("邮件已保存到：" + fp.getPath());
                                    alert.showAndWait();

                                } catch (MessagingException | FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                            System.out.println("下载了" + clickedRec.getNickname() + "的“" + clickedRec.getTheme() + "”邮件\n");
                        });
                    }
                }
            };
            return cell;
        });

        //单击行显示邮件内容
        EmailList.setRowFactory(tv -> {
            TableRow<Receive> row = new TableRow<Receive>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && (!row.isEmpty())){
                    int flag = row.getItem().getId();
                    try {
                        String mailText = parseMessage(messageMap.get(flag));
                        setEmailText(mailText);

                        setMsg(messageMap.get(flag));

                        //拉起邮件内容面板
                        Stage primaryStage = new Stage();
                        Parent root = null;
                        root = FXMLLoader.load(getClass().getResource("/view/EmailUI.fxml"));
                        primaryStage.setTitle("邮件客户端");
                        primaryStage.setScene(new Scene(root, 600, 400));
                        primaryStage.show();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }catch (MessagingException e) {
                        e.printStackTrace();
                    }

                }
            });
            return row;
        });
    }

    //打开收信面板
    public void openReceivePane(ActionEvent actionEvent) throws UnsupportedEncodingException, MessagingException {
        ReceivePane.setVisible(true);
        WritePane.setVisible(false);
        Map<Integer, MimeMessage> map = getReceive();
        showEmailTable(map);
    }

    //打开写信面板
    public void openWritePane(ActionEvent actionEvent) {
        ReceivePane.setVisible(false);
        WritePane.setVisible(true);
    }

    //添加附件
    public void openFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择待发送的文件");
        Stage selectFile = new Stage();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File file = fileChooser.showOpenDialog(selectFile);
        if (file != null) {
            String filePath = file.getPath();
            //存放数据
            User path = new User();
            path.setFilePath(filePath);
            System.out.println("待发送附件：" + filePath);
        }
    }

    //发送邮件
    public void sendEmail(ActionEvent actionEvent) {
        User user = new User();
        SMTPSend.sendEmail(user.getFromEmail(), user.getFrompwd(), fromNickname.getText(), toEmail.getText(), toNickname.getText(), toTheme.getText(), EmailEditor.getHtmlText(), user.getFilePath());

    }

    //退出程序
    public void platExit(ActionEvent actionEvent) {
        Platform.exit();
    }

}
