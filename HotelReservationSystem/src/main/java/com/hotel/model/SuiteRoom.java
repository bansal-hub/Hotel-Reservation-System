package com.hotel.model;

/**
 * Represents a Suite room type — the most luxurious room category.
 * Demonstrates Inheritance from Room abstract class.
 */
public class SuiteRoom extends Room {

    private static final long serialVersionUID = 1L;

    private int numberOfRooms;

    /**
     * Constructor for SuiteRoom.
     *
     * @param roomId        Unique room identifier
     * @param roomNumber    Physical room number
     * @param pricePerNight Cost per night in INR
     * @param numberOfRooms Number of rooms within the suite
     */
    public SuiteRoom(String roomId, String roomNumber, double pricePerNight, int numberOfRooms) {
        super(roomId, roomNumber, RoomCategory.SUITE, pricePerNight);
        this.numberOfRooms = numberOfRooms;
    }

    /**
     * Returns amenities available in a Suite.
     * Implements abstract method from Room (Polymorphism).
     */
    @Override
    public String getAmenities() {
        return String.format(
            "Wi-Fi, 65\" Smart TV, Central Air Conditioning, Full Mini-bar, Jacuzzi, " +
            "Multiple En-suite Bathrooms, Living Room, Kitchenette, 24/7 Butler Service, " +
            "Premium Room Service, Private Dining Area (%d-room suite)", numberOfRooms
        );
    }

    /**
     * Suite rooms allow up to 6 guests.
     */
    @Override
    public int getMaxOccupancy() {
        return 6;
    }

    public int getNumberOfRooms() {
        return numberOfRooms;
    }

    public void setNumberOfRooms(int numberOfRooms) {
        this.numberOfRooms = numberOfRooms;
    }

    @Override
    public String toString() {
        return String.format(
            "SuiteRoom[ID=%s, Number=%s, Price=₹%.2f/night, Rooms=%d, Available=%s]",
            getRoomId(), getRoomNumber(), getPricePerNight(), numberOfRooms, isAvailable() ? "Yes" : "No"
        );
    }
}
