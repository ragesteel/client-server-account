package ru.javatalks.fundamentals.account.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("AccountService Client");
        Scene myScene = new Scene((Parent) FXMLLoader.load(getClass().getResource("/client.fxml")));
        stage.setScene(myScene);
        stage.show();
    }
}
