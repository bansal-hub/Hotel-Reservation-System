package com.hotel.util;

import java.io.*;
import java.nio.file.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for all file I/O operations.
 * Handles serialization and deserialization of objects to/from disk.
 * Implements the data persistence layer using Java Serialization.
 */
public class FileHandler {

    private static final Logger LOGGER = Logger.getLogger(FileHandler.class.getName());

    /** Directory where all data files are stored */
    public static final String DATA_DIR = "hotel_data";

    public static final String ROOMS_FILE        = DATA_DIR + File.separator + "rooms.dat";
    public static final String CUSTOMERS_FILE    = DATA_DIR + File.separator + "customers.dat";
    public static final String RESERVATIONS_FILE = DATA_DIR + File.separator + "reservations.dat";
    public static final String PAYMENTS_FILE     = DATA_DIR + File.separator + "payments.dat";

    // Private constructor — utility class should not be instantiated
    private FileHandler() {}

    /**
     * Initializes the data directory if it does not exist.
     * Called once at application startup.
     */
    public static void initializeDataDirectory() {
        try {
            Path dirPath = Paths.get(DATA_DIR);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
                System.out.println("  Data directory created: " + DATA_DIR);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to create data directory", e);
            throw new RuntimeException("Cannot create data directory: " + e.getMessage(), e);
        }
    }

    /**
     * Serializes and saves an object to a file.
     *
     * @param object   The object to persist (must implement Serializable)
     * @param filePath Path of the file to write to
     * @throws RuntimeException if the write operation fails
     */
    public static void saveObject(Object object, String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(filePath)))) {
            oos.writeObject(object);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to save object to: " + filePath, e);
            throw new RuntimeException("Failed to save data to file: " + filePath, e);
        }
    }

    /**
     * Deserializes and loads an object from a file.
     *
     * @param filePath Path of the file to read from
     * @return The deserialized object, or null if the file does not exist
     * @throws RuntimeException if the read operation fails
     */
    @SuppressWarnings("unchecked")
    public static <T> T loadObject(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;   // No data yet — caller must handle null
        }
        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(file)))) {
            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.log(Level.WARNING, "Failed to load object from: " + filePath, e);
            // Return null so callers initialize empty collections
            return null;
        }
    }

    /**
     * Checks whether a data file exists.
     *
     * @param filePath Path to check
     * @return true if the file exists, false otherwise
     */
    public static boolean fileExists(String filePath) {
        return new File(filePath).exists();
    }

    /**
     * Deletes a data file (used for testing/reset purposes).
     *
     * @param filePath Path of the file to delete
     */
    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }
}
