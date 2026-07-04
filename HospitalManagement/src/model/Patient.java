package model;

import model.enums.PatientStatus;

/**
 * CONCEPT: ENCAPSULATION & ACCESS SPECIFIERS
 * 
 * 1. Encapsulation: We bundle the data (private fields) and methods (public getters/setters) 
 *    that operate on the data in a single class. This hides internal details from external classes.
 * 2. Access Specifiers: We use the 'private' keyword to prevent direct mutation of sensitive patient fields, 
 *    and 'public' on constructors and accessors to allow controlled external reading and updating.
 */
public class Patient {
    // --- PRIVATE ACCESS SPECIFIERS (Fields are hidden) ---
    private String patientId;
    private String name;
    private int age;
    private String gender;
    private String phoneNumber;
    private String address;
    private String bloodGroup;
    private String disease;
    private PatientStatus admissionStatus;
    private String assignedDoctorId;
    private String admissionDate;
    private String dischargeDate;
    private String roomNumber;
    private String patientType; // "IP" (Inpatient) or "OP" (Outpatient)

    // --- PUBLIC ACCESS SPECIFIERS (Allowing instantiation) ---
    public Patient() {
        this.admissionStatus = PatientStatus.NOT_ADMITTED;
        this.assignedDoctorId = "N/A";
        this.admissionDate = "N/A";
        this.dischargeDate = "N/A";
        this.roomNumber = "N/A";
        this.patientType = "OP";
    }

    public Patient(String patientId, String name, int age, String gender, String phoneNumber, 
                   String address, String bloodGroup, String disease, PatientStatus admissionStatus, 
                   String assignedDoctorId, String admissionDate, String dischargeDate, String roomNumber,
                   String patientType) {
        this.patientId = patientId;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.bloodGroup = bloodGroup;
        this.disease = disease;
        this.admissionStatus = admissionStatus;
        this.assignedDoctorId = assignedDoctorId;
        this.admissionDate = admissionDate;
        this.dischargeDate = dischargeDate;
        this.roomNumber = roomNumber;
        this.patientType = patientType;
    }

    // --- ENCAPSULATION GETTERS & SETTERS (Controlled interface access) ---
    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public PatientStatus getAdmissionStatus() {
        return admissionStatus;
    }

    public void setAdmissionStatus(PatientStatus admissionStatus) {
        this.admissionStatus = admissionStatus;
    }

    public String getAssignedDoctorId() {
        return assignedDoctorId;
    }

    public void setAssignedDoctorId(String assignedDoctorId) {
        this.assignedDoctorId = assignedDoctorId;
    }

    public String getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(String admissionDate) {
        this.admissionDate = admissionDate;
    }

    public String getDischargeDate() {
        return dischargeDate;
    }

    public void setDischargeDate(String dischargeDate) {
        this.dischargeDate = dischargeDate;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getPatientType() {
        return patientType;
    }

    public void setPatientType(String patientType) {
        this.patientType = patientType;
    }

    @Override
    public String toString() {
        return String.format(
            "Patient ID: %s | Name: %s | Type: %s | Age: %d | Gender: %s | Phone: %s | Status: %s | Doctor: %s | Room: %s | Disease: %s",
            patientId, name, patientType, age, gender, phoneNumber, admissionStatus, assignedDoctorId, roomNumber, disease
        );
    }
}
