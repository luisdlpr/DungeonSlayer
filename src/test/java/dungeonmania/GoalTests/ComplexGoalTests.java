package dungeonmania.GoalTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;

import static dungeonmania.TestUtils.getGoals;

public class ComplexGoalTests{
    @Test
    @DisplayName("creation of complex goal")
    public void textComplexGoalCreation() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_complexGoalsTest_andAll", "c_movementTest_testMovementDown");

        assertEquals(getGoals(res), "(:exit AND :treasure (1)) AND (:boulders (1) AND :enemies (1))");
    }

    @Test
    @DisplayName("test OR")
    public void testORGoal() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_ComplexGoalTest_OR", "c_movementTest_testMovementDown");

        assertEquals(getGoals(res), ":exit OR :treasure (1)");

        // Move to the exit
        dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);

        // Check that the dungeon response returns an empty goal string
        assertEquals(getGoals(res), "");
    }

    @Test
    @DisplayName("test AND")
    public void testANDGoal() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_ComplexGoalTest_AND", "c_movementTest_testMovementDown");
        assertTrue(getGoals(res).contains("exit") && getGoals(res).contains("treasure"));

        // Move to the treasure
        res = dmc.tick(Direction.DOWN);
        assertTrue(getGoals(res).contains("exit"));
        assertFalse(getGoals(res).contains("treasure"));

        // Move to the exit, goal should be satisfied
        res = dmc.tick(Direction.DOWN);
        assertEquals(getGoals(res), "");
        
    }

    @Test
    @DisplayName("test a 4 subgoal disjunction")
    public void test4disjunction() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_ComplexGoalTest_4disconjunction", "c_movementTest_testMovementDown");

        assertEquals(getGoals(res), "(:treasure (1) OR :boulders (1)) OR (:enemies (1) OR :exit)");

        // Move to the exit
        dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);

        // Check that the dungeon response returns an empty goal string
        assertEquals(getGoals(res), "");
    }

    @Test
    @DisplayName("test a 4 subgoal mixed conjunction/disjunction")
    public void test4mixedGoals() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_ComplexGoalTest_4Mixed", "c_movementTest_testMovementDown");

        assertEquals(getGoals(res), "(:treasure (1) AND :boulders (1)) AND (:enemies (1) OR :exit)");

        // Move to the exit
        res = dmc.tick(Direction.RIGHT);

        assertTrue(getGoals(res).contains("treasure"));
        assertTrue(getGoals(res).contains("enemies"));
        assertTrue(getGoals(res).contains("exit"));
        assertFalse(getGoals(res).contains("boulders"));

        dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.DOWN);
    
        assertFalse(getGoals(res).contains("treasure"));
        assertTrue(getGoals(res).contains("enemies"));
        assertTrue(getGoals(res).contains("exit"));
        assertFalse(getGoals(res).contains("boulders"));

        res = dmc.tick(Direction.DOWN);
        // Check that the dungeon response returns an empty goal string
        assertEquals(getGoals(res), "");
    }

}