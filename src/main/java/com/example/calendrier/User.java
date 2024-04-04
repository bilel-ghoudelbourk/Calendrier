package com.example.calendrier;

public class User {
    private String username;
    private String password;
    private String type;
    private Preferences preferences; // Ajoutez ce champ pour les préférences

    // Getters et setters existants

    public Preferences getPreferences() {
        return preferences;
    }

    public void setPreferences(Preferences preferences) {
        this.preferences = preferences;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
