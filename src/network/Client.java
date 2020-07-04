package network;

import app.*;
import app.Reader;
import collection.CollectionManager;
import collection.Coordinates;
import collection.Flat;
import collection.House;
import commands.Command;
import commands.CommandsManager;

import javax.jws.soap.SOAPBinding;
import javax.security.auth.callback.TextInputCallback;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class Client {
    public final static int PORT = 8000;
    public final static String ADDR = "localhost";

    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException, NoSuchAlgorithmException {
//        final int PORT = 8000;
//        final String ADDR = "localhost";
        //int userId = 0;
        ClientController clientController = new ClientController();
        DBManager dbManager = new DBManager();
//        try {
//            clientController.enter();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }

        String b = "Иисус пожалуйста помоги!!";



        while (true) {
            String answer = "";
//            try {
//                answer = Reader.request();
                answer = args[0];
//            } catch (IOException e) {
//                System.err.println("Ошибка ввода/вывода");
//                e.printStackTrace();
//            }
            try {
            Socket s = new Socket(ADDR, PORT);
            OutputStream out = s.getOutputStream();
            InputStream in = s.getInputStream();
            UserInterface userInterface = new UserInterface(in, out);
            //System.out.println(answer.length());

                if (answer != null){

                    if (answer.length() > 0) {
                        if (clientController.getUser() != null) {

                            String[] parsedCommand = answer.split(" ");
                            if (CommandsManager.getInstance().contains(parsedCommand[0])) {
                                Command command = CommandsManager.getInstance().getCommand(parsedCommand[0]);
                                if (command.getCommand().equals("execute_script")) {
                                    String[] argss = {parsedCommand[1]};
                                   new Script_handler().execute_script(true,clientController,dbManager,userInterface,argss);
                                } else {
                                    preparedSendCommand(answer, clientController, userInterface);
                                }
                            } else {
                                System.err.println("Неизвестная команда, используйте команду help, чтобы посмотреть список всех доступных команд.");
                            }


                        } else {
                            clientController.enter();
                            //System.out.println(clientController.getUserId());
                        }
                    }

                }
                userInterface.send(null);

                in.close();
                out.close();
                s.close();
            } catch (ConnectException e) {
                System.err.println("Сервер недоступен : (");
//                System.out.println("Выйти из программы? (y/n)");
//                String conf = Reader.request();
//                if (conf.equals("y")) {
//
//                }

            } catch (SQLException e) {
                e.printStackTrace();
            } catch (TransformerException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
            String exit = "exit";
            if (exit.equals(answer)) {
                System.out.println("Выход из программы");
                System.exit(0);
            }
        }

    }

    public static void preparedSendCommand(String answer, ClientController clientController, UserInterface userInterface) throws IOException, NoSuchAlgorithmException, SQLException, ClassNotFoundException {
        String[] parsedCommand = answer.split(" ");
        if (CommandsManager.getInstance().contains(parsedCommand[0])) {
            Command command = CommandsManager.getInstance().getCommand(parsedCommand[0]);
            if (command.getCommand().equals("add") || command.getCommand().equals("add_if_min")) {
                //System.out.println("это add! или add_if_min?");
                String arg[] = new String[]{"-", "-"};
                command.setArgs(arg); //заглушка от NPE
                Flat flat = Reader.requestForFlat();
                command.setObject(flat);
                command.setUser(clientController.getUser());
                userInterface.send(command);
            } else if (command.getCommand().equals("update")) {
                String arg[] = new String[1];
                arg[0] = parsedCommand[1];
                command.setArgs(arg);
                //System.out.println("это update!");
                Flat flat = Reader.requestForFlat();
                command.setObject(flat);
                command.setUser(clientController.getUser());
                userInterface.send(command);

            } else if (command.getCommand().equals("count_greater_than_house")) {
                //System.out.println("это count_greater_than_house");
                String arg[] = new String[]{"-", "-"};
                command.setArgs(arg); //заглушка от NPE
                House house = Reader.requestForHouse();
                command.setObject(house);
                userInterface.send(command);
            } else if (command.getCommand().equals("execute_script")) {
                //System.out.println("это xecute_script");
                userInterface.send(answer);
                boolean EOF = false;
//                            do {
//                                System.out.println(userInterface.receive());
//                            } while (!userInterface.receive().equals("EOF"));
                while (EOF == false) {
                    Object o = userInterface.receive();
                    System.out.println(o);
                    if (o.equals("EOF")) EOF = true;
                }
            } else if (command.getCommand().equals("singup")) {
                System.out.println("===Регистрация нового пользователя===");
                String login = Reader.request("Придумайте логин: ", false);
                String password = Reader.request("Придумайте пароль: ", false);
                String[] singUpData = new String[]{login, password};
                command.setArgs(singUpData);
                //userInterface.send(answer + " " + login + " " + password);
                userInterface.send(command);
            } else if (command.getCommand().equals("exit")) {
                System.out.println("Выход из программы");
                System.exit(0);
            }  else if (command.getCommand().equals("logout")) {
                System.out.println("Выход из аккаунта");
                clientController.logOut();
                clientController.enter();
                //System.out.println(clientController.getUser());
            } else if (command.getCommand().equals("clear")) {
                String arg[] = new String[]{"-", "-"};
                command.setArgs(arg); //заглушка от NPE
                command.setUser(clientController.getUser());
                userInterface.send(command);
            } else if (command.getCommand().equals("remove_by_id")) {
                String arg[] = new String[]{parsedCommand[1]};
                command.setArgs(arg);
                command.setUser(clientController.getUser());
                userInterface.send(command);
            } else if (command.getCommand().equals("remove_first")) {
                String arg[] = new String[]{"-", "-"};
                command.setArgs(arg); //заглушка от NPE
                command.setUser(clientController.getUser());
                userInterface.send(command);
            }else {
                //System.out.println("обычная команда");
                userInterface.send(answer);
                //System.out.println(answer);
            }
            System.out.println(userInterface.receive());
        } else {
            System.err.println("Неизвестная команда, используйте команду help, чтобы посмотреть список всех доступных команд.");
        }
    }


}
