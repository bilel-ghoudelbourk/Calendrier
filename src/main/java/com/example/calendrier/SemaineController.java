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
import javafx.scene.control.DialogPane;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;






public class SemaineController {
    @FXML
    private GridPane scheduleGrid;
    @FXML
    private Label currentWeekLabel;

    private LocalDate currentWeekStart;
    private List<Event> events=null;

    @FXML
    private void initialize() {
        loadEvents();
        currentWeekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        updateWeekDisplay();
        populateHeaders();
        HeaderController.setCurrentViewController(this);
    }

    private void loadEvents() {
        List<Event> globalFilteredEvents = HeaderController.getFilteredEventsGlobal();
        if (globalFilteredEvents != null) {
            events = globalFilteredEvents; 
        } else {
            events = IcsReader.readIcsFromUrl(HeaderController.getEventsUrl()); 
        }
    }
    private void populateHeaders() {
        Label emptyLabel = new Label("");
        scheduleGrid.add(emptyLabel, 0, 0);
        GridPane.setHalignment(emptyLabel, HPos.CENTER);

        String[] daysOfWeek = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi"};
        for (int i = 0; i < daysOfWeek.length; i++) {
            Label dayLabel = new Label(daysOfWeek[i]);
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
        //events = IcsReader.readIcsFromUrl(HeaderController.getEventsUrl());
        for (Event event : events) {
            if (isEventInCurrentWeek(event)) {
                placeEventInSchedule(event);
            }
        }
    }

    public void updateEvents(List<Event> filteredEvents) {
        this.events = filteredEvents;
        updateWeekDisplay();
    }



    public void refreshEvents() {
        loadEvents();
        updateWeekDisplay(); 
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
        eventLabel.getStyleClass().add("event-label"); 

        double minHeight = durationSlots * 30;
        eventLabel.setMinHeight(minHeight);

        eventLabel.setOnMouseClicked(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Détails de la séance");
            alert.setHeaderText(eventStart.toString() + " - " + eventEnd.toString());
            String content = "Lieu: " + event.getLocation() + "\n\nDescription:\n" + event.getSummary();

            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setPrefSize(800, 400);
            Label l=new Label(content);
            scrollPane.setContent(l);

            Scene currentScene = scheduleGrid.getScene();
            if (currentScene != null) {
                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.getStylesheets().addAll(currentScene.getStylesheets());
                dialogPane.getStyleClass().add("myDialog");
            }

            alert.getDialogPane().setContent(scrollPane);

            alert.showAndWait();
        });



        scheduleGrid.add(eventLabel, dayOfWeek, startRow, 1, durationSlots);
    }




}
