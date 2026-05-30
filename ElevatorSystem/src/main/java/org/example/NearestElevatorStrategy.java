package org.example;

import java.util.List;

public class NearestElevatorStrategy implements ElevatorSelectionStrategy{

    @Override
    public ElevatorController selectElevator(List<ElevatorController> controllers,
                                             int requestFloor,
                                             ElevatorDirection direction) {
        ElevatorController best = null;
        int minDistance = Integer.MAX_VALUE;

        for(ElevatorController controller: controllers){
            int nextFloorStoppage = controller.elevatorCar.nextFloorStoppage;

            boolean isSameDirectionCandidate =
                    controller.elevatorCar.movingDirection == direction &&
                            ((direction == ElevatorDirection.UP && nextFloorStoppage <= requestFloor) ||
                                    (direction == ElevatorDirection.DOWN && nextFloorStoppage >= requestFloor));

            int dist = Math.abs(nextFloorStoppage - requestFloor);

            if(isSameDirectionCandidate && dist<minDistance){
                minDistance = dist;
                best = controller;
            }

        }
        if(best == null){
            for(ElevatorController controller: controllers){
                if(controller.elevatorCar.movingDirection == ElevatorDirection.IDLE){
                    best = controller;
                    break;
                }
            }
            if(best == null){
                best = controllers.get(0);
            }
        }
        return best;
    }
}
