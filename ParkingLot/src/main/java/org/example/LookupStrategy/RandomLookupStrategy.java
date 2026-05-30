package org.example.LookupStrategy;

import org.example.entity.ParkingSpot;

import java.util.List;

public class RandomLookupStrategy implements ParkingSpotLookupStrategy {
    @Override
    public ParkingSpot selectSpot(List<ParkingSpot> spots){
        for(ParkingSpot spot: spots){
            if(spot.isSpotFree()){
                return spot;
            }
        }
        return null;
    }
}
