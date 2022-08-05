package dungeonmania.Entities.StaticEntities;

import java.util.List;
import java.util.Random;

import org.json.JSONObject;

import dungeonmania.Dungeon;
import dungeonmania.Entities.Interactable;
import dungeonmania.Entities.Player;
import dungeonmania.Entities.MoveableEntities.ZombieToastEntity;
import dungeonmania.ObserverPatterns.DungeonSubject;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.util.Position;

/**
 * Static entity in position according to config, responsible for generating valid spawning positions and creating ZombieToastEntities. 
 * @author Stan Korotun (z5637728)
 */

public class ZombieToastSpawnerEntity extends StaticEntity implements Interactable{

    private int spawnerTickCounter = 1;

    public ZombieToastSpawnerEntity(JSONObject object, Integer id, boolean isInteractable) {
        super(object, id, true);
    }

    @Override
    public void update(DungeonSubject obj) {
        Dungeon dungeon = (Dungeon)obj;
        
        if (spawnerTickCounter == dungeon.getConfigParam("zombie_spawn_rate")) {
            int id = dungeon.getNumZombiesSpawned();
            id += 1;
            if (generateZombieSpawnPosition(dungeon) != null) {
                dungeon.setNumZombiesSpawned(id);
                dungeon.addObserver(new ZombieToastEntity("zombie_toast"+id, "zombie_toast", generateZombieSpawnPosition(dungeon), false, dungeon.getConfigParam("zombie_health"), dungeon.getConfigParam("zombie_attack")));
            }
            
            spawnerTickCounter = 1;
            return;
        }
        spawnerTickCounter+=1;
    }
    
    private Position generateZombieSpawnPosition(Dungeon dungeon) {
        
        List<Position> cardinallyAdjacentPositions = getPosition().getCardinallyAdjacentPositions();

        Random rand = new Random();
        while (cardinallyAdjacentPositions.size() > 0) {
            int num = rand.nextInt(cardinallyAdjacentPositions.size());
            Position possiblePosition = cardinallyAdjacentPositions.get(num);
            if (dungeon.getEntitiesAtPosition(possiblePosition).size() == 0) {
                return possiblePosition;
            } else {
                cardinallyAdjacentPositions.remove(possiblePosition);
            }
        }
        
        return null;
    }

    @Override
    public void interact(DungeonSubject obj) throws InvalidActionException {
        Dungeon dungeon = (Dungeon) obj;
        Player player = dungeon.getPlayer();
        if (dungeon.playerHasSword() && 
            this.getPosition().getCardinallyAdjacentPositions().contains(player.getCurrentPosition())) 
            {
            dungeon.removeObserver(this);
        } else {
            throw new InvalidActionException(
                        "Zombie toast spawners can only be destroyed by a cardinally adjacent player with a sword!");
        }
        
    }
}
