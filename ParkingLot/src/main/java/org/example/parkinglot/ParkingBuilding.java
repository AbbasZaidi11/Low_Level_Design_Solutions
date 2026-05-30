package org.example.parkinglot;

import org.example.Ticket;
import org.example.entity.ParkingSpot;
import org.example.entity.Vehicle;
import org.example.pricing.CostComputation;

import java.time.LocalDateTime;
import java.util.List;

public class ParkingBuilding {
    private final List<ParkingLevel> levels;

    public ParkingBuilding(List<ParkingLevel> levels, CostComputation costComputation){
        this.levels = levels;
    }

    Ticket allocate(Vehicle vehicle){
        for(ParkingLevel level: levels){
            if(level.hasAvailability(vehicle.getVehicleType())){
                ParkingSpot spot = level.park(vehicle.getVehicleType());
                if(spot!=null){
                    Ticket ticket = new Ticket(vehicle,level,spot, LocalDateTime.now());
                    System.out.println("Parking allocated at level: "
                    + level.getLevelNumber()
                    + " spot: "+spot.getSpotId());
                    return ticket;
                }
            }
        }
        throw new RuntimeException("Parking Full");
    }

    void release(Ticket ticket){
        ticket.getLevel().unPark(
                ticket.getVehicle().getVehicleType(),
                ticket.getSpot()
        );
    }
}
