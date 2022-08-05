package dungeonmania.Entities.StaticEntities;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;

import dungeonmania.Dungeon;
import dungeonmania.Entities.Entity;
import dungeonmania.Entities.Player;
import dungeonmania.ObserverPatterns.DungeonSubject;
import dungeonmania.util.Position;

/**
 * Portal entity class with method to update player position after walking on a portal
 * @author Nancy Huynh (z5257042)
 */
public class PortalEntity extends StaticEntity {
    private String colour;
    private PortalEntity other = null;
    private static final List<String> blocks = Arrays.asList(new String[]{"boulder", "zombie_toast_spawner", "door", "wall"});
    
    /**
     * Portal construction with a JSONObject
     * @param object JSONObject specifying type, x, and y pos
     * @param id unique id
     * @param colour colour of portal as string
     */
    public PortalEntity(JSONObject object, Integer id, String colour) {
        super(object, id, false);
        this.colour = colour;
    }

    /**
     * Update position of player when they walk on portal
     */
    public void update(DungeonSubject obj) {
        Dungeon dungeon = (Dungeon) obj;
        Player player = dungeon.getPlayer();

        // If player walks on portal, perform portal recursion to find new position
        if (dungeon.entityCollisionCheck(this)) {
            Position newPosition = portalRecursion(dungeon, getPosition());
            if (newPosition != null) player.setCurrentPosition(newPosition);
            else player.goBack();
        };
    }

    /**
     * Return position of player after going through portal
     * @param dungeon
     * @param position position that player is moving to
     * @return new position of player or null if player stays where they are
     */
    public Position portalRecursion(Dungeon dungeon, Position position) {
        List<Entity> atPosition = dungeon.getEntitiesAtPosition(position);
        List<PortalEntity> portalsAtPosition = atPosition.stream()
                                                            .filter(e -> e instanceof PortalEntity)
                                                            .map(e -> (PortalEntity) e)
                                                            .collect(Collectors.toList());

        // Continue recursion if player teleports onto another portal
        if (portalsAtPosition.size() > 0) {
            Position newPosition = getTeleportedPosition(dungeon, portalsAtPosition.get(0));
            if (newPosition != null) return portalRecursion(dungeon, newPosition);
        } else { // Return teleported position if not on portal
            return position;
        }
        
        return null; // Return null if no new position
    }

    /**
     * Return valid position to teleport to given the portal the player walks through
     * @param dungeon
     * @param portal portal object taht the player walks on
     * @return valid position for player to teleport to at corresponding portal
     */
    private Position getTeleportedPosition(Dungeon dungeon, PortalEntity portal) {
        List<Position> adjacent = portal.other.getPosition().getCardinallyAdjacentPositions();
        List<Position> available = adjacent.stream()
                                            .filter(p -> dungeon.getEntitiesAtPosition(p).size() == 0 || checkBlocksInPosition(dungeon.getEntitiesAtPosition(p)))
                                            .collect(Collectors.toList());
        if (available.size() != 0) return available.get(0);
        return null;
    }

    /**
     * Given a list of entities in a position, check if any spider blocks exist there
     * @param entitiesInPosition
     * @return if the
     */
    private boolean checkBlocksInPosition(List<Entity> entitiesInPosition) {
        return entitiesInPosition.stream().anyMatch(e -> blocks.contains(e.getType())) == false;
    }

    public String getColour() {
        return colour;
    }

    public PortalEntity getOtherPortal() {
        return other;
    }

    public void setOtherPortal(PortalEntity other) {
        this.other = other;
    }

}
