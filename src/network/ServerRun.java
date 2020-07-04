package network;

import app.*;
import collection.CollectionManager;
import commands.Command;
import commands.CommandsManager;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerRun implements Runnable{
    final int PORT = 8000;
    int clientCount = 0;

    CollectionManager collection;
    DBManager dbManager;
    //    CollectionManager collection = CollectionManager.getInstance();
//    DBManager dbManager = new DBManager();
    //System.getProperties().list(System.out);
    //System.out.println(System.getProperties());

//       if (System.getProperty("os.name").equals("Windows 10")) {
//        dbManager.getSshTunnel(8777); //прокидываем ssh при запуске вне гелиоса
//    }

    public ServerRun(DBManager dbManager,CollectionManager collection) {
        this.dbManager = dbManager;
        this.collection = collection;
    }

    public static void main(String[] args) {
        //ServerRun server = new ServerRun(new DBManager(),CollectionManager.getInstance());

       if (System.getProperty("os.name").equals("Windows 10")) {
           DBManager dbManager = new DBManager();
            dbManager.getSshTunnel(8777); //прокидываем ssh при запуске на windows 10
        }

        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(new ServerRun(new DBManager(),CollectionManager.getInstance()));
        executorService.shutdown();
    }




    private void receive(ServerSocket ss) {
        try {
            Socket s = ss.accept();
            InputStream in = s.getInputStream();
            OutputStream out = s.getOutputStream();
            UserInterface userInterface = new UserInterface(in, out);
            //DBManager dbManager = new DBManager();

            System.out.println("query accepted #" + ++clientCount);

            QueryHandler queryHandler = new QueryHandler(dbManager, userInterface, collection);
//            ExecutorService executorService = Executors.newCachedThreadPool();
//            executorService.submit(new QueryHandler(dbManager, userInterface, collection));
            //executorService.shutdown();
            queryHandler.run();


            in.close();
            out.close();
            s.close();
        } catch (SocketException | EOFException e) {
            System.err.println("Клиент отключился (сервер продолжает работу)");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Кажется что то пошло не так, но я все еще работаю!");
        }
    }

    @Override
    public void run() {
        System.out.println("Server started");
        try {
            collection.loadCollection(dbManager.getCollection());
        } catch (SQLException | ClassNotFoundException throwables) {
            System.err.println("Ошибка при загрузке коллекции");
            throwables.printStackTrace();
            System.exit(-1);
        }
        System.out.println("Коллекция успешно загруженна из базы данных");

        ServerSocket ss = null;

        {
            try {
                ss = new ServerSocket(PORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        while (true) {

            receive(ss);


        }
    }
}
