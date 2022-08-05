package dungeonmania.EntityTests.CollectableEntityTests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.Dungeon;
import dungeonmania.DungeonManiaController;
import dungeonmania.Helper;
import dungeonmania.Entities.Entity;
import dungeonmania.Entities.CollectableEntities.TreasureEntity;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import static dungeonmania.TestUtils.getInventory;

public class TreasureTests {

    Helper helper = new Helper();

    @Test
    @DisplayName("correct funtionality of treasure construction")
    public void testUnitTreasureCreation() {
        // Create new treasure
        TreasureEntity treasure = new TreasureEntity(helper.createEntityJSON("treasure","1", "0"), 0);
        Position expectedPosition = new Position(1, 0);
        
        // Check that id and position are correct
        assertTrue(expectedPosition.equals(treasure.getEntityResponse().getPosition()));
        assertTrue("treasure0".equals(treasure.getEntityResponse().getId()));
    }

    @Test
    @DisplayName("treasure with correct position from string in controller")
    public void testTreasureCreationController() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_treasureTest_basic", "c_movementTest_testMovementDown");
        Dungeon dungeon = dmc.currentDungeon;

        List<Entity> entities = dungeon.getEntities();

        TreasureEntity treasure = new TreasureEntity(helper.createEntityJSON("treasure", "1", "0"), 0);

        assertTrue(helper.assertEntityExistsInList(entities, treasure));

        for (Entity e : entities) {
            if (e.getType().equals("treasure")){
                assertTrue(e.getClass().getSimpleName().equals("TreasureEntity"));
            }
        }
    }

    @Test
    @DisplayName("collecting treasure to player inventory")
    public void testTreasureAddToInventory() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_treasureTest_basic", "c_movementTest_testMovementDown");

        // Move right to collect treasure
        DungeonResponse res = dmc.tick(Direction.RIGHT);
        // Check that treasure has been added to inventory
        assertTrue(getInventory(res, "treasure").size() == 1);

        // Repeat collection test for another piece of treasure
        res = dmc.tick(Direction.RIGHT);
        assertTrue(getInventory(res, "treasure").size() == 2);

        // Check that walking back on positions wher treasure was collected
        // does not change the inventory
        dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        assertTrue(getInventory(res, "treasure").size() == 2);
    }

}
