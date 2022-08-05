package dungeonmania.MovementBehaviours;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dungeonmania.Dungeon;
import dungeonmania.Entities.Entity;
import dungeonmania.Entities.StaticEntities.DoorEntity;
import dungeonmania.ObserverPatterns.DungeonSubject;
import dungeonmania.util.Position;

/**
 * Responsible for usual zombie movement, when the player is not under the effect of an invincibility potion. 
 * @author Stan Korotun (z5637728)
 */

public class ZombieToastMovement implements MovementBehaviour{

    @Override
    public Position nextMove(Position currentPosition, DungeonSubject dungeonSubject) {
        
        Dungeon dungeon = (Dungeon)dungeonSubject;

        List<Position> cardinallyAdjacentPositions = getNextPositions(currentPosition, dungeon);

        if (cardinallyAdjacentPositions.size() == 0) {
            return currentPosition;
        }

        Random rand = new Random();
        int num = rand.nextInt(cardinallyAdjacentPositions.size());
        Position possiblePosition = cardinallyAdjacentPositions.get(num);
        return possiblePosition;
    }

    private List<Position> getNextPositions(Position current, Dungeon dungeon) {
        List<Position> neighbours = current.getCardinallyAdjacentPositions();
        List<Position> tilesToRemove = new ArrayList<Position>();

        for (Position tile : neighbours) {
            if ((dungeon.getEntityAtPosition("wall", tile) != null ||
                dungeon.getEntityAtPosition("boulder", tile) != null ||
                dungeon.getEntityAtPosition("door", tile) != null ||
                checkIfLockedDoor(dungeon.getEntityAtPosition("door", tile))
                )){
                tilesToRemove.add(tile);
            }
        }


        neighbours.removeAll(tilesToRemove);

        return neighbours;
    }

    // If not a door, return false
    // If a door, return true if locked, else false
    private Boolean checkIfLockedDoor(Entity entity) {
        if (entity instanceof DoorEntity) {
            DoorEntity door = (DoorEntity) entity;
            return !door.getIsUnlocked();
        } else {
            return false;
        }
    }

    
}
