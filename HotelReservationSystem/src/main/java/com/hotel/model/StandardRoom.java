package com.hotel.model;

/**
 * Represents a Standard room type.
 * Demonstrates Inheritance from Room abstract class.
 */
public class StandardRoom extends Room {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor for StandardRoom.
     *
     * @param roomId       Unique room identifier
     * @param roomNumber   Physical room number
     * @param pricePerNight Cost per night in INR
     */
    public StandardRoom(String roomId, String roomNumber, double pricePerNight) {
        super(roomId, roomNumber, RoomCategory.STANDARD, pricePerNight);
    }

    /**
     * Returns amenities available in a Standard room.
     * Implements abstract method from Room (Polymorphism).
     */
    @Override
    public String getAmenities() {
        return "Wi-Fi, TV, Air Conditioning, En-suite Bathroom, Daily Housekeeping";
    }

    /**
     * Standard rooms allow up to 2 guests.
     */
    @Override
    public int getMaxOccupancy() {
        return 2;
    }

    @Override
    public String toString() {
        return String.format(
            "StandardRoom[ID=%s, Number=%s, Price=₹%.2f/night, Available=%s]",
            getRoomId(), getRoomNumber(), getPricePerNight(), isAvailable() ? "Yes" : "No"
        );
    }
}
