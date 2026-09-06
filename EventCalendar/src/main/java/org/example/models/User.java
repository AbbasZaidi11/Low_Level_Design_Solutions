package org.example.models;

import java.time.LocalTime;
import java.util.Collections;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.TreeSet;

public class User extends Participant {

    private final LocalTime workingHoursStart;
    private final LocalTime workingHoursEnd;
    private Optional<Team> team;
    private final TreeSet<Event> events;

    public User(String name, LocalTime workingHoursStart, LocalTime workingHoursEnd) {
        super(name);
        if (workingHoursStart == null || workingHoursEnd == null || !workingHoursStart.isBefore(workingHoursEnd)) {
            throw new IllegalArgumentException("Working-hours start must be before end");
        }
        this.workingHoursStart = workingHoursStart;
        this.workingHoursEnd = workingHoursEnd;
        this.team = Optional.empty();
        this.events = new TreeSet<>();
    }

    public LocalTime getWorkingHoursStart() {
        return workingHoursStart;
    }

    public LocalTime getWorkingHoursEnd() {
        return workingHoursEnd;
    }

    public Optional<Team> getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = Optional.of(team);
    }

    public NavigableSet<Event> getEvents() {
        return Collections.unmodifiableNavigableSet(events);
    }

    public void addEvent(Event event) {
        events.add(event);
    }
}
