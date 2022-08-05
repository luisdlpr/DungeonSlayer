package dungeonmania.PlayerStates;

import dungeonmania.Entities.Player;

/**
 * Normal State of player - no potion effects applied
 * @author Luis Reyes (z5206766)
 */
public class NormalState implements PlayerState {
    private Player player;
    private String name = "NormalState";
    private int timer = 0;

    public NormalState(Player player) {
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
        player.setState(player.getInvisibleState());
        return;        
    }

    @Override
    public void goNormal() {
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
