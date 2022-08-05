package dungeonmania.BattleTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.DungeonManiaController;
import dungeonmania.Helper;
import dungeonmania.Entities.MoveableEntities.MoveableEntity;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;

public class BattleSpiderTests {
    Helper helper = new Helper();

    @Test
    @DisplayName("player wins a battle against a spider and spider wins")
    public void testSpiderBattlePlayerDies() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_battleTest_basicSpider", "c_battleTest_spiderKillsPlayer");
        
        assertTrue(res.getEntities().stream().map(e -> e.getType()).collect(Collectors.toList()).contains("player"));

        res = dmc.tick(Direction.DOWN);

        assertTrue(res.getBattles().size() == 1);
        assertFalse(res.getEntities().stream().map(e -> e.getType()).collect(Collectors.toList()).contains("player"));
    }

    @Test
    @DisplayName("player wins a battle against a spider and player wins")
    public void testSpiderBattleSpiderDies() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_battleTest_basicSpider", "c_battleTest_playerKillsSpider");
        
        assertTrue(res.getEntities().stream().map(e -> e.getType()).collect(Collectors.toList()).contains("player"));
        assertTrue(res.getEntities().stream().map(e -> e.getType()).collect(Collectors.toList()).contains("spider"));

        res = dmc.tick(Direction.DOWN);

        assertTrue(res.getBattles().size() == 1);
        assertTrue(res.getEntities().stream().map(e -> e.getType()).collect(Collectors.toList()).contains("player"));
        assertFalse(res.getEntities().stream().map(e -> e.getType()).collect(Collectors.toList()).contains("spider"));
    }

    @Test
    @DisplayName("test health calculations in a battle against a spider and player wins")
    public void testBasicCalculationsSpiderWin() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_battleTest_basicSpider", "c_battleTest_playerKillsSpider");
        
        assertTrue(res.getEntities().stream().map(e -> e.getType()).collect(Collectors.toList()).contains("player"));
        assertTrue(res.getEntities().stream().map(e -> e.getType()).collect(Collectors.toList()).contains("spider"));

        res = dmc.tick(Direction.DOWN);

        assertTrue(res.getBattles().size() == 1);
        BattleResponse battle = res.getBattles().get(0);
        assertEquals(3, battle.getRounds().size());

        //r1
        assertEquals(-2.0, battle.getRounds().get(0).getDeltaEnemyHealth(), 0.01);
        assertEquals(-0.5, battle.getRounds().get(0).getDeltaCharacterHealth(), 0.01);

        //r2
        assertEquals(-2.0, battle.getRounds().get(1).getDeltaEnemyHealth(), 0.01);
        assertEquals(-0.5, battle.getRounds().get(1).getDeltaCharacterHealth(), 0.01);

        //r3
        assertEquals(-2.0, battle.getRounds().get(2).getDeltaEnemyHealth(), 0.01);
        assertEquals(-0.5, battle.getRounds().get(2).getDeltaCharacterHealth(), 0.01);

        // test player health is updated after battle
        assertEquals(dmc.currentDungeon.getPlayer().getHealth(), 8.5, 0.01);
        assertFalse(res.getEntities().stream().map(e -> e.getType()).collect(Collectors.toList()).contains("spider"));
    }

    @Test
    @DisplayName("test health calculations in a battle against a spider and player loses")
    public void testBasicCalculationsSpiderLoss() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_battleTest_basicSpider", "c_battleTest_spiderKillsPlayer");
        
        assertTrue(res.getEntities().stream().map(e -> e.getType()).collect(Collectors.toList()).contains("player"));
        assertTrue(res.getEntities().stream().map(e -> e.getType()).collect(Collectors.toList()).contains("spider"));

        res = dmc.tick(Direction.DOWN);

        assertTrue(res.getBattles().size() == 1);
        BattleResponse battle = res.getBattles().get(0);
        assertEquals(2, battle.getRounds().size());

        //r1
        assertEquals(-0.2, battle.getRounds().get(0).getDeltaEnemyHealth(), 0.01);
        assertEquals(-0.5, battle.getRounds().get(0).getDeltaCharacterHealth(), 0.01);

        //r2
        assertEquals(-0.2, battle.getRounds().get(0).getDeltaEnemyHealth(), 0.01);
        assertEquals(-0.5, battle.getRounds().get(0).getDeltaCharacterHealth(), 0.01);

        // test player health is updated after battle
        MoveableEntity spider = (MoveableEntity) dmc.currentDungeon.getEntities().stream()
                                                                                .filter(e -> e.getType().equals("spider"))
                                                                                .findFirst()
                                                                                .get();
        assertEquals(spider.getHealth(), 4.6, 0.01);
        assertFalse(res.getEntities().stream().map(e -> e.getType()).collect(Collectors.toList()).contains("player"));
    }
}