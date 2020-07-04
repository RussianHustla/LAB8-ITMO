package clientUI.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

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
        try {
            String response = (String) AppController.requestToServer("info");
            info_text.setText(response);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
