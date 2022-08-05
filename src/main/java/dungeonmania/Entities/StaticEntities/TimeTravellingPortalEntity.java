package dungeonmania.Entities.StaticEntities;

import org.json.JSONObject;

import dungeonmania.Dungeon;
import dungeonmania.ObserverPatterns.DungeonSubject;

public class TimeTravellingPortalEntity extends StaticEntity {

    /**
     * TimeTravellingPortal construction with a JSONObject
     */
    public TimeTravellingPortalEntity(JSONObject object, Integer id) {
        super(object, id, false);
    }

    /**
     * Time travel if player walks on portal
     */
    public void update(DungeonSubject obj) {
        Dungeon dungeon = (Dungeon) obj;
        if (dungeon.entityCollisionCheck(this)){
            dungeon.setTimeTravelDungeon(dungeon.getTimeTraveller().timeTravelPortal());
        }
    }
    
}
