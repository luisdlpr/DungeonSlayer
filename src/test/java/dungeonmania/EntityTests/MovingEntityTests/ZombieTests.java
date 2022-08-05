package dungeonmania.EntityTests.MovingEntityTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.DungeonManiaController;
import dungeonmania.Entities.Entity;
import dungeonmania.Entities.MoveableEntities.ZombieToastEntity;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import static dungeonmania.TestUtils.getEntities;
import static dungeonmania.TestUtils.getInventory;

public class ZombieTests {
    @Test //white box test to check zombie toast spawner destruction
    @DisplayName("Test zombie toast spawner is destroyed, it is destroyed before any zombies spawn.")
    public void testZombieSpawnerDestructionNoZombies() throws IllegalArgumentException, InvalidActionException {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_zombies", "c_simple");

        //assert there is a zombie spawner on the map
        assertEquals(1, getEntities(res, "zombie_toast_spawner").size());

        //pick up sword
        res = dmc.tick(Direction.DOWN);

        //walk back to stand cardinally adjacent to zombie spawner
        res = dmc.tick(Direction.UP);
        assertEquals(1, getInventory(res, "sword").size());
        
        //interact with toast spawner
        res = dmc.interact("zombie_toast_spawner0");

        //assert there is not zombie toast spawner on the map
        assertEquals(0, getEntities(res, "zombie_toast_spawner").size());

    }

    @Test //white box test to check zombie toast spawner destruction
    @DisplayName("Test spawning zombies, no potions.")
    public void testZombieSpawningNoPotions() throws IllegalArgumentException, InvalidActionException {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_zombies", "c_simple");

        //assert there is a zombie spawner on the map
        assertEquals(1, getEntities(res, "zombie_toast_spawner").size());

        //pick up sword (it should take 10 ticks for a zombie to spawn)
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);

        //assert there is a zombie on the map
        assertEquals(1, getEntities(res, "zombie_toast").size());

        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        
        //another zombie should have spawned now
        assertEquals(2, getEntities(res, "zombie_toast").size());

    }

    @Test //white box test to check zombie toast spawner destruction
    @DisplayName("Test spawning zombie and his position in relation to the player after invincibility potion")
    public void testZombieSpawningInvincibilityPotion() throws IllegalArgumentException, InvalidActionException {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_zombiesInvincibility", "c_simple");

        //assert there is a zombie spawner on the map
        assertEquals(1, getEntities(res, "zombie_toast_spawner").size());

        //pick up sword and potions (it should take 10 ticks for a zombie to spawn)
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);

        //assert there is a zombie on the map
        assertEquals(1, getEntities(res, "zombie_toast").size());
        //find zombie
        
        List<Entity> ents  = dmc.currentDungeon.getEntities();    
        ZombieToastEntity zombieEntity = null;
        for (Entity e : ents) {
            if (e.getType().equals("zombie_toast")) {
                zombieEntity = (ZombieToastEntity)e;
            }
                
        }

        //distance betwen zombie and player
        double before = dmc.currentDungeon.getPlayer().getCurrentPosition().getDistanceBetween(zombieEntity.getPosition());

        res = dmc.tick("invincibility_potion0");
        res = dmc.tick("invincibility_potion1");
        res = dmc.tick("invincibility_potion2");
        res = dmc.tick("invincibility_potion3");
        
        //find zombie
        
        ents  = dmc.currentDungeon.getEntities();   
        for (Entity e : ents) {
            if (e.getType().equals("zombie_toast")) {
                zombieEntity = (ZombieToastEntity)e;
            }
                
        }

        double after = dmc.currentDungeon.getPlayer().getCurrentPosition().getDistanceBetween(zombieEntity.getPosition());
        assertTrue(after >= before);

    }
    
    @Test
    @DisplayName("destroying spawner with invalid id")
    public void destroyInvalidZombieSpawner() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_enemiesMultipleSpawners", "c_movementTest_testMovementDown");
        
        // Try to destroy non-existent spawner
        assertThrows(IllegalArgumentException.class, () -> dmc.interact("zombie_toast_spawner10"));
    }

    @Test
    @DisplayName("destroying spawner with no weapon")
    public void destroyZombieSpawnerNoWeapon() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_enemiesMultipleSpawners", "c_movementTest_testMovementDown");
        
        // Try to destroy non-existent spawner
        assertThrows(InvalidActionException.class, () -> dmc.interact("zombie_toast_spawner0"));
    }

    @Test
    @DisplayName("destroying spawner with no weapon")
    public void toasterBlockedShouldNotSpawnZombies() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_zombiesToasterNoOpenSquares", "c_simple");

        //assert there is a zombie spawner on the map
        assertEquals(1, getEntities(res, "zombie_toast_spawner").size());

        //(it should take 10 ticks for a zombie to spawn)
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);

        //assert there is no zombie on the map because the spawner's cardianally adjacent squares are blocked
        assertEquals(0, getEntities(res, "zombie_toast").size());

        //(it should take 10 ticks for a zombie to spawn)
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);

        //assert there is no zombie on the map because the spawner's cardianally adjacent squares are blocked
        assertEquals(0, getEntities(res, "zombie_toast").size());
    }

}
