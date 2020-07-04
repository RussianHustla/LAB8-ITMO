package clientUI.controllers;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import app.DBManager;
import app.UserInterface;
import clientUI.ClientUI;
import collection.Coordinates;
import collection.Flat;
import collection.Furnish;
import collection.House;
import commands.Clear;
import commands.Command;
import commands.Remove_first;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import static clientUI.ClientUI.currentBundle;
import static clientUI.controllers.GUIUtils.autoFitTable;
import static com.sun.javafx.scene.control.skin.Utils.getResource;

public class AppController {
    public final static int PORT = 8000;
    public final static String ADDR = "localhost";
    private ObservableList<Flat> flats = FXCollections.observableArrayList();
    private ObservableList<Flat> actualFlats = FXCollections.observableArrayList();
    private ObservableList<String> parameters = FXCollections.observableArrayList("id", "name", "Coordination X",
            "Coordination Y", "Area", "Number of rooms", "Kitchen area", "Time to metro on foot",
            "Furnish", "House name", "House year", "Number of flats on floor", "Author id");
    private ObservableList<String> languages = FXCollections.observableArrayList("Русский", "English");
    public static Socket s;
    public static OutputStream out;
    public static InputStream in;
    public static UserInterface userInterface;
    public static Date lastUpdateDate;
    public static Flat flatToUpdate;
    DBManager dbManager = new DBManager();


    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button add_button;

    @FXML
    private TableView<Flat> flats_table;

    @FXML
    private TableColumn<Flat, Integer> id_column;

    @FXML
    private TableColumn<Flat, String> name_column;

    @FXML
    private TableColumn<Coordinates, Double> coordX_column;

    @FXML
    private TableColumn<Coordinates, Double> coordY_column;

    @FXML
    private TableColumn<Flat, Date> creationDate_column;

    @FXML
    private TableColumn<Flat, Double> area_column;

    @FXML
    private TableColumn<Flat, Integer> numberOfRooms_column;

    @FXML
    private TableColumn<Flat, Integer> kitchenArea_column;

    @FXML
    private TableColumn<Flat, Double> timeToMetroOnFoot_column;

    @FXML
    private TableColumn<Flat, Furnish> furnish_column;

    @FXML
    private TableColumn<House, String> houseName_column;

    @FXML
    private TableColumn<House, Integer> houseYear_column;

    @FXML
    private TableColumn<House, Integer> numberOfFlatsOnFloor_column;

    @FXML
    private TableColumn<Flat, Integer> authorId_column;

    @FXML
    private Text username_text;

    @FXML
    private Text userId_text;

    @FXML
    private Button clear_button;

    @FXML
    private Button count_greater_than_house_button;

    @FXML
    private Button script_button;

    @FXML
    private Button info_button;

    @FXML
    private Button history_button;

    @FXML
    private Button remove_first_button;

    @FXML
    private Button refresh_button;

    @FXML
    private Text lastUpdate_text;

    @FXML
    private Canvas userColor_canvas;

    @FXML
    private ResizableCanvas visual_canvas;

    @FXML
    private AnchorPane canvasParent_pane;

    @FXML
    private ChoiceBox<String> filterParam_choiseBox;

    @FXML
    private Button filter_button;

    @FXML
    private Button abortFilter_button;

    @FXML
    private TextField filter_field;

    @FXML
    private ChoiceBox<String> language_choiceBox;

    @FXML
    private Button changeLanguage_button;

    @FXML
    private Text language_text;

    @FXML
    private Text color_text;

    @FXML
    private Text filterParams_text;

    @FXML
    private Tab table_tab;

    @FXML
    private Tab visual_tab;

    @FXML
    private Text login_text;

    @FXML
    private Text date_text;


    private javafx.event.EventHandler<WindowEvent> closeEventHandler =  event -> {
        System.out.println("Завершение");
        Platform.exit();
        System.exit(0);
    };

    public javafx.event.EventHandler<WindowEvent> getCloseEventHandler(){
        return closeEventHandler;
    }




    @FXML
    void initialize() {

        refresh_button.setText("X");

        drawBackground();


        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(new Updater());
        executorService.shutdown();

        login_text.setText(ClientUI.user.getLogin());
        userId_text.setText("ID #" + ClientUI.userId);
        GraphicsContext context = userColor_canvas.getGraphicsContext2D();
        context.setFill(ClientUI.userColor);
        context.setStroke(ClientUI.userColor);
        context.setLineWidth(5);

        context.fillOval(0,0, 30, 30);
        flats_table.setEditable(true);
        filterParam_choiseBox.setItems(parameters);
        language_choiceBox.setItems(languages);




//        try {
//
//            refresh_table();
//
//        } catch (ConnectException e) {
//            System.err.println("Сервер недоступен : (");
////                System.out.println("Выйти из программы? (y/n)");
////                String conf = Reader.request();
////                if (conf.equals("y")) {
////
////                }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }

        visual_canvas.setOnMouseClicked(event -> {
//            System.out.println("click");
            double clickX = corrX(event.getX());
            double clickY = corrY(event.getY());
//            System.out.println(clickX);
//            System.out.println(clickY);

            for (Flat flat : flats) {
                double flatX = flat.getX();
                double flatY = flat.getY();
                if (getDistance(flatX, flatY, clickX, clickY) <= flat.getArea()/2f) {
                    System.out.println("Clicked on " + flat.getName());
                    flatToUpdate = flat;
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/clientUI/view/update.fxml"));
                    try {
                        loader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Parent root = loader.getRoot();
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.showAndWait();
                }
            }


        });

        changeLanguage_button.setOnAction(event -> {
            String language = language_choiceBox.getValue();

            if (!language.equals("")) {
                if (language.equals("English")) {
                    currentBundle = ResourceBundle.getBundle("resources", new Locale("en", "US"));
                    translate();
                } else if (language.equals("Русский")) {
                    currentBundle = ResourceBundle.getBundle("resources", new Locale("ru", "RU"));
                    translate();
                }
            }
        });

        visual_tab.setOnSelectionChanged(event -> {
            System.out.println("переход на вкладку");
            drawBackground();
            drawFlatsVisualObjects(actualFlats);
        });



        refresh_button.setOnAction(event -> {
//            try {
//                refresh_table();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            }

//            autoFitTable(flats_table);
            Platform.exit();
            System.exit(0);
        });

        count_greater_than_house_button.setOnAction(event -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/clientUI/view/countGreaterThanHouse.fxml"));
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.showAndWait();
        });

        history_button.setOnAction(event -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/clientUI/view/history.fxml"));
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.showAndWait();
        });

        info_button.setOnAction(event -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/clientUI/view/info.fxml"));
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.showAndWait();
        });

        remove_first_button.setOnAction(event -> {
            try {
                Command command = new Remove_first();
                String arg[] = new String[]{"-", "-"};
                command.setArgs(arg); //заглушка от NPE
                command.setUser(ClientUI.user);
                requestToServer(command);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

        script_button.setOnAction(event -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/clientUI/view/executeScript.fxml"));
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.showAndWait();
        });

        clear_button.setOnAction(event -> {
            try {
                Command command = new Clear();
                String arg[] = new String[]{"-", "-"};
                command.setArgs(arg); //заглушка от NPE
                command.setUser(ClientUI.user);
                requestToServer(command);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

        add_button.setOnAction(event -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/clientUI/view/add.fxml"));
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.showAndWait();
        });

        flats_table.setRowFactory( tv -> {
            TableRow<Flat> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    flatToUpdate = row.getItem();
                    System.out.println(flatToUpdate);
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/clientUI/view/update.fxml"));
                    try {
                        loader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Parent root = loader.getRoot();
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.showAndWait();

                }
            });
            return row ;
        });

        //"id", "name", "Coordination X",
        //            "Coordination Y", "Creation Date", "Area", "Number of rooms", "Kitchen area", "Time to metro on foot",
        //            "Furnish", "House name", "House year", "Number of flats on floor", "Author id"

        filter_button.setOnAction(event -> {
            String value = filter_field.getText().trim();
            String param = filterParam_choiseBox.getValue();

            if (!value.equals("") && !(param == null)) {
                List<Flat> answer = null;
                if ("Furnish".equals(param)) {
                    Furnish referenceFurnish = null;
                    try {
                        referenceFurnish = Furnish.valueOf(value.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.err.println("Некорректное значение");
                    }

                    Furnish finalReferenceFurnish = referenceFurnish;
                    answer = actualFlats.stream()
                            .filter(flat -> flat.getFurnish().equals(finalReferenceFurnish))
                            .collect(Collectors.toList());
                } else if ("id".equals(param)) {
                    int reference = 0;
                    try {
                        reference = Integer.valueOf(value);
                    } catch (NumberFormatException e) {
                        System.err.println("Некорректное значение");
                    }

                    int finalReference = reference;
                    answer = actualFlats.stream()
                            .filter(flat -> flat.getId() == finalReference)
                            .collect(Collectors.toList());
                } else if ("Coordination X".equals(param)) {
                    double reference = 0;
                    try {
                        reference = Double.valueOf(value);
                    } catch (NumberFormatException e) {
                        System.err.println("Некорректное значение");
                    }

                    double finalReference = reference;
                    answer = actualFlats.stream()
                            .filter(flat -> flat.getX() == finalReference)
                            .collect(Collectors.toList());
                } else if ("Coordination Y".equals(param)) {
                    double reference = 0;
                    try {
                        reference = Double.valueOf(value);
                    } catch (NumberFormatException e) {
                        System.err.println("Некорректное значение");
                    }

                    double finalReference = reference;
                    answer = actualFlats.stream()
                            .filter(flat -> flat.getY() == finalReference)
                            .collect(Collectors.toList());
                } else if ("Area".equals(param)) {
                    double reference = 0;
                    try {
                        reference = Double.valueOf(value);
                    } catch (NumberFormatException e) {
                        System.err.println("Некорректное значение");
                    }

                    double finalReference = reference;
                    answer = actualFlats.stream()
                            .filter(flat -> flat.getArea() == finalReference)
                            .collect(Collectors.toList());
                } else if ("name".equals(param)) {
                    String reference = param;
                    answer = actualFlats.stream()
                            .filter(flat -> flat.getName().equals(reference))
                            .collect(Collectors.toList());
                } else if ("Number of rooms".equals(param)) {
                    int reference = 0;
                    try {
                        reference = Integer.valueOf(value);
                    }catch (NumberFormatException e) {
                        System.err.println("Некорректное значение");
                    }

                    int finalReference = reference;
                    answer = actualFlats.stream()
                            .filter(flat -> flat.getNumberOfRooms() == finalReference)
                            .collect(Collectors.toList());
                } else if ("Kitchen area".equals(param)) {
                    int reference = 0;
                    try {
                        reference = Integer.valueOf(value);
                    }catch (NumberFormatException e) {
                        System.err.println("Некорректное значение");
                    }

                    int finalReference = reference;
                    answer = actualFlats.stream()
                            .filter(flat -> flat.getKitchenArea() == finalReference)
                            .collect(Collectors.toList());
                } else if ("Time to metro on foot".equals(param)) {
                    double reference = 0;
                    try {
                        reference = Double.valueOf(value);
                    }catch (NumberFormatException e) {
                        System.err.println("Некорректное значение");
                    }

                    double finalReference = reference;
                    answer = actualFlats.stream()
                            .filter(flat -> flat.getTimeToMetroOnFoot() == finalReference)
                            .collect(Collectors.toList());
                } else if ("House name".equals(param)) {
                    String reference = value;
                    answer = actualFlats.stream()
                            .filter(flat -> flat.getHouseName().equals(reference))
                            .collect(Collectors.toList());
                } else if ("House year".equals(param)) {
                    int reference = 0;
                    try {
                        reference = Integer.valueOf(value);
                    } catch (NumberFormatException e) {
                        System.err.println("Некорректное значение");
                    }

                    int finalReference = reference;
                    answer = actualFlats.stream()
                            .filter(flat -> flat.getHouseYear() == finalReference)
                            .collect(Collectors.toList());
                } else if ("Number of flats on floor".equals(param)) {
                    int reference = 0;
                    try {
                        reference = Integer.valueOf(value);
                    } catch (NumberFormatException e) {
                        System.err.println("Некорректное значение");
                    }

                    int finalReference = reference;
                    answer = actualFlats.stream()
                            .filter(flat -> flat.getHouseNumberOfFlatsOnFloor() == finalReference)
                            .collect(Collectors.toList());
                } else if ("Author id".equals(param)) {
                    int reference = 0;
                    try {
                        reference = Integer.valueOf(value);
                    } catch (NumberFormatException e) {
                        System.err.println("Некорректное значение");
                    }

                    int finalReference = reference;
                    answer = actualFlats.stream()
                            .filter(flat -> flat.getAuthor() == finalReference)
                            .collect(Collectors.toList());
                }

                actualFlats.clear();
                flats_table.setItems(actualFlats);
                actualFlats.addAll(answer);
                flats_table.setItems(actualFlats);
                autoFitTable(flats_table);

                drawBackground();

                drawFlatsVisualObjects(actualFlats);

            }
        });

        abortFilter_button.setOnAction(event -> {
            flats_table.setItems(flats);
            drawBackground();
            drawFlatsVisualObjects(flats);
            actualFlats.clear();
            actualFlats.addAll(flats);
        });

    }

    public static Object requestToServer(Object o) throws IOException, ClassNotFoundException {
//        System.out.println("ловим сокет");
        s = new Socket(ADDR, PORT);
        out = s.getOutputStream();
        in = s.getInputStream();
//        System.out.println("пiмав сокет");
        userInterface = new UserInterface(in, out);
        //System.out.println(answer.length());


        //userInterface.send(null);
        userInterface.send(o);
//        System.out.println("отправляем " + o);
        Object response = userInterface.receive();

//        System.out.println(response);

//        in.close();
//        out.close();
//        s.close();

        return response;
    }

    public void refresh_table() throws IOException, ClassNotFoundException {
        flats.clear();
        flats_table.setItems(flats);

        Object collection = requestToServer("show");
//
        System.out.println("Server response: " + collection);
        flats.addAll((ArrayList) collection);

        actualFlats.clear();
        actualFlats.addAll(flats);

        drawBackground();

        if (flats.size() > 0) {
            id_column.setCellValueFactory(new PropertyValueFactory<Flat, Integer>("id"));
            name_column.setCellValueFactory(new PropertyValueFactory<Flat, String>("name"));
            coordX_column.setCellValueFactory(new PropertyValueFactory<Coordinates, Double>("x"));
            coordY_column.setCellValueFactory(new PropertyValueFactory<Coordinates, Double>("y"));
            creationDate_column.setCellValueFactory(new PropertyValueFactory<Flat, Date>("creationDate"));
            area_column.setCellValueFactory(new PropertyValueFactory<Flat, Double>("area"));
            numberOfRooms_column.setCellValueFactory(new PropertyValueFactory<Flat, Integer>("numberOfRooms"));
            kitchenArea_column.setCellValueFactory(new PropertyValueFactory<Flat, Integer>("kitchenArea"));
            timeToMetroOnFoot_column.setCellValueFactory(new PropertyValueFactory<Flat, Double>("timeToMetroOnFoot"));
            furnish_column.setCellValueFactory(new PropertyValueFactory<Flat, Furnish>("furnish"));
            houseName_column.setCellValueFactory(new PropertyValueFactory<House, String>("houseName"));
            houseYear_column.setCellValueFactory(new PropertyValueFactory<House, Integer>("houseYear"));
            numberOfFlatsOnFloor_column.setCellValueFactory(new PropertyValueFactory<House, Integer>("HouseNumberOfFlatsOnFloor"));
            authorId_column.setCellValueFactory(new PropertyValueFactory<Flat, Integer>("author"));

            flats_table.setItems(flats);

            for (Flat flat : flats) {
                int authorId = flat.getAuthor();
                double x = flat.getX();
                double y = flat.getY();
                double size = flat.getArea();
                Color color = null;
                try {
                    color = dbManager.getUserColor(authorId);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

                drawCircle(x, y, size, color);
            }
        }




//        context.clearRect(0, 0, visual_canvas.getWidth(), visual_canvas.getHeight());


        lastUpdateDate = new Date();
        DateFormat df = DateFormat.getDateInstance(DateFormat.FULL, currentBundle.getLocale());
        String formattedDate = df.format(lastUpdateDate);
        date_text.setText(String.valueOf(formattedDate));

        autoFitTable(flats_table);


    }

    public Coordinates get_center() {
        double verticalCenter = visual_canvas.getHeight() / 2;
        double horizontalCenter = visual_canvas.getWidth() / 2;

        return new Coordinates(horizontalCenter, verticalCenter);

    }


    public void drawCoordinateSpace() {
        GraphicsContext context = visual_canvas.getGraphicsContext2D();

        context.setFill(Color.TEAL);
        context.setStroke(Color.BLACK);
        context.setLineWidth(1);
        Coordinates center = get_center();
        context.strokeLine(0, center.getY(), visual_canvas.getWidth(), center.getY());
        context.strokeLine(center.getX(), 0, center.getX(), visual_canvas.getHeight());

    }

    public void drawCircle(double x, double y, double radius, Color color) {
        GraphicsContext context = visual_canvas.getGraphicsContext2D();
        context.setFill(color);
        context.setStroke(color);
        context.setLineWidth(1);
        Coordinates center = get_center();

        double corX = x + center.getX() - radius/2;
        double corY = center.getY() - y - radius/2;
        System.out.println(center);
        System.out.println(corX);
        System.out.println(corY);
        context.fillOval(corX,corY, radius, radius);
//        context.strokeOval(corX,corY, radius, radius);
        System.out.println("рисуем круг");
    }

    public double corrX(double x) {
        Coordinates center = get_center();
        double corrX = (center.getX() - x) * -1;
        return corrX;
    }

    public double corrY(double y) {
        Coordinates center = get_center();
        double corrY = center.getY() - y;
        return corrY;
    }

    public double getDistance(double x1, double y1, double x2, double y2) {
        double d = Math.abs(Math.sqrt((x2 - x1)*(x2 - x1) + (y2 - y1)*(y2 - y1)));
        return d;
    }

    public void drawBackground() {
//        File imgFile = new File("src/clientUI/controllers/map.jpg");
//        System.out.println(imgFile.exists());
////        java.net.URL imgURL = getResource("/map.jpg");
//        String localUrl = "";
//        try {
//            localUrl = imgFile.toURI().toURL().toString();
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
        Image map = new Image(getClass().getResourceAsStream("map.jpg"));
//        System.out.println(localUrl);
//        System.out.println(map);

        visual_canvas.resize(canvasParent_pane.getWidth(), canvasParent_pane.getHeight());

        GraphicsContext context = visual_canvas.getGraphicsContext2D();
        context.setFill(Color.TEAL);
        context.setStroke(Color.BLACK);
        context.setLineWidth(1);
        context.drawImage(map,0,0);

        drawCoordinateSpace();

    }

    public void drawFlatsVisualObjects(ObservableList<Flat> actualFlats) {
        for (Flat flat : actualFlats) {
            int authorId = flat.getAuthor();
            double x = flat.getX();
            double y = flat.getY();
            double size = flat.getArea();
            Color color = null;
            try {
                color = dbManager.getUserColor(authorId);
            } catch (SQLException | ClassNotFoundException throwables) {
                throwables.printStackTrace();
            }

            drawCircle(x, y, size, color);
        }
    }

    public void translate() {
        try {
            add_button.setText(new String(currentBundle.getString("add_button").getBytes ("ISO-8859-1"),"windows-1251"));
//            refresh_button.setText(new String(currentBundle.getString("refresh_button").getBytes ("ISO-8859-1"),"windows-1251"));
            clear_button.setText(new String(currentBundle.getString("clear_button").getBytes ("ISO-8859-1"),"windows-1251"));
            count_greater_than_house_button.setText(new String(currentBundle.getString("greater_than_house_button").getBytes ("ISO-8859-1"),"windows-1251"));
            script_button.setText(new String(currentBundle.getString("execute_script_button").getBytes ("ISO-8859-1"),"windows-1251"));
            info_button.setText(new String(currentBundle.getString("info_button").getBytes ("ISO-8859-1"),"windows-1251"));
            history_button.setText(new String(currentBundle.getString("history_button").getBytes ("ISO-8859-1"),"windows-1251"));
            filter_button.setText(new String(currentBundle.getString("filter_button").getBytes ("ISO-8859-1"),"windows-1251"));
            abortFilter_button.setText(new String(currentBundle.getString("abort_filter_button").getBytes ("ISO-8859-1"),"windows-1251"));
            color_text.setText(new String(currentBundle.getString("user_color_text").getBytes ("ISO-8859-1"),"windows-1251"));
            username_text.setText(new String(currentBundle.getString("username_text").getBytes ("ISO-8859-1"),"windows-1251"));
            filterParams_text.setText(new String(currentBundle.getString("filter_text").getBytes ("ISO-8859-1"),"windows-1251"));
            language_text.setText(new String(currentBundle.getString("language_text").getBytes ("ISO-8859-1"),"windows-1251"));
            filter_field.setPromptText(new String(currentBundle.getString("filter_prompt").getBytes ("ISO-8859-1"),"windows-1251"));
            table_tab.setText(new String(currentBundle.getString("table_tab").getBytes ("ISO-8859-1"),"windows-1251"));
            visual_tab.setText(new String(currentBundle.getString("visual_tab").getBytes ("ISO-8859-1"),"windows-1251"));
            remove_first_button.setText(new String(currentBundle.getString("remove_first_button").getBytes ("ISO-8859-1"),"windows-1251"));
            lastUpdate_text.setText(new String(currentBundle.getString("last_update_text").getBytes ("ISO-8859-1"),"windows-1251"));

            DateFormat df = DateFormat.getDateInstance(DateFormat.FULL, currentBundle.getLocale());
            String formattedDate = df.format(lastUpdateDate);
            date_text.setText(String.valueOf(formattedDate));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }



    class Updater implements Runnable {

        @Override
        public void run() {
//            try {
//                refresh_table();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            }





            System.out.println("hello");
            bgUpdate();
        }

        public void bgUpdate() {
            int i = 2;

            while (i >0) {
                ArrayList<Flat> collection = null;
                try {
//                System.out.println("ждем ответ");
                    collection = (ArrayList<Flat>) requestToServer("show");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                System.out.println("Server response: " + collection);

                flats.clear();
                flats_table.setItems(flats);

                flats.addAll(collection);

                actualFlats.clear();
                actualFlats.addAll(flats);

                drawBackground();

//            if (flats.size() > 0) {
                id_column.setCellValueFactory(new PropertyValueFactory<Flat, Integer>("id"));
                name_column.setCellValueFactory(new PropertyValueFactory<Flat, String>("name"));
                coordX_column.setCellValueFactory(new PropertyValueFactory<Coordinates, Double>("x"));
                coordY_column.setCellValueFactory(new PropertyValueFactory<Coordinates, Double>("y"));
                creationDate_column.setCellValueFactory(new PropertyValueFactory<Flat, Date>("creationDate"));
                area_column.setCellValueFactory(new PropertyValueFactory<Flat, Double>("area"));
                numberOfRooms_column.setCellValueFactory(new PropertyValueFactory<Flat, Integer>("numberOfRooms"));
                kitchenArea_column.setCellValueFactory(new PropertyValueFactory<Flat, Integer>("kitchenArea"));
                timeToMetroOnFoot_column.setCellValueFactory(new PropertyValueFactory<Flat, Double>("timeToMetroOnFoot"));
                furnish_column.setCellValueFactory(new PropertyValueFactory<Flat, Furnish>("furnish"));
                houseName_column.setCellValueFactory(new PropertyValueFactory<House, String>("houseName"));
                houseYear_column.setCellValueFactory(new PropertyValueFactory<House, Integer>("houseYear"));
                numberOfFlatsOnFloor_column.setCellValueFactory(new PropertyValueFactory<House, Integer>("HouseNumberOfFlatsOnFloor"));
                authorId_column.setCellValueFactory(new PropertyValueFactory<Flat, Integer>("author"));

                flats_table.setItems(flats);

                for (Flat flat : flats) {
                    int authorId = flat.getAuthor();
                    double x = flat.getX();
                    double y = flat.getY();
                    double size = flat.getArea();
                    Color color = null;
                    try {
                        color = dbManager.getUserColor(authorId);
                    } catch (SQLException | ClassNotFoundException throwables) {
                        throwables.printStackTrace();
                    }

                    drawCircle(x, y, size, color);
                }
//            }

                lastUpdateDate = new Date();
                DateFormat df = DateFormat.getDateInstance(DateFormat.FULL, currentBundle.getLocale());
                String formattedDate = df.format(lastUpdateDate);
                date_text.setText(String.valueOf(formattedDate));

//                autoFitTable(flats_table);

                i--;
            }


            while (true) {
//                System.out.println("hi");



                ArrayList<Flat> collection = null;
                try {
//                    System.out.println("ждем ответ");
                    collection = (ArrayList<Flat>) requestToServer("show");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
//
//                System.out.println("Server response: " + collection);
                Collections.sort(flats);
                Collections.sort(collection);
                if (hasCollectionsDiff(flats, collection)) {
                    System.out.println("\n\n\n" + "Обнаружено изменение в коллекции" + "\n\n\n");
//                    System.out.println("Отличия:");
//                    for (int i = 0; i < flats.size(); i++) {
//                        Flat flat = flats.get(i);
//                        Flat flat1 = collection.get(i);
//                            if (!flat.toString().equals(flat1.toString())) {
//                                System.out.println("разнист 1: ");
//                                System.out.println(flat);
//                                System.out.println("разнист 2: ");
//                                System.out.println(flat1);
//                            }
//
//                    }
                    flats.clear();
                    flats_table.setItems(flats);

                    flats.addAll(collection);

                    actualFlats.clear();
                    actualFlats.addAll(flats);

                    drawBackground();

//                    if (flats.size() > 0) {
                    id_column.setCellValueFactory(new PropertyValueFactory<Flat, Integer>("id"));
                    name_column.setCellValueFactory(new PropertyValueFactory<Flat, String>("name"));
                    coordX_column.setCellValueFactory(new PropertyValueFactory<Coordinates, Double>("x"));
                    coordY_column.setCellValueFactory(new PropertyValueFactory<Coordinates, Double>("y"));
                    creationDate_column.setCellValueFactory(new PropertyValueFactory<Flat, Date>("creationDate"));
                    area_column.setCellValueFactory(new PropertyValueFactory<Flat, Double>("area"));
                    numberOfRooms_column.setCellValueFactory(new PropertyValueFactory<Flat, Integer>("numberOfRooms"));
                    kitchenArea_column.setCellValueFactory(new PropertyValueFactory<Flat, Integer>("kitchenArea"));
                    timeToMetroOnFoot_column.setCellValueFactory(new PropertyValueFactory<Flat, Double>("timeToMetroOnFoot"));
                    furnish_column.setCellValueFactory(new PropertyValueFactory<Flat, Furnish>("furnish"));
                    houseName_column.setCellValueFactory(new PropertyValueFactory<House, String>("houseName"));
                    houseYear_column.setCellValueFactory(new PropertyValueFactory<House, Integer>("houseYear"));
                    numberOfFlatsOnFloor_column.setCellValueFactory(new PropertyValueFactory<House, Integer>("HouseNumberOfFlatsOnFloor"));
                    authorId_column.setCellValueFactory(new PropertyValueFactory<Flat, Integer>("author"));

                        flats_table.setItems(flats);

                        for (Flat flat : flats) {
                            int authorId = flat.getAuthor();
                            double x = flat.getX();
                            double y = flat.getY();
                            double size = flat.getArea();
                            Color color = null;
                            try {
                                color = dbManager.getUserColor(authorId);
                            } catch (SQLException | ClassNotFoundException throwables) {
                                throwables.printStackTrace();
                            }

                            drawCircle(x, y, size, color);
                        }
//                    }

                    lastUpdateDate = new Date();
                    DateFormat df = DateFormat.getDateInstance(DateFormat.FULL, currentBundle.getLocale());
                    String formattedDate = df.format(lastUpdateDate);
                    date_text.setText(String.valueOf(formattedDate));
                } else {
//                    System.out.println("Коллекция не изменилась");
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public Boolean hasCollectionsDiff(ObservableList<Flat> flats, ArrayList<Flat> collection) {
            for (int i = 0; i < flats.size(); i++) {
                Flat flat = flats.get(i);
                Flat flat1 = collection.get(i);
                if (!flat.toString().equals(flat1.toString())) {
                    System.out.println("разнист 1: ");
                    System.out.println(flat);
                    System.out.println("разнист 2: ");
                    System.out.println(flat1);
                    return true;
                }
            }
            return false;
        }
    }

}


