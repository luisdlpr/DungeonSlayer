package dungeonmania.MovementBehaviours;

import dungeonmania.ObserverPatterns.DungeonSubject;
import dungeonmania.util.Position;

/**
 * Interface for movement behaviour of specific movingEntities
 * @author Luis Reyes (z5206766)
 */
public interface MovementBehaviour {
    /**
     * generate the next move based on current position
     * @param currentPosition Position current position of entity
     * @return Position where the entity should move next
     */
    public Position nextMove(Position currentPosition, DungeonSubject dungeon);
}
