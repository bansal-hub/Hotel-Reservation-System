package com.hotel.service;

import com.hotel.exception.PaymentFailedException;
import com.hotel.model.Payment;
import com.hotel.repository.PaymentRepository;
import com.hotel.util.IDGenerator;
import com.hotel.util.InputValidator;

import java.util.Scanner;

/**
 * Service class for processing payments.
 * Demonstrates Polymorphism — processes different Payment subtypes uniformly.
 */
public class PaymentService {

    private final PaymentRepository paymentRepository;

    /**
     * Constructor using Dependency Injection.
     *
     * @param paymentRepository The payment repository to use
     */
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    /**
     * Initiates and processes a payment for a reservation.
     * Collects payment details from the user and delegates to the appropriate
     * payment type (Factory + Polymorphism).
     *
     * @param reservationId The reservation being paid for
     * @param amount        Total amount due
     * @param method        Payment method chosen by the user
     * @param scanner       Scanner for reading additional input
     * @return The completed Payment object
     * @throws PaymentFailedException if the payment processing fails
     */
    public Payment processPayment(String reservationId, double amount,
                                   Payment.PaymentMethod method, Scanner scanner) {

        String paymentId = IDGenerator.generatePaymentId();
        Payment payment;

        System.out.println("\n  ─── Payment Processing ───────────────────────────────");
        System.out.printf("  Amount Due : ₹%.2f%n", amount);
        System.out.println("  Method     : " + method);
        System.out.println();

        // Build the correct Payment subtype based on chosen method (Factory + Polymorphism)
        payment = switch (method) {
            case CREDIT_CARD -> buildCreditCardPayment(paymentId, reservationId, amount, scanner);
            case UPI         -> buildUpiPayment(paymentId, reservationId, amount, scanner);
            case CASH        -> new Payment.CashPayment(paymentId, reservationId, amount);
        };

        // Simulate processing animation
        System.out.print("  Processing payment");
        for (int i = 0; i < 3; i++) {
            try { Thread.sleep(500); } catch (InterruptedException ignored) {}
            System.out.print(".");
        }
        System.out.println();

        // Execute the payment (each subtype has its own logic — Polymorphism)
        boolean success = payment.processPayment();

        if (success) {
            System.out.println("  ✓ Payment SUCCESSFUL! Payment ID: " + paymentId);
            System.out.println("  " + payment.getPaymentDescription());
            paymentRepository.save(payment);
            return payment;
        } else {
            System.out.println("  ✗ Payment FAILED. Please try again or use a different method.");
            throw new PaymentFailedException(
                "Payment processing failed for reservation: " + reservationId
            );
        }
    }

    /**
     * Simulates a refund when a reservation is cancelled.
     *
     * @param reservationId The reservation being cancelled
     * @param amount        Amount to be refunded
     */
    public void simulateRefund(String reservationId, double amount) {
        System.out.println("\n  ─── Refund Simulation ────────────────────────────────");
        System.out.printf("  Refund Amount : ₹%.2f%n", amount);
        System.out.println("  Processing refund...");
        try { Thread.sleep(800); } catch (InterruptedException ignored) {}
        System.out.println("  ✓ Refund of ₹" + String.format("%.2f", amount) +
                           " will be credited within 5-7 business days.");
    }

    // ─── Private helpers ─────────────────────────────────────────────────────────

    private Payment.CreditCardPayment buildCreditCardPayment(String paymentId, String reservationId,
                                                              double amount, Scanner scanner) {
        System.out.print("  Enter 16-digit card number : ");
        String cardNumber = scanner.nextLine().trim();
        InputValidator.validateCardNumber(cardNumber);
        String masked = InputValidator.maskCardNumber(cardNumber);
        System.out.print("  Enter cardholder name      : ");
        scanner.nextLine(); // Just consume — not stored for security
        System.out.print("  Enter CVV (3 digits)       : ");
        scanner.nextLine(); // Consume — not stored
        System.out.println("  Card details accepted (" + masked + ")");
        return new Payment.CreditCardPayment(paymentId, reservationId, amount, masked);
    }

    private Payment.UpiPayment buildUpiPayment(String paymentId, String reservationId,
                                                double amount, Scanner scanner) {
        System.out.print("  Enter UPI ID (e.g. name@upi) : ");
        String upiId = scanner.nextLine().trim();
        InputValidator.validateUpiId(upiId);
        System.out.println("  UPI ID accepted: " + upiId);
        return new Payment.UpiPayment(paymentId, reservationId, amount, upiId);
    }
}
