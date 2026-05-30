package org.example;

import org.example.entity.ParkingSpot;
import org.example.entity.Vehicle;
import org.example.parkinglot.ParkingLevel;

import java.time.LocalDateTime;

public class Ticket {
    private final Vehicle vehicle;
    private final ParkingLevel level;
    private final ParkingSpot spot;
    private final LocalDateTime entryTime;

    public Ticket(Vehicle vehicle, ParkingLevel level, ParkingSpot spot,LocalDateTime entryTime){
        this.vehicle = vehicle;
        this.level = level;
        this.spot = spot;
        this.entryTime = entryTime;
    }
    public Vehicle getVehicle(){
        return vehicle;
    }

    public ParkingLevel getLevel(){
        return level;
    }
    public ParkingSpot getSpot(){
        return spot;
    }
    public LocalDateTime getEntryTime(){
        return entryTime;
    }
}
