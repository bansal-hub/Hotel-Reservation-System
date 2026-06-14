package com.hotel.service;

import com.hotel.model.*;
import com.hotel.util.IDGenerator;

/**
 * Factory class for creating Room objects.
 * Demonstrates the Factory Design Pattern — centralizes object creation logic.
 */
public class RoomFactory {

    // Private constructor — static factory, not instantiable
    private RoomFactory() {}

    /**
     * Creates a new Standard room.
     *
     * @param roomNumber    Physical room number (e.g. "101")
     * @param pricePerNight Price per night in INR
     * @return A new StandardRoom instance
     */
    public static StandardRoom createStandardRoom(String roomNumber, double pricePerNight) {
        String id = IDGenerator.generateRoomId();
        return new StandardRoom(id, roomNumber, pricePerNight);
    }

    /**
     * Creates a new Deluxe room.
     *
     * @param roomNumber    Physical room number
     * @param pricePerNight Price per night in INR
     * @param hasBalcony    Whether the room has a balcony
     * @return A new DeluxeRoom instance
     */
    public static DeluxeRoom createDeluxeRoom(String roomNumber, double pricePerNight,
                                               boolean hasBalcony) {
        String id = IDGenerator.generateRoomId();
        return new DeluxeRoom(id, roomNumber, pricePerNight, hasBalcony);
    }

    /**
     * Creates a new Suite room.
     *
     * @param roomNumber     Physical room number
     * @param pricePerNight  Price per night in INR
     * @param numberOfRooms  Number of rooms within the suite
     * @return A new SuiteRoom instance
     */
    public static SuiteRoom createSuiteRoom(String roomNumber, double pricePerNight,
                                             int numberOfRooms) {
        String id = IDGenerator.generateRoomId();
        return new SuiteRoom(id, roomNumber, pricePerNight, numberOfRooms);
    }

    /**
     * Creates a Room of the specified category using a general-purpose factory method.
     * Demonstrates Polymorphism — returns the abstract Room type.
     *
     * @param category      Room category (STANDARD / DELUXE / SUITE)
     * @param roomNumber    Physical room number
     * @param pricePerNight Price per night in INR
     * @return Room of the specified category with default sub-type parameters
     */
    public static Room createRoom(Room.RoomCategory category, String roomNumber,
                                   double pricePerNight) {
        return switch (category) {
            case STANDARD -> createStandardRoom(roomNumber, pricePerNight);
            case DELUXE   -> createDeluxeRoom(roomNumber, pricePerNight, false);
            case SUITE    -> createSuiteRoom(roomNumber, pricePerNight, 2);
        };
    }
}
