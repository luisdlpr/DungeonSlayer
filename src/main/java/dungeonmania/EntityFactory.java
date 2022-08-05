package dungeonmania;

import org.json.JSONObject;

import dungeonmania.Buildables.Bow;
import dungeonmania.Buildables.Buildable;
import dungeonmania.Buildables.MidnightArmour;
import dungeonmania.Buildables.Sceptre;
import dungeonmania.Buildables.Shield;
import dungeonmania.Entities.Entity;
import dungeonmania.Entities.PlayerEntity;
import dungeonmania.Entities.StaticEntities.PlaceholderEntity;
import dungeonmania.Entities.CollectableEntities.ArrowEntity;
import dungeonmania.Entities.CollectableEntities.Potions.InvincibilityPotion;
import dungeonmania.Entities.CollectableEntities.Potions.InvisibilityPotion;
import dungeonmania.Entities.CollectableEntities.Weapons.SwordEntity;
import dungeonmania.Entities.CollectableEntities.KeyEntity;
import dungeonmania.Entities.CollectableEntities.SunStoneEntity;
import dungeonmania.Entities.CollectableEntities.TimeTurnerEntity;
import dungeonmania.Entities.CollectableEntities.UnplacedBombEntity;
import dungeonmania.Entities.CollectableEntities.TreasureEntity;
import dungeonmania.Entities.CollectableEntities.WoodEntity;
import dungeonmania.Entities.MoveableEntities.AssassinEntity;
import dungeonmania.Entities.MoveableEntities.HydraEntity;
import dungeonmania.Entities.MoveableEntities.MercenaryEntity;
import dungeonmania.Entities.MoveableEntities.MoveableEntity;
import dungeonmania.Entities.MoveableEntities.SpiderEntity;
import dungeonmania.Entities.MoveableEntities.ZombieToastEntity;
import dungeonmania.Entities.StaticEntities.BoulderEntity;
import dungeonmania.Entities.StaticEntities.FloorSwitchEntity;
import dungeonmania.Entities.StaticEntities.PlacedBombEntity;
import dungeonmania.Entities.StaticEntities.DoorEntity;
import dungeonmania.Entities.StaticEntities.ExitEntity;
import dungeonmania.Entities.StaticEntities.PortalEntity;
import dungeonmania.Entities.StaticEntities.SwampTileEntity;
import dungeonmania.Entities.StaticEntities.TimeTravellingPortalEntity;
import dungeonmania.Entities.StaticEntities.WallEntity;
import dungeonmania.Entities.StaticEntities.ZombieToastSpawnerEntity;
import dungeonmania.Entities.StaticEntities.LogicEntities.LightBulbEntity;
import dungeonmania.Entities.StaticEntities.LogicEntities.SwitchDoorEntity;
import dungeonmania.Entities.StaticEntities.LogicEntities.WireEntity;
import dungeonmania.util.Position;

/**
 * Class to easily convery JSONObject given in files to entities.
 * @author Luis Reyes (z5206766)
 */
public class EntityFactory {
    private JSONObject config;

    /**
     * pass in config file to construct
     * @param config
     */
    public EntityFactory(JSONObject config) {
        this.config = config;
    }

    /**
     * instantiate entity based on string type stored in JSON object
     * @param object JSONObject to construct
     * @param id Integer unique id based on type to give
     * @return Entity new entity generated
     */
    public Entity createEntity(JSONObject object, Integer id) {
        Entity newEntity = new PlaceholderEntity(object, id);
        switch(object.getString("type")) {
            // Static Entities
            case "wall":
                return (Entity) new WallEntity(object, id);
            case "boulder":
                return (Entity) new BoulderEntity(object, id);
            case "switch":
                return (Entity) new FloorSwitchEntity(object, id);
            case "door":
                return (Entity) new DoorEntity(object, id, object.getInt("key"));
            case "portal":
                return (Entity) new PortalEntity(object, id, object.getString("colour"));
            case "zombie_toast_spawner":
                return (Entity) new ZombieToastSpawnerEntity(object, id, true);
            case "exit":
                return (Entity) new ExitEntity(object, id);
            case "swamp_tile":
                return (Entity) new SwampTileEntity(object, id, object.getInt("movement_factor"));
            case "time_travelling_portal":
                return (Entity) new TimeTravellingPortalEntity(object, id);
            case "light_bulb_off":
                return (Entity) new LightBulbEntity(object, id, false);
            case "wire":
                return (Entity) new WireEntity(object, id, false);
            case "switch_door":
                return (Entity) new SwitchDoorEntity(object, id, object.getInt("key"));

            // Collectable Entities
            case "key":
                return (Entity) new KeyEntity(object, id, object.getInt("key"));
            case "bomb":
                return (Entity) new UnplacedBombEntity(object, id, false);
            case "treasure":
                return (Entity) new TreasureEntity(object, id);
            case "wood":
                return (Entity) new WoodEntity(object, id);
            case "arrow":
                return (Entity) new ArrowEntity(object, id);
            case "invisibility_potion":
                return (Entity) new InvisibilityPotion(object, id, config.getInt("invisibility_potion_duration"));
            case "invincibility_potion":
                return (Entity) new InvincibilityPotion(object, id, config.getInt("invincibility_potion_duration"));
            case "sword":
                return (Entity) new SwordEntity(object, id, config.getInt("sword_attack"), config.getInt("sword_durability"));
            case "sun_stone":
                return (Entity) new SunStoneEntity(object, id);
            case "time_turner":
                return (Entity) new TimeTurnerEntity(object, id);
                
            // Moveable Entities
            case "spider":
                return (Entity) new SpiderEntity(object, id, config.getDouble("spider_health"), config.getDouble("spider_attack"));
            case "mercenary":
                return (Entity) new MercenaryEntity(object, id, config.getDouble("mercenary_health"), config.getDouble("mercenary_attack"), 
                                                config.getInt("bribe_amount"), config.getInt("bribe_radius"));
            case "zombie_toast":
                return (Entity) new ZombieToastEntity(object, id, config.getDouble("zombie_health"), config.getDouble("zombie_attack"));
            case "assassin":
                return (Entity) new AssassinEntity(object, id, config.getDouble("assassin_health"), config.getDouble("assassin_attack"), 
                                               config.getInt("assassin_bribe_amount"), config.getInt("bribe_radius"), 
                                               config.getDouble("assassin_bribe_fail_rate"), config.getInt("assassin_recon_radius"));
            case "hydra":
                return (Entity) new HydraEntity(object, id, config.getDouble("hydra_health"), config.getDouble("hydra_attack"), config.getDouble("hydra_health_increase_rate"), config.getDouble("hydra_health_increase_amount"));
            }
        return newEntity;
    }

    /**
     * instantiate entity based on string type stored in savegame
     * @param object JSONObject to construct
     * @param id Integer unique id based on type to give
     * @return Entity entity loaded from previous save
     */
    public Entity loadEntity(JSONObject object, Integer id) {
        Entity newEntity = new PlaceholderEntity(object, id);
        switch(object.getString("type")) {
            // Static Entities
            case "wall":
                newEntity = new WallEntity(object, id);
                break;
            case "boulder":
                newEntity = new BoulderEntity(object, id);
                break;
            case "switch":
                // if switch was active in save
                newEntity = new FloorSwitchEntity(object, id);
                if (object.getString("state").equals("ActiveState")) {
                    FloorSwitchEntity floorswitch = (FloorSwitchEntity) newEntity;
                    floorswitch.goActive();
                    floorswitch.setId(object.getString("id"));
                    return (Entity) floorswitch;
                }
                break;
            case "door":
                newEntity = new DoorEntity(object, id, object.getInt("keyId"));
                DoorEntity door = (DoorEntity) newEntity;
                door.setId(object.getString("id"));
                if (object.getBoolean("isOpen")) {
                    door.unlock();
                }
                return (Entity) door;
            case "portal":
                newEntity = new PortalEntity(object, id, object.getString("colour"));
                break;
            case "zombie_toast_spawner":
                newEntity = new ZombieToastSpawnerEntity(object, id, true);
                break;
            case "exit":
                newEntity = new ExitEntity(object, id);
                break;
            case "swamp_tile":
                newEntity = new SwampTileEntity(object, id, object.getInt("movement_factor"));
                break;
            case "time_travelling_portal":
                newEntity = new ExitEntity(object, id);
                break;

            // Collectable Entities
            case "key":
                newEntity = new KeyEntity(object, id, object.getInt("keyId"));
                break;
            case "bomb":
                // if bomb was armed in save
                if (object.has("armed")) {
                    newEntity = new PlacedBombEntity(object.getString("id"),
                                                    object.getString("type"), 
                                                    new Position(object.getInt("x"), object.getInt("y")), 
                                                    false);
                } else {
                    newEntity = new UnplacedBombEntity(object, id, false);
                }
                break;
            case "treasure":
                newEntity = new TreasureEntity(object, id);
                break;
            case "wood":
                newEntity = new WoodEntity(object, id);
                break;
            case "arrow":
                newEntity = new ArrowEntity(object, id);
                break;
            case "invisibility_potion":
                newEntity = new InvisibilityPotion(object, id, config.getInt("invisibility_potion_duration"));
                break;
            case "invincibility_potion":
                newEntity = new InvincibilityPotion(object, id, config.getInt("invincibility_potion_duration"));
                break;
            case "sword":
                newEntity = new SwordEntity(object, id, config.getInt("sword_attack"), object.getInt("durability"));
                break;
            case "sun_stone":
                newEntity = new SunStoneEntity(object, id);
                break;
            case "time_turner":
                newEntity = new TimeTurnerEntity(object, id);
                break;
                
            // Moveable Entities
            case "spider":
                newEntity = new SpiderEntity(object, id, object.getDouble("health"), config.getDouble("spider_attack"));
                MoveableEntity mob = (MoveableEntity) newEntity;
                mob.setTickCounter(object.getInt("swamp"));
                mob.setId(object.getString("id"));
                return (Entity) mob;
            case "mercenary":
                newEntity = new MercenaryEntity(object, id, object.getDouble("health"), config.getDouble("mercenary_attack"), 
                                                config.getInt("bribe_amount"), config.getInt("bribe_radius"));
                MercenaryEntity merc = (MercenaryEntity) newEntity;
                merc.setTickCounter(object.getInt("swamp"));
                // if merc was bribed in save
                if (object.has("mind_control")) {
                    merc.mindControl(object.getInt("mind_control"));
                } else if (object.getString("state").equals("BribedState")) {
                    merc.goBribed();
                }
                merc.setId(object.getString("id"));
                return (Entity) merc;
            case "zombie_toast":
                newEntity = new ZombieToastEntity(object, id, object.getDouble("health"), config.getDouble("zombie_attack"));
                MoveableEntity zomb = (MoveableEntity) newEntity;
                zomb.setTickCounter(object.getInt("swamp"));
                zomb.setId(object.getString("id"));
                return (Entity) zomb;
            case "assassin":
                newEntity = new AssassinEntity(object, id, object.getDouble("health"), config.getDouble("assassin_attack"), 
                                               config.getInt("assassin_bribe_amount"), config.getInt("bribe_radius"),
                                               config.getDouble("assassin_bribe_fail_rate"), config.getInt("assassin_recon_radius"));
                AssassinEntity assasin = (AssassinEntity) newEntity;
                assasin.setTickCounter(object.getInt("swamp"));
                if (object.has("mind_control")) {
                    assasin.mindControl(object.getInt("mind_control"));
                } else if (object.getString("state").equals("BribedState")) {
                    assasin.goBribed();
                }
                assasin.setId(object.getString("id"));
                return (Entity) assasin;
            case "hydra":
                newEntity = new HydraEntity(object, id, object.getDouble("health"), config.getDouble("hydra_attack"), config.getDouble("hydra_health_increase_rate"), config.getDouble("hydra_health_increase_amount"));
                HydraEntity hydra = (HydraEntity) newEntity;
                hydra.setTickCounter(object.getInt("swamp"));
                hydra.setId(object.getString("id"));
                return (Entity) hydra;
            case "older_player":
                // {"attack":10,"x":0,"y":0,"health":10,"id":"player0","type":"player"}
                newEntity = new PlayerEntity(object, id, object.getDouble("health"), object.getDouble("attack"));
                PlayerEntity oldPlayer = (PlayerEntity) newEntity;
                return (Entity) oldPlayer;
        }
        newEntity.setId(object.getString("id"));
        return newEntity;
    }

    public Buildable loadBuildable(JSONObject object) {
        Buildable newBuildable = null;
        switch(object.getString("type")) {
            // buildables
            case "bow":
                newBuildable = new Bow(object.getDouble("durability"));
                break;
            case "shield":
                newBuildable = new Shield(object.getDouble("durability"), object.getDouble("defence"));
                break;
            case "sceptre":
                newBuildable = new Sceptre(config.getDouble("mind_control_duration"));
                break;
            case "midnight_armour":
                newBuildable = new MidnightArmour(object.getDouble("attack"), object.getDouble("defence"), false);
                break;
        }
        newBuildable.setId(object.getString("id"));
        return newBuildable;
    }
}
