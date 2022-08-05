package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.Buildables.Shield;
import dungeonmania.Entities.Player;
import dungeonmania.Entities.CollectableEntities.ArrowEntity;
import dungeonmania.Entities.CollectableEntities.KeyEntity;
import dungeonmania.Entities.CollectableEntities.TreasureEntity;
import dungeonmania.Entities.CollectableEntities.Potions.InvincibilityPotion;
import dungeonmania.Entities.MoveableEntities.MercenaryEntity;
import dungeonmania.Entities.StaticEntities.DoorEntity;
import dungeonmania.Entities.StaticEntities.FloorSwitchEntity;
import dungeonmania.Entities.StaticEntities.PortalEntity;
import dungeonmania.Entities.StaticEntities.WallEntity;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class PersistenceTests {

    @Test
    @DisplayName("Player data to JSON")
    public void testPlayerToJSON() {
        // create a mock player
        Player player = new Player(new Position(2, 1), 2.0, 10.0);
        
        // set built items to 2
        player.increaseNumberBuilt();
        player.increaseNumberBuilt();

        // change current state + add mock potion timer and current potion
        JSONObject potionData = new JSONObject();
        potionData.put("type", "invincibility_potion");
        potionData.put("x", 0);
        potionData.put("y", 1);
        InvincibilityPotion potion = new InvincibilityPotion(potionData, 0, 3);

        JSONObject potionData2 = new JSONObject();
        potionData2.put("type", "invincibility_potion");
        potionData2.put("x", 1);
        potionData2.put("y", 1);
        InvincibilityPotion potion2 = new InvincibilityPotion(potionData2, 1, 3);

        player.addStatusEffect(potion);
        player.addStatusEffect(potion2);
        player.updateStatusEffect();

        // add fake last position (1, 1)
        player.setLastPosition(new Position(1, 1));

        JSONObject result = SaveUtility.playerToJSON(player);

        JSONObject expected = new JSONObject();
        expected.put("health", 10.0);
        expected.put("attack", 2.0);
        expected.put("defense", 0);
        expected.put("curr_position_x", 2);
        expected.put("curr_position_y", 1);
        expected.put("last_position_x", 1);
        expected.put("last_position_y", 1);
        expected.put("player_state", "InvincibleState");
        JSONArray statusEffects = new JSONArray();

        JSONObject potionData2Object = new JSONObject();
        potionData2Object.put("type", "invincibility_potion");
        potionData2Object.put("id", "invincibility_potion1");
        potionData2Object.put("x", 1);
        potionData2Object.put("y", 1);
        statusEffects.put(potionData2Object);

        expected.put("status_effects", statusEffects);

        JSONObject currentPotion = new JSONObject();
        currentPotion.put("type", "invincibility_potion");
        currentPotion.put("id", "invincibility_potion0");
        currentPotion.put("x", 0);
        currentPotion.put("y", 1);

        expected.put("current_potion", currentPotion);
        expected.put("potion_timer", 2);

        expected.put("item_built_count", 2);
        assertEquals(expected.toString(), result.toString());

    }

    @Test
    @DisplayName("Entity data to JSON")
    public void testEntityToJSON() {
        Helper helper = new Helper();

        WallEntity wall = new WallEntity(helper.createEntityJSON("wall", "0", "0"), 0);
        FloorSwitchEntity floorswitch = new FloorSwitchEntity(helper.createEntityJSON("switch", "1", "0"), 0);
        MercenaryEntity mercenary = new MercenaryEntity(helper.createEntityJSON("mercenary", "2", "0"), 0, 1, 2, 3, 1);

        JSONObject keyJSON = helper.createEntityJSON("key", "3", "0");
        keyJSON.put("key", 1);
        KeyEntity key = new KeyEntity(keyJSON, 0, 1);

        JSONObject doorJSON = helper.createEntityJSON("door", "4", "0");
        doorJSON.put("key", 1);
        DoorEntity door = new DoorEntity(doorJSON, 0, 1);

        JSONObject portalJSON = helper.createEntityJSON("portal", "5", "0");
        portalJSON.put("colour", "BLUE");
        PortalEntity portal = new PortalEntity(portalJSON, 0, "BLUE");

        // type
        assertEquals("wall", SaveUtility.entityToJSON(wall).get("type"));
        assertEquals("switch", SaveUtility.entityToJSON(floorswitch).get("type"));
        assertEquals("mercenary", SaveUtility.entityToJSON(mercenary).get("type"));
        assertEquals("key", SaveUtility.entityToJSON(key).get("type"));
        assertEquals("door", SaveUtility.entityToJSON(door).get("type"));
        assertEquals("portal", SaveUtility.entityToJSON(portal).get("type"));

        // position
        assertEquals(0, SaveUtility.entityToJSON(wall).get("x"));
        assertEquals(1, SaveUtility.entityToJSON(floorswitch).get("x"));
        assertEquals(2, SaveUtility.entityToJSON(mercenary).get("x"));
        assertEquals(3, SaveUtility.entityToJSON(key).get("x"));
        assertEquals(4, SaveUtility.entityToJSON(door).get("x"));
        assertEquals(5, SaveUtility.entityToJSON(portal).get("x"));

        assertEquals(0, SaveUtility.entityToJSON(wall).get("y"));
        assertEquals(0, SaveUtility.entityToJSON(floorswitch).get("y"));
        assertEquals(0, SaveUtility.entityToJSON(mercenary).get("y"));
        assertEquals(0, SaveUtility.entityToJSON(key).get("y"));
        assertEquals(0, SaveUtility.entityToJSON(door).get("y"));
        assertEquals(0, SaveUtility.entityToJSON(portal).get("y"));

        // for switch -> active or not?
        assertEquals("InactiveState", SaveUtility.entityToJSON(floorswitch).get("state"));

        // for moveable entity -> health and attack
        assertEquals(1.0, SaveUtility.entityToJSON(mercenary).get("health"));
        assertEquals(2.0, SaveUtility.entityToJSON(mercenary).get("attack"));

        // for mercenary type -> bribed or not?
        assertEquals("HostileState", SaveUtility.entityToJSON(mercenary).get("state"));

        // for key and door -> keyId
        assertEquals(1, SaveUtility.entityToJSON(key).get("keyId"));
        assertEquals(1, SaveUtility.entityToJSON(door).get("keyId"));

        // for portal -> color
        assertEquals("BLUE", SaveUtility.entityToJSON(portal).get("colour"));
    }

    @Test
    @DisplayName("Weapon to JSON")
    public void testWeaponToJSON() {
        Shield shield = new Shield(10, 11);

        JSONObject expected = new JSONObject();
        expected.put("type", "shield");
        expected.put("id", ""); // difficult since this is handled by build but should print correctly, will test with other tests
        expected.put("attack", 0.0);
        expected.put("durability", 10.0);
        expected.put("defence", 11.0);

        assertEquals(expected.toString(), SaveUtility.weaponToJSON(shield).toString());
    }

    @Test
    @DisplayName("Inventory to JSON")
    public void testInventoryToJSON() {
        // create a mock player
        Player player = new Player(new Position(2, 1), 2.0, 10.0);
        Helper helper = new Helper();

        player.addToInventory(new ArrowEntity(helper.createEntityJSON("arrow", "2", "2"), 0));
        player.addToInventory(new TreasureEntity(helper.createEntityJSON("treasure", "2", "2"), 0));
        player.addToInventory(new TreasureEntity(helper.createEntityJSON("treasure", "2", "2"), 1));
        player.buildItem(new Shield(10, 11));
        
        ArrayList<String> expected = new ArrayList<String>();
        expected.add("arrow0");
        expected.add("treasure0");
        expected.add("treasure1");

        JSONObject inventory = SaveUtility.inventoryToJSON(player);
        for (int i = 0; i < inventory.getJSONArray("collectables").length(); i ++) {
            JSONObject object = inventory.getJSONArray("collectables").getJSONObject(i);
            assertEquals(expected.get(i), object.getString("id"));
        }

        assertEquals(inventory.getJSONArray("buildables").getJSONObject(0).getString("type"), "shield");
    }

    // @Test
    // @DisplayName("Battles to JSON")
    // public void testBattleToJSON() {
    //     // create a mock player
    //     Player player = new Player(new Position(2, 1), 2.0, 10.0);
    //     Helper helper = new Helper();

    //     player.addToInventory(new ArrowEntity(helper.createEntityJSON("arrow", "2", "2"), 0));
    //     player.addToInventory(new TreasureEntity(helper.createEntityJSON("treasure", "2", "2"), 0));
    //     player.addToInventory(new TreasureEntity(helper.createEntityJSON("treasure", "2", "2"), 1));
    //     player.buildItem(new Shield(10, 11));
        
    //     ArrayList<String> expected = new ArrayList<String>();
    //     expected.add("arrow0");
    //     expected.add("treasure0");
    //     expected.add("treasure1");

    //     JSONObject inventory = SaveUtility.inventoryToJSON(player);
    //     for (int i = 0; i < inventory.getJSONArray("collectables").length(); i ++) {
    //         JSONObject object = inventory.getJSONArray("collectables").getJSONObject(i);
    //         assertEquals(expected.get(i), object.getString("id"));
    //     }

    //     assertEquals(inventory.getJSONArray("buildables").getJSONObject(0).getString("type"), "shield");
    // }

    @Test
    @DisplayName("Create JSON from dungeon in play correctly")
    public void testGenerateSaveJSON() {
        // treasurecount CONFIRM WHAT IS ADDED
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_buildBowTest", "c_simple");
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);

        JSONObject saveState = SaveUtility.saveGameToJSON(dmc.currentDungeon);

        JSONObject expected = new JSONObject();

        JSONArray entities = new JSONArray();

        JSONObject arrow0 = new JSONObject();
        arrow0.put("type", "arrow");
        arrow0.put("id", "arrow0");
        arrow0.put("x", 1);
        arrow0.put("y", 1);

        JSONObject arrow1 = new JSONObject();
        arrow1.put("type", "arrow");
        arrow1.put("id", "arrow1");
        arrow1.put("x", 2);
        arrow1.put("y", 1);

        JSONObject arrow2 = new JSONObject();
        arrow2.put("type", "arrow");
        arrow2.put("id", "arrow2");
        arrow2.put("x",3);
        arrow2.put("y", 1);

        JSONObject wood = new JSONObject();
        wood.put("type", "wood");
        wood.put("id", "wood0");
        wood.put("x",4);
        wood.put("y", 1);

        entities.put(arrow2);
        entities.put(wood);

        expected.put("entities", entities);

        JSONObject goal = new JSONObject();
        goal.put("goal", "exit");
        expected.put("goal-condition", goal);
        
        JSONObject inventory = new JSONObject();
        JSONArray collectables = new JSONArray();
        collectables.put(arrow0);
        collectables.put(arrow1);
        inventory.put("collectables", collectables);
        inventory.put("buildables", new JSONArray());

        expected.put("inventory", inventory);

        JSONObject player = new JSONObject();
        player.put("health", 10.0);
        player.put("attack", 1.0);
        player.put("defense", 0);
        player.put("curr_position_x", 2);
        player.put("curr_position_y", 1);
        player.put("last_position_x", 1);
        player.put("last_position_y", 1);
        player.put("player_state", "NormalState");
        player.put("status_effects", new JSONArray());
        player.put("current_potion", new JSONObject());
        player.put("item_built_count", 0);
        player.put("potion_timer", 0);

        expected.put("player", player);

        expected.put("config", dmc.currentDungeon.getConfig());

        expected.put("dungeonId", dmc.currentDungeon.getId());
        expected.put("dungeonName", dmc.currentDungeon.getName());
        expected.put("current_tick", dmc.currentDungeon.getTick());
        expected.put("numSpidersSpawned", 0);
        expected.put("numZombiesSpawned", dmc.currentDungeon.getNumZombiesSpawned());
        expected.put("enemiesKilled", dmc.currentDungeon.getEnemiesKilled());

        assertEquals(expected.toString(), saveState.toString());
    }
    
    @Test
    @DisplayName("Continue a dungeon game from a JSON")
    public void testLoadSaveJSON() {
        Helper helper = new Helper();
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_buildBowTest", "c_simple");
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);

        JSONObject saveState = SaveUtility.saveGameToJSON(dmc.currentDungeon);
        Dungeon dungeon = new Dungeon(saveState, false);

        DungeonResponse res = dungeon.getDungeonResponse();

        ArrayList<EntityResponse> entities = new ArrayList<EntityResponse>();
        entities.add(new EntityResponse("player", "player", new Position(2, 1), false));
        entities.add(new EntityResponse("arrow2", "arrow", new Position(3, 1), false));
        entities.add(new EntityResponse("wood0", "wood", new Position(4, 1), false));

        ArrayList<ItemResponse> inventory = new ArrayList<ItemResponse>();
        inventory.add(new ItemResponse("arrow0", "arrow"));
        inventory.add(new ItemResponse("arrow1", "arrow"));

        DungeonResponse expected = new DungeonResponse("0", "d_buildBowTest", entities, inventory, new ArrayList<BattleResponse>(), new ArrayList<String>(), ":exit");
        assertTrue(helper.assertEqualsDungeonResponse(expected, res));
    }

    @Test
    @DisplayName("Call saveGame and check a game save json file is created and check a game save can be loaded to create a dungeon game")
    public void testControllerSaveAndLoadGame() {
        Helper helper = new Helper();
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_buildBowTest", "c_simple");
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);

        dmc.saveGame("save1");
        DungeonManiaController dmc2 = new DungeonManiaController();
        dmc2.loadGame("save1");

        DungeonResponse res = dmc2.tick(Direction.RIGHT);

        ArrayList<EntityResponse> entities = new ArrayList<EntityResponse>();
        entities.add(new EntityResponse("player", "player", new Position(3, 1), false));
        entities.add(new EntityResponse("wood0", "wood", new Position(4, 1), false));

        ArrayList<ItemResponse> inventory = new ArrayList<ItemResponse>();
        inventory.add(new ItemResponse("arrow0", "arrow"));
        inventory.add(new ItemResponse("arrow1", "arrow"));
        inventory.add(new ItemResponse("arrow2", "arrow"));

        DungeonResponse expected = new DungeonResponse("0", "d_buildBowTest", entities, inventory, new ArrayList<BattleResponse>(), new ArrayList<String>(), ":exit");
        assertTrue(helper.assertEqualsDungeonResponse(expected, res));
    }

    @Test
    @DisplayName("check behaviour of lastposition when saved before first tick")
    public void testSaveFromStart() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_buildBowTest", "c_simple");
        JSONObject playerData = SaveUtility.playerToJSON(dmc.currentDungeon.getPlayer());
        assertEquals(playerData.getInt("last_position_x"), 
                    dmc.currentDungeon.getPlayer().getCurrentPosition().getX());
        assertEquals(playerData.getInt("last_position_y"), 
                    dmc.currentDungeon.getPlayer().getCurrentPosition().getY());
    }

    @Test
    @DisplayName("Check for sword entity behaviour")
    public void testSaveSword() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_zombies", "c_simple");

        JSONObject saveGame = SaveUtility.saveGameToJSON(dmc.currentDungeon);
        JSONObject sword = saveGame.getJSONArray("entities").getJSONObject(0);
        assertEquals(sword.getString("type"), "sword");
        assertEquals(sword.getString("id"), "sword0");
        assertEquals(sword.getInt("x"), 1);
        assertEquals(sword.getInt("y"), 2);
        assertEquals(sword.getInt("durability"), 1);
        assertEquals(sword.getInt("attack"), 2);
        assertEquals(sword.getInt("defence"), 0);

        dmc.tick(Direction.DOWN);
        JSONObject saveGame2 = SaveUtility.saveGameToJSON(dmc.currentDungeon);
        JSONObject sword2 = saveGame2.getJSONObject("inventory").getJSONArray("collectables").getJSONObject(0);
        assertEquals(sword.toString(), sword2.toString());;
    }

    @Test
    @DisplayName("Check for loading entity behaviour")
    public void testLoadEntities() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_loadEntityTest", "c_simple");
        dmc.saveGame("test");
        
        DungeonManiaController dmc2 = new DungeonManiaController();
        DungeonResponse res = dmc2.loadGame("test");
        assertTrue(res.getEntities().size() == 17);

    }

    @Test
    @DisplayName("test loading in a locked and unlocked door")
    public void testLoadDoor() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_loadDoorTest", "c_simple");
        dmc.saveGame("test41");
        
        // door should be locked on this save
        DungeonManiaController dmc2 = new DungeonManiaController();
        dmc2.loadGame("test41");
        DoorEntity door = (DoorEntity) dmc2.currentDungeon.getEntityAtPosition("door", new Position(1, 0));
        assertFalse(door.getIsUnlocked());

        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);

        door = (DoorEntity) dmc.currentDungeon.getEntityAtPosition("door", new Position(1, 0));
        assertTrue(door.getIsUnlocked());

        dmc.saveGame("test41");

        dmc2 = new DungeonManiaController();
        dmc2.loadGame("test41");
        door = (DoorEntity) dmc2.currentDungeon.getEntityAtPosition("door", new Position(1, 0));
        assertTrue(door.getIsUnlocked());

    }

    @Test
    @DisplayName("test LoadPlayer")
    public void testLoadPlayerExtrasFromSave() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_buildBowTest", "c_simple");

        Player player = new Player(new Position(0, 0), 0.0, 0.0);
        JSONObject playerData = new JSONObject();
        playerData.put("player_state", "InvisibleState");
        playerData.put("potion_timer", 3);
        playerData.put("last_position_x", 0);
        playerData.put("last_position_y", 0);
        playerData.put("item_built_count", 1);

        
        JSONArray PotionQ = new JSONArray();
        JSONObject potionData = new JSONObject();
        potionData.put("type", "invincibility_potion");
        potionData.put("id", "invincibility_potion0");
        potionData.put("x",4);
        potionData.put("y", 1);

        PotionQ.put(potionData);

        playerData.put("status_effects", PotionQ);

        JSONObject potionData1 = new JSONObject();
        potionData1.put("type", "invincibility_potion");
        potionData1.put("id", "invincibility_potion1");
        potionData1.put("x",4);
        potionData1.put("y", 1);

        playerData.put("current_potion", potionData1);

        JSONArray buildables = new JSONArray();
        JSONObject shield = new JSONObject();
        shield.put("type", "shield");
        shield.put("id", "shield0");
        shield.put("attack", 0);
        shield.put("durability", 1);
        shield.put("defence", 2);

        buildables.put(shield);

        JSONObject inventory = new JSONObject();
        inventory.put("buildables", buildables);
        inventory.put("collectables", new JSONArray());

        playerData.put("inventory", inventory);

        player.loadPlayer(playerData, new EntityFactory(dmc.currentDungeon.getConfig()));
        player.loadInventory(playerData.getJSONObject("inventory"), new EntityFactory(dmc.currentDungeon.getConfig()));

        assertEquals(player.getState().getName(), "InvisibleState");
        assertEquals(player.getStatusEffects().size(), 1);
        assertEquals(player.getStatusEffects().get(0).getId(), "invincibility_potion0");
        assertEquals(player.getCurrentPotion().getId(), "invincibility_potion1");
        assertEquals(player.getBuiltItems().size(), 1);
        assertEquals(player.getBuiltItems().get(0).getId(), "shield0");

        playerData.put("player_state", "InvincibleState");
        player.loadPlayer(playerData, new EntityFactory(dmc.currentDungeon.getConfig()));

        assertEquals(player.getState().getName(), "InvincibleState");
    }

    @Test
    @DisplayName("allgames lists correct names of gamesaves")
    public void testControllerAllGames() {
        DungeonManiaController dmc = new DungeonManiaController();
        assertTrue(dmc.allGames().contains("test"));
    }

    @Test
    @DisplayName("test Illegal Argument Exception is thrown with invalid load")
    public void testErrorInvalidName() {
        DungeonManiaController dmc = new DungeonManiaController();
        assertThrows(IllegalArgumentException.class, () -> {dmc.loadGame("inVALID_game_980182");});
    }

    @Test
    @DisplayName("test new items (milestone 3)")
    public void testMileStone3Items() {
        Helper helper = new Helper();
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_persistenceTest_M3", "M3_persistenceTest");

        // Collect materials
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);

        DungeonResponse exp;
        assertDoesNotThrow(() -> dmc.build("midnight_armour"));
        assertDoesNotThrow(() -> dmc.build("sceptre"));

        exp = dmc.saveGame("test182");

        DungeonManiaController dmc2 = new DungeonManiaController();
        DungeonResponse res = dmc2.loadGame("test182");
        assertTrue(helper.assertEqualsDungeonResponse(exp, res));
    }
}
