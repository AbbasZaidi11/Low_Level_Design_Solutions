package org.example.pricing;

import org.example.Ticket;

public class FixedPricingStrategy implements PricingStrategy{
    @Override
    public double calculate(Ticket ticket){
        return 100;
    }
}
