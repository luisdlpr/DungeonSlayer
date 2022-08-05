package dungeonmania.Goals.GoalStrategies;

import java.util.stream.Collectors;

import dungeonmania.Dungeon;
import dungeonmania.Entities.StaticEntities.FloorSwitchEntity;

public class BoulderCheck extends GoalCheck {

    @Override
    public boolean checkSuccess(Dungeon dungeon) {
        return dungeon.getEntitiesOfType("switch").stream().map(e -> (FloorSwitchEntity) e).allMatch(s -> s.getState().equals(s.getActiveState()));
    }

    @Override
    public String remaining(Dungeon dungeon) {
        int numInactiveSwitches = dungeon.getEntitiesOfType("switch").stream()
                                                                        .map(e -> (FloorSwitchEntity) e)
                                                                        .filter(e -> e.getState().equals(e.getInactiveState()))
                                                                        .collect(Collectors.toList())
                                                                        .size();
        return " (" + String.valueOf(numInactiveSwitches) + ")";
    }
    
}
