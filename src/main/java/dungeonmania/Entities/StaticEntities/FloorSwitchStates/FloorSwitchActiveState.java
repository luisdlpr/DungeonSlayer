package dungeonmania.Entities.StaticEntities.FloorSwitchStates;

import dungeonmania.Entities.StaticEntities.FloorSwitchEntity;

/**
 * Active floorswitch state
 * @author Jordan Liang (z5254761)
 */

public class FloorSwitchActiveState implements FloorSwitchState {
    private FloorSwitchEntity floorswitch;
    private String name = "ActiveState";

    public FloorSwitchActiveState(FloorSwitchEntity floorswitch) {
        this.floorswitch = floorswitch;
    }
    
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void goActive() {    
        // Do nothing
        // Already active   
    }

    @Override
    public void goInactive() {
        floorswitch.setState(floorswitch.getInactiveState());           
    }
}
