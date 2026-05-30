package org.example.controllers;

import java.util.List;
import java.util.UUID;

import org.example.entities.Booking;
import org.example.entities.Show;
import org.example.entities.User;
import org.example.service.BookingService;

public class BookingController {

    private final BookingService bookingService;

    public BookingController(){
        this.bookingService = new BookingService();
    }

    public Booking createBooking(User user, Show show, List<Integer> seats){
        Booking booking = bookingService.book(user,show,seats);
        return booking;
    }

    public Booking getBooking(UUID bookingId){
        return bookingService.getBooking(bookingId);
    }

    public List<Booking> getBookingsForUser(User user){
        return bookingService.getBookingsForUser(user);
    }
}
