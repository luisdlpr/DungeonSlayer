package dungeonmania.Goals;

import org.json.JSONObject;

import dungeonmania.Dungeon;
import dungeonmania.Goals.GoalStrategies.BoulderCheck;
import dungeonmania.Goals.GoalStrategies.EnemiesCheck;
import dungeonmania.Goals.GoalStrategies.ExitCheck;
import dungeonmania.Goals.GoalStrategies.GoalCheck;
import dungeonmania.Goals.GoalStrategies.TreasureCheck;

/**
 * Basic goal class
 * @author Nancy Huynh (z5257042)
 */
public class BasicGoal implements GoalComponent {
    private String goal;
    private GoalCheck checkingStrategy;

    public BasicGoal(JSONObject goal) {
        this.goal = goal.getString("goal");
        switch(this.goal) {
            case "exit": checkingStrategy = new ExitCheck(); break;
            case "boulders": checkingStrategy = new BoulderCheck(); break;
            case "treasure": checkingStrategy = new TreasureCheck(); break;
            case "enemies": checkingStrategy = new EnemiesCheck(); break;
        }
    }

    public Boolean checkSuccess(Dungeon dungeon) {
        if (this.checkingStrategy == null) return false;
        return this.checkingStrategy.checkSuccess(dungeon);
    }

    public String getGoal(Dungeon dungeon) {
        return this.checkSuccess(dungeon) ? "" : ":" + goal + this.checkingStrategy.remaining(dungeon);
    }

    public Boolean isComplex() {
        return false;
    }
}
