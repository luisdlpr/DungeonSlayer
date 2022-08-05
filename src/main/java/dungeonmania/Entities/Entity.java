package dungeonmania.Entities;

import org.json.JSONObject;

import dungeonmania.ObserverPatterns.DungeonObserver;
import dungeonmania.ObserverPatterns.DungeonSubject;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Position;

/**
 * abstract class encapsulating shared behaviour of all entities in dungeon
 * A dungeon entity is any object present in the dungeon.
 * @author Luis Reyes (z5206766)
 */
public abstract class Entity implements DungeonObserver{
    private String id;
    private String type;
    private Position position;
    private Boolean isInteractable;

    /**
     * manual constructioon
     * @param id
     * @param type
     * @param position
     * @param isInteractable
     */
    public Entity(String id, String type, Position position, Boolean isInteractable) {
        this.id = id;
        this.type = type;
        this.position = position;
        this.isInteractable = isInteractable;
    }

    /**
     * construction with a JSONObject
     * @param object JSONObject specifying type, x, and y pos
     * @param id Integer unique id 
     * @param isInteractable boolean has interaction functionality
     */
    public Entity(JSONObject object, Integer id, boolean isInteractable) {
        this.type = object.getString("type");
        this.id = this.type + String.valueOf(id);
        this.position = new Position(object.getInt("x"), object.getInt("y"));
        this.isInteractable = isInteractable;
    }

    /**
     * @return an EntityResponse filled with the entities relevant data
     */
    public EntityResponse getEntityResponse() {
        EntityResponse res = new EntityResponse(id, type, position, isInteractable);
        return res;
    }

    /**
     * update the dungeonSubject based on entity behaviour
     */
    public abstract void update(DungeonSubject obj);

    /**
     * @return String return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return String return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return Position return the position
     */
    public Position getPosition() {
        return position;
    }

    /**
     * @param position the position to set
     */
    public void setPosition(Position position) {
        this.position = position;
    }

    /**
     * @return Boolean return the isInteractable
     */
    public Boolean isInteractable() {
        return isInteractable;
    }

    /**
     * @param isInteractable the isInteractable to set
     */
    public void setIsInteractable(Boolean isInteractable) {
        this.isInteractable = isInteractable;
    }

}
