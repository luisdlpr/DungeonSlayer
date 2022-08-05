package dungeonmania.Entities;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import dungeonmania.EntityFactory;
import dungeonmania.Buildables.Buildable;
import dungeonmania.Buildables.Sceptre;
import dungeonmania.Entities.CollectableEntities.ArrowEntity;
import dungeonmania.Entities.CollectableEntities.CollectableEntity;
import dungeonmania.Entities.CollectableEntities.KeyEntity;
import dungeonmania.Entities.CollectableEntities.SunStoneEntity;
import dungeonmania.Entities.CollectableEntities.TreasureEntity;
import dungeonmania.Entities.CollectableEntities.WoodEntity;
import dungeonmania.Entities.CollectableEntities.Potions.Potion;
import dungeonmania.Entities.CollectableEntities.Weapons.SwordEntity;
import dungeonmania.Entities.CollectableEntities.Weapons.Weapon;
import dungeonmania.PlayerStates.InvincibleState;
import dungeonmania.PlayerStates.InvisibleState;
import dungeonmania.PlayerStates.NormalState;
import dungeonmania.PlayerStates.PlayerState;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

/**
 * The interactable player that is present within the dungeon
 * @author Luis Reyes (z5206766) & Nancy Huynh (5257042)
 */
public class Player {
    // Player States (state pattern)
    private PlayerState NormalState;
    private PlayerState InvisibleState;
    private PlayerState InvincibleState;

    private double health;
    private double attackDamage;
    private double defensePoints = 0;
    private List<CollectableEntity> inventory = new ArrayList<CollectableEntity>();
    private int numberBuiltItems = 0; // For buildable item ids
    private List<Buildable> itemsBuilt = new ArrayList<Buildable>();
    private Position lastPosition;
    private Position currentPosition;
    private PlayerState state;
    private Queue<Potion> statusEffects = new LinkedList<Potion>();
    private Potion currentPotion = null;
    private boolean isAlive = true;

    /**
     * Constructor taking in key data from config and dungeon files
     * @param startingPosition
     * @param attackDamage
     * @param health
     */
    public Player(Position startingPosition, double attackDamage, double health) {
        this.health = health;
        this.attackDamage = attackDamage;
        this.currentPosition = startingPosition;
        
        // initialise player to normal state.
        NormalState = new NormalState(this);
        InvisibleState = new InvisibleState(this);
        InvincibleState = new InvincibleState(this);

        this.state = NormalState;
    }

    public void loadPlayer(JSONObject playerData, EntityFactory entityFactory) {
        // set last position
        this.setLastPosition(new Position(playerData.getInt("last_position_x"), playerData.getInt("last_position_y")));
        
        // set player state
        String state = playerData.getString("player_state");
        switch(state) {
            case "NormalState":
                this.setState(this.getNormalState());
                break;
            case "InvisibleState":
                this.setState(this.getInvisibleState());
                break;
            case "InvincibleState":
                this.setState(this.getInvincibleState());
                break;
        }
        this.setPotionTimer(playerData.getInt("potion_timer"));

        JSONArray PotionQ = playerData.getJSONArray("status_effects");
        if (!PotionQ.isEmpty()){
            for (int i = 0; i < PotionQ.length(); i ++) {
                JSONObject potionData = PotionQ.getJSONObject(i);
                Entity e = entityFactory.loadEntity(potionData, 0);
                this.statusEffects.add((Potion) e);
            }
        }
        
        JSONObject currentPotionData = playerData.getJSONObject("current_potion");
        if (!currentPotionData.isEmpty()) {
            Entity currentPotion = entityFactory.loadEntity(currentPotionData, 0);
            this.currentPotion = (Potion) currentPotion;
        }

        this.numberBuiltItems = playerData.getInt("item_built_count");
    }

    public void loadInventory(JSONObject inventoryData, EntityFactory entityFactory) {
        JSONArray collectables = inventoryData.getJSONArray("collectables");
        JSONArray buildables = inventoryData.getJSONArray("buildables");

        for (int i = 0; i < collectables.length(); i ++) {
            Entity e = entityFactory.loadEntity(collectables.getJSONObject(i), 0);
            CollectableEntity item = (CollectableEntity) e;
            this.addToInventory(item);
        }

        for (int i = 0; i < buildables.length(); i ++) {
            Buildable item = entityFactory.loadBuildable(buildables.getJSONObject(i));
            this.buildItem(item);
        }
    }

    /**
     * move the player entities currentPosition and update lastPosition based on given direction
     * @param direction Direction specifying direction of movement
     */
    public void move(Direction direction) {
        this.lastPosition = this.currentPosition;
        this.currentPosition = currentPosition.translateBy(direction);
    }

    /**
     * Send player back to their last position (move unsuccessful)
     */
    public void goBack() {
        this.currentPosition = this.lastPosition;
    }

    /**
     * get current state of player
     * @return PlayerState specifies potion state of player
     */
    public PlayerState getState() {
        return this.state;
    }

    /**
     * @return PlayerState a normal player state
     */
    public PlayerState getNormalState() {
        return NormalState;
    }

    /**
     * @return PlayerState an invisible player state
     */
    public PlayerState getInvisibleState() {
        return InvisibleState;
    }

    /**
     * @return PlayerState an invincible player state
     */
    public PlayerState getInvincibleState() {
        return InvincibleState;
    }


    /**
     * adds a collectableEntity to player inventory
     * @param entity CollectableEntity to be collected by player
     */
    public void addToInventory(CollectableEntity entity) {
        this.inventory.add(entity);
    }

    public void removeFromInventory(CollectableEntity entity) {
        this.inventory.remove(entity);
    }

    /**
     * get players current inventory
     * @return List<CollectableEntity>
     */
    public List<CollectableEntity> getInventory() {
        return this.inventory;
    }

    /**
     * get players current health
     * @return double value of current player health
     */
    public double getHealth() {
        return this.health;
    }

    /**
     * get players defense points for battle calculation
     * @return double value of defense points from items
     */
    public double getDefensePoints() {
        return this.defensePoints;
    }

    /**
     * get current position of player
     * @return Position current position of player
     */
    public Position getCurrentPosition() {
        return this.currentPosition;
    }

    /**
     * generate an EntityResponse for the player
     * @return EntityResponse filled with player data
     */
    public EntityResponse getEntityResponse() {
        return new EntityResponse("player", "player", this.currentPosition, false);
    }

    /**
     * @param health the health to set
     */
    public void setHealth(double health) {
        this.health = health;
    }

    /**
     * @return double return the attackDamage
     */
    public double getAttackDamage() {
        return attackDamage;
    }

    /**
     * @param attackDamage the attackDamage to set
     */
    public void setAttackDamage(double attackDamage) {
        this.attackDamage = attackDamage;
    }

    /**
     * @param defensePoints the defensePoints to set
     */
    public void setDefensePoints(double defensePoints) {
        this.defensePoints = defensePoints;
    }

    /**
     * @return Position return the lastPosition
     */
    public Position getLastPosition() {
        return lastPosition;
    }

    /**
     * @param lastPosition the lastPosition to set
     */
    public void setLastPosition(Position lastPosition) {
        this.lastPosition = lastPosition;
    }

    /**
     * @param currentPosition the currentPosition to set
     */
    public void setCurrentPosition(Position currentPosition) {
        this.currentPosition = currentPosition;
    }

    /**
     * @param state the state to set
     */
    public void setState(PlayerState state) {
        this.state = state;
    }
    

    /**
     * @return int time left on current potion (tick)
     */
    public int getPotionTimer() {
        return state.getTimer();
    }

    /**
     * @param timer int amount of ticks left on current potion
     */
    public void setPotionTimer(int timer) {
        state.setTimer(timer);
    }

    public void decrementPotionTimer() {
        state.decrementTimer();
    }

    /**
     * @return List<Potion> list of potions to be applied
     */
    public List<Potion> getStatusEffects() {
        return (List<Potion>) ((LinkedList<Potion>) this.statusEffects);
    }

    /**
     * @param potion queue a potion to be applied
     */
    public void addStatusEffect(Potion potion) {
        this.statusEffects.add(potion);
        this.removeFromInventory(potion);
    }

    /**
     * update player state based on status effect queue
     */
    public void updateStatusEffect() {
        // timer has elapsed but queue is not empty - use next potion
        if (this.getPotionTimer() == 0 && this.statusEffects.size() != 0) {
            Potion potion = statusEffects.poll();
            potion.applyToPlayer(this);
            currentPotion = potion;
        // timer has elapsed and no potions left in queue
        } else if (this.getPotionTimer() == 0) {
            this.state.goNormal();
            currentPotion = null;
        // timer has not elapsed
        } else {
            this.decrementPotionTimer();
        }
    }
    
    /**
     * Get list of items built
     * @return
     */
    public List<Buildable> getBuiltItems() {
      return itemsBuilt;
    }

    /**
     * Add an item that was built
     * @param buildable
     */
    public void buildItem(Buildable buildable) {
        itemsBuilt.add(buildable);
    }

    /**
     * Remove an item that was built
     * @param buildable
     */
    public void removeBuiltItem(Buildable buildable) {
        itemsBuilt.remove(buildable);
    }

    /**
     * Get count of treasure player is holding
     * @return number of treasure
     */
    public List<CollectableEntity> getTreasures() {
        return inventory.stream()
                        .filter(i -> i instanceof TreasureEntity)
                        .collect(Collectors.toList());
    }

    /**
     * Get count of treasure player is holding
     * @return number of treasure
     */
    public int getTreasureCount() {
        return getTreasures().size();
    }

    /**
     * Get count of treasure that player is holding that counts towards goal
     * @return treasure player has that counts towards goal
     */
    public int getTreasureGoalCount() {
        int treasure = inventory.stream()
                                .filter(i -> i instanceof TreasureEntity)
                                .collect(Collectors.toList())
                                .size();
        int sunstones = inventory.stream()
                                    .filter(i -> i instanceof SunStoneEntity)
                                    .collect(Collectors.toList())
                                    .size();
        return treasure + sunstones;
    }

    /**
     * Check if the player is holding a sword
     * @return
     */
    public boolean hasSword() {
        return inventory.stream()
                        .anyMatch(i -> i instanceof SwordEntity);
    }

    public Potion getCurrentPotion() {
        return this.currentPotion;
    }
    
    /**
     * Reduce the durability of weapon and remove it if durability <= 0
     * @param weapon
     */
    public void reduceDurability(Weapon weapon) {
        weapon.setDurability(weapon.getDurability() - 1);
        if (weapon.getDurability() <= 0) {
            if (weapon instanceof Buildable) removeBuiltItem((Buildable) weapon);
            else removeFromInventory((CollectableEntity) weapon);
        }
    }

    /**
     * Increase count of items player has built
     */
    public void increaseNumberBuilt() {
        numberBuiltItems++;
    }

    /**
     * Get number of items the player has built
     * @return
     */
    public int getNumberBuiltItems() {
      return numberBuiltItems;
    }

    public void setIsAlive() {
        this.isAlive = false;
    }

    public boolean getIsAlive() {
        return isAlive;
    }

    public boolean hasSunStone() {
        return getSunStones().size() > 0;
    }

    /**
     * Get list of sun stone entities
     * @return sun stone entities
     */
    public List<CollectableEntity> getSunStones() {
        return getInventory().stream()
                                .filter(i -> i instanceof SunStoneEntity)
                                .collect(Collectors.toList());
    }

    /**
     * Get list of wood entities
     * @return wood entities
     */
    public List<CollectableEntity> getWoods() {
        return getInventory().stream()
                                .filter(i -> i instanceof WoodEntity)
                                .collect(Collectors.toList());
    }

    /**
     * Get list of key entities
     * @return key entities
     */
    public List<CollectableEntity> getKeys() {
        return getInventory().stream()
                                .filter(i -> i instanceof KeyEntity)
                                .collect(Collectors.toList());
    }

    /**
     * Get list of arrow entities
     * @return arrow entities
     */
    public List<CollectableEntity> getArrows() {
        return getInventory().stream()
                                .filter(i -> i instanceof ArrowEntity)
                                .collect(Collectors.toList());
    }

    /**
     * Get list of sword entities
     * @return sword entities
     */
    public List<CollectableEntity> getSwords() {
        return getInventory().stream()
                                .filter(i -> i instanceof SwordEntity)
                                .collect(Collectors.toList());
    }

    /**
     * @return Sceptre object if player has built one
     */
    public Sceptre getSceptre() {
        if (getBuiltItems().stream().anyMatch(b -> b instanceof Sceptre)) {
            return getBuiltItems().stream()
                                    .filter(b -> b instanceof Sceptre)
                                    .map(b -> (Sceptre) b)
                                    .collect(Collectors.toList())
                                    .get(0);
        } else {
            return null;
        }
    }

}
