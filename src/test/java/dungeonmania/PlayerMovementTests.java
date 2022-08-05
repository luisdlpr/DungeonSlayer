package dungeonmania;

// import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertFalse;
// import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

// import static dungeonmania.TestUtils.getPlayer;
// import static dungeonmania.TestUtils.getEntities;
// import static dungeonmania.TestUtils.getInventory;
// import static dungeonmania.TestUtils.getGoals;
// import static dungeonmania.TestUtils.countEntityOfType;
// import static dungeonmania.TestUtils.getValueFromConfigFile;

// import java.util.ArrayList;
// import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.Entities.Player;
// import dungeonmania.response.models.BattleResponse;
// import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;
// import dungeonmania.response.models.EntityResponse;
// import dungeonmania.response.models.RoundResponse;
// import dungeonmania.util.Direction;
// import dungeonmania.util.Position;
import dungeonmania.util.Position;


public class PlayerMovementTests {
    @Test
    @DisplayName("Test movement in the up direction (called directly from class)")
    public void testPlayerMoveUp() {
        Player player = new Player(new Position(0, 0), 0, 0);

        player.move(Direction.UP);

        assertEquals(new Position(0, -1), player.getCurrentPosition());
        assertEquals(new Position(0, 0), player.getLastPosition());
    }

    @Test
    @DisplayName("Test movement in the down direction (called directly from class)")
    public void testPlayerMoveDown() {
        Player player = new Player(new Position(0, 0), 0, 0);

        player.move(Direction.DOWN);

        assertEquals(new Position(0, 1), player.getCurrentPosition());
        assertEquals(new Position(0, 0), player.getLastPosition());
    }
    
    @Test
    @DisplayName("Test movement in the left direction (called directly from class)")
    public void testPlayerMoveLeft() {
        Player player = new Player(new Position(0, 0), 0, 0);

        player.move(Direction.LEFT);

        assertEquals(new Position(-1, 0), player.getCurrentPosition());
        assertEquals(new Position(0, 0), player.getLastPosition());
    }

    @Test
    @DisplayName("Test movement in the right direction (called directly from class)")
    public void testPlayerMoveRight() {
        Player player = new Player(new Position(0, 0), 0, 0);

        player.move(Direction.RIGHT);

        assertEquals(new Position(1, 0), player.getCurrentPosition());
        assertEquals(new Position(0, 0), player.getLastPosition());
    }

    @Test
    @DisplayName("Test movement into a simulated collision (called directly from class)")
    public void testPlayerMoveAndGoBack() {
        Player player = new Player(new Position(0, 0), 0, 0);

        player.move(Direction.DOWN);

        assertEquals(new Position(0, 1), player.getCurrentPosition());
        assertEquals(new Position(0, 0), player.getLastPosition());

        player.goBack();

        assertEquals(new Position(0, 0), player.getCurrentPosition());
        assertEquals(new Position(0, 0), player.getLastPosition());
    }

    @Test
    @DisplayName("Test movement integration with dungeonController")
    public void testDungeonControllerMovePlayer() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_movementTest_testMovementDown", "c_movementTest_testMovementDown");
        
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.LEFT);

        List<EntityResponse> entities = dmc.getDungeonResponseModel().getEntities();
        for (EntityResponse eResponse : entities) {
            if (eResponse.getType().equals("player")) {
                assertTrue(eResponse.getPosition().equals(new Position(0, 2)));
            }
        }
    }
}
