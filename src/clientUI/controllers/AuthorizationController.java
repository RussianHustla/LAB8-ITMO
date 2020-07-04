package clientUI.controllers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ResourceBundle;

import app.DBManager;
import app.User;
import clientUI.ClientUI;
import clientUI.animations.Shake;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.postgresql.util.PSQLException;

import static clientUI.ClientUI.currentBundle;

public class AuthorizationController {


    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField login_field;

    @FXML
    private PasswordField password_field;

    @FXML
    private Button login_button;

    @FXML
    private Button signup_button;

    @FXML
    private Text message_text;

    @FXML
    private Label Auth_label;

    @FXML
    void initialize() {
        translate();

        login_button.setOnAction(event -> {
            String login = login_field.getText().trim();
            String password = password_field.getText().trim();

            if (!login.equals("") && !password.equals("")) {
                System.out.println(login);
                System.out.println(password);

                DBManager dbManager = new DBManager();

                int id = 0;
                Color color = null;
                try {
                    id = dbManager.logInUser(login, password);
                } catch (SQLException throwables) {
//                    throwables.printStackTrace();
                    try {
                        message_text.setText(new String(currentBundle.getString("server_is_not_available").getBytes ("ISO-8859-1"),"windows-1251"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

                if (id > 0) {
                    try {
                        color = dbManager.getUserColor(id);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    ClientUI.userId = id;
                    try {
                        ClientUI.user = new User(ClientUI.userId, login, password);
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    System.out.println(ClientUI.user);
                    try {
                        System.out.println(new String(currentBundle.getString("auth_success").getBytes ("ISO-8859-1"),"windows-1251") + ClientUI.user.getId());
                        message_text.setText(new String(currentBundle.getString("auth_success").getBytes ("ISO-8859-1"),"windows-1251") + ClientUI.user.getId());
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    try {
                        ClientUI.userColor = dbManager.getUserColor(ClientUI.user.getId());
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    login_button.getScene().getWindow().hide();
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/clientUI/view/app.fxml"));
                    try {
                        loader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Parent root = loader.getRoot();
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.showAndWait();
//                    AppController controller = loader.getController();

                    stage.setOnCloseRequest(event1 -> {
                        System.out.println("закрываемся");
                        Platform.exit();
                        System.exit(0);
                    });



                    //System.out.println(user);
                } else {
                    System.err.println("Вход не выполнен, проверте корректность данных для входа и повторите попытку, либо зарегистрируйтесь");
                    try {
                        message_text.setText(new String(currentBundle.getString("auth_err").getBytes ("ISO-8859-1"),"windows-1251"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    Shake animationlogin = new Shake(login_field);
                    animationlogin.play();
                    Shake animationPass = new Shake(password_field);
                    animationPass.play();
                    login_field.setText("");
                    password_field.setText("");
                }
            } else {
                if (login.equals("")) {
                    Shake animation = new Shake(login_field);
                    animation.play();
                }
                if (password.equals("")) {
                    Shake animation = new Shake(password_field);
                    animation.play();
                }
                }

        });

        signup_button.setOnAction(event -> {
            String login = login_field.getText().trim();
            String password = password_field.getText().trim();

            if (!login.equals("") && !password.equals("")) {
                System.out.println(login);
                System.out.println(password);

                DBManager dbManager = new DBManager();

                int id = 0;
                Color color = null;
                try {
                    id = dbManager.signUpUser(login, password);

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }


                if (id > 0) {
                    try {
                        color = dbManager.getUserColor(id);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    ClientUI.userId = id;
                    try {
                        ClientUI.user = new User(ClientUI.userId, login, password);
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    System.out.println(ClientUI.user);
                    System.out.println("Зарегистрирован полльзователь с id = " + ClientUI.user.getId());
                    try {
                        message_text.setText(new String(currentBundle.getString("signup_success").getBytes ("ISO-8859-1"),"windows-1251") + ClientUI.user.getId());
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    try {
                        ClientUI.userColor = dbManager.getUserColor(ClientUI.user.getId());
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    signup_button.getScene().getWindow().hide();
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/clientUI/view/app.fxml"));
                    try {
                        loader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Parent root = loader.getRoot();
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.showAndWait();


                    //System.out.println(user);
                } else if (id == -2){
                    System.err.println("Пользователь с таким логином уже существует");
                    try {
                        message_text.setText(new String(currentBundle.getString("signup_err").getBytes ("ISO-8859-1"),"windows-1251"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    Shake animationlogin = new Shake(login_field);
                    animationlogin.play();
                    Shake animationPass = new Shake(password_field);
                    animationPass.play();
                    login_field.setText("");
                    password_field.setText("");
                }
            } else {
                if (login.equals("")) {
                    Shake animation = new Shake(login_field);
                    animation.play();
                }
                if (password.equals("")) {
                    Shake animation = new Shake(password_field);
                    animation.play();
                }
            }

        });

    }

    public void translate() {
        try {
            login_button.setText(new String(currentBundle.getString("login_button").getBytes ("ISO-8859-1"),"windows-1251"));
            signup_button.setText(new String(currentBundle.getString("signup_button").getBytes ("ISO-8859-1"),"windows-1251"));
            login_field.setPromptText(new String(currentBundle.getString("login_prompt").getBytes ("ISO-8859-1"),"windows-1251"));
            password_field.setPromptText(new String(currentBundle.getString("pass_prompt").getBytes ("ISO-8859-1"),"windows-1251"));
            Auth_label.setText(new String(currentBundle.getString("auth_label").getBytes ("ISO-8859-1"),"windows-1251"));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

}

