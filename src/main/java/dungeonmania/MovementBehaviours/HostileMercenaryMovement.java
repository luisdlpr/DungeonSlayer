package dungeonmania.MovementBehaviours;
import dungeonmania.Dungeon;
import dungeonmania.ObserverPatterns.DungeonSubject;
import dungeonmania.util.Position;

/**
 * Class implementing for hostile mercenary behaviour
 * @author Jordan Liang (z5254761)
 */

public class HostileMercenaryMovement extends MercenaryMovement implements MovementBehaviour {
    
    @Override
    public Position nextMove(Position currentPosition, DungeonSubject dungeonSubject) {
        Dungeon dm = (Dungeon) dungeonSubject;
        Position playerPos = dm.getPlayerPosition();

        if (currentPosition.equals(playerPos)) {
            return null;
        }
        return pathToPlayer(currentPosition, playerPos, dm);
    }
}
