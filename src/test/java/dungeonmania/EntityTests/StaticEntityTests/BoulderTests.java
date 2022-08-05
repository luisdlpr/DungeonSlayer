package dungeonmania.EntityTests.StaticEntityTests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.Dungeon;
import dungeonmania.DungeonManiaController;
import dungeonmania.Helper;
import dungeonmania.Entities.Entity;
import dungeonmania.Entities.StaticEntities.BoulderEntity;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class BoulderTests {

    Helper helper = new Helper();
    
    // Test boulder creation
    @Test
    @DisplayName("test constructor for boulder operates correctly")
    public void testUnitBoulderCreation() {
        // constructor takes in dungeon filename, config filename, and int representing how many dungeons have been created (passed in from controller)
        BoulderEntity boulder = new BoulderEntity(helper.createEntityJSON("boulder","1", "0"), 0);
        
        Position expectedPosition = new Position(1, 0);

        // check id is equal
        assertTrue(expectedPosition.equals(boulder.getEntityResponse().getPosition()));
        assertTrue("boulder0".equals(boulder.getEntityResponse().getId()));
    }

    @Test
    @DisplayName("WhiteBox test to check boulders are created correctly in dungeonController")
    public void testControllerBoulderCreation() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_boulderTest_basicCollision", "c_boulderTest_playerCollision");
        Dungeon dungeon = dmc.currentDungeon;

        List<Entity> entities = dungeon.getEntities();

        BoulderEntity boulder = new BoulderEntity(helper.createEntityJSON("boulder","3", "2"), 0);

        assertTrue(helper.assertEntityExistsInList(entities, boulder));

        for (Entity e : entities) {
            if (e.getType().equals("boulder")){
                assertTrue(e.getClass().getSimpleName().equals("BoulderEntity"));
            }
        }
    }


    // Test boulder movement
    @Test
    @DisplayName("Test player can push the boulder")
    public void testPlayerMoveBoulder() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        dmc.newGame("d_boulderTest_basicCollision", "c_boulderTest_playerCollision");
        dmc.tick(Direction.RIGHT);
        // The boulder is to the right of the player, if the player moves into, it should shift
        // the boulder to the right, provided there is no wall blocking boulder movement

        checkBoulderPlayerPos(dmc, new Position(3, 2), new Position(4, 2), "boulder0");

        // Try moving the boulder in another direction (UP)
        dmc.tick(Direction.UP);   // Player at: (3,1)
        dmc.tick(Direction.RIGHT);  // Player at: (4,1)
        dmc.tick(Direction.DOWN);  // Player at: (4,2)

        checkBoulderPlayerPos(dmc, new Position(4, 2), new Position(4, 3), "boulder0");
    }

    // Test player cannot push boulder if wall blocking
    @Test
    @DisplayName("Test that player cannot push a boulder if a wall is blocking")
    public void testPlayerMoveBoulderIntoWall() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        dmc.newGame("d_boulderTest_basicCollision", "c_boulderTest_playerCollision");
        dmc.tick(Direction.RIGHT); 

        // Check player position at (3,2) and boulder at (4,2)
        checkBoulderPlayerPos(dmc, new Position(3, 2), new Position(4, 2), "boulder0");

        // Try and push the boulder to the right again, where a wall is
        dmc.tick(Direction.RIGHT);

        // Check that the boulder and player has not moved
        checkBoulderPlayerPos(dmc, new Position(3, 2), new Position(4, 2), "boulder0");
    }

    // Test player cannot push boulder if door is blocking
    @Test
    @DisplayName("Test that player cannot push a boulder if a door is blocking")
    public void testPlayerMoveBoulderIntoDoor() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        dmc.newGame("d_boulderTest_doorCollision", "c_boulderTest_playerCollision");
        dmc.tick(Direction.RIGHT); 

        // Check player position at (3,2) and boulder at (4,2)
        checkBoulderPlayerPos(dmc, new Position(3, 2), new Position(4, 2), "boulder0");

        // Try and push the boulder to the right again, where a door is
        dmc.tick(Direction.RIGHT);

        // Check that the boulder and player has not moved
        checkBoulderPlayerPos(dmc, new Position(3, 2), new Position(4, 2), "boulder0");
    }


    // Test that the player cannot push more than 1 boulder at once
    @Test
    @DisplayName("Test that player cannot push more than one boulder at once")
    public void testPlayerMoveMultipleBoulders() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        dmc.newGame("d_boulderTest_basicCollision", "c_boulderTest_playerCollision");
        dmc.tick(Direction.RIGHT); 

        // Check player position at (3,2) and boulder at (4,2)
        checkBoulderPlayerPos(dmc, new Position(3, 2), new Position(4, 2), "boulder0");

        // Try and push the boulder to the right again, where another boulder is
        dmc.tick(Direction.RIGHT);

        // Check that the original boulder and player has not moved
        checkBoulderPlayerPos(dmc, new Position(3, 2), new Position(4, 2), "boulder0");

        // Check the second boulder has not moved either
        checkBoulderPlayerPos(dmc, new Position(3, 2), new Position(4, 2), "boulder1");

    }

    private void checkBoulderPlayerPos(DungeonManiaController dmc,
                                        Position player, Position boulder, 
                                        String boulderId) {
        DungeonResponse res = dmc.getDungeonResponseModel();
        Boolean foundPlayer = false;
        for (EntityResponse e : res.getEntities()) {
            if (e.getType().equals("player")) {
                foundPlayer = true;
                // Assert the player is where the boulder was
                assertTrue(e.getPosition().equals(player));
            } else if (e.getId().equals(boulderId)) {
                // Assert the boulder position
                assertTrue(e.getPosition().equals(boulder));
            }
        }
        assertTrue(foundPlayer);
    }


// Win condition of placing all boulders on switched would need to tested 

}
