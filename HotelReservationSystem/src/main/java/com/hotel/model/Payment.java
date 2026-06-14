package com.hotel.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Abstract base class for all payment types.
 * Demonstrates Abstraction and Polymorphism.
 */
public abstract class Payment implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Enum for supported payment methods */
    public enum PaymentMethod {
        CREDIT_CARD, UPI, CASH
    }

    /** Enum for payment status */
    public enum PaymentStatus {
        SUCCESS, FAILED, PENDING
    }

    private String paymentId;
    private String reservationId;
    private double amount;
    private PaymentMethod method;
    private PaymentStatus status;
    private LocalDateTime paymentDateTime;

    /**
     * Base constructor for Payment.
     *
     * @param paymentId     Unique payment identifier
     * @param reservationId Associated reservation ID
     * @param amount        Amount to be paid in INR
     * @param method        Payment method chosen
     */
    public Payment(String paymentId, String reservationId, double amount, PaymentMethod method) {
        this.paymentId = paymentId;
        this.reservationId = reservationId;
        this.amount = amount;
        this.method = method;
        this.status = PaymentStatus.PENDING;
        this.paymentDateTime = LocalDateTime.now();
    }

    /**
     * Abstract method to process the payment.
     * Subclasses implement their own processing logic (Polymorphism).
     *
     * @return true if payment succeeded, false otherwise
     */
    public abstract boolean processPayment();

    /**
     * Abstract method to get a payment receipt description.
     *
     * @return String receipt/description
     */
    public abstract String getPaymentDescription();

    // ─── Getters ────────────────────────────────────────────────────────────────

    public String getPaymentId()      { return paymentId; }
    public String getReservationId()  { return reservationId; }
    public double getAmount()         { return amount; }
    public PaymentMethod getMethod()  { return method; }
    public PaymentStatus getStatus()  { return status; }
    public LocalDateTime getPaymentDateTime() { return paymentDateTime; }

    // ─── Setters ────────────────────────────────────────────────────────────────

    public void setPaymentId(String paymentId)           { this.paymentId = paymentId; }
    public void setReservationId(String reservationId)   { this.reservationId = reservationId; }
    public void setAmount(double amount)                 { this.amount = amount; }
    public void setMethod(PaymentMethod method)          { this.method = method; }
    public void setStatus(PaymentStatus status)          { this.status = status; }
    public void setPaymentDateTime(LocalDateTime dt)     { this.paymentDateTime = dt; }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return String.format(
            "Payment[ID=%s, ReservationID=%s, Amount=₹%.2f, Method=%s, Status=%s, DateTime=%s]",
            paymentId, reservationId, amount, method, status,
            paymentDateTime != null ? paymentDateTime.format(fmt) : "N/A"
        );
    }

    // ─── Inner concrete payment classes ─────────────────────────────────────────

    /**
     * Credit Card payment implementation.
     */
    public static class CreditCardPayment extends Payment {

        private static final long serialVersionUID = 1L;
        private String maskedCardNumber;

        public CreditCardPayment(String paymentId, String reservationId,
                                  double amount, String maskedCardNumber) {
            super(paymentId, reservationId, amount, PaymentMethod.CREDIT_CARD);
            this.maskedCardNumber = maskedCardNumber;
        }

        /**
         * Simulates credit card payment processing.
         * 90% success rate simulation.
         */
        @Override
        public boolean processPayment() {
            // Simulate processing delay and 90% success rate
            boolean success = Math.random() > 0.10;
            setStatus(success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED);
            return success;
        }

        @Override
        public String getPaymentDescription() {
            return String.format("Credit Card payment of ₹%.2f via card ending in %s",
                getAmount(), maskedCardNumber);
        }

        public String getMaskedCardNumber() { return maskedCardNumber; }
        public void setMaskedCardNumber(String n) { this.maskedCardNumber = n; }
    }

    /**
     * UPI payment implementation.
     */
    public static class UpiPayment extends Payment {

        private static final long serialVersionUID = 1L;
        private String upiId;

        public UpiPayment(String paymentId, String reservationId,
                           double amount, String upiId) {
            super(paymentId, reservationId, amount, PaymentMethod.UPI);
            this.upiId = upiId;
        }

        /**
         * Simulates UPI payment processing.
         * 95% success rate simulation.
         */
        @Override
        public boolean processPayment() {
            boolean success = Math.random() > 0.05;
            setStatus(success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED);
            return success;
        }

        @Override
        public String getPaymentDescription() {
            return String.format("UPI payment of ₹%.2f via UPI ID: %s", getAmount(), upiId);
        }

        public String getUpiId() { return upiId; }
        public void setUpiId(String upiId) { this.upiId = upiId; }
    }

    /**
     * Cash payment implementation.
     */
    public static class CashPayment extends Payment {

        private static final long serialVersionUID = 1L;

        public CashPayment(String paymentId, String reservationId, double amount) {
            super(paymentId, reservationId, amount, PaymentMethod.CASH);
        }

        /**
         * Cash payments always succeed (handled at the front desk).
         */
        @Override
        public boolean processPayment() {
            setStatus(PaymentStatus.SUCCESS);
            return true;
        }

        @Override
        public String getPaymentDescription() {
            return String.format("Cash payment of ₹%.2f received at front desk", getAmount());
        }
    }
}
