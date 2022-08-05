package dungeonmania.BattleTests;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import static dungeonmania.TestUtils.getEntities;

public class HydraBattleTests {
    @Test
    public void testHydraBattleNeverHeal() {
        // initialise dungeon
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("hydraA", "hydraNeverHeal");


        dmc.tick(Direction.RIGHT);

        // check the battle response
        DungeonResponse res = dmc.getDungeonResponseModel();
        BattleResponse battle = res.getBattles().get(0);

        // check all fields are as expected
        assertEquals(battle.getEnemy(), "hydra");
        assertTrue(battle.getRounds().size() == 10);
    
        // check health calculations    
        assertEquals(battle.getRounds().get(0).getDeltaCharacterHealth(), -0.1, 0.01);
        assertEquals(battle.getRounds().get(0).getDeltaEnemyHealth(), -1, 0.01);
        
        //assert dungeon response has player
        assertEquals(1, getEntities(res, "player").size());

        //assert hydra is dead
        assertEquals(0, getEntities(res, "hydra").size());
        
    }

    @Test
    public void testHydraBattleAlwaysHeal() {
        // initialise dungeon
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("hydraA", "hydraAlwaysHeal");


        dmc.tick(Direction.RIGHT);

        // check the battle response
        DungeonResponse res = dmc.getDungeonResponseModel();
        BattleResponse battle = res.getBattles().get(0);

        // check all fields are as expected
        assertEquals(battle.getEnemy(), "hydra");
    
        // check health calculations (Hydra's health should always increase by 1)   
        assertEquals(battle.getRounds().get(0).getDeltaCharacterHealth(), -0.1, 0.01);
        assertEquals(battle.getRounds().get(0).getDeltaEnemyHealth(), 1, 0.01);
        
        //assert dungeon response has no player
        assertEquals(0, getEntities(res, "player").size());

        //assert hydra is alive
        assertEquals(1, getEntities(res, "hydra").size());
        
    }

    @Test
    public void testHydraBattleSometimesHeal() {
        // initialise dungeon
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("hydraA", "hydraSometimesHeal");

        assertDoesNotThrow(() -> dmc.tick(Direction.RIGHT));
        
        // check the battle response
        DungeonResponse res = dmc.getDungeonResponseModel();
        BattleResponse battle = res.getBattles().get(0);

        // check a battle occured between player and hydra
        assertEquals(battle.getEnemy(), "hydra");
    
        // check health calculation, the player's health should be decreasing by 0.1 whereas hydra is random in this test
        assertEquals(battle.getRounds().get(0).getDeltaCharacterHealth(), -0.1, 0.01);
        
    }
}
