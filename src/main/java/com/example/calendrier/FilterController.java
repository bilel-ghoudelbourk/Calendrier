package com.example.calendrier;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.scene.control.Label;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

public class FilterController {

    @FXML
    private ComboBox<String> filterComboBox;

    @FXML
    private TextField searchBar;

    @FXML
    private VBox suggestionsBox;

    private ListView<String> suggestionsList = new ListView<>();
    private ObservableList<Summary> summaries = FXCollections.observableArrayList();
    private ObservableList<String> suggestions = FXCollections.observableArrayList();

    private List<Event> events;

    public void initialize() {
        loadEventsFromUrl();
        filterComboBox.setItems(FXCollections.observableArrayList("Matière", "Enseignant", "Groupes", "Type", "Salle"));
        filterComboBox.getSelectionModel().selectFirst();

        suggestionsList.setItems(suggestions);
        suggestionsBox.getChildren().add(suggestionsList);
        suggestionsBox.setVisible(false);

        searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            updateSuggestionsBasedOnFilter(filterComboBox.getValue(), newValue);
        });

        searchBar.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DOWN) {
                suggestionsList.requestFocus();
            }
        });

        suggestionsList.setOnMouseClicked(event -> {
            searchBar.setText(suggestionsList.getSelectionModel().getSelectedItem());
            suggestionsBox.setVisible(false);
        });

        filterComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            suggestions.clear(); 
            suggestionsBox.setVisible(false); 
            
            updateSuggestionsBasedOnFilter(newValue, searchBar.getText().trim());
        });
    }

    private Set<String> getUniqueLocationsFromEvents(List<Event> events) {
        return events.stream()
                .map(Event::getLocation) 
                .filter(location -> location != null && !location.isEmpty()) 
                .flatMap(location -> Arrays.stream(location.split(",\\s*"))) 
                .collect(Collectors.toSet()); 
    }

    private void updateSuggestionsBasedOnFilter(String filterType, String searchText) {
        suggestions.clear(); 

        if ("Matière".equals(filterType) && !searchText.isEmpty()) {
            summaries.stream()
                    .map(Summary::getMatiere)
                    .distinct()
                    .filter(matiere -> matiere.toLowerCase().contains(searchText.toLowerCase()))
                    .forEach(suggestions::add);
        } else if ("Enseignant".equals(filterType) && !searchText.isEmpty()) {
            summaries.stream()
                    .flatMap(summary -> summary.getEnseignants().stream())
                    .distinct()
                    .filter(enseignant -> enseignant.toLowerCase().contains(searchText.toLowerCase()))
                    .forEach(suggestions::add);
        } else if ("Groupes".equals(filterType) && !searchText.isEmpty()) {
            summaries.stream()
                    .flatMap(summary -> summary.getGroupes().stream())
                    .distinct()
                    .filter(groupe -> groupe.toLowerCase().contains(searchText.toLowerCase()))
                    .forEach(suggestions::add);
        } else if ("Type".equals(filterType) && !searchText.isEmpty()) {
            summaries.stream()
                    .map(Summary::getType)
                    .distinct()
                    .filter(type -> type.toLowerCase().contains(searchText.toLowerCase()))
                    .forEach(suggestions::add);
        } if ("Salle".equals(filterType) && !searchText.isEmpty()) {
            Set<String> uniqueLocations = getUniqueLocationsFromEvents(events);

            uniqueLocations.stream()
                    .filter(location -> location.toLowerCase().contains(searchText.toLowerCase()))
                    .forEach(suggestions::add);
        }

        suggestionsBox.setVisible(!suggestions.isEmpty());
    }




    private void loadEventsFromUrl() {
        String eventsUrl = HeaderController.getEventsUrl();
        events = IcsReader.readIcsFromUrl(eventsUrl);
        for (Event event : events) {
            summaries.add(new Summary(event.getSummary()));
        }
    }

    private boolean filterEvent(Event event, String filterType, String searchText) {
        Summary summary = new Summary(event.getSummary());

        switch (filterType) {
            case "Matière":
                return summary.getMatiere().toLowerCase().contains(searchText.toLowerCase());
            case "Enseignant":
                return summary.getEnseignants().stream().anyMatch(enseignant -> enseignant.toLowerCase().contains(searchText.toLowerCase()));
            case "Groupes":
                return summary.getGroupes().stream().anyMatch(groupe -> groupe.toLowerCase().contains(searchText.toLowerCase()));
            case "Type":
                return summary.getType().toLowerCase().contains(searchText.toLowerCase());
            case "Salle":
                return event.getLocation() != null && event.getLocation().toLowerCase().contains(searchText.toLowerCase());
            default:
                return false;
        }
    }

    @FXML
    private Label filterStatusLabel; 
    @FXML
    private void handleFilterAction(ActionEvent event) {
        loadEventsFromUrl();
        String filterType = filterComboBox.getValue();
        String searchText = searchBar.getText().trim().toLowerCase();

        
        List<Event>  filteredEvents = events.stream()
                .filter(e -> filterEvent(e, filterType, searchText))
                .collect(Collectors.toList());

        
        updateUIWithFilteredEvents(filteredEvents);

        
        if(filteredEvents.isEmpty()){
            filterStatusLabel.setText("Aucun événement trouvé pour '" + searchText + "' dans " + filterType);
        } else {
            filterStatusLabel.setText("Filtre de '" + searchText + "' dans " + filterType + " appliqué. " + filteredEvents.size() + " événements trouvés.");
        }
    }

    @FXML
    private void reinstall() {
        String eventsUrl = HeaderController.getEventsUrl();
        events = IcsReader.readIcsFromUrl(eventsUrl);
        updateUIWithFilteredEvents(events);
    }

    private void updateUIWithFilteredEvents(List<Event> filteredEvents) {
        HeaderController.updateFilteredEventsGlobal(filteredEvents);
        Object viewController = HeaderController.getCurrentViewController();
        if (viewController instanceof MoisController) {
            ((MoisController) viewController).updateEvents(filteredEvents);
        } else if (viewController instanceof SemaineController) {
            ((SemaineController) viewController).updateEvents(filteredEvents);
        } else if (viewController instanceof JourController) {
            ((JourController) viewController).updateEvents(filteredEvents);
        }
    }



    @FXML
    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
