package com.hotel.model;

import java.io.Serializable;

/**
 * Abstract base class for all room types.
 * Demonstrates Abstraction and Encapsulation OOP principles.
 */
public abstract class Room implements Serializable {

    private static final long serialVersionUID = 1L;

    private String roomId;
    private String roomNumber;
    private RoomCategory category;
    private double pricePerNight;
    private boolean available;

    /**
     * Enum representing the category of a room.
     */
    public enum RoomCategory {
        STANDARD, DELUXE, SUITE
    }

    /**
     * Constructor for Room.
     *
     * @param roomId       Unique room identifier
     * @param roomNumber   Physical room number in the hotel
     * @param category     Room category (Standard/Deluxe/Suite)
     * @param pricePerNight Cost per night in INR
     */
    public Room(String roomId, String roomNumber, RoomCategory category, double pricePerNight) {
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.category = category;
        this.pricePerNight = pricePerNight;
        this.available = true;
    }

    /**
     * Abstract method to get a description of room amenities.
     * Each subclass must implement this method (Polymorphism).
     *
     * @return String describing the amenities of this room type
     */
    public abstract String getAmenities();

    /**
     * Abstract method to get max occupancy.
     *
     * @return Maximum number of guests allowed
     */
    public abstract int getMaxOccupancy();

    // ─── Getters ────────────────────────────────────────────────────────────────

    public String getRoomId() {
        return roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public RoomCategory getCategory() {
        return category;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public boolean isAvailable() {
        return available;
    }

    // ─── Setters ────────────────────────────────────────────────────────────────

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public void setCategory(RoomCategory category) {
        this.category = category;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return String.format(
            "Room[ID=%s, Number=%s, Category=%s, Price=%.2f/night, Available=%s]",
            roomId, roomNumber, category, pricePerNight, available ? "Yes" : "No"
        );
    }
}
