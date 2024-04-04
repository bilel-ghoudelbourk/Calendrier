package com.example.calendrier;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserManager {
    public static List<User> loadUsersFromFile(String filePath) {
        List<User> users = new ArrayList<>();
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONObject jsonObject = new JSONObject(content);
            JSONArray usersArray = jsonObject.getJSONArray("users");

            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject userJson = usersArray.getJSONObject(i);
                User user = new User();
                user.setUsername(userJson.getString("username"));
                user.setPassword(userJson.getString("password"));
                user.setType(userJson.getString("type"));


                if (userJson.has("preferences")) {
                    JSONObject prefsJson = userJson.getJSONObject("preferences");
                    Preferences prefs = new Preferences();
                    prefs.setTheme(prefsJson.optString("theme", "Mode Clair"));
                    prefs.setTypeAffichage(prefsJson.optString("typeAffichage", "Par Semaine"));
                    prefs.setFormation(prefsJson.optString("formation", ""));
                    prefs.setSalle(prefsJson.optString("salle", ""));
                    prefs.setEnseignant(prefsJson.optString("enseignant", ""));
                    user.setPreferences(prefs);
                }

                users.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }


    public static void addEventsToUserInJsonFile(String username, List<Event> events, String filePath) {
        try {

            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONObject jsonObject = new JSONObject(content);
            JSONArray usersArray = jsonObject.getJSONArray("users");

            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject userJson = usersArray.getJSONObject(i);
                if (userJson.getString("username").equals(username)) {

                    JSONArray eventsArray = new JSONArray();
                    for (Event event : events) {
                        JSONObject eventJson = new JSONObject();
                        eventJson.put("summary", event.getSummary());
                        eventJson.put("startDateTime", event.getStartDateTime().toString());
                        eventJson.put("endDateTime", event.getEndDateTime().toString());
                        eventJson.put("location", event.getLocation());
                        eventsArray.put(eventJson);
                    }
                    userJson.put("events", eventsArray);
                    break;
                }
            }


            try (FileWriter file = new FileWriter(Paths.get(filePath).toString())) {
                file.write(jsonObject.toString(2));
            }

            System.out.println("Events added successfully for user " + username);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error adding events to JSON file for user " + username);
        }

    }

    public static List<Event> readEventsFromUserInJsonFile(String username, String filePath) {
        List<Event> events = new ArrayList<>();
        try {

            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONObject jsonObject = new JSONObject(content);
            JSONArray usersArray = jsonObject.getJSONArray("users");

            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject userJson = usersArray.getJSONObject(i);
                if (userJson.getString("username").equals(username)) {

                    if (userJson.has("events")) {
                        JSONArray eventsArray = userJson.getJSONArray("events");
                        for (int j = 0; j < eventsArray.length(); j++) {
                            JSONObject eventJson = eventsArray.getJSONObject(j);
                            Event event = new Event(
                                    eventJson.getString("summary"),
                                    LocalDateTime.parse(eventJson.getString("startDateTime")),
                                    LocalDateTime.parse(eventJson.getString("endDateTime")),
                                    eventJson.getString("location")
                            );
                            events.add(event);
                        }
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error reading events for user " + username + " from JSON file.");
        }
        return events;
    }

}
