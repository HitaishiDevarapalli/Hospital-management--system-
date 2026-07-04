package file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import model.Patient;
import model.Doctor;
import model.Staff;
import model.enums.PatientStatus;
import model.enums.DoctorAvailability;
import model.enums.StaffStatus;
import model.enums.Shift;

public class CSVReader {

    private static String[] parseCsvLine(String line) {
        ArrayList<String> fields = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        fields.add(sb.toString().trim());
        return fields.toArray(new String[0]);
    }

    public static ArrayList<Patient> readPatients(String filePath) {
        ArrayList<Patient> patients = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return patients;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = parseCsvLine(line);
                if (parts.length >= 14) {
                    Patient p = new Patient(
                        parts[0], // ID
                        parts[1], // Name
                        Integer.parseInt(parts[2]), // Age
                        parts[3], // Gender
                        parts[4], // Phone Number
                        parts[5], // Address
                        parts[6], // Blood Group
                        parts[7], // Disease
                        PatientStatus.valueOf(parts[8]), // Admission Status
                        parts[9], // Assigned Doctor ID
                        parts[10], // Admission Date
                        parts[11], // Discharge Date
                        parts[12], // Room Number
                        parts[13]  // Patient Type (IP/OP)
                    );
                    patients.add(p);
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Error reading patients CSV: " + e.getMessage());
        }
        return patients;
    }

    public static ArrayList<Doctor> readDoctors(String filePath) {
        ArrayList<Doctor> doctors = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return doctors;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = parseCsvLine(line);
                if (parts.length >= 14) {
                    Doctor d = new Doctor(
                        parts[0], // ID
                        parts[1], // Name
                        Integer.parseInt(parts[2]), // Age
                        parts[3], // Gender
                        parts[4], // Phone
                        parts[5], // Email
                        parts[6], // Department
                        parts[7], // Specialization
                        parts[8], // Qualification
                        Integer.parseInt(parts[9]), // Experience
                        Shift.valueOf(parts[10]), // Shift
                        DoctorAvailability.valueOf(parts[11]), // Availability
                        Double.parseDouble(parts[12]), // Salary
                        parts[13] // Certificate Status
                    );
                    doctors.add(d);
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Error reading doctors CSV: " + e.getMessage());
        }
        return doctors;
    }

    public static ArrayList<Staff> readStaff(String filePath) {
        ArrayList<Staff> staffList = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return staffList;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = parseCsvLine(line);
                if (parts.length >= 11) {
                    Staff s = new Staff(
                        parts[0], // ID
                        parts[1], // Name
                        Integer.parseInt(parts[2]), // Age
                        parts[3], // Gender
                        parts[4], // Phone
                        parts[5], // Department
                        parts[6], // Role
                        Shift.valueOf(parts[7]), // Shift
                        Double.parseDouble(parts[8]), // Salary
                        StaffStatus.valueOf(parts[9]), // Status
                        parts[10] // Joining Date
                    );
                    staffList.add(s);
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Error reading staff CSV: " + e.getMessage());
        }
        return staffList;
    }
}
