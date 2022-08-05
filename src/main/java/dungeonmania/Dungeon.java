package dungeonmania;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import dungeonmania.Buildables.Buildable;
import dungeonmania.Buildables.BuildablesFactory;
import dungeonmania.Entities.Entity;
import dungeonmania.Entities.Player;
import dungeonmania.Entities.PlayerEntity;
import dungeonmania.Entities.CollectableEntities.CollectableEntity;
import dungeonmania.Entities.MoveableEntities.MercenaryEntity;
import dungeonmania.Entities.MoveableEntities.MoveableEntity;
import dungeonmania.Entities.MoveableEntities.SpiderEntity;
import dungeonmania.Entities.MoveableEntities.MercenaryStates.BribedMercenaryState;
import dungeonmania.Entities.StaticEntities.FloorSwitchEntity;
import dungeonmania.Entities.StaticEntities.PlacedBombEntity;
import dungeonmania.Entities.StaticEntities.PortalEntity;
import dungeonmania.Entities.StaticEntities.LogicEntities.LogicEntity;
import dungeonmania.Entities.StaticEntities.StaticEntity;
import dungeonmania.Goals.BasicGoal;
import dungeonmania.Goals.ComplexGoal;
import dungeonmania.Goals.GoalComponent;
import dungeonmania.ObserverPatterns.DungeonObserver;
import dungeonmania.ObserverPatterns.DungeonSubject;
import dungeonmania.Battles.Battle;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.FileLoader;
import dungeonmania.util.Position;

/**
 * The dungeon generated based on dungeon and config files that the player plays on.
 * @authors Luis Reyes (z5206766) & Stan Korotun (z5637728) & Nancy Huynh (z5257042) & Jordan Liang (z5254761)
 */
public class Dungeon implements DungeonSubject{
    private String dungeonId;
    private String dungeonName;
    private List<Entity> entities = new ArrayList<Entity>();
    private List<Entity> entitiesToDetonateThisTick = new ArrayList<Entity>();
    private Player player;
    private Integer currentTick = 0;
    private EntityFactory entityFactory;
    private JSONObject config;
    private GoalComponent goal;
    private JSONObject originalGoal;
    private BuildablesFactory buildablesFactory;
    private Integer numSpidersSpawned = 0;
    private Integer numZombiesSpawned = 0;
    private int enemiesKilled = 0;
    private List<Battle> battleList = new ArrayList<Battle>();
    private TimeTravel timeTraveller;
    private JSONObject timeTravelDungeon = null;

    public Dungeon(String dungeonPath, String configPath, Integer uniqueId) throws IllegalArgumentException {
        // convert resource files to JSONS
        JSONObject dungeon = new JSONObject(JSONtoString(dungeonPath, true));
        JSONObject configJson = new JSONObject(JSONtoString(configPath, false));

        // create entity factory
        this.entityFactory = new EntityFactory(configJson);

        // Create config object
        config = configJson;

        // get a list of entities from dungeon
        JSONArray fileEntities = dungeon.getJSONArray("entities");
        Position playerStartingPosition = new Position(0, 0);

        // go through list of entities and add each one by one
        for (Integer i = 0; i < fileEntities.length(); i++) {
            JSONObject JSONentity = (JSONObject) fileEntities.get(i);
            // if the entity is the player just extract position and move on
            if (JSONentity.getString("type").equals("player")) {
                playerStartingPosition = new Position(JSONentity.getInt("x"), JSONentity.getInt("y"));
            } else {
                // need some logic to choose entity
                Entity typedEntity = this.entityFactory.createEntity(JSONentity, getNewId(JSONentity.getString("type")));
                this.entities.add(typedEntity);
            }
        }

        // Pair up portals
        pairPortals();
        
        // Fill in other dungeon fields
        dungeonName = dungeonPath;
        dungeonId = String.valueOf(uniqueId);

        originalGoal = dungeon.getJSONObject("goal-condition");
        if (dungeon.getJSONObject("goal-condition").has("subgoals")) {
            goal = new ComplexGoal(dungeon.getJSONObject("goal-condition"));
        } else {
            goal = new BasicGoal(dungeon.getJSONObject("goal-condition"));
        }
        
        player = new Player(playerStartingPosition, getConfigParam("player_attack"), getConfigParam("player_health"));

        // Buildables
        buildablesFactory = new BuildablesFactory(this);

        // Time traveller
        timeTraveller = new TimeTravel(this);
    }

    public Dungeon(JSONObject saveGame, boolean timeTravel) {
        // Create config object
        this.config = saveGame.getJSONObject("config");

        // create entity factory
        this.entityFactory = new EntityFactory(this.config);

        // get a list of entities from dungeon
        JSONArray fileEntities = saveGame.getJSONArray("entities");

        // generate entities
        for (Integer i = 0; i < fileEntities.length(); i++) {
            JSONObject JSONentity = fileEntities.getJSONObject(i);
            this.entities.add(entityFactory.loadEntity(JSONentity, 0));
        }

        // arm placed bombs
        for (Entity e : this.entities) {
            if (e instanceof PlacedBombEntity) {
                PlacedBombEntity bomb = (PlacedBombEntity) e;
                bomb.updateSwitches(this);
            }
        }

        // player 
        if (!timeTravel) {
            JSONObject playerData = saveGame.getJSONObject("player");
            Position playerStart = new Position(
                playerData.getInt("curr_position_x"), 
                playerData.getInt("curr_position_y")
            );
            this.player = new Player(playerStart, playerData.getDouble("attack"), playerData.getDouble("health"));
            this.player.loadPlayer(playerData, this.entityFactory);
            this.player.loadInventory(saveGame.getJSONObject("inventory"), this.entityFactory);
            buildablesFactory = new BuildablesFactory(this);
        } 

        // Pair up portals
        pairPortals();
        
        // fill in other dungeon fields
        this.dungeonName = saveGame.getString("dungeonName");
        this.dungeonId = saveGame.getString("dungeonId");
        this.currentTick = saveGame.getInt("current_tick");
        this.numSpidersSpawned = saveGame.getInt("numSpidersSpawned");
        this.numZombiesSpawned = saveGame.getInt("numZombiesSpawned");
        this.enemiesKilled = saveGame.getInt("enemiesKilled");

        originalGoal = saveGame.getJSONObject("goal-condition");
        if (originalGoal.has("subgoals")) {
            goal = new ComplexGoal(saveGame.getJSONObject("goal-condition"));
        } else {
            goal = new BasicGoal(saveGame.getJSONObject("goal-condition"));
        }

        timeTraveller = new TimeTravel(this);
    }

    public Dungeon(JSONObject dungeon, String configPath, Integer uniqueId) {
        JSONObject configJson = new JSONObject(JSONtoString(configPath, false));

        // create entity factory
        this.entityFactory = new EntityFactory(configJson);

        // Create config object
        config = configJson;

        // get a list of entities from dungeon
        JSONArray fileEntities = dungeon.getJSONArray("entities");
        Position playerStartingPosition = new Position(0, 0);

        // go through list of entities and add each one by one
        for (Integer i = 0; i < fileEntities.length(); i++) {
            JSONObject JSONentity = (JSONObject) fileEntities.get(i);
            // if the entity is the player just extract position and move on
            if (JSONentity.getString("type").equals("player")) {
                playerStartingPosition = new Position(JSONentity.getInt("x"), JSONentity.getInt("y"));
            } else {
                // need some logic to choose entity
                Entity typedEntity = this.entityFactory.createEntity(JSONentity, getNewId(JSONentity.getString("type")));
                this.entities.add(typedEntity);
            }
        }

        // Fill in other dungeon fields
        // dungeonName = dungeonPath;
        dungeonId = String.valueOf(uniqueId);
        dungeonName = "random" + dungeonId;

        originalGoal = dungeon.getJSONObject("goal-condition");
        if (dungeon.getJSONObject("goal-condition").has("subgoals")) {
            goal = new ComplexGoal(dungeon.getJSONObject("goal-condition"));
        } else {
            goal = new BasicGoal(dungeon.getJSONObject("goal-condition"));
        }
        
        player = new Player(playerStartingPosition, getConfigParam("player_attack"), getConfigParam("player_health"));
        // Buildables
        buildablesFactory = new BuildablesFactory(this);
        timeTraveller = new TimeTravel(this);
    }

    public JSONObject getOriginalGoal() {
        return this.originalGoal;
    }

    /**
     * get tick dungeon is currently on since newGame
     * @return Integer tick
     */
    public Integer getTick() {
        return this.currentTick;
    }

    /**
     * increment to next tick
     */
    public void incrementTick() {
        this.currentTick += 1;
    }

    public JSONObject getConfig() {
      return config;
    }

    /**
     * Go through the entity list and store portal counterparts in each portal
     */
    private void pairPortals() {
        // Get a list of all the portal entities typecasted
        List<PortalEntity> portals = entities.stream()
                                                .filter(p -> p.getType().equals("portal"))
                                                .map(p -> (PortalEntity) p)
                                                .collect(Collectors.toList());
        
        // Find counterpart portal for every portal
        for (PortalEntity p: portals) {
            List<PortalEntity> others = portals.stream()
                                                .filter(other -> other.getOtherPortal() == null)
                                                .filter(other -> !other.equals(p) && other.getColour().equals(p.getColour()))
                                                .collect(Collectors.toList());
            
            // Set other side of portal in every portal
            if (others.size() > 0) {
                p.setOtherPortal(others.get(0));
                others.get(0).setOtherPortal(p);
            }
        }
    }

    /**
     * return a list of entities of a certain type given a String
     */
    public List<Entity> getEntitiesOfType(String s) {
        return entities.stream()
        .filter(ent -> (ent.getType().equals(s)))
        .collect(Collectors.toList());
    }

    /**
     * Get current number of allies in the dungeon
     * @return int number of allies
     */
    public int numAllies() {
        List<Entity> mercenaries = getEntitiesOfType("mercenary");
        List<Entity> assassins = getEntitiesOfType("assassin");
        mercenaries.addAll(assassins);

        int count = 0;
        for (Entity entity : mercenaries) {
            MercenaryEntity merc = (MercenaryEntity) entity;
            if (merc.getState() instanceof BribedMercenaryState) {
                count = count + 1;
            }
        }
        return count;
    }

    /**
     * Assuming there is at most one of each type of entity on a square
     * Go through list of entities on a position and if a entity of certain type exists return it
     * @param type
     * @return entity of specified type on position or null if there is no such entity
     */
    public Entity getEntityById(String id) {
        return entities.stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);
    }
    
    /**
     * Find a list of entities on a given posiiton
     * @param position
     * @return list of entities on a given position
     */
    public List<Entity> getEntitiesAtPosition(Position position) {
        return entities.stream().filter(e -> e.getPosition().equals(position)).collect(Collectors.toList());
    }

    /**
     * Assuming there is at most one of each type of entity on a square
     * Go through list of entities on a position and if a entity of certain type exists return it
     * @param type
     * @return entity of specified type on position or null if there is no such entity
     */
    public Entity getEntityAtPosition(String type, Position position) {
        for (Entity e: getEntitiesAtPosition(position)) {
            if (e.getType().equals(type)) return e;
        }

        return null;
    }

    /**
     * Given a position, check if there is a switch there and return instance of it if present
     */
    public FloorSwitchEntity getSwitchAtPosition(Position position) {
        for (Entity entity : this.getEntitiesOfType("switch")) {
            FloorSwitchEntity fs = (FloorSwitchEntity) entity;
            if (fs.getPosition().equals(position)) {
                // There is an switch at the requested position
                return fs;
            }
        }
        // Returns null if not found
        return null;
    }

    /**
     * spawn behaviour check for each tick based on config files
     */
    public void spawnForTick() {
        // spider spawn
        if ((int) getConfigParam("spider_spawn_rate") != 0 && currentTick % (int) getConfigParam("spider_spawn_rate") == 0) {
            HashMap<String, String> spiderData = new HashMap<String, String>();
            Random random = new Random();

            // get a 20x20 box relative to the player
            Integer bounds = 10;
            Integer playerX = player.getCurrentPosition().getX();
            Integer playerY = player.getCurrentPosition().getY();

            Integer randomXPosition = (playerX - bounds) + random.nextInt(2*bounds);
            Integer randomYPosition = (playerY - bounds) + random.nextInt(2*bounds);

            // if spider spawns directly on player move the spawn point to reduce confusion
            if (playerX == randomXPosition && playerY == randomYPosition) {
                randomXPosition += 1;
                randomYPosition -= 1;
            }

            spiderData.put("type", "spider");
            spiderData.put("x", String.valueOf(randomXPosition));
            spiderData.put("y", String.valueOf(randomYPosition));
            //String spiderId = "spider" + numSpidersSpawned;
            numSpidersSpawned +=1;

            JSONObject spiderJSON = new JSONObject(spiderData);

            DungeonObserver newSpider = new SpiderEntity(
                spiderJSON, 
                numSpidersSpawned, 
                getConfigParam("spider_health"), 
                getConfigParam("spider_attack")
            );

            addObserver(newSpider);
        }
    }

    public int getNumSpidersSpawned() {
        return numSpidersSpawned;
    }

    /**
     * get player in dungeon
     * @return Player player
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * generate a unique id for given entity type
     * @param type String entity type
     * @return unique Integer for Id
     */
    public Integer getNewId(String type) {
        
        Integer counter = 0;
        for (Entity e : this.entities) {
            if (e.getEntityResponse().getType().equals(type)) {
                counter += 1;
            }
        }
        return counter; 
    }

    /**
     * generate a dungeonResponse
     * @return DungeonResponse
     */
    public DungeonResponse getDungeonResponse() {
        /// Entities ///
        List<EntityResponse> entityResponses = new ArrayList<EntityResponse>();
        for (Entity e : this.entities) {
            entityResponses.add(e.getEntityResponse());
        }
        if (player.getIsAlive()) {
            entityResponses.add(this.player.getEntityResponse());
        }

        /// Inventory ///
        List<ItemResponse> inventory = new ArrayList<ItemResponse>();
        // Collectables
        for (CollectableEntity e : player.getInventory()) {
            inventory.add(e.getItemResponse());
        }
        // Built items
        for (Buildable b: player.getBuiltItems()) {
            inventory.add(b.getItemResponse());
        }

        /// Battles ///
        List<BattleResponse> battles = new ArrayList<BattleResponse>();
        for (Battle b : battleList) {
            battles.add(b.getBattleResponse());
        }

        /// Goals ///
        String goals = goal.checkSuccess(this) ? null : goal.getGoal(this);

        return new DungeonResponse(this.dungeonId, this.dungeonName, entityResponses, inventory, battles, buildablesFactory.getBuildables(), goals);
    }

    /**
     * get the list of entities in the dungeon currently
     * @return List<Entity>
     */
    public List<Entity> getEntities() {
        return entities;
    }

    /**
     * add an observer to the observer list (entities)
     */
    public void addObserver(DungeonObserver obj) {
        Entity entity = (Entity) obj;
        this.entities.add(entity);
    }


    /**
     * remove an observer to the observer list (entities)
     */
    public void removeObserver(DungeonObserver obj) {
        Entity entity = (Entity) obj;
        this.entities.remove(entity);
    }

    /**
     * notify all observers
     */
    public void notifyObservers() {
        for (int i = (entities.size() - 1); i > -1; i--) { //boulders need to be updated first in order for correct bomb detonation behaviour
            Entity item = entities.get(i);
            if (item.getType().equals("boulder")) {
                item.update(this);
            }
            
        }

        for (int i = (entities.size() - 1); i > -1; i--) { // Update static entities
            Entity item = entities.get(i);
            if (!item.getType().equals("boulder") && item instanceof StaticEntity) {
                item.update(this);
            }
        }

        

        List<Entity> logicEntitiesList = new ArrayList<>();
        //check logic entities here
        for (int i = (entities.size() - 1); i > -1; i--) { //non-boulder entities are updated here
            Entity item = entities.get(i);
            if (item instanceof LogicEntity) { //will need to be refactored but i think its fine for now
                //add to logic entities list
                logicEntitiesList.add(item);
            }
        }

        //perform checking on the logic entitites list
        boolean atLeastOneChanged = false;
        do {
            atLeastOneChanged = false;
            for (Entity ent : logicEntitiesList) {
                ent.update(this);
                LogicEntity logicalEnt = (LogicEntity) ent;
                if (logicalEnt.hasBeenChanged() == true) {
                    atLeastOneChanged = true;
                }
            }
        } while (atLeastOneChanged == true);
        

        for (int i = (entities.size() - 1); i > -1; i--) { // Update non static entities like mercenaries
            Entity item = entities.get(i);
            if (!(item instanceof StaticEntity)) {
                item.update(this);
            }
        }

        
        for (Entity i : entitiesToDetonateThisTick) { //bomb detonation populates this list, to avoid out of index exceptions the detonated entities are removed like this
            removeObserver(i);
        }
    }

    /**
     * get the name of the dungeon
     * @return String dungeon name
     */
    public String getName() {
        return dungeonName;
    }

    /**
     * get the id of the dungeon
     * @return String dungeon id
     */
    public String getId() {
        return dungeonId;
    }

    /**
     * extract contents of .JSON file to a string
     * @param pathName String pathname of file relative to resources path
     * @param isDungeon boolean is a dungeon (true) or config (false)
     * @return String JSON data
     */
    public String JSONtoString(String pathName, Boolean isDungeon) throws IllegalArgumentException {
        String prePath = isDungeon ? "/dungeons/" : "/configs/";
        
        try {
            String result = FileLoader.loadResourceFile(prePath + pathName + ".json");
            return result;
        } catch (IOException e) {
            throw new IllegalArgumentException("Something went wrong loading the dungeon/config file");
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Invalid dungeon or config filepath");
        }
    }

    /**
     * checks for collision between player and entity
     * @param entity entity to check position
     * @return boolean is the player in collision with something
     */
    public boolean entityCollisionCheck(Entity entity) {
        return this.player.getCurrentPosition().equals(entity.getPosition());
    }

    /**
     * send the player back to their previous position
     */
    public void reversePlayerMovement() {
        this.player.goBack();
    }

    /**
     * Send item to player inventory and remove item from dungeon
     * @param item CollectableEntity
     */
    public void sendToPlayerInventory(CollectableEntity item) {
        this.player.addToInventory(item);
        this.removeObserver((DungeonObserver) item);
    }

    /**
     * get item specified from player inventory
     * @param itemUsedId String id of item to return
     * @return CollectableEntity item
     */
    public CollectableEntity getFromInventory(String itemUsedId) {
        List<CollectableEntity> inventory = player.getInventory();
        CollectableEntity item = null;
        for (CollectableEntity e : inventory) {
            if (e.getId().equals(itemUsedId)) {
                item = e;
            }
        }
        return item;
    }

    /**
     * tick player status effects
     */
    public void tickPlayerStatus() {
        player.updateStatusEffect();
    }

    /**
     * input a player movement to the game
     * @param movementDirection direction of movement
     */
    public void inputMove(Direction movementDirection) {
        player.move(movementDirection);
    }

    /**
     * @return entitiesToDetonateThisTick
     */
    public List<Entity> getEntitiesToDetonate() {
        return this.entitiesToDetonateThisTick;
    }

    /**
     * Set entitiesToDetonateThisTick
     * @param list
     */
    public void setEntitiesToDetonate(List<Entity> list) {
        this.entitiesToDetonateThisTick = list;
    }

    /**
     * discard item from player inventory
     * @param item
     */
    public void discardItem(CollectableEntity item) {
        player.removeFromInventory(item);
    }

    /**
     * Check if player is holding a key currently
     * @return boolean if they have a key in inventory
     */
    public boolean hasKey() {
        return player.getInventory().stream()
            .anyMatch(a -> a.getType().equals("key"));
    }

    /**
     * Check if player is holding a sword currently
     * @return boolean if they have a sword in inventory
     */
    public boolean playerHasSword() {
        return player.hasSword();
    }

    /**
     * Get player's current position in the dungeon
     * @return
     */
    public Position getPlayerPosition() {
        return this.player.getCurrentPosition();
    }

    /**
     * Return list of all moveable entities currently in the dungeon
     * @return
     */
    public List<MoveableEntity> getMoveableEntities() {
        return getEntities().stream().filter(e -> e instanceof MoveableEntity).map(e -> (MoveableEntity)e).collect(Collectors.toList());
    }

    /**
     * Check if a battle needs to be started
     * If so, start the battle and add it to battleList
     */
    public void checkBattle() {
        if (player.getState().getName().equals("InvisibleState")) {
            return;
        }
    
        List <MoveableEntity> moveableEntities = getMoveableEntities();
        for (MoveableEntity e : moveableEntities) {
            if (entityCollisionCheck(e)) { // Start battle if player collides with mob
                if (e instanceof MercenaryEntity && 
                    ((MercenaryEntity) e).getState() instanceof BribedMercenaryState) { // Do not fight with ally
                    return;
                }  else if (e instanceof PlayerEntity && !getOlderPlayer().canBattle()) {
                    return;
                }
                Battle battle = new Battle(this, player, e);
                battle.fightBattle();
                battleList.add(battle);
                if (player.getIsAlive()) enemiesKilled++;
            }
        }
    }

    /**
     * get a value from config
     * @param s String key to access
     * @return double value extracted
     */
    public double getConfigParam(String s) {
        return config.getDouble(s);
    }

    /**
     * Get number of zombies spawned for zombie ids
     * @return
     */
    public int getNumZombiesSpawned() {
        return numZombiesSpawned;
    }

    /**
     * Set number of zombies spawned
     * @param num
     */
    public void setNumZombiesSpawned(int num) {
        numZombiesSpawned = num;
    }

    /**
     * Return the number of enemies killed
     * @return
     */
    public int getEnemiesKilled() {
        return enemiesKilled;
    }

    /**
     * @return whether player has a sceptre
     */
    public boolean hasSceptre() {
        return player.getSceptre() != null;
    }

    /**
     * Get BuildableFactory
     * @return buildable factory with all possible buildable items
     */
    public BuildablesFactory getBuildablesFactory() {
        return buildablesFactory;
    }

    public TimeTravel getTimeTraveller() {
        return timeTraveller;
    }

    public void updateTimeTravelSaves() {
        timeTraveller.updateTimeTravelSaves();
    }

    public void setPlayer(Player player) {
        this.player = player;
        buildablesFactory = new BuildablesFactory(this);
    }

    public void setTimeTravelDungeon(JSONObject timeTravelDungeon) {
        this.timeTravelDungeon = timeTravelDungeon;
    }

    public JSONObject getTimeTravelDungeon() {
        return timeTravelDungeon;
    }

    public PlayerEntity getOlderPlayer() {
        return (PlayerEntity) getEntityById("older_player0");
    }

    public void setOldPlayerStates(Queue<JSONObject> states) {
        getOlderPlayer().setStates(states);
    }

    public EntityFactory getEntityFactory() {
        return entityFactory;
    }
    
}
