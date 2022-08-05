package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;

import dungeonmania.Entities.Entity;
import dungeonmania.Entities.MoveableEntities.MercenaryEntity;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.response.models.RoundResponse;
import dungeonmania.util.Position;
import static dungeonmania.TestUtils.getValueFromConfigFile;

public class Helper {

    /**
     * Create the JSONObject for an entity given the x and y coordinate
     * If type is portal, will create a blue portal
     * @param type
     * @param x
     * @param y
     * @return
     */
    public JSONObject createEntityJSON(String type, String x, String y) {
        HashMap<String, String> jsonData = new HashMap<String, String>();
        jsonData.put("type", type);
        jsonData.put("x", x);
        jsonData.put("y", y);

        return new JSONObject(jsonData);
    }

    public Boolean assertEntityExistsInList(List<Entity> actual, Entity expected) {
        for (Entity e : actual) {
            if (e.getEntityResponse().equals(expected.getEntityResponse())) {
                return true;
            }
        }
        return false;
    }

    // Get position of entity with given id
    public Position getEntityPosition(DungeonManiaController dmc, String entityId) {
        DungeonResponse res = dmc.getDungeonResponseModel();
        return res.getEntities().stream()
                                .filter(s -> s.getId().equals(entityId))
                                .findFirst().get().getPosition();
    }

    public void assertBattleCalculations(String enemyType, BattleResponse battle, boolean enemyDies, String configFilePath) {
        List<RoundResponse> rounds = battle.getRounds();
        double playerHealth = Double.parseDouble(getValueFromConfigFile("player_health", configFilePath));
        double enemyHealth = Double.parseDouble(getValueFromConfigFile(enemyType + "_health", configFilePath));
        double playerAttack = Double.parseDouble(getValueFromConfigFile("player_attack", configFilePath));
        double enemyAttack = Double.parseDouble(getValueFromConfigFile(enemyType + "_attack", configFilePath));

        for (RoundResponse round : rounds) {
            assertEquals(-(enemyAttack / 10), round.getDeltaCharacterHealth(), 0.001);
            assertEquals(-(playerAttack / 5), round.getDeltaEnemyHealth(), 0.001);
            enemyHealth += round.getDeltaEnemyHealth();
            playerHealth += round.getDeltaCharacterHealth();
        }

        if (enemyDies) {
            assertTrue(enemyHealth <= 0);
        } else {
            assertTrue(playerHealth <= 0);
        }
    }

    public Boolean assertEqualsDungeonResponse(DungeonResponse expected, DungeonResponse actual) {

        if (!expected.getDungeonId().equals(actual.getDungeonId())) {
            System.out.println("dungeon id");
            return false;
        } else if (!expected.getDungeonName().equals(actual.getDungeonName())) {
            System.out.println("dungeon name");
            return false;
        } 

        HashMap<String, Position> entities = new HashMap<String, Position>();
        for (EntityResponse e : expected.getEntities()) {
            entities.put(e.getId(), e.getPosition());
        }

        for (EntityResponse e : actual.getEntities()) {
            if (!entities.get(e.getId()).equals(e.getPosition())) {
                System.out.println("entity" + e.getId());
                return false;
            }
        }

        List<String> inventory = expected.getInventory().stream().map(e -> e.getId()).collect(Collectors.toList());
        for (ItemResponse i : actual.getInventory()) {
            if (!inventory.contains(i.getId())) {
                System.out.println(inventory);
                System.out.println("dungeon inventory" + i.getId());
                return false;
            }
        }

        if (expected.getBattles().size() != actual.getBattles().size()) {
            System.out.println("battles");
            return false;
        }

        return expected.getGoals().equals(actual.getGoals());
    }
    
    // Get state of given mercenary
    public String getState(DungeonManiaController dmc, String mercenaryId) {
        Dungeon currDungeon = dmc.currentDungeon;
        String name = null;
        for (Entity e : currDungeon.getEntities()) {
            if (e.getId().equals(mercenaryId)) {
                name = ((MercenaryEntity) e).getState().getName();
            } 
        }
        return name;
    }

}
