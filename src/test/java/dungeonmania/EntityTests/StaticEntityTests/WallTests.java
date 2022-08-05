package dungeonmania.EntityTests.StaticEntityTests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.Dungeon;
import dungeonmania.DungeonManiaController;
import dungeonmania.Helper;
import dungeonmania.Entities.Entity;
import dungeonmania.Entities.StaticEntities.WallEntity;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class WallTests {
    Helper helper = new Helper();

    @Test
    @DisplayName("test constructor for wall operates correctly")
    public void testUnitWallCreation() {
        // constructor takes in dungeon filename, config filename, and int representing how many dungeons have been created (passed in from controller)
        WallEntity wall = new WallEntity(helper.createEntityJSON("wall","1", "0"), 0);
        
        Position expectedPosition = new Position(1, 0);
        
        // check id is equal
        assertTrue(expectedPosition.equals(wall.getEntityResponse().getPosition()));
        assertTrue("wall0".equals(wall.getEntityResponse().getId()));
    }

    @Test
    @DisplayName("WhiteBox test to check walls are created correctly in dungeonController")
    public void testControllerWallCreation() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_battleTest_basicMercenary", "c_movementTest_testMovementDown");
        Dungeon dungeon = dmc.currentDungeon;

        List<Entity> entities = dungeon.getEntities();

        WallEntity wall = new WallEntity(helper.createEntityJSON("wall","1", "0"), 0);

        assertTrue(helper.assertEntityExistsInList(entities, wall));

        for (Entity e : entities) {
            if (e.getType().equals("wall")){
                assertTrue(e.getClass().getSimpleName().equals("WallEntity"));
            }
        }
    }
    
    @Test
    @DisplayName("Test wall does not allow collision with player (moves player back on hit)")
    public void testPlayerMoveIntoWall() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        dmc.newGame("d_battleTest_basicMercenary", "c_movementTest_testMovementDown");
        dmc.tick(Direction.UP);
        dmc.tick(Direction.RIGHT);

        DungeonResponse res = dmc.getDungeonResponseModel();
        Boolean foundPlayer = false;
        for (EntityResponse e : res.getEntities()) {
            if (e.getType().equals("player")) {
                foundPlayer = true;
                assertTrue(e.getPosition().equals(new Position(0, 0)));
            }
        }
        assertTrue(foundPlayer);

        // try again and see what happens too
        dmc.tick(Direction.RIGHT);

        res = dmc.getDungeonResponseModel();
        foundPlayer = false;
        for (EntityResponse e : res.getEntities()) {
            if (e.getType().equals("player")) {
                foundPlayer = true;
                assertTrue(e.getPosition().equals(new Position(0, 0)));
            }
        }
        assertTrue(foundPlayer);
    }
}
