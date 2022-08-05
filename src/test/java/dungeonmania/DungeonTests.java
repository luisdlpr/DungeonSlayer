package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.Entities.Entity;
import dungeonmania.Entities.StaticEntities.PlaceholderEntity;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

/**
 * Tests for dungeon class
 * @author Luis Reyes (z5206766)
 */
public class DungeonTests {
    @Test
    @DisplayName("Test the dungeon files are correctly read and a dungeon is created")
    public void testDungeonConstructIdName() {
        // constructor takes in dungeon filename, config filename, and int representing how many dungeons have been created (passed in from controller)
        Dungeon dungeon = new Dungeon("d_movementTest_testMovementDown", "c_movementTest_testMovementDown", 0);
        
        // check id is equal
        assertEquals("0", dungeon.getId());
        
        // check name is equal
        assertEquals("d_movementTest_testMovementDown", dungeon.getName());
    }
    
    @Test
    @DisplayName("Test the dungeon creates the correct mock entity list from a given pathname")
    public void testDungeonConstructEntities() {
        Dungeon dungeon = new Dungeon("d_movementTest_testMovementDown", "c_movementTest_testMovementDown", 0);
        Position exitPosition = new Position(1, 3);
        Entity exit = new PlaceholderEntity("exit0", "exit", exitPosition, false);

        Position playerPosition = new Position(1, 1);
        Entity player = new PlaceholderEntity("player", "player", playerPosition, false);

        List<Entity> entities = dungeon.getEntities();

        // not storing player in entities
        assertTrue(assertEntityExistsInList(entities, exit));
        assertTrue(dungeon.getPlayer().getEntityResponse().equals(player.getEntityResponse()));
    }

    private Boolean assertEntityExistsInList(List<Entity> actual, Entity expected) {
        for (Entity e : actual) {
            if (e.getEntityResponse().equals(expected.getEntityResponse())) {
                return true;
            }
        }
        return false;
    }
    
    @Test
    @DisplayName("Test newGame correctly instantiates the dungeon (without full dungeon response for now)")
    public void basicMovement() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_movementTest_testMovementDown", "c_movementTest_testMovementDown");
        
        // check id is equal
        assertEquals("0", res.getDungeonId());
    
        // check name is equal
        assertEquals("d_movementTest_testMovementDown", res.getDungeonName());
    }

    @Test
    @DisplayName("Test newGame throws error for invalid dungeon or config")
    public void invalidNewGame() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();

        assertThrows(IllegalArgumentException.class, () -> {
            dmc.newGame("invalid", "c_movementTest_testMovementDown");
        }); 
        assertThrows(IllegalArgumentException.class, () -> {
            dmc.newGame("d_movementTest_testMovementDown", "invalid");
        }); 
    }

    @Test
    @DisplayName("Test using invalid items or non existant items")
    public void invalidItemTick() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        dmc.newGame("d_DoorsAndKeysWrongKey", "c_movementTest_testMovementDown");
        dmc.tick(Direction.RIGHT);

        assertThrows(IllegalArgumentException.class, () -> {
            dmc.tick("key0");
        }); 
        assertThrows(InvalidActionException.class, () -> {
            dmc.tick("invincibility_potion0");
        }); 
    }
}
