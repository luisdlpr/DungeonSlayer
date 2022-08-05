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
    * Light bulb entity, can light up and turn off if connected to "power", logic is not fully implemented, there are problems with bulbs not deactivating if the length of wires is two or more.
    * @author Stan Korotun (z5367728) 
    */

public class LightBulbEntity extends StaticEntity implements LogicEntity{

    private String logicValue;
    private boolean hasBeenChanged = false;
    List<Entity> switchesAndLogicEntsInRange = new ArrayList<Entity>();

    public LightBulbEntity(JSONObject object, Integer id, boolean isInteractable) {
        super(object, id, isInteractable);
        logicValue = object.getString("logic");
    }

    @Override
    public void update(DungeonSubject obj) {
        updateSwitchesAndLogicEntsInRange(obj);
        for (Entity e : switchesAndLogicEntsInRange) {
            if (e.getType().equals("switch")) {
                FloorSwitchEntity s = (FloorSwitchEntity) e;
                if(s.getState() == s.getActiveState()) {
                    if (this.getType().equals("light_bulb_off")) {
                        hasBeenChanged = true;
                    } else {
                        hasBeenChanged = false;
                    }
                    this.setType("light_bulb_on");
                    return;
                } 
            }
            //if instance of logic entity
                //if the state of entity is active
                //change this entity to active also, update hasBeenChanged accordingly
            if (e instanceof LogicEntity && e instanceof LightBulbEntity == false) {
                LogicEntity l = (LogicEntity) e;
                if (l.isActive() == true) {
                    if (this.getType().equals("light_bulb_off")) {
                        hasBeenChanged = true;
                    } else {
                        hasBeenChanged = false;
                    }
                    this.setType("light_bulb_on");
                    return;
                } 
            }    
              
        }
        if (this.getType().equals("light_bulb_on")) {
            hasBeenChanged = true;
        } else {
            hasBeenChanged = false;
        }
        this.setType("light_bulb_off");

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

    @Override
    public Boolean hasBeenChanged() {
        return this.hasBeenChanged;
    }

    @Override
    public boolean isActive() {
        if (getType().equals("light_bulb_on")) {
            return true;
        }
        return false;
    }

}
