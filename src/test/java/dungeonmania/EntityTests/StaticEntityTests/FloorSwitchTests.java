package dungeonmania.EntityTests.StaticEntityTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.Dungeon;
import dungeonmania.DungeonManiaController;
import dungeonmania.Helper;
import dungeonmania.Entities.Entity;
import dungeonmania.Entities.StaticEntities.FloorSwitchEntity;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class FloorSwitchTests {

    Helper helper = new Helper();

    // Test floorswitch creation
    @Test
    @DisplayName("test constructor for floorswitch operates correctly")
    public void testUnitSwitchCreation() {
        // constructor takes in dungeon filename, config filename, and int representing how many dungeons have been created (passed in from controller)
        FloorSwitchEntity floorswitch = new FloorSwitchEntity(helper.createEntityJSON("switch","1", "0"), 0);
        
        Position expectedPosition = new Position(1, 0);
        
        // check id is equal
        assertTrue(expectedPosition.equals(floorswitch.getEntityResponse().getPosition()));
        assertTrue("switch0".equals(floorswitch.getEntityResponse().getId()));
    }

    @Test
    @DisplayName("WhiteBox test to check floorswitchs are created correctly in dungeonController")
    public void testControllerSwitchCreation() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_floorswitchTest_basicCollision", "c_floorswitchTest_playerCollision");
        Dungeon dungeon = dmc.currentDungeon;

        List<Entity> entities = dungeon.getEntities();

        FloorSwitchEntity floorswitch = new FloorSwitchEntity(helper.createEntityJSON("switch","4", "2"), 0);

        assertTrue(helper.assertEntityExistsInList(entities, floorswitch));

        for (Entity e : entities) {
            if (e.getType().equals("switch")){
                assertTrue(e.getClass().getSimpleName().equals("FloorSwitchEntity"));
            }
        }
    }

    // Test player can stand on switch
    @Test
    @DisplayName("Test player can stand on switch")
    public void testPlayerStandOnSwitch() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        dmc.newGame("d_floorswitchTest_basicCollision", "c_floorswitchTest_playerCollision");
        dmc.tick(Direction.UP);

        // The floorswitch is above the player. The player should just be able to move up and stand on it

        checkSwitchPlayerPos(dmc, new Position(4, 2), "switch0");

    }


    // Check if an instantiated switch is inactive by default
    @Test
    @DisplayName("Instantiated switch is in InactiveState")
    public void testSwitchInactiveStateDefault() {
        Dungeon dungeon = new Dungeon("d_floorswitchTest_basicCollision", "c_floorswitchTest_playerCollision", 0);
        for (Entity e : dungeon.getEntities()) {
            if (e instanceof FloorSwitchEntity) {
                FloorSwitchEntity switchEntity = (FloorSwitchEntity) e;
                assertEquals("InactiveState", (switchEntity.getState().getName()));
            }
        }
    }

    // Test player can stand on switch without activating it
    @Test
    @DisplayName("Test player can stand on switch without activating it")
    public void testPlayerStandOnSwitchButNoActive() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        dmc.newGame("d_floorswitchTest_basicCollision", "c_floorswitchTest_playerCollision");

        // Check floorswitch initially inactive
        assertTrue(getState(dmc, "switch0").equals("InactiveState"));

        // The floorswitch is above the player. The player should just be able to move up and stand on it
        dmc.tick(Direction.UP);
        checkSwitchPlayerPos(dmc, new Position(4, 2), "switch0");

        // Check floorswitch still inactive
        assertTrue(getState(dmc, "switch0").equals("InactiveState"));

    }


    // // Test whether pushing a boulder onto the switch will activate it
    @Test
    @DisplayName("Test if a boulder can be pushed onto a switch")
    public void testBoulderOnSwitch() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        dmc.newGame("d_floorswitchTest_basicCollision", "c_floorswitchTest_playerCollision");
    
        // Position the player so they are to the left of the boulder
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.UP);

        
        // Check the switch is currently not active
        assertTrue(getState(dmc, "switch0").equals("InactiveState"));
        

        // Push the boulder onto the switch
        dmc.tick(Direction.RIGHT);
        // Check that the boulder and switch are in the same place

        checkBoulderSwitchPos(dmc, new Position(4, 2), "switch0", "boulder0");

        
        assertTrue(getState(dmc, "switch0").equals("ActiveState"));
        

        // Now push the boulder off the switch and check that switch is now inactive
        dmc.tick(Direction.RIGHT);

        
        assertTrue(getState(dmc, "switch0").equals("InactiveState"));
        

    }

    // // Test activating multiple switches
    @Test
    @DisplayName("Test multiple boulders and switches")
    public void testMultipleBoulderAndSwitch() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        dmc.newGame("d_floorswitchTest_multipleSwitches", "c_floorswitchTest_playerCollision");

        // Check the switches are currently not active
        assertTrue(getState(dmc, "switch0").equals("InactiveState"));
        assertTrue(getState(dmc, "switch1").equals("InactiveState"));
        assertTrue(getState(dmc, "switch2").equals("InactiveState"));
    
        // Push boulder onto switch0
        // Player now at 3,2
        dmc.tick(Direction.UP);
        assertTrue(getState(dmc, "switch0").equals("ActiveState"));
        dmc.tick(Direction.DOWN);

        dmc.tick(Direction.RIGHT);

        dmc.tick(Direction.UP);
        assertTrue(getState(dmc, "switch1").equals("ActiveState"));
        dmc.tick(Direction.DOWN);

        dmc.tick(Direction.RIGHT);

        dmc.tick(Direction.UP);
        assertTrue(getState(dmc, "switch2").equals("ActiveState"));

        // Now push the boulder off the switch and check that switch is now inactive
        dmc.tick(Direction.UP);
        assertTrue(getState(dmc, "switch2").equals("InactiveState"));
    }

    
    private String getState(DungeonManiaController dmc, String switchId) {
        Dungeon currDungeon = dmc.currentDungeon;
        String name = null;
        for (Entity e : currDungeon.getEntities()) {
            if (e.getId().equals(switchId)) {
                name = ((FloorSwitchEntity) e).getState().getName();
            } 
        }
        return name;
    }
    
    private void checkSwitchPlayerPos(DungeonManiaController dmc,
                                        Position expected, 
                                        String switchId) {
        DungeonResponse res = dmc.getDungeonResponseModel();
        Boolean foundPlayer = false;
        for (EntityResponse e : res.getEntities()) {
            if (e.getType().equals("player")) {
                foundPlayer = true;
                // Assert the player is where the switch is
                assertTrue(e.getPosition().equals(expected));
            } else if (e.getId().equals(switchId)) {
                // Assert the switch position
                assertTrue(e.getPosition().equals(expected));
            }
        }
        assertTrue(foundPlayer);
    }

    private void checkBoulderSwitchPos(DungeonManiaController dmc,
                                        Position expected, String switchId,
                                        String boulderId) {
        DungeonResponse res = dmc.getDungeonResponseModel();
        for (EntityResponse e : res.getEntities()) {
            if (e.getId().equals(switchId)) {
                // Switch is in expected position
                assertTrue(e.getPosition().equals(expected));
            } else if (e.getId().equals(boulderId)) {
                // Boulder is in expected position
                assertTrue(e.getPosition().equals(expected));
            }
        }
    }


}
