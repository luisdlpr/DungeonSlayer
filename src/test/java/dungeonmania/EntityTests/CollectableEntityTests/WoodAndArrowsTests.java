package dungeonmania.EntityTests.CollectableEntityTests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.Dungeon;
import dungeonmania.DungeonManiaController;
import dungeonmania.Helper;
import dungeonmania.Entities.Entity;
import dungeonmania.Entities.CollectableEntities.ArrowEntity;
import dungeonmania.Entities.CollectableEntities.WoodEntity;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import static dungeonmania.TestUtils.getInventory;

public class WoodAndArrowsTests {
    Helper helper = new Helper();

    @Test
    @DisplayName("correct funtionality of wood construction")
    public void testUnitWoodCreation() {
        // Create new wood
        WoodEntity wood = new WoodEntity(helper.createEntityJSON("wood","1", "0"), 0);
        Position expectedPosition = new Position(1, 0);
        
        // Check that id and position are correct
        assertTrue(expectedPosition.equals(wood.getEntityResponse().getPosition()));
        assertTrue("wood0".equals(wood.getEntityResponse().getId()));
    }

    @Test
    @DisplayName("wood with correct position from string in controller")
    public void testWoodCreationController() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_woodAndArrowTest_basic", "c_movementTest_testMovementDown");
        Dungeon dungeon = dmc.currentDungeon;

        List<Entity> entities = dungeon.getEntities();

        WoodEntity wood = new WoodEntity(helper.createEntityJSON("wood", "1", "0"), 0);

        assertTrue(helper.assertEntityExistsInList(entities, wood));

        for (Entity e : entities) {
            if (e.getType().equals("wood")){
                assertTrue(e.getClass().getSimpleName().equals("WoodEntity"));
            }
        }
    }

    @Test
    @DisplayName("correct funtionality of arrow construction")
    public void testUnitArrowCreation() {
        // Create new arrow
        WoodEntity arrow = new WoodEntity(helper.createEntityJSON("arrow","1", "0"), 0);
        Position expectedPosition = new Position(1, 0);
        
        // Check that id and position are correct
        assertTrue(expectedPosition.equals(arrow.getEntityResponse().getPosition()));
        assertTrue("arrow0".equals(arrow.getEntityResponse().getId()));
    }

    @Test
    @DisplayName("arrow with correct position from string in controller")
    public void testArrowCreationController() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_woodAndArrowTest_basic", "c_movementTest_testMovementDown");
        Dungeon dungeon = dmc.currentDungeon;

        List<Entity> entities = dungeon.getEntities();

        ArrowEntity arrow = new ArrowEntity(helper.createEntityJSON("arrow", "2", "0"), 0);

        assertTrue(helper.assertEntityExistsInList(entities, arrow));

        for (Entity e : entities) {
            if (e.getType().equals("arrow")){
                assertTrue(e.getClass().getSimpleName().equals("ArrowEntity"));
            }
        }
    }

    @Test
    @DisplayName("collecting wood and arrows to player inventory")
    public void testWoodArrowAddToInventory() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_woodAndArrowTest_basic", "c_movementTest_testMovementDown");

        // Move right to collect wood
        DungeonResponse res = dmc.tick(Direction.RIGHT);
        // Check that wood has been added to inventory
        assertTrue(getInventory(res, "wood").size() == 1);

        // Move right to collect arrow
        res = dmc.tick(Direction.RIGHT);
        // Check that arrow has been added to inventory
        assertTrue(getInventory(res, "arrow").size() == 1);

        // Check that walking back on positions where treasure was collected
        // does not change the inventory
        dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        assertTrue(getInventory(res, "wood").size() == 1);
        assertTrue(getInventory(res, "arrow").size() == 1);
    }

}
