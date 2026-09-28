package medicarehospital;

import java.util.Scanner;

public class Main {
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final HospitalSystem HOSPITAL = new HospitalSystem();

    public static void main(String[] args) {
        boolean running = true;
        System.out.println("==========================================");
        System.out.println("   MEDICARE HOSPITAL PATIENT SYSTEM");
        System.out.println("==========================================");

        while (running) {
            displayMainMenu();
            int choice = readInt("Enter your choice: ", 0, 13);
            switch (choice) {
                case 1: registerPatient(); break;
                case 2: searchPatient(); break;
                case 3: updatePatient(); break;
                case 4: deletePatient(); break;
                case 5: HOSPITAL.displayAllPatients(); break;
                case 6: allocateBed(); break;
                case 7: releaseBed(); break;
                case 8: HOSPITAL.displayWardLayout(); break;
                case 9: HOSPITAL.displayAvailableBeds(); break;
                case 10: HOSPITAL.displayOccupiedBeds(); break;
                case 11: displayCompleteReport(); break;
                case 12: HOSPITAL.displaySortedBySurname(); break;
                case 13: HOSPITAL.displaySortedByPatientId(); break;
                case 0:
                    running = false;
                    System.out.println("\nThank you for using the MediCare Hospital System.");
                    break;
                default: break;
            }
        }
        SCANNER.close();
    }

    private static void displayMainMenu() {
        System.out.println("\n==========================================");
        System.out.println("                MAIN MENU");
        System.out.println("==========================================");
        System.out.println("1.  Register Patient");
        System.out.println("2.  Search Patient");
        System.out.println("3.  Update Patient");
        System.out.println("4.  Delete Patient");
        System.out.println("5.  Display All Patients");
        System.out.println("------------------------------------------");
        System.out.println("6.  Allocate Bed");
        System.out.println("7.  Release Bed");
        System.out.println("8.  Display Ward Layout");
        System.out.println("9.  Display Available Beds");
        System.out.println("10. Display Occupied Beds");
        System.out.println("------------------------------------------");
        System.out.println("11. Display Complete Ward Report");
        System.out.println("12. Sort Patients by Surname");
        System.out.println("13. Sort Patients by Patient ID");
        System.out.println("------------------------------------------");
        System.out.println("0.  Exit");
        System.out.println("==========================================");
    }

    private static void registerPatient() {
        System.out.println("\n========== REGISTER PATIENT ==========");
        String id = readPatientId("Patient ID (for example P001): ");
        if (HOSPITAL.searchPatient(id) != null) {
            System.out.println("Error: That Patient ID already exists.");
            return;
        }
        String firstName = readRequiredString("First Name: ");
        String lastName = readRequiredString("Last Name: ");
        int age = readInt("Age: ", 0, 130);
        String gender = readRequiredString("Gender: ");
        String condition = readRequiredString("Medical Condition: ");
        PatientCategory category = readCategory();

        Patient patient = category == PatientCategory.INPATIENT
                ? new Inpatient(id, firstName, lastName, age, gender, condition, "NONE", "NONE")
                : new Patient(id, firstName, lastName, age, gender, condition, category);
        System.out.println(HOSPITAL.registerPatient(patient)
                ? "Patient registered successfully." : "Patient registration failed.");
    }

    private static void searchPatient() {
        System.out.println("\n========== SEARCH PATIENT ==========");
        Patient patient = HOSPITAL.searchPatient(readRequiredString("Enter Patient ID: "));
        if (patient == null) System.out.println("Patient not found.");
        else patient.displayDetails();
    }

    private static void updatePatient() {
        System.out.println("\n========== UPDATE PATIENT ==========");
        String id = readRequiredString("Enter Patient ID: ");
        Patient patient = HOSPITAL.searchPatient(id);
        if (patient == null) {
            System.out.println("Patient not found.");
            return;
        }
        patient.displayDetails();
        System.out.println("Enter the patient's new information.");
        String firstName = readRequiredString("First Name: ");
        String lastName = readRequiredString("Last Name: ");
        int age = readInt("Age: ", 0, 130);
        String gender = readRequiredString("Gender: ");
        String condition = readRequiredString("Medical Condition: ");
        PatientCategory category = readCategory();
        System.out.println(HOSPITAL.updatePatient(id, firstName, lastName, age,
                gender, condition, category)
                ? "Patient updated successfully." : "Patient update failed.");
    }

    private static void deletePatient() {
        System.out.println("\n========== DELETE PATIENT ==========");
        String id = readRequiredString("Enter Patient ID: ");
        Patient patient = HOSPITAL.searchPatient(id);
        if (patient == null) {
            System.out.println("Patient not found.");
            return;
        }
        patient.displayDetails();
        String answer = readRequiredString("Are you sure you want to delete this patient? (Y/N): ");
        if (!answer.equalsIgnoreCase("Y")) {
            System.out.println("Delete operation cancelled.");
            return;
        }
        System.out.println(HOSPITAL.deletePatient(id)
                ? "Patient deleted successfully." : "Patient deletion failed.");
    }

    private static void allocateBed() {
        System.out.println("\n========== ALLOCATE BED ==========");
        String id = readRequiredString("Enter Inpatient ID: ");
        Patient patient = HOSPITAL.searchPatient(id);
        if (patient == null) {
            System.out.println("Patient not found.");
            return;
        }
        if (!(patient instanceof Inpatient)) {
            System.out.println("Only inpatients may be allocated hospital beds.");
            return;
        }
        Inpatient inpatient = (Inpatient) patient;
        if (!"NONE".equalsIgnoreCase(inpatient.getBedNumber())) {
            System.out.println("This inpatient already occupies " + inpatient.getBedNumber() + ".");
            return;
        }
        if (HOSPITAL.areAllBedsOccupied()) {
            System.out.println("All 20 beds are currently occupied.");
            return;
        }
        HOSPITAL.displayAvailableBeds();
        String bedNumber = readBedNumber("Enter bed number (B01-B20): ");
        System.out.println(HOSPITAL.allocateBed(id, bedNumber)
                ? "Bed " + bedNumber + " allocated successfully."
                : "Bed allocation failed. The bed may already be occupied.");
    }

    private static void releaseBed() {
        System.out.println("\n========== RELEASE BED ==========");
        String id = readRequiredString("Enter Inpatient ID: ");
        Patient patient = HOSPITAL.searchPatient(id);
        if (patient == null) {
            System.out.println("Patient not found.");
            return;
        }
        if (!(patient instanceof Inpatient)) {
            System.out.println("This patient is not an inpatient.");
            return;
        }
        Inpatient inpatient = (Inpatient) patient;
        if ("NONE".equalsIgnoreCase(inpatient.getBedNumber())) {
            System.out.println("This inpatient does not currently occupy a bed.");
            return;
        }
        System.out.println("Current bed: " + inpatient.getBedNumber());
        System.out.println(HOSPITAL.releaseBed(id)
                ? "Bed released successfully." : "Bed release failed.");
    }

    private static void displayCompleteReport() {
        HOSPITAL.displayReport();
        HOSPITAL.displayAllPatients();
        HOSPITAL.displayAvailableBeds();
        HOSPITAL.displayOccupiedBeds();
    }

    private static PatientCategory readCategory() {
        System.out.println("\nPatient Category:");
        System.out.println("1. Inpatient");
        System.out.println("2. Outpatient");
        System.out.println("3. Emergency");
        int choice = readInt("Select category: ", 1, 3);
        if (choice == 1) return PatientCategory.INPATIENT;
        if (choice == 2) return PatientCategory.OUTPATIENT;
        return PatientCategory.EMERGENCY;
    }

    private static String readPatientId(String message) {
        while (true) {
            String value = readRequiredString(message).toUpperCase();
            if (value.matches("P\\d{3,}")) return value;
            System.out.println("Invalid ID. Use P followed by at least three digits.");
        }
    }

    private static String readBedNumber(String message) {
        while (true) {
            String value = readRequiredString(message).toUpperCase();
            if (value.matches("B(0[1-9]|1[0-9]|20)")) return value;
            System.out.println("Invalid bed. Enter a bed from B01 to B20.");
        }
    }

    private static String readRequiredString(String message) {
        while (true) {
            System.out.print(message);
            String value = SCANNER.nextLine().trim();
            if (!value.isEmpty()) return value;
            System.out.println("This field may not be empty.");
        }
    }

    private static int readInt(String message, int minimum, int maximum) {
        while (true) {
            try {
                System.out.print(message);
                int value = Integer.parseInt(SCANNER.nextLine().trim());
                if (value < minimum || value > maximum) {
                    System.out.printf("Enter a value from %d to %d.%n", minimum, maximum);
                    continue;
                }
                return value;
            } catch (NumberFormatException exception) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }
    }
}
