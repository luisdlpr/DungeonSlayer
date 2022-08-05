package dungeonmania.Entities.StaticEntities;
import org.json.JSONObject;

import dungeonmania.Dungeon;
import dungeonmania.Entities.Player;
import dungeonmania.ObserverPatterns.DungeonSubject;
import dungeonmania.util.Position;

/**
 * Boulder entity class
 * @author Jordan Liang (z5254761)
 */

public class BoulderEntity extends StaticEntity {

    /**
     * construction with a JSONObject
     * @param object JSONObject specifying type, x, and y pos
     * @param id Integer unique id 
     */

    public BoulderEntity(JSONObject object, Integer id) {
        super(object, id, false);
    }

    /**
     * Pushes the boulder in the specified direction, checking if it is blocked by an entity in the
     * given entity list
     */

    public boolean pushBoulder(Position positionVector, Dungeon dungeon) {
        // Given the positionVector (direction) that the player pushed in
        // Move the boulder in that direction
        // There is an empty space or a switch in the newPos for the boulder
        Position boulderPos = this.getPosition();
        Position newPos = boulderPos.translateBy(positionVector);
        
        Boolean successful = true;
        Boolean wasOnSwitch = false;
        FloorSwitchEntity switchBoulderwasOn = null;

        if (dungeon.getEntityAtPosition("switch", newPos) != null) {
            // The boulder is being pushed onto a floorswitch
            successful = true;
            FloorSwitchEntity switchToTurnOn = (FloorSwitchEntity) dungeon.getEntityAtPosition("switch", newPos);
            switchToTurnOn.goActive();
        } else if (dungeon.getEntitiesAtPosition(newPos).size() == 0) {
            // Pushing onto empty square
            successful = true;
        } else {
            // The place the boulder is being pushed to is not a floorswitch
            // nor is it an empty square
            // Unsuccessful push
            successful = false;
        }

        if (dungeon.getSwitchAtPosition(boulderPos) != null) {
            // Boulder was already on a switch
            switchBoulderwasOn = dungeon.getSwitchAtPosition(boulderPos);
            wasOnSwitch = true;
        }

        if (successful) {
            // If the boulder was on a switch before moving, turn that switch off
            this.setPosition(newPos);
            if (wasOnSwitch) {
                switchBoulderwasOn.goInactive();
            }
        }
        return successful;
    }

    /**
     * Update boulder entity based on if a player is initiating a push
     */
    public void update(DungeonSubject obj) {
        Dungeon dungeon = (Dungeon) obj;
        Player player = dungeon.getPlayer();

        if (player.getCurrentPosition().equals(this.getPosition())) {
            // Get the direction (positionVector) the player is moving
            // Initialise a positionVector

            Position positionVector = Position.calculatePositionBetween(
                                                player.getLastPosition(), 
                                                player.getCurrentPosition());

            // Move the boulder
            if (!this.pushBoulder(positionVector, dungeon)) {
                player.goBack();
            }
        }

    }
    
}
