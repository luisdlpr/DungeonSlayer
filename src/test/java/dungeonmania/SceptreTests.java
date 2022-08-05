package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;

import static dungeonmania.TestUtils.getInventory;
import static dungeonmania.TestUtils.getEntities;

public class SceptreTests {

    Helper helper = new Helper();

    // Unit/Integration: Test creation and addition to inventory with both recipes
    // Integration: Test Mercenary and assassin mind control

    // 1 sceptre = (1 wood OR 2 arrows) + (1 key OR 1 treasure) + (1 sunstone)
    // 1 sceptre + 1 (retained) sunstone = (1 wood OR 2 arrows) + (1 sunstone) + (1 sunstone)
    
    @Test
    @DisplayName("building with 1 wood, 1 key, 1 sunstone")
    public void testSceptreBuild1() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_sceptreBuildTest_1SunStone", "M3_sunStoneSceptreTest");

        // Collect materials
        dmc.tick(Direction.UP);
        DungeonResponse res = dmc.tick(Direction.UP);
        // Check buildables is correct with insufficient materials
        assertEquals(res.getBuildables(), Arrays.asList());
        
        // Check buildables with sufficient materials
        res = dmc.tick(Direction.UP);
        assertEquals(1, getInventory(res, "wood").size());
        assertEquals(1, getInventory(res, "key").size());
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(Arrays.asList(new String[]{"sceptre"}), res.getBuildables());

        // Build sceptre
        res = assertDoesNotThrow(() -> dmc.build("sceptre"));
        assertEquals(1, getInventory(res, "sceptre").size());
        assertEquals(0, getInventory(res, "wood").size());
        assertEquals(0, getInventory(res, "key").size());
        assertEquals(0, getInventory(res, "sun_stone").size());
        assertEquals(Arrays.asList(), res.getBuildables());
    }

    @Test
    @DisplayName("building with 2 arrows, 1 key, 1 sunstone")
    public void testSceptreBuild2() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_sceptreBuildTest_1SunStone", "M3_sunStoneSceptreTest");

        // Collect materials
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        DungeonResponse res = dmc.tick(Direction.DOWN);

        // Check buildables with sufficient materials
        assertEquals(2, getInventory(res, "arrow").size());
        assertEquals(1, getInventory(res, "key").size());
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(Arrays.asList(new String[]{"sceptre"}), res.getBuildables());

        // Build sceptre
        res = assertDoesNotThrow(() -> dmc.build("sceptre"));
        assertEquals(1, getInventory(res, "sceptre").size());
        assertEquals(0, getInventory(res, "arrow").size());
        assertEquals(0, getInventory(res, "key").size());
        assertEquals(0, getInventory(res, "sun_stone").size());
        assertEquals(Arrays.asList(), res.getBuildables());
    }

    @Test
    @DisplayName("building with 1 wood, 1 treasure, 1 sunstone")
    public void testSceptreBuild3() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_sceptreBuildTest_1SunStone", "M3_sunStoneSceptreTest");

        // Collect materials
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        DungeonResponse res = dmc.tick(Direction.LEFT);

        // Check buildables with sufficient materials
        assertEquals(1, getInventory(res, "wood").size());
        assertEquals(1, getInventory(res, "treasure").size());
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(Arrays.asList(new String[]{"sceptre"}), res.getBuildables());

        // Build sceptre
        res = assertDoesNotThrow(() -> dmc.build("sceptre"));
        assertEquals(1, getInventory(res, "sceptre").size());
        assertEquals(0, getInventory(res, "wood").size());
        assertEquals(0, getInventory(res, "treasure").size());
        assertEquals(0, getInventory(res, "sun_stone").size());
        assertEquals(Arrays.asList(), res.getBuildables());
    }

    @Test
    @DisplayName("building with 2 arrows, 1 treasure, 1 sunstone")
    public void testSceptreBuild4() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_sceptreBuildTest_1SunStone", "M3_sunStoneSceptreTest");

        // Collect materials
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        DungeonResponse res = dmc.tick(Direction.RIGHT);

        // Check buildables with sufficient materials
        assertEquals(2, getInventory(res, "arrow").size());
        assertEquals(1, getInventory(res, "treasure").size());
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(Arrays.asList(new String[]{"sceptre"}), res.getBuildables());

        // Build sceptre
        res = assertDoesNotThrow(() -> dmc.build("sceptre"));
        assertEquals(1, getInventory(res, "sceptre").size());
        assertEquals(0, getInventory(res, "arrow").size());
        assertEquals(0, getInventory(res, "treasure").size());
        assertEquals(0, getInventory(res, "sun_stone").size());
        assertEquals(Arrays.asList(), res.getBuildables());
    }

    @Test
    @DisplayName("building with 1 wood, 1 sunstone, 1 sunstone")
    public void testSceptreBuild5() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_sceptreBuildTest_2SunStone", "M3_sunStoneSceptreTest");

        // Collect materials
        dmc.tick(Direction.UP);
        dmc.tick(Direction.UP);
        DungeonResponse res = dmc.tick(Direction.UP);

        // Check buildables with sufficient materials
        assertEquals(1, getInventory(res, "wood").size());
        assertEquals(2, getInventory(res, "sun_stone").size());
        assertEquals(Arrays.asList(new String[]{"sceptre"}), res.getBuildables());

        // Build sceptre
        res = assertDoesNotThrow(() -> dmc.build("sceptre"));
        assertEquals(1, getInventory(res, "sceptre").size());
        assertEquals(0, getInventory(res, "wood").size());
        // 1 sun stone should be retained because it was used as a replacement for treasure/key
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(Arrays.asList(), res.getBuildables());
    }

    @Test
    @DisplayName("building with 2 arrows, 1 sunstone, 1 sunstone")
    public void testSceptreBuild6() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_sceptreBuildTest_2SunStone", "M3_sunStoneSceptreTest");

        // Collect materials
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        DungeonResponse res = dmc.tick(Direction.DOWN);

        // Check buildables with sufficient materials
        assertEquals(2, getInventory(res, "arrow").size());
        assertEquals(2, getInventory(res, "sun_stone").size());
        assertEquals(Arrays.asList(new String[]{"sceptre"}), res.getBuildables());

        // Build sceptre
        res = assertDoesNotThrow(() -> dmc.build("sceptre"));
        assertEquals(1, getInventory(res, "sceptre").size());
        assertEquals(0, getInventory(res, "arrow").size());
        // 1 sun stone should be retained because it was used as a replacement for treasure/key
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(Arrays.asList(), res.getBuildables());
    }

    @Test
    @DisplayName("mercenary and assassin mind control when player has sceptre")
    public void testMercenaryAssassinSceptre() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_mindControlTest", "M3_sunStoneSceptreTest");

        // Check exception is thrown when trying to mind control when player has no gold and no sceptre
        assertThrows(InvalidActionException.class, () -> dmc.interact("mercenary0"));
        assertThrows(InvalidActionException.class, () -> dmc.interact("assassin0"));

        // Build sceptre
        dmc.tick(Direction.UP);
        dmc.tick(Direction.UP);
        dmc.tick(Direction.UP);
        DungeonResponse res = assertDoesNotThrow(() -> dmc.build("sceptre"));

        // Try to mind control with sceptre
        res = assertDoesNotThrow(() -> dmc.interact("mercenary0"));
        // Check that merc is in bribed state
        assertEquals("BribedState", helper.getState(dmc, "mercenary0"));
        // Check that the mercenary is no longer interactable
        // Meaning they have been mind controlled
        EntityResponse mercenary = getEntities(res, "mercenary").get(0);
        assertEquals(false, mercenary.isInteractable());

        // Let 1 tick pass before trying to mind control assassin
        dmc.tick(Direction.LEFT);
        res = assertDoesNotThrow(() -> dmc.interact("assassin0"));
        assertEquals("BribedState", helper.getState(dmc, "assassin0"));
        EntityResponse assassin = getEntities(res, "assassin").get(0);
        assertEquals(false, assassin.isInteractable());

        // Let another tick pass and the tick after that mercenary should be interactable again (not controlled)
        dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        // Check that merc has gone back to being hostile after 2 ticks
        // Mind control has ended
        assertEquals("HostileState", helper.getState(dmc, "mercenary0"));
        mercenary = getEntities(res, "mercenary").get(0);
        assertEquals(true, mercenary.isInteractable());

        // Assassin should be uncontrolled the tick after this
        res = dmc.tick(Direction.LEFT);
        assertEquals("HostileState", helper.getState(dmc, "assassin0"));
        assassin = getEntities(res, "assassin").get(0);
        assertEquals(true, assassin.isInteractable());
    }

}
