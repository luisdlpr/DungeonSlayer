package dungeonmania.BattleTests;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.Dungeon;
import dungeonmania.DungeonManiaController;
import dungeonmania.Helper;
import dungeonmania.Entities.Entity;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;

public class BattlePotionTests {
    Helper helper = new Helper();

    @Test
    @DisplayName("player wins a battle against a spider with an invinicibility potion")
    public void testSpiderBattleInvincibility() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_spiderPotionTest", "c_PotionTest_queuePotions");
        Dungeon dungeon = dmc.currentDungeon;

        // Get the potion
        dmc.tick(Direction.DOWN);
        // Use the potion
        assertDoesNotThrow(() -> dmc.tick("invincibility_potion0"));
        // Move to same square as spider
        DungeonResponse res = dmc.tick(Direction.LEFT);
        // Expect that player won the battle
        // check that spider has been removed from dungeon
        assertEquals(res.getBattles().size(), 1);

        List<Entity> spidersInDungeon = dungeon.getEntities()
                                                .stream()
                                                .filter(e -> e.getId().equals("spider0"))
                                                .collect(Collectors.toList());
        assertEquals(0, spidersInDungeon.size());
    }
    
    @Test
    @DisplayName("test battles with every spider and mercenary while invincible")
    public void testInvincibilityBattle() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_battleTest_Invincibility", "c_battleTest_Invincibility");

        // Pick up potion
        dmc.tick(Direction.DOWN);
        DungeonResponse res;
        // Try use potion, on this tick a battle with a spider should ensue
        assertDoesNotThrow(() -> {dmc.tick("invincibility_potion0");});

        // Check the battle response
        res = dmc.getDungeonResponseModel();
        assertEquals(1, res.getBattles().size());
        BattleResponse battle = res.getBattles().get(0);

        // Check all fields are as expected
        assertEquals(battle.getEnemy(), "spider");
        assertTrue(battle.getRounds().size() == 1);
        assertEquals(battle.getRounds().get(0).getDeltaCharacterHealth(), 0.0, 0.01);
        assertEquals(battle.getRounds().get(0).getDeltaEnemyHealth(), -5.0, 0.01);
        assertTrue(battle.getRounds().get(0).getWeaponryUsed().get(0).getType().equals("invincibility_potion"));

        // Move to battle zombie with invincibility
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        // Check response
        assertEquals(2, res.getBattles().size());
        battle = res.getBattles().get(1);
        assertEquals(battle.getEnemy(), "zombie_toast");
        assertTrue(battle.getRounds().size() == 1);
        assertEquals(battle.getRounds().get(0).getDeltaCharacterHealth(), 0.0, 0.01);
        assertEquals(battle.getRounds().get(0).getDeltaEnemyHealth(), -5.0, 0.01);
        assertTrue(battle.getRounds().get(0).getWeaponryUsed().get(0).getType().equals("invincibility_potion"));

        // Move to battle mercenary with invincibility
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        assertEquals(3, res.getBattles().size());
        battle = res.getBattles().get(2);

        // Check response
        assertEquals(battle.getEnemy(), "mercenary");
        assertTrue(battle.getRounds().size() == 1);
        assertEquals(battle.getRounds().get(0).getDeltaCharacterHealth(), 0.0, 0.01);
        assertEquals(battle.getRounds().get(0).getDeltaEnemyHealth(), -5.0, 0.01);
    }
}
