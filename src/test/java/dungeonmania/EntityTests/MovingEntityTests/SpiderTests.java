package dungeonmania.EntityTests.MovingEntityTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.Dungeon;
import dungeonmania.DungeonManiaController;
import dungeonmania.Entities.MoveableEntities.SpiderEntity;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class SpiderTests {
    @Test
    @DisplayName("Test spider spawns based on spawn rate (2 ticks)- might fail when battles are implemented")
    public void testSpiderSpawning2ticks() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_spiderTest_spawning", "c_spiderTest_spawning2ticks");
        // will need to modify config so other things dont spawn
        Integer entityCount = dmc.getDungeonResponseModel().getEntities().size();
        
        // test a spider spawns every tick
        dmc.tick(Direction.UP);
        assertEquals(entityCount, dmc.getDungeonResponseModel().getEntities().size()); 
        dmc.tick(Direction.UP);
        assertEquals(entityCount + 1, dmc.getDungeonResponseModel().getEntities().size()); 
        entityCount += 1;
        dmc.tick(Direction.UP);
        assertEquals(entityCount, dmc.getDungeonResponseModel().getEntities().size()); 
    }

    @Test
    @DisplayName("Test spider spawns based on spawn rate (1 ticks)- might fail when battles are implemented")
    public void testSpiderSpawning1ticks() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_spiderTest_spawning", "c_spiderTest_spawning");
        // will need to modify config so other things dont spawn
        Integer entityCount = dmc.getDungeonResponseModel().getEntities().size();
        
        // test a spider spawns every tick
        dmc.tick(Direction.UP);
        assertEquals(entityCount + 1, dmc.getDungeonResponseModel().getEntities().size()); 
        entityCount += 1;
        dmc.tick(Direction.UP);
        assertEquals(entityCount + 1, dmc.getDungeonResponseModel().getEntities().size()); 
        entityCount += 1;
        dmc.tick(Direction.UP);
        assertEquals(entityCount + 1, dmc.getDungeonResponseModel().getEntities().size()); 
    }

    @Test
    @DisplayName("Test spider spawns based on spawn rate - might fail when battles are implemented")
    public void testSpiderStats() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_spiderTest_basicMovement", "c_spiderTest_basicMovement");
        Dungeon dungeon = dmc.currentDungeon;
        SpiderEntity spider = (SpiderEntity) dungeon.getEntities()
                                                    .stream()
                                                    .filter(e -> e.getType().equals("spider"))
                                                    .collect(Collectors.toList())
                                                    .get(0);
        
        assertEquals(spider.getAttackDamage(), Double.valueOf(1));
        assertEquals(spider.getHealth(), Double.valueOf(5));

    }
    
    @Test
    @DisplayName("Test movement of spider upon spawning")
    public void testSpiderMovement() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_spiderTest_basicMovement", "c_spiderTest_basicMovement");
        DungeonResponse dungeon = dmc.getDungeonResponseModel();
        EntityResponse spider = findSpider(dungeon);
        assertNotNull(spider);
        Position initial = new Position(5, 5);
        assertTrue(spider.getPosition().equals(initial));

        dmc.tick(Direction.UP);
        dungeon = dmc.getDungeonResponseModel();
        spider = findSpider(dungeon);
        assertTrue(spider.getPosition().equals(new Position(5, 4)));

        dmc.tick(Direction.UP);
        dungeon = dmc.getDungeonResponseModel();
        spider = findSpider(dungeon);
        assertEquals(spider.getPosition(), new Position(6, 4));

        for (int i  = 0; i < 10; i++) {
            dmc.tick(Direction.UP);
        }
        dungeon = dmc.getDungeonResponseModel();
        spider = findSpider(dungeon);
        assertEquals(spider.getPosition(), new Position(6, 6));
    }

    private EntityResponse findSpider(DungeonResponse dungeon) {
        List<EntityResponse> entities = dungeon.getEntities();
        return entities.stream()
                .filter(e-> e.getType().equals("spider"))
                .collect(Collectors.toList())
                .get(0);
    }

    @Test
    @DisplayName("Test spider boulder interaction")
    public void testSpiderMovementBoulder() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_spiderTest_boulderMovement", "c_spiderTest_basicMovement");
        DungeonResponse dungeon = dmc.getDungeonResponseModel();
        EntityResponse spider = findSpider(dungeon);
        assertNotNull(spider);
        Position initial = new Position(5, 5);
        assertTrue(spider.getPosition().equals(initial));

        dmc.tick(Direction.UP);
        dungeon = dmc.getDungeonResponseModel();
        spider = findSpider(dungeon);
        assertTrue(spider.getPosition().equals(new Position(5, 4)));

        dmc.tick(Direction.UP);
        dungeon = dmc.getDungeonResponseModel();
        spider = findSpider(dungeon);
        assertEquals(spider.getPosition(), new Position(4, 4));

        for (int i  = 0; i < 7; i++) {
            dungeon = dmc.getDungeonResponseModel();
            spider = findSpider(dungeon);
            dmc.tick(Direction.UP);
        }
        dungeon = dmc.getDungeonResponseModel();
        spider = findSpider(dungeon);
        assertEquals(spider.getPosition(), new Position(5, 6));
    }
}
