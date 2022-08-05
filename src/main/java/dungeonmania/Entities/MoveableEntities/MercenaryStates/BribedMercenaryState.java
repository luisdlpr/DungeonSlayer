package dungeonmania.Entities.MoveableEntities.MercenaryStates;

import dungeonmania.Entities.MoveableEntities.MercenaryEntity;

/**
 * Bribed mercenary state
 * @author Jordan Liang (z5254761)
 */

public class BribedMercenaryState implements MercenaryState {
    private String name = "BribedState";
    private MercenaryEntity mercenary;

    public BribedMercenaryState(MercenaryEntity mercenary) {
        this.mercenary = mercenary;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void goBribed() {
        // Do nothing
    }

    @Override
    public void goHostile() {
        mercenary.setState(mercenary.getHostileState());
    }
    
}
