package org.example.models;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Event implements Comparable<Event> {
    private final String name;
    private final List<User> userParticipants;
    private final List<String> directUserNames;
    private final List<String> teamNames;
    private final int representativesPerTeam;
    private final Map<String, List<User>> selectedRepresentatives;
    private final TimeSlot timeSlot;

    public Event(String name, List<User> userParticipants, List<String> directUserNames, List<String> teamNames,
                 int representativesPerTeam, Map<String, List<User>> selectedRepresentatives, TimeSlot timeSlot) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Event name is required");
        this.name = name;
        this.userParticipants = List.copyOf(userParticipants);
        this.directUserNames = List.copyOf(directUserNames);
        this.teamNames = List.copyOf(teamNames);
        this.representativesPerTeam = representativesPerTeam;
        this.selectedRepresentatives = Map.copyOf(selectedRepresentatives);
        this.timeSlot = Objects.requireNonNull(timeSlot, "Time slot is required");
    }

    public String getName() {
        return name;
    }

    public List<User> getUserParticipants() {
        return userParticipants;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public List<String> getDirectUserNames() { return directUserNames; }
    public List<String> getTeamNames() { return teamNames; }
    public int getRepresentativesPerTeam() { return representativesPerTeam; }
    public Map<String, List<User>> getSelectedRepresentatives() { return selectedRepresentatives; }

    @Override
    public int compareTo(Event other) {
        int result = this.timeSlot.compareTo(other.timeSlot);

        if (result != 0) {
            return result;
        }

        return this.name.compareTo(other.name);
    }
}
