package dungeonmania.Entities.CollectableEntities.Weapons;

import dungeonmania.response.models.ItemResponse;

/**
 * Weapon interface for sword, bow and shield
 * @author Nancy Huynh (z5257042)
 */
public interface Weapon {
    public double getDurability();
    public void setDurability(double durability);
    public double getDefencePoints();
    public double getAttackDamage();
    public ItemResponse getItemResponse();
}