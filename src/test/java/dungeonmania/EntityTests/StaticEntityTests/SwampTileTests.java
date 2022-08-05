package dungeonmania.EntityTests.StaticEntityTests;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.DungeonManiaController;
import dungeonmania.Helper;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class SwampTileTests {

    Helper helper = new Helper();
    
    @Test
    @DisplayName("Test that the player is not slowed down by a swamp tile")
    public void testSwampTilePlayerNotSlowed() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        dmc.newGame("d_swampTest_playerMovement", "c_simple");

        // Check we are on the zero tick
        assertEquals(0, dmc.currentDungeon.getTick());
        
        // Move right onto the tile - we should now be on tick 1
        dmc.tick(Direction.RIGHT);
        assertEquals(1, dmc.currentDungeon.getTick());

        // Move off the tile - we should now be on tick 2, since it should not slow player
        dmc.tick(Direction.RIGHT);
        assertEquals(2, dmc.currentDungeon.getTick());
    }

    // Once an entity moves onto a swamp tile, it becomes trapped there for
    // multiplication factor number of ticks

    @Test
    @DisplayName("Test mercenary gets stuck on swamp tile")
    public void testSwampTileMercenarySlowed() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        dmc.newGame("d_swampTest_mercenaryMovement", "c_simple");

        // We are on the zero tick
        Position swampTilePos = helper.getEntityPosition(dmc, "swamp_tile0");
        
        // Move left - we should now be on tick 1
        // Mercenary should have moved onto the swamp tile in this tick 
        dmc.tick(Direction.LEFT);
        assertTrue(helper.getEntityPosition(dmc, "mercenary0").equals(swampTilePos));

        // Move player to advance tick, mercenary should still be at swamp tile (Tick 2)
        dmc.tick(Direction.LEFT);
        assertTrue(helper.getEntityPosition(dmc, "mercenary0").equals(swampTilePos));

        // Move player to advance tick, mercenary should still be at swamp tile (Tick 3)
        dmc.tick(Direction.LEFT);
        assertTrue(helper.getEntityPosition(dmc, "mercenary0").equals(swampTilePos));

        // Mercenary now should be able to move (Tick 4)
        // Should be off the swamp tile (to the left)
        dmc.tick(Direction.LEFT);
        assertTrue(helper.getEntityPosition(dmc, "mercenary0").
                                            equals(swampTilePos.translateBy(Direction.LEFT)));
    }

    @Test
    @DisplayName("Test allied mercenary gets stuck on swamp tile")
    public void testSwampTileAllySlowed() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        dmc.newGame("d_swampTest_alliedMercenary", "c_simple");

        // We are on the zero tick
        Position swampTilePos = helper.getEntityPosition(dmc, "swamp_tile0");

        // 1st tick
        dmc.tick(Direction.RIGHT);
        assertEquals(1, dmc.currentDungeon.getTick());
        // 2nd tick
        dmc.tick(Direction.RIGHT);
        assertEquals(2, dmc.currentDungeon.getTick());
        // 3rd tick
        dmc.tick(Direction.RIGHT);
        assertEquals(3, dmc.currentDungeon.getTick());

        // Bribe mercenary
        assertDoesNotThrow(() -> dmc.interact("mercenary0"));

        // 4th tick
        dmc.tick(Direction.LEFT);
        // 5th tick
        dmc.tick(Direction.LEFT);
        // 6th tick
        dmc.tick(Direction.LEFT);
        // 7th tick
        dmc.tick(Direction.LEFT);

        // Mercenary now stuck on tile
        // Move player and check the mercenary does not follow
        dmc.tick(Direction.LEFT);
        assertTrue(helper.getEntityPosition(dmc, "mercenary0").equals(swampTilePos));
        dmc.tick(Direction.LEFT);
        assertTrue(helper.getEntityPosition(dmc, "mercenary0").equals(swampTilePos));

         // Mercenary now should be able to move (Tick 4)
        // Should be off the swamp tile (to the left)
        dmc.tick(Direction.LEFT);
        assertTrue(helper.getEntityPosition(dmc, "mercenary0").
                                            equals(swampTilePos.translateBy(Direction.LEFT)));
    }

    @Test
    @DisplayName("Test zombie gets stuck on swamp tile")
    public void testSwampTileZombieSlowed() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        dmc.newGame("d_swampTest_zombieMovement", "c_simple");

        // We are on the zero tick
        
        // Move left - we should now be on tick 1
        // Zombie will have moved onto a tile
        dmc.tick(Direction.LEFT);
        Position zombiePos = helper.getEntityPosition(dmc, "zombie_toast0");

        // Move player to advance tick, zombie should not have moved
        dmc.tick(Direction.LEFT);
        assertTrue(helper.getEntityPosition(dmc, "zombie_toast0").equals(zombiePos));

        // Move player to advance tick, mercenary should still be at swamp tile (Tick 3)
        dmc.tick(Direction.LEFT);
        assertTrue(helper.getEntityPosition(dmc, "zombie_toast0").equals(zombiePos));

        // Zombie should have moved off swamp tile
        dmc.tick(Direction.LEFT);
        assertFalse(helper.getEntityPosition(dmc, "zombie_toast0").equals(zombiePos));
    }

    @Test
    @DisplayName("Test spider gets stuck on swamp tile")
    public void testSwampTileSpiderSlowed() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        dmc.newGame("d_swampTest_spiderMovement", "c_simple");

        // We are on the zero tick
        
        // Move left - we should now be on tick 1
        // Spider will have moved onto a tile
        dmc.tick(Direction.LEFT);
        Position spiderPos = helper.getEntityPosition(dmc, "spider0");

        // Move player to advance tick, spider should not have moved
        dmc.tick(Direction.LEFT);
        assertTrue(helper.getEntityPosition(dmc, "spider0").equals(spiderPos));

        // Move player to advance tick, spider should still be at swamp tile (Tick 3)
        dmc.tick(Direction.LEFT);
        assertTrue(helper.getEntityPosition(dmc, "spider0").equals(spiderPos));

        // Spider should have moved off swamp tile
        dmc.tick(Direction.LEFT);
        assertFalse(helper.getEntityPosition(dmc, "spider0").equals(spiderPos));
    }

    @Test
    @DisplayName("Test multiple swamps have individual movement factors")
    public void testSwampTileMultipleSwamps() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        dmc.newGame("d_swampTest_multipleSwamps", "c_simple");

        // We are on the zero tick
        Position swampTilePos1 = helper.getEntityPosition(dmc, "swamp_tile0");
        Position swampTilePos2 = helper.getEntityPosition(dmc, "swamp_tile1");
        
        // Move left - we should now be on tick 1
        // Mercenary should have moved onto the swamp tile in this tick 
        dmc.tick(Direction.LEFT);
        assertTrue(helper.getEntityPosition(dmc, "mercenary0").equals(swampTilePos1));

        // Move player to advance tick, mercenary should still be at swamp tile (Tick 2)
        dmc.tick(Direction.LEFT);
        assertTrue(helper.getEntityPosition(dmc, "mercenary0").equals(swampTilePos1));

        // Move player to advance tick, mercenary should still be at swamp tile (Tick 3)
        dmc.tick(Direction.LEFT);
        assertTrue(helper.getEntityPosition(dmc, "mercenary0").equals(swampTilePos1));

        // Mercenary now should be able to move (Tick 4)
        // Should have moved to the next swamp tile 
        // This one lasts 3 ticks
        dmc.tick(Direction.LEFT);
        assertTrue(helper.getEntityPosition(dmc, "mercenary0").equals(swampTilePos2));

        // Move player to advance tick, mercenary should still be at swamp tile (Tick 5)
        dmc.tick(Direction.LEFT);
        assertTrue(helper.getEntityPosition(dmc, "mercenary0").equals(swampTilePos2));

        // Move player to advance tick, mercenary should still be at swamp tile (Tick 6)
        dmc.tick(Direction.LEFT);
        assertTrue(helper.getEntityPosition(dmc, "mercenary0").equals(swampTilePos2));

        // Move player to advance tick, mercenary should still be at swamp tile (Tick 7)
        dmc.tick(Direction.LEFT);
        assertTrue(helper.getEntityPosition(dmc, "mercenary0").equals(swampTilePos2));

        // Mercenary should be able to move off swamp tile now
        dmc.tick(Direction.LEFT);
        assertTrue(helper.getEntityPosition(dmc, "mercenary0").equals(swampTilePos2.translateBy(Direction.LEFT)));

    }

}
