package dungeonmania.Entities.StaticEntities.LogicEntities;

import org.json.JSONObject;

import dungeonmania.Entities.StaticEntities.FloorSwitchEntity;
import dungeonmania.Entities.StaticEntities.StaticEntity;
import dungeonmania.ObserverPatterns.DungeonSubject;

import java.util.ArrayList;
import java.util.List;

import dungeonmania.Dungeon;
import dungeonmania.Entities.Entity;

    /**
    * Wire entity, can trasmit "power", logic is not fully implemented, there are problems with wires not deactivating if the length is two or more.
    * @author Stan Korotun (z5367728) 
    */

public class WireEntity extends StaticEntity implements LogicEntity{

    private boolean hasBeenChanged = false;
    private boolean isActive = false;
    List<Entity> switchesAndLogicEntsInRange = new ArrayList<Entity>();

    public WireEntity(JSONObject object, Integer id, boolean isInteractable) {
        super(object, id, isInteractable);
    }

    @Override
    public void update(DungeonSubject obj) {
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
                    return;
                } 
            }
            if (e instanceof LogicEntity && e instanceof LightBulbEntity == false) {
                LogicEntity l = (LogicEntity) e;
                if (l.isActive() == true) {
                    if (this.isActive == false) {
                        hasBeenChanged = true;
                    } else {
                        hasBeenChanged = false;
                    }
                    this.isActive = true;
                    return;
                } 
            }  
        }
        
        //this runs when no nearby active entities are found
        if (this.isActive == true) {
            hasBeenChanged = true;
        } else {
            hasBeenChanged = false;
        }
        this.isActive = false;
        
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
