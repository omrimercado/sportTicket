# sportTicket
Sporticket is a full-fledged sports ticket buying and selling platform developed in Java. This project demonstrates a robust client-server architecture using TCP sockets, a JavaFX-based GUI client, and a modular design that includes Data Access Objects (DAO), Service layers, and Domain Models.
Features
User Authentication:
Users can sign up and log in, with functionality differing by role (regular user vs. admin).

Ticket Management:

Buy/Sell Tickets: Users can post tickets for sale and purchase tickets from others.
Unique Ticket IDs: Tickets are assigned unique, hidden identifiers for precise operations.
Search Functionality: Implements custom string matching (e.g., KMP and Rabin-Karp algorithms) to search events and tickets.
Admin Panel:
Admins have additional controls, including viewing and deleting users and tickets.

Client-Server Communication:
The server listens for JSON-formatted requests over TCP sockets, processes them through a layered architecture, and responds accordingly.

Responsive GUI:
Built with JavaFX, the GUI features dynamic, responsive layouts using modern CSS styling.

Architecture Overview
Domain Models (DM):
Defines the core data structures (User, Ticket, Event).

DAO Layer:
Manages data persistence using JSON files (e.g., tickets.txt, users.txt).

Service Layer:
Contains business logic such as ticket transactions and balance updates.

Controller Layer (Server):
Routes incoming JSON requests to the appropriate service methods.

GUI (Client):
A JavaFX application that presents a user-friendly interface for all operations, including dynamic tables for tickets and users.

Getting Started
Prerequisites
JDK 11 or later (tested with OpenJDK 23)
Maven (or your preferred build tool)
JavaFX libraries (configured via Maven or added to your IDE)
Building the Project
Clone the Repository:
bash
Copy
git clone https://github.com/yourusername/sporticket.git
Build the Project with Maven:
bash
Copy
mvn clean install
Running the Application
Start the Server: Navigate to the server module (or jar) and run:
bash
Copy
java -jar server.jar
Run the Client GUI: Navigate to the client module (or jar) and run:
bash
Copy
java -jar client.jar
Contributing
Contributions, issues, and feature requests are welcome! Please open an issue or submit a pull request.
