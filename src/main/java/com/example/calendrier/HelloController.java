package com.example.calendrier;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.util.List;

public class HelloController {
    @FXML
    private ListView<String> eventsList;

    @FXML
    private void initialize() {
        List<Event> events = IcsReader.readIcsFromUrl("https://edt-api.univ-avignon.fr/api/exportAgenda/tdoption/def5020057410cf5c853a924037ce2629db4bc0fe3bd0382e5a07e7e433edd7c7b6a120b7b2c13079afe5493fb79969be2d4786ffcca7abeca404df672aa7001c3e5f252d5b94a390dd452cb7a356d3d6b9682f6c1c3ee5b");
        for (Event event : events) {
            eventsList.getItems().add(event.getSummary() + " - " + event.getStartDateTime() + " to " + event.getEndDateTime());
        }
    }
}