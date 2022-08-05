package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertTrue;

// import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// import dungeonmania.Entities.Entity;
import dungeonmania.Entities.Player;
// import dungeonmania.Entities.StaticEntities.PlaceholderEntity;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Position;


public class PlayerTests {
    // private Boolean assertEntityExistsInList(List<Entity> actual, Entity expected) {
    //     for (Entity e : actual) {
    //         if (e.getEntityResponse().equals(expected.getEntityResponse())) {
    //             return true;
    //         }
    //     }
    //     return false;
    // }

    // needs to be refactored with CollectableEntity class

    // @Test
    // @DisplayName("Stores mock entities in inventory and returns it as a List")
    // public void testPlayerInventory() {
    //     Player player = new Player(new Position(0, 0), 0, 0);

    //     Position woodPosition = new Position(0, 0);
    //     Entity wood = new PlaceholderEntity("wood0", "wood", woodPosition, true);

    //     Position arrowPosition = new Position(0, 1);
    //     Entity arrow = new PlaceholderEntity("arrow0", "arrow", arrowPosition, true);

    //     player.addToInventory(wood);
    //     player.addToInventory(arrow);

    //     // check id is equal
    //     assertTrue(assertEntityExistsInList(player.getInventory(), wood));
    //     assertTrue(assertEntityExistsInList(player.getInventory(), arrow));
    // }
    
    @Test
    @DisplayName("Instantiated player on newGame has correct AD and health values")
    public void testPlayerCorrectBattleStats() {
        Dungeon dungeon = new Dungeon("d_movementTest_testMovementDown", "c_movementTest_testMovementDown", 0);
        Player player = dungeon.getPlayer();
        assertEquals(10, player.getAttackDamage());
        assertEquals(10, player.getHealth());
    }

    @Test
    @DisplayName("Instantiated player on newGame has correct AD and health values")
    public void testPlayerCorrectStartPosition() {
        Dungeon dungeon = new Dungeon("d_movementTest_testMovementDown", "c_movementTest_testMovementDown", 0);
        Player player = dungeon.getPlayer();
        Position startingPosition = new Position(1, 1);

        assertEquals(startingPosition, player.getCurrentPosition());
    }
    
    @Test
    @DisplayName("Instantiated player on newGame is in normalState")
    public void testPlayerNormalStateDefault() {
        Dungeon dungeon = new Dungeon("d_movementTest_testMovementDown", "c_movementTest_testMovementDown", 0);
        Player player = dungeon.getPlayer();

        assertEquals("NormalState", player.getState().getName());
    }
    

    @Test
    @DisplayName("Test player can generate an entityResponse")
    public void testPlayerEntityResponse() {
        Player player = new Player(new Position(0, 0), 0, 0);
        Position position = new Position(0, 0);
        EntityResponse expectedResponse = new EntityResponse("player", "player", position, false);

        assertEquals(expectedResponse, player.getEntityResponse());
    }
}
