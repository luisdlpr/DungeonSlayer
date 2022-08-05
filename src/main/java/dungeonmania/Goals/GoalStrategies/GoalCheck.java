package dungeonmania.Goals.GoalStrategies;

import dungeonmania.Dungeon;

/**
 * Strategy Pattern Template for goal types
 * @author Luis Reyes (z5206766)
 */
public abstract class GoalCheck {
    public abstract boolean checkSuccess(Dungeon dungeon);
    public abstract String remaining(Dungeon dungeon);
}
