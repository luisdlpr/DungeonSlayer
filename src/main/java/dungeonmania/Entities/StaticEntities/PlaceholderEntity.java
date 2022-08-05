package dungeonmania.Entities.StaticEntities;

import org.json.JSONObject;

import dungeonmania.ObserverPatterns.DungeonSubject;
import dungeonmania.util.Position;

// class used for testing before specific entity classes created
public class PlaceholderEntity extends StaticEntity{
    
    public PlaceholderEntity(String id, String type, Position position, boolean isInteractable) {
        super(id, type, position, isInteractable);
    }

    public PlaceholderEntity(JSONObject object, Integer id) {
        super(object, id, false);
    }

    @Override
    public void update(DungeonSubject obj) {
        
    }
}
