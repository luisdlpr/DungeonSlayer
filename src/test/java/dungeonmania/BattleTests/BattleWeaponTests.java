package dungeonmania.BattleTests;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.RoundResponse;
import dungeonmania.util.Direction;

public class BattleWeaponTests {
    @Test
    @DisplayName("attack twice with bow - spider and bow durability")
    public void testSpiderBattleBow() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_spiderBowTest", "c_battleTests_basicMercenaryMercenaryDies");

        // Collect materials for bow
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        assertDoesNotThrow(() -> dmc.build("bow"));
        dmc.tick(Direction.UP);
        dmc.tick(Direction.UP);
        dmc.tick(Direction.LEFT);
        DungeonResponse res = dmc.tick(Direction.LEFT);

        // Check battle fields are correct after battling spider
        BattleResponse battle = res.getBattles().get(0);
        assertEquals(2, battle.getRounds().size());

        // Check round 1 response calculations
        RoundResponse round1 = battle.getRounds().get(0);
        // -(bowMultiplier * (player.getAttackDamage() + allyAttackBuff + swordAttack)) / 5
        assertEquals(round1.getDeltaEnemyHealth(), -(2.0 * 10.0) / 5);
        // Make sure bow was used in round
        assertTrue(round1.getWeaponryUsed().stream().anyMatch(i -> i.getType().equals("bow")));

        // Check round 2 response calculations
        RoundResponse round2 = battle.getRounds().get(1);
        assertEquals(round2.getDeltaEnemyHealth(), -(2.0 * 10.0) / 5);
        // Make sure bow was used in round
        assertTrue(round2.getWeaponryUsed().stream().anyMatch(i -> i.getType().equals("bow")));

        // Test bow durability
        assertEquals(false, res.getInventory().stream().anyMatch(i -> i.getType().equals("bow")));
    }

    @Test
    @DisplayName("shield reduces enemy attack - spider and shield durability")
    public void testShieldReducesAttack() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_spiderShieldBattle", "c_battleTests_basicMercenaryMercenaryDies");

        // Collect materials to build shield
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        assertDoesNotThrow(() -> dmc.build("shield"));
        DungeonResponse res = dmc.tick(Direction.LEFT);

        // Check for correct number of rounds in battle
        BattleResponse battle = res.getBattles().get(0);
        assertEquals(3, battle.getRounds().size());

        // Check round 1 calculations
        RoundResponse round1 = battle.getRounds().get(0);
        // deltaPlayerHealth = (enemy.getAttackDamage()- allyDefenceBuff - shieldDefence) / 10;
        assertEquals(round1.getDeltaCharacterHealth(), -(5.0 - 1.0) / 10);
        // Make sure shield was used in round
        assertTrue(round1.getWeaponryUsed().stream().anyMatch(i -> i.getType().equals("shield")));

        // Check round2 calculations
        RoundResponse round2 = battle.getRounds().get(1);
        assertEquals(round2.getDeltaCharacterHealth(), -(5.0 - 1.0) / 10);
        // Make sure shield was used in round
        assertTrue(round2.getWeaponryUsed().stream().anyMatch(i -> i.getType().equals("shield")));

        // Test shield durability
        assertEquals(false, res.getInventory().stream().anyMatch(i -> i.getType().equals("shield")));
        
    }

    @Test
    @DisplayName("sword increases attack damage - spider and sword durability")
    public void testSwordIncreasesAttack() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_spiderSwordBattle", "c_battleTests_basicMercenaryMercenaryDies");

        // Pick up sword
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        DungeonResponse res = dmc.tick(Direction.LEFT);

        // Check for correct number of rounds in battle
        BattleResponse battle = res.getBattles().get(0);
        assertEquals(3, battle.getRounds().size());

        // Round1 calculations
        RoundResponse round1 = battle.getRounds().get(0);
        // (bowMultiplier * (player.getAttackDamage() + allyAttackBuff + swordAttack)) / 5
        assertEquals(round1.getDeltaEnemyHealth(), -(10.0 + 2.0) / 5);
        // Check sword was used in the round
        assertTrue(round1.getWeaponryUsed().stream().anyMatch(i -> i.getType().equals("sword")));

        // Round2 calculations
        RoundResponse round2 = battle.getRounds().get(1);
        assertEquals(round2.getDeltaEnemyHealth(), -(10.0 + 2.0) / 5);
        // Check sword was used in the round
        assertTrue(round2.getWeaponryUsed().stream().anyMatch(i -> i.getType().equals("sword")));

        // Test sword durability
        assertEquals(false, res.getInventory().stream().anyMatch(i -> i.getType().equals("sword")));
    }

}
