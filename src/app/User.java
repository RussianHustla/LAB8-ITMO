package app;

import javafx.scene.paint.Color;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class User implements Serializable {
    private int id;
//    private Color color;
    private String login;
    private byte[] password;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", password=" + Arrays.toString(password) +
                '}';
    }

    public User(int id, String login, String password) throws NoSuchAlgorithmException {
        this.id = id;
        this.login = login;
        this.password = sha1(password);
    }

    public byte[] sha1(String str) throws NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        byte[] bytes = sha1.digest(str.getBytes());
        return bytes;
    }

    public Color parseColor(String color) {
        String[] rgbSt = color.split(",");
        double r = Double.parseDouble(rgbSt[0]);
        double g = Double.parseDouble(rgbSt[1]);
        double b = Double.parseDouble(rgbSt[2]);

        return new Color(r,g,b,1);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(String password) throws NoSuchAlgorithmException {
        this.password = sha1(password);
    }
}
