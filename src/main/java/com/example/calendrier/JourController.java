package com.example.calendrier;

import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;
import java.util.List;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;


public class JourController {
    @FXML
    private GridPane scheduleGrid;
    @FXML
    private Label currentDayLabel;

    private LocalDate currentDate;

    @FXML
    private void initialize() {
        currentDate = LocalDate.now();
        updateDayDisplay(currentDate);
        populateHeaders();
    }

    @FXML
    private void handlePreviousDay() {
        currentDate = currentDate.minusDays(1);
        updateDayDisplay(currentDate);
    }

    @FXML
    private void handleNextDay() {
        currentDate = currentDate.plusDays(1);
        updateDayDisplay(currentDate);
    }

    private void populateHeaders() {

        LocalTime time = LocalTime.of(8, 0);
        for (int i = 1; i <= 23; i++) {
            Label timeLabel = new Label(time.toString());
            scheduleGrid.add(timeLabel, 0, i);
            GridPane.setHalignment(timeLabel, HPos.LEFT);
            time = time.plusMinutes(30);
        }
    }

    private void updateDayDisplay(LocalDate day) {
        currentDayLabel.setText("Jour: " + day);

        scheduleGrid.getChildren().clear();
        populateHeaders();

        List<Event> events = loadEventsForDay(day);

        for (Event event : events) {
            placeEventInSchedule(event);
        }
    }

    private List<Event> loadEventsForDay(LocalDate day) {
        return IcsReader.readIcsFromUrl("https://edt-api.univ-avignon.fr/api/exportAgenda/tdoption/def5020057410cf5c853a924037ce2629db4bc0fe3bd0382e5a07e7e433edd7c7b6a120b7b2c13079afe5493fb79969be2d4786ffcca7abeca404df672aa7001c3e5f252d5b94a390dd452cb7a356d3d6b9682f6c1c3ee5b");
    }

    private void placeEventInSchedule(Event event) {
        LocalDate eventDate = event.getStartDateTime().toLocalDate();
        if (!eventDate.equals(currentDate)) {
            return;
        }

        LocalTime startOfSchedule = LocalTime.of(8, 0);
        LocalTime eventStart = event.getStartDateTime().toLocalTime();
        LocalTime eventEnd = event.getEndDateTime().toLocalTime();
        long startSlotsFromGridStart = Duration.between(startOfSchedule, eventStart).toMinutes() / 30;
        long endSlotsFromGridStart = Duration.between(startOfSchedule, eventEnd).toMinutes() / 30;

        int startRow = (int) startSlotsFromGridStart + 1;
        int durationSlots = (int) (endSlotsFromGridStart - startSlotsFromGridStart);

        Label eventLabel = new Label(event.getSummary() + "\n@" + event.getLocation());
        eventLabel.getStyleClass().add("event-label");
        eventLabel.setWrapText(true);
        double minHeight = durationSlots * 30;
        eventLabel.setMinHeight(minHeight);



        eventLabel.setOnMouseClicked(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Détails de la séance");
            alert.setHeaderText(eventStart.toString() + " - " + eventEnd.toString());
            String content = "Lieu: " + event.getLocation() + "\n\nDescription:\n" + event.getSummary();
            alert.setContentText(content);
            alert.showAndWait();
        });


        scheduleGrid.add(eventLabel, 1, startRow, 1, durationSlots);
    }


}
