package dungeonmania.Goals.GoalStrategies;

import dungeonmania.Dungeon;

public class TreasureCheck extends GoalCheck {

    @Override
    public boolean checkSuccess(Dungeon dungeon) {
        return dungeon.getPlayer().getTreasureGoalCount() >= (int) dungeon.getConfigParam("treasure_goal");
    }

    @Override
    public String remaining(Dungeon dungeon) {
        String str = " (";
        str += String.valueOf((int) dungeon.getConfigParam("treasure_goal") - (int) dungeon.getPlayer().getTreasureGoalCount());
        str += ")";
        return str;
    }
    
}
