 package dungeonmania.EntityTests.CollectableEntityTests.WeaponTests;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.DungeonManiaController;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.RoundResponse;
import dungeonmania.util.Direction;

import static dungeonmania.TestUtils.getInventory;

public class MidnightArmourTests {
    // Unit: Test crafting and that materials are removed with no zombies
    // Unit: Test crafting and that materials are removed with zombies
    // Integration: Test battle calculations when midnight armour is equipped and urability
    // Integration: exception is thrown when player tries to craft with zombies in dungeon

    @Test
    @DisplayName("crafting midnight armour no zombies")
    public void testArmourCraftNoZombies() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_midnightArmour_noZombies", "M3_sunStoneSceptreTest");

        // Check for correct exception when player doesn't have the items
        assertThrows(InvalidActionException.class, () -> dmc.build("midnight_armour"));

        // Collect materials
        dmc.tick(Direction.DOWN);
        DungeonResponse res = dmc.tick(Direction.DOWN);
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(1, getInventory(res, "sword").size());
        // Check DungeonResponse for correct buildables now that you have materials to build a armour
        assertEquals(Arrays.asList(new String[]{"midnight_armour"}), res.getBuildables());

        // Build armour
        res = assertDoesNotThrow(() -> dmc.build("midnight_armour"));
        assertEquals(0, getInventory(res, "sun_stone").size());
        assertEquals(0, getInventory(res, "sword").size());
        assertEquals(1, getInventory(res, "midnight_armour").size());
    }

    @Test
    @DisplayName("crafting midnight armour with zombies")
    public void testArmourCraftZombies() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_midnightArmour_zombies", "M3_sunStoneSceptreTest");

        // Collect materials
        dmc.tick(Direction.DOWN);
        DungeonResponse res = dmc.tick(Direction.DOWN);
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(1, getInventory(res, "sword").size());
        // Check DungeonResponse for empty buildables
        // Even though player has materials for armour, they cannot build it because there is a zombie
        assertEquals(Arrays.asList(), res.getBuildables());

        // Try to build armour - should throw exception
        assertThrows(InvalidActionException.class, () -> dmc.build("midnight_armour"));
    }

    @Test
    @DisplayName("midnight armour in battle")
    public void testArmourBattle() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_midnightArmour_noZombies", "M3_sunStoneSceptreTest");

        // Collect materials
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        // Build armour
        assertDoesNotThrow(() -> dmc.build("midnight_armour"));
        // Fight spider
        DungeonResponse res = dmc.tick(Direction.LEFT);

        // Check battle fields are correct after battling spider
        BattleResponse battle = res.getBattles().get(0);
        assertEquals(3, battle.getRounds().size());

        // Check round 1 response calculations
        RoundResponse round1 = battle.getRounds().get(0);
        // -(bowMultiplier * (player.getAttackDamage() + allyAttackBuff + swordAttack + armourAttack)) / 5
        assertEquals(round1.getDeltaEnemyHealth(), -(10.0 + 2.0) / 5);
        // deltaPlayerHealth = (enemy.getAttackDamage()- allyDefenceBuff - shieldDefence - armourDefence) / 10;
        assertEquals(round1.getDeltaCharacterHealth(), -(5.0 - 2.0) / 10);
        // Make sure armour was used in round
        assertTrue(round1.getWeaponryUsed().stream().anyMatch(i -> i.getType().equals("midnight_armour")));

        // Check round 2 response calculations
        RoundResponse round2 = battle.getRounds().get(1);
        assertEquals(round2.getDeltaEnemyHealth(), -(10.0 + 2.0) / 5);
        assertEquals(round1.getDeltaCharacterHealth(), -(5.0 - 2.0) / 10);
        // Make sure armour was used in round
        assertTrue(round2.getWeaponryUsed().stream().anyMatch(i -> i.getType().equals("midnight_armour")));

        // Test armour durability
        assertEquals(true, res.getInventory().stream().anyMatch(i -> i.getType().equals("midnight_armour")));

    }

}
