package com.example.calendrier;

import java.time.LocalDateTime;

public class Event {
    private String summary;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String location;

    public Event(String summary, LocalDateTime startDateTime, LocalDateTime endDateTime, String location) {
        this.summary = summary;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.location = location;
    }

    // Getters
    public String getSummary() {
        return summary;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public String getLocation() {
        return location;
    }
}
