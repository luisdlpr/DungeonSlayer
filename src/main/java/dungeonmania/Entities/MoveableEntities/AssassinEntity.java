package dungeonmania.Entities.MoveableEntities;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.json.JSONObject;

import dungeonmania.Dungeon;
import dungeonmania.Entities.Player;
import dungeonmania.Entities.CollectableEntities.CollectableEntity;
import dungeonmania.Entities.CollectableEntities.TreasureEntity;
import dungeonmania.Entities.MoveableEntities.MercenaryStates.BribedMercenaryState;
import dungeonmania.Entities.MoveableEntities.MercenaryStates.HostileMercenaryState;
import dungeonmania.ObserverPatterns.DungeonSubject;
import dungeonmania.PlayerStates.InvincibleState;
import dungeonmania.PlayerStates.InvisibleState;
import dungeonmania.exceptions.InvalidActionException;

/**
 * An assassin entity that is based on the implementation of mercenary with relevant changes made such as bribing and overwritten update
 * method to accomodate for recon radius checking.
 * @author Stan Korotun (z5367728) 
 */

public class AssassinEntity extends MercenaryEntity {

    private double assassinBribeFailRate;
    private int assassinReconRadius;

    public AssassinEntity(JSONObject object, Integer id, double health, double attackDamage,
                         int assassinBribeAmount, int bribeRadius,
                         double assassinBribeFailRate, int assassinReconRadius) {
        super(object, id, health, attackDamage, assassinBribeAmount, bribeRadius);
        this.assassinBribeFailRate = assassinBribeFailRate;
        this.assassinReconRadius = assassinReconRadius;
    }

    /**
     * Updates the mercenary in the subject. Moves entity and then checks for battle conditions.
     */
    @Override
    public void update(DungeonSubject obj) {
        Dungeon dungeon = (Dungeon) obj;
        Player player = dungeon.getPlayer();
        
        if (player.getState() instanceof InvisibleState && player.getCurrentPosition().getDistanceBetween(this.getPosition()) > assassinReconRadius) {
            // Player is not visible
            this.setMovement(invisiblePlayer);
        } else if (player.getState() instanceof InvincibleState) {
            this.setMovement(invinciblePlayer);
        } else if (this.getState() instanceof BribedMercenaryState) {
            this.setMovement(bribedMercenary);
        } else if (this.getState() instanceof HostileMercenaryState) {
            this.setMovement(hostileMercenary);
        }
        
        if (!this.isStuckInSwamp(dungeon)) {
            this.move(obj);
        }
        portalMovement(dungeon);
        updateMindControl();
    }

    @Override
    public void bribe(Player player) {
        List<CollectableEntity> treasures = player.getInventory().stream()
                                                  .filter(i -> i instanceof TreasureEntity)
                                                  .collect(Collectors.toList());

        for (int i = 0; i < this.getBribeAmount(); i++) {
            player.removeFromInventory(treasures.get(i));
        }

        //apply the chance of bribe falilure
        if (assassinBribeFailRate == 1) {
            //this.goHostile();
            return;
        }
        if (assassinBribeFailRate == 0) {
            this.goBribed();
            return;
        }
        Random rand = new Random();
        double randValue = rand.nextDouble();
        if (assassinBribeFailRate > randValue) {   
            this.goBribed();
        }
        //if bribe is successful, goBribed
        

    }

    @Override
    public void interact(DungeonSubject obj) throws InvalidActionException {
        Dungeon dungeon = (Dungeon) obj;
        Player player = dungeon.getPlayer();
        double mercDistance = player.getCurrentPosition().getDistanceBetween(this.getPosition());

        // Mind control with sceptre if player has sceptre
        if (dungeon.hasSceptre()) {
            this.mindControl(dungeon.getConfigParam("mind_control_duration"));
        } else {
            if (mercDistance > this.getBribeRadius()) {
                throw new InvalidActionException("Assassin out of range");
            } else if (player.getTreasureCount() < this.getBribeAmount() &&
                        !dungeon.hasSceptre()) {
                throw new InvalidActionException("Not enough treasure or not holding sceptre");
            }
            this.bribe(player);
        }
    }
}
