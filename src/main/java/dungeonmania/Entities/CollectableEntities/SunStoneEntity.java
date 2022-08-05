package dungeonmania.Entities.CollectableEntities;

import org.json.JSONObject;

import dungeonmania.util.Position;

public class SunStoneEntity extends CollectableEntity {

    public SunStoneEntity(String id, String type, Position position, boolean isInteractable) {
        super(id, type, position, isInteractable);
    }
    
    public SunStoneEntity(JSONObject object, Integer id) {
        super(object, id, false);
    }

}
