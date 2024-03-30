package com.example.calendrier;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;
import java.time.temporal.TemporalAdjusters;
import java.time.DayOfWeek;
import java.util.List;
import javafx.geometry.HPos;


public class SemaineController {
    @FXML
    private GridPane scheduleGrid;
    @FXML
    private Label currentWeekLabel;

    private LocalDate currentWeekStart;

    @FXML
    private void initialize() {
        currentWeekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        updateWeekDisplay();
        populateHeaders();
    }

    private void populateHeaders() {
        Label emptyLabel = new Label("");
        scheduleGrid.add(emptyLabel, 0, 0);
        GridPane.setHalignment(emptyLabel, HPos.CENTER);

        String[] daysOfWeek = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi"};
        for (int i = 0; i < daysOfWeek.length; i++) {
            Label dayLabel = new Label(daysOfWeek[i]);
            dayLabel.setStyle("-fx-border-style: solid; -fx-border-width: 0 0 1 0; -fx-border-color: #9DB2BF;");
            scheduleGrid.add(dayLabel, i + 1, 0);
            GridPane.setHalignment(dayLabel, HPos.CENTER);
        }


        LocalTime time = LocalTime.of(8, 0);
        for (int i = 1; i <= 23; i++) {
            Label timeLabel = new Label(time.toString());
            scheduleGrid.add(timeLabel, 0, i);
            GridPane.setHalignment(timeLabel, HPos.RIGHT);
            time = time.plusMinutes(30);
        }
    }


    @FXML
    private void handlePreviousWeek() {
        currentWeekStart = currentWeekStart.minusWeeks(1);
        updateWeekDisplay();
    }

    @FXML
    private void handleNextWeek() {
        currentWeekStart = currentWeekStart.plusWeeks(1);
        updateWeekDisplay();
    }

    private void updateWeekDisplay() {
        currentWeekLabel.setText("Semaine du " + currentWeekStart);
        scheduleGrid.getChildren().clear();
        populateHeaders();
        List<Event> events = IcsReader.readIcsFromUrl("https://edt-api.univ-avignon.fr/api/exportAgenda/tdoption/def5020057410cf5c853a924037ce2629db4bc0fe3bd0382e5a07e7e433edd7c7b6a120b7b2c13079afe5493fb79969be2d4786ffcca7abeca404df672aa7001c3e5f252d5b94a390dd452cb7a356d3d6b9682f6c1c3ee5b");
        for (Event event : events) {
            if (isEventInCurrentWeek(event)) {
                placeEventInSchedule(event);
            }
        }
    }


    private boolean isEventInCurrentWeek(Event event) {
        LocalDate eventDate = event.getStartDateTime().toLocalDate();
        return !eventDate.isBefore(currentWeekStart) && eventDate.isBefore(currentWeekStart.plusWeeks(1));
    }

    private void placeEventInSchedule(Event event) {
        int dayOfWeek = event.getStartDateTime().getDayOfWeek().getValue();
        if (dayOfWeek < 1 || dayOfWeek > 5) return;

        LocalTime eventStart = event.getStartDateTime().toLocalTime();
        LocalTime eventEnd = event.getEndDateTime().toLocalTime();
        long startSlotsFromGridStart = Duration.between(LocalTime.of(8, 0), eventStart).toMinutes() / 30;
        long endSlotsFromGridStart = Duration.between(LocalTime.of(8, 0), eventEnd).toMinutes() / 30;

        int startRow = (int) startSlotsFromGridStart + 1;
        int durationSlots = (int) (endSlotsFromGridStart - startSlotsFromGridStart) + 1;

        Label eventLabel = new Label(event.getSummary() + "\n@" + (event.getLocation() != null ? event.getLocation() : "Unknown Location"));
        eventLabel.getStyleClass().add("event-label"); // Utiliser la classe CSS au lieu du style inline

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

        scheduleGrid.add(eventLabel, dayOfWeek, startRow, 1, durationSlots);
    }




}
