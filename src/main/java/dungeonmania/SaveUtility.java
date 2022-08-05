package dungeonmania;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONArray;
import org.json.JSONObject;

import dungeonmania.Buildables.Buildable;
import dungeonmania.Entities.Entity;
import dungeonmania.Entities.Player;
import dungeonmania.Entities.PlayerEntity;
import dungeonmania.Entities.CollectableEntities.CollectableEntity;
import dungeonmania.Entities.CollectableEntities.KeyEntity;
import dungeonmania.Entities.CollectableEntities.Potions.Potion;
import dungeonmania.Entities.CollectableEntities.Weapons.SwordEntity;
import dungeonmania.Entities.CollectableEntities.Weapons.Weapon;
import dungeonmania.Entities.MoveableEntities.AssassinEntity;
import dungeonmania.Entities.MoveableEntities.MercenaryEntity;
import dungeonmania.Entities.MoveableEntities.MoveableEntity;
import dungeonmania.Entities.StaticEntities.DoorEntity;
import dungeonmania.Entities.StaticEntities.FloorSwitchEntity;
import dungeonmania.Entities.StaticEntities.PlacedBombEntity;
import dungeonmania.Entities.StaticEntities.PortalEntity;
import dungeonmania.Entities.StaticEntities.SwampTileEntity;

// need a way to keep battlesList

/**
 * Class to handle saveGames and persistence
 * @authors Luis Reyes (z5206766)
 */
public class SaveUtility {

    /**
     * save player data to a JSON Object
     * @param player player to save
     * @return JSONObject with player data
     */
    public static JSONObject playerToJSON(Player player) {
        JSONObject object = new JSONObject();
        // stats
        object.put("health", player.getHealth());
        object.put("attack", player.getAttackDamage());
        object.put("defense", player.getDefensePoints());
        
        // position
        object.put("curr_position_x", player.getCurrentPosition().getX());
        object.put("curr_position_y", player.getCurrentPosition().getY());
        if (player.getLastPosition() != null) {
            object.put("last_position_x", player.getLastPosition().getX());
            object.put("last_position_y", player.getLastPosition().getY());    
        } else {
            object.put("last_position_x", player.getCurrentPosition().getX());
            object.put("last_position_y", player.getCurrentPosition().getY());
        }

        // player state
        object.put("player_state", player.getState().getName());

        // status effects queue
        JSONArray statusEffectsJSON = new JSONArray();
        if (player.getStatusEffects().size() > 0) {
            for (Potion p : player.getStatusEffects()) {
                JSONObject potionData = entityToJSON(p);
                statusEffectsJSON.put(potionData);
            }
        }
        object.put("status_effects", statusEffectsJSON);

        // current potion data
        if (player.getCurrentPotion() != null) {
            object.put("current_potion", entityToJSON(player.getCurrentPotion()));
        } else {
            object.put("current_potion", new JSONObject());
        }
        object.put("potion_timer", player.getPotionTimer());
        
        // item_built_count
        object.put("item_built_count", player.getNumberBuiltItems());

        return object;
    }

    /**
     * save entity data to a JSON Object
     * @param entity entity to save
     * @return JSONObject with entity data
     */
    public static JSONObject entityToJSON(Entity entity) {
        JSONObject object = new JSONObject();

        // Basic entity
        object.put("type", entity.getType());
        object.put("id", entity.getId());
        object.put("x", entity.getPosition().getX());
        object.put("y", entity.getPosition().getY());

        // State entities -> switch, mercenary
        if (entity.getType().equals("switch")) {
            FloorSwitchEntity floorSwitch = (FloorSwitchEntity) entity;
            object.put("state", floorSwitch.getState().getName());
        } else if (entity.getType().equals("mercenary")) {
            MercenaryEntity mercenary = (MercenaryEntity) entity;
            object.put("state", mercenary.getState().getName());
            if (mercenary.getMindControlCounter() != 0) {
                object.put("mind_control", mercenary.getMindControlCounter());
            }
        } else if (entity.getType().equals("assassin")) {
            AssassinEntity assassin = (AssassinEntity) entity;
            object.put("state", assassin.getState().getName());
            if (assassin.getMindControlCounter() != 0) {
                object.put("mind_control", assassin.getMindControlCounter());
            }
        }

        // moveable entity stats -> attack, health
        if (entity instanceof MoveableEntity) {
            MoveableEntity mob = (MoveableEntity) entity;
            object.put("health", mob.getHealth());
            object.put("attack", mob.getAttackDamage());
            if (!(entity instanceof PlayerEntity)) object.put("swamp", mob.getTickCounter());
        }

        // key/door entities
        if (entity.getType().equals("key")) {
            KeyEntity key = (KeyEntity) entity;
            object.put("keyId", key.getkeyId());
        } else if (entity.getType().equals("door")) {
            DoorEntity door = (DoorEntity) entity;
            object.put("keyId", door.getkeyId());
            object.put("isOpen", door.getIsUnlocked());
        }

        // portals
        if (entity.getType().equals("portal")) {
            PortalEntity portal = (PortalEntity) entity;
            object.put("colour", portal.getColour());
        }

        // sword
        if (entity.getType().equals("sword")) {
            SwordEntity sword = (SwordEntity) entity;
            object.put("durability", sword.getDurability());
            object.put("attack", sword.getAttackDamage());
            object.put("defence", sword.getDefencePoints());
        }
        
        // swamptile
        if (entity.getType().equals("swamp_tile")) {
            SwampTileEntity swamp = (SwampTileEntity) entity;
            object.put("movement_factor", swamp.getMovementFactor());
        }

        // bombs
        if (entity instanceof PlacedBombEntity) {
            object.put("armed", true);
        }

        return object;
    }

    /**
     * save weapon data to a JSON Object
     * @param weapon Weapon to save
     * @return JSONObject with weapon data
     */
    public static JSONObject weaponToJSON(Weapon weapon) {
        JSONObject object = new JSONObject();
        object.put("type", weapon.getItemResponse().getType());

        String id = "";
        if (weapon instanceof Buildable) {
            Buildable buildable = (Buildable) weapon;
            id = buildable.getId();
        } else if (weapon instanceof SwordEntity) {
            SwordEntity sword = (SwordEntity) weapon;
            id = sword.getId();
            object.put("x", sword.getPosition().getX());
            object.put("y", sword.getPosition().getY());
        }

        object.put("id", id);
        object.put("attack", weapon.getAttackDamage());
        if (!weapon.getItemResponse().getType().equals("midnight_armour")) {
            object.put("durability", weapon.getDurability());
        }
        object.put("defence", weapon.getDefencePoints());

        return object;
    }

    /**
     * save buildable data to a JSON Object
     * @param buildable Buildable to save
     * @return JSONObject with weapon data
     */
    public static JSONObject buildableToJSON(Buildable buildable) {
        JSONObject object = new JSONObject();
        object.put("type", buildable.getItemResponse().getType());

        String id = buildable.getItemResponse().getId();
        object.put("id", id);

        return object;
    }


    /**
     * save inventory data to a JSON Object
     * @param player Player with inventory to save
     * @return JSONObject with both collectables and buildables inventory data
     */
    public static JSONObject inventoryToJSON(Player player) {
        JSONArray collectables = new JSONArray();

        JSONArray buildables = new JSONArray();
        
        for (CollectableEntity item : player.getInventory()) {
            if (item instanceof SwordEntity) {
                collectables.put(weaponToJSON((SwordEntity) item));
            } else {
                collectables.put(entityToJSON(item));
            }
        }

        for (Buildable item : player.getBuiltItems()) {
            if (item.getType().equals("sceptre")) {
                buildables.put(buildableToJSON(item));
            } else {
                buildables.put(weaponToJSON((Weapon) item));
            }
        }

        JSONObject inventory = new JSONObject();
        inventory.put("collectables", collectables);
        inventory.put("buildables", buildables);

        return inventory;
    }

    public static JSONObject saveGameToJSON(Dungeon currentDungeon) {
        JSONObject saveGame = new JSONObject();

        // entities (on-map)
        JSONArray entities = new JSONArray();
        for (Entity e : currentDungeon.getEntities()) {
            entities.put(entityToJSON(e));
        }
        saveGame.put("entities", entities);

        // inventory
        JSONObject inventory = inventoryToJSON(currentDungeon.getPlayer());
        saveGame.put("inventory", inventory);

        // goal condition
        saveGame.put("goal-condition", currentDungeon.getOriginalGoal());

        JSONObject player = playerToJSON(currentDungeon.getPlayer());
        saveGame.put("player", player);

        saveGame.put("config", currentDungeon.getConfig());
        saveGame.put("dungeonId", currentDungeon.getId());
        saveGame.put("dungeonName", currentDungeon.getName());
        saveGame.put("current_tick", currentDungeon.getTick());
        saveGame.put("numSpidersSpawned", currentDungeon.getNumSpidersSpawned());
        saveGame.put("numZombiesSpawned", currentDungeon.getNumZombiesSpawned());
        saveGame.put("enemiesKilled", currentDungeon.getEnemiesKilled());
        return saveGame;
    }

    public static Dungeon loadGameFromJSON(JSONObject object) {
        return new Dungeon(object, false);
    }

    /**
     * Save a given json to a given filename in the gameSaves directory
     * @param object JSONObject object to save
     * @param name String filename
     */
    public static void JSONToFile(JSONObject object, String name) {
        File saveDirectory = new File("src/main/resources/gameSaves/");
        if (!saveDirectory.exists()) {
            saveDirectory.mkdir();
        }
        try {
            FileWriter file = new FileWriter("src/main/resources/gameSaves/" + name + ".json");
            file.write(object.toString());
            file.flush();
            file.close();
        } catch (IOException e) {
            // e.printStackTrace();
        }
    }

    /**
     * Save a given json to a given filename in the gameSaves directory
     * @param object JSONObject object to save
     * @param name String filename
     */
    public static JSONObject FileToJSON(String name) throws IOException{
        String str = "";
        try {
            str = new String(Files.readAllBytes(Paths.get("src/main/resources/gameSaves/" + name + ".json")));
        } catch (IOException e) {
            throw new IOException();
        }
        return new JSONObject(str);
    }

    public static JSONObject savePastStatetoJSON(Dungeon currentDungeon) {
        JSONObject saveGame = new JSONObject();

        // entities (on-map)
        JSONArray entities = new JSONArray();
        for (Entity e : currentDungeon.getEntities()) {
            entities.put(entityToJSON(e));
        }
        // Save old player as an entity
        PlayerEntity pastPlayer = new PlayerEntity(currentDungeon.getPlayer(), currentDungeon.getNewId("older_player"));
        entities.put(entityToJSON(pastPlayer));
        saveGame.put("entities", entities);

        // inventory
        JSONObject inventory = inventoryToJSON(currentDungeon.getPlayer());
        saveGame.put("inventory", inventory);

        // goal condition
        saveGame.put("goal-condition", currentDungeon.getOriginalGoal());

        saveGame.put("config", currentDungeon.getConfig());
        saveGame.put("dungeonId", currentDungeon.getId());
        saveGame.put("dungeonName", currentDungeon.getName());
        saveGame.put("current_tick", currentDungeon.getTick());
        saveGame.put("numSpidersSpawned", currentDungeon.getNumSpidersSpawned());
        saveGame.put("numZombiesSpawned", currentDungeon.getNumZombiesSpawned());
        saveGame.put("enemiesKilled", currentDungeon.getEnemiesKilled());

        JSONObject player = playerToJSON(currentDungeon.getPlayer());
        saveGame.put("player", player);
        
        return saveGame;
    }
}
