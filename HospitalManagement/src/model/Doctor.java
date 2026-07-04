package model;

import model.enums.DoctorAvailability;
import model.enums.Shift;

public class Doctor {
    private String doctorId;
    private String name;
    private int age;
    private String gender;
    private String phone;
    private String email;
    private String department;
    private String specialization;
    private String qualification;
    private int experience;
    private Shift shift;
    private DoctorAvailability availability;
    private double salary;
    private String certificateStatus; // GENUINE, FAKE, UNVERIFIED

    public Doctor() {
        this.shift = Shift.GENERAL;
        this.availability = DoctorAvailability.AVAILABLE;
        this.certificateStatus = "UNVERIFIED";
    }

    public Doctor(String doctorId, String name, int age, String gender, String phone, String email, 
                  String department, String specialization, String qualification, int experience, 
                  Shift shift, DoctorAvailability availability, double salary, String certificateStatus) {
        this.doctorId = doctorId;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
        this.department = department;
        this.specialization = specialization;
        this.qualification = qualification;
        this.experience = experience;
        this.shift = shift;
        this.availability = availability;
        this.salary = salary;
        this.certificateStatus = certificateStatus;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public DoctorAvailability getAvailability() {
        return availability;
    }

    public void setAvailability(DoctorAvailability availability) {
        this.availability = availability;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getCertificateStatus() {
        return certificateStatus;
    }

    public void setCertificateStatus(String certificateStatus) {
        this.certificateStatus = certificateStatus;
    }

    @Override
    public String toString() {
        return String.format(
            "Doctor ID: %s | Name: %s | Dept: %s | Spec: %s | Shift: %s | Availability: %s | Verification: %s | Salary: $%.2f",
            doctorId, name, department, specialization, shift, availability, certificateStatus, salary
        );
    }
}
