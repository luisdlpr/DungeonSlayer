package dungeonmania.Entities.CollectableEntities;

import org.json.JSONObject;

import dungeonmania.Dungeon;
import dungeonmania.Entities.Player;
import dungeonmania.Entities.StaticEntities.PlacedBombEntity;
import dungeonmania.ObserverPatterns.DungeonSubject;

/**
 * The bomb before it it can be detonated, this class handles picking bomb up, adding to inventory and removing from static entities
 * When the bomb is a placed, that instance is removed and a new class is instantiated of type PlacedBombEntity. That class handles
 * the detonation of bomb, more detail in the class itself.
 * @author Stan Korotun (z5637728)
 */

public class UnplacedBombEntity extends CollectableEntity{

    public UnplacedBombEntity(JSONObject object, Integer id, boolean isInteractable) {
        super(object, id, false);
    }

    @Override
    public void update(DungeonSubject obj) {
        Dungeon dungeon = (Dungeon) obj;
        Player player = dungeon.getPlayer();

        if (player.getCurrentPosition().equals(this.getPosition())) {
            player.addToInventory(this);
            dungeon.removeObserver(this);

        }
        
    }

    public PlacedBombEntity place(DungeonSubject obj) {
        Dungeon dungeon = (Dungeon) obj;
        Player player = dungeon.getPlayer();
        
        PlacedBombEntity bombPlaced = new PlacedBombEntity(getId(), getType(), player.getCurrentPosition(), false, obj);
        player.removeFromInventory(this);
        return bombPlaced;
    }
}
