package dungeonmania.EntityTests.CollectableEntityTests.WeaponTests;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.DungeonManiaController;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import static dungeonmania.TestUtils.getInventory;


public class BowAndShieldTests {
    @Test
    @DisplayName("bow build behaviour")
    public void testBowCreation() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_buildBowTest", "c_spiderTest_basicMovement");
        
        // Check for correct exception when player doesn't have the items
        assertThrows(InvalidActionException.class, () -> dmc.build("bow"));
        
        // Collect the items
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        DungeonResponse res = dmc.tick(Direction.RIGHT);
        assertTrue(getInventory(res, "wood").size() == 1);
        assertTrue(getInventory(res, "arrow").size() == 3);
        // Check DungeonResponse for correct buildables nwo that you have materials to build a bow
        assertTrue(res.getBuildables().equals(Arrays.asList(new String[]{"bow"})));

        // Build with enough items
        res = assertDoesNotThrow(() -> dmc.build("bow"));
        assertTrue(getInventory(res, "wood").size() == 0);
        assertTrue(getInventory(res, "arrow").size() == 0);
        assertTrue(res.getBuildables().size() == 0);
        assertTrue(getInventory(res, "bow").size() == 1);

    }

    @Test
    @DisplayName("shield build behaviour")
    public void testShieldCreation() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_buildShieldTest", "c_spiderTest_basicMovement");
        
        // Check for correct exception when player doesn't have the items
        assertThrows(InvalidActionException.class, () -> dmc.build("shield"));
        
        // Collect the items
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        DungeonResponse res = dmc.tick(Direction.RIGHT);
        assertTrue(getInventory(res, "wood").size() == 2);
        assertTrue(getInventory(res, "treasure").size() == 1);
        // Check DungeonResponse for correct buildables nwo that you have materials to build a shield
        assertEquals(res.getBuildables(), Arrays.asList(new String[]{"shield"}));

        // Try bulding shield with key
        res = assertDoesNotThrow(() -> dmc.build("shield"));
        assertTrue(getInventory(res, "wood").size() == 0);
        assertEquals(0, getInventory(res, "treasure").size());
        assertTrue(res.getBuildables().size() == 0);
        assertTrue(getInventory(res, "shield").size() == 1);

        // Collect the items
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertTrue(getInventory(res, "wood").size() == 2);
        assertTrue(getInventory(res, "key").size() == 1);
        assertTrue(res.getBuildables().equals(Arrays.asList(new String[]{"shield"})));

        // Build with enough items
        res = assertDoesNotThrow(() -> dmc.build("shield"));
        assertTrue(getInventory(res, "wood").size() == 0);
        assertTrue(getInventory(res, "key").size() == 0);
        assertTrue(res.getBuildables().size() == 0);
        assertTrue(getInventory(res, "shield").size() == 2);
    }

    @Test
    @DisplayName("inavlid build behaviour")
    public void testInvalidBuild() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_buildShieldTest", "c_spiderTest_basicMovement");
        
        // Check for correct exception when player tried to build not bow/shield
        assertThrows(IllegalArgumentException.class, () -> dmc.build("notvalid"));
    }

    @Test
    @DisplayName("buildable response for bow and shield")
    public void testBuildableResponse() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_buildBowShieldTest", "c_spiderTest_basicMovement");
        
        // Collect the items
        DungeonResponse res = dmc.tick(Direction.RIGHT);
        assertTrue(res.getBuildables().size() == 0);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertTrue(res.getBuildables().equals(Arrays.asList(new String[]{"bow"})));
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertTrue(res.getBuildables().equals(Arrays.asList(new String[]{"bow", "shield"})));

        // Build with enough items
        res = assertDoesNotThrow(() -> dmc.build("bow"));
        // Check response
        assertTrue(res.getBuildables().equals(Arrays.asList(new String[]{"shield"})));
        assertTrue(getInventory(res, "bow").size() == 1);

        
        // Collect enough items to make another shield
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        // Build with enough items
        res = assertDoesNotThrow(() -> dmc.build("shield"));
        // Check response
        assertTrue(res.getBuildables().equals(Arrays.asList(new String[]{"shield"})));
        assertTrue(getInventory(res, "shield").size() == 1);
        // Build with enough items
        res = assertDoesNotThrow(() -> dmc.build("shield"));
        // Check response
        assertTrue(res.getBuildables().size() == 0);
        assertTrue(getInventory(res, "shield").size() == 2);
    }

}
