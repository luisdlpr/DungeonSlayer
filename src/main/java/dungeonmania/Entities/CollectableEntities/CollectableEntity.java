package dungeonmania.Entities.CollectableEntities;

import org.json.JSONObject;

import dungeonmania.Dungeon;
import dungeonmania.Entities.Entity;
import dungeonmania.Entities.Player;
import dungeonmania.ObserverPatterns.DungeonSubject;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.util.Position;

/**
 * A entity collectable by the player on pickup
 * @author Luis Reyes (z5206766) & 
 */
public abstract class CollectableEntity extends Entity {
    /**
     * manual construction
     * @param id String for unique id
     * @param type String for entity type
     * @param position Position current position in dungeon co-ordinate system
     * @param isInteractable boolean has interaction functionality
     */
    public CollectableEntity(String id, String type, Position position, boolean isInteractable) {
        super(id, type, position, isInteractable);
    }

    /**
     * construction with a JSONObject
     * @param object JSONObject specifying type, x, and y pos
     * @param id Integer unique id 
     * @param isInteractable boolean has interaction functionality
     */
    public CollectableEntity(JSONObject object, Integer id, boolean isInteractable) {
        super(object, id, isInteractable);
    }

    /**
     * Default method for adding collectable to inventory if player stands on it
     * Override this for any class that does more than adding to inventory
     */
    public void update(DungeonSubject obj) {
        Dungeon dungeon = (Dungeon) obj;
        Player player = dungeon.getPlayer();

        if (player.getCurrentPosition().equals(this.getPosition())) {
            player.addToInventory(this);
            dungeon.removeObserver(this);
        }
    }

    public ItemResponse getItemResponse() {
        return new ItemResponse(this.getId(), this.getType());
    }
}
