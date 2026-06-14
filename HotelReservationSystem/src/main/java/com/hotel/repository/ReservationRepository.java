package com.hotel.repository;

import com.hotel.model.Reservation;
import com.hotel.util.FileHandler;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Repository for Reservation entities.
 * Handles in-memory storage and file persistence for reservations.
 * Implements the Singleton Pattern.
 */
public class ReservationRepository implements Repository<Reservation, String> {

    // ─── Singleton ───────────────────────────────────────────────────────────────

    private static ReservationRepository instance;

    private ReservationRepository() {
        this.reservations = new LinkedHashMap<>();
        loadFromDisk();
    }

    public static synchronized ReservationRepository getInstance() {
        if (instance == null) {
            instance = new ReservationRepository();
        }
        return instance;
    }

    // ─── Storage ─────────────────────────────────────────────────────────────────

    /** In-memory map: reservationId → Reservation */
    private final Map<String, Reservation> reservations;

    // ─── CRUD Operations ─────────────────────────────────────────────────────────

    @Override
    public void save(Reservation reservation) {
        reservations.put(reservation.getReservationId(), reservation);
        persistToDisk();
    }

    @Override
    public Optional<Reservation> findById(String reservationId) {
        return Optional.ofNullable(reservations.get(reservationId));
    }

    @Override
    public List<Reservation> findAll() {
        return new ArrayList<>(reservations.values());
    }

    @Override
    public void deleteById(String reservationId) {
        reservations.remove(reservationId);
        persistToDisk();
    }

    // ─── Domain-Specific Queries ──────────────────────────────────────────────────

    /**
     * Finds all reservations made by a specific customer.
     *
     * @param customerId Customer ID to filter by
     * @return List of reservations for that customer
     */
    public List<Reservation> findByCustomerId(String customerId) {
        return reservations.values().stream()
            .filter(r -> r.getCustomer().getCustomerId().equals(customerId))
            .collect(Collectors.toList());
    }

    /**
     * Finds all active (CONFIRMED) reservations for a specific room.
     *
     * @param roomId Room ID to filter by
     * @return List of confirmed reservations for that room
     */
    public List<Reservation> findActiveByRoomId(String roomId) {
        return reservations.values().stream()
            .filter(r -> r.getRoom().getRoomId().equals(roomId))
            .filter(r -> r.getStatus() == Reservation.ReservationStatus.CONFIRMED)
            .collect(Collectors.toList());
    }

    /**
     * Finds all confirmed reservations.
     *
     * @return List of all confirmed reservations
     */
    public List<Reservation> findConfirmed() {
        return reservations.values().stream()
            .filter(r -> r.getStatus() == Reservation.ReservationStatus.CONFIRMED)
            .collect(Collectors.toList());
    }

    // ─── Persistence ─────────────────────────────────────────────────────────────

    @Override
    public void persistToDisk() {
        FileHandler.saveObject(new ArrayList<>(reservations.values()), FileHandler.RESERVATIONS_FILE);
    }

    @Override
    public void loadFromDisk() {
        List<Reservation> loaded = FileHandler.loadObject(FileHandler.RESERVATIONS_FILE);
        if (loaded != null) {
            for (Reservation res : loaded) {
                reservations.put(res.getReservationId(), res);
            }
        }
    }
}
