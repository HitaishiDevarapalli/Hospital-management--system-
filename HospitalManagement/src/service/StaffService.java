package service;

import exception.StaffNotFoundException;
import java.util.ArrayList;
import model.Staff;
import model.enums.Shift;
import model.enums.StaffStatus;
import util.Validator;

public class StaffService {
    private ArrayList<Staff> staffList;

    public StaffService(ArrayList<Staff> staffList) {
        this.staffList = staffList;
    }

    public ArrayList<Staff> getAllStaff() {
        return staffList;
    }

    public boolean nurseExists(String nurseId) {
        for (Staff s : staffList) {
            if (s.getStaffId().equalsIgnoreCase(nurseId) && s.getRole().equalsIgnoreCase("Nurse")) {
                return true;
            }
        }
        return false;
    }

    public void addStaff(Staff s) throws Exception {
        // Unique Staff ID check
        for (Staff existing : staffList) {
            if (existing.getStaffId().equalsIgnoreCase(s.getStaffId())) {
                throw new Exception("Duplicate ID Violation: Staff ID " + s.getStaffId() + " already exists!");
            }
        }
        if (!Validator.validateAge(s.getAge())) {
            throw new Exception("Age cannot be negative!");
        }
        if (!Validator.validatePhoneNumber(s.getPhone())) {
            throw new Exception("Invalid phone number format!");
        }
        if (s.getSalary() < 0) {
            throw new Exception("Salary cannot be negative!");
        }

        staffList.add(s);
        ReportService.log(String.format("Staff member %s added. Role: %s, Name: %s", s.getStaffId(), s.getRole(), s.getName()));
    }

    public Staff findStaff(String staffId) throws StaffNotFoundException {
        for (Staff s : staffList) {
            if (s.getStaffId().equalsIgnoreCase(staffId)) {
                return s;
            }
        }
        throw new StaffNotFoundException("Staff member with ID " + staffId + " not found!");
    }

    public void updateStaff(String staffId, String name, int age, String gender, String phone, 
                            String department, String role, Shift shift, double salary, 
                            StaffStatus status) throws Exception {
        Staff s = findStaff(staffId);
        if (!Validator.validateAge(age)) {
            throw new Exception("Age cannot be negative!");
        }
        if (!Validator.validatePhoneNumber(phone)) {
            throw new Exception("Invalid phone number format!");
        }
        if (salary < 0) {
            throw new Exception("Salary cannot be negative!");
        }

        s.setName(name);
        s.setAge(age);
        s.setGender(gender);
        s.setPhone(phone);
        s.setDepartment(department);
        s.setRole(role);
        s.setShift(shift);
        s.setSalary(salary);
        s.setStatus(status);

        ReportService.log(String.format("Staff member %s details updated.", staffId));
    }

    public void removeStaff(String staffId) throws StaffNotFoundException {
        Staff s = findStaff(staffId);
        staffList.remove(s);
        ReportService.log(String.format("Staff member %s removed.", staffId));
    }
}
