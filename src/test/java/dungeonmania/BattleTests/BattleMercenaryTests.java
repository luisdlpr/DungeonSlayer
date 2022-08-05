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
import dungeonmania.Helper;

public class BattleMercenaryTests {

    Helper helper = new Helper();

    @Test
    @DisplayName("Test the player can battle and win against a mercenary")
    public void testBattleHostileWin() {
        // initialise dungeon
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_battleTest_basicMercenary", "c_battleTests_basicMercenaryMercenaryDies");

        // move right to start battle
        dmc.tick(Direction.RIGHT);
        DungeonResponse res = dmc.getDungeonResponseModel();
        // check the battle response
        BattleResponse battle = res.getBattles().get(0);

        // check all fields are as expected
        assertEquals(battle.getEnemy(), "mercenary");
        assertTrue(battle.getRounds().size() == 3);
        assertEquals(battle.getRounds().get(0).getDeltaCharacterHealth(), -0.5, 0.01);
        assertEquals(battle.getRounds().get(0).getDeltaEnemyHealth(), -2.0, 0.01);

        helper.assertBattleCalculations("mercenary", battle, true, "c_battleTests_basicMercenaryMercenaryDies");
    }

    @Test
    @DisplayName("Test the player can battle and lose against a mercenary")
    public void testBattleHostileLoss() {
        // initialise dungeon
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_battleTest_basicMercenary", "c_battleTests_basicMercenaryPlayerDies");

        // move right to start battle
        dmc.tick(Direction.RIGHT);
        DungeonResponse res = dmc.getDungeonResponseModel();
        // check the battle response
        BattleResponse battle = res.getBattles().get(0);

        // check all fields are as expected
        assertEquals(battle.getEnemy(), "mercenary");
        assertTrue(battle.getRounds().size() == 3);
        assertEquals(battle.getRounds().get(0).getDeltaCharacterHealth(), -2.0, 0.01);
        assertEquals(battle.getRounds().get(0).getDeltaEnemyHealth(), -0.2, 0.01);

        helper.assertBattleCalculations("mercenary", battle, false, "c_battleTests_basicMercenaryPlayerDies");
    }

    @Test
    @DisplayName("Test the player cannot battle an allied mercenary")
    public void testBattleAlly() {
        // initialise dungeon
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_battleTest_fightAlly", "c_mercenaryTest_bribe_radius3");

        // move right to pick up treasure
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        assertDoesNotThrow(() -> dmc.interact("mercenary0"));
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        // If they were hostile, they would fight on this tick
        DungeonResponse res = dmc.getDungeonResponseModel();
        // check the battle response
        // There should be no battles
        assertTrue(res.getBattles().isEmpty());
    }

    @Test
    @DisplayName("Test that ally status buffs are applied to the player")
    public void testBattleAllyBuffs() {
        // initialise dungeon
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_battleTest_MercenaryAllyBattles", "c_battleTest_MercenaryAlly");

        dmc.tick(Direction.DOWN);
        assertDoesNotThrow(() -> dmc.interact("mercenary0"));
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.RIGHT);
        DungeonResponse res = dmc.tick(Direction.RIGHT);

        BattleResponse battle = res.getBattles().get(0);
        assertEquals(3, battle.getRounds().size());

        // Round 1
        RoundResponse round1 = battle.getRounds().get(0);
        // deltaEnemyHealth = (bowMultiplier * (player.getAttackDamage() + allyAttackBuff + swordAttack)) / 5;
        assertEquals(round1.getDeltaCharacterHealth(), -(5.0 - 3.0) / 10);
        assertEquals(round1.getDeltaEnemyHealth(), -(10.0 + 5.0) / 5);

        // Round 2
        RoundResponse round2 = battle.getRounds().get(1);
        // deltaEnemyHealth = (bowMultiplier * (player.getAttackDamage() + allyAttackBuff + swordAttack)) / 5;
        assertEquals(round2.getDeltaCharacterHealth(), -(5.0 - 3.0) / 10);
        assertEquals(round2.getDeltaEnemyHealth(), -(10.0 + 5.0) / 5);
    }

    @Test
    @DisplayName("Test that an ally buff can fully negate enemy damage if it is high enough")
    public void testBattleAllyDamageNegation() {
        // initialise dungeon
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_battleTest_MercenaryAllyBattles", "c_battleTest_MercenaryAlly");

        dmc.tick(Direction.DOWN);
        assertDoesNotThrow(() -> dmc.interact("mercenary0"));
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.DOWN);
        DungeonResponse res = dmc.tick(Direction.DOWN);

        BattleResponse battle = res.getBattles().get(0);
        assertEquals(2, battle.getRounds().size());

        // Round 1
        RoundResponse round1 = battle.getRounds().get(0);
        // deltaEnemyHealth = (bowMultiplier * (player.getAttackDamage() + allyAttackBuff + swordAttack)) / 5;
        assertEquals(round1.getDeltaCharacterHealth(), 0.0);
        assertEquals(round1.getDeltaEnemyHealth(), -(10.0 + 5.0) / 5);

        // Round 2
        RoundResponse round2 = battle.getRounds().get(1);
        // deltaEnemyHealth = (bowMultiplier * (player.getAttackDamage() + allyAttackBuff + swordAttack)) / 5;
        assertEquals(round2.getDeltaCharacterHealth(), 0.0);
        assertEquals(round2.getDeltaEnemyHealth(), -(10.0 + 5.0) / 5);
    }

    @Test
    @DisplayName("Test that a high ally attack buff can help the player instantly kill an enemy")
    public void testBattleAllyHighAttackBonus() {
        // initialise dungeon
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_battleTest_MercenaryAllyBattles", "c_battleTests_MercenaryAllyHighAttack");

        dmc.tick(Direction.DOWN);
        assertDoesNotThrow(() -> dmc.interact("mercenary0"));
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.RIGHT);
        DungeonResponse res = dmc.tick(Direction.RIGHT);

        // Should win in just one round
        BattleResponse battle = res.getBattles().get(0);
        assertEquals(1, battle.getRounds().size());

        // Round 1
        RoundResponse round1 = battle.getRounds().get(0);
        // deltaEnemyHealth = (bowMultiplier * (player.getAttackDamage() + allyAttackBuff + swordAttack)) / 5;
        assertEquals(round1.getDeltaCharacterHealth(), -(5.0 - 3.0) / 10);
        assertEquals(round1.getDeltaEnemyHealth(), -(10.0 + 20.0) / 5);
    }
}
