package dungeonmania.Entities.StaticEntities.FloorSwitchStates;

/**
 * State of floorswitches
 * @author Jordan Liang (z5254761)
 */

public abstract interface FloorSwitchState {
    public String getName();
    public void goActive();
    public void goInactive();
}
