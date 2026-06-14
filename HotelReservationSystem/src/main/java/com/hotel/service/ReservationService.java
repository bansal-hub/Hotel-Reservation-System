package com.hotel.service;

import com.hotel.exception.*;
import com.hotel.model.*;
import com.hotel.repository.*;
import com.hotel.util.IDGenerator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service class for all Reservation business logic.
 * Coordinates between RoomService, CustomerService, and PaymentService.
 * Demonstrates Composition — uses multiple service collaborators.
 */
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomService roomService;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /**
     * Constructor using Dependency Injection.
     *
     * @param reservationRepository Repository for reservations
     * @param roomService           Room service for availability management
     */
    public ReservationService(ReservationRepository reservationRepository,
                               RoomService roomService) {
        this.reservationRepository = reservationRepository;
        this.roomService = roomService;
    }

    /**
     * Creates a new reservation for a customer and room.
     *
     * @param customer       The customer making the booking
     * @param roomId         The ID of the room to book
     * @param checkInDate    Desired check-in date
     * @param numberOfNights Number of nights to stay
     * @return The newly created Reservation (status: PENDING until payment)
     * @throws RoomNotAvailableException if the room is already booked
     * @throws RoomNotFoundException     if the room ID doesn't exist
     */
    public Reservation createReservation(Customer customer, String roomId,
                                          LocalDate checkInDate, int numberOfNights) {
        Room room = roomService.getRoomById(roomId);

        if (!room.isAvailable()) {
            throw new RoomNotAvailableException(
                "Room " + room.getRoomNumber() + " is currently not available for booking."
            );
        }

        // Check for duplicate active bookings for this room (double-booking prevention)
        List<Reservation> activeBookings = reservationRepository.findActiveByRoomId(roomId);
        if (!activeBookings.isEmpty()) {
            throw new RoomNotAvailableException(
                "Room " + room.getRoomNumber() + " already has a confirmed booking."
            );
        }

        String reservationId = IDGenerator.generateReservationId();
        Reservation reservation = new Reservation(
            reservationId, customer, room, checkInDate, numberOfNights
        );

        reservationRepository.save(reservation);
        System.out.println("  ✓ Reservation created with ID: " + reservationId);
        return reservation;
    }

    /**
     * Confirms a reservation after successful payment.
     *
     * @param reservationId The reservation to confirm
     * @param payment       The completed payment
     * @throws ReservationNotFoundException if the reservation doesn't exist
     */
    public void confirmReservation(String reservationId, Payment payment) {
        Reservation reservation = getReservationById(reservationId);
        reservation.confirm(payment);

        // Mark the room as unavailable — prevents double booking
        roomService.markRoomAsBooked(reservation.getRoom().getRoomId());

        reservationRepository.save(reservation);
        System.out.println("  ✓ Reservation " + reservationId + " confirmed successfully!");
    }

    /**
     * Cancels an existing confirmed reservation.
     *
     * @param reservationId The reservation ID to cancel
     * @throws ReservationNotFoundException if not found
     * @throws InvalidInputException        if already cancelled
     */
    public Reservation cancelReservation(String reservationId) {
        Reservation reservation = getReservationById(reservationId);

        if (reservation.getStatus() == Reservation.ReservationStatus.CANCELLED) {
            throw new InvalidInputException(
                "Reservation " + reservationId + " is already cancelled."
            );
        }

        // Free the room back up
        roomService.markRoomAsAvailable(reservation.getRoom().getRoomId());

        reservation.cancel();
        reservationRepository.save(reservation);

        System.out.println("  ✓ Reservation " + reservationId + " has been cancelled.");
        return reservation;
    }

    /**
     * Retrieves a reservation by its ID.
     *
     * @param reservationId The ID to look up
     * @return The found Reservation
     * @throws ReservationNotFoundException if not found
     */
    public Reservation getReservationById(String reservationId) {
        return reservationRepository.findById(reservationId)
            .orElseThrow(() -> new ReservationNotFoundException(
                "Reservation not found with ID: " + reservationId
            ));
    }

    /**
     * Retrieves all reservations in the system.
     *
     * @return List of all reservations
     */
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    /**
     * Retrieves all reservations for a specific customer.
     *
     * @param customerId The customer ID to filter by
     * @return List of that customer's reservations
     */
    public List<Reservation> getReservationsByCustomer(String customerId) {
        return reservationRepository.findByCustomerId(customerId);
    }

    /**
     * Displays a formatted summary table of all reservations.
     *
     * @param reservations List of reservations to display
     */
    public void displayReservationsSummary(List<Reservation> reservations) {
        if (reservations.isEmpty()) {
            System.out.println("  No reservations found.");
            return;
        }
        System.out.println("\n  ┌──────────────────────┬──────────────────────┬────────┬────────────┬──────────┬───────────────┬───────────┐");
        System.out.println("  │    Reservation ID    │      Customer        │  Room  │  Check-In  │  Nights  │     Total     │  Status   │");
        System.out.println("  ├──────────────────────┼──────────────────────┼────────┼────────────┼──────────┼───────────────┼───────────┤");
        for (Reservation r : reservations) {
            System.out.printf("  │ %-20s │ %-20s │ %-6s │ %-10s │ %-8d │ ₹%-12.2f │ %-9s │%n",
                abbrev(r.getReservationId(), 20),
                truncate(r.getCustomer().getName(), 20),
                r.getRoom().getRoomNumber(),
                r.getCheckInDate().format(DATE_FMT),
                r.getNumberOfNights(),
                r.getTotalAmount(),
                r.getStatus()
            );
        }
        System.out.println("  └──────────────────────┴──────────────────────┴────────┴────────────┴──────────┴───────────────┴───────────┘");
    }

    private String truncate(String s, int maxLen) {
        return s.length() <= maxLen ? s : s.substring(0, maxLen - 2) + "..";
    }

    private String abbrev(String s, int maxLen) {
        return s.length() <= maxLen ? s : s.substring(s.length() - maxLen);
    }
}
