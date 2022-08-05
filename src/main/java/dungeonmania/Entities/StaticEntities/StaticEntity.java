package dungeonmania.Entities.StaticEntities;

import org.json.JSONObject;

import dungeonmania.Entities.Entity;
import dungeonmania.ObserverPatterns.DungeonSubject;
import dungeonmania.util.Position;

/**
 * A entity thats position stays static on the map and has a specific collision action with player (which could include movement).  
 * @author Luis Reyes (z5206766) & 
 */
public abstract class StaticEntity extends Entity {

    /**
     * manual construction
     * @param id String for unique id
     * @param type String for entity type
     * @param position Position current position in dungeon co-ordinate system
     * @param isInteractable boolean has interaction functionality
     */
    public StaticEntity(String id, String type, Position position, boolean isInteractable) {
        super(id, type, position, isInteractable);
    }

    /**
     * construction with a JSONObject
     * @param object JSONObject specifying type, x, and y pos
     * @param id Integer unique id 
     * @param isInteractable boolean has interaction functionality
     */
    public StaticEntity(JSONObject object, Integer id, boolean isInteractable) {
        super(object, id, isInteractable);
    }

    /**
     * update dungeon subject based on specific behaviour
     */
    public abstract void update(DungeonSubject obj);
}
