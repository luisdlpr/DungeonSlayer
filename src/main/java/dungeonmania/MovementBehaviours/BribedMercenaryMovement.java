package dungeonmania.MovementBehaviours;
import dungeonmania.Dungeon;
import dungeonmania.ObserverPatterns.DungeonSubject;
import dungeonmania.util.Position;

/**
 * Class implementing for bribed mercenary behaviour
 * @author Jordan Liang (z5254761)
 */

public class BribedMercenaryMovement extends MercenaryMovement implements MovementBehaviour {

    @Override
    public Position nextMove(Position currentPosition, DungeonSubject dungeonSubject) {
        Dungeon dm = (Dungeon) dungeonSubject;
        Position playerPos = dm.getPlayer().getLastPosition();

        if (currentPosition.equals(playerPos)) {
            return null;
        }

        return pathToPlayer(currentPosition, playerPos, dm);
    }
}
