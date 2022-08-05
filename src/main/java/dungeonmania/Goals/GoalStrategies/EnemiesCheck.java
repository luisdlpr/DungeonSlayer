package dungeonmania.Goals.GoalStrategies;

import dungeonmania.Dungeon;
import dungeonmania.Entities.StaticEntities.ZombieToastSpawnerEntity;

public class EnemiesCheck extends GoalCheck {

    @Override
    public boolean checkSuccess(Dungeon dungeon) {
        return dungeon.getEntities().stream().anyMatch(e -> e instanceof ZombieToastSpawnerEntity) == false &&
        (int) dungeon.getConfigParam("enemy_goal") <= dungeon.getEnemiesKilled();
    }

    @Override
    public String remaining(Dungeon dungeon) {
        String str = " (";
        str += String.valueOf((int) dungeon.getConfigParam("enemy_goal") - (int) dungeon.getEnemiesKilled());
        str += ")";
        return str;
    }
}
