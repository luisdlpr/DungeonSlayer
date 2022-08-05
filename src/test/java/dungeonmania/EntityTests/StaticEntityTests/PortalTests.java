package dungeonmania.EntityTests.StaticEntityTests;

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
import dungeonmania.Entities.StaticEntities.PortalEntity;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import static dungeonmania.TestUtils.getEntities;

public class PortalTests {

    Helper helper = new Helper();

    @Test
    @DisplayName("correct funtionality of portal construction")
    public void testUnitPortalCreation() {
        // Create a new portal
        PortalEntity portal = new PortalEntity(helper.createEntityJSON("portal","1", "0"), 0, "BLUE");
        Position expectedPosition = new Position(1, 0);
        
        // Check that id and position are correct
        assertTrue(expectedPosition.equals(portal.getEntityResponse().getPosition()));
        assertTrue("portal0".equals(portal.getEntityResponse().getId()));
    }
    

    @Test
    @DisplayName("pair of portals with correct position from string in controller")
    public void testPortalCreationController() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_portalTest_basic", "c_movementTest_testMovementDown");
        Dungeon dungeon = dmc.currentDungeon;

        List<Entity> entities = dungeon.getEntities();

        PortalEntity portal0 = new PortalEntity(helper.createEntityJSON("portal","1", "1"), 0, "BLUE");
        PortalEntity portal1 = new PortalEntity(helper.createEntityJSON("portal","3", "3"), 1, "BLUE");
        portal0.setOtherPortal(portal1);
        portal1.setOtherPortal(portal0);

        // Check that correct portal objects exist
        // With correct counterparts
        assertTrue(helper.assertEntityExistsInList(entities, portal0));
        assertTrue(helper.assertEntityExistsInList(entities, portal1));

        for (Entity e : entities) {
            if (e.getType().equals("portal")){
                assertTrue(e.getClass().getSimpleName().equals("PortalEntity"));
            }
        }
    }

    @Test
    @DisplayName("player walking through portal")
    public void testPlayerWalkOnPortal() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_portalTest_basic", "c_movementTest_testMovementDown");

        // Make the player move right to walk into the portal
        DungeonResponse res = dmc.tick(Direction.RIGHT);
        Position player = getEntities(res, "player").get(0).getPosition();
        // The expect position is (3, 4), right of the portal
        Position expectedPosition = new Position(4, 3);
        assertEquals(expectedPosition, player);

        // Test walking back into the portal
        res = dmc.tick(Direction.LEFT);
        player = getEntities(res, "player").get(0).getPosition();
        // The expect position is (2, 1), right of first
        expectedPosition = new Position(2, 1);
        assertEquals(expectedPosition, player);
    }

    @Test
    @DisplayName("player walking to portal next to walls")
    public void testPlayerToCrowdedPortal() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_portalTest_crowded", "c_movementTest_testMovementDown");

        // Make the player move right to walk into the portal
        DungeonResponse res = dmc.tick(Direction.RIGHT);
        Position player = getEntities(res, "player").get(0).getPosition();
        // The expect position is (2, 4), to the left of the portal
        Position expectedPosition = new Position(2, 4);
        assertEquals(expectedPosition, player);

        // Walk into another portal
        res = dmc.tick(Direction.LEFT);
        player = getEntities(res, "player").get(0).getPosition();
        // The expect position is (3, 3), to the left of portal
        expectedPosition = new Position(2, 2);
        assertEquals(expectedPosition, player);
    }

    @Test
    @DisplayName("portals in tiny map")
    public void testPlayerToTinyPortal() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_portalTest_tiny", "c_movementTest_testMovementDown");

        // Make the player walk into the portal
        DungeonResponse res = dmc.tick(Direction.UP);
        Position player = getEntities(res, "player").get(0).getPosition();
        Position expectedPosition = new Position(2, 3);
        assertEquals(expectedPosition, player);

        // Walk into other side portal
        res = dmc.tick(Direction.RIGHT);
        player = getEntities(res, "player").get(0).getPosition();
        assertEquals(expectedPosition, player);
    }

    @Test
    @DisplayName("player walking into portal surrounded by blockers at other")
    public void testPlayerBlockedPortal() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_portalTest_walls", "c_movementTest_testMovementDown");

        // Make the player walk into the portal
        DungeonResponse res = dmc.tick(Direction.RIGHT);
        Position player = getEntities(res, "player").get(0).getPosition();
        // Player shouldn't move because other side is surrounded by walls
        Position expectedPosition = new Position(0, 1);
        assertEquals(expectedPosition, player);
    }

    // floor switch, exit, collectable

    @Test
    @DisplayName("player walking through multiple portals")
    public void testPlayerWalkOnPortalMultiple() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_portalTest_basic", "c_movementTest_testMovementDown");

        // Make the player move right to walk into the portal
        DungeonResponse res = dmc.tick(Direction.RIGHT);
        Position player = getEntities(res, "player").get(0).getPosition();
        // The expected position is (3, 4), the other half of the portal
        Position expectedPosition = new Position(4, 3);
        assertEquals(expectedPosition, player);

        // Make player walk into a different portal
        res = dmc.tick(Direction.RIGHT);
        player = getEntities(res, "player").get(0).getPosition();
        // The expected position is (5, 5), the other half of this portal
        expectedPosition = new Position(6, 5);
        assertEquals(expectedPosition, player);
        // Test walking back into the portal
        res = dmc.tick(Direction.LEFT);
        player = getEntities(res, "player").get(0).getPosition();
        expectedPosition = new Position(6, 3);
        assertEquals(expectedPosition, player);
    }

    @Test
    @DisplayName("portal chaining with blocks")
    public void testPortalChainingWithBlocks() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_portalChainingTest", "c_movementTest_testMovementDown");

        // Make the player move right to walk into the portal
        DungeonResponse res = dmc.tick(Direction.RIGHT);
        Position player = getEntities(res, "player").get(0).getPosition();
        // The expected position after portal chaining
        Position expectedPosition = new Position(2, 3);
        assertEquals(expectedPosition, player);
    }

    @Test
    @DisplayName("portal chaining")
    public void testPortalChaining1() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_portalsAdvanced", "c_movementTest_testMovementDown");
        // Start at (3,0)
        // Make the player move right to walk into the portal
        DungeonResponse res = dmc.tick(Direction.RIGHT);
        Position player = getEntities(res, "player").get(0).getPosition();
        // Player should go to red then grey portal
        Position expectedPosition = new Position(6, 1);
        assertEquals(expectedPosition, player);
        
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);

        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.UP);
        player = getEntities(res, "player").get(0).getPosition();
        // Player should go to red then grey portal
        expectedPosition = new Position(5, 0);
        assertEquals(expectedPosition, player);
    }

    @Test
    @DisplayName("portal mercenary")
    public void testPortalMercenary() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_portalMercenaryTest", "c_movementTest_testMovementDown");
        dmc.tick(Direction.UP);
        assertDoesNotThrow(() -> dmc.interact("mercenary0"));
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);

        assertEquals(new Position(4, 3), helper.getEntityPosition(dmc, "mercenary0"));

    }

}
