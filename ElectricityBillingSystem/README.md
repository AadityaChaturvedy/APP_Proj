E-Bill Management System 💡

A modern and feature-rich desktop application built with JavaFX for managing customer electricity bills. This system provides a clean user interface for administrators to perform CRUD operations, alongside advanced features like multi-language support, theme switching, and an interactive chatbot.

✨ Features
```
Full CRUD Functionality: Create, read, update, and delete customer bills with ease.

Live Search & Filtering: Instantly filter through the entire bill database by customer name, city, or meter number.

Dynamic Theming: Switch between a sleek light mode and a comfortable dark mode on the fly.

Multi-Language Support: Fully localized interface supporting English, Hindi, and Tamil.

Interactive Chatbot: An integrated AI assistant to answer queries about bill status, units consumed, and amounts, complete with Text-to-Speech responses. 🤖

Data Persistence: All bill data is automatically saved to a local JSON file, ensuring no data loss between sessions.

Responsive UI: A clean and modern user interface built with JavaFX and styled with CSS.
```
🛠️ Tech Stack
```
Core: Java 17+

Framework: JavaFX 20

Build Tool: Apache Maven

Libraries:

Jackson: For seamless JSON serialization and deserialization.

FreeTTS: For the text-to-speech engine in the chatbot.

Ikonli: For high-quality font icons used throughout the application.
```
🚀 Getting Started
```
Follow these instructions to get a copy of the project up and running on your local machine.

Prerequisites

Java Development Kit (JDK): Version 17 or higher.

Apache Maven: Version 3.8 or higher.

Execution

Navigate to the project directory:

Bash
cd ElectricityBillingSystem

Bash
mvn clean javafx:run
The application will start, and you will be greeted with the login screen. Use admin for both the username and password.
```

📂 Project Structure
The project is organized following standard Maven conventions, with a clear separation of concerns.

```
src
└── main
    ├── java
    │   └── com
    │       └── ebill
    │           ├── controllers   // FXML controllers for UI logic
    │           ├── models        // Data models (e.g., Bill.java)
    │           ├── services      // Singleton services for backend logic
    │           └── MainApp.java  // Main application entry point
    └── resources
        ├── css               // Stylesheets for themes
        ├── fxml              // FXML files for UI layout
        ├── lang              // JSON files for localization
        └── data.json         // Initial seed data for the application
```
🏗️ Architecture
```
The application is built upon a refined Model-View-Controller (MVC) pattern, enhanced with a Service layer to handle all business logic and cross-cutting concerns.

Model (Bill.java): Represents the application's data structure, using JavaFX properties for easy UI binding.

View (.fxml files): Defines the layout and appearance of the user interface.

Controller (*Controller.java): Manages UI logic, handles user input, and acts as the bridge between the View and the backend.

Service (*Service.java): Singleton classes responsible for single, distinct functionalities like data persistence (DataService), theme management (ThemeManager), and localization (LocalizationService). This design keeps the controllers lean and promotes code reuse.
```
