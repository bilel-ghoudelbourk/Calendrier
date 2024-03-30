package com.example.calendrier;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

public class HeaderController {

    @FXML
    private ComboBox<String> viewComboBox;


    @FXML
    private void initialize() {
        viewComboBox.getItems().addAll("Type d'affichage", "Par Jour", "Par Semaine" ,"Par Mois");
        viewComboBox.getSelectionModel().select("Type d'affichage");

        viewComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {

                viewComboBox.getSelectionModel().select(oldValue);
            } else {
                try {
                    changeView(newValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void changeView(String viewName) throws Exception {
        Stage stage = (Stage) viewComboBox.getScene().getWindow();
        Parent view = null;

        double width = stage.getWidth();
        double height = stage.getHeight();

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
            Scene scene = new Scene(view);
            stage.setScene(scene);
            stage.setWidth(width);
            stage.setHeight(height);

            stage.show();
        }
    }

}
