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

                // Parser les préférences
                if (userJson.has("preferences")) {
                    JSONObject prefsJson = userJson.getJSONObject("preferences");
                    Preferences prefs = new Preferences();
                    prefs.setTheme(prefsJson.optString("theme", "Mode Clair")); // Valeur par défaut si non spécifié
                    prefs.setTypeAffichage(prefsJson.optString("typeAffichage", "Par Semaine")); // Valeur par défaut
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

}
