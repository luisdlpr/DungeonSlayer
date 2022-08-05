package dungeonmania.Entities.StaticEntities.FloorSwitchStates;

import dungeonmania.Entities.StaticEntities.FloorSwitchEntity;

/**
 * Inactive floorswitch state
 * @author Jordan Liang (z5254761)
 */

public class FloorSwitchInactiveState implements FloorSwitchState{
    private FloorSwitchEntity floorswitch;
    private String name = "InactiveState";

    public FloorSwitchInactiveState(FloorSwitchEntity floorswitch) {
        this.floorswitch = floorswitch;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void goActive() {
        floorswitch.setState(floorswitch.getActiveState());        
    }

    @Override
    public void goInactive() {
        // Do nothing
        // Already inactive
    }
}
