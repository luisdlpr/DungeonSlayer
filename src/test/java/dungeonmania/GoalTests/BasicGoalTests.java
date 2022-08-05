package dungeonmania.GoalTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;

import static dungeonmania.TestUtils.getGoals;

public class BasicGoalTests {

    @Test
    @DisplayName("creation of basic goal")
    public void testGoalCreationUnit() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_movementTest_testMovementDown", "c_movementTest_testMovementDown");

        assertEquals(getGoals(res), ":exit");

    }

    @Test
    @DisplayName("exit goal")
    public void testExitGoal() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_movementTest_testMovementDown", "c_movementTest_testMovementDown");

        // Move to the exit
        dmc.tick(Direction.DOWN);
        DungeonResponse res = dmc.tick(Direction.DOWN);

        // Check that the dungeon response returns an empty goal string
        assertEquals(getGoals(res), "");
    }

    @Test
    @DisplayName("collect treasure goal")
    public void testTreasureGoal() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_treasureGoal_basic", "c_movementTest_testMovementDown");

        // Collect 1 treasure
        DungeonResponse res = dmc.tick(Direction.RIGHT);

        // Check that the dungeon response returns an empty goal string
        assertEquals(getGoals(res), "");
    }

    @Test
    @DisplayName("boulder on all floor switches goal")
    public void testBouldersGoal() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_floorswitchTest_basicCollision", "c_movementTest_testMovementDown");

        // Position the player so they are to the left of the boulder
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.UP);
        // Move boulder onto only floor switch
        DungeonResponse res = dmc.tick(Direction.RIGHT);


        // Check that the dungeon response returns an empty goal string
        assertEquals(getGoals(res), "");
    }

    @Test
    @DisplayName("boulder on multiple floor switches goal")
    public void testMultipleBouldersGoal() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_floorswitchTest_multipleSwitches", "c_movementTest_testMovementDown");

        // Push all bouldewrs on all floor switches except one
        dmc.tick(Direction.UP);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.UP);
        dmc.tick(Direction.DOWN);
        DungeonResponse res = dmc.tick(Direction.RIGHT);
        // Check that the dungeon response returns :boulders because one switch has no boulder
        assertTrue(getGoals(res).contains(":boulders"));
        
        res = dmc.tick(Direction.UP);
        // Check that the dungeon response returns an empty goal string
        assertEquals(getGoals(res), "");
    }

    @Test
    @DisplayName("boulder on multiple floor switches goal from example")
    public void testMultipleBouldersGoalExample() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("boulders", "c_movementTest_testMovementDown");

        // Push all bouldewrs on all floor switches except one
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.UP);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.LEFT);
        DungeonResponse res = dmc.tick(Direction.UP);

        // Check that the dungeon response returns :boulders because one switch has no boulder
        assertTrue(getGoals(res).contains(":boulders"));

        // Activate all floor switches
        res = dmc.tick(Direction.UP);

        // Check that the dungeon response returns an empty goal string
        assertEquals(getGoals(res), "");
    }
    
}
