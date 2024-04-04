package com.example.calendrier;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent initialView = FXMLLoader.load(getClass().getResource("/com/example/calendrier/login.fxml"));
        primaryStage.setTitle("Calendrier");

        Scene scene = new Scene(initialView, 400, 600);

        String cssPath = getClass().getResource("/com/example/calendrier/style_dark.css").toExternalForm();
        scene.getStylesheets().add(cssPath);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
