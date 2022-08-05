package dungeonmania;

import dungeonmania.Buildables.BuildablesFactory;
import dungeonmania.Entities.Entity;
import dungeonmania.Entities.Interactable;
import dungeonmania.Entities.Player;
import dungeonmania.Entities.CollectableEntities.CollectableEntity;
import dungeonmania.Entities.CollectableEntities.UnplacedBombEntity;
import dungeonmania.Entities.CollectableEntities.Potions.Potion;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.FileLoader;
import dungeonmania.util.Position;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Queue;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONObject;

public class DungeonManiaController {
    public Integer dungeonCounter = 0;
    public Dungeon currentDungeon;
    private static final int timeTravelPortalTicks = 30;

    public String getSkin() {
        return "default";
    }

    public String getLocalisation() {
        return "en_US";
    }

    /**
     * /dungeons
     */
    public static List<String> dungeons() {
        return FileLoader.listFileNamesInResourceDirectory("dungeons");
    }

    /**
     * /configs
     */
    public static List<String> configs() {
        return FileLoader.listFileNamesInResourceDirectory("configs");
    }

    /**
     * /game/new
     */
    public DungeonResponse newGame(String dungeonName, String configName) throws IllegalArgumentException {
        Dungeon dungeon;
        dungeon = new Dungeon(dungeonName, configName, dungeonCounter);
        this.dungeonCounter += 1;
        this.currentDungeon = dungeon;
        return dungeon.getDungeonResponse();
    }

    /**
     * /game/dungeonResponseModel
     */
    public DungeonResponse getDungeonResponseModel() {
        return this.currentDungeon.getDungeonResponse();
    }

    /**
     * /game/tick/item
     */
    public DungeonResponse tick(String itemUsedId) throws IllegalArgumentException, InvalidActionException {
        currentDungeon.updateTimeTravelSaves();
        currentDungeon.incrementTick();
        currentDungeon.spawnForTick();

        Player player = currentDungeon.getPlayer();
        CollectableEntity item = currentDungeon.getFromInventory(itemUsedId);

        if (item == null) {
            throw new InvalidActionException("item not found");
        } else if (!item.getType().equals("bomb") &&
            !item.getType().equals("invincibility_potion") &&
            !item.getType().equals("invisibility_potion")) {
            throw new IllegalArgumentException("item is not usable from inventory");
        }

        if (item.getType().equals("invisibility_potion") || item.getType().equals("invincibility_potion")) {
            Potion potion = (Potion) item;
            potion.use(player);
        } else if (item instanceof UnplacedBombEntity) {
            UnplacedBombEntity bomb = (UnplacedBombEntity) item;
            currentDungeon.addObserver(bomb.place(currentDungeon));
            List<Entity> toDetonate = currentDungeon.getEntitiesToDetonate();
            for (Entity e : toDetonate) {
                currentDungeon.removeObserver(e);
            }
        }
        currentDungeon.tickPlayerStatus();
        currentDungeon.notifyObservers();
        currentDungeon.checkBattle();
        

        return currentDungeon.getDungeonResponse();
    }

    /**
     * /game/tick/movement
     */
    public DungeonResponse tick(Direction movementDirection) {
        currentDungeon.updateTimeTravelSaves();
        currentDungeon.incrementTick();
        currentDungeon.spawnForTick();

        currentDungeon.inputMove(movementDirection);
        
        currentDungeon.notifyObservers();
        currentDungeon.checkBattle();
        
        currentDungeon.tickPlayerStatus();

        // If player walks on time travelling portal, change the dungeon
        if (currentDungeon.getTimeTravelDungeon() != null) {
            Player currPlayer = currentDungeon.getPlayer();
            Queue<JSONObject> states = currentDungeon.getTimeTraveller().getJSONforTicks(timeTravelPortalTicks);
            currentDungeon = new Dungeon(currentDungeon.getTimeTravelDungeon(), true);
            currentDungeon.setPlayer(currPlayer);
            currentDungeon.setOldPlayerStates(states);
        }

        return getDungeonResponseModel();
    }

    /**
     * /game/build
     */
    public DungeonResponse build(String buildable) throws IllegalArgumentException, InvalidActionException {
        BuildablesFactory buildablesFactory = currentDungeon.getBuildablesFactory();

        if (!buildablesFactory.isBuildable(buildable)) {
            throw new IllegalArgumentException(buildable + "is not a valid buildable item");
        }
        
        if (!buildablesFactory.playerCanBuild(buildable)) {
            throw new InvalidActionException("Not enough materials to make a " + buildable + ".");
        }

        buildablesFactory.buildItem(buildable);

        return currentDungeon.getDungeonResponse();

    }

    /**
     * /game/interact
     */
    public DungeonResponse interact(String entityId) throws IllegalArgumentException, InvalidActionException {
        Interactable interactable = (Interactable) currentDungeon.getEntityById(entityId);
        if (interactable == null) {
            throw new IllegalArgumentException();
        }
        interactable.interact(currentDungeon);
        return currentDungeon.getDungeonResponse();
    }

    /**
     * /game/save
     */
    public DungeonResponse saveGame(String name) throws IllegalArgumentException {
        // SaveUtility saveUtility = new SaveUtility();
        JSONObject save = SaveUtility.saveGameToJSON(currentDungeon);
        SaveUtility.JSONToFile(save, name);
        return this.getDungeonResponseModel();
    }

    /**
     * /game/load
     */
    public DungeonResponse loadGame(String name) throws IllegalArgumentException {
        if (this.allGames().contains(name)) {
            JSONObject saveGame;
            try {
                saveGame = SaveUtility.FileToJSON(name);
            } catch (IOException e) {
                throw new IllegalArgumentException("something went wrong");
            }
            Dungeon load = new Dungeon(saveGame, false);

            this.dungeonCounter += 1;
            this.currentDungeon = load;

            return currentDungeon.getDungeonResponse();

        } else {
            throw new IllegalArgumentException("save does not exist");
        }
    }

    /**
     * /games/all
     */
    public List<String> allGames() {
        return Stream.of(new File("src/main/resources/gameSaves/").listFiles())
        .filter(file -> !file.isDirectory())
        .map(File::getName)
        .map(s -> s.substring(0, s.length() - 5))
        .collect(Collectors.toList());
        // return FileLoader.listFileNamesInResourceDirectory("gameSaves");
    }

    /**
     * /game/rewind
     */
    public DungeonResponse rewind(int ticks) throws IllegalArgumentException {
        if (ticks <= 0) throw new IllegalArgumentException("Rewind must be >= 0 ticks.");
        if (ticks > currentDungeon.getTick())throw new IllegalArgumentException(ticks + " ticks have not occurred yet") ;

        Player currPlayer = currentDungeon.getPlayer();
        Queue<JSONObject> states = currentDungeon.getTimeTraveller().getJSONforTicks(ticks);
        currentDungeon = new Dungeon(currentDungeon.getTimeTraveller().rewind(ticks), true);
        currentDungeon.setPlayer(currPlayer);
        currentDungeon.setOldPlayerStates(states);

        return currentDungeon.getDungeonResponse();
    }
    
    /**
     * /game/new/generate
     */
    public DungeonResponse generateDungeon(int xStart, int yStart, 
                                           int xEnd, int yEnd, String configName)
                                           throws IllegalArgumentException {

        Map<Position, String> map = GenerationUtility.randomisedPrims(xStart, yStart, xEnd, yEnd);
        JSONObject object = GenerationUtility.convertMapToJson(map);
        
        Dungeon dungeon;
        dungeon = new Dungeon(object, configName, dungeonCounter);
        
        this.dungeonCounter += 1;
        this.currentDungeon = dungeon;
        return dungeon.getDungeonResponse();     
    }

}
