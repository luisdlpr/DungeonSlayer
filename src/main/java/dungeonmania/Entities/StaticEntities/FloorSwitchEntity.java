package dungeonmania.Entities.StaticEntities;

import org.json.JSONObject;

import dungeonmania.Entities.StaticEntities.FloorSwitchStates.FloorSwitchActiveState;
import dungeonmania.Entities.StaticEntities.FloorSwitchStates.FloorSwitchInactiveState;
import dungeonmania.Entities.StaticEntities.FloorSwitchStates.FloorSwitchState;
import dungeonmania.ObserverPatterns.DungeonSubject;

/**
 * Floorswitch entity class
 * @author Jordan Liang (z5254761)
 */

public class FloorSwitchEntity extends StaticEntity {
    // Floorswitch state pattern
    FloorSwitchState ActiveState;
    FloorSwitchState InactiveState;

    private FloorSwitchState state;

    /**
     * construction with a JSONObject
     * @param object JSONObject specifying type, x, and y pos
     * @param id Integer unique id 
     */

    public FloorSwitchEntity(JSONObject object, Integer id) {
        super(object, id, false);

        // The default state of a newly initialised switch is inactive
        InactiveState = new FloorSwitchInactiveState(this);
        ActiveState = new FloorSwitchActiveState(this);

        // Initially inactive with no boulder
        this.state = InactiveState;
    }

    /**
     * Get the name of the state that switch is currently in
     */
    public String getName() {
        return state.getName();
    }

    /**
     * Tell the switch to go active
     */

    public void goActive() {
        state.goActive();
    }

    /**
     * Tell the switch to go inactive
     */

    public void goInactive() {
        state.goInactive();
    }

    /**
     * Get current switch state
     */

    public FloorSwitchState getState() {
        return state;
    }

    /**
     * Set switch state
     */

    public void setState(FloorSwitchState state) {
        this.state = state;
    }

    /**
     * Getters for the different states
     */

    public FloorSwitchState getActiveState() {
        return ActiveState;
    }

    public FloorSwitchState getInactiveState() {
        return InactiveState;
    }

    @Override
    public void update(DungeonSubject obj) {
        // Do nothing
    }

}
