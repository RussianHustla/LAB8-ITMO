package clientUI.controllers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

import app.Reader;
import clientUI.ClientUI;
import clientUI.animations.Shake;
import collection.Coordinates;
import collection.Flat;
import collection.House;
import commands.Add;
import commands.Command;
import commands.Count_greater_than_house;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import static clientUI.ClientUI.currentBundle;

public class CountGreaterThanHouseController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField houseName_field;

    @FXML
    private TextField year_field;

    @FXML
    private TextField numberOfFlatsOnFloor_field;

    @FXML
    private Button count_button;

    @FXML
    private Text message_text;

    @FXML
    private Label count_label;

    @FXML
    void initialize() {
        try {
            translate();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        count_button.setOnAction(event -> {

            message_text.setText("");
            Boolean check = true;

            String houseName = houseName_field.getText().trim();
            String val = validate(houseName);
            if (val.equals("empty")) {
                message_text.setText("Поле \"" + houseName_field.getPromptText() + "\" не может быть пустым");
                Shake animation = new Shake(houseName_field);
                animation.play();
                check = false;
            }

            String houseYearSt = year_field.getText().trim();
            val = validate(houseYearSt, 1, -1);
            int houseYear = 0;
            if (val.equals("empty")) {
                message_text.setText("Поле \"" + year_field.getPromptText() + "\" не может быть пустым");
                Shake animation = new Shake(year_field);
                animation.play();
                check = false;
            } else if (!val.equals("OK")) {
                message_text.setText(val + "\"" + year_field.getPromptText() + "\"");
                Shake animation = new Shake(year_field);
                animation.play();
                check = false;
                year_field.setText("");
            } else {
                houseYear = Integer.parseInt(houseYearSt);
            }

            String numberOfFlatsOnFloorSt = numberOfFlatsOnFloor_field.getText().trim();
            val = validate(numberOfFlatsOnFloorSt, 1, -1);
            int numberOfFlatsOnFloor = 0;
            if (val.equals("empty")) {
                message_text.setText("Поле \"" + numberOfFlatsOnFloor_field.getPromptText() + "\" не может быть пустым");
                Shake animation = new Shake(numberOfFlatsOnFloor_field);
                animation.play();
                check = false;
            } else if (!val.equals("OK")) {
                message_text.setText(val + "\"" + numberOfFlatsOnFloor_field.getPromptText() + "\"");
                Shake animation = new Shake(numberOfFlatsOnFloor_field);
                animation.play();
                check = false;
                numberOfFlatsOnFloor_field.setText("");
            } else {
                numberOfFlatsOnFloor = Integer.parseInt(numberOfFlatsOnFloorSt);
            }

            if (check) {
                House house = new House(houseName, houseYear, numberOfFlatsOnFloor);


                Command command = new Count_greater_than_house();

                String arg[] = new String[]{"-", "-"};
                command.setArgs(arg); //заглушка от NPE
                command.setObject(house);
//                Color colorbuff = ClientUI.user.getColor(); //костыль потому што колор не сериализуется((
//                ClientUI.user.setColor(null);
                command.setUser(ClientUI.user);

                try {
//                    userInterface.send(command);
                    String response = (String) AppController.requestToServer(command);
                    message_text.setText(response);
                    clearFields();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });



    }

    public String validate(String value) {
        if (value.equals("")) {
            return "empty";
        }
        return "OK";
    }

    public String validate(String value, int min, int max) {
        if (value.equals("")) {
            return "empty";
        }
        try {
            if (!checkNumber(Double.parseDouble(value), min, max)) {
                return "Некорректные границы числа в поле";
            }
        } catch (NumberFormatException e) {
            return "Некорректное число в поле";
        }
        return "OK";
    }

    public static boolean checkNumber(double s, int min, int max) {
        return ((min < 0 || s >= min) && (max < 0 || s <= max));
    }

    public void clearFields() {

        houseName_field.setText("");
        year_field.setText("");
        numberOfFlatsOnFloor_field.setText("");
    }


    public void translate() throws UnsupportedEncodingException {
        year_field.setPromptText(new String(currentBundle.getString("year_prompt").getBytes ("ISO-8859-1"),"windows-1251"));
        numberOfFlatsOnFloor_field.setPromptText(new String(currentBundle.getString("number_of_rooms_on_floor_prompt").getBytes ("ISO-8859-1"),"windows-1251"));
        houseName_field.setPromptText(new String(currentBundle.getString("house_prompt").getBytes ("ISO-8859-1"),"windows-1251"));
        count_button.setText(new String(currentBundle.getString("count_button").getBytes ("ISO-8859-1"),"windows-1251"));
        count_label.setText(new String(currentBundle.getString("count_label").getBytes ("ISO-8859-1"),"windows-1251"));
    }
}
