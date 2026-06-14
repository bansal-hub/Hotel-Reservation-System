package com.hotel.model;

import java.io.Serializable;

/**
 * Represents a hotel customer.
 * Demonstrates Encapsulation with private fields and public accessors.
 */
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    private String customerId;
    private String name;
    private String email;
    private String phoneNumber;

    /**
     * Full constructor for Customer.
     *
     * @param customerId  Unique customer identifier
     * @param name        Full name of the customer
     * @param email       Email address
     * @param phoneNumber Contact phone number
     */
    public Customer(String customerId, String name, String email, String phoneNumber) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    // ─── Getters ────────────────────────────────────────────────────────────────

    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    // ─── Setters ────────────────────────────────────────────────────────────────

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return String.format(
            "Customer[ID=%s, Name=%s, Email=%s, Phone=%s]",
            customerId, name, email, phoneNumber
        );
    }
}
