package dungeonmania.EntityTests.CollectableEntityTests;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.Dungeon;
import dungeonmania.DungeonManiaController;
import dungeonmania.Helper;
import dungeonmania.Entities.Entity;
import dungeonmania.Entities.CollectableEntities.SunStoneEntity;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

import static dungeonmania.TestUtils.getInventory;
import static dungeonmania.TestUtils.getEntities;
import static dungeonmania.TestUtils.getGoals;

public class SunStoneTests {

    Helper helper = new Helper();

    @Test
    @DisplayName("sun stone construction")
    public void testUnitSunStoneCreation() {
        // Create new sunstone
        SunStoneEntity sunstone = new SunStoneEntity(helper.createEntityJSON("sun_stone","1", "0"), 0);
        Position expectedPosition = new Position(1, 0);
        
        // Check that id and position are correct
        assertTrue(expectedPosition.equals(sunstone.getEntityResponse().getPosition()));
        assertTrue("sun_stone0".equals(sunstone.getEntityResponse().getId()));
    }

    @Test
    @DisplayName("sunstone with correct position from string in controller")
    public void testSunSToneCreationController() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_sunStoneTests", "M3_sunStoneSceptreTest");
        Dungeon dungeon = dmc.currentDungeon;

        List<Entity> entities = dungeon.getEntities();

        SunStoneEntity sunstone1 = new SunStoneEntity(helper.createEntityJSON("sun_stone", "0", "2"), 0);
        SunStoneEntity sunstone2 = new SunStoneEntity(helper.createEntityJSON("sun_stone", "1", "1"), 1);

        assertTrue(helper.assertEntityExistsInList(entities, sunstone1));
        assertTrue(helper.assertEntityExistsInList(entities, sunstone2));

        for (Entity e : entities) {
            if (e.getType().equals("sun_stone")){
                assertTrue(e.getClass().getSimpleName().equals("SunStoneEntity"));
            }
        }
    }

    @Test
    @DisplayName("collecting sunstone to inventory and opening door")
    public void testSunStoneDoor() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_sunStoneTests", "M3_sunStoneSceptreTest");

        // p
        // s
        // door

        // Collect sun stone
        DungeonResponse res = dmc.tick(Direction.DOWN);
        // Check that treasure has been added to inventory
        assertTrue(getInventory(res, "sun_stone").size() == 1);

        // Check that door is opened when player walks on it
        dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(0, 4), getEntities(res, "player").get(0).getPosition());
    }

    @Test
    @DisplayName("collecting sunstone to inventory and building shield")
    public void testSunStoneBuildShield() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_sunStoneTests", "M3_sunStoneSceptreTest");

        // p    ss  wood    wood

        // Collect materials
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        DungeonResponse res = dmc.tick(Direction.RIGHT);
        // Check that materials has been added to inventory
        assertTrue(getInventory(res, "sun_stone").size() == 1);
        assertTrue(getInventory(res, "wood").size() == 2);
        // Check buildables response
        assertEquals("shield", res.getBuildables().get(0));

        // Build shield
        res = assertDoesNotThrow(() -> dmc.build("shield"));
        assertEquals(1, getInventory(res, "shield").size());
    }

    @Test
    @DisplayName("collecting sunstone for treasure goal")
    public void testSunStoneTreasureGoal() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_sunStoneTests", "M3_sunStoneSceptreTest");

        // Collect 2 sun stones
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.UP);
        DungeonResponse res = dmc.tick(Direction.RIGHT);
        assertEquals(2, getInventory(res, "sun_stone").size());

        // Check response for empty goal string
        assertEquals("", getGoals(res));
    }

}
