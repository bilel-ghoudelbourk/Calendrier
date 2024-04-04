package com.example.calendrier;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.DayOfWeek;
import java.util.List;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;

import javafx.scene.control.Hyperlink;
import java.net.URLEncoder;
import java.net.URI;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;






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
        scheduleGrid.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                setupKeyShortcuts(newScene);
            }
        });
        HeaderController.setCurrentViewController(this);
    }

    private void loadEvents() {
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


    private void setupKeyShortcuts(Scene scene) {

        scene.setOnKeyPressed(null);


        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case LEFT:
                    handlePreviousWeek();
                    break;
                case RIGHT:
                    handleNextWeek();
                    break;
                default:
                    break;
            }
        });
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

            e.consume();
        });

        scheduleGrid.setOnMouseClicked(e -> {
            if (e.getTarget() instanceof Pane || e.getTarget() instanceof Region) {
                openAddEventWindow();
            }
        });

        scheduleGrid.add(eventLabel, dayOfWeek, startRow, 1, durationSlots);
    }


    private void openAddEventWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ajoutEvent.fxml"));
            Parent root = fxmlLoader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Ajouter un événement");

            Scene scene = new Scene(root);
            String css = this.scheduleGrid.getScene().getStylesheets().get(0);
            scene.getStylesheets().add(css);

            stage.setScene(scene);
            stage.showAndWait();
        } catch(Exception e) {
            e.printStackTrace();
        }
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
