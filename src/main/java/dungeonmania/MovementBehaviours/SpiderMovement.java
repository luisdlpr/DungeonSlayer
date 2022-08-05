package dungeonmania.MovementBehaviours;

import java.util.List;

import dungeonmania.Dungeon;
import dungeonmania.ObserverPatterns.DungeonSubject;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

/**
 * class specifying movement behaviour of the spider entity
 * @author Luis Reyes (z5206766)
 */
public class SpiderMovement implements MovementBehaviour {
    private Position initialPosition;
    // stores circular movement pattern around spawn point
    private List<Position> circleMovement;
    // index reached in movement pattern list
    private Integer index = 1;
    private Integer direction = 1;

    /**
     * construct movement for spider around spawn point
     * @param initialPosition Position spawn point
     */
    public SpiderMovement(Position initialPosition) {
        this.initialPosition = initialPosition;
        this.circleMovement = initialPosition.getAdjacentPositions();
    }

    /**
     * calculate next move of spider
     */
    @Override
    public Position nextMove(Position currentPosition, DungeonSubject dungeon) {
        // if spider hasnt moved from spawn, go up
        if (currentPosition.equals(initialPosition)) {
            return initialPosition.translateBy(Direction.UP);
        // increment index and go to position in list
        } else {
            this.index += this.direction;
            // if reached end of list start again
            if (this.index.equals(8)) {
                this.index = 0;
            } else if (this.index <= -1) {
                this.index = 7;
            }
            Position newPosition = circleMovement.get(this.index);
            Dungeon typedDungeon = (Dungeon) dungeon;
            if (typedDungeon.getEntitiesAtPosition(newPosition).size() != 0 &&
             typedDungeon.getEntityAtPosition("boulder", newPosition) != null) {
                this.direction *= -1;
                this.index += 2*this.direction;
                if (this.index.equals(8)) {
                    this.index = 0;
                } else if (this.index <= -1) {
                    this.index = 7;
                }
                newPosition = circleMovement.get(this.index);
            }

            return newPosition;
        }
    }
}
