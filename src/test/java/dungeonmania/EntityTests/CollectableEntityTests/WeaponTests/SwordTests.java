package dungeonmania.EntityTests.CollectableEntityTests.WeaponTests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.Dungeon;
import dungeonmania.DungeonManiaController;
import dungeonmania.Helper;
import dungeonmania.Entities.Entity;
import dungeonmania.Entities.CollectableEntities.Weapons.SwordEntity;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import static dungeonmania.TestUtils.getInventory;

public class SwordTests {
    Helper helper = new Helper();

    @Test
    @DisplayName("correct funtionality of sword construction")
    public void testUnitSwordCreation() {
        // Create a new sword
        SwordEntity sword = new SwordEntity(helper.createEntityJSON("sword","1", "0"), 0, 1, 1);
        Position expectedPosition = new Position(1, 0);
        
        // Check that id and position are correct
        assertTrue(expectedPosition.equals(sword.getEntityResponse().getPosition()));
        assertTrue("sword0".equals(sword.getEntityResponse().getId()));
    }
    
    @Test
    @DisplayName("sword creation from string in controller")
    public void testSwordCreationController() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_swordTest_basic", "c_movementTest_testMovementDown");
        Dungeon dungeon = dmc.currentDungeon;

        List<Entity> entities = dungeon.getEntities();

        SwordEntity sword = new SwordEntity(helper.createEntityJSON("sword","1", "2"), 0, 2, 1);

        // Check that sword exists
        assertTrue(helper.assertEntityExistsInList(entities, sword));

        for (Entity e : entities) {
            if (e.getType().equals("sword")){
                assertTrue(e.getClass().getSimpleName().equals("SwordEntity"));
            }
        }
    }

    @Test
    @DisplayName("player picking up sword")
    public void testCollectSword() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_swordTest_basic", "c_movementTest_testMovementDown");

        // Move right to collect sword
        DungeonResponse res = dmc.tick(Direction.DOWN);
        // Check that sword has been added to inventory
        assertTrue(getInventory(res, "sword").size() == 1);
    }

}
