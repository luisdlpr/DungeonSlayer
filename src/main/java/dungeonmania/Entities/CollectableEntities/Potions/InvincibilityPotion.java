package dungeonmania.Entities.CollectableEntities.Potions;

import org.json.JSONObject;

import dungeonmania.Dungeon;
import dungeonmania.Entities.Player;
import dungeonmania.Entities.CollectableEntities.CollectableEntity;
import dungeonmania.ObserverPatterns.DungeonSubject;

/**
 * Invincibility Potion - CollectableEntity
 * @author Luis Reyes (z5206766)
 */
public class InvincibilityPotion extends Potion {
    public InvincibilityPotion(JSONObject object, Integer id, int duration) {
        super(object, id, duration);
        setPotionType("InvincibilityPotion");
    }

    /**
     * add to player statusEffect queue
     */
    @Override
    public void use(Player player) {
        player.addStatusEffect(this);
    }

    /**
     * on player collision with this entity on the map, add to player inventory
     */
    @Override
    public void update(DungeonSubject obj) {
        Dungeon dungeon = (Dungeon) obj;
        if (dungeon.entityCollisionCheck(this)) {
            dungeon.sendToPlayerInventory((CollectableEntity) this);
        }
    }

    /**
     * apply potion effects to player state
     * @param player Player
     */
    public void applyToPlayer(Player player) {
        int timerToSet = Math.max(this.getDuration() - 1, 0);
        player.getState().goInvincible();
        player.setPotionTimer(timerToSet);
    }
}
