package com.example.calendrier;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;
import java.time.format.TextStyle;
import java.util.Locale;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import javafx.scene.control.ScrollPane;

import javafx.scene.control.DialogPane;



public class MoisController {
    @FXML private GridPane calendarGrid;
    @FXML private Label currentMonthLabel;

    private YearMonth currentYearMonth;
    private List<Event> eventsThisMonth;

    public void initialize() {
        currentYearMonth = YearMonth.now();
        loadEventsForMonth();
        updateMonthDisplay();
    }

    private void loadEventsForMonth() {
        eventsThisMonth = IcsReader.readIcsFromUrl(HeaderController.getEventsUrl());
    }
    private void updateMonthDisplay() {
        currentMonthLabel.setText(currentYearMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + currentYearMonth.getYear());
        populateCalendar();
    }
    private void populateCalendar() {
        calendarGrid.getChildren().clear();
        LocalDate firstDayOfMonth = currentYearMonth.atDay(1);
        int firstDayOfWeekOffset = firstDayOfMonth.getDayOfWeek().getValue() - 1;
        for (int day = 1; day <= currentYearMonth.lengthOfMonth(); day++) {
            LocalDate date = currentYearMonth.atDay(day);
            int column = (firstDayOfWeekOffset + day - 1) % 7;
            int row = (firstDayOfWeekOffset + day - 1) / 7;
            VBox dayCell = new VBox();
            dayCell.getStyleClass().add("day-cell");

            Label lblDay = new Label(Integer.toString(day));
            lblDay.getStyleClass().add("day-label");
            dayCell.getChildren().add(lblDay);

            int eventCount = getEventCountForDate(date);
            Label lblEventCount = new Label(eventCount + " séance" + (eventCount > 1 ? "s" : ""));
            lblEventCount.getStyleClass().add("event-count-label");
            dayCell.getChildren().add(lblEventCount);

            dayCell.setOnMouseClicked(e -> showEventsOfDay(date));
            calendarGrid.add(dayCell, column, row);
        }
    }

    private int getEventCountForDate(LocalDate date) {
        return (int) eventsThisMonth.stream()
                .filter(event -> !event.getStartDateTime().toLocalDate().isAfter(date) &&
                        !event.getEndDateTime().toLocalDate().isBefore(date))
                .count();
    }

    public void showEventsOfDay(LocalDate date) {
        List<Event> eventsOfDay = eventsThisMonth.stream()
                .filter(event -> event.getStartDateTime().toLocalDate().isEqual(date))
                .sorted(Comparator.comparing(event -> event.getStartDateTime().toLocalTime()))
                .collect(Collectors.toList());

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.getDefault());
        alert.setTitle("Événements du " + dateFormatter.format(date));
        alert.setHeaderText("Événements pour " + date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + dateFormatter.format(date));

        VBox contentBox = new VBox(5);
        contentBox.setStyle("-fx-padding: 10;");

        if (eventsOfDay.isEmpty()) {
            contentBox.getChildren().add(new Label("Aucun événement pour ce jour."));
        } else {
            for (Event event : eventsOfDay) {
                LocalTime eventStart = event.getStartDateTime().toLocalTime();
                LocalTime eventEnd = event.getEndDateTime().toLocalTime();
                String eventText = String.format("%s - %s : %s (%s)",
                        eventStart.format(DateTimeFormatter.ofPattern("HH:mm")),
                        eventEnd.format(DateTimeFormatter.ofPattern("HH:mm")),
                        event.getSummary(),
                        event.getLocation() != null ? event.getLocation() : "Lieu non spécifié");

                Label eventLabel = new Label(eventText);
                eventLabel.setWrapText(true);
                eventLabel.getStyleClass().addAll("event-label", "text-color", "primary-color");
                contentBox.getChildren().add(eventLabel);
            }
        }

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setPrefHeight(400);
        scrollPane.setPrefWidth(800);
        alert.getDialogPane().setContent(scrollPane);

        Scene currentScene = calendarGrid.getScene();
        if (currentScene != null) {
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().addAll(currentScene.getStylesheets());
            dialogPane.getStyleClass().add("myDialog");
        }

        alert.showAndWait();
    }





    @FXML private void goToNextMonth() {
        currentYearMonth = currentYearMonth.plusMonths(1);
        loadEventsForMonth();
        updateMonthDisplay();
    }

    @FXML private void goToPreviousMonth() {
        currentYearMonth = currentYearMonth.minusMonths(1);
        loadEventsForMonth();
        updateMonthDisplay();
    }
}
