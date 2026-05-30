package org.example.pricing;

import org.example.Ticket;

public interface PricingStrategy {
    double calculate(Ticket ticket);
}
