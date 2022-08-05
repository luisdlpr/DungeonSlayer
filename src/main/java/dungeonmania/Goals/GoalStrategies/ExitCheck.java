package dungeonmania.Goals.GoalStrategies;

import dungeonmania.Dungeon;

public class ExitCheck extends GoalCheck {
    @Override
    public boolean checkSuccess(Dungeon dungeon) {
        return dungeon.getEntitiesOfType("exit").stream().anyMatch(e -> dungeon.entityCollisionCheck(e));
    }

    @Override
    public String remaining(Dungeon dungeon) {
        return "";
    }
    
}
