package dungeonmania.PlayerStates;

import dungeonmania.Entities.Player;

/**
 * Invisibility PlayerState
 * @author Luis Reyes (z5206766)
 */
public class InvincibleState implements PlayerState{
    private Player player;
    private String name = "InvincibleState";
    private int timer = 0;

    public InvincibleState(Player player) {
        this.player = player;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void goInvincible() {
        return;        
    }

    @Override
    public void goInvisible() {
        player.setState(player.getInvisibleState());
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
