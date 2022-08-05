package dungeonmania.Entities.CollectableEntities;

import org.json.JSONObject;

import dungeonmania.Dungeon;
import dungeonmania.ObserverPatterns.DungeonSubject;
import dungeonmania.util.Position;
/**
 * A key entity that can be pickup by the player and used to unlock corresponding doors as per the config allocation.
 * @author Stan Korotun (z5367728) 
 */
public class KeyEntity extends CollectableEntity{

    private int keyId;

    public KeyEntity(String id, String type, Position position, boolean isInteractable) {
        super(id, type, position, isInteractable);
    }
    
    public KeyEntity(JSONObject object, Integer id, int keyId) {
        super(object, id, false);
        this.keyId = keyId;
    }

    @Override
    public void update(DungeonSubject obj) {
        Dungeon dungeon = (Dungeon) obj;

        boolean alreadyHasAKey = dungeon.hasKey();

        if (dungeon.entityCollisionCheck(this) && !alreadyHasAKey) {
            dungeon.sendToPlayerInventory(this);
        }
        
    }

    public Integer getkeyId() {
        return keyId;
    }

    public void setkeyId(Integer keyId) {
        this.keyId = keyId;
    }
    
}
