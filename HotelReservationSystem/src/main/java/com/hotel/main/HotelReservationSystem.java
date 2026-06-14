package com.hotel.main;
import com.hotel.exception.*;
import com.hotel.model.*;
import com.hotel.repository.*;
import com.hotel.service.*;
import com.hotel.util.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Main entry point for the Hotel Reservation System.
 * Implements the console menu and orchestrates all service interactions.
 *
 * Architecture:
 *   UI (this class) → Service Layer → Repository Layer → File Persistence
 */
public class HotelReservationSystem {

    //  Services (injected at startup)
    private final RoomService roomService;
    private final CustomerService customerService;
    private final ReservationService reservationService;
    private final PaymentService paymentService;

    private final Scanner scanner;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    // Constructor

    public HotelReservationSystem() {
        // Initialize data directory for file persistence
        FileHandler.initializeDataDirectory();

        // Instantiate repositories (Singleton pattern internally)
        RoomRepository        roomRepo        = RoomRepository.getInstance();
        CustomerRepository    customerRepo    = CustomerRepository.getInstance();
        ReservationRepository reservationRepo = ReservationRepository.getInstance();
        PaymentRepository     paymentRepo     = PaymentRepository.getInstance();

        // Wire services with their repositories (Dependency Injection)
        this.roomService        = new RoomService(roomRepo);
        this.customerService    = new CustomerService(customerRepo);
        this.reservationService = new ReservationService(reservationRepo, roomService);
        this.paymentService     = new PaymentService(paymentRepo);

        this.scanner = new Scanner(System.in);

        // Seed sample data only on first run
        seedSampleDataIfEmpty();
    }

    // Application Entry Point

    public static void main(String[] args) {
        HotelReservationSystem app = new HotelReservationSystem();
        app.run();
    }

    //  Main Loop

    private void run() {
        printWelcomeBanner();
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readIntSafe(1, 8);
            switch (choice) {
                case 1 -> viewAllAvailableRooms();
                case 2 -> searchRoomByCategory();
                case 3 -> bookRoom();
                case 4 -> cancelReservation();
                case 5 -> viewReservationDetails();
                case 6 -> viewAllReservations();
                case 7 -> viewAllCustomers();
                case 8 -> { running = false; printGoodbye(); }
            }
        }
        scanner.close();
    }

    // Menu Handlers

    /**
     * Handler: View All Available Rooms
     */
    private void viewAllAvailableRooms() {
        printSectionHeader("ALL AVAILABLE ROOMS");
        List<Room> rooms = roomService.getAvailableRooms();
        if (rooms.isEmpty()) {
            System.out.println("  No rooms are currently available.");
        } else {
            roomService.displayRooms(rooms);
            System.out.println("\n  Total available rooms: " + rooms.size());
        }
        pressEnterToContinue();
    }

    /**
     * Handler: Search Room by Category
     */
    private void searchRoomByCategory() {
        printSectionHeader("SEARCH ROOMS BY CATEGORY");
        System.out.println("  Select Category:");
        System.out.println("  1. Standard  (Budget-friendly, up to 2 guests)");
        System.out.println("  2. Deluxe    (Premium amenities, up to 3 guests)");
        System.out.println("  3. Suite     (Luxury experience, up to 6 guests)");
        System.out.print("\n  Enter choice (1-3): ");
        int choice = readIntSafe(1, 3);

        Room.RoomCategory category = switch (choice) {
            case 1 -> Room.RoomCategory.STANDARD;
            case 2 -> Room.RoomCategory.DELUXE;
            default -> Room.RoomCategory.SUITE;
        };

        List<Room> rooms = roomService.getAvailableRoomsByCategory(category);
        System.out.println("\n  Available " + category + " Rooms:");
        roomService.displayRooms(rooms);

        if (!rooms.isEmpty()) {
            // Show amenities for the first result as a sample
            Room sample = rooms.get(0);
            System.out.println("\n  ★ " + category + " Room Amenities:");
            System.out.println("    " + sample.getAmenities());
            System.out.println("    Max Occupancy: " + sample.getMaxOccupancy() + " guests");
        }
        pressEnterToContinue();
    }

    /**
     * Handler: Book a Room (full booking flow)
     */
    private void bookRoom() {
        printSectionHeader("BOOK A ROOM");

        try {
            // Step 1: Show available rooms
            List<Room> available = roomService.getAvailableRooms();
            if (available.isEmpty()) {
                System.out.println("  Sorry, no rooms are currently available.");
                pressEnterToContinue();
                return;
            }
            System.out.println("  Available Rooms:");
            roomService.displayRooms(available);

            // Step 2: Select room by room number
            System.out.print("\n  Enter room number to book: ");
            String roomNumber = scanner.nextLine().trim();

            Room selectedRoom = available.stream()
                .filter(r -> r.getRoomNumber().equalsIgnoreCase(roomNumber))
                .findFirst()
                .orElseThrow(() -> new RoomNotFoundException(
                    "No available room found with number: " + roomNumber
                ));

            System.out.println("\n  Selected: " + selectedRoom);
            System.out.println("  Amenities: " + selectedRoom.getAmenities());

            // Step 3: Customer details
            System.out.println("\n  ─── Customer Details ─────────────────────────────────");
            System.out.print("  Full Name     : ");
            String name = scanner.nextLine().trim();
            System.out.print("  Email Address : ");
            String email = scanner.nextLine().trim();
            System.out.print("  Phone Number  : ");
            String phone = scanner.nextLine().trim();

            Customer customer = customerService.findOrCreateCustomer(name, email, phone);
            System.out.println("  Customer ID   : " + customer.getCustomerId());

            // Step 4: Check-in date and nights
            System.out.println("\n  ─── Booking Details ──────────────────────────────────");
            LocalDate checkIn = readDate("  Check-in date (dd-MM-yyyy) [leave blank for today]: ");
            System.out.print("  Number of nights             : ");
            int nights = readIntSafe(1, 365);
            InputValidator.validateNights(nights);

            double total = selectedRoom.getPricePerNight() * nights;
            LocalDate checkOut = checkIn.plusDays(nights);

            System.out.println("\n  ─── Booking Summary ──────────────────────────────────");
            System.out.println("  Customer   : " + customer.getName());
            System.out.printf("  Room       : %s (%s)%n", selectedRoom.getRoomNumber(), selectedRoom.getCategory());
            System.out.println("  Check-in   : " + checkIn.format(DATE_FMT));
            System.out.println("  Check-out  : " + checkOut.format(DATE_FMT));
            System.out.printf("  Total      : ₹%.2f (%d nights × ₹%.2f)%n",
                total, nights, selectedRoom.getPricePerNight());

            System.out.print("\n  Confirm booking? (Y/N): ");
            if (!scanner.nextLine().trim().equalsIgnoreCase("Y")) {
                System.out.println("  Booking cancelled by user.");
                pressEnterToContinue();
                return;
            }

            // Step 5: Create reservation (PENDING status)
            Reservation reservation = reservationService.createReservation(
                customer, selectedRoom.getRoomId(), checkIn, nights
            );

            // Step 6: Payment
            System.out.println("\n  ─── Select Payment Method ────────────────────────────");
            System.out.println("  1. Credit Card");
            System.out.println("  2. UPI");
            System.out.println("  3. Cash");
            System.out.print("\n  Enter choice (1-3): ");
            int payChoice = readIntSafe(1, 3);

            Payment.PaymentMethod method = switch (payChoice) {
                case 1 -> Payment.PaymentMethod.CREDIT_CARD;
                case 2 -> Payment.PaymentMethod.UPI;
                default -> Payment.PaymentMethod.CASH;
            };

            Payment payment = paymentService.processPayment(
                reservation.getReservationId(), total, method, scanner
            );

            // Step 7: Confirm reservation
            reservationService.confirmReservation(reservation.getReservationId(), payment);

            // Step 8: Show confirmation
            Reservation confirmed = reservationService.getReservationById(
                reservation.getReservationId()
            );
            System.out.println(confirmed.getDetailedInfo());

        } catch (RoomNotFoundException | RoomNotAvailableException e) {
            System.out.println("\n  ✗ Room Error: " + e.getMessage());
        } catch (InvalidInputException e) {
            System.out.println("\n  ✗ Input Error: " + e.getMessage());
        } catch (PaymentFailedException e) {
            System.out.println("\n  ✗ " + e.getMessage());
            System.out.println("  Your reservation has been saved but not confirmed.");
            System.out.println("  Please try booking again with a different payment method.");
        } catch (Exception e) {
            System.out.println("\n  ✗ Unexpected error: " + e.getMessage());
        }

        pressEnterToContinue();
    }

    /**
     * Handler: Cancel a Reservation
     */
    private void cancelReservation() {
        printSectionHeader("CANCEL RESERVATION");

        try {
            System.out.print("  Enter Reservation ID to cancel: ");
            String reservationId = scanner.nextLine().trim();
            InputValidator.validateNotBlank(reservationId, "Reservation ID");

            Reservation reservation = reservationService.getReservationById(reservationId);

            System.out.println("\n  Reservation found:");
            System.out.println("  Customer : " + reservation.getCustomer().getName());
            System.out.println("  Room     : " + reservation.getRoom().getRoomNumber());
            System.out.println("  Status   : " + reservation.getStatus());
            System.out.printf("  Amount   : ₹%.2f%n", reservation.getTotalAmount());

            System.out.print("\n  Confirm cancellation? (Y/N): ");
            if (!scanner.nextLine().trim().equalsIgnoreCase("Y")) {
                System.out.println("  Cancellation aborted.");
                pressEnterToContinue();
                return;
            }

            Reservation cancelled = reservationService.cancelReservation(reservationId);
            paymentService.simulateRefund(reservationId, cancelled.getTotalAmount());

        } catch (ReservationNotFoundException e) {
            System.out.println("\n  ✗ " + e.getMessage());
        } catch (InvalidInputException e) {
            System.out.println("\n  ✗ " + e.getMessage());
        }

        pressEnterToContinue();
    }

    /**
     * Handler: View Reservation Details
     */
    private void viewReservationDetails() {
        printSectionHeader("VIEW RESERVATION DETAILS");

        try {
            System.out.print("  Enter Reservation ID: ");
            String reservationId = scanner.nextLine().trim();
            InputValidator.validateNotBlank(reservationId, "Reservation ID");

            Reservation reservation = reservationService.getReservationById(reservationId);
            System.out.println(reservation.getDetailedInfo());

        } catch (ReservationNotFoundException e) {
            System.out.println("\n  ✗ " + e.getMessage());
        } catch (InvalidInputException e) {
            System.out.println("\n  ✗ " + e.getMessage());
        }

        pressEnterToContinue();
    }

    /**
     * Handler: View All Reservations
     */
    private void viewAllReservations() {
        printSectionHeader("ALL RESERVATIONS");
        List<Reservation> reservations = reservationService.getAllReservations();
        reservationService.displayReservationsSummary(reservations);
        System.out.println("\n  Total reservations: " + reservations.size());
        pressEnterToContinue();
    }

    /**
     * Handler: View All Customers
     */
    private void viewAllCustomers() {
        printSectionHeader("ALL CUSTOMERS");
        List<Customer> customers = customerService.getAllCustomers();
        customerService.displayCustomers(customers);
        System.out.println("\n  Total customers: " + customers.size());
        pressEnterToContinue();
    }

    //  UI Helpers

    private void printWelcomeBanner() {
        System.out.println();
        System.out.println("  ╔═══════════════════════════════════════════════════════╗");
        System.out.println("  ║          GRAND PALACE HOTEL & RESORTS                 ║");
        System.out.println("  ║          Hotel Reservation Management System          ║");
        System.out.println("  ║                   Version 1.0                         ║");
        System.out.println("  ╚═══════════════════════════════════════════════════════╝");
        System.out.println("  Data directory : " + FileHandler.DATA_DIR);
        System.out.println("  Rooms loaded   : " + roomService.getAllRooms().size());
        System.out.println("  Customers      : " + customerService.getAllCustomers().size());
        System.out.println("  Reservations   : " + reservationService.getAllReservations().size());
        System.out.println();
    }

    private void printMainMenu() {
        System.out.println();
        System.out.println("  ╔═══════════════════════════════════════╗");
        System.out.println("  ║      HOTEL RESERVATION SYSTEM         ║");
        System.out.println("  ╠═══════════════════════════════════════╣");
        System.out.println("  ║  1.  View Available Rooms             ║");
        System.out.println("  ║  2.  Search Room By Category          ║");
        System.out.println("  ║  3.  Book Room                        ║");
        System.out.println("  ║  4.  Cancel Reservation               ║");
        System.out.println("  ║  5.  View Reservation Details         ║");
        System.out.println("  ║  6.  View All Reservations            ║");
        System.out.println("  ║  7.  View All Customers               ║");
        System.out.println("  ║  8.  Exit                             ║");
        System.out.println("  ╚═══════════════════════════════════════╝");
        System.out.print("  Enter Choice: ");
    }

    private void printSectionHeader(String title) {
        System.out.println("\n  ══════════════════════════════════════════════════════");
        System.out.println("  " + title);
        System.out.println("  ══════════════════════════════════════════════════════");
    }

    private void printGoodbye() {
        System.out.println("\n  Thank you for using Grand Palace Hotel Reservation System.");
        System.out.println("  All data has been saved. Goodbye!\n");
    }

    private void pressEnterToContinue() {
        System.out.print("\n  Press Enter to continue...");
        scanner.nextLine();
    }

    /**
     * Reads a date from user input. Defaults to today if blank.
     *
     * @param prompt The prompt to display
     * @return Parsed LocalDate
     */
    private LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isBlank()) {
                return LocalDate.now();
            }
            try {
                LocalDate date = LocalDate.parse(input, DATE_FMT);
                if (date.isBefore(LocalDate.now())) {
                    System.out.println("  ✗ Check-in date cannot be in the past. Try again.");
                    continue;
                }
                return date;
            } catch (DateTimeParseException e) {
                System.out.println("  ✗ Invalid date format. Please use dd-MM-yyyy (e.g. 25-12-2025).");
            }
        }
    }

    /**
     * Safely reads an integer from stdin, re-prompting on invalid input.
     *
     * @param min Minimum accepted value
     * @param max Maximum accepted value
     * @return A valid integer in [min, max]
     */
    private int readIntSafe(int min, int max) {
        while (true) {
            try {
                String line = scanner.nextLine().trim();
                int value = Integer.parseInt(line);
                if (value < min || value > max) {
                    System.out.printf("  ✗ Please enter a number between %d and %d: ", min, max);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.printf("  ✗ Invalid input. Enter a number (%d-%d): ", min, max);
            }
        }
    }

    //  Sample Data Seeder

    /**
     * Seeds the system with sample rooms on first run.
     * Only runs when no rooms exist in the repository.
     */
    private void seedSampleDataIfEmpty() {
        if (!roomService.getAllRooms().isEmpty()) {
            return; // Data already exists — don't seed again
        }

        System.out.println("  Initializing hotel with sample room data...");

        // Standard Rooms (101–105)
        roomService.addRoom(RoomFactory.createStandardRoom("101", 2500.00));
        roomService.addRoom(RoomFactory.createStandardRoom("102", 2500.00));
        roomService.addRoom(RoomFactory.createStandardRoom("103", 2800.00));
        roomService.addRoom(RoomFactory.createStandardRoom("104", 2800.00));
        roomService.addRoom(RoomFactory.createStandardRoom("105", 3000.00));

        // Deluxe Rooms (201–204)
        roomService.addRoom(RoomFactory.createDeluxeRoom("201", 5500.00, true));
        roomService.addRoom(RoomFactory.createDeluxeRoom("202", 5500.00, false));
        roomService.addRoom(RoomFactory.createDeluxeRoom("203", 6000.00, true));
        roomService.addRoom(RoomFactory.createDeluxeRoom("204", 6000.00, true));

        // Suites (301–303)
        roomService.addRoom(RoomFactory.createSuiteRoom("301", 12000.00, 2));
        roomService.addRoom(RoomFactory.createSuiteRoom("302", 15000.00, 3));
        roomService.addRoom(RoomFactory.createSuiteRoom("303", 20000.00, 4));

        System.out.println("  ✓ 12 rooms initialized successfully.");
    }
}
