package dungeonmania.EntityTests.MovingEntityTests;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.Dungeon;
import dungeonmania.DungeonManiaController;
import dungeonmania.Helper;
import dungeonmania.Entities.Entity;
import dungeonmania.Entities.Player;
import dungeonmania.Entities.MoveableEntities.MercenaryEntity;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class MercenaryTests {
    
    Helper helper = new Helper();
    
    // Test mercenary creation
    @Test
    @DisplayName("test constructor for mercenary operates correctly")
    public void testUnitMercenaryCreation() {
        MercenaryEntity mercenary = new MercenaryEntity(helper.createEntityJSON("mercenary","7", "2"),
                                                         0, 10, 20, 3, 1);
        
        Position expectedPosition = new Position(7, 2);
        int expectedHealth = 10;
        int expectedDamage = 20;
        int expectedBribeAmount = 3;
        int expectedBribeRadius = 1;

        assertTrue(expectedPosition.equals(mercenary.getEntityResponse().getPosition()));
        assertTrue("mercenary0".equals(mercenary.getEntityResponse().getId()));
        assertEquals(expectedHealth, mercenary.getHealth());
        assertEquals(expectedDamage, mercenary.getAttackDamage());
        assertEquals(expectedBribeAmount, mercenary.getBribeAmount());
        assertEquals(expectedBribeRadius, mercenary.getBribeRadius());
    }

    @Test
    @DisplayName("WhiteBox test to check mercenaries are created correctly in dungeonController")
    public void testControllerBoulderCreation() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_mercenaryTest_basicMovement", "c_mercenaryTest_basicMovement");
        Dungeon dungeon = dmc.currentDungeon;

        List<Entity> entities = dungeon.getEntities();

        MercenaryEntity mercenary = new MercenaryEntity(helper.createEntityJSON("mercenary","7", "2"),
                                                         0, 10, 20, 3, 1);

        assertTrue(helper.assertEntityExistsInList(entities, mercenary));

        for (Entity e : entities) {
            if (e.getType().equals("mercenary")){
                assertTrue(e.getClass().getSimpleName().equals("MercenaryEntity"));
            }
        }
    }

    // Check if an instantiated mercenary is hostile by default
    @Test
    @DisplayName("Check instantiated mercenary is hostile")
    public void testMercenaryHostileStateDefault() {
        Dungeon dungeon = new Dungeon("d_mercenaryTest_basicMovement", "c_mercenaryTest_basicMovement", 0);
        for (Entity e : dungeon.getEntities()) {
            if (e instanceof MercenaryEntity) {
                MercenaryEntity mercEntity = (MercenaryEntity) e;
                assertEquals("HostileState", (mercEntity.getState().getName()));
            }
        }
    }

    @Test
    @DisplayName("Test basic bribe behaviour")
    public void testBribeMercenaryBasic() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        dmc.newGame("d_mercenaryTest_bribe", "c_mercenaryTest_basicMovement");
    
        // Position the player so they pick up the treasure
        dmc.tick(Direction.RIGHT);
        assertEquals(treasureCount(dmc), 1);

        // Check the mercenary is still hostile
        assertTrue(helper.getState(dmc, "mercenary0").equals("HostileState"));
        assertDoesNotThrow(() -> dmc.interact("mercenary0"));

        // Check mercenary is now bribed state and treasure has been removed
        assertTrue(helper.getState(dmc, "mercenary0").equals("BribedState"));
        assertEquals(treasureCount(dmc), 0);

    }

    @Test
    @DisplayName("No treasure exception")
    public void testBribeNoTreasure() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        dmc.newGame("d_mercenaryTest_bribe", "c_mercenaryTest_basicMovement");
    
        // Position the player so they do not pick up the treasure
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        assertEquals(treasureCount(dmc), 0);

        // Check the mercenary is still hostile
        assertTrue(helper.getState(dmc, "mercenary0").equals("HostileState"));
        assertThrows(InvalidActionException.class, () -> dmc.interact("mercenary0"));
    }

    @Test
    @DisplayName("Not in range exception")
    public void testBribeOutOfRange() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        dmc.newGame("d_mercenaryTest_basicMovement", "c_mercenaryTest_basicMovement");
    
        // Go left, picking up treasure
        dmc.tick(Direction.LEFT);
        assertEquals(treasureCount(dmc), 1);

        // Check the mercenary is hostile
        assertTrue(helper.getState(dmc, "mercenary0").equals("HostileState"));

        // Now try bribing
        assertThrows(InvalidActionException.class, () -> dmc.interact("mercenary0"));
    }

    @Test
    @DisplayName("Test that a bribed mercenary cannot be bribed/interacted with")
    public void testBribeFriendlyMercenary() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        dmc.newGame("d_mercenaryTest_bribe", "c_mercenaryTest_basicMovement");
    
        // Position the player so they pick up the treasure
        dmc.tick(Direction.RIGHT);
        assertEquals(treasureCount(dmc), 1);

        // Check the mercenary is still hostile
        assertTrue(helper.getState(dmc, "mercenary0").equals("HostileState"));
        assertDoesNotThrow(() -> dmc.interact("mercenary0"));

        // Check mercenary is now bribed state and treasure has been removed
        assertTrue(helper.getState(dmc, "mercenary0").equals("BribedState"));
        assertEquals(treasureCount(dmc), 0);
        assertFalse(getMerc(dmc, "mercenary0").isInteractable());
    }

    
    // Hostile mercenary movement
    @Test
    @DisplayName("Test basic hostile mercenary movement")
    public void testHostileMovementBasic() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_mercenaryTest_basicMovement", "c_mercenaryTest_basicMovement");

        Double distance = getDistanceBetween(dmc, "mercenary0");

        // Check the distance is closing or equal every time
        dmc.tick(Direction.UP);
        assertTrue(getDistanceBetween(dmc, "mercenary0") <= distance);
        distance = getDistanceBetween(dmc, "mercenary0");
        dmc.tick(Direction.UP);
        assertTrue(getDistanceBetween(dmc, "mercenary0") <= distance);
        distance = getDistanceBetween(dmc, "mercenary0");
        dmc.tick(Direction.UP);
        assertTrue(getDistanceBetween(dmc, "mercenary0") <= distance);
        
    }
    

    // Friendly mercenary movement
    @Test
    @DisplayName("Test basic bribed mercenary movement")
    public void testBribedMovementBasic() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_mercenaryTest_bribeMovement", "c_mercenaryTest_bribe_radius3");

        Double distance = getDistanceBetween(dmc, "mercenary0");

        // Check the distance is closing or equal every time
        dmc.tick(Direction.RIGHT);
        assertTrue(getDistanceBetween(dmc, "mercenary0") == distance - 2);
        distance = getDistanceBetween(dmc, "mercenary0");
        dmc.tick(Direction.LEFT);
        assertTrue(getDistanceBetween(dmc, "mercenary0").equals(distance));
        distance = getDistanceBetween(dmc, "mercenary0");
        dmc.tick(Direction.LEFT);
        assertTrue(getDistanceBetween(dmc, "mercenary0").equals(distance));
        distance = getDistanceBetween(dmc, "mercenary0");

        assertTrue(helper.getState(dmc, "mercenary0").equals("HostileState"));
        assertDoesNotThrow(() -> dmc.interact("mercenary0"));
        assertTrue(helper.getState(dmc, "mercenary0").equals("BribedState"));

        // Merc and player should be right next to eachother
        dmc.tick(Direction.RIGHT);

        // Go up, check merc is where player last was
        dmc.tick(Direction.UP);
        assertTrue(helper.getEntityPosition(dmc, "mercenary0").equals(getPlayer(dmc).getLastPosition()));
        dmc.tick(Direction.RIGHT);
        assertTrue(helper.getEntityPosition(dmc, "mercenary0").equals(getPlayer(dmc).getLastPosition()));
        dmc.tick(Direction.DOWN);
        assertTrue(helper.getEntityPosition(dmc, "mercenary0").equals(getPlayer(dmc).getLastPosition()));
        dmc.tick(Direction.LEFT);
        assertTrue(helper.getEntityPosition(dmc, "mercenary0").equals(getPlayer(dmc).getLastPosition()));

    }

    // Other movement tests
    @Test
    @DisplayName("Test no path case")
    public void testNoPath() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_mercenaryTest_nopath", "c_mercenaryTest_basicMovement");
        Position mercPos = helper.getEntityPosition(dmc, "mercenary0");

        // Since there is no path to the player, the mercenary will not move
        dmc.tick(Direction.LEFT);
        assertEquals(mercPos, helper.getEntityPosition(dmc, "mercenary0"));
        dmc.tick(Direction.LEFT);
        assertEquals(mercPos, helper.getEntityPosition(dmc, "mercenary0"));
        
    }

    @Test
    @DisplayName("Path gets blocked by boulder")
    public void testMercenaryPathBlockedBoulder() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_mercenaryTest_boulderBlock", "c_mercenaryTest_basicMovement");
        Position mercPos = helper.getEntityPosition(dmc, "mercenary0");

        // Since there is still a path to the player, the mercenary will move towards them
        dmc.tick(Direction.LEFT);
        assertEquals(mercPos.translateBy(Direction.LEFT), helper.getEntityPosition(dmc, "mercenary0"));
        mercPos = helper.getEntityPosition(dmc, "mercenary0");
        // Go right, mercenary still moving
        dmc.tick(Direction.RIGHT);
        assertEquals(mercPos.translateBy(Direction.LEFT), helper.getEntityPosition(dmc, "mercenary0"));
        mercPos = helper.getEntityPosition(dmc, "mercenary0");
        // Go right again, pushing boulder
        dmc.tick(Direction.RIGHT);
        // Mercenary cannot move for subsequent ticks
        dmc.tick(Direction.UP);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.UP);
        dmc.tick(Direction.DOWN);

        assertTrue(mercPos.equals(helper.getEntityPosition(dmc, "mercenary0")));
        
    }

    @Test
    @DisplayName("Path gets blocked by door")
    public void testMercenaryPathBlockedDoor() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_mercenaryTest_doorBlock", "c_mercenaryTest_basicMovement");
        Position mercPos = helper.getEntityPosition(dmc, "mercenary0");

        // No path to player, mercenary should not move
        // Pick up a key and go left/right to run a few ticks
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.RIGHT);
        assertEquals(mercPos, helper.getEntityPosition(dmc, "mercenary0"));
        // Go right and unlock the door
        // The mercenary should be able to move on this tick, so goes left
        dmc.tick(Direction.RIGHT);
        assertEquals(mercPos.translateBy(Direction.LEFT), helper.getEntityPosition(dmc, "mercenary0"));
        mercPos = helper.getEntityPosition(dmc, "mercenary0");
        // Now go left, the merc should now be following
        dmc.tick(Direction.LEFT);
        assertEquals(mercPos.translateBy(Direction.LEFT), helper.getEntityPosition(dmc, "mercenary0"));
        mercPos = helper.getEntityPosition(dmc, "mercenary0");

        dmc.tick(Direction.LEFT);
        assertEquals(mercPos.translateBy(Direction.LEFT), helper.getEntityPosition(dmc, "mercenary0"));
        mercPos = helper.getEntityPosition(dmc, "mercenary0");

        dmc.tick(Direction.LEFT);
        assertEquals(mercPos.translateBy(Direction.LEFT), helper.getEntityPosition(dmc, "mercenary0"));
        mercPos = helper.getEntityPosition(dmc, "mercenary0");
    }

    @Test
    @DisplayName("Test invincible player case")
    public void testMovementInvinciblePlayer() throws IllegalArgumentException, InvalidActionException {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_mercenaryTest_potions", "c_mercenaryTest_basicMovement");
        Double distance = getDistanceBetween(dmc, "mercenary0");

        // Go left, the merc should follow
        dmc.tick(Direction.LEFT);
        assertTrue(getDistanceBetween(dmc, "mercenary0").equals(distance));
        distance = getDistanceBetween(dmc, "mercenary0");
        dmc.tick(Direction.LEFT);
        assertTrue(getDistanceBetween(dmc, "mercenary0").equals(distance));
        distance = getDistanceBetween(dmc, "mercenary0");
        dmc.tick("invincibility_potion0");
        // After using invincibility potion, the merc should now run from player
        dmc.tick(Direction.LEFT);
        assertTrue(getDistanceBetween(dmc, "mercenary0") > distance);
        distance = getDistanceBetween(dmc, "mercenary0");
        dmc.tick(Direction.LEFT);
        assertTrue(getDistanceBetween(dmc, "mercenary0") > distance);
        distance = getDistanceBetween(dmc, "mercenary0");
    }
    

    @Test
    @DisplayName("Test invisible player case")
    public void testMovementInvisiblePlayer() throws IllegalArgumentException, InvalidActionException {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_mercenaryTest_potions", "c_mercenaryTest_basicMovement");
        Double distance = getDistanceBetween(dmc, "mercenary0");

        // Go left, the merc should follow
        dmc.tick(Direction.LEFT);
        assertTrue(getDistanceBetween(dmc, "mercenary0").equals(distance));
        distance = getDistanceBetween(dmc, "mercenary0");
        dmc.tick(Direction.LEFT);
        assertTrue(getDistanceBetween(dmc, "mercenary0").equals(distance));
        distance = getDistanceBetween(dmc, "mercenary0");
        dmc.tick("invisibility_potion0");
        // After using invincibility potion, the merc has random movement, in this case he can randomly
        // move back and forth, so just run a few ticks before checking that he isnt keeping up
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        assertTrue(getDistanceBetween(dmc, "mercenary0") >= distance);
        
    }

    @Test
    @DisplayName("Test dijkstra works in picking one swamp path over another")
    public void testAdvancedMovementSwampTile() throws IllegalArgumentException, InvalidActionException {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_mercenaryTest_advMovement_swampPaths", "c_simple");

        // There are two swamp paths. The path upwards from the mercenary is the longer one,
        // they should take the shorter path (go down)
        Position badStart = helper.getEntityPosition(dmc, "mercenary0").translateBy(Direction.UP);
        Position goodStart = helper.getEntityPosition(dmc, "mercenary0").translateBy(Direction.DOWN);

        // Go left, the merc should choose the ideal path
        dmc.tick(Direction.LEFT);
        // Assert they have moved down
        assertFalse(badStart.equals(helper.getEntityPosition(dmc, "mercenary0")));
        assertTrue(goodStart.equals(helper.getEntityPosition(dmc, "mercenary0")));
    }

    @Test
    @DisplayName("Test dijkstra helps mercenary avoid swamp tiles")
    public void testAdvancedMovementAvoidSwampTile() throws IllegalArgumentException, InvalidActionException {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_mercenaryTest_advMovement_avoidSwamp", "c_simple");

        // Ideal path past the swamp tiles
        List<Position> idealPath = new ArrayList<Position>();
        idealPath.add(new Position(6, 6));
        idealPath.add(new Position(5, 6));
        idealPath.add(new Position(4, 6));
        idealPath.add(new Position(4, 5));
        idealPath.add(new Position(4, 4));
        idealPath.add(new Position(3, 4));

        // There is only one ideal path, so check the mercenary is following it
        // every time a tick passes
        for (Position idealPos : idealPath) {
            dmc.tick(Direction.LEFT);
            assertTrue(idealPos.equals(helper.getEntityPosition(dmc, "mercenary0")));
        }
    }

    @Test
    @DisplayName("Test dijkstra allows mercenary to go around a block of swamp tiles, even if it means cardinally longer route")
    public void testAdvancedMovementAvoidSwampBlock() throws IllegalArgumentException, InvalidActionException {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_mercenaryTest_advMovement_avoidSwampBlock", "c_simple");

        // Get distance between player and mercenary
        double distance = getDistanceBetween(dmc, "mercenary0");
        Position mercPos = helper.getEntityPosition(dmc, "mercenary0");

        // Move left into the swamp tiles
        // While the player is inside the swamp tiles, the mercenary should also just keep moving left
        // This is because there is no cheaper way to reach the swamp tile
        dmc.tick(Direction.LEFT);
        assertTrue(getDistanceBetween(dmc, "mercenary0") == distance);
        assertTrue(helper.getEntityPosition(dmc, "mercenary0").equals(mercPos.translateBy(Direction.LEFT)));

        mercPos = helper.getEntityPosition(dmc, "mercenary0");
        dmc.tick(Direction.LEFT);
        assertTrue(getDistanceBetween(dmc, "mercenary0") == distance);
        assertTrue(helper.getEntityPosition(dmc, "mercenary0").equals(mercPos.translateBy(Direction.LEFT)));
        
        dmc.tick(Direction.LEFT);
        // Now the player is out of the swamp, there is a 'cheaper' way to get to him (accounting for swamp factor)
        // So, check that our mercenary takes an alternative path, even if it does not decrease distance
        assertTrue(getDistanceBetween(dmc, "mercenary0") >= distance);

        dmc.tick(Direction.LEFT);
        assertTrue(getDistanceBetween(dmc, "mercenary0") >= distance);
        dmc.tick(Direction.LEFT);
        assertTrue(getDistanceBetween(dmc, "mercenary0") >= distance);
        dmc.tick(Direction.LEFT);
        assertTrue(getDistanceBetween(dmc, "mercenary0") >= distance);
    }

    /* THIS IS PASSING LOCALLY BUT NOT ON THE GITLAB RUNNER
    @Test
    @DisplayName("Test dijkstra helps mercenary find ideal path through swamp block")
    public void testAdvancedMovementIdealPathSwampBlock() throws IllegalArgumentException, InvalidActionException {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_mercenaryTest_advMovement_idealPathSwampBlock", "c_simple");

        // Ideal path through the swamp tiles
        List<Position> idealPath = new ArrayList<Position>();
        idealPath.add(new Position(6, 5));
        idealPath.add(new Position(6, 6));
        idealPath.add(new Position(6, 7));
        idealPath.add(new Position(5, 7));
        idealPath.add(new Position(4, 7));
        idealPath.add(new Position(3, 7));
        idealPath.add(new Position(3, 6));
        idealPath.add(new Position(3, 5));
        idealPath.add(new Position(2, 5));

        // Since the swamps on the ideal path take 1 tick to leave, account for that by doubling num ticks
        // Subtract 2 because first 2 ticks are unrestricted
        int numTicks = idealPath.size()*2 - 3;
        List<Position> mercenaryPath = new ArrayList<Position>();
        
        // Build the mercenary path
        Position mercPos = helper.getEntityPosition(dmc, "mercenary0");
        for (int i = 0; i < numTicks; i++) {
            dmc.tick(Direction.LEFT);
            if (!helper.getEntityPosition(dmc, "mercenary0").equals(mercPos)) {
                mercPos = helper.getEntityPosition(dmc, "mercenary0");
                mercenaryPath.add(mercPos);
            }
        }

        for (int i = 0; i < idealPath.size(); i++) {
            assertTrue(idealPath.get(i).equals(mercenaryPath.get(i)));
        }

    }
    */
     
    
    @Test
    @DisplayName("Test dijkstra helps mercenary account for portals")
    public void testAdvancedMovementPortalBasic() throws IllegalArgumentException, InvalidActionException {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_mercenaryTest_advMovement_portal_basic", "c_simple");

        Position mercPos = helper.getEntityPosition(dmc, "mercenary0");
        dmc.tick(Direction.DOWN);

        assertTrue(helper.getEntityPosition(dmc, "mercenary0").equals(mercPos.translateBy(Direction.UP)));
        dmc.tick(Direction.DOWN);

        assertTrue(helper.getEntityPosition(dmc, "mercenary0").equals(new Position(3, 3)));
    }

    @Test
    @DisplayName("Test dijkstra ignores portals that are blocked")
    public void testAdvancedMovementBadPortal() throws IllegalArgumentException, InvalidActionException {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_mercenaryTest_advMovement_portal_badPortal", "c_simple");

        Position mercPos = helper.getEntityPosition(dmc, "mercenary0");
        dmc.tick(Direction.DOWN);
        assertTrue(helper.getEntityPosition(dmc, "mercenary0").equals(mercPos.translateBy(Direction.DOWN)));
        dmc.tick(Direction.DOWN);
    }

    @Test
    @DisplayName("Test dijkstra utilises portal chains")
    public void testAdvancedMovementPortalChain() throws IllegalArgumentException, InvalidActionException {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_mercenaryTest_advMovement_portal_recursive", "c_simple");

        // The fastest path is going through the portal chain
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        // Check the merc has done so
        Position mercPos = helper.getEntityPosition(dmc, "mercenary0");
        Position RedPortalExit = helper.getEntityPosition(dmc, "portal3").translateBy(Direction.RIGHT);
        assertTrue(mercPos.equals(RedPortalExit));
    }
        

    /* 
    * HELPER FUNCTIONS
    */

    // Get treasure count of dungeon
    private int treasureCount(DungeonManiaController dmc) {
        Dungeon currDungeon = dmc.currentDungeon;
        return currDungeon.getPlayer().getTreasureCount();
    }

    // Get distance between player and given merc
    private Double getDistanceBetween(DungeonManiaController dmc,
                                        String mercId) {
        DungeonResponse res = dmc.getDungeonResponseModel();
        Dungeon d = dmc.currentDungeon;
        Position player = d.getPlayerPosition();

        Position merc =  res.getEntities().stream()
                                .filter(s -> s.getId().equals(mercId))
                                .findFirst().get().getPosition();
        return player.getDistanceBetween(merc);
    }

    // Get player instance
    private Player getPlayer(DungeonManiaController dmc) {
        Dungeon d = dmc.currentDungeon;
        return d.getPlayer();
    }

    // Get merc instance
    private MercenaryEntity getMerc(DungeonManiaController dmc, String mercId) {
        Dungeon currDungeon = dmc.currentDungeon;
        Entity entity = currDungeon.getEntitiesOfType("mercenary").stream()
                                                                .filter(m -> m.getId().equals(mercId))
                                                                .findFirst().get();

        return (MercenaryEntity) entity;
    }

}