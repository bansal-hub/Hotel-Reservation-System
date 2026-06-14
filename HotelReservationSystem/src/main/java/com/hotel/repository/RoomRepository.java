package com.hotel.repository;

import com.hotel.model.Room;
import com.hotel.util.FileHandler;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Repository for Room entities.
 * Handles in-memory storage and file persistence for rooms.
 * Implements the Singleton Pattern — only one instance exists.
 */
public class RoomRepository implements Repository<Room, String> {

    // ─── Singleton ───────────────────────────────────────────────────────────────

    private static RoomRepository instance;

    private RoomRepository() {
        this.rooms = new LinkedHashMap<>();
        loadFromDisk();
    }

    /**
     * Returns the single instance of RoomRepository (Singleton Pattern).
     *
     * @return The shared RoomRepository instance
     */
    public static synchronized RoomRepository getInstance() {
        if (instance == null) {
            instance = new RoomRepository();
        }
        return instance;
    }

    // ─── Storage ─────────────────────────────────────────────────────────────────

    /** In-memory map: roomId → Room */
    private final Map<String, Room> rooms;

    // ─── CRUD Operations ─────────────────────────────────────────────────────────

    @Override
    public void save(Room room) {
        rooms.put(room.getRoomId(), room);
        persistToDisk();
    }

    @Override
    public Optional<Room> findById(String roomId) {
        return Optional.ofNullable(rooms.get(roomId));
    }

    @Override
    public List<Room> findAll() {
        return new ArrayList<>(rooms.values());
    }

    @Override
    public void deleteById(String roomId) {
        rooms.remove(roomId);
        persistToDisk();
    }

    // ─── Domain-Specific Queries ──────────────────────────────────────────────────

    /**
     * Finds all available rooms (availability = true).
     *
     * @return List of available rooms
     */
    public List<Room> findAvailableRooms() {
        return rooms.values().stream()
            .filter(Room::isAvailable)
            .collect(Collectors.toList());
    }

    /**
     * Finds available rooms by category.
     *
     * @param category The room category to filter by
     * @return List of available rooms in the given category
     */
    public List<Room> findAvailableRoomsByCategory(Room.RoomCategory category) {
        return rooms.values().stream()
            .filter(Room::isAvailable)
            .filter(r -> r.getCategory() == category)
            .collect(Collectors.toList());
    }

    /**
     * Finds a room by its room number.
     *
     * @param roomNumber The room number to search
     * @return Optional containing the room if found
     */
    public Optional<Room> findByRoomNumber(String roomNumber) {
        return rooms.values().stream()
            .filter(r -> r.getRoomNumber().equalsIgnoreCase(roomNumber))
            .findFirst();
    }

    /**
     * Checks if a room number already exists (to prevent duplicates).
     *
     * @param roomNumber Room number to check
     * @return true if the room number is already taken
     */
    public boolean roomNumberExists(String roomNumber) {
        return rooms.values().stream()
            .anyMatch(r -> r.getRoomNumber().equalsIgnoreCase(roomNumber));
    }

    // ─── Persistence ─────────────────────────────────────────────────────────────

    @Override
    public void persistToDisk() {
        FileHandler.saveObject(new ArrayList<>(rooms.values()), FileHandler.ROOMS_FILE);
    }

    @Override
    public void loadFromDisk() {
        List<Room> loaded = FileHandler.loadObject(FileHandler.ROOMS_FILE);
        if (loaded != null) {
            for (Room room : loaded) {
                rooms.put(room.getRoomId(), room);
            }
        }
    }
}
