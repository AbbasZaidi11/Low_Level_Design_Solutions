package org.example.parkinglot;

import org.example.Ticket;
import org.example.entity.Vehicle;

public class EntranceGate {
    public Ticket enter(ParkingBuilding building, Vehicle vehicle) {
        return building.allocate(vehicle);
    }
}



