package com.example.calendrier;

public class Preferences {
    private String theme;
    private String typeAffichage;
    private String formation;
    private String salle;
    private String enseignant;

    // Getters et Setters pour chaque champ

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getTypeAffichage() {
        return typeAffichage;
    }

    public void setTypeAffichage(String typeAffichage) {
        this.typeAffichage = typeAffichage;
    }

    public String getFormation() {
        return formation;
    }

    public void setFormation(String formation) {
        this.formation = formation;
    }

    public String getSalle() {
        return salle;
    }

    public void setSalle(String salle) {
        this.salle = salle;
    }

    public String getEnseignant() {
        return enseignant;
    }

    public void setEnseignant(String enseignant) {
        this.enseignant = enseignant;
    }
}
