package org.example.models;

import java.time.LocalDateTime;
import java.util.Objects;

public final class TimeSlot implements Comparable<TimeSlot> {
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    public TimeSlot(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = Objects.requireNonNull(startTime, "Start time is required");
        this.endTime = Objects.requireNonNull(endTime, "End time is required");
        if (!startTime.isBefore(endTime)) throw new IllegalArgumentException("Start time must be before end time");
    }

    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }

    public boolean overlaps(TimeSlot other) {
        return startTime.isBefore(other.endTime) && other.startTime.isBefore(endTime);
    }

    @Override
    public int compareTo(TimeSlot other) {
        int byStart = startTime.compareTo(other.startTime);
        return byStart != 0 ? byStart : endTime.compareTo(other.endTime);
    }
}
