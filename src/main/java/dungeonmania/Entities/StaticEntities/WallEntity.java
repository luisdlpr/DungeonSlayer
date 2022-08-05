package dungeonmania.Entities.StaticEntities;

import org.json.JSONObject;

import dungeonmania.Dungeon;
import dungeonmania.ObserverPatterns.DungeonSubject;

/**
 * Wall Entity
 * @author Luis Reyes (z5206766)
 */
public class WallEntity extends StaticEntity {

    /**
     * construction with a JSONObject
     * @param object JSONObject specifying type, x, and y pos
     * @param id Integer unique id 
     */
    public WallEntity(JSONObject object, Integer id) {
        super(object, id, false);
    }

    /**
     * update player if they attempt to collide with the wall
     */
    @Override
    public void update(DungeonSubject obj) {
        Dungeon dungeon = (Dungeon) obj;
        if (dungeon.entityCollisionCheck(this)) {
            dungeon.reversePlayerMovement();
        };
    }
    
}
