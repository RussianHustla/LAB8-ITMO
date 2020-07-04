package app;

import collection.Coordinates;
import collection.Flat;
import collection.Furnish;
import collection.House;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import javafx.scene.paint.Color;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.*;
import java.util.Date;


public class DBManager {
    Connection dbConnection;
    final static String USER = "s284701";
    final static String PASS = "dzk924";

    public void getSshTunnel(int listenerPort) {
        String host = "se.ifmo.ru";
        int port = 2222;

//        String user = "s284698";
//        String pass = "rpp013";

        String listenerHost = "127.0.0.1";
        //int listenerPort = 8777;

        String listeningHost = "pg";
        int listeningPort = 5432;

        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(USER, host, port);
            session.setPassword(PASS);
            session.setConfig("StrictHostKeyChecking", "no");

//			System.out.println("Establishing Connection...");
            session.connect();

            session.setPortForwardingL(listenerPort, listeningHost, listeningPort);
//			System.out.println(listenerHost + ":" + listenerPort + " -> " + listeningHost + ":" + listeningPort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getUrl() {
        String host = "se.ifmo.ru";
        int port = 2222;

        String listenerHost = "127.0.0.1";
        int listenerPort = 8777;

        String listeningHost = "pg";
        int listeningPort = 5432;

        if (System.getProperty("os.name").equals("Windows 10")) {
            return "jdbc:postgresql://" + listenerHost + ":" + listenerPort + "/studs";
        } else return "jdbc:postgresql://" + listeningHost + ":" + listeningPort + "/studs";

        //return "jdbc:postgresql://" + listenerHost + ":" + listenerPort + "/studs";
    }

    public Connection getDbConnection() throws ClassNotFoundException, SQLException {
        String connectionString = getUrl();
        Class.forName("org.postgresql.Driver");
        dbConnection = DriverManager.getConnection(connectionString, USER, PASS);
        return dbConnection;
    }

    public byte[] sha1(String str) throws NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        byte[] bytes = sha1.digest(str.getBytes());
        return bytes;
    }

    public String generateColor() {

        Random rand = new Random();
        double r = rand.nextFloat();
        Random randg = new Random();
        double g = randg.nextFloat();
        Random randb = new Random();
        double b = randb.nextFloat();
//        double r = Math.random() / 2f + 0.5;
//        double g = Math.random() / 2f + 0.5;
//        double b = Math.random() / 2f + 0.5;
        String color = new String(String.valueOf(r) + "," + String.valueOf(g) + "," + String.valueOf(b));
        return color;
    }

    public int signUpUser(String login, String password) throws SQLException, ClassNotFoundException, NoSuchAlgorithmException {
        if (isLoginFree(login)) {
            byte[] hash = sha1(password);
            String color = generateColor();
            String insert = "INSERT INTO users (login,password,color) VALUES(?,?,?)";
            PreparedStatement prSt = getDbConnection().prepareStatement(insert);
            prSt.setString(1, login);
            prSt.setBytes(2, hash);
            prSt.setString(3, color);
            prSt.executeUpdate();
            int id = logInUser(login,password);
            return id;
        } else return -2; //пользователь с таким логином уже существует
    }

    public int logInUser(String login, String password) throws SQLException, ClassNotFoundException, NoSuchAlgorithmException {
        int id = -1;
        byte[] hash = sha1(password);
        String select = "SELECT id FROM users WHERE login=? AND password=?";
        PreparedStatement prSt = getDbConnection().prepareStatement(select);
        prSt.setString(1, login);
        prSt.setBytes(2, hash);
        ResultSet resultSet = prSt.executeQuery();
        while (resultSet.next()) {
            id = resultSet.getInt("id");
        }
        return id;
    }

    public Color parseColor(String color) {
        System.out.println(color);
        String[] rgbSt = color.split(",");
        double r = Double.parseDouble(rgbSt[0]);
        double g = Double.parseDouble(rgbSt[1]);
        double b = Double.parseDouble(rgbSt[2]);

        return new Color(r,g,b,1);
    }

    public Color getUserColor(int id) throws SQLException, ClassNotFoundException {
        String color = null;
        String select = "SELECT color FROM users WHERE id=?";
        PreparedStatement prSt = getDbConnection().prepareStatement(select);
        prSt.setInt(1, id);
        ResultSet resultSet = prSt.executeQuery();
        while (resultSet.next()) {
            color = resultSet.getString("color");
        }
        System.out.println(color);
        return parseColor(color);
    }

    public boolean isLoginFree(String login) throws SQLException, ClassNotFoundException {
        int id = -1;
        String select = "SELECT id FROM users WHERE login=?";
        PreparedStatement prSt = getDbConnection().prepareStatement(select);
        prSt.setString(1, login);
        ResultSet resultSet = prSt.executeQuery();
        while (resultSet.next()) {
            id = resultSet.getInt("id");
        }
        if (id == -1) return true;
        else return false;
    }

    public synchronized int addFlat(Flat flat, User user) throws SQLException, ClassNotFoundException {
        int id = -1;
        String insert = "INSERT INTO flats (name,coordx,coordy,creationdate,area,numberofrooms,kitchenarea,timetometroonfoot,furnish,housename,year,numberofflatsonfloor,author) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
        //String insert = "INSERT INTO flats (name,coordx,coordy) VALUES(?,?,?) RETURNING id";
        PreparedStatement prSt = getDbConnection().prepareStatement(insert,Statement.RETURN_GENERATED_KEYS);
        prSt.setString(1, flat.getName());
        prSt.setDouble(2, flat.getCoordinates().getX());
        prSt.setDouble(3, flat.getCoordinates().getY());
        Timestamp ts = new Timestamp(flat.getCreationDate().getTime());
        prSt.setTimestamp(4, ts);
        prSt.setDouble(5, flat.getArea());
        prSt.setInt(6, flat.getNumberOfRooms());
        prSt.setInt(7, flat.getKitchenArea());
        prSt.setDouble(8, flat.getTimeToMetroOnFoot());
        prSt.setString(9, flat.getFurnish().toString());
        prSt.setString(10, flat.getHouseName());
        prSt.setInt(11, flat.getHouseYear());
        prSt.setInt(12, flat.getHouseNumberOfFlatsOnFloor());
        prSt.setInt(13, user.getId());
        prSt.executeUpdate();

        ResultSet resultSet = prSt.getGeneratedKeys();
        while (resultSet.next()) {
            id = resultSet.getInt("id");
        }
        return id;

    }

    public void getFlat() {

    }

    public synchronized ArrayList<Flat> getCollection() throws SQLException, ClassNotFoundException {
        ArrayList<Flat> flats = new ArrayList<>();
        String select = "SELECT * FROM flats";
        Statement statement = getDbConnection().createStatement();
        ResultSet resultSet = statement.executeQuery(select);
        while (resultSet.next()) {
            Timestamp ts = resultSet.getTimestamp("creationdate");
            Date creationdate = new Date(ts.getTime());

            Coordinates coordinates = new Coordinates(resultSet.getDouble("coordx"),
                    resultSet.getDouble("coordy"));

            House house = new House(resultSet.getString("housename"),
                    resultSet.getInt("year"),
                    resultSet.getInt("numberofflatsonfloor"));

            flats.add(new Flat(resultSet.getInt("id"),
                    resultSet.getString("name"),
                    coordinates,
                    creationdate,
                    resultSet.getDouble("area"),
                    resultSet.getInt("numberofrooms"),
                    resultSet.getInt("kitchenarea"),
                    resultSet.getDouble("timetometroonfoot"),
                    Furnish.valueOf(resultSet.getString("furnish")),
                    house,
                    resultSet.getInt("author")));
        }
        return flats;
    }

    public synchronized void deleteByAuthor(int id) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM flats WHERE author =?";
        PreparedStatement prSt = getDbConnection().prepareStatement(sql);
        prSt.setInt(1, id);
        prSt.executeUpdate();
    }

    public synchronized void remove_by_id(int id) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM flats WHERE id =?";
        PreparedStatement prSt = getDbConnection().prepareStatement(sql);
        prSt.setInt(1, id);
        prSt.executeUpdate();
    }

    public synchronized void updateById(int id, Flat flat, User user) throws SQLException, ClassNotFoundException {
        String update = "UPDATE flats SET name = ?,coordx = ?,coordy = ?,creationdate = ?,area = ?,numberofrooms = ?,kitchenarea = ?,timetometroonfoot = ?,furnish = ?,housename = ?,year = ?,numberofflatsonfloor = ?,author = ? WHERE id=" + id;
        //String insert = "INSERT INTO flats (name,coordx,coordy) VALUES(?,?,?) RETURNING id";
        PreparedStatement prSt = getDbConnection().prepareStatement(update);
        prSt.setString(1, flat.getName());
        prSt.setDouble(2, flat.getCoordinates().getX());
        prSt.setDouble(3, flat.getCoordinates().getY());
        Timestamp ts = new Timestamp(flat.getCreationDate().getTime());
        prSt.setTimestamp(4, ts);
        prSt.setDouble(5, flat.getArea());
        prSt.setInt(6, flat.getNumberOfRooms());
        prSt.setInt(7, flat.getKitchenArea());
        prSt.setDouble(8, flat.getTimeToMetroOnFoot());
        prSt.setString(9, flat.getFurnish().toString());
        prSt.setString(10, flat.getHouseName());
        prSt.setInt(11, flat.getHouseYear());
        prSt.setInt(12, flat.getHouseNumberOfFlatsOnFloor());
        prSt.setInt(13, user.getId());
        prSt.executeUpdate();
    }

}
