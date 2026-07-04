# MediCore - Hospital Management System

MediCore is a pure Java console application for managing patients, doctors, support staff, and emergency calls. It is designed following professional object-oriented programming (OOP) principles, clean encapsulation, and structured file database storage using CSV.

## Features

- **Patient Management**: Registration, admission tracking, doctor assignment, discharge logs, search, and details updates.
- **Doctor Management**: Scheduling, department mapping, availability tracking, and viewing assigned patient lists.
- **Staff Management**: Tracking support staff (Nurses, Receptionists, Lab Technicians, Pharmacists, etc.), shifts, roles, and status.
- **Emergency System**: High-priority alert dispatcher supporting critical case logging, ambulance deployment, and staff resource allocation.
- **Advanced Reports**: Real-time hospital metrics, including bed availability, doctor load, disease-wise patient distribution, and exportable patient reports.
- **CSV Storage Engine**: Persistent storage that auto-loads data on startup and saves changes back to CSV.
- **Audit Logger**: Automatically logs every transaction and state change to a persistent text-based audit trail (`output/audit_log.txt`).

## Architecture & Code Structure

The code is modularized as follows:
- `model/`: Objects mapping patients, doctors, staff, and emergency events.
- `model/enums/`: Strongly typed states like statuses, shifts, and priorities.
- `service/`: Domain logic handling workflow validations and business rules.
- `file/`: Custom reader and writer parsing CSV lines safely (supporting quotes and commas).
- `util/`: Validator for sanitizing telephone formats, ages, and emails.
- `exception/`: Domain-specific custom exceptions (e.g., `PatientNotFoundException`, `InvalidPatientStateException`).

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 8 or higher.
- VS Code with Java Extension Pack (optional).

### Compile and Run

To compile and run the system from the command line, run the following commands from the `HospitalManagement/` directory:

```bash
# Compile the code
javac -d bin src/model/enums/*.java src/model/*.java src/exception/*.java src/util/*.java src/file/*.java src/service/*.java src/Main.java

# Run the application
java -cp bin Main
```
