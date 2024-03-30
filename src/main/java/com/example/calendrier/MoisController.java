package com.example.calendrier;

import javafx.fxml.FXML;
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
        eventsThisMonth = IcsReader.readIcsFromUrl("https://edt-api.univ-avignon.fr/api/exportAgenda/tdoption/def5020057410cf5c853a924037ce2629db4bc0fe3bd0382e5a07e7e433edd7c7b6a120b7b2c13079afe5493fb79969be2d4786ffcca7abeca404df672aa7001c3e5f252d5b94a390dd452cb7a356d3d6b9682f6c1c3ee5b");
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

    private void showEventsOfDay(LocalDate date) {
        List<Event> eventsOfDay = eventsThisMonth.stream()
                .filter(event -> event.getStartDateTime().toLocalDate().isEqual(date))
                .sorted(Comparator.comparing(event -> event.getStartDateTime().toLocalTime()))
                .collect(Collectors.toList());

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.getDefault());
        alert.setTitle("Événements du " + dateFormatter.format(date));
        alert.setHeaderText("Événements pour " + date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()) +" "+ dateFormatter.format(date));

        if (eventsOfDay.isEmpty()) {
            alert.setContentText("Aucun événement pour ce jour.");
        } else {
            StringBuilder content = new StringBuilder();
            for (Event event : eventsOfDay) {
                LocalTime eventStart = event.getStartDateTime().toLocalTime();
                LocalTime eventEnd = event.getEndDateTime().toLocalTime();
                content.append(String.format("%s - %s : %s (%s)\n",
                        eventStart.format(DateTimeFormatter.ofPattern("HH:mm")),
                        eventEnd.format(DateTimeFormatter.ofPattern("HH:mm")),
                        event.getSummary(),
                        event.getLocation() != null ? event.getLocation() : "Lieu non spécifié"));
            }
            alert.setContentText(content.toString());
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
