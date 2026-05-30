package org.example.Bill;

import org.example.Reservation.Reservation;

public interface BillingStrategy {
    Bill generateBill(Reservation reservation);
}

