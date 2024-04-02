package com.example.calendrier;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.Modality;

import java.util.List;


public class HeaderController {

    private static String EVENTS_URL = null;

    public static String getEventsUrl() {
        return EVENTS_URL;
    }
    private static Object currentViewController;

    public static void setCurrentViewController(Object viewController) {
        currentViewController = viewController;
    }

    public static Object getCurrentViewController() {
        return currentViewController;
    }



    private static List<Event> filteredEventsGlobal = null; 
    public static void updateFilteredEventsGlobal(List<Event> filteredEvents) {
        filteredEventsGlobal = filteredEvents; 
    }

    public static List<Event> getFilteredEventsGlobal() {
        return filteredEventsGlobal; 
    }



    @FXML
    private ComboBox<String> viewComboBox;

    @FXML
    private ComboBox<String> themeComboBox;

    @FXML
    private ComboBox<String> formationComboBox;

    @FXML
    private ComboBox<String> salleComboBox;
    @FXML
    private ComboBox<String> enseignantComboBox;

    @FXML
    private void initialize() {
        viewComboBox.getItems().addAll("Type d'affichage", "Par Jour", "Par Semaine", "Par Mois");
        viewComboBox.getSelectionModel().select("Type d'affichage");

        themeComboBox.getItems().addAll("Thème","Mode Clair", "Mode Sombre","Bonus 1","Bonus 2","Bonus 3");
        themeComboBox.getSelectionModel().select("Thème");

        formationComboBox.getItems().addAll("Formation", "M1-IA-IL-Cla","M1-IA-IL-Alt", "M1-ILSEN-Cla","M1-ILSEN-Alt","M1-SICOM-Cla","M1-SICOM-Alt", "autres-...");
        formationComboBox.getSelectionModel().select("Formation");

        salleComboBox.getItems().addAll("Salle", "Amphi Ada","Amphi Blaise","S5 = C 024","Stat 5 = Info - C 130", "autres-...");
        salleComboBox.getSelectionModel().select("Salle");

        enseignantComboBox.getItems().addAll("Enseignant", "Cecillon Noe", "autres-...");
        enseignantComboBox.getSelectionModel().select("Enseignant");

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

        formationComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                try {
                    changeFormation(newValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        enseignantComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                try {
                    changeEnseignant(newValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        salleComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                try {
                    changeSalle(newValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private void changeFormation(String formation){
        filteredEventsGlobal=null;
        EVENTS_URL = null;
        switch (formation){
            case "M1-IA-IL-Cla":
                EVENTS_URL = "https://edt-api.univ-avignon.fr/api/exportAgenda/tdoption/def5020057410cf5c853a924037ce2629db4bc0fe3bd0382e5a07e7e433edd7c7b6a120b7b2c13079afe5493fb79969be2d4786ffcca7abeca404df672aa7001c3e5f252d5b94a390dd452cb7a356d3d6b9682f6c1c3ee5b";
                break;
            case "M1-IA-IL-Alt":
                EVENTS_URL ="https://edt-api.univ-avignon.fr/api/exportAgenda/tdoption/def50200a6feb5384bf725a47174fccbee30c1ba4efacc8b80a24085d50e033a93dc239b8f518cc0302da065e2d2272d41cc8c0cc3b00784856916234e5f3bf0cd4ec797bc54a7d116a5b759ca88e504796207c01fbccbae";
                break;
            case "M1-ILSEN-Cla":
                EVENTS_URL = "https://edt-api.univ-avignon.fr/api/exportAgenda/tdoption/def502005749f7269207305e322cdacb4b948a369c1015ff1cf7f2694740b0c8758e92e8f4d58bed9a7f7fcc126e2ac46df1ac30193d2a425e53cd518f0ee9ea1ce0c1cb7a314270474fd727ab2d9b66d284fb57b4ef5571";
                break;
            case "M1-ILSEN-Alt":
                EVENTS_URL ="https://edt-api.univ-avignon.fr/api/exportAgenda/tdoption/def50200bfbf7405961f26baa8fa1f6ac8d0251b3731fab26da699ae517206a1ea7980a8f08c1663bf68a7a10477e06f1c3eb9b19c713223a18c5c6fe497f5ee06a13bb78101d6c14f751328cb8e47078cabae562778b41c";
                break;
            case "M1-SICOM-Cla":
                EVENTS_URL ="https://edt-api.univ-avignon.fr/api/exportAgenda/tdoption/def502009b6d9e1f1452712d0f8c90bdabf2bf212e6c86ea1646d6e8440dc7ecc036993e20fd203589b2c3d2bdc6b5829695f3103f08a0f2664201235c314ddee0367c72e934986502b1475c9f7ce14ab58b5292a1ede780";
                break;
            case "M1-SICOM-Alt":
                EVENTS_URL ="https://edt-api.univ-avignon.fr/api/exportAgenda/tdoption/def50200a1bc27602f662bb59b19f83737bba4db65d5ce5ec46e1b3e0ee7f2ffdafa16564b15cafad214d7ebe8468d1a915429c9a37bdc803578092262648e8990b7724588e535426e41546161c61acc22750faa359b0c71";
                break;
        }

        if (EVENTS_URL != null) {
            Object viewController = getCurrentViewController();
            if (viewController instanceof MoisController) {
                ((MoisController) viewController).refreshEvents();
            } else if (viewController instanceof SemaineController) {
                ((SemaineController) viewController).refreshEvents();
            } else if (viewController instanceof JourController) {
                ((JourController) viewController).refreshEvents();
            }
        }

    }

    private void changeEnseignant(String enseignant){
        filteredEventsGlobal=null;
        EVENTS_URL = null;
        switch (enseignant){
            case "Cecillon Noe":
                EVENTS_URL = "https://edt-api.univ-avignon.fr/api/exportAgenda/enseignant/def5020014cf744f63f7181931e243c5139c5d8427de488f3da5b30b52905edfe9de85e8da750e291f852c095f6fd05f93658cbbf3260bf1308a84c444accdb9ab8f67de5f5758e0b59200e3c78068a677fc5055644c4635";
                break;
        }

        if (EVENTS_URL != null) {
            Object viewController = getCurrentViewController();
            if (viewController instanceof MoisController) {
                ((MoisController) viewController).refreshEvents();
            } else if (viewController instanceof SemaineController) {
                ((SemaineController) viewController).refreshEvents();
            } else if (viewController instanceof JourController) {
                ((JourController) viewController).refreshEvents();
            }
        }

    }

    private void changeSalle(String salle){
        filteredEventsGlobal=null;
        EVENTS_URL = null;
        switch (salle){
            case "Amphi Ada":
                EVENTS_URL ="https://edt-api.univ-avignon.fr/api/exportAgenda/salle/def502004ad8994efaea20cc307f258ea6940659048992f8712fea82dec40dcef9abb3370496d6f4a5156e1c04d131777bebfd1376d5817914a63b8ba72e1cc80559be6094a8a4689bdf30d27f2da7160deed0c85d15fc8be99cc2ac";
                break;
            case "Amphi Blaise":
                EVENTS_URL ="https://edt-api.univ-avignon.fr/api/exportAgenda/salle/def50200dc3d0adee5f98911a8bd365e66f942778f7a2244539c829387654844bd3b144c4de653625ed4a641a7a82d4b60a7df71c474d50b3240dae876e4168e09ad94985081177a0294577f00a78b40dcf42fb13566fb3ee40efc8cca7afe";
                break;
            case "S5 = C 024":
                EVENTS_URL ="https://edt-api.univ-avignon.fr/api/exportAgenda/salle/def50200ab96ed3b7d4b0f5c3d44a46642e5654a8e9fdabc731a94ede5b6807f053b363b0e27e30b1d83dbccfb1d42dd1bee05093463ad4af955564c23e91ac733add95c456d1cc24979ac126c4559017c6c2b3f00c12a12ba8dc0";
                break;
            case "Stat 5 = Info - C 130":
                EVENTS_URL ="https://edt-api.univ-avignon.fr/api/exportAgenda/salle/def5020055568aa88e82405e8ab383ff65f0002f62035a1c938ca9c4f7b858b8f2af3cc2a9ab5b51602951286aa7423fb84867330918a2610331b8dde6ab047a498f6f8cf7b94feac704f8702a394e92ca557afe27fdcd62bdb5769325b2";
                break;
        }

        if (EVENTS_URL != null) {

            Object viewController = getCurrentViewController();
            if (viewController instanceof MoisController) {
                ((MoisController) viewController).refreshEvents();
            } else if (viewController instanceof SemaineController) {
                ((SemaineController) viewController).refreshEvents();
            } else if (viewController instanceof JourController) {
                ((JourController) viewController).refreshEvents();
            }
        }

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
            case "Bonus 1":
                cssFile = "style_bonus1.css";
                break;
            case "Bonus 2":
                cssFile = "style_bonus2.css";
                break;
            case "Bonus 3":
                cssFile = "style_bonus3.css";
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


    @FXML
    private void openFilterWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("filter.fxml"));
            Parent root = fxmlLoader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL); 
            stage.setTitle("Filtre");

            
            Scene scene = new Scene(root);

            
            String css = this.viewComboBox.getScene().getStylesheets().get(0); 
            scene.getStylesheets().add(css);

            stage.setScene(scene);
            stage.showAndWait(); 
        } catch(Exception e) {
            e.printStackTrace();
        }
    }




}

