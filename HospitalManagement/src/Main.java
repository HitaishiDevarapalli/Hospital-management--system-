import exception.DoctorNotFoundException;
import exception.InvalidPatientStateException;
import exception.PatientNotFoundException;
import exception.StaffNotFoundException;
import file.CSVReader;
import file.CSVWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;
import model.Doctor;
import model.Emergency;
import model.Patient;
import model.Staff;
import model.enums.*;
import service.DoctorService;
import service.EmergencyService;
import service.PatientService;
import service.ReportService;
import service.StaffService;

public class Main {
    private static final String PATIENTS_CSV = "HospitalManagement/input/patients.csv";
    private static final String DOCTORS_CSV = "HospitalManagement/input/doctors.csv";
    private static final String STAFF_CSV = "HospitalManagement/input/staff.csv";

    private static PatientService patientService;
    private static DoctorService doctorService;
    private static StaffService staffService;
    private static EmergencyService emergencyService;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=================================================");
        System.out.println("     MEDICORE HOSPITAL MANAGEMENT SYSTEM         ");
        System.out.println("=================================================");
        
        // Password protection on startup
        boolean authenticated = false;
        while (!authenticated) {
            System.out.print("Enter Authorization Password to Access Portal: ");
            String inputPassword = scanner.nextLine();
            if ("asishsir".equals(inputPassword)) {
                authenticated = true;
                System.out.println("\nAccess Granted. Welcome to MediCore.");
            } else {
                System.out.println("Unauthorized Access Attempt. Invalid Password. Try Again.\n");
            }
        }

        System.out.println("\nInitializing MediCore Systems... Loading Databases...");
        
        // Load CSV data
        ArrayList<Patient> patients = CSVReader.readPatients(PATIENTS_CSV);
        ArrayList<Doctor> doctors = CSVReader.readDoctors(DOCTORS_CSV);
        ArrayList<Staff> staffList = CSVReader.readStaff(STAFF_CSV);

        // Initialize Services
        patientService = new PatientService(patients);
        doctorService = new DoctorService(doctors);
        staffService = new StaffService(staffList);
        emergencyService = new EmergencyService();

        ReportService.log("MediCore Hospital Management System started.");

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readIntInput(scanner, "Choose an option: ");
            switch (choice) {
                case 1:
                    handlePatientMenu(scanner);
                    break;
                case 2:
                    handleDoctorMenu(scanner);
                    break;
                case 3:
                    handleStaffMenu(scanner);
                    break;
                case 4:
                    handleEmergencyMenu(scanner);
                    break;
                case 5:
                    handleReportMenu(scanner);
                    break;
                case 6:
                    handleFeePaymentMenu(scanner);
                    break;
                case 7:
                    saveAllData();
                    break;
                case 8:
                    System.out.print("Do you want to save changes before exiting? (yes/no): ");
                    String saveChoice = scanner.nextLine().trim().toLowerCase();
                    if (saveChoice.equals("yes") || saveChoice.equals("y")) {
                        saveAllData();
                    }
                    System.out.println("\nThank you for using MediCore. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please choose between 1 and 8.");
            }
        }
        scanner.close();
    }

    private static void printMainMenu() {
        System.out.println("\n=========================================");
        System.out.println("   MEDICORE HOSPITAL MANAGEMENT SYSTEM   ");
        System.out.println("=========================================");
        System.out.println("1. Patient Management");
        System.out.println("2. Doctor Management");
        System.out.println("3. Staff Management");
        System.out.println("4. Emergency Management");
        System.out.println("5. Reports");
        System.out.println("6. Fee Payment Module");
        System.out.println("7. Save Data");
        System.out.println("8. Exit");
        System.out.println("-----------------------------------------");
    }

    // ==========================================
    // PATIENT MENU HANDLER
    // ==========================================
    private static void handlePatientMenu(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- PATIENT MANAGEMENT ---");
            System.out.println("1. Add Patient");
            System.out.println("2. Search Patient");
            System.out.println("3. Update Patient");
            System.out.println("4. View Patient Details");
            System.out.println("5. View All Patients");
            System.out.println("6. Admit Patient");
            System.out.println("7. Assign Doctor");
            System.out.println("8. Discharge Patient");
            System.out.println("9. Complete Treatment Workflow (Auto)");
            System.out.println("10. Back to Main Menu");
            
            int choice = readIntInput(scanner, "Enter choice: ");
            try {
                switch (choice) {
                    case 1: {
                        System.out.print("Enter Patient ID (e.g. P021): ");
                        String id = scanner.nextLine().trim();
                        try {
                            Patient existingPatient = patientService.findPatient(id);
                            System.out.println("\n[ERROR] Duplicate ID: Patient ID " + id + " already exists in the system!");
                            break;
                        } catch (PatientNotFoundException e) {
                            // Unique ID, proceed
                        }

                        System.out.println("Patient Type:");
                        System.out.println("1. Inpatient (IP)");
                        System.out.println("2. Outpatient (OP)");
                        int typeSelect = readIntInput(scanner, "Choose option (1-2): ");
                        String type = (typeSelect == 1) ? "IP" : "OP";

                        Patient p = new Patient();
                        p.setPatientId(id);
                        p.setPatientType(type);

                        if ("IP".equals(type)) {
                            System.out.print("Inpatient detected. Enter Room Number to assign: ");
                            String roomNumber = scanner.nextLine().trim();
                            p.setRoomNumber(roomNumber);
                            p.setAdmissionStatus(PatientStatus.ADMITTED);
                            p.setAdmissionDate(LocalDate.now().toString());
                        } else {
                            System.out.println("\nOutpatient detected. No Room Number will be assigned.");
                            p.setRoomNumber("N/A (OP)");
                            System.out.println("Available Doctors:");
                            for (Doctor docIter : doctorService.getAllDoctors()) {
                                System.out.println(" - " + docIter.getDoctorId() + ": " + docIter.getName() + " (" + docIter.getSpecialization() + ")");
                            }
                            System.out.print("Enter Doctor ID to Assign: ");
                            String docId = scanner.nextLine().trim();
                            if (!doctorService.doctorExists(docId)) {
                                System.out.println("[ERROR] Assigned Doctor ID does not exist! Patient creation cancelled.");
                                break;
                            }
                            p.setAssignedDoctorId(docId);
                        }

                        System.out.print("Enter Name: ");
                        String name = scanner.nextLine().trim();
                        int age = readIntInput(scanner, "Enter Age: ");
                        System.out.print("Enter Gender: ");
                        String gender = scanner.nextLine().trim();
                        System.out.print("Enter Phone: ");
                        String phone = scanner.nextLine().trim();
                        System.out.print("Enter Address: ");
                        String address = scanner.nextLine().trim();
                        System.out.print("Enter Blood Group (e.g., O+, A-): ");
                        String blood = scanner.nextLine().trim();
                        System.out.print("Enter Disease: ");
                        String disease = scanner.nextLine().trim();

                        p.setName(name);
                        p.setAge(age);
                        p.setGender(gender);
                        p.setPhoneNumber(phone);
                        p.setAddress(address);
                        p.setBloodGroup(blood);
                        p.setDisease(disease);

                        patientService.addPatient(p);
                        System.out.println("Patient added successfully!");
                        break;
                    }
                    case 2:
                        System.out.print("Enter Patient ID to Search: ");
                        String searchId = scanner.nextLine().trim();
                        Patient found = patientService.findPatient(searchId);
                        System.out.println("\nFound: " + found);
                        break;
                    case 3:
                        System.out.print("Enter Patient ID to Update: ");
                        String updateId = scanner.nextLine().trim();
                        Patient ex = patientService.findPatient(updateId);
                        System.out.print("New Name (" + ex.getName() + "): ");
                        String uName = scanner.nextLine().trim();
                        if (uName.isEmpty()) uName = ex.getName();
                        
                        String ageStr = promptWithDefault(scanner, "New Age", String.valueOf(ex.getAge()));
                        int uAge = Integer.parseInt(ageStr);
                        
                        String uGender = promptWithDefault(scanner, "New Gender", ex.getGender());
                        String uPhone = promptWithDefault(scanner, "New Phone", ex.getPhoneNumber());
                        String uAddr = promptWithDefault(scanner, "New Address", ex.getAddress());
                        String uBlood = promptWithDefault(scanner, "New Blood Group", ex.getBloodGroup());
                        String uDisease = promptWithDefault(scanner, "New Disease", ex.getDisease());

                        patientService.updatePatient(updateId, uName, uAge, uGender, uPhone, uAddr, uBlood, uDisease);
                        System.out.println("Patient updated successfully!");
                        break;
                    case 4:
                        System.out.print("Enter Patient ID to View: ");
                        String viewId = scanner.nextLine().trim();
                        Patient v = patientService.findPatient(viewId);
                        System.out.println("\n--- Patient Details ---");
                        System.out.println("ID: " + v.getPatientId());
                        System.out.println("Name: " + v.getName());
                        System.out.println("Type: " + v.getPatientType());
                        System.out.println("Age: " + v.getAge());
                        System.out.println("Gender: " + v.getGender());
                        System.out.println("Phone: " + v.getPhoneNumber());
                        System.out.println("Address: " + v.getAddress());
                        System.out.println("Blood Group: " + v.getBloodGroup());
                        System.out.println("Disease: " + v.getDisease());
                        System.out.println("Admission Status: " + v.getAdmissionStatus());
                        System.out.println("Assigned Doctor ID: " + v.getAssignedDoctorId());
                        System.out.println("Admission Date: " + v.getAdmissionDate());
                        System.out.println("Discharge Date: " + v.getDischargeDate());
                        System.out.println("Room Number: " + v.getRoomNumber());
                        break;
                    case 5:
                        System.out.println("\n--- ALL PATIENTS ---");
                        ArrayList<Patient> list = patientService.getAllPatients();
                        if (list.isEmpty()) {
                            System.out.println("No patients in the system.");
                        } else {
                            for (Patient pat : list) {
                                System.out.println(pat);
                            }
                        }
                        break;
                    case 6:
                        System.out.print("Enter Patient ID to Admit: ");
                        String admitId = scanner.nextLine().trim();
                        
                        Patient toAdmit = patientService.findPatient(admitId);
                        if ("OP".equalsIgnoreCase(toAdmit.getPatientType())) {
                            System.out.println("Error: Outpatients (OP) cannot be admitted as inpatients!");
                            break;
                        }
                        
                        System.out.print("Enter Room Number: ");
                        String room = scanner.nextLine().trim();
                        patientService.admitPatient(admitId, room);
                        System.out.println("Patient admitted successfully to room " + room + "!");
                        break;
                    case 7: {
                        System.out.print("Enter Patient ID: ");
                        String patId = scanner.nextLine().trim();
                        Patient patientObj = patientService.findPatient(patId);

                        System.out.print("Enter Doctor ID to Assign: ");
                        String docId = scanner.nextLine().trim();
                        Doctor doctorObj = doctorService.findDoctor(docId);

                        System.out.println("\nChecking assignment compatibility...");
                        System.out.println("Patient: " + patientObj.getName() + " (Disease: " + patientObj.getDisease() + ")");
                        System.out.println("Doctor: " + doctorObj.getName() + " (Specialization: " + doctorObj.getSpecialization() + " | Dept: " + doctorObj.getDepartment() + ")");

                        String diseaseVal = patientObj.getDisease().toLowerCase();
                        String specialization = doctorObj.getSpecialization().toLowerCase();
                        String department = doctorObj.getDepartment().toLowerCase();

                        boolean matches = false;
                        if (diseaseVal.contains("heart") || diseaseVal.contains("cardio")) {
                            matches = specialization.contains("cardio") || department.contains("cardio");
                        } else if (diseaseVal.contains("preg") || diseaseVal.contains("gyn") || diseaseVal.contains("birth")) {
                            matches = specialization.contains("gyn") || department.contains("obgyn") || department.contains("obstetrics");
                        } else if (diseaseVal.contains("fracture") || diseaseVal.contains("bone") || diseaseVal.contains("ortho")) {
                            matches = specialization.contains("ortho") || department.contains("ortho");
                        } else if (diseaseVal.contains("child") || diseaseVal.contains("pediatric") || diseaseVal.contains("fever") || diseaseVal.contains("flu")) {
                            matches = specialization.contains("pediatr") || specialization.contains("general") || department.contains("pediatr") || department.contains("general");
                        } else if (diseaseVal.contains("stroke") || diseaseVal.contains("brain") || diseaseVal.contains("neuro")) {
                            matches = specialization.contains("neuro") || department.contains("neuro");
                        } else {
                            matches = specialization.contains(diseaseVal) || department.contains(diseaseVal) || specialization.contains("general") || department.contains("general") || specialization.contains("er") || department.contains("emergency");
                        }

                        if (!matches) {
                            System.out.println("\n[WARNING] Doctor's specialization does not align with patient's disease!");
                            System.out.print("Do you still want to assign this doctor? (yes/no): ");
                            String confirm = scanner.nextLine().trim().toLowerCase();
                            if (!confirm.equals("yes") && !confirm.equals("y")) {
                                System.out.println("Assignment cancelled.");
                                break;
                            }
                        } else {
                            System.out.println("[MATCH] Doctor's specialization matches patient's medical condition.");
                        }

                        patientService.assignDoctor(patId, docId, true);
                        System.out.println("Doctor assigned successfully!");
                        break;
                    }
                    case 8:
                        System.out.print("Enter Patient ID to Discharge: ");
                        String dischargeId = scanner.nextLine().trim();
                        patientService.dischargePatient(dischargeId);
                        System.out.println("Patient discharged successfully!");
                        break;
                    case 9:
                        handleAutomatedWorkflow(scanner);
                        break;
                    case 10:
                        back = true;
                        break;
                    default:
                        System.out.println("Invalid option.");
                }
            } catch (PatientNotFoundException | InvalidPatientStateException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("General Error: " + e.getMessage());
            }
        }
    }

    // ==========================================
    // AUTOMATED PATIENT TREATMENT WORKFLOW
    // ==========================================
    private static void handleAutomatedWorkflow(Scanner scanner) {
        System.out.println("\n=========================================");
        System.out.println("   AUTOMATED PATIENT TREATMENT WORKFLOW  ");
        System.out.println("=========================================");
        
        Patient patient = null;
        System.out.println("Choose patient selection method:");
        System.out.println("1. Existing Patient");
        System.out.println("2. Register New Patient");
        int sel = readIntInput(scanner, "Enter choice (1-2): ");
        
        try {
            if (sel == 1) {
                System.out.print("Enter Patient ID: ");
                String pid = scanner.nextLine().trim();
                patient = patientService.findPatient(pid);
            } else {
                System.out.println("\n--- Step 1: Patient Registration ---");
                System.out.print("Enter Patient ID (e.g. P022): ");
                String id = scanner.nextLine().trim();
                if (patientService.getAllPatients().stream().anyMatch(pat -> pat.getPatientId().equalsIgnoreCase(id))) {
                    System.out.println("[ERROR] Duplicate Patient ID! Aborting workflow.");
                    return;
                }
                
                System.out.println("Patient Type:\n1. Inpatient (IP)\n2. Outpatient (OP)");
                int typeSel = readIntInput(scanner, "Choose Type (1-2): ");
                String type = (typeSel == 1) ? "IP" : "OP";
                
                patient = new Patient();
                patient.setPatientId(id);
                patient.setPatientType(type);
                
                if ("IP".equals(type)) {
                    System.out.print("Enter Room Number to assign: ");
                    patient.setRoomNumber(scanner.nextLine().trim());
                    patient.setAdmissionStatus(PatientStatus.ADMITTED);
                    patient.setAdmissionDate(LocalDate.now().toString());
                } else {
                    patient.setRoomNumber("N/A (OP)");
                }
                
                System.out.print("Enter Patient Name: ");
                patient.setName(scanner.nextLine().trim());
                patient.setAge(readIntInput(scanner, "Enter Patient Age: "));
                System.out.print("Enter Gender: ");
                patient.setGender(scanner.nextLine().trim());
                System.out.print("Enter Phone: ");
                patient.setPhoneNumber(scanner.nextLine().trim());
                System.out.print("Enter Address: ");
                patient.setAddress(scanner.nextLine().trim());
                System.out.print("Enter Blood Group: ");
                patient.setBloodGroup(scanner.nextLine().trim());
                System.out.print("Enter Primary Complaint / Disease: ");
                patient.setDisease(scanner.nextLine().trim());
                
                // For OP, assign a doctor now
                if ("OP".equals(type)) {
                    System.out.println("Assigning doctor to Outpatient...");
                    for (Doctor dIter : doctorService.getAllDoctors()) {
                        System.out.println(" - " + dIter.getDoctorId() + ": " + dIter.getName());
                    }
                    System.out.print("Select Doctor ID: ");
                    String did = scanner.nextLine().trim();
                    if (!doctorService.doctorExists(did)) {
                        System.out.println("Invalid Doctor ID. Aborting workflow.");
                        return;
                    }
                    patient.setAssignedDoctorId(did);
                }
                
                patientService.addPatient(patient);
                System.out.println("Registration Complete!");
            }
            
            // Assign doctor if not already assigned
            if ("N/A".equals(patient.getAssignedDoctorId()) || patient.getAssignedDoctorId() == null) {
                System.out.println("\nNo Doctor Assigned. Please assign a doctor to continue treatment:");
                for (Doctor dIter : doctorService.getAllDoctors()) {
                    System.out.println(" - " + dIter.getDoctorId() + ": " + dIter.getName() + " (" + dIter.getSpecialization() + ")");
                }
                System.out.print("Enter Doctor ID: ");
                String did = scanner.nextLine().trim();
                if (!doctorService.doctorExists(did)) {
                    System.out.println("Invalid Doctor ID. Aborting workflow.");
                    return;
                }
                patient.setAssignedDoctorId(did);
                if ("IP".equalsIgnoreCase(patient.getPatientType())) {
                    patient.setAdmissionStatus(PatientStatus.ADMITTED);
                }
            }
            
            Doctor doctor = doctorService.findDoctor(patient.getAssignedDoctorId());
            
            // --- Step 2: Diagnostic Tests ---
            System.out.println("\n--- Step 2: Diagnostic Tests Determination ---");
            System.out.print("Are diagnostic tests required for " + patient.getName() + "? (yes/no): ");
            String testRequired = scanner.nextLine().trim().toLowerCase();
            
            double testCharges = 0;
            ArrayList<String> completedTests = new ArrayList<>();
            if (testRequired.equals("yes") || testRequired.equals("y")) {
                System.out.println("\nGenerating Test Reports...");
                System.out.println("Executing Blood Test... Result: Normal [Charge: $50.00]");
                testCharges += 50.00; completedTests.add("Blood Test ($50)");
                
                System.out.println("Executing Sugar Test... Result: 96 mg/dL [Charge: $30.00]");
                testCharges += 30.00; completedTests.add("Sugar Test ($30)");
                
                System.out.println("Executing BP Test... Result: 120/80 mmHg [Charge: $20.00]");
                testCharges += 20.00; completedTests.add("BP Test ($20)");
                
                System.out.println("Executing Weight Test... Result: Healthy BMI [Charge: $10.00]");
                testCharges += 10.00; completedTests.add("Weight Test ($10)");
            } else {
                System.out.println("No diagnostic tests ordered.");
            }
            
            // --- Step 3: Doctor Examination ---
            System.out.println("\n--- Step 3: Doctor Examination ---");
            System.out.println("Doctor " + doctor.getName() + " is reviewing diagnostic reports and examining " + patient.getName() + "...");
            System.out.println("Diagnosis: Symptoms align with " + patient.getDisease() + ". Initiating treatment plan.");
            
            // --- Step 4 & 5: Prescription & Medicine Cost Calculation ---
            System.out.println("\n--- Step 4: Generating Prescription & Calculating Medicine Cost ---");
            double medicineCost = 0;
            ArrayList<String> prescribedMeds = new ArrayList<>();
            
            String disease = patient.getDisease().toLowerCase();
            if (disease.contains("heart") || disease.contains("cardio")) {
                prescribedMeds.add("Aspirin - $15.00"); medicineCost += 15.00;
                prescribedMeds.add("Clopidogrel - $35.00"); medicineCost += 35.00;
                prescribedMeds.add("Atorvastatin - $45.00"); medicineCost += 45.00;
            } else if (disease.contains("preg") || disease.contains("gyn")) {
                prescribedMeds.add("Prenatal Vitamins - $20.00"); medicineCost += 20.00;
                prescribedMeds.add("Folic Acid - $12.00"); medicineCost += 12.00;
            } else if (disease.contains("fracture") || disease.contains("bone")) {
                prescribedMeds.add("Ibuprofen (Painkiller) - $10.00"); medicineCost += 10.00;
                prescribedMeds.add("Calcium Supplements - $22.00"); medicineCost += 22.00;
            } else {
                prescribedMeds.add("Amoxicillin (Antibiotic) - $25.00"); medicineCost += 25.00;
                prescribedMeds.add("Paracetamol - $8.00"); medicineCost += 8.00;
            }
            
            System.out.println("Prescription Details:");
            for (String med : prescribedMeds) {
                System.out.println(" - " + med);
            }
            System.out.printf("Total Medicine Cost: $%.2f\n", medicineCost);
            
            // --- Step 6: Total Bill Generation ---
            System.out.println("\n--- Step 5: Total Bill Generation ---");
            double consultationFee = 50.00;
            double roomCharges = 0;
            
            if ("IP".equalsIgnoreCase(patient.getPatientType())) {
                int days = 3; // Default stay calculation
                roomCharges = days * 150.00;
                System.out.printf("Inpatient Room Charges (%d days @ $150/day): $%.2f\n", days, roomCharges);
            } else {
                System.out.println("Patient Type: Outpatient (OP) - No Room Charges applicable.");
            }
            
            double totalBill = consultationFee + testCharges + medicineCost + roomCharges;
            
            System.out.println("\n=========================================");
            System.out.println("           FINAL BILL RECEIPT            ");
            System.out.println("=========================================");
            System.out.printf("1. Consultation Fee:         $%7.2f\n", consultationFee);
            System.out.printf("2. Diagnostic Test Charges:  $%7.2f\n", testCharges);
            System.out.printf("3. Medication Charges:       $%7.2f\n", medicineCost);
            System.out.printf("4. Room & Ward Charges:      $%7.2f\n", roomCharges);
            System.out.println("-----------------------------------------");
            System.out.printf("GRAND TOTAL AMOUNT DUE:      $%7.2f\n", totalBill);
            System.out.println("=========================================");
            
            System.out.print("Process final payment collection? (yes/no): ");
            String payConfirm = scanner.nextLine().trim().toLowerCase();
            if (payConfirm.equals("yes") || payConfirm.equals("y")) {
                if ("IP".equalsIgnoreCase(patient.getPatientType())) {
                    patient.setAdmissionStatus(PatientStatus.DISCHARGED);
                    patient.setDischargeDate(LocalDate.now().toString());
                }
                ReportService.log(String.format("Auto Treatment Workflow Completed. Payment of $%.2f collected for Patient: %s (%s)", totalBill, patient.getName(), patient.getPatientId()));
                System.out.println("Payment recorded! Patient records updated.");
            } else {
                System.out.println("Billing skipped. Patient records remain open.");
            }
            
        } catch (Exception e) {
            System.out.println("Workflow Error: " + e.getMessage());
        }
    }

    // ==========================================
    // DOCTOR MENU HANDLER
    // ==========================================
    private static void handleDoctorMenu(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- DOCTOR MANAGEMENT ---");
            System.out.println("1. Add Doctor");
            System.out.println("2. Update Doctor");
            System.out.println("3. View Doctor Details");
            System.out.println("4. View All Doctors");
            System.out.println("5. View Assigned Patients");
            System.out.println("6. Verify Doctor Certificates (Admin Action)");
            System.out.println("7. Back to Main Menu");

            int choice = readIntInput(scanner, "Enter choice: ");
            try {
                switch (choice) {
                    case 1: {
                        System.out.print("Enter Doctor ID (e.g. D021): ");
                        String id = scanner.nextLine().trim();
                        if (doctorService.doctorExists(id)) {
                            System.out.println("\n[ERROR] Duplicate ID: Doctor ID " + id + " already exists in the system!");
                            break;
                        }

                        System.out.print("Enter Name: ");
                        String name = scanner.nextLine().trim();
                        int age = readIntInput(scanner, "Enter Age: ");
                        System.out.print("Enter Gender: ");
                        String gender = scanner.nextLine().trim();
                        System.out.print("Enter Phone: ");
                        String phone = scanner.nextLine().trim();
                        System.out.print("Enter Email: ");
                        String email = scanner.nextLine().trim();
                        System.out.print("Enter Department: ");
                        String dept = scanner.nextLine().trim();
                        System.out.print("Enter Specialization: ");
                        String spec = scanner.nextLine().trim();
                        System.out.print("Enter Qualification: ");
                        String qual = scanner.nextLine().trim();
                        int exp = readIntInput(scanner, "Enter Experience (Years): ");
                        
                        System.out.println("Shifts: MORNING, AFTERNOON, EVENING, NIGHT, GENERAL, ON_CALL, OFF");
                        System.out.print("Enter Shift: ");
                        Shift shift = Shift.valueOf(scanner.nextLine().trim().toUpperCase());
                        
                        System.out.println("Availabilities: AVAILABLE, BUSY, ON_LEAVE");
                        System.out.print("Enter Availability: ");
                        DoctorAvailability avail = DoctorAvailability.valueOf(scanner.nextLine().trim().toUpperCase());
                        
                        System.out.print("Enter Salary: ");
                        double salary = Double.parseDouble(scanner.nextLine().trim());

                        Doctor doc = new Doctor(id, name, age, gender, phone, email, dept, spec, qual, exp, shift, avail, salary, "UNVERIFIED");
                        doctorService.addDoctor(doc);
                        System.out.println("Doctor added successfully!");
                        break;
                    }
                    case 2:
                        System.out.print("Enter Doctor ID to Update: ");
                        String updateId = scanner.nextLine().trim();
                        Doctor ex = doctorService.findDoctor(updateId);
                        
                        String uName = promptWithDefault(scanner, "New Name", ex.getName());
                        int uAge = Integer.parseInt(promptWithDefault(scanner, "New Age", String.valueOf(ex.getAge())));
                        String uGender = promptWithDefault(scanner, "New Gender", ex.getGender());
                        String uPhone = promptWithDefault(scanner, "New Phone", ex.getPhone());
                        String uEmail = promptWithDefault(scanner, "New Email", ex.getEmail());
                        String uDept = promptWithDefault(scanner, "New Department", ex.getDepartment());
                        String uSpec = promptWithDefault(scanner, "New Specialization", ex.getSpecialization());
                        String uQual = promptWithDefault(scanner, "New Qualification", ex.getQualification());
                        int uExp = Integer.parseInt(promptWithDefault(scanner, "New Experience", String.valueOf(ex.getExperience())));
                        
                        System.out.println("Shifts: MORNING, AFTERNOON, EVENING, NIGHT, GENERAL, ON_CALL, OFF");
                        String uShiftStr = promptWithDefault(scanner, "New Shift", ex.getShift().name());
                        Shift uShift = Shift.valueOf(uShiftStr.toUpperCase());
                        
                        System.out.println("Availabilities: AVAILABLE, BUSY, ON_LEAVE");
                        String uAvailStr = promptWithDefault(scanner, "New Availability", ex.getAvailability().name());
                        DoctorAvailability uAvail = DoctorAvailability.valueOf(uAvailStr.toUpperCase());
                        
                        double uSalary = Double.parseDouble(promptWithDefault(scanner, "New Salary", String.valueOf(ex.getSalary())));

                        doctorService.updateDoctor(updateId, uName, uAge, uGender, uPhone, uEmail, uDept, uSpec, uQual, uExp, uShift, uAvail, uSalary);
                        System.out.println("Doctor updated successfully!");
                        break;
                    case 3:
                        System.out.print("Enter Doctor ID to View: ");
                        String viewId = scanner.nextLine().trim();
                        Doctor d = doctorService.findDoctor(viewId);
                        System.out.println("\n--- Doctor Details ---");
                        System.out.println("ID: " + d.getDoctorId());
                        System.out.println("Name: " + d.getName());
                        System.out.println("Verification Status: " + d.getCertificateStatus());
                        System.out.println("Age: " + d.getAge());
                        System.out.println("Gender: " + d.getGender());
                        System.out.println("Phone: " + d.getPhone());
                        System.out.println("Email: " + d.getEmail());
                        System.out.println("Department: " + d.getDepartment());
                        System.out.println("Specialization: " + d.getSpecialization());
                        System.out.println("Qualification: " + d.getQualification());
                        System.out.println("Experience: " + d.getExperience() + " years");
                        System.out.println("Shift: " + d.getShift());
                        System.out.println("Availability: " + d.getAvailability());
                        System.out.println("Salary: $" + d.getSalary());
                        break;
                    case 4:
                        System.out.println("\n--- ALL DOCTORS ---");
                        ArrayList<Doctor> list = doctorService.getAllDoctors();
                        if (list.isEmpty()) {
                            System.out.println("No doctors in system.");
                        } else {
                            for (Doctor docIter : list) {
                                System.out.println(docIter);
                            }
                        }
                        break;
                    case 5:
                        System.out.print("Enter Doctor ID: ");
                        String doctorId = scanner.nextLine().trim();
                        ArrayList<Patient> assigned = doctorService.getAssignedPatients(doctorId, patientService.getAllPatients());
                        System.out.println("\n--- Assigned Admitted Patients ---");
                        if (assigned.isEmpty()) {
                            System.out.println("No patients assigned.");
                        } else {
                            for (Patient pObj : assigned) {
                                System.out.println(pObj);
                            }
                        }
                        break;
                    case 6:
                        System.out.println("\n--- VERIFY DOCTOR CERTIFICATES ---");
                        for (Doctor docIter : doctorService.getAllDoctors()) {
                            System.out.println("ID: " + docIter.getDoctorId() + " | Name: " + docIter.getName() + " | Cert Status: " + docIter.getCertificateStatus());
                        }
                        System.out.print("Enter Doctor ID to verify: ");
                        String verifyId = scanner.nextLine().trim();
                        Doctor toVerify = doctorService.findDoctor(verifyId);
                        
                        System.out.println("Select Verification Status:");
                        System.out.println("1. GENUINE");
                        System.out.println("2. FAKE");
                        int vChoice = readIntInput(scanner, "Choice (1-2): ");
                        String status = (vChoice == 1) ? "GENUINE" : "FAKE";
                        
                        toVerify.setCertificateStatus(status);
                        ReportService.log(String.format("Admin verified Doctor %s's certificate as %s.", verifyId, status));
                        System.out.println("Doctor certificate status updated successfully!");
                        break;
                    case 7:
                        back = true;
                        break;
                    default:
                        System.out.println("Invalid option.");
                }
            } catch (DoctorNotFoundException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    // ==========================================
    // STAFF MENU HANDLER
    // ==========================================
    private static void handleStaffMenu(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- STAFF MANAGEMENT ---");
            System.out.println("1. Add Staff");
            System.out.println("2. Update Staff");
            System.out.println("3. View Staff Details");
            System.out.println("4. Remove Staff");
            System.out.println("5. View All Staff");
            System.out.println("6. Back to Main Menu");

            int choice = readIntInput(scanner, "Enter choice: ");
            try {
                switch (choice) {
                    case 1: {
                        System.out.print("Enter Staff ID (e.g. S021): ");
                        String id = scanner.nextLine().trim();
                        try {
                            Staff existing = staffService.findStaff(id);
                            System.out.println("\n[ERROR] Duplicate ID: Staff ID " + id + " already exists in the system!");
                            break;
                        } catch (StaffNotFoundException e) {
                            // Unique ID, proceed
                        }

                        System.out.print("Enter Name: ");
                        String name = scanner.nextLine().trim();
                        int age = readIntInput(scanner, "Enter Age: ");
                        System.out.print("Enter Gender: ");
                        String gender = scanner.nextLine().trim();
                        System.out.print("Enter Phone: ");
                        String phone = scanner.nextLine().trim();
                        System.out.print("Enter Department: ");
                        String dept = scanner.nextLine().trim();
                        
                        System.out.println("Select Role:");
                        System.out.println("1. Nurse");
                        System.out.println("2. Receptionist");
                        System.out.println("3. Cleaning Staff");
                        System.out.println("4. Watchman Staff");
                        System.out.println("5. Lab Technician");
                        System.out.println("6. Pharmacist");
                        System.out.println("7. Accountant");
                        System.out.println("8. Ward Boy");
                        System.out.println("9. Security");
                        
                        int roleChoice = readIntInput(scanner, "Enter choice (1-9): ");
                        String role;
                        switch (roleChoice) {
                            case 1: role = "Nurse"; break;
                            case 2: role = "Receptionist"; break;
                            case 3: role = "Cleaning Staff"; break;
                            case 4: role = "Watchman Staff"; break;
                            case 5: role = "Lab Technician"; break;
                            case 6: role = "Pharmacist"; break;
                            case 7: role = "Accountant"; break;
                            case 8: role = "Ward Boy"; break;
                            case 9: role = "Security"; break;
                            default: role = "Security";
                        }
                        
                        System.out.print("Enter Joining Date (YYYY-MM-DD): ");
                        String joinDate = scanner.nextLine().trim();
                        Shift shift = Shift.GENERAL;
                        System.out.println("Assigning Shift: GENERAL (Automatically Assigned)");
                        
                        System.out.print("Enter Salary: ");
                        double salary = Double.parseDouble(scanner.nextLine().trim());
                        
                        System.out.println("Status: ACTIVE, ON_LEAVE, INACTIVE");
                        System.out.print("Enter Status: ");
                        StaffStatus status = StaffStatus.valueOf(scanner.nextLine().trim().toUpperCase());

                        Staff s = new Staff(id, name, age, gender, phone, dept, role, shift, salary, status, joinDate);
                        staffService.addStaff(s);
                        System.out.println("Staff member added successfully!");
                        break;
                    }
                    case 2:
                        System.out.print("Enter Staff ID to Update: ");
                        String updateId = scanner.nextLine().trim();
                        Staff ex = staffService.findStaff(updateId);
                        
                        String uName = promptWithDefault(scanner, "New Name", ex.getName());
                        int uAge = Integer.parseInt(promptWithDefault(scanner, "New Age", String.valueOf(ex.getAge())));
                        String uGender = promptWithDefault(scanner, "New Gender", ex.getGender());
                        String uPhone = promptWithDefault(scanner, "New Phone", ex.getPhone());
                        String uDept = promptWithDefault(scanner, "New Department", ex.getDepartment());
                        String uRole = promptWithDefault(scanner, "New Role", ex.getRole());
                        
                        System.out.println("Shifts: MORNING, AFTERNOON, EVENING, NIGHT, GENERAL, ON_CALL, OFF");
                        Shift uShift = Shift.valueOf(promptWithDefault(scanner, "New Shift", ex.getShift().name()).toUpperCase());
                        
                        double uSalary = Double.parseDouble(promptWithDefault(scanner, "New Salary", String.valueOf(ex.getSalary())));
                        
                        System.out.println("Status: ACTIVE, ON_LEAVE, INACTIVE");
                        StaffStatus uStatus = StaffStatus.valueOf(promptWithDefault(scanner, "New Status", ex.getStatus().name()).toUpperCase());

                        staffService.updateStaff(updateId, uName, uAge, uGender, uPhone, uDept, uRole, uShift, uSalary, uStatus);
                        System.out.println("Staff member updated successfully!");
                        break;
                    case 3:
                        System.out.print("Enter Staff ID to View: ");
                        String viewId = scanner.nextLine().trim();
                        Staff st = staffService.findStaff(viewId);
                        System.out.println("\n--- Staff Details ---");
                        System.out.println("ID: " + st.getStaffId());
                        System.out.println("Name: " + st.getName());
                        System.out.println("Age: " + st.getAge());
                        System.out.println("Gender: " + st.getGender());
                        System.out.println("Phone: " + st.getPhone());
                        System.out.println("Department: " + st.getDepartment());
                        System.out.println("Role: " + st.getRole());
                        System.out.println("Shift: " + st.getShift());
                        System.out.println("Salary: $" + st.getSalary());
                        System.out.println("Status: " + st.getStatus());
                        System.out.println("Joining Date: " + st.getJoiningDate());
                        break;
                    case 4:
                        System.out.print("Enter Staff ID to Remove: ");
                        String removeId = scanner.nextLine().trim();
                        staffService.removeStaff(removeId);
                        System.out.println("Staff member removed successfully!");
                        break;
                    case 5:
                        System.out.println("\n--- ALL STAFF ---");
                        ArrayList<Staff> list = staffService.getAllStaff();
                        if (list.isEmpty()) {
                            System.out.println("No staff in system.");
                        } else {
                            for (Staff staffIter : list) {
                                System.out.println(staffIter);
                            }
                        }
                        break;
                    case 6:
                        back = true;
                        break;
                    default:
                        System.out.println("Invalid option.");
                }
            } catch (StaffNotFoundException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    // ==========================================
    // EMERGENCY MENU HANDLER
    // ==========================================
    private static void handleEmergencyMenu(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- EMERGENCY MANAGEMENT ---");
            System.out.println("1. Raise Emergency Call");
            System.out.println("2. Back to Main Menu");

            int choice = readIntInput(scanner, "Enter choice: ");
            try {
                switch (choice) {
                    case 1:
                        System.out.print("Enter Emergency Contact Number: ");
                        String contactNumber = scanner.nextLine().trim();
                        
                        Emergency e = emergencyService.raiseEmergency("UNKNOWN", "Emergency Call", EmergencyPriority.CRITICAL, true);
                        ReportService.log(String.format("Emergency raised from contact number: %s. Case ID: %s", contactNumber, e.getEmergencyId()));
                        
                        System.out.println("\n[EMERGENCY CALL RECORDED]");
                        System.out.println("Case ID: " + e.getEmergencyId());
                        System.out.println("Contact Number: " + contactNumber);
                        System.out.println("Dispatching ambulance and assigning emergency doctors...");
                        break;
                    case 2:
                        back = true;
                        break;
                    default:
                        System.out.println("Invalid option. Please choose 1 or 2.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    // ==========================================
    // REPORT MENU HANDLER
    // ==========================================
    private static void handleReportMenu(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- REPORT MENU ---");
            System.out.println("1. Patient Report (Generates File)");
            System.out.println("2. Doctor Summary Report");
            System.out.println("3. Staff Summary Report");
            System.out.println("4. Emergency Summary Report");
            System.out.println("5. Hospital Summary Dashboard");
            System.out.println("6. Back to Main Menu");

            int choice = readIntInput(scanner, "Enter choice: ");
            switch (choice) {
                case 1:
                    ReportService.generatePatientReport(patientService.getAllPatients());
                    break;
                case 2:
                    System.out.println("\n--- DOCTOR REPORT ---");
                    for (Doctor d : doctorService.getAllDoctors()) {
                        System.out.println(d);
                    }
                    break;
                case 3:
                    System.out.println("\n--- STAFF REPORT ---");
                    for (Staff s : staffService.getAllStaff()) {
                        System.out.println(s);
                    }
                    break;
                case 4:
                    System.out.println("\n--- EMERGENCY REPORT ---");
                    if (emergencyService.getAllEmergencies().isEmpty()) {
                        System.out.println("No emergency records found.");
                    } else {
                        for (Emergency em : emergencyService.getAllEmergencies()) {
                            System.out.println(em);
                        }
                    }
                    break;
                case 5:
                    String summary = ReportService.getHospitalSummary(
                        patientService.getAllPatients(),
                        doctorService.getAllDoctors(),
                        staffService.getAllStaff(),
                        emergencyService.getAllEmergencies()
                    );
                    System.out.println("\n" + summary);
                    break;
                case 6:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    // ==========================================
    // FEE PAYMENT MODULE MENU
    // ==========================================
    private static void handleFeePaymentMenu(Scanner scanner) {
        System.out.println("\n=========================================");
        System.out.println("          FEE PAYMENT MODULE             ");
        System.out.println("=========================================");
        System.out.print("Enter Patient ID: ");
        String patientId = scanner.nextLine().trim();

        try {
            Patient p = patientService.findPatient(patientId);
            System.out.println("\nPatient Found: " + p.getName() + " (" + p.getPatientType() + ")");
            
            if ("OP".equalsIgnoreCase(p.getPatientType())) {
                System.out.println("Consultation Fee Due: $50.00");
                System.out.print("Confirm collection of Consultation Fee? (yes/no): ");
                String confirm = scanner.nextLine().trim().toLowerCase();
                if (confirm.equals("yes") || confirm.equals("y")) {
                    ReportService.log(String.format("Consultation fee ($50) collected for Outpatient: %s (%s)", p.getName(), patientId));
                    System.out.println("Payment processed successfully! Receipt issued.");
                } else {
                    System.out.println("Payment cancelled.");
                }
            } else {
                System.out.println("\nInpatient Billing Menu:");
                System.out.println("1. Collect Consultation Fee ($50.00)");
                System.out.println("2. Settle Total Bill (Discharge / Final Billing)");
                int option = readIntInput(scanner, "Select choice (1-2): ");
                
                if (option == 1) {
                    ReportService.log(String.format("Consultation fee ($50) collected for Inpatient: %s (%s)", p.getName(), patientId));
                    System.out.println("Consultation fee collected successfully!");
                } else if (option == 2) {
                    System.out.println("\n--- Total Bill Breakdown ---");
                    double consultationFee = 50.00;
                    double roomRate = 150.00;
                    int daysHospitalized = (p.getAdmissionStatus() == PatientStatus.DISCHARGED) ? 5 : 3;
                    double roomCharges = daysHospitalized * roomRate;
                    double treatmentCharges = 300.00;
                    double totalBill = consultationFee + roomCharges + treatmentCharges;

                    System.out.printf("1. Consultation Fee:         $%7.2f\n", consultationFee);
                    System.out.printf("2. Room Charges (%d days):    $%7.2f\n", daysHospitalized, roomCharges);
                    System.out.printf("3. Treatment & Medications:  $%7.2f\n", treatmentCharges);
                    System.out.println("-----------------------------------------");
                    System.out.printf("TOTAL AMOUNT DUE:            $%7.2f\n", totalBill);
                    System.out.println("-----------------------------------------");
                    
                    System.out.print("Confirm collection of total Inpatient Bill? (yes/no): ");
                    String confirm = scanner.nextLine().trim().toLowerCase();
                    if (confirm.equals("yes") || confirm.equals("y")) {
                        ReportService.log(String.format("Total Bill of $%.2f collected for Inpatient: %s (%s)", totalBill, p.getName(), patientId));
                        System.out.println("Final payment settled! Roster records archived.");
                    } else {
                        System.out.println("Payment cancelled.");
                    }
                } else {
                    System.out.println("Invalid option.");
                }
            }
        } catch (PatientNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ==========================================
    // SAVE DATA
    // ==========================================
    private static void saveAllData() {
        System.out.println("\nSaving data to CSV database...");
        CSVWriter.writePatients(PATIENTS_CSV, patientService.getAllPatients());
        CSVWriter.writeDoctors(DOCTORS_CSV, doctorService.getAllDoctors());
        CSVWriter.writeStaff(STAFF_CSV, staffService.getAllStaff());
        System.out.println("Data saved successfully!");
        ReportService.log("Hospital data saved to CSV files.");
    }

    // ==========================================
    // UTILITY METHODS
    // ==========================================
    private static int readIntInput(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
            }
        }
    }

    private static String promptWithDefault(Scanner scanner, String promptName, String defaultValue) {
        System.out.print(promptName + " [" + defaultValue + "]: ");
        String val = scanner.nextLine().trim();
        return val.isEmpty() ? defaultValue : val;
    }
}
