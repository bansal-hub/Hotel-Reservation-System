# Grand Palace Hotel Reservation System

A complete, production-ready Hotel Reservation System built in Java 17 using OOP principles, layered architecture, and file-based persistence.

---

## Project Structure

```
HotelReservationSystem/
├── HotelReservationSystem.jar        ← Executable JAR (ready to run)
├── run.sh                            ← Linux/macOS launcher
├── run.bat                           ← Windows launcher
├── README.md
└── src/main/java/com/hotel/
    ├── model/
    │   ├── Room.java                 ← Abstract base class
    │   ├── StandardRoom.java         ← Standard room type
    │   ├── DeluxeRoom.java           ← Deluxe room type
    │   ├── SuiteRoom.java            ← Suite room type
    │   ├── Customer.java             ← Customer entity
    │   ├── Reservation.java          ← Booking entity (Composition)
    │   └── Payment.java              ← Abstract + 3 concrete types
    │
    ├── exception/
    │   ├── RoomNotFoundException.java
    │   ├── RoomNotAvailableException.java
    │   ├── ReservationNotFoundException.java
    │   ├── CustomerNotFoundException.java
    │   ├── PaymentFailedException.java
    │   └── InvalidInputException.java
    │
    ├── repository/
    │   ├── Repository.java           ← Generic CRUD interface
    │   ├── RoomRepository.java       ← Singleton
    │   ├── CustomerRepository.java   ← Singleton
    │   ├── ReservationRepository.java← Singleton
    │   └── PaymentRepository.java    ← Singleton
    │
    ├── service/
    │   ├── RoomFactory.java          ← Factory Pattern
    │   ├── RoomService.java
    │   ├── CustomerService.java
    │   ├── PaymentService.java
    │   └── ReservationService.java
    │
    ├── util/
    │   ├── FileHandler.java          ← Serialization persistence
    │   ├── IDGenerator.java          ← Unique ID generation
    │   └── InputValidator.java       ← Input validation
    │
    └── main/
        └── HotelReservationSystem.java ← Entry point + console menu
```

---

## Requirements

- **Java 17+** (JRE is sufficient to run the JAR)
- No external dependencies — pure Java standard library

---

## How to Run

### Option 1 — Executable JAR (Easiest)
```bash
java -jar HotelReservationSystem.jar
```

### Option 2 — Shell script (Linux/macOS)
```bash
chmod +x run.sh
./run.sh
```

### Option 3 — Windows batch file
```cmd
run.bat
```

### Option 4 — Compile from source
```bash
# Compile
find src -name "*.java" > sources.txt
javac -source 17 -target 17 -d out @sources.txt

# Run
java -cp out com.hotel.main.HotelReservationSystem
```

---

## Console Menu

```
╔═══════════════════════════════════════╗
║      HOTEL RESERVATION SYSTEM         ║
╠═══════════════════════════════════════╣
║  1.  View Available Rooms             ║
║  2.  Search Room By Category          ║
║  3.  Book Room                        ║
║  4.  Cancel Reservation               ║
║  5.  View Reservation Details         ║
║  6.  View All Reservations            ║
║  7.  View All Customers               ║
║  8.  Exit                             ║
╚═══════════════════════════════════════╝
```

---

## Features

### Room Management
- 12 pre-seeded rooms across 3 categories: Standard, Deluxe, Suite
- Standard (101–105): ₹2,500–₹3,000/night | 2 guests
- Deluxe (201–204): ₹5,500–₹6,000/night | 3 guests | Optional balcony
- Suite (301–303): ₹12,000–₹20,000/night | 6 guests | Multi-room

### Booking Flow
1. View available rooms
2. Select room by number
3. Enter customer details (auto-registers or reuses existing customer)
4. Enter check-in date (dd-MM-yyyy) and number of nights
5. Confirm booking summary
6. Select payment: Credit Card / UPI / Cash
7. Payment processed → Reservation confirmed
8. Full receipt displayed

### Cancellation Flow
1. Enter Reservation ID
2. Confirm cancellation
3. Room released back to available pool
4. Refund simulated (5–7 business days message)

### Data Persistence
All data is saved automatically to `hotel_data/` in the working directory:
- `hotel_data/rooms.dat`
- `hotel_data/customers.dat`
- `hotel_data/reservations.dat`
- `hotel_data/payments.dat`

Data survives application restarts — on next launch all history is restored.

---

## OOP Concepts Demonstrated

| Concept | Where Used |
|---|---|
| **Abstraction** | `Room`, `Payment` abstract classes |
| **Inheritance** | `StandardRoom`, `DeluxeRoom`, `SuiteRoom` extend `Room` |
| **Polymorphism** | `getAmenities()`, `processPayment()` overridden per subtype |
| **Encapsulation** | All fields private with getters/setters |
| **Interfaces** | `Repository<T,ID>` generic CRUD interface |
| **Composition** | `Reservation` HAS-A `Customer`, `Room`, `Payment` |
| **Exception Handling** | 6 custom exceptions, try-catch throughout |
| **Singleton Pattern** | All 4 Repository classes |
| **Factory Pattern** | `RoomFactory` creates room subtypes |
| **Generics** | `Repository<T, ID>` interface |

---

## Sample IDs (first run)

| Type | Example ID |
|---|---|
| Room | `RM-202606091953-1` |
| Customer | `CUST-202606091953-1` |
| Reservation | `RES-202606091953-1` |
| Payment | `PAY-202606091953-1` |

---

## Payment Simulation Success Rates

| Method | Success Rate |
|---|---|
| Credit Card | 90% |
| UPI | 95% |
| Cash | 100% |

If payment fails, the reservation is saved as PENDING and the room remains available for re-booking.
