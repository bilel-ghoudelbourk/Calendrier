package com.example.calendrier;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DialogPane;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import java.util.Optional;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;



import java.util.List;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private List<User> users;
    private static User currentUser;

    public static User getCurrentUser() {
        return currentUser;
    }


    public void initialize() {
        users = UserManager.loadUsersFromFile("src/main/resources/com/example/calendrier/users.json");

    }
    @FXML
    protected void handleLoginAction(ActionEvent event) {

        String username = usernameField.getText();
        String password = passwordField.getText();
        Optional<User> matchingUser = users.stream()
                .filter(user -> user.getUsername().equals(username) && user.getPassword().equals(password))
                .findFirst();

        if (matchingUser.isPresent()) {
            currentUser = matchingUser.get();
            Preferences preferences = currentUser.getPreferences();
            String cssFile=null;
            String viewToLoad = "jour-view.fxml";

            if(preferences != null) {
                switch(preferences.getTypeAffichage()) {
                    case "Par Jour":
                        viewToLoad = "Jour-view.fxml";
                        break;
                    case "Par Semaine":
                        viewToLoad = "Semaine-view.fxml";
                        break;
                    case "Par Mois":
                        viewToLoad = "Mois-view.fxml";
                        break;
                }
                switch (preferences.getTheme()) {
                    case "Mode Clair":
                        cssFile = "style_light.css";
                        break;
                    case "Mode Sombre":
                        cssFile = "style_dark.css";
                        break;
                    case "Bonus 1":
                        cssFile = "style_bonus1.css";
                        break;
                    case "Bonus 2":
                        cssFile = "style_bonus2.css";
                        break;
                    case "Bonus 3":
                        cssFile = "style_bonus3.css";
                        break;
                }
            }



            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(viewToLoad));
                Parent root = loader.load();

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                scene.getStylesheets().clear();

                if (cssFile != null) {
                    String cssPath = getClass().getResource("/com/example/calendrier/" + cssFile).toExternalForm();
                    scene.getStylesheets().add(cssPath);
                }

                stage.setScene(scene);


                stage.setMaximized(true);
                stage.setResizable(true);

                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de connexion");
            alert.setHeaderText(null);
            alert.setContentText("Nom d'utilisateur ou mot de passe incorrect.");


            DialogPane dialogPane = alert.getDialogPane();
            String css = this.usernameField.getScene().getStylesheets().get(0);
            dialogPane.getStylesheets().add(css);
            dialogPane.getStyleClass().add("myDialog");

            alert.showAndWait();
        }
    }

    @FXML
    protected void onKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (passwordField.isFocused()) {
                String username = usernameField.getText();
                String password = passwordField.getText();
                Optional<User> matchingUser = users.stream()
                        .filter(user -> user.getUsername().equals(username) && user.getPassword().equals(password))
                        .findFirst();

                if (matchingUser.isPresent()) {
                    currentUser = matchingUser.get();
                    Preferences preferences = currentUser.getPreferences();
                    String cssFile=null;
                    String viewToLoad = "jour-view.fxml";

                    if(preferences != null) {
                        switch(preferences.getTypeAffichage()) {
                            case "Par Jour":
                                viewToLoad = "Jour-view.fxml";
                                break;
                            case "Par Semaine":
                                viewToLoad = "Semaine-view.fxml";
                                break;
                            case "Par Mois":
                                viewToLoad = "Mois-view.fxml";
                                break;
                        }
                        switch (preferences.getTheme()) {
                            case "Mode Clair":
                                cssFile = "style_light.css";
                                break;
                            case "Mode Sombre":
                                cssFile = "style_dark.css";
                                break;
                            case "Bonus 1":
                                cssFile = "style_bonus1.css";
                                break;
                            case "Bonus 2":
                                cssFile = "style_bonus2.css";
                                break;
                            case "Bonus 3":
                                cssFile = "style_bonus3.css";
                                break;
                        }
                    }



                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(viewToLoad));
                        Parent root = loader.load();

                        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        Scene scene = new Scene(root);
                        scene.getStylesheets().clear();

                        if (cssFile != null) {
                            String cssPath = getClass().getResource("/com/example/calendrier/" + cssFile).toExternalForm();
                            scene.getStylesheets().add(cssPath);
                        }

                        stage.setScene(scene);


                        stage.setMaximized(true);
                        stage.setResizable(true);

                        stage.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur de connexion");
                    alert.setHeaderText(null);
                    alert.setContentText("Nom d'utilisateur ou mot de passe incorrect.");


                    DialogPane dialogPane = alert.getDialogPane();
                    String css = this.usernameField.getScene().getStylesheets().get(0);
                    dialogPane.getStylesheets().add(css);
                    dialogPane.getStyleClass().add("myDialog");

                    alert.showAndWait();
                }
            }
        }
    }

}
