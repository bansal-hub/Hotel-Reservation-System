package com.hotel.service;

import com.hotel.exception.CustomerNotFoundException;
import com.hotel.exception.InvalidInputException;
import com.hotel.model.Customer;
import com.hotel.repository.CustomerRepository;
import com.hotel.util.IDGenerator;
import com.hotel.util.InputValidator;

import java.util.List;

/**
 * Service class encapsulating all business logic for Customer management.
 */
public class CustomerService {

    private final CustomerRepository customerRepository;

    /**
     * Constructor using Dependency Injection.
     *
     * @param customerRepository The customer repository to use
     */
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Registers a new customer after validating inputs.
     *
     * @param name        Full name of the customer
     * @param email       Customer's email address
     * @param phoneNumber Customer's phone number
     * @return The newly created Customer
     * @throws InvalidInputException if validation fails or email is already taken
     */
    public Customer registerCustomer(String name, String email, String phoneNumber) {
        InputValidator.validateNotBlank(name, "Name");
        InputValidator.validateEmail(email);
        InputValidator.validatePhone(phoneNumber);

        if (customerRepository.emailExists(email)) {
            throw new InvalidInputException(
                "A customer with email " + email + " is already registered."
            );
        }

        String customerId = IDGenerator.generateCustomerId();
        Customer customer = new Customer(customerId, name, email, phoneNumber);
        customerRepository.save(customer);

        System.out.println("  ✓ Customer registered with ID: " + customerId);
        return customer;
    }

    /**
     * Finds an existing customer by ID, or registers them if not found.
     * Returns existing customer if email matches.
     *
     * @param name        Full name
     * @param email       Email address
     * @param phoneNumber Phone number
     * @return Existing or newly created Customer
     */
    public Customer findOrCreateCustomer(String name, String email, String phoneNumber) {
        return customerRepository.findByEmail(email)
            .orElseGet(() -> registerCustomer(name, email, phoneNumber));
    }

    /**
     * Retrieves a customer by their ID.
     *
     * @param customerId The customer ID
     * @return The found Customer
     * @throws CustomerNotFoundException if not found
     */
    public Customer getCustomerById(String customerId) {
        return customerRepository.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException(
                "Customer not found with ID: " + customerId
            ));
    }

    /**
     * Retrieves a customer by their email address.
     *
     * @param email The email to search
     * @return The found Customer
     * @throws CustomerNotFoundException if not found
     */
    public Customer getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email)
            .orElseThrow(() -> new CustomerNotFoundException(
                "No customer registered with email: " + email
            ));
    }

    /**
     * Retrieves all customers in the system.
     *
     * @return List of all customers
     */
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    /**
     * Displays a formatted table of customers.
     *
     * @param customers List of customers to display
     */
    public void displayCustomers(List<Customer> customers) {
        if (customers.isEmpty()) {
            System.out.println("  No customers found.");
            return;
        }
        System.out.println("\n  ┌─────────────────────┬──────────────────────┬──────────────────────────┬──────────────┐");
        System.out.println("  │     Customer ID     │         Name         │          Email           │    Phone     │");
        System.out.println("  ├─────────────────────┼──────────────────────┼──────────────────────────┼──────────────┤");
        for (Customer c : customers) {
            System.out.printf("  │ %-19s │ %-20s │ %-24s │ %-12s │%n",
                abbreviate(c.getCustomerId(), 19),
                truncate(c.getName(), 20),
                truncate(c.getEmail(), 24),
                c.getPhoneNumber()
            );
        }
        System.out.println("  └─────────────────────┴──────────────────────┴──────────────────────────┴──────────────┘");
    }

    private String truncate(String s, int maxLen) {
        return s.length() <= maxLen ? s : s.substring(0, maxLen - 2) + "..";
    }

    private String abbreviate(String s, int maxLen) {
        return s.length() <= maxLen ? s : s.substring(s.length() - maxLen);
    }
}
