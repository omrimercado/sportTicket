package org.sporticket.finalgui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/sporticket/finalgui/hello-view.fxml"));
        Parent root = loader.load();

        stage.setScene(new Scene(root, 900, 800));
        stage.setResizable(true);
        stage.setTitle("Sport Ticket System");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
