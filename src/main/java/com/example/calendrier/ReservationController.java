package com.example.calendrier;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


public class ReservationController {

    @FXML
    private ComboBox<String> salleComboBox;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<String> startTimeComboBox;

    @FXML
    private ComboBox<String> endTimeComboBox;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private Button reserveButton;
    @FXML
    private Label confirmationLabel;



    private static Object parentController;

    public static void setParentController(Object parentController) {
        ReservationController.parentController = parentController;
    }

    @FXML
    private void initialize() {
        salleComboBox.getItems().addAll("Séléctionner une salle", "Amphi Blaise", "S5 = C 024", "Stat 5 = Info - C 130");
        salleComboBox.getSelectionModel().selectFirst();


        datePicker.setValue(LocalDate.now());


        List<String> availableStartTimes = generateTimes(8, 18);
        List<String> availableEndTimes = generateTimes(9, 19);
        startTimeComboBox.setItems(FXCollections.observableArrayList(availableStartTimes));
        endTimeComboBox.setItems(FXCollections.observableArrayList(availableEndTimes));


        startTimeComboBox.getSelectionModel().select("08:00");
        endTimeComboBox.getSelectionModel().select("19:00");

        salleComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updateAvailableTimes(newVal));

        reserveButton.setOnAction(event -> makeReservation());
    }

    private List<String> generateTimes(int startHour, int endHour) {
        return IntStream.range(startHour, endHour)
                .mapToObj(hour -> List.of(String.format("%02d:00", hour), String.format("%02d:30", hour)))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }


    private void updateAvailableTimes(String salle) {

        List<Event> events = fetchEventsForSalle(salle);

        List<String> availableTimes = IntStream.range(8, 20)
                .mapToObj(hour -> List.of(String.format("%02d:00", hour), String.format("%02d:30", hour)))
                .flatMap(List::stream)
                .collect(Collectors.toList());

        startTimeComboBox.setItems(FXCollections.observableArrayList(availableTimes));
        endTimeComboBox.setItems(FXCollections.observableArrayList(availableTimes));



    }

    private List<Event> fetchEventsForSalle(String salle) {
        String eventsUrl;
        switch (salle){
            case "Amphi Ada":
                eventsUrl ="https://edt-api.univ-avignon.fr/api/exportAgenda/salle/def502004ad8994efaea20cc307f258ea6940659048992f8712fea82dec40dcef9abb3370496d6f4a5156e1c04d131777bebfd1376d5817914a63b8ba72e1cc80559be6094a8a4689bdf30d27f2da7160deed0c85d15fc8be99cc2ac";
                break;
            case "Amphi Blaise":
                eventsUrl ="https://edt-api.univ-avignon.fr/api/exportAgenda/salle/def50200dc3d0adee5f98911a8bd365e66f942778f7a2244539c829387654844bd3b144c4de653625ed4a641a7a82d4b60a7df71c474d50b3240dae876e4168e09ad94985081177a0294577f00a78b40dcf42fb13566fb3ee40efc8cca7afe";
                break;
            case "S5 = C 024":
                eventsUrl ="https://edt-api.univ-avignon.fr/api/exportAgenda/salle/def50200ab96ed3b7d4b0f5c3d44a46642e5654a8e9fdabc731a94ede5b6807f053b363b0e27e30b1d83dbccfb1d42dd1bee05093463ad4af955564c23e91ac733add95c456d1cc24979ac126c4559017c6c2b3f00c12a12ba8dc0";
                break;
            case "Stat 5 = Info - C 130":
                eventsUrl ="https://edt-api.univ-avignon.fr/api/exportAgenda/salle/def5020055568aa88e82405e8ab383ff65f0002f62035a1c938ca9c4f7b858b8f2af3cc2a9ab5b51602951286aa7423fb84867330918a2610331b8dde6ab047a498f6f8cf7b94feac704f8702a394e92ca557afe27fdcd62bdb5769325b2";
                break;
            default:

                return new ArrayList<>();
        }

        try {

            return IcsReader.readIcsFromUrl(eventsUrl);
        } catch (Exception e) {


            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    private void makeReservation() {
        if (datePicker.getValue() == null || startTimeComboBox.getValue() == null || endTimeComboBox.getValue() == null || salleComboBox.getValue().equals("Séléctionner une salle")) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        LocalTime startTime = LocalTime.parse(startTimeComboBox.getValue());
        LocalTime endTime = LocalTime.parse(endTimeComboBox.getValue());
        LocalDateTime startDateTime = LocalDateTime.of(datePicker.getValue(), startTime);
        LocalDateTime endDateTime = LocalDateTime.of(datePicker.getValue(), endTime);


        List<Event> existingEvents = fetchEventsForSalle(salleComboBox.getValue());


        boolean isOverlapping = existingEvents.stream().anyMatch(event ->
                startDateTime.isBefore(event.getEndDateTime()) && endDateTime.isAfter(event.getStartDateTime()));

        if (isOverlapping) {
            showAlert("Erreur de réservation", "Le créneau sélectionné chevauche un événement existant.");
            return;
        }


        Event newEvent = new Event(descriptionTextArea.getText(), startDateTime, endDateTime, salleComboBox.getValue());

        try {
            ReservationController.addEventToSalle(salleComboBox.getValue(), newEvent, "src/main/resources/com/example/calendrier/salles.json");
            List<Event> newEvents= getEventsForSalle(descriptionTextArea.getText(),"src/main/resources/com/example/calendrier/salles.json");
            List<Event> finalEvent=mergeSalle(existingEvents,newEvents);
            if (parentController instanceof MoisController) {
                ((MoisController) parentController).updateEvents(finalEvent);
            } else if (parentController instanceof SemaineController) {
                ((SemaineController) parentController).updateEvents(finalEvent);
            } else if (parentController instanceof JourController) {
                ((JourController) parentController).updateEvents(finalEvent);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        confirmationLabel.setText("Nouvelle réservation effectuée avec succès.");
    }



    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    public static void addEventToSalle(String salleNom, Event event, String filePath) throws Exception {
        String jsonContent = new String(Files.readAllBytes(Paths.get(filePath)));
        JSONObject root = new JSONObject(jsonContent);

        JSONArray salles = root.getJSONArray("salles");
        for (int i = 0; i < salles.length(); i++) {
            JSONObject salle = salles.getJSONObject(i);
            if (salle.getString("nom").equals(salleNom)) {
                JSONArray events = salle.getJSONArray("events");
                JSONObject eventJSON = new JSONObject();
                eventJSON.put("summary", event.getSummary());
                eventJSON.put("startDateTime", event.getStartDateTime().toString());
                eventJSON.put("endDateTime", event.getEndDateTime().toString());
                eventJSON.put("location", event.getLocation());
                events.put(eventJSON);
                break;
            }
        }

        try (FileWriter file = new FileWriter(filePath)) {
            file.write(root.toString(4));
        }
    }

    public static List<Event> getEventsForSalle(String salleNom, String filePath) {
        List<Event> eventsList = new ArrayList<>();
        try {

            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONObject root = new JSONObject(content);


            JSONArray salles = root.getJSONArray("salles");
            for (int i = 0; i < salles.length(); i++) {
                JSONObject salle = salles.getJSONObject(i);
                if (salle.getString("nom").equals(salleNom)) {

                    JSONArray events = salle.getJSONArray("events");
                    for (int j = 0; j < events.length(); j++) {
                        JSONObject eventObj = events.getJSONObject(j);
                        Event event = new Event(
                                eventObj.getString("summary"),
                                LocalDateTime.parse(eventObj.getString("startDateTime")),
                                LocalDateTime.parse(eventObj.getString("endDateTime")),
                                eventObj.getString("location")
                        );
                        eventsList.add(event);
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return eventsList;
    }

    public static List<Event> mergeSalle(List<Event> e1, List<Event> e2){
        return Stream.concat(e1.stream(), e2.stream())
                .distinct()
                .collect(Collectors.toList());
    }

    @FXML
    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }



}
