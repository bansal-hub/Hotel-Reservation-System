package com.hotel.repository;

import com.hotel.model.Payment;
import com.hotel.util.FileHandler;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Repository for Payment entities.
 * Handles in-memory storage and file persistence for payments.
 * Implements the Singleton Pattern.
 */
public class PaymentRepository implements Repository<Payment, String> {

    // ─── Singleton ───────────────────────────────────────────────────────────────

    private static PaymentRepository instance;

    private PaymentRepository() {
        this.payments = new LinkedHashMap<>();
        loadFromDisk();
    }

    public static synchronized PaymentRepository getInstance() {
        if (instance == null) {
            instance = new PaymentRepository();
        }
        return instance;
    }

    // ─── Storage ─────────────────────────────────────────────────────────────────

    /** In-memory map: paymentId → Payment */
    private final Map<String, Payment> payments;

    // ─── CRUD Operations ─────────────────────────────────────────────────────────

    @Override
    public void save(Payment payment) {
        payments.put(payment.getPaymentId(), payment);
        persistToDisk();
    }

    @Override
    public Optional<Payment> findById(String paymentId) {
        return Optional.ofNullable(payments.get(paymentId));
    }

    @Override
    public List<Payment> findAll() {
        return new ArrayList<>(payments.values());
    }

    @Override
    public void deleteById(String paymentId) {
        payments.remove(paymentId);
        persistToDisk();
    }

    // ─── Domain-Specific Queries ──────────────────────────────────────────────────

    /**
     * Finds a payment associated with a specific reservation.
     *
     * @param reservationId Reservation ID to look up
     * @return Optional containing the payment if found
     */
    public Optional<Payment> findByReservationId(String reservationId) {
        return payments.values().stream()
            .filter(p -> p.getReservationId().equals(reservationId))
            .findFirst();
    }

    /**
     * Finds all successful payments.
     *
     * @return List of successful payments
     */
    public List<Payment> findSuccessfulPayments() {
        return payments.values().stream()
            .filter(p -> p.getStatus() == Payment.PaymentStatus.SUCCESS)
            .collect(Collectors.toList());
    }

    // ─── Persistence ─────────────────────────────────────────────────────────────

    @Override
    public void persistToDisk() {
        FileHandler.saveObject(new ArrayList<>(payments.values()), FileHandler.PAYMENTS_FILE);
    }

    @Override
    public void loadFromDisk() {
        List<Payment> loaded = FileHandler.loadObject(FileHandler.PAYMENTS_FILE);
        if (loaded != null) {
            for (Payment payment : loaded) {
                payments.put(payment.getPaymentId(), payment);
            }
        }
    }
}
