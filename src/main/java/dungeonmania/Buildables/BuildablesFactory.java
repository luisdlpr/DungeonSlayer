package dungeonmania.Buildables;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dungeonmania.Dungeon;
import dungeonmania.Entities.Player;

/**
 * Buildable factory for a given dungeon
 * @author Nancy Huynh (z5257042)
 */
public class BuildablesFactory {

    Dungeon dungeon;
    Player player;
    List<Buildable> buildables = new ArrayList<>();

    /**
     * Constructor the buildable factory for a dungeon
     * Add new buildable items types here
     * @param dungeon
     */
    public BuildablesFactory(Dungeon dungeon) {
        this.dungeon = dungeon;
        this.player = dungeon.getPlayer();

        buildables.add(new Bow(dungeon.getConfigParam("bow_durability")));
        buildables.add(new Shield(dungeon.getConfigParam("shield_durability"), dungeon.getConfigParam("shield_defence")));

        // Milestone 3 buildables: config values may not exist
        if (dungeon.getConfig().has("mind_control_duration")) {
            buildables.add(new Sceptre(dungeon.getConfigParam("mind_control_duration")));
        }
        
        if (dungeon.getConfig().has("midnight_armour_attack")) {
            buildables.add(new MidnightArmour(dungeon.getConfigParam("midnight_armour_attack"),
                                                dungeon.getConfigParam("midnight_armour_defence"),
                                                dungeon.getEntitiesOfType("zombie_toast").size() != 0));
        }
    }

    /**
     * Build the given item
     * precondition: isBuildable = true, playerCanBuild = true
     * @param type
     * @return The item built
     */
    public Buildable buildItem(String type) {
        Buildable buildable = buildables.stream().filter(b -> b.getType().equals(type)).findFirst().orElse(null);
        buildable.build(player);
        return buildable;
    }

    /**
     * Check if given type is a buildable item type
     * @param type
     * @return if the buildable is valid
     */
    public boolean isBuildable(String type) {
        return buildables.stream().anyMatch(b -> b.getType().equals(type));
    }

    /**
     * Check if player has materials to build item
     * precondition: isBuildable = true
     * @param type
     * @return if player can build item currently
     */
    public boolean playerCanBuild(String type) {
        Buildable buildable = buildables.stream().filter(b -> b.getType().equals(type)).findFirst().orElse(null);
        return buildable.playerHasMaterials(player);
    }

    /**
     * @return list of type of buildables that the player can currently build
     */
    public List<String> getBuildables() {
        return buildables.stream()
                            .filter(b -> b.playerHasMaterials(player))
                            .map(b -> b.getType()).collect(Collectors.toList());
    }
}
