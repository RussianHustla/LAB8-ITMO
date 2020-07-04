package clientUI.controllers;

import javafx.stage.WindowEvent;


public class MainViewController {

    //Тут описание всяких контроллов

    private javafx.event.EventHandler<WindowEvent> closeEventHandler = new javafx.event.EventHandler<WindowEvent>() {
        @Override
        public void handle(WindowEvent event) {
            System.out.println("Завершение");
            System.exit(0);
        }
    };

    public javafx.event.EventHandler<WindowEvent> getCloseEventHandler(){
        return closeEventHandler;
    }

}