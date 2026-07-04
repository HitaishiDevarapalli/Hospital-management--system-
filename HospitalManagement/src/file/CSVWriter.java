package file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import model.Patient;
import model.Doctor;
import model.Staff;

public class CSVWriter {

    private static String escapeCsvField(String field) {
        if (field == null) return "";
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }

    public static void writePatients(String filePath, ArrayList<Patient> patients) {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write("PatientID,Name,Age,Gender,PhoneNumber,Address,BloodGroup,Disease,AdmissionStatus,AssignedDoctorID,AdmissionDate,DischargeDate,RoomNumber,PatientType");
            bw.newLine();
            for (Patient p : patients) {
                bw.write(String.format("%s,%s,%d,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                    escapeCsvField(p.getPatientId()),
                    escapeCsvField(p.getName()),
                    p.getAge(),
                    escapeCsvField(p.getGender()),
                    escapeCsvField(p.getPhoneNumber()),
                    escapeCsvField(p.getAddress()),
                    escapeCsvField(p.getBloodGroup()),
                    escapeCsvField(p.getDisease()),
                    p.getAdmissionStatus().name(),
                    escapeCsvField(p.getAssignedDoctorId()),
                    escapeCsvField(p.getAdmissionDate()),
                    escapeCsvField(p.getDischargeDate()),
                    escapeCsvField(p.getRoomNumber()),
                    escapeCsvField(p.getPatientType())
                ));
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing patients CSV: " + e.getMessage());
        }
    }

    public static void writeDoctors(String filePath, ArrayList<Doctor> doctors) {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write("DoctorID,Name,Age,Gender,Phone,Email,Department,Specialization,Qualification,Experience,Shift,Availability,Salary,CertificateStatus");
            bw.newLine();
            for (Doctor d : doctors) {
                bw.write(String.format("%s,%s,%d,%s,%s,%s,%s,%s,%s,%d,%s,%s,%.2f,%s",
                    escapeCsvField(d.getDoctorId()),
                    escapeCsvField(d.getName()),
                    d.getAge(),
                    escapeCsvField(d.getGender()),
                    escapeCsvField(d.getPhone()),
                    escapeCsvField(d.getEmail()),
                    escapeCsvField(d.getDepartment()),
                    escapeCsvField(d.getSpecialization()),
                    escapeCsvField(d.getQualification()),
                    d.getExperience(),
                    d.getShift().name(),
                    d.getAvailability().name(),
                    d.getSalary(),
                    escapeCsvField(d.getCertificateStatus())
                ));
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing doctors CSV: " + e.getMessage());
        }
    }

    public static void writeStaff(String filePath, ArrayList<Staff> staffList) {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write("StaffID,Name,Age,Gender,Phone,Department,Role,Shift,Salary,Status,JoiningDate");
            bw.newLine();
            for (Staff s : staffList) {
                bw.write(String.format("%s,%s,%d,%s,%s,%s,%s,%s,%.2f,%s,%s",
                    escapeCsvField(s.getStaffId()),
                    escapeCsvField(s.getName()),
                    s.getAge(),
                    escapeCsvField(s.getGender()),
                    escapeCsvField(s.getPhone()),
                    escapeCsvField(s.getDepartment()),
                    escapeCsvField(s.getRole()),
                    s.getShift().name(),
                    s.getSalary(),
                    s.getStatus().name(),
                    escapeCsvField(s.getJoiningDate())
                ));
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing staff CSV: " + e.getMessage());
        }
    }
}
