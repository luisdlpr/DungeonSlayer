package dungeonmania.EntityTests;

import org.junit.jupiter.api.Test;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static dungeonmania.TestUtils.getEntities;

public class LogicTests {
    @Test
    public void testSwitchBulb() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("logicA", "simpleAssassin");

        assertEquals(1, getEntities(res, "light_bulb_off").size());
        assertEquals(0, getEntities(res, "light_bulb_on").size());

        // push boulder onto switch
        res = dmc.tick(Direction.RIGHT);
        
        // assert the bulb is switched on
        assertEquals(0, getEntities(res, "light_bulb_off").size());
        assertEquals(1, getEntities(res, "light_bulb_on").size());

        // walk around to push boulder off switch
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);

        // assert that the lightbulb is switched off
        assertEquals(1, getEntities(res, "light_bulb_off").size());
        assertEquals(0, getEntities(res, "light_bulb_on").size());

    }

    @Test
    public void testSwitchBulbOneWire() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("logicB", "simpleAssassin");

        assertEquals(1, getEntities(res, "light_bulb_off").size());
        assertEquals(0, getEntities(res, "light_bulb_on").size());

        // push boulder onto switch
        res = dmc.tick(Direction.RIGHT);
        
        // assert the bulb is switched on
        assertEquals(0, getEntities(res, "light_bulb_off").size());
        assertEquals(1, getEntities(res, "light_bulb_on").size());

        // walk around to push boulder off switch
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);

        // assert that the lightbulb is switched off
        assertEquals(1, getEntities(res, "light_bulb_off").size());
        assertEquals(0, getEntities(res, "light_bulb_on").size());


    }

    @Test
    public void testSwitchDoor() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("logicDoor", "simpleAssassin");

        // push boulder onto switch
        res = dmc.tick(Direction.RIGHT);

        // walk around to door, avoiding key
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        // assert player position is equal to door position, meaning the player is able to walk on door
        assertEquals(getEntities(res, "player").get(0).getPosition(), getEntities(res, "switch_door").get(0).getPosition());


    }

    @Test
    public void testSwitchDoorUsingKey() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("logicDoor", "simpleAssassin");

        // walk to door, picking up key not activating switch
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);

        // assert player position is equal to door position, meaning the player is able to walk on door
        assertEquals(getEntities(res, "player").get(0).getPosition(), getEntities(res, "switch_door").get(0).getPosition());

    }
}
