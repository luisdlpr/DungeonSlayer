package dungeonmania.Entities.MoveableEntities;

import org.json.JSONObject;

import dungeonmania.Dungeon;
import dungeonmania.MovementBehaviours.MovementBehaviour;
import dungeonmania.MovementBehaviours.SpiderMovement;
import dungeonmania.ObserverPatterns.DungeonSubject;
import dungeonmania.util.Position;

/**
 * A spider entity (hostile to player) as described in spec
 * @author Luis Reyes (z5206766) & 
 */
public class SpiderEntity extends MoveableEntity {
    private Position initialPosition;
    private MovementBehaviour movement;

    // constructor using JSON
    public SpiderEntity(JSONObject object, Integer id, double health, double attackDamage) {
        super(object, id, health, attackDamage, false);
        // movement defined around initial position
        this.initialPosition = this.getPosition();
        // use spider movement (strategy pattern)
        this.movement = new SpiderMovement(initialPosition);
    }

    /**
     * displaces the entity based on the given movement pattern.
     */
    public void move(DungeonSubject dungeon) {
        Position newPosition = movement.nextMove(this.getPosition(), dungeon);
        this.setPosition(newPosition);     
    }

    /**
     * Updates the spider in the subject. Moves entity and then checks for battle conditions.
     */
    @Override
    public void update(DungeonSubject obj) {
        Dungeon dungeon = (Dungeon) obj;
        if (!this.isStuckInSwamp(dungeon)) {
            this.move(obj);
        }
    }
    
}
