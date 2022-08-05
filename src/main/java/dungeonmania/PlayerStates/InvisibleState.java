package dungeonmania.PlayerStates;

import dungeonmania.Entities.Player;

/**
 * Invisibility PlayerState
 * @author Luis Reyes (z5206766)
 */
public class InvisibleState implements PlayerState{
    private Player player;
    private String name = "InvisibleState";
    private int timer = 0;

    public InvisibleState(Player player) {
        this.player = player;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void goInvincible() {
        player.setState(player.getInvincibleState());
        return;        
    }

    @Override
    public void goInvisible() {
        return;        
    }

    @Override
    public void goNormal() {
        player.setState(player.getNormalState());
        return;        
    }

    public int getTimer() {
        return this.timer;
    }

    public void decrementTimer() {
        this.timer -= 1;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }
}
