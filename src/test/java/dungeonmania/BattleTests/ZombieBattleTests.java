package dungeonmania.BattleTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import static dungeonmania.TestUtils.getEntities;

public class ZombieBattleTests {
    @Test
    public void testZombieBattlePlayerDies() {
        // initialise dungeon
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_battleTest_zombie", "strongZombies");


        dmc.tick(Direction.RIGHT);

        // check the battle response
        DungeonResponse res = dmc.getDungeonResponseModel();
        BattleResponse battle = res.getBattles().get(0);

        // check all fields are as expected
        assertEquals(battle.getEnemy(), "zombie_toast");
        assertTrue(battle.getRounds().size() == 1);
    
        // check health calculations    
        assertEquals(battle.getRounds().get(0).getDeltaCharacterHealth(), -10, 0.01);
        assertEquals(battle.getRounds().get(0).getDeltaEnemyHealth(), -0.2, 0.01);
        
        //assert dungeon response has no player
        assertEquals(0, getEntities(res, "player").size());

        //assert zombie is still alive
        assertEquals(1, getEntities(res, "zombie_toast").size());
        
    }

    @Test
    public void testZombieBattleZombieDies() {
        // initialise dungeon
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_battleTest_zombie", "weakZombies");

        dmc.tick(Direction.RIGHT);

        // check the battle response
        DungeonResponse res = dmc.getDungeonResponseModel();
        BattleResponse battle = res.getBattles().get(0);

        // check all fields are as expected
        assertEquals(battle.getEnemy(), "zombie_toast");
        assertTrue(battle.getRounds().size() == 3);
    
        // check health calculations   
        assertEquals(battle.getRounds().get(0).getDeltaCharacterHealth(), -0.1, 0.01);
        assertEquals(battle.getRounds().get(0).getDeltaEnemyHealth(), -2, 0.01);

        assertEquals(battle.getRounds().get(1).getDeltaCharacterHealth(), -0.1, 0.01);
        assertEquals(battle.getRounds().get(1).getDeltaEnemyHealth(), -2, 0.01);

        assertEquals(battle.getRounds().get(2).getDeltaCharacterHealth(), -0.1, 0.01);
        assertEquals(battle.getRounds().get(2).getDeltaEnemyHealth(), -2, 0.01);
        
        //assert dungeon get entities for zombie toast is size 0
        assertEquals(0, getEntities(res, "zombie_toast").size());
        assertEquals(1, getEntities(res, "player").size());

        
    }


}
