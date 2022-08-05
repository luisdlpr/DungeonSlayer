package dungeonmania.Entities.MoveableEntities;

import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;

import dungeonmania.Dungeon;
import dungeonmania.Entities.Interactable;
import dungeonmania.Entities.Player;
import dungeonmania.Entities.CollectableEntities.CollectableEntity;
import dungeonmania.Entities.CollectableEntities.TreasureEntity;
import dungeonmania.Entities.MoveableEntities.MercenaryStates.BribedMercenaryState;
import dungeonmania.Entities.MoveableEntities.MercenaryStates.HostileMercenaryState;
import dungeonmania.Entities.MoveableEntities.MercenaryStates.MercenaryState;
import dungeonmania.Entities.StaticEntities.PortalEntity;
import dungeonmania.MovementBehaviours.HostileMercenaryMovement;
import dungeonmania.MovementBehaviours.MovementBehaviour;
import dungeonmania.MovementBehaviours.ZombieToastMovement;
import dungeonmania.MovementBehaviours.ZombieToastMovementInvincibility;
import dungeonmania.MovementBehaviours.BribedMercenaryMovement;
import dungeonmania.ObserverPatterns.DungeonSubject;
import dungeonmania.PlayerStates.InvincibleState;
import dungeonmania.PlayerStates.InvisibleState;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.util.Position;

/**
 * A mercenary entity (hostile/friendly to player)
 * @author Jordan Liang (z5254761)
 */

public class MercenaryEntity extends MoveableEntity implements Interactable {
    private MovementBehaviour movement;

    // State pattern
    MercenaryState BribedState;
    MercenaryState HostileState;

    // Movement strategies
    MovementBehaviour invisiblePlayer;
    MovementBehaviour hostileMercenary;
    MovementBehaviour bribedMercenary;
    MovementBehaviour invinciblePlayer;

    private MercenaryState state;
    private double mindControlCounter = 0;
    private int bribeAmount;
    private int bribeRadius;

    // constructor using JSON
    public MercenaryEntity(JSONObject object, Integer id, double health, double attackDamage, 
                           int bribeAmount, int bribeRadius) {
        super(object, id, health, attackDamage, true);

        this.bribeAmount = bribeAmount;
        this.bribeRadius = bribeRadius;
        
        BribedState = new BribedMercenaryState(this);
        HostileState = new HostileMercenaryState(this);
        invisiblePlayer = new ZombieToastMovement();
        invinciblePlayer = new ZombieToastMovementInvincibility();
        hostileMercenary = new HostileMercenaryMovement();
        bribedMercenary = new BribedMercenaryMovement();

        // By default, use HostileMercenary movement (Strategy) and be hostile (state)
        this.state = HostileState;
        this.movement = hostileMercenary;
    }

    /**
     * displaces the entity based on the given movement pattern.
     */
    public void move(DungeonSubject dungeon) {
        Position newPosition = movement.nextMove(this.getPosition(), dungeon);
        if (newPosition == null) {
            // Cannot move
            return;
        }

        this.setPosition(newPosition);     
    }

    /**
     * Attempts to bribe the mercenary. Returns true if successful, false if not
     */
    public void bribe(Player player) {
        List<CollectableEntity> treasures = player.getInventory().stream()
                                                  .filter(i -> i instanceof TreasureEntity)
                                                  .collect(Collectors.toList());

        for (int i = 0; i < this.getBribeAmount(); i++) {
            player.removeFromInventory(treasures.get(i));
        }
        this.goBribed();
    }

    /**
     * Start mind control for given duration
     * @param duration
     */
    public void mindControl(double duration) {
        mindControlCounter = duration;
        this.goBribed();
    }

    public double getMindControlCounter() {
        return mindControlCounter;
    }

    public void setMindControlCounter(double mindControlCounter) {
        this.mindControlCounter = mindControlCounter;
    }

    /**
     * Get the name of the state that mercenary is currently in
     */
    public String getName() {
        return state.getName();
    }

    /**
     * Get current movement behaviour
     */

    public MovementBehaviour getMovement() {
        return movement;
    }

    /**
     * Get bribe amount for mercenary
     */

    public int getBribeAmount() {
        return bribeAmount;
    }

    /**
     * Get bribe radius for mercenary
     */
    public int getBribeRadius() {
        return bribeRadius;
    }


    /**
     * Set movement behaviour
     */
    public void setMovement(MovementBehaviour movement) {
        this.movement = movement;
    }


    /**
     * Get current mercenary state
     */

    public MercenaryState getState() {
        return state;
    }

    /**
     * Set mercenary state
     */

    public void setState(MercenaryState state) {
        this.state = state;
    }

    /**
     * Tell the mercenary to become bribed
     * After becoming bribed, he will no longer be interactable
     */

    public void goBribed() {
        state.goBribed();
        this.setIsInteractable(false);
    }

    /**
     * Tell the mercenary to go hostile
     */

    public void goHostile() {
        state.goHostile();
    }

    /**
     * Getters for the different states
     */

    public MercenaryState getBribedState() {
        return BribedState;
    }

    public MercenaryState getHostileState() {
        return HostileState;
    }

    /**
     * Updates the mercenary in the subject. Moves entity and then checks for battle conditions.
     */
    @Override
    public void update(DungeonSubject obj) {
        // Check the player state and update the mercenary state accordingly
        Dungeon dungeon = (Dungeon) obj;
        Player player = dungeon.getPlayer();
        
        if (player.getState() instanceof InvisibleState) {
            // Player is not visible
            // Set movement to zombie movement (invisiblePlayer)
            this.setMovement(invisiblePlayer);
        } else if (player.getState() instanceof InvincibleState) {
            this.setMovement(invinciblePlayer);
        } else if (this.state instanceof BribedMercenaryState) {
            this.setMovement(bribedMercenary);
        } else if (this.state instanceof HostileMercenaryState) {
            this.setMovement(hostileMercenary);
        }

        
        if (!this.isStuckInSwamp(dungeon)) {
            this.move(obj);
        }

        portalMovement(dungeon);

        // Update mind control
        updateMindControl();
    }

    public void portalMovement(Dungeon dungeon) {
        // Update position if mercenary walks on portal
        List<PortalEntity> portals = dungeon.getEntitiesOfType("portal").stream()
                                                                            .map(p -> (PortalEntity) p)
                                                                            .filter(p -> p.getPosition().equals(this.getPosition()))
                                                                            .collect(Collectors.toList());

        if (portals.size() > 0) {
            Position pastPosition = this.getPosition();
            Position newPosition = portals.get(0).portalRecursion(dungeon, getPosition());
            if (newPosition != null) this.setPosition(newPosition);
            else this.setPosition(pastPosition);
        }
        
    }
    
    public void updateMindControl() {
        if (mindControlCounter > 0) {
            mindControlCounter--;
            if (mindControlCounter == 0) {
                this.goHostile();
                setIsInteractable(true);
            }
        }
    }

    @Override
    public void interact(DungeonSubject obj) throws InvalidActionException {
        Dungeon dungeon = (Dungeon) obj;
        Player player = dungeon.getPlayer();
        double mercDistance = player.getCurrentPosition().getDistanceBetween(this.getPosition());

        if (dungeon.hasSceptre()) {
            this.mindControl(dungeon.getConfigParam("mind_control_duration"));
        } else {
            if (mercDistance > this.getBribeRadius()) {
                throw new InvalidActionException("Mercenary out of range");
            } else if (dungeon.getPlayer().getTreasureCount() < this.getBribeAmount() &&
                        !dungeon.hasSceptre()) {
                throw new InvalidActionException("Not enough treasure or not holding sceptre");
            }
            this.bribe(player);
        }
    }
}
