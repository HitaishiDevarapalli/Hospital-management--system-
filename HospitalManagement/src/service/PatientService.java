package service;

import exception.InvalidPatientStateException;
import exception.PatientNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import model.Patient;
import model.enums.PatientStatus;
import util.Validator;

public class PatientService {
    private ArrayList<Patient> patients;

    public PatientService(ArrayList<Patient> patients) {
        this.patients = patients;
    }

    public ArrayList<Patient> getAllPatients() {
        return patients;
    }

    public void addPatient(Patient p) throws InvalidPatientStateException {
        // Unique Patient ID check (Required: "do not allow duplicate IDs for Patient, Doctor, Staff, or any other entity")
        for (Patient existing : patients) {
            if (existing.getPatientId().equalsIgnoreCase(p.getPatientId())) {
                throw new InvalidPatientStateException("Duplicate ID Violation: Patient ID " + p.getPatientId() + " already exists!");
            }
        }
        if (!Validator.validateAge(p.getAge())) {
            throw new InvalidPatientStateException("Age cannot be negative!");
        }
        if (!Validator.validatePhoneNumber(p.getPhoneNumber())) {
            throw new InvalidPatientStateException("Invalid phone number format! Must be 10-15 digits.");
        }

        // Rule: If Outpatient (OP), do not assign a Room Number.
        if ("OP".equalsIgnoreCase(p.getPatientType())) {
            p.setRoomNumber("N/A (OP)");
            // Ensure they have a doctor assigned; if not, throw exception to require one
            if (p.getAssignedDoctorId() == null || "N/A".equalsIgnoreCase(p.getAssignedDoctorId())) {
                throw new InvalidPatientStateException("Outpatient (OP) must have an assigned doctor! Please select a doctor.");
            }
        } else {
            // Inpatient (IP) starts with no room until admitted
            p.setRoomNumber("N/A");
        }
        
        patients.add(p);
        ReportService.log(String.format("Patient %s (%s) added. Name: %s", p.getPatientId(), p.getPatientType(), p.getName()));
    }

    public Patient findPatient(String patientId) throws PatientNotFoundException {
        for (Patient p : patients) {
            if (p.getPatientId().equalsIgnoreCase(patientId)) {
                return p;
            }
        }
        throw new PatientNotFoundException("Patient with ID " + patientId + " not found!");
    }

    public void admitPatient(String patientId, String roomNumber) throws PatientNotFoundException, InvalidPatientStateException {
        Patient p = findPatient(patientId);
        
        // Rule: Admitting requires Inpatient (IP) type
        if ("OP".equalsIgnoreCase(p.getPatientType())) {
            throw new InvalidPatientStateException("Outpatient (OP) patients cannot be admitted. Only Inpatients (IP) can be admitted.");
        }
        
        if (p.getAdmissionStatus() == PatientStatus.ADMITTED) {
            throw new InvalidPatientStateException("Already admitted patient cannot be admitted again!");
        }
        
        p.setAdmissionStatus(PatientStatus.ADMITTED);
        p.setRoomNumber(roomNumber);
        p.setAdmissionDate(LocalDate.now().toString());
        p.setDischargeDate("N/A");
        
        ReportService.log(String.format("Patient %s admitted. Room: %s", patientId, roomNumber));
    }

    public void dischargePatient(String patientId) throws PatientNotFoundException, InvalidPatientStateException {
        Patient p = findPatient(patientId);
        if (p.getAdmissionStatus() != PatientStatus.ADMITTED) {
            throw new InvalidPatientStateException("Patient cannot be discharged before admission! Current Status: " + p.getAdmissionStatus());
        }
        
        p.setAdmissionStatus(PatientStatus.DISCHARGED);
        p.setDischargeDate(LocalDate.now().toString());
        
        ReportService.log(String.format("Patient %s discharged.", patientId));
    }

    public void assignDoctor(String patientId, String doctorId, boolean doctorExists) throws PatientNotFoundException, InvalidPatientStateException {
        Patient p = findPatient(patientId);
        
        // Rule: Inpatient must be admitted first before assigning a doctor
        if ("IP".equalsIgnoreCase(p.getPatientType()) && p.getAdmissionStatus() != PatientStatus.ADMITTED) {
            throw new InvalidPatientStateException("Inpatient (IP) can only be assigned a doctor if they are admitted!");
        }
        if (!doctorExists) {
            throw new InvalidPatientStateException("Doctor with ID " + doctorId + " does not exist!");
        }
        
        p.setAssignedDoctorId(doctorId);
        ReportService.log(String.format("Doctor %s assigned to Patient %s.", doctorId, patientId));
    }

    public void updatePatient(String patientId, String name, int age, String gender, String phone, String address, String bloodGroup, String disease) 
            throws PatientNotFoundException, InvalidPatientStateException {
        Patient p = findPatient(patientId);
        if (!Validator.validateAge(age)) {
            throw new InvalidPatientStateException("Age cannot be negative!");
        }
        if (!Validator.validatePhoneNumber(phone)) {
            throw new InvalidPatientStateException("Invalid phone number format!");
        }
        
        p.setName(name);
        p.setAge(age);
        p.setGender(gender);
        p.setPhoneNumber(phone);
        p.setAddress(address);
        p.setBloodGroup(bloodGroup);
        p.setDisease(disease);
        
        ReportService.log(String.format("Patient %s details updated.", patientId));
    }
}
