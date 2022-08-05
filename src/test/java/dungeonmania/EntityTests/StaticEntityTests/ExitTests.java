package dungeonmania.EntityTests.StaticEntityTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.Dungeon;
import dungeonmania.DungeonManiaController;
import dungeonmania.Helper;
import dungeonmania.Entities.Entity;
import dungeonmania.Entities.StaticEntities.ExitEntity;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

import static dungeonmania.TestUtils.getGoals;

/**
 * Tests for exit creation and functionality
 * @author nancy Huynh (z5257042)
 */

public class ExitTests {

    Helper helper = new Helper();

    @Test
    @DisplayName("constructor for exit operates correctly")
    public void testUnitExitCreation() {
        // constructor takes in dungeon filename, config filename, and int representing how many dungeons have been created (passed in from controller)
        
        // Create a new exit at (1, 0)
        ExitEntity exit = new ExitEntity(helper.createEntityJSON("exit","1", "0"), 0);
        
        Position expectedPosition = new Position(1, 0);
        
        // Check position and id of created entity are correct
        assertTrue(expectedPosition.equals(exit.getEntityResponse().getPosition()));
        assertTrue("exit0".equals(exit.getEntityResponse().getId()));
    }

    @Test
    @DisplayName("exit is created correctly in dungeonController")
    public void testControllerExitCreation() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_movementTest_testMovementDown", "c_movementTest_testMovementDown");
        Dungeon dungeon = dmc.currentDungeon;

        List<Entity> entities = dungeon.getEntities();

        ExitEntity exit = new ExitEntity(helper.createEntityJSON("exit","1", "3"), 0);

        assertTrue(helper.assertEntityExistsInList(entities, exit));

        for (Entity e : entities) {
            if (e.getType().equals("exit")){
                assertTrue(e.getClass().getSimpleName().equals("ExitEntity"));
            }
        }
    }

    @Test
    @DisplayName("wall exit updates its goal object")
    public void testPlayerOnExit() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        // Create new config where player is one move off exit
        dmc.newGame("d_exitTest", "c_movementTest_testMovementDown");

        // Move player on the exit
        DungeonResponse res = dmc.tick(Direction.DOWN);

        // Check that the goal object is updated
        assertEquals("", getGoals(res));
    }
}
