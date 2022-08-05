package dungeonmania.Entities.CollectableEntities;

import org.json.JSONObject;

import dungeonmania.util.Position;

/**
 * Wood entity class
 * @author Nancy Huynh (z5257042)
 */
public class WoodEntity extends CollectableEntity {

    public WoodEntity(String id, String type, Position position, boolean isInteractable) {
        super(id, type, position, isInteractable);
    }
    
    public WoodEntity(JSONObject object, Integer id) {
        super(object, id, false);
    }
    
}
