package dungeonmania;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static dungeonmania.TestUtils.getEntities;
import static dungeonmania.TestUtils.getInventory;

public class DoorsAndKeysTests {
    
    @Test
    @DisplayName("Test two doors")
    public void testTwoDoorsOneKey() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_DoorsAndKeysTwoDoors", "c_DoorsKeysTest_useKeyWalkThroughOpenDoor");

        // pick up key
        res = dmc.tick(Direction.RIGHT);
        // Position pos = getEntities(res, "player").get(0).getPosition();
        assertEquals(1, getInventory(res, "key").size());

        // walk into door and check key is gone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, getInventory(res, "key").size());
        //assertNotEquals(pos, getEntities(res, "player").get(0).getPosition());

        //walk one step up to pick up next key
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "key").size());

        //walk into the door and check the key is gone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, getInventory(res, "key").size());
    }
    
    @Test
    @DisplayName("Test two keys walking over behaviour")
    public void testTwoKeysOneDoor() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_DoorsAndKeysTwoKeys", "c_DoorsKeysTest_useKeyWalkThroughOpenDoor");

        // pick up key
        res = dmc.tick(Direction.RIGHT);
        // Position pos = getEntities(res, "player").get(0).getPosition();
        assertEquals(1, getInventory(res, "key").size());

        // walk over second key which should not be picked up
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "key").size());
        //assertNotEquals(pos, getEntities(res, "player").get(0).getPosition());

        //walk one onto an empy tile now, still should only have one key
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "key").size());

    }

    @Test
    @DisplayName("Test two door collision with wrong key used")
    public void testWrongKeyUsed() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_DoorsAndKeysWrongKey", "c_DoorsKeysTest_useKeyWalkThroughOpenDoor");

        // pick up key
        res = dmc.tick(Direction.RIGHT);
        //Position pos = getEntities(res, "player").get(0).getPosition();
        assertEquals(1, getInventory(res, "key").size());

        // walk into to door try open
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "key").size());
        Position pos = getEntities(res, "player").get(0).getPosition();

        //wthe door does not match the key, should not open, key should be in inventory still
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "key").size());
        
        //assert that the player is in the same position after failing to open the door as he was when he was standing in front of it
        assertEquals(pos, getEntities(res, "player").get(0).getPosition());

    }

    

    
}