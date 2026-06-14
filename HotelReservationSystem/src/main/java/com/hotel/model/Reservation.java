package com.hotel.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a hotel reservation/booking.
 * Uses Composition — a Reservation HAS-A Customer, Room, and Payment.
 */
public class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Enum for reservation status */
    public enum ReservationStatus {
        CONFIRMED, CANCELLED, PENDING
    }

    private String reservationId;
    private Customer customer;
    private Room room;
    private LocalDate checkInDate;
    private int numberOfNights;
    private double totalAmount;
    private ReservationStatus status;
    private Payment payment;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /**
     * Full constructor for Reservation.
     *
     * @param reservationId  Unique reservation identifier
     * @param customer       The customer who made the booking
     * @param room           The room being reserved
     * @param checkInDate    Date of check-in
     * @param numberOfNights Number of nights to stay
     */
    public Reservation(String reservationId, Customer customer, Room room,
                       LocalDate checkInDate, int numberOfNights) {
        this.reservationId = reservationId;
        this.customer = customer;
        this.room = room;
        this.checkInDate = checkInDate;
        this.numberOfNights = numberOfNights;
        this.totalAmount = room.getPricePerNight() * numberOfNights;
        this.status = ReservationStatus.PENDING;
        this.payment = null;
    }

    /**
     * Calculates and returns the check-out date.
     *
     * @return LocalDate of check-out
     */
    public LocalDate getCheckOutDate() {
        return checkInDate.plusDays(numberOfNights);
    }

    /**
     * Confirms the reservation after successful payment.
     *
     * @param payment The payment associated with this reservation
     */
    public void confirm(Payment payment) {
        this.payment = payment;
        this.status = ReservationStatus.CONFIRMED;
    }

    /**
     * Cancels the reservation and frees the room.
     */
    public void cancel() {
        this.status = ReservationStatus.CANCELLED;
        if (this.room != null) {
            this.room.setAvailable(true);
        }
    }

    // ─── Getters ────────────────────────────────────────────────────────────────

    public String getReservationId()        { return reservationId; }
    public Customer getCustomer()           { return customer; }
    public Room getRoom()                   { return room; }
    public LocalDate getCheckInDate()       { return checkInDate; }
    public int getNumberOfNights()          { return numberOfNights; }
    public double getTotalAmount()          { return totalAmount; }
    public ReservationStatus getStatus()    { return status; }
    public Payment getPayment()             { return payment; }

    // ─── Setters ────────────────────────────────────────────────────────────────

    public void setReservationId(String id)           { this.reservationId = id; }
    public void setCustomer(Customer customer)        { this.customer = customer; }
    public void setRoom(Room room)                    { this.room = room; }
    public void setCheckInDate(LocalDate date)        { this.checkInDate = date; }
    public void setNumberOfNights(int nights)         { this.numberOfNights = nights; }
    public void setTotalAmount(double amount)         { this.totalAmount = amount; }
    public void setStatus(ReservationStatus status)   { this.status = status; }
    public void setPayment(Payment payment)           { this.payment = payment; }

    /**
     * Returns a formatted detailed view of this reservation.
     */
    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n╔══════════════════════════════════════════════════════╗\n");
        sb.append("║             RESERVATION DETAILS                     ║\n");
        sb.append("╠══════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║  Reservation ID : %-34s ║\n", reservationId));
        sb.append(String.format("║  Status         : %-34s ║\n", status));
        sb.append("╠══════════════════════════════════════════════════════╣\n");
        sb.append("║                  CUSTOMER INFO                      ║\n");
        sb.append("╠══════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║  Name           : %-34s ║\n", customer.getName()));
        sb.append(String.format("║  Email          : %-34s ║\n", customer.getEmail()));
        sb.append(String.format("║  Phone          : %-34s ║\n", customer.getPhoneNumber()));
        sb.append("╠══════════════════════════════════════════════════════╣\n");
        sb.append("║                   ROOM INFO                         ║\n");
        sb.append("╠══════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║  Room Number    : %-34s ║\n", room.getRoomNumber()));
        sb.append(String.format("║  Category       : %-34s ║\n", room.getCategory()));
        sb.append(String.format("║  Price/Night    : %-34s ║\n", String.format("₹%.2f", room.getPricePerNight())));
        sb.append("╠══════════════════════════════════════════════════════╣\n");
        sb.append("║                  BOOKING INFO                       ║\n");
        sb.append("╠══════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║  Check-in       : %-34s ║\n", checkInDate.format(DATE_FMT)));
        sb.append(String.format("║  Check-out      : %-34s ║\n", getCheckOutDate().format(DATE_FMT)));
        sb.append(String.format("║  Nights         : %-34d ║\n", numberOfNights));
        sb.append(String.format("║  Total Amount   : %-34s ║\n", String.format("₹%.2f", totalAmount)));
        if (payment != null) {
            sb.append("╠══════════════════════════════════════════════════════╣\n");
            sb.append("║                  PAYMENT INFO                       ║\n");
            sb.append("╠══════════════════════════════════════════════════════╣\n");
            sb.append(String.format("║  Payment ID     : %-34s ║\n", payment.getPaymentId()));
            sb.append(String.format("║  Method         : %-34s ║\n", payment.getMethod()));
            sb.append(String.format("║  Payment Status : %-34s ║\n", payment.getStatus()));
        }
        sb.append("╚══════════════════════════════════════════════════════╝");
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format(
            "Reservation[ID=%s, Customer=%s, Room=%s, CheckIn=%s, Nights=%d, Total=₹%.2f, Status=%s]",
            reservationId, customer.getName(), room.getRoomNumber(),
            checkInDate.format(DATE_FMT), numberOfNights, totalAmount, status
        );
    }
}
