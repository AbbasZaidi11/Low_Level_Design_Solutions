package org.example.parkinglot;

import org.example.Ticket;
import org.example.payment.Payment;
import org.example.pricing.CostComputation;

public class ExitGate {

    private final CostComputation costComputation;

    public ExitGate(CostComputation costComputation) {
        this.costComputation = costComputation;
    }

    public void completeExit(ParkingBuilding building,
                             Ticket ticket,
                             Payment payment) {

        double amount = calculatePrice(ticket);

        boolean success = payment.pay(amount);
        if (!success) {
            throw new RuntimeException("Payment failed. Exit denied.");
        }

        building.release(ticket);
        System.out.println("Exit successful. Gate opened.");
    }

    private double calculatePrice(Ticket ticket) {
        return costComputation.compute(ticket);
    }
}

