package dungeonmania.EntityTests;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static dungeonmania.TestUtils.getEntities;
import static dungeonmania.TestUtils.getInventory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;

public class BombTests {
    @Test
    @DisplayName("Test surrounding entities are removed when placing a bomb next to an active switch with config file bomb radius set to 1")
    public void placeBombRadius1() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bombTest_placeBombRadius2", "c_movementTest_testMovementDown");

        // Activate Switch
        res = dmc.tick(Direction.RIGHT);

        // Pick up Bomb
        res = dmc.tick(Direction.DOWN);
        assertEquals(1, getInventory(res, "bomb").size());

        // Place Cardinally Adjacent
        res = dmc.tick(Direction.RIGHT);
        String bombId = getInventory(res, "bomb").get(0).getId();
        res = assertDoesNotThrow(() -> dmc.tick(bombId));

        // Check Bomb exploded with radius 1
        //
        //              Boulder/Switch      Wall            Wall
        //              Bomb                Treasure
        //
        //              Treasure
        assertEquals(0, getEntities(res, "bomb").size());
        assertEquals(0, getEntities(res, "boulder").size());
        assertEquals(0, getEntities(res, "switch").size());
        assertEquals(1, getEntities(res, "wall").size());
        assertEquals(1, getEntities(res, "treasure").size());
        assertEquals(1, getEntities(res, "player").size());
    }

    @Test
    @DisplayName("Test surrounding entities are removed when placing a bomb next to an active switch with config file bomb radius set to 1")
    public void placeBombRadius1a() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bombTest_placeBombRadius2", "c_movementTest_testMovementDown");

        // Activate Switch
        res = dmc.tick(Direction.RIGHT);

        // Pick up Bomb
        res = dmc.tick(Direction.DOWN);
        assertEquals(1, getInventory(res, "bomb").size());

        // Place Cardinally Adjacent
        res = dmc.tick(Direction.RIGHT);
        String bombId = getInventory(res, "bomb").get(0).getId();
        res = assertDoesNotThrow(() -> dmc.tick(bombId));

        // Check Bomb exploded with radius 1
        //
        //              Boulder/Switch      Wall            Wall
        //              Bomb                Treasure
        //
        //              Treasure
        assertEquals(0, getEntities(res, "bomb").size());
        assertEquals(0, getEntities(res, "boulder").size());
        assertEquals(0, getEntities(res, "switch").size());
        assertEquals(1, getEntities(res, "wall").size());
        assertEquals(1, getEntities(res, "treasure").size());
        assertEquals(1, getEntities(res, "player").size());
    }

    @Test
    @DisplayName("Test surrounding entities are removed when placing a bomb next to an active switch with config file bomb radius set to 2")
    public void placeBombRadius2TestA() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bombTest_placeBombRadius2TestA", "c_movementTest_testMovementDown");

        // Activate Switch
        res = dmc.tick(Direction.RIGHT);

        // Pick up Bomb
        res = dmc.tick(Direction.DOWN);
        assertEquals(1, getInventory(res, "bomb").size());

        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "bomb").size());

        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "bomb").size());

        res = dmc.tick(Direction.UP);
        assertEquals(1, getInventory(res, "bomb").size());

        // Place Cardinally Adjacent
        res = dmc.tick(Direction.RIGHT);
        String bombId = getInventory(res, "bomb").get(0).getId();
        res = assertDoesNotThrow(() -> dmc.tick(bombId));

        // Check Bomb exploded with radius 1
        //
        //              Boulder/Switch      Wall            Wall
        //              Bomb                Treasure
        //
        //              Treasure
        assertEquals(0, getEntities(res, "bomb").size());
        assertEquals(0, getEntities(res, "boulder").size());
        assertEquals(0, getEntities(res, "switch").size());
        assertEquals(0, getEntities(res, "wall").size());
        assertEquals(1, getEntities(res, "treasure").size());
        assertEquals(1, getEntities(res, "player").size());
    }

    @Test
    @DisplayName("Test surrounding entities are removed when placing a bomb next to an active switch with config file bomb radius set to 2, switch is activated after bomb is placed")
    public void placeBombRadius2BombPlacedBeforeSwitchActive() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bombTest_placeBombRadius2", "c_bombTest_placeBombRadius2");

        // Walk to bomb
        res = dmc.tick(Direction.DOWN);

        // Pick up Bomb
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "bomb").size());

        res = dmc.tick(Direction.RIGHT);
        String bombId = getInventory(res, "bomb").get(0).getId();
        res = assertDoesNotThrow(() -> dmc.tick(bombId));

        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);

        res = dmc.tick(Direction.UP);

        //this activates the switch
        res = dmc.tick(Direction.RIGHT);

        // Check Bomb exploded with radius 2
        //
        //              Boulder/Switch      Wall            Wall
        //              Bomb                Treasure
        //
        //              Treasure
        assertEquals(0, getEntities(res, "bomb").size());
        assertEquals(0, getEntities(res, "boulder").size());
        assertEquals(0, getEntities(res, "switch").size());
        assertEquals(0, getEntities(res, "wall").size());
        assertEquals(0, getEntities(res, "treasure").size());
        assertEquals(1, getEntities(res, "player").size());
    }

    @Test
    @DisplayName("Test surrounding entities are removed when placing a bomb next to an active switch with config file bomb radius set to 1, switch is activated after bomb is placed")
    public void placeBombRadius1BombPlacedBeforeSwitchActive() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bombTest_placeBombRadius2", "c_movementTest_testMovementDown");

        // Walk to bomb
        res = dmc.tick(Direction.DOWN);

        // Pick up Bomb
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "bomb").size());

        res = dmc.tick(Direction.RIGHT);
        String bombId = getInventory(res, "bomb").get(0).getId();
        res = assertDoesNotThrow(() -> dmc.tick(bombId));

        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);

        res = dmc.tick(Direction.UP);

        //this activates the switch
        res = dmc.tick(Direction.RIGHT);

        // Check Bomb exploded with radius 2
        //
        //              Boulder/Switch      Wall            Wall
        //              Bomb                Treasure
        //
        //              Treasure
        assertEquals(0, getEntities(res, "bomb").size());
        assertEquals(0, getEntities(res, "boulder").size());
        assertEquals(0, getEntities(res, "switch").size());
        assertEquals(1, getEntities(res, "wall").size());
        assertEquals(1, getEntities(res, "treasure").size());
        assertEquals(1, getEntities(res, "player").size());
    }

    @Test
    @DisplayName("Test outer circle for removal of entites")
    public void placeBombRadius2OuterCircle() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bombTest_outerCircle", "c_bombTest_placeBombRadius2");

        // Walk to bomb and pick up 1, 0
        res = dmc.tick(Direction.DOWN);

        // back at 0, 0
        res = dmc.tick(Direction.DOWN);
        
        //res = dmc.tick(Direction.UP);
        //assertEquals(1, getInventory(res, "bomb").size());

        //place bomb
        String bombId = getInventory(res, "bomb").get(0).getId();
        res = assertDoesNotThrow(() -> dmc.tick(bombId));


        // Check Bomb exploded with radius 2
        //
        //              Boulder/Switch      Wall            Wall
        //              Bomb                Treasure
        //
        //              Treasure
        assertEquals(0, getEntities(res, "bomb").size());
        assertEquals(0, getEntities(res, "boulder").size());
        assertEquals(0, getEntities(res, "switch").size());
        assertEquals(10, getEntities(res, "wall").size());
        assertEquals(1, getEntities(res, "player").size());
    }

    @Test
    @DisplayName("Test outer circle for removal of entites second test")
    public void placeBombRadius2OuterCircleSecondTest() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bombTest_outerCircleSecondTest", "c_bombTest_placeBombRadius2");

        // Walk to bomb and pick up 1, 0
        res = dmc.tick(Direction.UP);

        // back at 0, 0
        res = dmc.tick(Direction.UP);
        
        //res = dmc.tick(Direction.UP);
        //assertEquals(1, getInventory(res, "bomb").size());

        //place bomb
        String bombId = getInventory(res, "bomb").get(0).getId();
        res = assertDoesNotThrow(() -> dmc.tick(bombId));


        // Check Bomb exploded with radius 2
        //
        //              Boulder/Switch      Wall            Wall
        //              Bomb                Treasure
        //
        //              Treasure
        assertEquals(0, getEntities(res, "bomb").size());
        assertEquals(0, getEntities(res, "boulder").size());
        assertEquals(0, getEntities(res, "switch").size());
        assertEquals(8, getEntities(res, "wall").size());
        assertEquals(1, getEntities(res, "player").size());
    }
    

}
