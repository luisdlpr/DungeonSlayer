package dungeonmania.Entities.MoveableEntities.MercenaryStates;

import dungeonmania.Entities.MoveableEntities.MercenaryEntity;

/**
 * Hostile mercenary state
 * @author Jordan Liang (z5254761)
 */

public class HostileMercenaryState implements MercenaryState {
    private MercenaryEntity mercenary;
    private String name = "HostileState";

    public HostileMercenaryState(MercenaryEntity mercenary) {
        this.mercenary = mercenary;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void goBribed() {
        mercenary.setState(mercenary.getBribedState());
        
    }

    @Override
    public void goHostile() {
        // Do nothing
        
    }
    
}
