package dungeonmania.Entities.StaticEntities.LogicEntities;

import org.json.JSONObject;

import dungeonmania.Entities.StaticEntities.DoorEntity;

import java.util.ArrayList;

import dungeonmania.Dungeon;
import dungeonmania.Entities.Player;
import dungeonmania.Entities.CollectableEntities.CollectableEntity;
import dungeonmania.Entities.CollectableEntities.KeyEntity;
import dungeonmania.ObserverPatterns.DungeonSubject;
import dungeonmania.Entities.StaticEntities.FloorSwitchEntity;

import java.util.List;
import dungeonmania.Entities.Entity;

    /**
    * Based on the door entity, the logic section is not finished but functionality of opening with switch is implemented
    * @author Stan Korotun (z5367728) 
    */

public class SwitchDoorEntity extends DoorEntity implements LogicEntity {

    private Boolean isUnlocked = false;
    private boolean hasBeenChanged = false;
    private boolean isActive = false;
    List<Entity> switchesAndLogicEntsInRange = new ArrayList<Entity>();

    public SwitchDoorEntity(JSONObject object, Integer id, Integer keyId) {
        super(object, id, keyId);
    }

    @Override
    public void update(DungeonSubject obj) {
        Dungeon dungeon = (Dungeon) obj;
        Player player = dungeon.getPlayer();
        
        //run the LogicEntity code to check if it should be activated and if it is, then set isUnlocked to true
        //when the player collides with the door the code below will let him through
        updateSwitchesAndLogicEntsInRange(obj);
        for (Entity e : switchesAndLogicEntsInRange) {
            if (e.getType().equals("switch")) {
                FloorSwitchEntity s = (FloorSwitchEntity) e;
                if(s.getState() == s.getActiveState()) {
                    if (this.isActive == false) {
                        hasBeenChanged = true;
                    } else {
                        hasBeenChanged = false;
                    }
                    this.isActive = true;
                    isUnlocked = true;
                } 
            }
            //if instance of logic entity
                //if the state of entity is active
                //change this entity to active also, update hasBeenChanged accordingly
                if (e instanceof LogicEntity && e instanceof LightBulbEntity == false) {
                    LogicEntity l = (LogicEntity) e;
                    if (l.isActive() == true) {
                        if (this.isActive == false) {
                            hasBeenChanged = true;
                        } else {
                            hasBeenChanged = false;
                        }
                        this.isActive = true;
                        isUnlocked = true;

                    } 
                }  
        }
        if (isUnlocked != true) {
            if (this.isActive == true) {
                hasBeenChanged = true;
            } else {
                hasBeenChanged = false;
            }
            this.isActive = false;
        }

        if (dungeon.entityCollisionCheck(this)) {
            if (this.isUnlocked) return;
            else if (player.hasSunStone()) {
                System.out.println();
                isUnlocked = true;
            } else {
                ArrayList<CollectableEntity> inv = (ArrayList<CollectableEntity>) player.getInventory();
                
                // If the player has a key, unlock the door
                for (int i = (inv.size() - 1); i > -1; i--) {
                    if (inv.get(i).getType().equals("key")) {
                        KeyEntity temp = (KeyEntity)inv.get(i);
                        if (temp.getkeyId() == getkeyId()) {
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

    @Override
    public Boolean hasBeenChanged() {
        return this.hasBeenChanged;
    }

    @Override
    public boolean isActive() {
        return this.isActive;
    }

    public void updateSwitchesAndLogicEntsInRange(DungeonSubject obj) {
        Dungeon dungeon = (Dungeon) obj;

        List<Entity> ents = dungeon.getEntities();
        for (Entity e : ents) {
            if (e.getType().equals("switch") || e instanceof LogicEntity && !e.getId().equals(this.getId())) {
                if(e.getPosition().isCardinallyAdjacent(this.getPosition())) {
                    switchesAndLogicEntsInRange.add(e);
                }
            }
        }
    }
    
}
