package com.hotel.repository;

import com.hotel.model.Customer;
import com.hotel.util.FileHandler;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Repository for Customer entities.
 * Handles in-memory storage and file persistence for customers.
 * Implements the Singleton Pattern.
 */
public class CustomerRepository implements Repository<Customer, String> {

    // ─── Singleton ───────────────────────────────────────────────────────────────

    private static CustomerRepository instance;

    private CustomerRepository() {
        this.customers = new LinkedHashMap<>();
        loadFromDisk();
    }

    public static synchronized CustomerRepository getInstance() {
        if (instance == null) {
            instance = new CustomerRepository();
        }
        return instance;
    }

    // ─── Storage ─────────────────────────────────────────────────────────────────

    /** In-memory map: customerId → Customer */
    private final Map<String, Customer> customers;

    // ─── CRUD Operations ─────────────────────────────────────────────────────────

    @Override
    public void save(Customer customer) {
        customers.put(customer.getCustomerId(), customer);
        persistToDisk();
    }

    @Override
    public Optional<Customer> findById(String customerId) {
        return Optional.ofNullable(customers.get(customerId));
    }

    @Override
    public List<Customer> findAll() {
        return new ArrayList<>(customers.values());
    }

    @Override
    public void deleteById(String customerId) {
        customers.remove(customerId);
        persistToDisk();
    }

    // ─── Domain-Specific Queries ──────────────────────────────────────────────────

    /**
     * Finds a customer by email address.
     *
     * @param email Email to search for
     * @return Optional containing the customer if found
     */
    public Optional<Customer> findByEmail(String email) {
        return customers.values().stream()
            .filter(c -> c.getEmail().equalsIgnoreCase(email))
            .findFirst();
    }

    /**
     * Checks if an email is already registered.
     *
     * @param email Email to check
     * @return true if the email already belongs to a customer
     */
    public boolean emailExists(String email) {
        return customers.values().stream()
            .anyMatch(c -> c.getEmail().equalsIgnoreCase(email));
    }

    /**
     * Finds customers whose names contain the given string (case-insensitive).
     *
     * @param name Partial or full name to search
     * @return List of matching customers
     */
    public List<Customer> findByNameContaining(String name) {
        return customers.values().stream()
            .filter(c -> c.getName().toLowerCase().contains(name.toLowerCase()))
            .collect(Collectors.toList());
    }

    // ─── Persistence ─────────────────────────────────────────────────────────────

    @Override
    public void persistToDisk() {
        FileHandler.saveObject(new ArrayList<>(customers.values()), FileHandler.CUSTOMERS_FILE);
    }

    @Override
    public void loadFromDisk() {
        List<Customer> loaded = FileHandler.loadObject(FileHandler.CUSTOMERS_FILE);
        if (loaded != null) {
            for (Customer customer : loaded) {
                customers.put(customer.getCustomerId(), customer);
            }
        }
    }
}
