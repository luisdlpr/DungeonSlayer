package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

import static dungeonmania.TestUtils.getInventory;
import static dungeonmania.TestUtils.getEntities;

public class TimeTravelTests {

    @Test
    @DisplayName("picking up time turner")
    public void testCollectingTimeTurner() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_timeTurnerTests", "c_movementTest_testMovementDown");

        // Collect time turner
        DungeonResponse res = dmc.tick(Direction.RIGHT);

        // Check that it has been added to inventory
        assertEquals(1, getInventory(res, "time_turner").size());
    }

    @Test
    @DisplayName("rewind exceptions")
    public void testRewindExceptions() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_timeTurnerTests", "c_movementTest_testMovementDown");
        
        // Collect time turner
        dmc.tick(Direction.RIGHT);
        
        // ticks <= 0
        assertThrows(IllegalArgumentException.class, () -> dmc.rewind(-1));

        // invalid number of (t)icks
        assertThrows(IllegalArgumentException.class, () -> dmc.rewind(2));
    }

    @Test
    @DisplayName("rewind one tick")
    public void testRewind1() {
        // Check that going back 1 tick has same game state
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_timeTurnerTests", "c_movementTest_testMovementDown");
        
        // Collect time turner
        DungeonResponse res = dmc.tick(Direction.RIGHT);
        assertEquals(0, getEntities(res, "time_turner").size());

        // Rewind
        res = assertDoesNotThrow(() -> dmc.rewind(1));
        // Check that time turner is on map
        assertEquals(1, getEntities(res, "time_turner").size());
        // Check that older self is on map
        assertEquals(new Position(0, 0), getEntities(res, "older_player").get(0).getPosition());
        // Check position of current self hasnt changed
        assertEquals(new Position(1, 0), getEntities(res, "player").get(0).getPosition());
    }

    @Test
    @DisplayName("rewind 5 ticks")
    public void testRewind5() {
        // Check that going back 5 tick has same game state
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_timeTurnerTests", "c_movementTest_testMovementDown");
        
        // Collect time turner
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        DungeonResponse res = dmc.tick(Direction.RIGHT);
        assertEquals(0, getEntities(res, "time_turner").size());

        // Rewind
        res = assertDoesNotThrow(() -> dmc.rewind(5));
        // Check that time turner is on map
        assertEquals(1, getEntities(res, "time_turner").size());
        // Check that older self is on map
        assertEquals(new Position(0, 0), getEntities(res, "older_player").get(0).getPosition());
        // Check position of current self hasnt changed
        assertEquals(new Position(5, 0), getEntities(res, "player").get(0).getPosition());
    }

    @Test
    @DisplayName("rewind 5 ticks")
    public void testRewind5Correct() {
        // Check that going back 5 tick has same game state
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_timeTurnerTests", "c_movementTest_testMovementDown");
        
        // Collect time turner
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        DungeonResponse res = dmc.tick(Direction.RIGHT);
        assertEquals(0, getEntities(res, "time_turner").size());

        // Rewind
        res = assertDoesNotThrow(() -> dmc.rewind(5));
        // Check that time turner is not on map
        assertEquals(0, getEntities(res, "time_turner").size());
        // Check that older self is on map
        assertEquals(new Position(4, 0), getEntities(res, "older_player").get(0).getPosition());
        // Check position of current self hasnt changed
        assertEquals(new Position(9, 0), getEntities(res, "player").get(0).getPosition());
    }


    @Test
    @DisplayName("time travelling portal less than 30 ticks")
    public void testTimeTravellingPortalRewindInitial() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_timeTravellingPortalTests_less30", "c_simple");
        
        dmc.tick(Direction.RIGHT);
        DungeonResponse res = dmc.tick(Direction.RIGHT);

        // Check that reverted to initial state
        // Check that sword is on map
        assertEquals(1, getEntities(res, "sword").size());
        // Check that older self is on map
        assertEquals(new Position(0, 0), getEntities(res, "older_player").get(0).getPosition());
        // Check position of current self hasnt changed
        assertEquals(new Position(2, 0), getEntities(res, "player").get(0).getPosition());
    } 

    @Test
    @DisplayName("time travelling portal 30 ticks")
    public void testTimeTravellingPortalRewind() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_timeTravellingPortalTests_30", "c_simple");
        
        
        for (int i = 0; i < 39; i++) {
            dmc.tick(Direction.RIGHT);
        }
        DungeonResponse res = dmc.tick(Direction.RIGHT);

        assertEquals(0, getEntities(res, "sword").size());

        // Check that has reverted 30 ticks
        // Check that sword is on map
        assertEquals(0, getEntities(res, "sword").size());
        // Check that older self is on map
        assertEquals(new Position(10, 0), getEntities(res, "older_player").get(0).getPosition());
        // Check position of current self hasnt changed
        assertEquals(new Position(40, 0), getEntities(res, "player").get(0).getPosition());
    
    } 

}
