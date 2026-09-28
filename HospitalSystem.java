package medicarehospital;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HospitalSystem {
    public static final int WARD_ROWS = 4;
    public static final int WARD_COLUMNS = 5;
    public static final int TOTAL_BEDS = WARD_ROWS * WARD_COLUMNS;
    public static final String WARD_NUMBER = "W01";

    private final ArrayList<Patient> patients = new ArrayList<>();
    private final Inpatient[][] beds = new Inpatient[WARD_ROWS][WARD_COLUMNS];

    public boolean registerPatient(Patient patient) {
        if (patient == null || searchPatient(patient.getPatientId()) != null) return false;
        patients.add(patient);
        return true;
    }

    public Patient searchPatient(String patientId) {
        if (patientId == null) return null;
        for (Patient patient : patients) {
            if (patient.getPatientId().equalsIgnoreCase(patientId.trim())) return patient;
        }
        return null;
    }

    public boolean updatePatient(String patientId, String firstName, String lastName,
            int age, String gender, String condition, PatientCategory newCategory) {
        Patient oldPatient = searchPatient(patientId);
        if (oldPatient == null || newCategory == null) return false;

        boolean oldInpatient = oldPatient instanceof Inpatient;
        boolean newInpatient = newCategory == PatientCategory.INPATIENT;
        if (oldInpatient == newInpatient) {
            oldPatient.setFirstName(firstName);
            oldPatient.setLastName(lastName);
            oldPatient.setAge(age);
            oldPatient.setGender(gender);
            oldPatient.setMedicalCondition(condition);
            oldPatient.setCategory(newCategory);
            return true;
        }

        int listIndex = patients.indexOf(oldPatient);
        Patient replacement;
        if (newInpatient) {
            replacement = new Inpatient(patientId, firstName, lastName, age,
                    gender, condition, "NONE", "NONE");
        } else {
            releaseBed(patientId);
            replacement = new Patient(patientId, firstName, lastName, age,
                    gender, condition, newCategory);
        }
        patients.set(listIndex, replacement);
        return true;
    }

    public boolean deletePatient(String patientId) {
        Patient patient = searchPatient(patientId);
        if (patient == null) return false;
        if (patient instanceof Inpatient) releaseBed(patientId);
        return patients.remove(patient);
    }

    public int getTotalPatients() { return patients.size(); }
    public List<Patient> getPatients() { return new ArrayList<>(patients); }

    public void displayAllPatients() {
        if (patients.isEmpty()) {
            System.out.println("\nNo patients are registered.");
            return;
        }
        System.out.println("\n========== ALL REGISTERED PATIENTS ==========");
        for (Patient patient : patients) patient.displayDetails();
    }

    public List<Patient> sortBySurname() {
        ArrayList<Patient> sorted = new ArrayList<>(patients);
        sorted.sort(Comparator.comparing(Patient::getLastName,
                String.CASE_INSENSITIVE_ORDER).thenComparing(Patient::getFirstName,
                String.CASE_INSENSITIVE_ORDER));
        return sorted;
    }

    public List<Patient> sortByPatientId() {
        ArrayList<Patient> sorted = new ArrayList<>(patients);
        sorted.sort(Comparator.comparing(Patient::getPatientId,
                String.CASE_INSENSITIVE_ORDER));
        return sorted;
    }

    public void displaySortedBySurname() {
        displayPatientList(sortBySurname(), "PATIENTS SORTED BY SURNAME");
    }

    public void displaySortedByPatientId() {
        displayPatientList(sortByPatientId(), "PATIENTS SORTED BY PATIENT ID");
    }

    private void displayPatientList(List<Patient> list, String title) {
        if (list.isEmpty()) {
            System.out.println("\nNo patients are registered.");
            return;
        }
        System.out.println("\n========== " + title + " ==========");
        for (Patient patient : list) patient.displayDetails();
    }

    public boolean allocateBed(String patientId, String bedNumber) {
        Patient patient = searchPatient(patientId);
        if (!(patient instanceof Inpatient)) return false;
        Inpatient inpatient = (Inpatient) patient;
        if (!"NONE".equalsIgnoreCase(inpatient.getBedNumber())) return false;

        int[] position = getBedPosition(bedNumber);
        if (position == null || beds[position[0]][position[1]] != null) return false;
        beds[position[0]][position[1]] = inpatient;
        inpatient.setWardNumber(WARD_NUMBER);
        inpatient.setBedNumber(formatBedNumber(position[0], position[1]));
        return true;
    }

    public boolean releaseBed(String patientId) {
        Patient patient = searchPatient(patientId);
        if (!(patient instanceof Inpatient)) return false;
        Inpatient inpatient = (Inpatient) patient;
        int[] position = getBedPosition(inpatient.getBedNumber());
        if (position == null || beds[position[0]][position[1]] != inpatient) return false;

        beds[position[0]][position[1]] = null;
        inpatient.setWardNumber("NONE");
        inpatient.setBedNumber("NONE");
        return true;
    }

    public boolean isBedOccupied(String bedNumber) {
        int[] position = getBedPosition(bedNumber);
        return position != null && beds[position[0]][position[1]] != null;
    }

    public int getOccupiedBedCount() {
        int count = 0;
        for (Inpatient[] row : beds) {
            for (Inpatient patient : row) if (patient != null) count++;
        }
        return count;
    }

    public int getAvailableBedCount() { return TOTAL_BEDS - getOccupiedBedCount(); }
    public boolean areAllBedsOccupied() { return getOccupiedBedCount() == TOTAL_BEDS; }
    public double getOccupancyPercentage() {
        return ((double) getOccupiedBedCount() / TOTAL_BEDS) * 100;
    }

    public void displayWardLayout() {
        System.out.println("\n============== WARD " + WARD_NUMBER + " LAYOUT ==============");
        for (int row = 0; row < beds.length; row++) {
            for (int column = 0; column < beds[row].length; column++) {
                String status = beds[row][column] == null
                        ? "Available" : beds[row][column].getPatientId();
                System.out.printf("%-18s", "[" + formatBedNumber(row, column) + ": " + status + "]");
            }
            System.out.println();
        }
    }

    public void displayAvailableBeds() {
        System.out.println("\n========== AVAILABLE BEDS ==========");
        boolean found = false;
        for (int row = 0; row < beds.length; row++) {
            for (int column = 0; column < beds[row].length; column++) {
                if (beds[row][column] == null) {
                    System.out.print(formatBedNumber(row, column) + "  ");
                    found = true;
                }
            }
        }
        System.out.println(found ? "" : "No beds are available.");
    }

    public void displayOccupiedBeds() {
        System.out.println("\n========== OCCUPIED BEDS ==========");
        boolean found = false;
        for (int row = 0; row < beds.length; row++) {
            for (int column = 0; column < beds[row].length; column++) {
                Inpatient patient = beds[row][column];
                if (patient != null) {
                    System.out.printf("%s -> Patient ID: %s, Name: %s %s%n",
                            formatBedNumber(row, column), patient.getPatientId(),
                            patient.getFirstName(), patient.getLastName());
                    found = true;
                }
            }
        }
        if (!found) System.out.println("No beds are occupied.");
    }

    public void displayReport() {
        System.out.println("\n==========================================");
        System.out.println("          MEDICARE WARD REPORT");
        System.out.println("==========================================");
        System.out.println("Total registered patients : " + getTotalPatients());
        System.out.println("Total occupied beds       : " + getOccupiedBedCount());
        System.out.println("Total available beds      : " + getAvailableBedCount());
        System.out.printf("Ward occupancy percentage : %.2f%%%n", getOccupancyPercentage());
        System.out.println("==========================================");
    }

    private int[] getBedPosition(String bedNumber) {
        if (bedNumber == null) return null;
        String value = bedNumber.trim().toUpperCase();
        if (!value.matches("B(0[1-9]|1[0-9]|20)")) return null;
        int index = Integer.parseInt(value.substring(1)) - 1;
        return new int[]{index / WARD_COLUMNS, index % WARD_COLUMNS};
    }

    private String formatBedNumber(int row, int column) {
        return String.format("B%02d", row * WARD_COLUMNS + column + 1);
    }
}
