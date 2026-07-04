package model;

import model.enums.Shift;
import model.enums.StaffStatus;

public class Staff {
    private String staffId;
    private String name;
    private int age;
    private String gender;
    private String phone;
    private String department;
    private String role; // Nurse, Receptionist, Cleaning Staff, Watchman Staff, etc.
    private Shift shift;
    private double salary;
    private StaffStatus status;
    private String joiningDate; // Re-added per new requirement

    public Staff() {
        this.shift = Shift.GENERAL;
        this.status = StaffStatus.ACTIVE;
        this.joiningDate = "N/A";
    }

    public Staff(String staffId, String name, int age, String gender, String phone, 
                 String department, String role, Shift shift, double salary, StaffStatus status, String joiningDate) {
        this.staffId = staffId;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.phone = phone;
        this.department = department;
        this.role = role;
        this.shift = shift;
        this.salary = salary;
        this.status = status;
        this.joiningDate = joiningDate;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
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

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public StaffStatus getStatus() {
        return status;
    }

    public void setStatus(StaffStatus status) {
        this.status = status;
    }

    public String getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(String joiningDate) {
        this.joiningDate = joiningDate;
    }

    @Override
    public String toString() {
        return String.format(
            "Staff ID: %s | Name: %s | Role: %s | Dept: %s | Shift: %s | Status: %s | Joining: %s | Salary: $%.2f",
            staffId, name, role, department, shift, status, joiningDate, salary
        );
    }
}
