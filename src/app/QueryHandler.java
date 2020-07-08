package app;

import collection.CollectionManager;
import commands.Command;
import commands.CommandsManager;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;

public class QueryHandler implements Runnable{
    DBManager dbManager;
    UserInterface userInterface;
    CollectionManager collection;

    public QueryHandler(DBManager dbManager, UserInterface userInterface, CollectionManager collection) {
        this.dbManager = dbManager;
        this.userInterface = userInterface;
        this.collection = collection;
    }

    @Override
    public void run() {
        Object o = null;
        try {
            o = userInterface.receive();
        } catch (SocketException | EOFException e) {
            System.err.println("Клиент отключился (сервер продолжает работу)");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (o != null) {
//            System.out.println(o);
            try {
                Command command = (Command) o;
                //System.out.println("команда " + command);
                System.out.println(command.getUser());
                String arg[] = command.getArgs();
//                        for (int i = 0; i < arg.length; i++) {
//                            System.out.println(arg[i]);
//                        }
                //System.out.println(command.getObject());
                CommandsManager.getInstance().executeCommandWithObj(dbManager, userInterface, collection, command, arg);
            } catch (ParserConfigurationException | TransformerException | NoSuchCommandException e) {
                try {
                    userInterface.send("Неизвестная команда, используйте команду help, чтобы посмотреть список всех доступных команд.");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                e.printStackTrace();
            } catch (InvalidInputException e) {
                try {
                    userInterface.send("Некорректный ввод команды");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            } catch (IOException e) {
                System.err.println("Ошибка ввода/вывода");
                e.printStackTrace();
            } catch (ClassCastException e) {
                //System.out.println("Команда из строки");
                try {
                    CommandsManager.getInstance().executeCommand(dbManager, userInterface, collection, o.toString());
                } catch (ParserConfigurationException | TransformerException | NoSuchCommandException ex) {
                    try {
                        userInterface.send("Неизвестная команда, используйте команду help, чтобы посмотреть список всех доступных команд.");
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    e.printStackTrace();
                } catch (InvalidInputException ex) {
                    try {
                        userInterface.send("Некорректный ввод команды");
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                } catch (IOException ex) {
                    System.err.println("Ошибка ввода/вывода");
                    e.printStackTrace();
                } catch (Exception ex) {
                    System.err.println("Неизвестная ошибка команды из строки");
                    e.printStackTrace();
                }
            } catch (Exception e) {
                System.err.println("Неизвестная ошибка");
                e.printStackTrace();
            }
        }
    }
}
