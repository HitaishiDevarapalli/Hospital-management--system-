package model;

import model.enums.EmergencyPriority;
import model.enums.EmergencyStatus;

public class Emergency {
    private String emergencyId;
    private String patientId;
    private String emergencyType; // Heart Attack, Stroke, etc.
    private EmergencyPriority priority;
    private String assignedDoctorId;
    private String assignedNurseId;
    private boolean ambulanceRequired;
    private String callTime;
    private EmergencyStatus status;

    public Emergency() {
        this.status = EmergencyStatus.REPORTED;
        this.assignedDoctorId = "N/A";
        this.assignedNurseId = "N/A";
    }

    public Emergency(String emergencyId, String patientId, String emergencyType, EmergencyPriority priority, 
                     String assignedDoctorId, String assignedNurseId, boolean ambulanceRequired, 
                     String callTime, EmergencyStatus status) {
        this.emergencyId = emergencyId;
        this.patientId = patientId;
        this.emergencyType = emergencyType;
        this.priority = priority;
        this.assignedDoctorId = assignedDoctorId;
        this.assignedNurseId = assignedNurseId;
        this.ambulanceRequired = ambulanceRequired;
        this.callTime = callTime;
        this.status = status;
    }

    public String getEmergencyId() {
        return emergencyId;
    }

    public void setEmergencyId(String emergencyId) {
        this.emergencyId = emergencyId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getEmergencyType() {
        return emergencyType;
    }

    public void setEmergencyType(String emergencyType) {
        this.emergencyType = emergencyType;
    }

    public EmergencyPriority getPriority() {
        return priority;
    }

    public void setPriority(EmergencyPriority priority) {
        this.priority = priority;
    }

    public String getAssignedDoctorId() {
        return assignedDoctorId;
    }

    public void setAssignedDoctorId(String assignedDoctorId) {
        this.assignedDoctorId = assignedDoctorId;
    }

    public String getAssignedNurseId() {
        return assignedNurseId;
    }

    public void setAssignedNurseId(String assignedNurseId) {
        this.assignedNurseId = assignedNurseId;
    }

    public boolean isAmbulanceRequired() {
        return ambulanceRequired;
    }

    public void setAmbulanceRequired(boolean ambulanceRequired) {
        this.ambulanceRequired = ambulanceRequired;
    }

    public String getCallTime() {
        return callTime;
    }

    public void setCallTime(String callTime) {
        this.callTime = callTime;
    }

    public EmergencyStatus getStatus() {
        return status;
    }

    public void setStatus(EmergencyStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format(
            "Emergency ID: %s | Patient: %s | Type: %s | Priority: %s | Status: %s | Doctor: %s | Nurse: %s | Ambulance: %b | Call Time: %s",
            emergencyId, patientId, emergencyType, priority, status, assignedDoctorId, assignedNurseId, ambulanceRequired, callTime
        );
    }
}
