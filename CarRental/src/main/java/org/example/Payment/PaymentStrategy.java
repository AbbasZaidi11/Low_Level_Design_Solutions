package org.example.Payment;

import org.example.Bill.Bill;

public interface PaymentStrategy {
    Payment processPayment(Bill bill, double paymentAmount);
}
