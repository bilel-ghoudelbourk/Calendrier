package com.example.calendrier;

import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.net.URI;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;


public class JourController {
    @FXML
    private GridPane scheduleGrid;
    @FXML
    private Label currentDayLabel;

    private LocalDate currentDate;
    private List<Event> events;

    @FXML
    private void initialize() {

        currentDate = LocalDate.now();
        loadEventsForDay();
        updateDayDisplay(currentDate);
        populateHeaders();
        HeaderController.setCurrentViewController(this);
        scheduleGrid.sceneProperty().addListener((observable, oldValue, newScene) -> {
            if (newScene != null && newScene.getWindow() != null) {
                setupKeyShortcuts(newScene);

                newScene.getWindow().addEventFilter(WindowEvent.WINDOW_SHOWN, windowEvent -> setupKeyShortcuts(newScene));
            }
        });
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

    private void setupKeyShortcuts(Scene scene) {
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case LEFT:
                    handlePreviousDay();
                    break;
                case RIGHT:
                    handleNextDay();
                    break;
                default:
                    break;
            }
        });
    }

    private void updateDayDisplay(LocalDate day) {
        currentDayLabel.setText("Jour: " + day);


        scheduleGrid.getChildren().clear();
        populateHeaders();


        for (Event event : events) {
            placeEventInSchedule(event);
        }
    }

    public void updateEvents(List<Event> e) {
        this.events = e;
        updateDayDisplay(currentDate);
    }

    public void refreshEvents() {
        loadEventsForDay();
        updateDayDisplay(currentDate);
    }


    private void loadEventsForDay() {
        List<Event> globalFilteredEvents = HeaderController.getFilteredEventsGlobal();
        List<Event> personalEvents = HeaderController.getPersonalEvents();
        List<Event> salleEvents = HeaderController.getFinalSalleEvents();


        if (globalFilteredEvents != null) {
            events = globalFilteredEvents;

        } else if(personalEvents!=null){ events=personalEvents; }
        else if(salleEvents!=null){ events=salleEvents; }
        else{
            events = IcsReader.readIcsFromUrl(HeaderController.getEventsUrl());
        }
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


        boolean summaryMatchesFormat = event.getSummary().matches(".*-.*-.*");
        VBox content = new VBox();

        if (summaryMatchesFormat) {

            Summary summary = new Summary(event.getSummary());
            content.getChildren().add(new Label("Matière: " + summary.getMatiere()));
            content.getChildren().add(new Label("Type: " + summary.getType()));
            content.getChildren().add(new Label("Groupes: " + String.join(", ", summary.getGroupes())));

            Label enseignantsLabel = new Label("Enseignants :");
            content.getChildren().add(enseignantsLabel);

            summary.getEnseignants().forEach(enseignant -> {
                Hyperlink emailLink = new Hyperlink(enseignant);
                emailLink.getStyleClass().add("hyperlink");
                emailLink.setOnAction(ev -> {
                    String email = getEmailForTeacher(enseignant);
                    openEmailClient(email);
                });
                content.getChildren().add(emailLink);
            });
        } else {


            content.getChildren().add(new Label(event.getSummary()));
        }

        Label eventLabel = new Label(event.getSummary() + "\n@" + (event.getLocation() != null ? event.getLocation() : "Unknown Location"));
        eventLabel.getStyleClass().add("event-label");
        eventLabel.setMinHeight(durationSlots * 30);
        eventLabel.setOnMouseClicked(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(String.format("%s - %s", eventStart.format(DateTimeFormatter.ofPattern("HH:mm")), eventEnd.format(DateTimeFormatter.ofPattern("HH:mm"))));
            alert.setTitle("Détails de l'événement");

            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setContent(content);
            scrollPane.setPrefSize(800, 400);

            Scene currentScene = scheduleGrid.getScene();

            if (currentScene != null) {
                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.setContent(scrollPane);
                dialogPane.getStylesheets().addAll(currentScene.getStylesheets());
                dialogPane.getStyleClass().add("myDialog");
            }
            alert.showAndWait();
        });

        scheduleGrid.add(eventLabel, 1, startRow, 1, durationSlots);
    }


    private String getEmailForTeacher(String teacherName) {
        switch (teacherName) {
            case ("CECILLON Noe"):
                return "noe.cecillon@univ-avignon.fr";
            default:
                return "example@example.com";
        }
    }

    private void openEmailClient(String email) {
        try {

            String subject = URLEncoder.encode("", "UTF-8");
            String body = URLEncoder.encode("", "UTF-8");
            String gmailUrl = "https://mail.google.com/mail/?view=cm&fs=1&to=" + email + "&su=" + subject + "&body=" + body;

            java.awt.Desktop.getDesktop().browse(new URI(gmailUrl));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
