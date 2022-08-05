package dungeonmania.Buildables;

import dungeonmania.Entities.Player;
import dungeonmania.Entities.CollectableEntities.Weapons.Weapon;
import dungeonmania.response.models.ItemResponse;

public class MidnightArmour implements Buildable, Weapon {
    private static final String type = "midnight_armour";
    private static final double durability = Double.POSITIVE_INFINITY;
    private static final int sword = 1;
    private static final int sunstone = 1;
    private String id = "";
    private double attack;
    private double defence;
    private boolean hasZombies;

    public MidnightArmour(double attack, double defence, boolean hasZombies) {
        this.attack = attack;
        this.defence = defence;
        this.hasZombies = hasZombies;
    }

    public void build(Player player) {
        player.removeFromInventory(player.getSwords().get(0));
        player.removeFromInventory(player.getSunStones().get(0));

        player.buildItem(this);
        // Create id after item is built
        id = type + String.valueOf(player.getNumberBuiltItems());
        player.increaseNumberBuilt();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ItemResponse getItemResponse() {
        return new ItemResponse(id, type);
    }

    public String getType() {
        return type;
    }

    public Boolean playerHasMaterials(Player player) {
        return player.getSwords().size() >= sword && player.getSunStones().size() >= sunstone && !hasZombies;
    }

    public double getAttackDamage() {
        return attack;
    }

    public double getDefencePoints() {
        return defence;
    }

    public double getDurability() {
        return durability;
    }

    public void setDurability(double durability) {
        // Do nothing - armour durability is infinity
    }

    public void setHasZombies(boolean hasZombies) {
      this.hasZombies = hasZombies;
    }

}
