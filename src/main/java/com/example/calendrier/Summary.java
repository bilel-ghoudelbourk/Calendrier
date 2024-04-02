package com.example.calendrier;

import java.util.Arrays;
import java.util.List;

public class Summary {
    private String matiere;
    private List<String> enseignants;
    private List<String> groupes;
    private String type;

    public Summary(String summary) {
        String[] parts = summary.split(" - ");
        if (parts.length > 0) {
            this.matiere = parts[0];
        }
        if (parts.length > 1) {
            this.enseignants = Arrays.asList(parts[1].split(", "));
        }
        if (parts.length > 2) {
            this.groupes = Arrays.asList(parts[2].split(", "));
        }
        if (parts.length > 3) {
            this.type = parts[3];
        }
    }

    // Getters
    public String getMatiere() {
        return matiere;
    }

    public List<String> getEnseignants() {
        return enseignants;
    }

    public List<String> getGroupes() {
        return groupes;
    }

    public String getType() {
        return type;
    }
}
