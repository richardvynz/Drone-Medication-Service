# DroneMedic: Revolutionizing Healthcare Delivery with Drone Technology


Welcome to DroneMedic, a pioneering service revolutionizing healthcare 
logistics by seamlessly merging drone technology with medical supply delivery, 
ensuring vital medications reach remote or challenging locations with unprecedented efficiency.


## Table of Contents

- Introduction.
-  Project Overview.
- Key Features.
- Functional Requirements.
- Non-Functional Requirements.
- Technologies Used.
- Installation and Usage.
- Testing.
- License.

### Introduction.
DroneMedic represents a paradigm shift, innovatively employing drone technology to ensure timely
and secure delivery of essential medications to regions previously hindered by limited access or 
challenging terrains.

### Project Overview.
DroneMedic orchestrates a fleet of ten cutting-edge drones exclusively designed for medication delivery.
Each drone is uniquely identified by a serial number, features distinct weight limitations based on its model, 
and boasts varying battery capacities. The project aims to establish a robust service via REST APIs, facilitating
streamlined loading and efficient delivery of medications.

### Key Features.
- **Drone Registration:** Seamlessly register new drones into the fleet.
- **Medication Loading:** Swiftly load medications onto drones for prompt delivery.
- **Inventory Check:** Precisely inspect loaded medications on specific drones.
- **Drone Availability Check:** Easily identify available drones for loading medications.
- **Battery Level Monitoring:** Monitor and manage individual drone battery levels efficiently.


### Functional Requirements.
Our service is designed to meet stringent functional requirements:

- **UI-Independent Operation:** Operates flawlessly via RESTful endpoints.
- **Weight Limit Checks:** Prevents overloading of drones to maintain safety standards.
- **Battery Level Safeguards:** Ensures no loading if battery level is **below 25%** for uninterrupted service.
- **Scheduled Battery Checks:** Regularly monitors drone battery levels, maintaining detailed event logs.

### Non-Functional Requirements.
To uphold reliability and best practices, DroneMedic adheres to the following non-functional requirements:

- **H2 In-Memory Database:** Utilizes H2 in-memory database for seamless local development and rapid iteration.
- **JSON Data Format:** Consistently maintains input/output data adhering to JSON standards.
- **Comprehensive Testing:** Includes an exhaustive suite of unit tests ensuring optimal coverage and stability.
- **Spring Boot Framework:** Leverages Spring Boot for unparalleled flexibility and robustness.


### Technologies Used
- Java
- Spring Boot
- Hibernate
- RESTful APIs
- JSON
- H2 In-Memory Database

### Installation and Usage
- **Installation:** Ensure you have Java installed. Download the project and navigate to its directory.
- **Build Project:** ./mvnw clean package
- **Run Application:** ./mvnw spring-boot:run
- **Accessing APIs:** Once the application is running, navigate to http://localhost:8080/swagger-ui.html 
in your web browser to access the Swagger UI. Swagger provides an interactive interface for testing the
  the RESTful APIs, allowing convenient and efficient API exploration and testing.

### Testing
DroneMedic prioritizes rigorous testing methodologies to ensure unwavering reliability and stability. 
The project includes an extensive suite of unit tests meticulously validating functionality and performance.

### License
This project is licensed under the MIT License, fostering an open and collaborative environment for further
advancements.

