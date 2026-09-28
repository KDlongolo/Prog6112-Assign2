package medicarehospital;

public class Patient {
    private final String patientId;
    private String firstName;
    private String lastName;
    private int age;
    private String gender;
    private String medicalCondition;
    private PatientCategory category;

    public Patient(String patientId, String firstName, String lastName, int age,
            String gender, String medicalCondition, PatientCategory category) {
        this.patientId = patientId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.gender = gender;
        this.medicalCondition = medicalCondition;
        this.category = category;
    }

    public String getPatientId() { return patientId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getMedicalCondition() { return medicalCondition; }
    public void setMedicalCondition(String medicalCondition) { this.medicalCondition = medicalCondition; }
    public PatientCategory getCategory() { return category; }
    protected void setCategory(PatientCategory category) { this.category = category; }

    protected String formattedCategory() {
        String value = category.name().toLowerCase();
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }

    public void displayDetails() {
        System.out.println("------------------------------------------");
        System.out.println("Patient ID        : " + patientId);
        System.out.println("First Name        : " + firstName);
        System.out.println("Last Name         : " + lastName);
        System.out.println("Age               : " + age);
        System.out.println("Gender            : " + gender);
        System.out.println("Medical Condition : " + medicalCondition);
        System.out.println("Category          : " + formattedCategory());
        System.out.println("------------------------------------------");
    }

    @Override
    public String toString() {
        return patientId + " - " + firstName + " " + lastName + " - " + formattedCategory();
    }
}
