package dungeonmania.Entities.CollectableEntities.Potions;

import org.json.JSONObject;

import dungeonmania.Entities.Player;
import dungeonmania.Entities.CollectableEntities.CollectableEntity;

/**
 * Potions which can be collected by player and upon application will change player state
 * @author Luis Reyes (z5206766)
 */
public abstract class Potion extends CollectableEntity{
    private String potionType;
    private int duration;

    /**
     * construct a potion
     * @param object JSONObject data from dungeon file
     * @param id Integer unique id
     * @param duration int duration from config file.
     */
    public Potion(JSONObject object, Integer id, int duration) {
        super(object, id, false);
        this.duration = duration;
    }

    /**
     * @return String potion type
     */
    public String getPotionType() {
        return this.potionType;
    }

    /**
     * @param potionType String potion type
     */
    public void setPotionType(String potionType) {
        this.potionType = potionType;
    }
    
    /**
     * @return get potion duration
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Use potion on player (add to player potion queue)
     * @param player Player
     */
    public abstract void use(Player player);

    /**
     * apply potion effects to player state
     * @param player Player
     */
    public abstract void applyToPlayer(Player player);
}
