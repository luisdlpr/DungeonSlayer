package dungeonmania.Entities.MoveableEntities;

import org.json.JSONObject;

import dungeonmania.Dungeon;
import dungeonmania.Entities.Entity;
import dungeonmania.Entities.StaticEntities.SwampTileEntity;
import dungeonmania.ObserverPatterns.DungeonSubject;
import dungeonmania.util.Position;

/**
 * A entity that changes its position on the dungeon regardless of player interaction.
 * @author Luis Reyes (z5206766) & Jordan Liang (z5254761)
 */
public abstract class MoveableEntity extends Entity {
    private double health;
    private double attackDamage;
    private int tickCounter;

    /**
     * manual construction
     * @param id String for unique id
     * @param type String for entity type
     * @param position Position current position in dungeon co-ordinate system
     * @param isInteractable boolean has interaction functionality
     */
    public MoveableEntity(String id, String type, Position position, boolean isInteractable, double health, double attackDamage) {
        super(id, type, position, isInteractable);
        this.health = health;
        this.attackDamage = attackDamage;
        this.tickCounter = 0;
    }

    /**
     * construction with a JSONObject
     * @param object JSONObject specifying type, x, and y pos
     * @param id Integer unique id 
     * @param isInteractable boolean has interaction functionality
     */
    public MoveableEntity(JSONObject object, Integer id, double health, double attackDamage, boolean isInteractable) {
        super(object, id, isInteractable);
        this.health = health;
        this.attackDamage = attackDamage;
    }

    public abstract void move(DungeonSubject dungeon);

    // update subject (observer pattern)
    public abstract void update(DungeonSubject dungeon);

    /**
     * @return double return the health
     */
    public double getHealth() {
        return health;
    }

    /**
     * @param health the health to set
     */
    public void setHealth(double health) {
        this.health = health;
    }

    /**
     * @return double return the attackDamage
     */
    public double getAttackDamage() {
        return attackDamage;
    }

    /**
     * @param attackDamage the attackDamage to set
     */
    public void setAttackDamage(double attackDamage) {
        this.attackDamage = attackDamage;
    }

    public int getTickCounter() {
        return tickCounter;
    }

    public void setTickCounter(int tickCounter) {
        this.tickCounter = tickCounter;
    }

    public void incrementTickCounter() {
        this.tickCounter = tickCounter + 1;
    }
    
    public boolean isStuckInSwamp(Dungeon dungeon) {
        if ((dungeon.getEntityAtPosition("swamp_tile", this.getPosition()) != null)) {
            SwampTileEntity swampTile = (SwampTileEntity) dungeon.getEntityAtPosition("swamp_tile", this.getPosition());

            if (this.getTickCounter() == swampTile.getMovementFactor()) {
                this.setTickCounter(0);
                return false;
            } else {
                this.incrementTickCounter();
                return true;
            }

        } 
        return false;
    }
}
