package com.example.calendrier;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.util.MapTimeZoneCache;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.time.Instant;
import java.time.ZoneId;
import java.io.FileInputStream;

public class IcsReader {
    static {
        System.setProperty("net.fortuna.ical4j.timezone.cache.impl","net.fortuna.ical4j.util.MapTimeZoneCache");
        System.setProperty("ical4j.unfolding.relaxed", "true");
        System.setProperty("ical4j.parsing.relaxed", "true");
        System.setProperty("ical4j.compatibility.outlook", "true");
        System.setProperty("ical4j.compatibility.notes", "true");

    }

    public static List<Event> readIcsFromUrl(String urlString) {
        List<Event> events = new ArrayList<>();
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlString))
                    .GET()
                    .build();

            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            CalendarBuilder builder = new CalendarBuilder();
            net.fortuna.ical4j.model.Calendar calendar = builder.build(response.body());

            for (Object o : calendar.getComponents(Component.VEVENT)) {
                try {
                    VEvent vevent = (VEvent) o;
                    String summary = vevent.getSummary() != null ? vevent.getSummary().getValue() : "No Summary";
                    DateTime start = (DateTime) (vevent.getStartDate() != null ? vevent.getStartDate().getDate() : new DateTime());
                    DateTime end = (DateTime) (vevent.getEndDate() != null ? vevent.getEndDate().getDate() : new DateTime());
                    String location = (vevent.getLocation() != null) ? vevent.getLocation().getValue() : "Unknown Location";

                    Event event = new Event(summary,
                            LocalDateTime.ofInstant(Instant.ofEpochMilli(start.getTime()), ZoneId.systemDefault()),
                            LocalDateTime.ofInstant(Instant.ofEpochMilli(end.getTime()), ZoneId.systemDefault()),
                            location);
                    events.add(event);
                } catch (Exception e) {
                    System.err.println("");
                }
            }
        } catch (Exception e) {
            System.err.println("");
        }
        return events;
    }
    public static List<Event> readIcsFile(String filePath) {
        List<Event> events = new ArrayList<>();
        try {
            FileInputStream fin = new FileInputStream(filePath);
            CalendarBuilder builder = new CalendarBuilder();
            net.fortuna.ical4j.model.Calendar calendar = builder.build(fin);

            for (Object o : calendar.getComponents(Component.VEVENT)) {
                try {
                    VEvent vevent = (VEvent) o;
                    String summary = vevent.getSummary() != null ? vevent.getSummary().getValue() : "No Summary";
                    DateTime start = (DateTime) (vevent.getStartDate() != null ? vevent.getStartDate().getDate() : new DateTime());
                    DateTime end = (DateTime) (vevent.getEndDate() != null ? vevent.getEndDate().getDate() : new DateTime());
                    String location = (vevent.getLocation() != null) ? vevent.getLocation().getValue() : "Unknown Location";

                    Event event = new Event(summary,
                            LocalDateTime.ofInstant(Instant.ofEpochMilli(start.getTime()), ZoneId.systemDefault()),
                            LocalDateTime.ofInstant(Instant.ofEpochMilli(end.getTime()), ZoneId.systemDefault()),
                            location);
                    events.add(event);
                } catch (Exception e) {
                    System.err.println("");
                }
            }
        } catch (Exception e) {
            System.err.println("");
        }
        return events;
    }



}

