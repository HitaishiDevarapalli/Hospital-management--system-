package service;

import exception.DoctorNotFoundException;
import java.util.ArrayList;
import model.Doctor;
import model.Patient;
import model.enums.DoctorAvailability;
import model.enums.PatientStatus;
import model.enums.Shift;
import util.Validator;

public class DoctorService {
    private ArrayList<Doctor> doctors;

    public DoctorService(ArrayList<Doctor> doctors) {
        this.doctors = doctors;
    }

    public ArrayList<Doctor> getAllDoctors() {
        return doctors;
    }

    public boolean doctorExists(String doctorId) {
        for (Doctor d : doctors) {
            if (d.getDoctorId().equalsIgnoreCase(doctorId)) {
                return true;
            }
        }
        return false;
    }

    public void addDoctor(Doctor d) throws Exception {
        // Unique Doctor ID check
        for (Doctor existing : doctors) {
            if (existing.getDoctorId().equalsIgnoreCase(d.getDoctorId())) {
                throw new Exception("Doctor ID " + d.getDoctorId() + " already exists!");
            }
        }
        if (!Validator.validateAge(d.getAge())) {
            throw new Exception("Age cannot be negative!");
        }
        if (!Validator.validatePhoneNumber(d.getPhone())) {
            throw new Exception("Invalid phone number format!");
        }
        if (!Validator.validateEmail(d.getEmail())) {
            throw new Exception("Invalid email format!");
        }
        if (d.getSalary() < 0) {
            throw new Exception("Salary cannot be negative!");
        }
        
        doctors.add(d);
        ReportService.log(String.format("Doctor %s added. Name: %s", d.getDoctorId(), d.getName()));
    }

    public Doctor findDoctor(String doctorId) throws DoctorNotFoundException {
        for (Doctor d : doctors) {
            if (d.getDoctorId().equalsIgnoreCase(doctorId)) {
                return d;
            }
        }
        throw new DoctorNotFoundException("Doctor with ID " + doctorId + " not found!");
    }

    public void updateDoctor(String doctorId, String name, int age, String gender, String phone, String email, 
                             String department, String specialization, String qualification, int experience, 
                             Shift shift, DoctorAvailability availability, double salary) throws Exception {
        Doctor d = findDoctor(doctorId);
        if (!Validator.validateAge(age)) {
            throw new Exception("Age cannot be negative!");
        }
        if (!Validator.validatePhoneNumber(phone)) {
            throw new Exception("Invalid phone number format!");
        }
        if (!Validator.validateEmail(email)) {
            throw new Exception("Invalid email format!");
        }
        if (salary < 0) {
            throw new Exception("Salary cannot be negative!");
        }

        d.setName(name);
        d.setAge(age);
        d.setGender(gender);
        d.setPhone(phone);
        d.setEmail(email);
        d.setDepartment(department);
        d.setSpecialization(specialization);
        d.setQualification(qualification);
        d.setExperience(experience);
        d.setShift(shift);
        d.setAvailability(availability);
        d.setSalary(salary);

        ReportService.log(String.format("Doctor %s details updated.", doctorId));
    }

    public ArrayList<Patient> getAssignedPatients(String doctorId, ArrayList<Patient> allPatients) throws DoctorNotFoundException {
        // First verify doctor exists
        findDoctor(doctorId);
        
        ArrayList<Patient> assigned = new ArrayList<>();
        for (Patient p : allPatients) {
            if (p.getAssignedDoctorId().equalsIgnoreCase(doctorId) && p.getAdmissionStatus() == PatientStatus.ADMITTED) {
                assigned.add(p);
            }
        }
        return assigned;
    }
}
