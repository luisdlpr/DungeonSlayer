package dungeonmania.Entities.StaticEntities;

import java.util.ArrayList;

import org.json.JSONObject;

import dungeonmania.Dungeon;
import dungeonmania.Entities.Player;
import dungeonmania.Entities.CollectableEntities.CollectableEntity;
import dungeonmania.Entities.CollectableEntities.KeyEntity;
import dungeonmania.ObserverPatterns.DungeonSubject;

/**
 * A door entity that can be opened by the player if the player has the corresponding Key according to the config.
 * @author Stan Korotun (z5367728) 
 */

public class DoorEntity extends StaticEntity {

    private Integer keyId;
    private Boolean isUnlocked = false;

    public DoorEntity(JSONObject object, Integer id, Integer keyId) {
        super(object, id, false);
        this.keyId = keyId;
    }

    @Override
    public void update(DungeonSubject obj) {
        Dungeon dungeon = (Dungeon) obj;
        Player player = dungeon.getPlayer();
        
        if (dungeon.entityCollisionCheck(this)) {
            if (this.isUnlocked) return;
            else if (player.hasSunStone()) {
                isUnlocked = true;
            } else {
                ArrayList<CollectableEntity> inv = (ArrayList<CollectableEntity>) player.getInventory();
                
                // If the player has a key, unlock the door
                for (int i = (inv.size() - 1); i > -1; i--) {
                    if (inv.get(i).getType().equals("key")) {
                        KeyEntity temp = (KeyEntity)inv.get(i);
                        if (temp.getkeyId() == this.keyId) {
                            isUnlocked = true;
                            dungeon.discardItem(inv.get(i));
                        }
                    }
                }

                // Player does not have a key, do not let them walk on door
                if (isUnlocked == false) dungeon.reversePlayerMovement(); return;
            }
                      
        }
    }
    
    public Integer getkeyId() {
        return keyId;
    }

    public void setkeyId(Integer keyId) {
        this.keyId = keyId;
    }

    public Boolean getIsUnlocked() {
        return isUnlocked;
    }

    public void setIsUnlocked(boolean val) {
        this.isUnlocked = val;
    }    
    
    public void unlock() {
        this.isUnlocked = true;
    }

}
