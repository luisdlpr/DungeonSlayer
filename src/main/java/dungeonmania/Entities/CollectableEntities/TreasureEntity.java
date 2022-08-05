package dungeonmania.Entities.CollectableEntities;
import org.json.JSONObject;

import dungeonmania.util.Position;

/**
 * Treasure entity class
 * @author Nancy Huynh (z5257042)
 */
public class TreasureEntity extends CollectableEntity {
    
    public TreasureEntity(String id, String type, Position position, boolean isInteractable) {
        super(id, type, position, isInteractable);
    }
    
    public TreasureEntity(JSONObject object, Integer id) {
        super(object, id, false);
    }

}
