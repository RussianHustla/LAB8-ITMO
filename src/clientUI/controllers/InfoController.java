package clientUI.controllers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

import static clientUI.ClientUI.currentBundle;

public class InfoController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Text info_text;

    @FXML
    private Label info_label;

    @FXML
    void initialize() {
        translate();
        try {
            String response = (String) AppController.requestToServer("info");
            info_text.setText(response);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
    public void translate() {
        try {
            info_label.setText(new String(currentBundle.getString("info_label").getBytes ("ISO-8859-1"),"windows-1251"));


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
}
