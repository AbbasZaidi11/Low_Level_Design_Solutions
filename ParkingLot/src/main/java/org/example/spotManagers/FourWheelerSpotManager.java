package org.example.spotManagers;

import org.example.LookupStrategy.ParkingSpotLookupStrategy;
import org.example.entity.ParkingSpot;

import java.util.List;

public class FourWheelerSpotManager extends ParkingSpotManager {

    /*
     1. Maintains a list of Four Wheeler Spots only
     2. Has its own lookup strategy
     3. Has its own lock, to avoid conflicts with other spot managers
     */
    public FourWheelerSpotManager(List<ParkingSpot> spots, ParkingSpotLookupStrategy strategy) {
        super(spots, strategy);
    }
}



