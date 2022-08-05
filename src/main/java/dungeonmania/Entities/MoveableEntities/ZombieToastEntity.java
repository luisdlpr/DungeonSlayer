package dungeonmania.Entities.MoveableEntities;

import org.json.JSONObject;

import dungeonmania.Dungeon;
import dungeonmania.MovementBehaviours.MovementBehaviour;
import dungeonmania.MovementBehaviours.ZombieToastMovement;
import dungeonmania.MovementBehaviours.ZombieToastMovementInvincibility;
import dungeonmania.ObserverPatterns.DungeonSubject;
import dungeonmania.util.Position;

/**
 * ZombieToastEntity with health and attack parameters according to config, can be spawned by Toasters or created in dungeon map. 
 * @author Stan Korotun (z5637728)
 */

public class ZombieToastEntity extends MoveableEntity{

    private MovementBehaviour movement = new ZombieToastMovement();

    public ZombieToastEntity(String id, String type, Position position, boolean isInteractable, double health,
            double attackDamage) {
            super(id, type, position, isInteractable, health, attackDamage);
    }

    public ZombieToastEntity(JSONObject object, Integer id, double health, double attackDamage) {
        super(object, id, health, attackDamage, false);
    }

    @Override
    public void move(DungeonSubject dungeon) {
        Position newPosition = movement.nextMove(this.getPosition(), dungeon);
        this.setPosition(newPosition);   
    }

    @Override
    public void update(DungeonSubject obj) {
        Dungeon dungeon = (Dungeon) obj;
        if (dungeon.getPlayer().getState() == dungeon.getPlayer().getInvincibleState()) {
            movement = new ZombieToastMovementInvincibility();
        }
        else {
            movement = new ZombieToastMovement();
        }
        
        if (!this.isStuckInSwamp(dungeon)) {
            this.move(dungeon);
        }
    }
    
}
