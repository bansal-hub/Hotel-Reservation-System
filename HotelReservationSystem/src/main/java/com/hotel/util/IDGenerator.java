package com.hotel.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility class for generating unique IDs for all entities.
 * Uses timestamp + sequential counter for collision-free IDs.
 */
public class IDGenerator {

    private static final AtomicInteger roomCounter        = new AtomicInteger(1);
    private static final AtomicInteger customerCounter    = new AtomicInteger(1);
    private static final AtomicInteger reservationCounter = new AtomicInteger(1);
    private static final AtomicInteger paymentCounter     = new AtomicInteger(1);

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("yyyyMMddHHmm");

    // Private constructor — utility class should not be instantiated
    private IDGenerator() {}

    /**
     * Generates a unique Room ID.
     * Format: RM-<timestamp>-<counter>
     *
     * @return Unique room ID string
     */
    public static String generateRoomId() {
        return "RM-" + LocalDateTime.now().format(FMT) + "-" + roomCounter.getAndIncrement();
    }

    /**
     * Generates a unique Customer ID.
     * Format: CUST-<timestamp>-<counter>
     *
     * @return Unique customer ID string
     */
    public static String generateCustomerId() {
        return "CUST-" + LocalDateTime.now().format(FMT) + "-" + customerCounter.getAndIncrement();
    }

    /**
     * Generates a unique Reservation ID.
     * Format: RES-<timestamp>-<counter>
     *
     * @return Unique reservation ID string
     */
    public static String generateReservationId() {
        return "RES-" + LocalDateTime.now().format(FMT) + "-" + reservationCounter.getAndIncrement();
    }

    /**
     * Generates a unique Payment ID.
     * Format: PAY-<timestamp>-<counter>
     *
     * @return Unique payment ID string
     */
    public static String generatePaymentId() {
        return "PAY-" + LocalDateTime.now().format(FMT) + "-" + paymentCounter.getAndIncrement();
    }

    /**
     * Resets all counters — used primarily for testing.
     */
    public static void resetCounters() {
        roomCounter.set(1);
        customerCounter.set(1);
        reservationCounter.set(1);
        paymentCounter.set(1);
    }
}
