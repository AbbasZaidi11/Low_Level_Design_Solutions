package org.example;

import java.time.LocalDate;
import java.util.List;

import org.example.Bill.Bill;
import org.example.Bill.BillManager;
import org.example.Bill.BillingStrategy;
import org.example.Bill.DailyBillingStrategy;
import org.example.Payment.Payment;
import org.example.Payment.PaymentManager;
import org.example.Payment.PaymentStrategy;
import org.example.Payment.UPIPaymentStrategy;
import org.example.Product.Vehicle;
import org.example.Product.VehicleInventoryManager;
import org.example.Product.VehicleType;
import org.example.Reservation.Reservation;
import org.example.Reservation.ReservationManager;
import org.example.Reservation.ReservationType;

public class Store {
    private final int storeId;
    private final Location storeLocation;

    private final VehicleInventoryManager inventory;
    private final ReservationManager reservationManager;

    private final BillManager billManager;
    private final PaymentManager paymentManager;

    public Store(int storeId,Location location){
        this.storeId = storeId;
        this.storeLocation = location;
        this.inventory = new VehicleInventoryManager();
        this.billManager = new BillManager(new DailyBillingStrategy(inventory));
        this.paymentManager = new PaymentManager(new UPIPaymentStrategy());
        this.reservationManager = new ReservationManager(inventory);
    }
    // ----------------- Search Vehicles --------------------

    public List<Vehicle> getVehicles(VehicleType type, LocalDate from , LocalDate to){
        return inventory.getAvailableVehicles(type, from, to);
    }

    // ----------------- Create Reservation -----------------


    public Reservation createReservation(int vehicleId, User user, LocalDate from, LocalDate to, ReservationType type) throws Exception{
        return reservationManager.createReservation(vehicleId,user,from,to ,type);
    }

    // ----------------- Update Reservation -----------------


    public void cancelReservation(int reservationId){
        reservationManager.cancelReservation(reservationId);
    }

    public void startTrip(int reservationId){
        reservationManager.startTrip(reservationId);
    }

    public void submitVehicle(int reservationId){
        reservationManager.submitVehicle(reservationId);
    }

    // ----------------- Billing & Payment ------------------

    public Bill generateBill(int reservationId, BillingStrategy billingStrategy) {
        Reservation r = reservationManager.findByID(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        billManager.setBillingStrategy(billingStrategy);
        return billManager.generateBill(r);
    }


    public Payment makePayment(Bill bill, PaymentStrategy paymentStrategy, double paymentAmount) {
        paymentManager.setPaymentStrategy(paymentStrategy);
        Payment payment = paymentManager.makePayment(bill, paymentAmount);
        if (!bill.isBillPaid()) {
            throw new RuntimeException("Payment failed");
        }
        reservationManager.remove(bill.getReservationId());
        return payment;
    }

    public VehicleInventoryManager getInventory() {
        return inventory;
    }

    public int getStoreId() {
        return storeId;
    }
}
