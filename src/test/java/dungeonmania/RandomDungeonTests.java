package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Position;

import static dungeonmania.TestUtils.getEntities;

public class RandomDungeonTests {
    @Test
    @DisplayName("Test player and exit exist in randomly generated dungeon")
    public void testRandomDungeonPlayerExitExist() {
        DungeonManiaController dmc = new DungeonManiaController();

        // Repeat tests for multiple dungeon
        for (int it = 0; it < 6; it++) {
            dmc.generateDungeon(0, 0, 6, 6, "c_simple");
            DungeonResponse res = dmc.getDungeonResponseModel();
            
            Dungeon curr = dmc.currentDungeon;
            assertTrue(curr.getPlayerPosition().equals(new Position(0, 0)));

            assertEquals(1, getEntities(res, "exit").size());
            assertTrue(getEntities(res, "exit").get(0).getPosition().equals(new Position(6, 6)));
        }
    }

    @Test
    @DisplayName("Test there is a boundary wall around the maze")
    public void testRandomDungeonBoundaryExists() {
        DungeonManiaController dmc = new DungeonManiaController();
        for (int it = 0; it < 6; it++) {
            dmc.generateDungeon(1, 1, 3 + it, 3 + it, "c_simple");
            Dungeon curr = dmc.currentDungeon;

            for (int i = 0; i <= 4; i++) {
                curr.getEntityAtPosition("wall", new Position(i, 0));
                curr.getEntityAtPosition("wall", new Position(i, 4 + it));
            }

            for (int i = 1; i <= 3; i++) {
                curr.getEntityAtPosition("wall", new Position(0, i));
                curr.getEntityAtPosition("wall", new Position(4 + it, i));
            }
        }
    }
}
