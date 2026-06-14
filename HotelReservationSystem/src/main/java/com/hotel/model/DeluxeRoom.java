package com.hotel.model;

/**
 * Represents a Deluxe room type.
 * Demonstrates Inheritance from Room abstract class.
 */
public class DeluxeRoom extends Room {

    private static final long serialVersionUID = 1L;

    private boolean hasBalcony;

    /**
     * Constructor for DeluxeRoom.
     *
     * @param roomId        Unique room identifier
     * @param roomNumber    Physical room number
     * @param pricePerNight Cost per night in INR
     * @param hasBalcony    Whether the room includes a balcony
     */
    public DeluxeRoom(String roomId, String roomNumber, double pricePerNight, boolean hasBalcony) {
        super(roomId, roomNumber, RoomCategory.DELUXE, pricePerNight);
        this.hasBalcony = hasBalcony;
    }

    /**
     * Returns amenities available in a Deluxe room.
     * Implements abstract method from Room (Polymorphism).
     */
    @Override
    public String getAmenities() {
        String base = "Wi-Fi, Smart TV, Air Conditioning, Mini-bar, En-suite Bathroom, " +
                      "Bathtub, Daily Housekeeping, Room Service";
        return hasBalcony ? base + ", Private Balcony" : base;
    }

    /**
     * Deluxe rooms allow up to 3 guests.
     */
    @Override
    public int getMaxOccupancy() {
        return 3;
    }

    public boolean isHasBalcony() {
        return hasBalcony;
    }

    public void setHasBalcony(boolean hasBalcony) {
        this.hasBalcony = hasBalcony;
    }

    @Override
    public String toString() {
        return String.format(
            "DeluxeRoom[ID=%s, Number=%s, Price=₹%.2f/night, Balcony=%s, Available=%s]",
            getRoomId(), getRoomNumber(), getPricePerNight(),
            hasBalcony ? "Yes" : "No", isAvailable() ? "Yes" : "No"
        );
    }
}
