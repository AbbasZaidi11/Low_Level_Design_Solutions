package org.example.app;

import org.example.managers.EventManager;
import org.example.managers.TeamManager;
import org.example.managers.UserManager;
import org.example.models.Event;
import org.example.models.TimeSlot;
import org.example.models.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class EventCalendar {
    private UserManager userManager;
    private TeamManager teamManager;
    private EventManager eventManager;

    public EventCalendar() {
        userManager = new UserManager();
        teamManager = new TeamManager(userManager);
        eventManager = new EventManager(userManager, teamManager);
    }

    public User createUser(String name, LocalTime startTime, LocalTime endTime) {
        return userManager.createUser(name, startTime, endTime);
    }

    public void createTeam(String name, List<String> userNames) {
        teamManager.createTeam(name, userNames);
    }

    public Event createEvent(String eventName, List<String> userNames, List<String> teamNames, int rep,
                             LocalDateTime startTime, LocalDateTime endTime) {
        return eventManager.createEvent(eventName, userNames, teamNames, rep, startTime, endTime);
    }

    public List<Event> getEventsForUser(String userId, LocalDateTime from, LocalDateTime to) {
        return eventManager.getEventsForUser(userId, from, to);
    }

    public List<TimeSlot> suggestAvailableSlots(List<String> userNames, List<String> teamNames, int representatives,
                                                 LocalDate date) {
        return eventManager.suggestAvailableSlots(userNames, teamNames, representatives, date);
    }
}
