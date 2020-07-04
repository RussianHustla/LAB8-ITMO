package clientUI.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

public class HistoryController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Text history_text;

    @FXML
    private Label history_label;

    @FXML
    void initialize() {
        try {
            String response = (String) AppController.requestToServer("history");
            history_text.setText(response);
            
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}

