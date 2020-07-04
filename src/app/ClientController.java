package app;

import javafx.scene.paint.Color;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class ClientController {
    private int userId = -1;
    private User user = null;

    public void enter() throws IOException, SQLException, ClassNotFoundException, NoSuchAlgorithmException {
        int id;
        System.out.println("===Выполните вход в приложение===");
        String operationType;
        do {
            operationType = Reader.request("Введите вход/регистрация", false).toUpperCase();
        } while (!operationType.equals("LOGIN") && !operationType.equals("ВХОД") && !operationType.equals("SINGUP")
                && !operationType.equals("РЕГИСТРАЦИЯ") && !operationType.equals("EXIT"));
        if (operationType.equals("EXIT")) {
            System.out.println("Выход из программы");
            System.exit(0);
        } else if (operationType.equals("LOGIN") || operationType.equals("ВХОД")) {
            login();
            //System.out.println("Выполнен вход пользователя " + getUserId());
        } else {
            singUp();
            //System.out.println("Зарегистрирован пользователь " + getUserId());
        }
        //return id;
    }

    public void login() throws IOException, SQLException, ClassNotFoundException, NoSuchAlgorithmException {
        DBManager dbManager = new DBManager();
        System.out.println("===Авторизация пользователя===");
        String login = Reader.request("Введите логин:", false);
        String password = Reader.request("Введите пароль:", false);
        int id = dbManager.logInUser(login,password);
        Color color = dbManager.getUserColor(id);
        if (id > 0) {
            setUserId(id);
            System.out.println("Выполнен вход в аккаунт пользователя с id = " + getUserId());
            user = new User(getUserId(),login,password);
            //System.out.println(user);
        } else {
            System.err.println("Вход не выполнен, проверте корректность данных для входа и повторите попытку, либо зарегистрируйтесь");
            enter();
        }

        //return id;
    }

    public void singUp() throws IOException, SQLException, ClassNotFoundException, NoSuchAlgorithmException {
        int id = -1;
        DBManager dbManager = new DBManager();
        System.out.println("===Регистрация нового пользователя===");
        String login = Reader.request("Придумайте логин: ", false);
        String password = Reader.request("Придумайте пароль: ", false);
        id = dbManager.signUpUser(login,password);
        if (id == -2) {
            System.err.println("Пользователь с таким логином уже существует");
            singUp();
        } else {
            setUserId(id);
            Color color = dbManager.getUserColor(id);
            System.out.println("Вы успешно зарегистрированы, получен id = " + getUserId());
            user = new User(getUserId(),login,password);
            //System.out.println(user);
        }
    }

    public void logOut() {
        setUserId(-1);
        user = null;
        System.gc();
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
