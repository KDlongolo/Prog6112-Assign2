package wildlifesa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RescueSystemTest {
    private RescueSystem system;
    private InjuredAnimalRescue injured;

    @BeforeEach
    void setUp() {
        system = new RescueSystem();
        injured = new InjuredAnimalRescue("R001", "Leo", "Lion", "Kruger Park",
                "Ranger Mokoena", 5, 200, "RECORDED", "Broken leg", 3000, true);
    }

    @Test
    void injuredRescueCostIncludesCareTreatmentAndSurgery() {
        assertEquals(9000.00, injured.calculateTotalRescueCost(), 0.001);
    }

    @Test
    void orphanedRescueCostIncludesFeedingAndFosterCare() {
        RescueCase orphan = new OrphanedAnimalRescue("R002", "Tiny", "Elephant",
                "Limpopo", "Ranger Dube", 4, 150, "RECORDED", 3, 1200, true);
        assertEquals(4300.00, orphan.calculateTotalRescueCost(), 0.001);
    }

    @Test
    void endangeredRescueCostIncludesSecurityAndSpecialistTeam() {
        RescueCase endangered = new EndangeredSpeciesRescue("R003", "Hope", "Rhino",
                "Hluhluwe", "Ranger Naidoo", 2, 500, "RECORDED",
                "Critically Endangered", 4000, true);
        assertEquals(13000.00, endangered.calculateTotalRescueCost(), 0.001);
    }

    @Test
    void prioritiesAreCalculatedForEachRescueType() {
        RescueCase orphan = new OrphanedAnimalRescue("R002", "Tiny", "Elephant",
                "Limpopo", "Ranger Dube", 4, 150, "RECORDED", 3, 1200, true);
        RescueCase endangered = new EndangeredSpeciesRescue("R003", "Hope", "Rhino",
                "Hluhluwe", "Ranger Naidoo", 2, 500, "RECORDED",
                "Critically Endangered", 4000, true);
        assertEquals("CRITICAL", injured.determineRescuePriority());
        assertEquals("HIGH", orphan.determineRescuePriority());
        assertEquals("CRITICAL", endangered.determineRescuePriority());
    }

    @Test
    void startAndCompleteUpdateStatus() {
        injured.startRescueOperation();
        assertEquals("IN PROGRESS", injured.getCurrentRescueStatus());
        injured.completeRescueOperation();
        assertEquals("COMPLETED", injured.getCurrentRescueStatus());
    }

    @Test
    void searchesForExistingRescueCase() {
        system.addRescueCase(injured);
        assertSame(injured, system.searchRescueCase("R001"));
        assertNull(system.searchRescueCase("R999"));
    }

    @Test
    void preventsDuplicateRescueCaseIds() {
        assertTrue(system.addRescueCase(injured));
        RescueCase duplicate = new InjuredAnimalRescue("r001", "Simba", "Lion",
                "Pretoria", "Ranger Smith", 2, 100, "RECORDED", "Cut", 500, false);
        assertFalse(system.addRescueCase(duplicate));
        assertEquals(1, system.getAllRescueCases().size());
    }

    @Test
    void reportContainsTotalsAndCaseInformation() {
        system.addRescueCase(injured);
        String report = system.generateRescueReport();
        assertTrue(report.contains("R001"));
        assertTrue(report.contains("Total number of rescue cases: 1"));
        assertTrue(report.contains("R9000.00"));
    }

    @Test
    void validatesBlankAndNonPositiveValues() {
        assertThrows(IllegalArgumentException.class, () ->
                new InjuredAnimalRescue("", "Leo", "Lion", "Kruger", "Ranger",
                        5, 200, "RECORDED", "Injury", 100, false));
        assertThrows(IllegalArgumentException.class, () ->
                new InjuredAnimalRescue("R010", "Leo", "Lion", "Kruger", "Ranger",
                        0, 200, "RECORDED", "Injury", 100, false));
    }
}
