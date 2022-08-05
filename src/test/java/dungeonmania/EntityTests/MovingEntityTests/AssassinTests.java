package dungeonmania.EntityTests.MovingEntityTests;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static dungeonmania.TestUtils.getEntities;

import org.junit.jupiter.api.Test;

import dungeonmania.DungeonManiaController;
import dungeonmania.Helper;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.RoundResponse;
import dungeonmania.util.Direction;
import static dungeonmania.TestUtils.getInventory;

public class AssassinTests {
    Helper helper = new Helper();

    @Test
    public void testAssassinCreationAndBribeWithRandomVal() {
        // initialise dungeon
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("assassin", "simpleAssassin");

        //test that an assassin exists
        assertEquals(1, getEntities(res, "assassin").size());

        // move to assassin while picking up treasure
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);

        //standing adjacent to assassin, attempt to bribe him, no error should be thrown
        assertDoesNotThrow(() -> dmc.interact("assassin0"));

        //regardless of bribe success or fail the assassin must still be on map when taking a step to the left
        dmc.tick(Direction.LEFT);
        res = dmc.getDungeonResponseModel();

        // check the assassin exists
        assertEquals(1, getEntities(res, "assassin").size());
    }

    @Test
    public void testBribingAssassinWithAllBribesFailingConfig() throws IllegalArgumentException, InvalidActionException {
        // initialise dungeon
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("assassin", "simpleAssassinBribeFail");

        // move to assassin while picking up treasure
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        DungeonResponse res = dmc.tick(Direction.RIGHT);

        //check how much treasure is in inventory should be 1
        assertEquals(2, getInventory(res, "treasure").size());

        //standing adjacent to assassin, bribe him
        //assertDoesNotThrow(() -> dmc.interact("assassin0"));
        res = dmc.interact("assassin0");

        //check how much treasure is in inventory should be 0
        assertEquals(0, getInventory(res, "treasure").size());

        dmc.tick(Direction.RIGHT);
        res = dmc.getDungeonResponseModel();
        // check the battle response
        BattleResponse battle = res.getBattles().get(0);

        //check battle has occured and assassin was killed
        assertEquals(battle.getEnemy(), "assassin");
        helper.assertBattleCalculations("assassin", battle, true, "simpleAssassinBribeFail");
    }

    @Test
    public void testBribingAssassinWithAllBribesSucceedingConfig() {
        // initialise dungeon
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("assassin", "simpleAssassinBribeSuccess");

        // move to assassin while picking up treasure
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        DungeonResponse res = dmc.tick(Direction.RIGHT);

        //test that an assassin exists
        assertEquals(1, getEntities(res, "assassin").size());

        //standing adjacent to assassin, attempt to bribe him
        assertDoesNotThrow(() -> dmc.interact("assassin0"));

        dmc.tick(Direction.RIGHT);
        res = dmc.getDungeonResponseModel();

        // check no battle occurs when stepping right because the asssassin is now an ally and still exists
        assertEquals(1, getEntities(res, "assassin").size());
    }

    @Test
    public void testAssassinAllyBattlingMerc() {
        // initialise dungeon
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("allyAssassin", "simpleAssassinBribeSuccess");

        // move to assassin while picking up treasure
        dmc.tick(Direction.RIGHT);

        //standing adjacent to assassin, bribe him
        assertDoesNotThrow(() -> dmc.interact("assassin0"));

        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        
        
        DungeonResponse res = dmc.getDungeonResponseModel();
        // check the battle response
        BattleResponse battle = res.getBattles().get(0);

        //check battle has occured and merc was killed
        assertEquals(battle.getEnemy(), "mercenary");

        assertEquals(7, battle.getRounds().size());

        // Round 1
        RoundResponse round1 = battle.getRounds().get(0);
        // deltaEnemyHealth = (bowMultiplier * (player.getAttackDamage() + allyAttackBuff + swordAttack)) / 5;
        assertEquals(round1.getDeltaCharacterHealth(), 0.0);
        assertEquals(round1.getDeltaEnemyHealth(), -(1.0 + 3.0) / 5);

        // Round 2
        RoundResponse round2 = battle.getRounds().get(1);
        // deltaEnemyHealth = (bowMultiplier * (player.getAttackDamage() + allyAttackBuff + swordAttack)) / 5;
        assertEquals(round2.getDeltaCharacterHealth(), 0.0);
        assertEquals(round2.getDeltaEnemyHealth(), -(1.0 + 3.0) / 5);

        RoundResponse round3 = battle.getRounds().get(2);
        // deltaEnemyHealth = (bowMultiplier * (player.getAttackDamage() + allyAttackBuff + swordAttack)) / 5;
        assertEquals(round3.getDeltaCharacterHealth(), 0.0);
        assertEquals(round3.getDeltaEnemyHealth(), -(1.0 + 3.0) / 5);

        RoundResponse round4 = battle.getRounds().get(3);
        // deltaEnemyHealth = (bowMultiplier * (player.getAttackDamage() + allyAttackBuff + swordAttack)) / 5;
        assertEquals(round4.getDeltaCharacterHealth(), 0.0);
        assertEquals(round4.getDeltaEnemyHealth(), -(1.0 + 3.0) / 5);

        RoundResponse round5 = battle.getRounds().get(4);
        // deltaEnemyHealth = (bowMultiplier * (player.getAttackDamage() + allyAttackBuff + swordAttack)) / 5;
        assertEquals(round5.getDeltaCharacterHealth(), 0.0);
        assertEquals(round5.getDeltaEnemyHealth(), -(1.0 + 3.0) / 5);

        RoundResponse round6 = battle.getRounds().get(5);
        // deltaEnemyHealth = (bowMultiplier * (player.getAttackDamage() + allyAttackBuff + swordAttack)) / 5;
        assertEquals(round6.getDeltaCharacterHealth(), 0.0);
        assertEquals(round6.getDeltaEnemyHealth(), -(1.0 + 3.0) / 5);

        RoundResponse round7 = battle.getRounds().get(6);
        // deltaEnemyHealth = (bowMultiplier * (player.getAttackDamage() + allyAttackBuff + swordAttack)) / 5;
        assertEquals(round7.getDeltaCharacterHealth(), 0.0);
        assertEquals(round7.getDeltaEnemyHealth(), -(1.0 + 3.0) / 5);

        
    }

    @Test
    public void testBribeAssassinNoTreasure() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        dmc.newGame("assassin", "simpleAssassinBribeSuccess");
    
        // Walk around the treasure
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);

        assertThrows(InvalidActionException.class, () -> dmc.interact("assassin0"));
    }

    @Test
    public void testBribeAssassinOutOfRange() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        dmc.newGame("assassin", "simpleAssassinBribeSuccess");
    
        dmc.tick(Direction.RIGHT);

        // Try to bribe
        assertThrows(InvalidActionException.class, () -> dmc.interact("assassin0"));
    }

}
