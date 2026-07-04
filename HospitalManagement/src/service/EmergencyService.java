package service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import model.Emergency;
import model.enums.EmergencyPriority;
import model.enums.EmergencyStatus;

public class EmergencyService {
    private ArrayList<Emergency> emergencies;
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public EmergencyService() {
        this.emergencies = new ArrayList<>();
    }

    public ArrayList<Emergency> getAllEmergencies() {
        return emergencies;
    }

    public Emergency raiseEmergency(String patientId, String type, EmergencyPriority priority, boolean ambulanceReq) {
        String newId = String.format("E%03d", emergencies.size() + 1);
        String callTime = LocalDateTime.now().format(timeFormatter);
        
        Emergency e = new Emergency(newId, patientId, type, priority, "N/A", "N/A", ambulanceReq, callTime, EmergencyStatus.REPORTED);
        emergencies.add(e);
        
        ReportService.log(String.format("Emergency %s raised for Patient %s. Priority: %s", newId, patientId, priority));
        if (ambulanceReq) {
            e.setStatus(EmergencyStatus.AMBULANCE_DISPATCHED);
            ReportService.log(String.format("Ambulance dispatched for Emergency %s.", newId));
        }
        return e;
    }

    public Emergency findEmergency(String emergencyId) throws Exception {
        for (Emergency e : emergencies) {
            if (e.getEmergencyId().equalsIgnoreCase(emergencyId)) {
                return e;
            }
        }
        throw new Exception("Emergency case " + emergencyId + " not found!");
    }

    public void assignDoctor(String emergencyId, String doctorId, boolean doctorExists) throws Exception {
        Emergency e = findEmergency(emergencyId);
        if (!doctorExists) {
            throw new Exception("Doctor ID " + doctorId + " does not exist!");
        }
        e.setAssignedDoctorId(doctorId);
        e.setStatus(EmergencyStatus.DOCTOR_ASSIGNED);
        ReportService.log(String.format("Doctor %s assigned to Emergency %s.", doctorId, emergencyId));
    }

    public void assignNurse(String emergencyId, String nurseId, boolean nurseExists) throws Exception {
        Emergency e = findEmergency(emergencyId);
        if (!nurseExists) {
            throw new Exception("Nurse with ID " + nurseId + " does not exist or is not a Nurse!");
        }
        e.setAssignedNurseId(nurseId);
        e.setStatus(EmergencyStatus.UNDER_TREATMENT);
        ReportService.log(String.format("Nurse %s assigned to Emergency %s. Case is now under treatment.", nurseId, emergencyId));
    }

    public void dispatchAmbulance(String emergencyId) throws Exception {
        Emergency e = findEmergency(emergencyId);
        e.setAmbulanceRequired(true);
        e.setStatus(EmergencyStatus.AMBULANCE_DISPATCHED);
        ReportService.log(String.format("Ambulance dispatched for Emergency %s.", emergencyId));
    }

    public void closeEmergency(String emergencyId) throws Exception {
        Emergency e = findEmergency(emergencyId);
        e.setStatus(EmergencyStatus.CLOSED);
        ReportService.log(String.format("Emergency %s closed.", emergencyId));
    }
}
