package dungeonmania.Buildables;

import java.util.List;

import dungeonmania.Entities.Player;
import dungeonmania.Entities.CollectableEntities.CollectableEntity;
import dungeonmania.Entities.CollectableEntities.Weapons.Weapon;
import dungeonmania.response.models.ItemResponse;

public class Shield implements Buildable, Weapon {  

    private static final double attackDamage = 0;
    private static final int wood = 2;
    private static final int treasure = 1;
    private static final int key = 1;
    private static final int sunstone = 1;
    private String type = "shield";
    private String id = "";
    private double durability;
    private double defence;

    public Shield(double durability, double defence) {
        this.durability = durability;
        this.defence = defence;
    }

    public Boolean playerHasMaterials(Player player) {
        int woods = player.getWoods().size();
        int treasures = player.getTreasureCount();
        int keys = player.getKeys().size();
        int sunstones = player.getSunStones().size();

        return woods >= wood && (treasures >= treasure || keys >= key || sunstones >= sunstone);         
    }

    /**
     * precondition: playerHasMaterials() = true
     */
    public void build(Player player) {
        List<CollectableEntity> woods = player.getWoods();
        List<CollectableEntity> treasures = player.getTreasures();
        List<CollectableEntity> keys = player.getKeys();
        
        // Remove treasure/key if it was used in crafting
        // From the forum: "sunstones will be retained in the crafting as long as it is used as a replacement."
        // So if sunstones are used in crafting shield, they are retained
        if (treasures.size() >= treasure) {
            player.removeFromInventory(treasures.get(0));
        } else if (keys.size() >= key) {
            player.removeFromInventory(keys.get(0));
        }

        for (int i = 0; i < wood; i++) {
            player.removeFromInventory(woods.get(i));
        }

        player.buildItem(this);

        // Create id after item is built
        id = type + String.valueOf(player.getNumberBuiltItems());
        player.increaseNumberBuilt();
    }
    
    public String getType() {
        return type;
    }

    public double getDurability() {
        return durability;
    }

    public void setDurability(double durability) {
    this.durability = durability;
    }

    /**
     * Return defence points for shield
     * @return defence points for the shield
     */
    public double getDefencePoints() {
        return defence;
    }

    /**
     * Return attack damage for shield
     * @return
     */
    public double getAttackDamage() {
        return attackDamage;
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

}
