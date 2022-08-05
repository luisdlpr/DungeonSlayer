package dungeonmania.PotionTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.Dungeon;
import dungeonmania.DungeonManiaController;
import dungeonmania.Entities.Player;
import dungeonmania.Entities.CollectableEntities.CollectableEntity;
import dungeonmania.Entities.CollectableEntities.Potions.InvisibilityPotion;
import dungeonmania.Entities.CollectableEntities.Potions.Potion;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class InvisibilityPotionTests {
    @Test
    @DisplayName("Whitebox test switching states directly on player")
    public void testPlayerStateSwitchInvis() {
        // mock player
        Player player = new Player(new Position(0, 0), 0, 1);

        // go invisible and check state
        player.getState().goInvisible();
        assertTrue(player.getState().getClass().getSimpleName().equals("InvisibleState"));

        // go normal and check state
        player.getState().goNormal();
        assertTrue(player.getState().getClass().getSimpleName().equals("NormalState"));
    }

    @Test
    @DisplayName("test switching states and switchback with controller")
    public void testSystemPlayerStateSwitchInvis() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_movementTest_testMovementDown", "c_movementTest_testMovementDown");

        Player player = dmc.currentDungeon.getPlayer();

        // go invisible and check state
        player.getState().goInvisible();
        // artificial - should be done on potion.use(Player)
        player.setPotionTimer(2);
        assertTrue(player.getState().getClass().getSimpleName().equals("InvisibleState"));

        // wait 3 ticks and should be normal
        for (int i = 0; i < 2; i ++) {
            dmc.tick(Direction.UP);
            assertTrue(player.getState().getClass().getSimpleName().equals("InvisibleState"));
        }

        // timer has run out - should return to normalState
        dmc.tick(Direction.UP);
        assertTrue(player.getState().getClass().getSimpleName().equals("NormalState"));
    }

    @Test
    @DisplayName("test getItemResponse")
    public void testGetItemResponseInvis() {
        // make a mock potion input
        HashMap<String, String> mockPotionData = new HashMap<String, String>();
        mockPotionData.put("x", "2");
        mockPotionData.put("y", "1");
        mockPotionData.put("type", "invisibility_potion");

        JSONObject mockPotionJSON = new JSONObject(mockPotionData);
        
        // make a new potion
        InvisibilityPotion invisPotion = new InvisibilityPotion(mockPotionJSON, 0, 2);

        // make the expected response
        ItemResponse expected = new ItemResponse("invisibility_potion0", "invisibility_potion");

        assertEquals(expected.getId(), invisPotion.getItemResponse().getId());
        assertEquals(expected.getType(), invisPotion.getItemResponse().getType());
    }

    @Test
    @DisplayName("test added to inventory on pickup")
    public void testPickUp() {
        // create new dungeon
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_potionTest_pickup", "c_movementTest_testMovementDown");

        // create mock entities to compare to (expected)
        EntityResponse mockItemOnMap = new EntityResponse(
            "invisibility_potion0", 
            "invisibility_potion",
            new Position(1, 1), 
            false
        );

        ItemResponse mockItem = new ItemResponse("invisibility_potion0", "invisibility_potion");

        // assert on map and not in inventory
        assertTrue(AssertContains(dmc.getDungeonResponseModel().getEntities(), mockItemOnMap));
        assertFalse(AssertContains(dmc.getDungeonResponseModel().getInventory(), mockItem));

        // on pickup condition assert goes into inventory and deleted from map
        dmc.tick(Direction.RIGHT);
        assertFalse(AssertContains(dmc.getDungeonResponseModel().getEntities(), mockItemOnMap));
        assertTrue(AssertContains(dmc.getDungeonResponseModel().getInventory(), mockItem));
    }


    // method to check item is in inventory
    private boolean AssertContains(List<ItemResponse> inventory, ItemResponse item) {
        for (ItemResponse i : inventory) {
            if (i.getType().equals(item.getType()) && i.getId().equals(item.getId())) {
                return true;
            }
        }
        return false;
    }

    // method to check entity is on map
    private boolean AssertContains(List<EntityResponse> inventory, EntityResponse item) {
        for (EntityResponse i : inventory) {
            if (i.equals(item)) {
                return true;
            }
        }
        return false;
    }

    @Test
    @DisplayName("test apply from potion method")
    public void testUsePlayer() {
        // have player pick up potion
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_potionTest_pickup", "c_movementTest_testMovementDown");

        ItemResponse mockItem = new ItemResponse("invisibility_potion0", "invisibility_potion");

        assertFalse(AssertContains(dmc.getDungeonResponseModel().getInventory(), mockItem));

        dmc.tick(Direction.RIGHT);

        assertTrue(AssertContains(dmc.getDungeonResponseModel().getInventory(), mockItem));

        // use on player from potion
        Dungeon dungeon = dmc.currentDungeon;
        Player player = dungeon.getPlayer();
        List<CollectableEntity> inventory = player.getInventory();
        
        // get potion (heavily whitebox)
        Potion potion = null;
        for (CollectableEntity e : inventory) {
            if (e.getItemResponse().getId().equals(mockItem.getId())) {
                potion = (Potion) e;
            }
        }

        potion.use(player);

        // check added to statusEffects queue
        assertTrue(player.getStatusEffects().size() == 1);

        // check it applies on next tick
        dmc.tick(Direction.UP);
        assertTrue(player.getState().getName().equals("InvisibleState"));
    }

    @Test
    @DisplayName("test apply from dmc")
    public void testUsePotionDMC() {
        // have player pick up potion
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_potionTest_pickup", "c_movementTest_testMovementDown");

        ItemResponse mockItem = new ItemResponse("invisibility_potion0", "invisibility_potion");

        assertFalse(AssertContains(dmc.getDungeonResponseModel().getInventory(), mockItem));

        dmc.tick(Direction.RIGHT);

        assertTrue(AssertContains(dmc.getDungeonResponseModel().getInventory(), mockItem));

        // use item by id
        try {
            dmc.tick("invisibility_potion0");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvalidActionException e) {
            e.printStackTrace();
        }
        
        // should apply to player on use tick
        Player player = dmc.currentDungeon.getPlayer();
        assertTrue(player.getState().getName().equals("InvisibleState"));
    }

    @Test
    @DisplayName("test apply from invalid id string in dmc")
    public void testUseInvalidPotionDMC() {
        // have player pick up potion
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_potionTest_pickup", "c_movementTest_testMovementDown");

        ItemResponse mockItem = new ItemResponse("invisibility_potion0", "invisibility_potion");

        assertFalse(AssertContains(dmc.getDungeonResponseModel().getInventory(), mockItem));

        dmc.tick(Direction.RIGHT);

        assertTrue(AssertContains(dmc.getDungeonResponseModel().getInventory(), mockItem));

        Exception exception = assertThrows(InvalidActionException.class, () -> {
            dmc.tick("invalid_id");
        });

        assertTrue(exception.getMessage().contains("item not found"));
    }

    @Test
    @DisplayName("test queued potions")
    public void testQueuedPotionsDMC() {
        // have player pick up potions
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_potionTest_pickup", "c_PotionTest_queuePotions");

        ItemResponse mockItem1 = new ItemResponse("invisibility_potion0", "invisibility_potion");
        ItemResponse mockItem2 = new ItemResponse("invisibility_potion1", "invisibility_potion");

        // assert not in inventory
        assertFalse(AssertContains(dmc.getDungeonResponseModel().getInventory(), mockItem1));
        assertFalse(AssertContains(dmc.getDungeonResponseModel().getInventory(), mockItem1));

        // pick up
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);

        // assert now in inventory
        assertTrue(AssertContains(dmc.getDungeonResponseModel().getInventory(), mockItem2));
        assertTrue(AssertContains(dmc.getDungeonResponseModel().getInventory(), mockItem2));

        Player player = dmc.currentDungeon.getPlayer();

        // use both potions (duration of 5)
        try {
            // tick 0
            dmc.tick("invisibility_potion0");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvalidActionException e) {
            e.printStackTrace();
        }
        assertTrue(player.getState().getName().equals("InvisibleState"));

        try {
            // tick 1
            dmc.tick("invisibility_potion1");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvalidActionException e) {
            e.printStackTrace();
        }
        assertTrue(player.getState().getName().equals("InvisibleState"));

        // go to tick 10 where state should return to normal
        for (int i = 0; i < 8; i ++) {
            // tick 2-9
            dmc.tick(Direction.LEFT);
            assertTrue(player.getState().getName().equals("InvisibleState"));
        }
        
        // tick 10 - should go normal
        dmc.tick(Direction.LEFT);
        assertTrue(player.getState().getName().equals("NormalState"));

    }
}
