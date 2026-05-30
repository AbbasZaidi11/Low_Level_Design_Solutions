package org.example;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        int totalFloors = 10;
        int elevatorCount = 2;

        List<ElevatorController> controllers = new ArrayList<>();
        for (int i = 1; i <= elevatorCount; i++) {
            ElevatorCar car = new ElevatorCar(i);
            car.setCurrentFloor(i);
            ElevatorController controller = new ElevatorController(car);
            controllers.add(controller);

            Thread controllerThread = new Thread(controller, "elevator-controller-" + i);
            controllerThread.setDaemon(true);
            controllerThread.start();
        }

        ElevatorScheduler scheduler = new ElevatorScheduler(controllers, new NearestElevatorStrategy());
        ExternalDispatcher externalDispatcher = new ExternalDispatcher(scheduler);
        Building building = new Building(totalFloors, externalDispatcher);

        building.getFloor(3).pressUpButton();
        building.getFloor(7).pressDownButton();

        InternalButton carOnePanel = new InternalButton(controllers.get(0));
        carOnePanel.pressButton(9);

        Thread.sleep(200);
        scheduler.setStrategy(new LeastBusyStrategy());
        building.getFloor(2).pressUpButton();
        building.getFloor(8).pressDownButton();

        Thread.sleep(1500);
        System.out.println("Simulation complete.");
    }
}
