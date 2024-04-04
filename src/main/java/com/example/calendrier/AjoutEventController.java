package com.example.calendrier;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.stage.Stage;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AjoutEventController {

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<String> startTimeComboBox;

    @FXML
    private ComboBox<String> endTimeComboBox;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private TextField locationTextField;

    @FXML
    private Button reserveButton;
    @FXML
    private Label confirmationLabel;

    private User currentUser;

    @FXML
    private void initialize() {
        populateTimeComboBoxes();
        reserveButton.setOnAction(event -> makeReservation());
        currentUser = LoginController.getCurrentUser();

        datePicker.setValue(LocalDate.now());

        String defaultStartTime = "09:00";
        String defaultEndTime = "10:00";
        if (startTimeComboBox.getItems().contains(defaultStartTime)) {
            startTimeComboBox.getSelectionModel().select(defaultStartTime);
        }
        if (endTimeComboBox.getItems().contains(defaultEndTime)) {
            endTimeComboBox.getSelectionModel().select(defaultEndTime);
        }
    }

    private static Object parentController;

    public static void setParentController(Object parentController) {
        AjoutEventController.parentController = parentController;
    }

    private void populateTimeComboBoxes() {
        List<String> availableTimes = IntStream.range(8, 20)
                .mapToObj(hour -> List.of(String.format("%02d:00", hour), String.format("%02d:30", hour)))
                .flatMap(List::stream)
                .collect(Collectors.toList());
        startTimeComboBox.setItems(FXCollections.observableArrayList(availableTimes));
        endTimeComboBox.setItems(FXCollections.observableArrayList(availableTimes));

        startTimeComboBox.getSelectionModel().select("09:00");
        endTimeComboBox.getSelectionModel().select("10:00");
    }


    List <Event> events = HeaderController.getPersonalEvents();


    private void makeReservation() {
        if (datePicker.getValue() == null || startTimeComboBox.getValue() == null || endTimeComboBox.getValue() == null || locationTextField.getText().isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        LocalTime startTime = LocalTime.parse(startTimeComboBox.getValue());
        LocalTime endTime = LocalTime.parse(endTimeComboBox.getValue());
        LocalDateTime startDateTime = LocalDateTime.of(datePicker.getValue(), startTime);
        LocalDateTime endDateTime = LocalDateTime.of(datePicker.getValue(), endTime);

        List<Event> personalEvents = UserManager.readEventsFromUserInJsonFile(currentUser.getUsername(), "src/main/resources/com/example/calendrier/users.json");

        boolean isOverlapping = personalEvents.stream().anyMatch(event ->
                startDateTime.isBefore(event.getEndDateTime()) && endDateTime.isAfter(event.getStartDateTime()));

        if (isOverlapping) {
            showAlert("Erreur d'Ajout", "Le créneau sélectionné chevauche un de vos événements existants.");
            return;
        }

        Event newEvent = new Event(descriptionTextArea.getText(), startDateTime, endDateTime, locationTextField.getText());
        events.add(newEvent);
        HeaderController.setPersonalEvents(events);
        String filePath = "src/main/resources/com/example/calendrier/users.json";
        UserManager.addEventsToUserInJsonFile(currentUser.getUsername(),events,filePath );

        if (parentController instanceof MoisController) {
            ((MoisController) parentController).updateEvents(personalEvents);
        } else if (parentController instanceof SemaineController) {
            ((SemaineController) parentController).updateEvents(personalEvents);
        } else if (parentController instanceof JourController) {
            ((JourController) parentController).updateEvents(personalEvents);
        }


        confirmationLabel.setText("Événement bien ajouté !");



    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
