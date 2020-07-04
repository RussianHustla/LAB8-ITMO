package clientUI;

import app.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public class ClientUI extends Application {
    public static int userId = -1;
    public static User user = null;
    public static Color userColor = null;
    public static ResourceBundle currentBundle = ResourceBundle.getBundle("resources");

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("view/authorization.fxml"));
        primaryStage.setTitle("Салам Аллейкум");
        primaryStage.setScene(new Scene(root, 700, 400));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
