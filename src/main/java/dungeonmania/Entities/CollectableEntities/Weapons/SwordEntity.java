package dungeonmania.Entities.CollectableEntities.Weapons;

import org.json.JSONObject;

import dungeonmania.Entities.CollectableEntities.CollectableEntity;
import dungeonmania.util.Position;

/**
 * Sword entity class
 * @author Nancy Huynh (z5257042)
 */
public class SwordEntity extends CollectableEntity implements Weapon {

    private static final double defencePoints = 0;
    private double attackDamage;
    private double durability;


    public SwordEntity(String id, String type, Position position, boolean isInteractable) {
        super(id, type, position, isInteractable);
    }
    
    public SwordEntity(JSONObject object, Integer id, double attackDamage, double durability) {
        super(object, id, false);
        this.attackDamage = attackDamage;
        this.durability = durability;
    }

    public double getDurability() {
        return durability;
    }

    public void setDurability(double durability) {
        this.durability = durability;
    }

    /**
     * Return defence points for sword
     * @return
     */
    public double getDefencePoints() {
        return defencePoints;
    }

    /**
     * Return attack damage for sword
     * @return sword attack damage
     */
    public double getAttackDamage() {
        return attackDamage;
    }

}
