package dungeonmania.Entities.CollectableEntities;

import org.json.JSONObject;

import dungeonmania.util.Position;

/**
 * Arrow entitiy class
 * @author Nancy Huynh (z5257042)
 */
public class ArrowEntity extends CollectableEntity {

    public ArrowEntity(String id, String type, Position position, boolean isInteractable) {
        super(id, type, position, isInteractable);
    }
    
    public ArrowEntity(JSONObject object, Integer id) {
        super(object, id, false);
    }

}
