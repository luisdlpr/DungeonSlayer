package dungeonmania.Buildables;

import java.util.List;

import dungeonmania.Entities.Player;
import dungeonmania.Entities.CollectableEntities.CollectableEntity;
import dungeonmania.Entities.CollectableEntities.Weapons.Weapon;
import dungeonmania.response.models.ItemResponse;

public class Bow implements Buildable, Weapon {

    private static final double defencePoints = 0;
    private static final double attackDamage = 2;    
    private static final String type = "bow";
    private static final int wood = 1;
    private static final int arrow = 3;
    private String id = "";
    private double durability;

    public Bow(double durability) {
        this.durability = durability;
    }
    
    public Boolean playerHasMaterials(Player player) {
        int woods = player.getWoods().size();
        int arrows = player.getArrows().size();

        return woods >= wood && arrows >= arrow;         
    }

    public void build(Player player) {
        List<CollectableEntity> woods = player.getWoods();
        List<CollectableEntity> arrows = player.getArrows();
        
        for (int i = 0; i < arrow; i++) {
            player.removeFromInventory(arrows.get(i));
        }

        player.removeFromInventory(woods.get(0));
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
     * Return defence points for bow
     * @return
     */
    public double getDefencePoints() {
        return defencePoints;
    }

    /**
     * Return attack damage for bow
     * @return attack damage for bow not accounting for player attack damage
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
