package com.example.calendrier;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent initialView = FXMLLoader.load(getClass().getResource("Semaine-view.fxml"));
        primaryStage.setTitle("Calendrier");
        primaryStage.setScene(new Scene(initialView, 600, 400));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
