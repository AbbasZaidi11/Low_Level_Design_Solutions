package org.example.managers;

import org.example.models.Event;
import org.example.models.Team;
import org.example.models.TimeSlot;
import org.example.models.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.TreeSet;

public class EventManager {
    private final UserManager userManager;
    private final TeamManager teamManager;
    private final Map<String, Event> eventMap = new LinkedHashMap<>();

    public EventManager(UserManager userManager, TeamManager teamManager) {
        this.userManager = userManager;
        this.teamManager = teamManager;
    }

    public Event createEvent(String eventName, List<String> userNames, List<String> teamNames, int representatives,
                             LocalDateTime start, LocalDateTime end) {
        TimeSlot slot = validateEventRequest(eventName, userNames, teamNames, representatives, start, end);
        List<String> directUsers = copyDistinct(userNames, "Direct users");
        List<String> teams = copyDistinct(teamNames, "Teams");
        LinkedHashSet<User> blockedUsers = new LinkedHashSet<>();
        Map<String, List<User>> selectedByTeam = new LinkedHashMap<>();

        for (String userName : directUsers) {
            User user = userManager.getUser(userName);
            ensureAvailable(user, slot);
            blockedUsers.add(user);
        }
        for (String teamName : teams) {
            Team team = teamManager.getTeam(teamName);
            List<User> selected = new ArrayList<>();
            for (User member : team.getTeamMembers()) {
                if (isAvailable(member, slot)) {
                    selected.add(member);
                    if (selected.size() == representatives) break;
                }
            }
            if (selected.size() < representatives) {
                throw new IllegalStateException("Event " + eventName + " cannot be created: team " + teamName
                        + " has fewer than " + representatives + " available representatives");
            }
            selectedByTeam.put(teamName, List.copyOf(selected));
            blockedUsers.addAll(selected);
        }

        Event event = new Event(eventName, new ArrayList<>(blockedUsers), directUsers, teams, representatives,
                selectedByTeam, slot);
        eventMap.put(eventName, event);
        blockedUsers.forEach(user -> userManager.addEvent(user, event));
        return event;
    }

    public List<Event> getEventsForUser(String userName, LocalDateTime from, LocalDateTime to) {
        TimeSlot range = new TimeSlot(from, to);
        return userManager.getUser(userName).getEvents().stream()
                .filter(event -> event.getTimeSlot().overlaps(range)).toList();
    }

    public List<TimeSlot> suggestAvailableSlots(List<String> userNames, List<String> teamNames, int representatives,
                                                LocalDate date) {
        Objects.requireNonNull(date, "Date is required");
        List<String> directUsers = copyDistinct(userNames, "Direct users");
        List<String> teams = copyDistinct(teamNames, "Teams");
        if (directUsers.isEmpty() && teams.isEmpty()) throw new IllegalArgumentException("At least one participant is required");
        if (!teams.isEmpty() && representatives <= 0) throw new IllegalArgumentException("Representatives must be positive");

        List<User> allCandidates = new ArrayList<>();
        directUsers.forEach(name -> allCandidates.add(userManager.getUser(name)));
        for (String teamName : teams) allCandidates.addAll(teamManager.getTeam(teamName).getTeamMembers());

        NavigableSet<LocalDateTime> boundaries = new TreeSet<>();
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();
        boundaries.add(dayStart);
        boundaries.add(dayEnd);
        for (User user : allCandidates) {
            boundaries.add(date.atTime(user.getWorkingHoursStart()));
            boundaries.add(date.atTime(user.getWorkingHoursEnd()));
            for (Event event : user.getEvents()) {
                TimeSlot eventSlot = event.getTimeSlot();
                if (eventSlot.getStartTime().isBefore(dayEnd) && eventSlot.getEndTime().isAfter(dayStart)) {
                    boundaries.add(max(eventSlot.getStartTime(), dayStart));
                    boundaries.add(min(eventSlot.getEndTime(), dayEnd));
                }
            }
        }

        List<TimeSlot> results = new ArrayList<>();
        LocalDateTime previous = null;
        for (LocalDateTime boundary : boundaries) {
            if (previous != null && previous.isBefore(boundary)) {
                TimeSlot candidate = new TimeSlot(previous, boundary);
                if (canAttend(directUsers, teams, representatives, candidate)) addOrMerge(results, candidate);
            }
            previous = boundary;
        }
        return List.copyOf(results);
    }

    private TimeSlot validateEventRequest(String name, List<String> users, List<String> teams, int representatives,
                                          LocalDateTime start, LocalDateTime end) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Event name is required");
        if (eventMap.containsKey(name)) throw new IllegalArgumentException("Event already exists with name " + name);
        List<String> directUsers = copyDistinct(users, "Direct users");
        List<String> teamNames = copyDistinct(teams, "Teams");
        if (directUsers.isEmpty() && teamNames.isEmpty()) throw new IllegalArgumentException("At least one participant is required");
        if (!teamNames.isEmpty() && representatives <= 0) throw new IllegalArgumentException("Representatives must be positive");
        TimeSlot slot = new TimeSlot(start, end);
        if (!start.toLocalDate().equals(end.toLocalDate())) throw new IllegalArgumentException("An event must start and end on the same day");
        return slot;
    }

    private List<String> copyDistinct(List<String> names, String label) {
        if (names == null) return List.of();
        if (names.stream().anyMatch(name -> name == null || name.isBlank())) throw new IllegalArgumentException(label + " cannot contain blank names");
        LinkedHashSet<String> unique = new LinkedHashSet<>(names);
        if (unique.size() != names.size()) throw new IllegalArgumentException(label + " must not contain duplicates");
        return List.copyOf(unique);
    }

    private boolean canAttend(List<String> directUsers, List<String> teams, int representatives, TimeSlot slot) {
        for (String userName : directUsers) if (!isAvailable(userManager.getUser(userName), slot)) return false;
        for (String teamName : teams) {
            long available = teamManager.getTeam(teamName).getTeamMembers().stream().filter(user -> isAvailable(user, slot)).count();
            if (available < representatives) return false;
        }
        return true;
    }

    private void ensureAvailable(User user, TimeSlot slot) {
        if (!isAvailable(user, slot)) throw new IllegalStateException("User " + user.getName() + " is not available for " + slot.getStartTime());
    }

    private boolean isAvailable(User user, TimeSlot slot) {
        if (!slot.getStartTime().toLocalDate().equals(slot.getEndTime().toLocalDate())) return false;
        if (slot.getStartTime().toLocalTime().isBefore(user.getWorkingHoursStart())
                || slot.getEndTime().toLocalTime().isAfter(user.getWorkingHoursEnd())) return false;
        return user.getEvents().stream().noneMatch(event -> event.getTimeSlot().overlaps(slot));
    }

    private void addOrMerge(List<TimeSlot> slots, TimeSlot candidate) {
        if (!slots.isEmpty() && slots.get(slots.size() - 1).getEndTime().equals(candidate.getStartTime())) {
            TimeSlot previous = slots.remove(slots.size() - 1);
            slots.add(new TimeSlot(previous.getStartTime(), candidate.getEndTime()));
        } else slots.add(candidate);
    }

    private LocalDateTime max(LocalDateTime first, LocalDateTime second) { return first.isAfter(second) ? first : second; }
    private LocalDateTime min(LocalDateTime first, LocalDateTime second) { return first.isBefore(second) ? first : second; }
}
