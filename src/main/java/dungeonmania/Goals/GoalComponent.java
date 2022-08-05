package dungeonmania.Goals;

import dungeonmania.Dungeon;

/**
 * Goal component interface for composite pattern goal objects
 * @author Nancy Huynh (z5257042)
 */
public interface GoalComponent {
    public Boolean checkSuccess(Dungeon dungeon);
    public String getGoal(Dungeon dungeon);
    public Boolean isComplex();
}