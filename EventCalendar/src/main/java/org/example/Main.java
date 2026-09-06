package org.example;

import org.example.app.EventCalendar;
import org.example.models.Event;
import org.example.models.TimeSlot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        EventCalendar calendar = new EventCalendar();
        LocalDate today = LocalDate.of(2026, 9, 6);
        LocalDate tomorrow = today.plusDays(1);

        calendar.createUser("A", LocalTime.of(10, 0), LocalTime.of(19, 0));
        calendar.createUser("B", LocalTime.of(9, 30), LocalTime.of(17, 30));
        calendar.createUser("C", LocalTime.of(11, 30), LocalTime.of(18, 30));
        calendar.createUser("D", LocalTime.of(10, 0), LocalTime.of(18, 0));
        calendar.createUser("E", LocalTime.of(11, 0), LocalTime.of(19, 30));
        calendar.createUser("F", LocalTime.of(11, 0), LocalTime.of(18, 30));
        calendar.createTeam("T1", List.of("C", "E"));
        calendar.createTeam("T2", List.of("B", "D", "F"));

        calendar.createEvent("Event1", List.of("A"), List.of("T1"), 2,
                tomorrow.atTime(14, 0), tomorrow.atTime(15, 0));
        expectFailure("overlapping selected representative", () -> calendar.createEvent("Event2", List.of("C"), List.of(), 0,
                tomorrow.atTime(14, 0), tomorrow.atTime(15, 0)));
        calendar.createEvent("Event3", List.of(), List.of("T1", "T2"), 2,
                today.atTime(15, 0), today.atTime(16, 0));
        calendar.createEvent("Event4", List.of("A"), List.of("T2"), 1,
                today.atTime(15, 0), today.atTime(16, 0));
        expectFailure("outside F working hours", () -> calendar.createEvent("Event5", List.of("F"), List.of(), 0,
                today.atTime(10, 0), today.atTime(11, 0)));
        expectFailure("insufficient T1 representatives", () -> calendar.createEvent("Event6", List.of(), List.of("T1"), 2,
                tomorrow.atTime(14, 0), tomorrow.atTime(15, 0)));

        System.out.println("A's events in range:");
        printEvents(calendar.getEventsForUser("A", today.atTime(10, 0), tomorrow.atTime(17, 0)));
        System.out.println("C's events (includes T1 selection):");
        printEvents(calendar.getEventsForUser("C", today.atStartOfDay(), tomorrow.plusDays(1).atStartOfDay()));
        System.out.println("Available slots for A + T1 (one representative) today:");
        for (TimeSlot slot : calendar.suggestAvailableSlots(List.of("A"), List.of("T1"), 1, today)) {
            System.out.println("  " + slot.getStartTime().toLocalTime() + " - " + slot.getEndTime().toLocalTime());
        }
    }

    private static void printEvents(List<Event> events) {
        events.forEach(event -> System.out.println("  " + event.getName() + " " + event.getTimeSlot().getStartTime()
                + " - " + event.getTimeSlot().getEndTime()));
    }

    private static void expectFailure(String scenario, Runnable action) {
        try {
            action.run();
            throw new IllegalStateException("Expected failure did not occur: " + scenario);
        } catch (IllegalArgumentException | IllegalStateException expected) {
            System.out.println("Expected failure (" + scenario + "): " + expected.getMessage());
        }
    }
}
