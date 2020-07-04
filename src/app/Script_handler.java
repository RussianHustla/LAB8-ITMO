package app;

import clientUI.controllers.ExecuteScriptController;
import collection.CollectionManager;
import commands.Command;
import commands.CommandsManager;
import network.Client;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;

public class Script_handler {
    private HashSet<Path> scriptsHistory = new HashSet<>();


    public void execute_script(boolean first, ClientController clientController, DBManager dbManager, UserInterface userInterface, Object[] args) throws IOException, ParserConfigurationException, TransformerException, ClassNotFoundException, SQLException, NoSuchAlgorithmException {
        if (args.length < 1) {
            throw new InvalidInputException("Need argument");
        }
        String scriptPath = (String) args[0];
        Path pathToScript = Paths.get(scriptPath);
        //scriptsHistory.add(pathToScript);
        System.out.println("Выполнение скрипта из файла " + pathToScript.getFileName());
        //userInterface.send("Выполнение скрипта из файла " + pathToScript.getFileName());
        try {
            BufferedReader reader = new BufferedReader(new FileReader(pathToScript.toFile()));
            String line = reader.readLine();
            while (line != null) {
                System.out.println("Считана строка " + line);
                String[] listScriptPath = line.split(" ");
                if (listScriptPath[0].equals("execute_script")) {
                    if (!scriptsHistory.contains(pathToScript)) {
                        scriptsHistory.add(pathToScript);
                        executeCommand(clientController, dbManager, userInterface, line);
                    } else {
                        System.err.println("Обнаружена попытка вызова скрипта, который уже был вызван ранее.");
                        //userInterface.send("Обнаружена попытка вызова скрипта, который уже был вызван ранее.");
                    }
                } else {
                    //executeCommand(dbManager, userInterface, collection, line);
                    if (!first) {
                        Socket s = new Socket(Client.ADDR, Client.PORT);
                        OutputStream out = s.getOutputStream();
                        InputStream in = s.getInputStream();
                        //UserInterface userInterface1 = new UserInterface(in, out);

//                        Client.preparedSendCommand(line,clientController,new UserInterface(in, out));
                        ExecuteScriptController.preparedSendCommand(line,clientController,new UserInterface(in, out));

                        in.close();
                        out.close();
                        s.close();
                    } else {
//                        Client.preparedSendCommand(line,clientController,userInterface);
                        ExecuteScriptController.preparedSendCommand(line,clientController,userInterface);
                        first = false;
                    }


                }
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            System.err.println("Файл не найден");
            //userInterface.send("Файл не найден");
        } catch (Exception e) {
            System.err.println("Ошибка во время выполнения скрипта.");
            //userInterface.send("Ошибка во время выполнения скрипта.");
            throw e;
        }
        scriptsHistory.removeAll(scriptsHistory);
        System.out.println("Считывание скрипта из файла " + pathToScript + " завершено");
        //userInterface.send("EOF");
        //userInterface.send("Считывание скрипта из файла " + pathToScript + " завершено");
    }

    public void executeCommand(ClientController clientController, DBManager dbManager, UserInterface userInterface, String s) throws IOException, ParserConfigurationException, TransformerException, ClassNotFoundException, SQLException, NoSuchAlgorithmException {
        String[] parsedCommand = CommandsManager.getInstance().parseCommand(s);
        Command command = CommandsManager.getInstance().getCommand(parsedCommand[0]);
        String[] args = Arrays.copyOfRange(parsedCommand, 1, parsedCommand.length);
        //history.add(command);
        execute_script(false, clientController, dbManager, userInterface, args);
    }
}
