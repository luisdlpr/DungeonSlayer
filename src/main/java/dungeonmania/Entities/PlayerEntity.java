package dungeonmania.Entities;

import java.util.Queue;

import org.json.JSONArray;
import org.json.JSONObject;

import dungeonmania.Dungeon;
import dungeonmania.Entities.CollectableEntities.CollectableEntity;
import dungeonmania.Entities.MoveableEntities.MoveableEntity;
import dungeonmania.ObserverPatterns.DungeonSubject;
import dungeonmania.util.Position;

public class PlayerEntity extends MoveableEntity {
    private Queue<JSONObject> states;
    private JSONObject currState;

    public PlayerEntity(JSONObject object, Integer id, double health, double attackDamage) {
        super(object, id, health, attackDamage, false);
    }

    public PlayerEntity(Player player, Integer id) {
        super("older_player" + id, "older_player", player.getCurrentPosition(), false,
                player.getHealth(), player.getAttackDamage());
    }

    public void move(DungeonSubject dungeon) {}

    public void update(DungeonSubject obj) {
        Dungeon dungeon = (Dungeon) obj;

        // Update position on tick
        if (states.size() != 0) {
            currState = states.poll();
            Position pos = new Position(currState.getJSONObject("player").getInt("curr_position_x"),
                            currState.getJSONObject("player").getInt("curr_position_y"));
            setPosition(pos);
        } else {
            dungeon.removeObserver(this);
        }

        // Remove collectables from map if older player walks on it
        Entity collectable = dungeon.getEntitiesAtPosition(getPosition()).stream()
                                                                            .filter(e -> e instanceof CollectableEntity)
                                                                            .findFirst()
                                                                            .orElse(null);
        if (collectable != null) {
            dungeon.removeObserver(collectable);
        }
    }

    public void setStates(Queue<JSONObject> states) {
        this.states = states;
        states.poll();
    }
    
    public boolean canBattle() {
        JSONArray collectables = currState.getJSONObject("inventory").getJSONArray("collectables");
        JSONArray buildables = currState.getJSONObject("inventory").getJSONArray("buildables");

        // Sun stone
        for (int i = 0; i < collectables.length(); i ++) {
            if (collectables.getJSONObject(i).get("type").equals("sun_stone")) {
                return false;
            }
        }

        // Midnight armour
        for (int i = 0; i < buildables.length(); i ++) {
            if (buildables.getJSONObject(i).get("type").equals("midnight_armour")) {
                return false;
            }
        }

        // Invisible
        if (currState.getJSONObject("player").get("player_state").equals("InvisibleState"));
        
        return true;
    }

}
