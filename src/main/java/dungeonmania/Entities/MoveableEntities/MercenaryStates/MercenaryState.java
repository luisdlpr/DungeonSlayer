package dungeonmania.Entities.MoveableEntities.MercenaryStates;

public abstract interface MercenaryState {
    public String getName();
    public void goHostile();
    public void goBribed();
}
