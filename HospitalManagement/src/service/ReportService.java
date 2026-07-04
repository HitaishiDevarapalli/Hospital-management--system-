package service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import model.Patient;
import model.Doctor;
import model.Staff;
import model.Emergency;
import model.enums.PatientStatus;
import model.enums.DoctorAvailability;
import model.enums.EmergencyPriority;

public class ReportService {
    private static final String AUDIT_LOG_PATH = "HospitalManagement/output/audit_log.txt";
    private static final String PATIENT_REPORT_PATH = "HospitalManagement/output/patient_report.txt";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final int TOTAL_BED_CAPACITY = 100;

    static {
        // Ensure output directory exists
        new File("HospitalManagement/output").mkdirs();
    }

    public static void log(String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(AUDIT_LOG_PATH, true))) {
            bw.write(String.format("[%s] %s", timestamp, message));
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to audit log: " + e.getMessage());
        }
    }

    public static void generatePatientReport(ArrayList<Patient> patients) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PATIENT_REPORT_PATH))) {
            bw.write("=========================================================================");
            bw.newLine();
            bw.write("                      MEDICORE PATIENT DISCHARGE & ADMISSION REPORT      ");
            bw.newLine();
            bw.write("                      Generated on: " + LocalDateTime.now().format(formatter));
            bw.newLine();
            bw.write("=========================================================================");
            bw.newLine();
            bw.newLine();
            
            bw.write(String.format("%-10s %-20s %-5s %-10s %-15s %-15s %-10s", 
                "ID", "Name", "Age", "Gender", "Status", "Room", "Doctor ID"));
            bw.newLine();
            bw.write("-------------------------------------------------------------------------");
            bw.newLine();

            for (Patient p : patients) {
                bw.write(String.format("%-10s %-20s %-5d %-10s %-15s %-15s %-10s",
                    p.getPatientId(),
                    p.getName().length() > 18 ? p.getName().substring(0, 18) : p.getName(),
                    p.getAge(),
                    p.getGender(),
                    p.getAdmissionStatus().name(),
                    p.getRoomNumber(),
                    p.getAssignedDoctorId()
                ));
                bw.newLine();
            }
            bw.write("-------------------------------------------------------------------------");
            bw.newLine();
            bw.write("Total Patient Records: " + patients.size());
            bw.newLine();
            log("Patient report generated successfully.");
            System.out.println("Patient report successfully written to " + PATIENT_REPORT_PATH);
        } catch (IOException e) {
            System.err.println("Error generating patient report: " + e.getMessage());
        }
    }

    public static String getHospitalSummary(ArrayList<Patient> patients, ArrayList<Doctor> doctors, ArrayList<Staff> staff, ArrayList<Emergency> emergencies) {
        int totalPatients = patients.size();
        int admittedCount = 0;
        int dischargedCount = 0;
        Map<String, Integer> doctorPatientCount = new HashMap<>();
        Map<String, Integer> diseaseCount = new HashMap<>();

        for (Patient p : patients) {
            if (p.getAdmissionStatus() == PatientStatus.ADMITTED) {
                admittedCount++;
            } else if (p.getAdmissionStatus() == PatientStatus.DISCHARGED) {
                dischargedCount++;
            }
            
            if (p.getAdmissionStatus() == PatientStatus.ADMITTED && !p.getAssignedDoctorId().equals("N/A")) {
                doctorPatientCount.put(p.getAssignedDoctorId(), doctorPatientCount.getOrDefault(p.getAssignedDoctorId(), 0) + 1);
            }

            if (p.getDisease() != null && !p.getDisease().trim().isEmpty()) {
                String dName = p.getDisease().trim();
                diseaseCount.put(dName, diseaseCount.getOrDefault(dName, 0) + 1);
            }
        }

        int availableDoctors = 0;
        for (Doctor d : doctors) {
            if (d.getAvailability() == DoctorAvailability.AVAILABLE) {
                availableDoctors++;
            }
        }

        int availableBeds = TOTAL_BED_CAPACITY - admittedCount;
        int totalStaff = staff.size();
        int totalEmergencies = emergencies.size();
        int criticalEmergencies = 0;
        for (Emergency e : emergencies) {
            if (e.getPriority() == EmergencyPriority.CRITICAL) {
                criticalEmergencies++;
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=========================================\n");
        sb.append("MEDICORE HOSPITAL MANAGEMENT SUMMARY\n");
        sb.append("=========================================\n");
        sb.append(String.format("Total Patients Records: %d\n", totalPatients));
        sb.append(String.format("Admitted Patients:      %d\n", admittedCount));
        sb.append(String.format("Discharged Patients:    %d\n", dischargedCount));
        sb.append(String.format("Available Beds:         %d / %d\n", Math.max(0, availableBeds), TOTAL_BED_CAPACITY));
        sb.append(String.format("Total Doctors:          %d\n", doctors.size()));
        sb.append(String.format("Available Doctors:      %d\n", availableDoctors));
        sb.append(String.format("Total Staff Members:    %d\n", totalStaff));
        sb.append(String.format("Emergency Cases:        %d\n", totalEmergencies));
        sb.append(String.format("Critical Cases:         %d\n", criticalEmergencies));
        sb.append("\n--- Doctor-wise Patient Load (Admitted Patients) ---\n");
        if (doctorPatientCount.isEmpty()) {
            sb.append("No active patient load assigned to doctors.\n");
        } else {
            for (Map.Entry<String, Integer> entry : doctorPatientCount.entrySet()) {
                String docName = entry.getKey();
                // Find doctor name if possible
                for (Doctor d : doctors) {
                    if (d.getDoctorId().equalsIgnoreCase(entry.getKey())) {
                        docName = d.getName() + " (" + d.getDoctorId() + ")";
                        break;
                    }
                }
                sb.append(String.format("Doctor %s: %d patients\n", docName, entry.getValue()));
            }
        }
        sb.append("\n--- Disease-wise Patient Distribution ---\n");
        if (diseaseCount.isEmpty()) {
            sb.append("No patient disease data recorded.\n");
        } else {
            for (Map.Entry<String, Integer> entry : diseaseCount.entrySet()) {
                sb.append(String.format("%-25s: %d patients\n", entry.getKey(), entry.getValue()));
            }
        }
        sb.append("=========================================");
        return sb.toString();
    }
}
