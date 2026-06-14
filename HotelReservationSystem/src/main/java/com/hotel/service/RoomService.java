package com.hotel.service;

import com.hotel.exception.RoomNotFoundException;
import com.hotel.exception.RoomNotAvailableException;
import com.hotel.exception.InvalidInputException;
import com.hotel.model.Room;
import com.hotel.repository.RoomRepository;
import java.util.List;

/**
 * Service class encapsulating all business logic for Room management.
 * Acts as the bridge between the Controller/UI layer and the Repository layer.
 */
public class RoomService {

    private final RoomRepository roomRepository;

    /**
     * Constructor using Dependency Injection.
     *
     * @param roomRepository The room repository to use
     */
    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    /**
     * Adds a new room to the system after validating inputs.
     *
     * @param room The room to add
     * @throws InvalidInputException if a room with the same number already exists
     */
    public void addRoom(Room room) {
        if (roomRepository.roomNumberExists(room.getRoomNumber())) {
            throw new InvalidInputException(
                "Room number " + room.getRoomNumber() + " already exists."
            );
        }
        roomRepository.save(room);
        System.out.println("  ✓ Room " + room.getRoomNumber() + " added successfully.");
    }

    /**
     * Retrieves all rooms in the system.
     *
     * @return List of all rooms
     */
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    /**
     * Retrieves all currently available rooms.
     *
     * @return List of available rooms
     */
    public List<Room> getAvailableRooms() {
        return roomRepository.findAvailableRooms();
    }

    /**
     * Retrieves available rooms by category.
     *
     * @param category Room category to filter
     * @return List of available rooms in the given category
     */
    public List<Room> getAvailableRoomsByCategory(Room.RoomCategory category) {
        return roomRepository.findAvailableRoomsByCategory(category);
    }

    /**
     * Retrieves a room by its ID.
     *
     * @param roomId The room ID to look up
     * @return The found Room
     * @throws RoomNotFoundException if no room with that ID exists
     */
    public Room getRoomById(String roomId) {
        return roomRepository.findById(roomId)
            .orElseThrow(() -> new RoomNotFoundException("Room not found with ID: " + roomId));
    }

    /**
     * Marks a room as unavailable (booked).
     *
     * @param roomId The ID of the room to mark as booked
     * @throws RoomNotFoundException    if the room doesn't exist
     * @throws RoomNotAvailableException if the room is already booked
     */
    public void markRoomAsBooked(String roomId) {
        Room room = getRoomById(roomId);
        if (!room.isAvailable()) {
            throw new RoomNotAvailableException(
                "Room " + room.getRoomNumber() + " is already booked."
            );
        }
        room.setAvailable(false);
        roomRepository.save(room);
    }

    /**
     * Marks a room as available again (after cancellation).
     *
     * @param roomId The ID of the room to free
     * @throws RoomNotFoundException if the room doesn't exist
     */
    public void markRoomAsAvailable(String roomId) {
        Room room = getRoomById(roomId);
        room.setAvailable(true);
        roomRepository.save(room);
    }

    /**
     * Displays a formatted table of available rooms.
     *
     * @param rooms List of rooms to display
     */
    public void displayRooms(List<Room> rooms) {
        if (rooms.isEmpty()) {
            System.out.println("  No rooms found.");
            return;
        }
        System.out.println("\n  ┌────────────┬────────────┬────────────┬─────────────────┬───────────┐");
        System.out.println("  │  Room ID   │   Number   │  Category  │  Price/Night    │ Available │");
        System.out.println("  ├────────────┼────────────┼────────────┼─────────────────┼───────────┤");
        for (Room r : rooms) {
            System.out.printf("  │ %-10s │ %-10s │ %-10s │ ₹%-14.2f │ %-9s │%n",
                abbreviate(r.getRoomId(), 10),
                r.getRoomNumber(),
                r.getCategory(),
                r.getPricePerNight(),
                r.isAvailable() ? "YES" : "NO"
            );
        }
        System.out.println("  └────────────┴────────────┴────────────┴─────────────────┴───────────┘");
    }

    /** Abbreviates a string to fit in a table column */
    private String abbreviate(String s, int maxLen) {
        return s.length() <= maxLen ? s : s.substring(s.length() - maxLen);
    }
}
