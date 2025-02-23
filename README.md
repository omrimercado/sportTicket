# Sporticket - Sports Ticket System



---

## Overview

**Sporticket** is a comprehensive sports ticket buying and selling platform developed in Java. The project demonstrates a robust client-server architecture using TCP sockets and a sleek, modern JavaFX-based GUI. It incorporates a layered design that includes Domain Models, Data Access Objects (DAO), Service layers, and Controllers for both the server and client.

---

## Features

- **User Authentication & Roles:**  
  - **Registration & Login:** Secure sign-up and login for users.
  - **Role-Based Functionality:** Different experiences for regular users and administrators.
  
- **Ticket Management:**  
  - **Buy/Sell Tickets:** Post tickets for sale or purchase tickets from other users.
  - **Unique Ticket IDs:** Automatically generated hidden IDs ensure precise ticket operations.
  - **Search Functionality:** Advanced string matching (KMP and Rabin-Karp) for fast event and ticket searches.
  
- **Admin Panel:**  
  - Manage users and tickets with additional administrative controls.
  
- **Client-Server Communication:**  
  - JSON-based messaging over TCP sockets ensures seamless data exchange.
  
- **Responsive & Modern GUI:**  
  - JavaFX-based client with dynamic layouts and custom CSS for an enhanced user experience.
  
- **Robust Architecture:**  
  - Clean separation of concerns with Domain Models, DAO, Service, and Controller layers.

---

- **Domain Models (DM):** Represent core entities such as `User`, `Ticket`, and `Event`.
- **DAO Layer:** Manages data persistence with JSON files (e.g., `tickets.txt`, `users.txt`).
- **Service Layer:** Contains business logic for transactions, balance updates, and unique ID generation.
- **Controller Layer (Server):** Routes JSON requests to appropriate services.
- **JavaFX GUI (Client):** A responsive, modern interface with dynamic tables and CSS styling.

---

**Contributing**
Contributions, feature requests, and issues are welcome! Please open an issue or submit a pull request.
For major changes, please open an issue first to discuss what you would like to change.
