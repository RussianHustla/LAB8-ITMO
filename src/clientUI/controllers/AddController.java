package clientUI.controllers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.ResourceBundle;

import app.Reader;
import clientUI.ClientUI;
import clientUI.animations.Shake;
import collection.Coordinates;
import collection.Flat;
import collection.Furnish;
import collection.House;
import commands.Add;
import commands.Command;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import static clientUI.ClientUI.currentBundle;
import static clientUI.controllers.AppController.userInterface;

public class AddController {
    ObservableList<Furnish> furnishes = FXCollections.observableArrayList();

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField name_field;

    @FXML
    private TextField coordX_field;

    @FXML
    private TextField coordY_field;

    @FXML
    private TextField area_field;

    @FXML
    private TextField numerOfRooms_field;

    @FXML
    private TextField kitchenArea_field;

    @FXML
    private TextField timeToMetroOnFoot_field;

    @FXML
    private TextField houseName_field;

    @FXML
    private TextField year_field;

    @FXML
    private TextField numberOfFlatsOnFloor_field;

    @FXML
    private ChoiceBox<Furnish> furnish_choiceBox;

    @FXML
    private Button add_button;

    @FXML
    private Text message_text;

    @FXML
    private Text furnish_text;

    @FXML
    private Label add_label;

    @FXML
    void initialize() {
        try {
            translate();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        furnishes.addAll(Arrays.asList(Furnish.values()));
        furnish_choiceBox.setItems(furnishes);

        add_button.setOnAction(event -> {
            message_text.setText("");
            Boolean check = true;

            String name = name_field.getText().trim();
            if (validate(name).equals("empty")) {
                message_text.setText("Поле \"" + name_field.getPromptText() + "\" не может быть пустым");
                Shake animation = new Shake(name_field);
                animation.play();
                check = false;
                name_field.setText("");
            }

            String coordX = coordX_field.getText().trim();
            Double x = null;
            if (validate(coordX).equals("empty")) {
                message_text.setText("Поле \"" + coordX_field.getPromptText() + "\" не может быть пустым");
                Shake animation = new Shake(coordX_field);
                animation.play();
                check = false;
                coordX_field.setText("");
            } else {
                try {
                    x = Double.parseDouble(coordX);
                } catch (NumberFormatException e) {
                    message_text.setText("Некорректное значение поля \"" + coordX_field.getPromptText() + "\"");
                    Shake animation = new Shake(coordX_field);
                    animation.play();
                    check = false;
                    coordX_field.setText("");
                }

            }

            String coordY = coordY_field.getText().trim();
            Double y = null;
            if (validate(coordY).equals("empty")) {
                message_text.setText("Поле \"" + coordY_field.getPromptText() + "\" не может быть пустым");
                Shake animation = new Shake(coordY_field);
                animation.play();
                check = false;
            } else {
                try {
                    y = Double.parseDouble(coordY);
                } catch (NumberFormatException e) {
                    message_text.setText("Некорректное значение поля \"" + coordY_field.getPromptText() + "\"");
                    Shake animation = new Shake(coordY_field);
                    animation.play();
                    check = false;
                    coordY_field.setText("");
                }
            }

            String areaSt = area_field.getText().trim();
            String val = validate(areaSt, 1, -1);
            Double area = null;
            if (val.equals("empty")) {
                message_text.setText("Поле \"" + area_field.getPromptText() + "\" не может быть пустым");
                Shake animation = new Shake(area_field);
                animation.play();
                check = false;
            } else if (!val.equals("OK")) {
                message_text.setText(val + "\"" + area_field.getPromptText() + "\"");
                Shake animation = new Shake(area_field);
                animation.play();
                check = false;
                area_field.setText("");
            } else {
                area = Double.parseDouble(areaSt);
            }

            String numberOfRoomsSt = numerOfRooms_field.getText().trim();
            val = validate(numberOfRoomsSt, 1, -1);
            int numberOfRooms = 0;
            if (val.equals("empty")) {
                message_text.setText("Поле \"" + numerOfRooms_field.getPromptText() + "\" не может быть пустым");
                Shake animation = new Shake(numerOfRooms_field);
                animation.play();
                check = false;
            } else if (!val.equals("OK")) {
                message_text.setText(val + "\"" + numerOfRooms_field.getPromptText() + "\"");
                Shake animation = new Shake(numerOfRooms_field);
                animation.play();
                check = false;
                numerOfRooms_field.setText("");
            } else {
                numberOfRooms = Integer.parseInt(numberOfRoomsSt);
            }

            String kitchenAreaSt = kitchenArea_field.getText().trim();
            val = validate(kitchenAreaSt, 1, -1);
            int kitchenArea = 0;
            if (val.equals("empty")) {
                message_text.setText("Поле \"" + kitchenArea_field.getPromptText() + "\" не может быть пустым");
                Shake animation = new Shake(kitchenArea_field);
                animation.play();
                check = false;
            } else if (!val.equals("OK")) {
                message_text.setText(val + "\"" + kitchenArea_field.getPromptText() + "\"");
                Shake animation = new Shake(kitchenArea_field);
                animation.play();
                check = false;
                kitchenArea_field.setText("");
            } else {
                kitchenArea = Integer.parseInt(kitchenAreaSt);
            }

            String timeToMetroOnFootSt = timeToMetroOnFoot_field.getText().trim();
            val = validate(timeToMetroOnFootSt, 1, -1);
            Double timeToMetroOnFoot = null;
            if (val.equals("empty")) {
                message_text.setText("Поле \"" + timeToMetroOnFoot_field.getPromptText() + "\" не может быть пустым");
                Shake animation = new Shake(timeToMetroOnFoot_field);
                animation.play();
                check = false;
            } else if (!val.equals("OK")) {
                message_text.setText(val + "\"" + timeToMetroOnFoot_field.getPromptText() + "\"");
                Shake animation = new Shake(timeToMetroOnFoot_field);
                animation.play();
                check = false;
                timeToMetroOnFoot_field.setText("");
            } else {
                timeToMetroOnFoot = Double.parseDouble(timeToMetroOnFootSt);
            }

            Furnish furnish = furnish_choiceBox.getValue();
            if (furnish == null) {
                message_text.setText("Поле \"" + furnish_text.getText() + "\" не может быть пустым");
                Shake animation = new Shake(furnish_choiceBox);
                animation.play();
                check = false;
            }

            String houseName = houseName_field.getText().trim();
            val = validate(houseName);
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
                Coordinates coordinates = new Coordinates(x,y);
                House house = new House(houseName, houseYear, numberOfFlatsOnFloor);

                Flat flat = new Flat(name, coordinates, new Date(), area, numberOfRooms, kitchenArea, timeToMetroOnFoot,
                        furnish, house);

                Command command = new Add();

                String arg[] = new String[]{"-", "-"};
                command.setArgs(arg); //заглушка от NPE
                command.setObject(flat);
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
        name_field.setText("");
        coordX_field.setText("");
        coordY_field.setText("");
        area_field.setText("");
        numerOfRooms_field.setText("");
        kitchenArea_field.setText("");
        timeToMetroOnFoot_field.setText("");
        furnish_choiceBox.setValue(null);
        houseName_field.setText("");
        year_field.setText("");
        numberOfFlatsOnFloor_field.setText("");
    }

    public void translate() throws UnsupportedEncodingException {
        name_field.setPromptText(new String(currentBundle.getString("name_prompt").getBytes ("ISO-8859-1"),"windows-1251"));
        coordX_field.setPromptText(new String(currentBundle.getString("coordX_prompt").getBytes ("ISO-8859-1"),"windows-1251"));
        coordY_field.setPromptText(new String(currentBundle.getString("coordY_prompt").getBytes ("ISO-8859-1"),"windows-1251"));
        area_field.setPromptText(new String(currentBundle.getString("area_prompt").getBytes ("ISO-8859-1"),"windows-1251"));
        numerOfRooms_field.setPromptText(new String(currentBundle.getString("number_of_rooms_prompt").getBytes ("ISO-8859-1"),"windows-1251"));
        kitchenArea_field.setPromptText(new String(currentBundle.getString("kitchen_area_prompt").getBytes ("ISO-8859-1"),"windows-1251"));
        timeToMetroOnFoot_field.setPromptText(new String(currentBundle.getString("time_to_metro_on_foot_prompt").getBytes ("ISO-8859-1"),"windows-1251"));
        houseName_field.setPromptText(new String(currentBundle.getString("house_prompt").getBytes ("ISO-8859-1"),"windows-1251"));
        year_field.setPromptText(new String(currentBundle.getString("year_prompt").getBytes ("ISO-8859-1"),"windows-1251"));
        numberOfFlatsOnFloor_field.setPromptText(new String(currentBundle.getString("number_of_rooms_on_floor_prompt").getBytes ("ISO-8859-1"),"windows-1251"));
        add_label.setText(new String(currentBundle.getString("add_label").getBytes ("ISO-8859-1"),"windows-1251"));
        add_button.setText(new String(currentBundle.getString("add_button").getBytes ("ISO-8859-1"),"windows-1251"));
        furnish_text.setText(new String(currentBundle.getString("furnish_text").getBytes ("ISO-8859-1"),"windows-1251"));
    }

}
