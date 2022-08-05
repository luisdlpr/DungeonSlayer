package dungeonmania.Buildables;

import java.util.ArrayList;
import java.util.List;

import dungeonmania.Entities.Player;
import dungeonmania.Entities.CollectableEntities.CollectableEntity;
import dungeonmania.response.models.ItemResponse;

public class Sceptre implements Buildable {

    private double duration;
    private List<CollectableEntity> materials = new ArrayList<>();
    private static final String type = "sceptre";
    private static final int wood = 1;
    private static final int arrow = 2;
    private static final int treasure = 1;
    private static final int key = 1;
    private String id = "";
    
    public Sceptre(double duration) {
        this.duration = duration;
    }

    public void build(Player player) {
        for (CollectableEntity e: materials) {
            player.removeFromInventory(e);
        }
        
        player.buildItem(this);
        // Create id after item is built
        id = type + String.valueOf(player.getNumberBuiltItems());
        player.increaseNumberBuilt();
    }

    public String getId() {
        return type;
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
        List<CollectableEntity> woods = player.getWoods();
        List<CollectableEntity> arrows = player.getArrows();
        List<CollectableEntity> keys = player.getKeys();
        List<CollectableEntity> sunstones = player.getSunStones();
        List<CollectableEntity> treasures = player.getTreasures();

        // 1 sceptre = (1 wood OR 2 arrows) + (1 key OR 1 treasure) + (1 sunstone)
        // 1 sceptre + 1 (retained) sunstone = (1 wood OR 2 arrows) + (1 sunstone) + (1 sunstone)
        boolean recipe1 = (woods.size() >= wood || arrows.size() >= arrow) &&
                            (keys.size() >= key || treasures.size() >= treasure) &&
                             sunstones.size() >= 1;
        boolean recipe2 = (woods.size() >= wood || arrows.size() >= arrow) && sunstones.size() >= 2;

        if (recipe1 || recipe2) {
            // Wood or arrows will be part of the recipe
            if (woods.size() >= wood) {
                materials.add(woods.get(0));
            } else {
                materials.add(arrows.get(0));
                materials.add(arrows.get(1));
            }
            // A sunstone will be used
            materials.add(sunstones.get(0));
        }

        if (recipe1) {
            // A key or treasure will be used
            materials.add(keys.size() >= key ? keys.get(0) : treasures.get(0));
        }

        return recipe1 || recipe2;
    }

    public double getDuration() {
        return duration;
    }

}
