package dungeonmania.PlayerStates;

/**
 * Interface for player potion effects state pattern
 * @author Luis Reyes (z5206766)
 */
public abstract interface PlayerState {
    public String getName();
    public void goInvincible();
    public void goInvisible();
    public void goNormal();
    public int getTimer();
    public void decrementTimer();
    public void setTimer(int timer);
}
