package com.example.calendrier;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

public class HeaderController {

    private static final String EVENTS_URL = "https://edt-api.univ-avignon.fr/api/exportAgenda/tdoption/def5020057410cf5c853a924037ce2629db4bc0fe3bd0382e5a07e7e433edd7c7b6a120b7b2c13079afe5493fb79969be2d4786ffcca7abeca404df672aa7001c3e5f252d5b94a390dd452cb7a356d3d6b9682f6c1c3ee5b";

    public static String getEventsUrl() {
        return EVENTS_URL;
    }

    @FXML
    private ComboBox<String> viewComboBox;

    @FXML
    private ComboBox<String> themeComboBox;

    @FXML
    private void initialize() {
        viewComboBox.getItems().addAll("Type d'affichage", "Par Jour", "Par Semaine", "Par Mois");
        viewComboBox.getSelectionModel().select("Type d'affichage");

        themeComboBox.getItems().addAll("Thème","Mode Clair", "Mode Sombre");
        themeComboBox.getSelectionModel().select("Thème");

        viewComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                try {
                    changeView(newValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        themeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                try {
                    changeTheme(newValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void changeView(String viewName) throws Exception {
        Stage stage = (Stage) viewComboBox.getScene().getWindow();
        Parent view = null;

        switch (viewName) {
            case "Par Jour":
                view = FXMLLoader.load(getClass().getResource("Jour-view.fxml"));
                break;
            case "Par Semaine":
                view = FXMLLoader.load(getClass().getResource("Semaine-view.fxml"));
                break;
            case "Par Mois" :
                view = FXMLLoader.load(getClass().getResource("Mois-view.fxml"));
                break;
        }

        if (view != null) {
            Scene currentScene = viewComboBox.getScene();
            Scene newScene = new Scene(view, currentScene.getWidth(), currentScene.getHeight());
            newScene.getStylesheets().setAll(currentScene.getStylesheets());
            stage.setScene(newScene);
            stage.show();
        }
    }


    private void changeTheme(String theme) throws Exception {
        Scene scene = viewComboBox.getScene();

        String cssFile = null;
        switch (theme) {
            case "Mode Clair":
                cssFile = "style_light.css";
                break;
            case "Mode Sombre":
                cssFile = "style_dark.css";
                break;
            case "Thème":
                return;
        }

        if (cssFile != null) {
            scene.getStylesheets().clear();
            String cssPath = getClass().getResource("/com/example/calendrier/" + cssFile).toExternalForm();
            scene.getStylesheets().add(cssPath);
        }
    }


}

