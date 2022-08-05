package dungeonmania.Battles;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dungeonmania.Dungeon;
import dungeonmania.Buildables.Buildable;
import dungeonmania.Entities.Entity;
import dungeonmania.Entities.Player;
import dungeonmania.Entities.CollectableEntities.CollectableEntity;
import dungeonmania.Entities.CollectableEntities.Potions.Potion;
import dungeonmania.Entities.CollectableEntities.Weapons.Weapon;
import dungeonmania.Entities.MoveableEntities.HydraEntity;
import dungeonmania.Entities.MoveableEntities.MoveableEntity;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.response.models.RoundResponse;

/**
 * Responsible for creating Rounds, fighting Rounds, keeping track of change in player and enemy health as well as weapons used in a round, including potions. 
 * Generates RoundResponse to be used in Battle which then creates a BatteResponse using a list of RoundResponse.
 * @authors Stan Korotun (z5637728) & Luis Reyes (z5206766) & Nancy Huynh (z5257042) & Jordan Liang (z5254761)
 */

public class Round {

    private Player player;
    private MoveableEntity enemy;
    private Dungeon dungeon;

    private double deltaPlayerHealth;
    private double deltaEnemyHealth;
    private List<Weapon> weaponry = new ArrayList<Weapon>();
    private Potion potionUsed;
    private Weapon sword;
    private Weapon bow;
    private Weapon shield;
    private Weapon armour;

    public Round (Player player, MoveableEntity enemy, Dungeon dungeon) {
        this.player = player;
        this.enemy = enemy;
        this.dungeon = dungeon;
        weaponry = getWeaponry();
        potionUsed= player.getCurrentPotion();
    }

    public List<Weapon> getWeaponry() {
        List<CollectableEntity> playerInventory = player.getInventory();
        List<Weapon> weaponry = new ArrayList<>();

        // Go through inventory and retrieve at most one of each type of weapon
        for (Entity e : playerInventory) {
            if (e.getType().equals("sword")) {
                weaponry.add((Weapon)e);
                sword = (Weapon)e;
                break;
            }
        }

        List<Buildable> buildables =  player.getBuiltItems();
        for (Buildable e : buildables) {
            if (e.getType().equals("shield")) {
                weaponry.add((Weapon)e);
                shield = (Weapon)e;
                break;
            }
        }

        for (Buildable e : buildables) {
            if (e.getType().equals("bow")) {
                weaponry.add((Weapon)e);
                bow = (Weapon)e;
                break;
            }
        }

        for (Buildable e : buildables) {
            if (e.getType().equals("midnight_armour")) {
                weaponry.add((Weapon)e);
                armour = (Weapon)e;
                break;
            }
        }

        return weaponry;
    }

    public void fightRound() {
        // Player wins battle if they are invincible
        if (player.getState().equals(player.getInvincibleState())) {
            deltaPlayerHealth = 0;
            deltaEnemyHealth = enemy.getHealth();
            return;
        }

        // Get stats from weapons player is using
        double bowMultiplier = 1;
        if (bow != null) bowMultiplier = bow.getAttackDamage();

        double swordAttack = 0;
        if (sword != null) swordAttack = sword.getAttackDamage();
        
        double shieldDefence = 0;  
        if (shield != null) shieldDefence = shield.getDefencePoints();

        double armourAttack = 0;  
        if (armour != null) armourAttack = armour.getAttackDamage();

        double armourDefence = 0;  
        if (armour != null) armourDefence = armour.getDefencePoints();
        
        // Get ally stats
        int numAllies = dungeon.numAllies();
        double allyAttack = dungeon.getConfigParam("ally_attack"); // Ally defence
        double allyAttackBuff = allyAttack * numAllies;
        double allyDefence = dungeon.getConfigParam("ally_defence"); // Ally defence
        double allyDefenceBuff = allyDefence * numAllies;

        
        boolean success = false;
        // Calculate damage dealt to people in the battle
        if (enemy.getType().equals("hydra")) {
            
            HydraEntity hydraEnemy = (HydraEntity) enemy;
            //calculate the chance of health increasing
            if (hydraEnemy.getHydraHealthIncreaseRate() == 1) { //check rate
                success = true;
            }

            if (hydraEnemy.getHydraHealthIncreaseRate() != 1 && hydraEnemy.getHydraHealthIncreaseRate() != 0) {
                Random rand = new Random();
                double randValue = rand.nextDouble();
                if (hydraEnemy.getHydraHealthIncreaseRate() > randValue) {   
                    success = true;
                }
            }
            
            if (success) { //if increase successful delta enenemy health needs to return a negative value based on the config health increase
                deltaEnemyHealth = -hydraEnemy.getHydraHealthIncreaseAmount();
            }
            //if unsuccessful delta enemy health is calculated normally
        }
        //if the enenemy is not a hydra or health increase chance was unsuccessful
        if (success == false) {
            deltaEnemyHealth = (bowMultiplier * (player.getAttackDamage() + allyAttackBuff + swordAttack + armourAttack)) / 5;
        }
        
        
        
        deltaPlayerHealth = (enemy.getAttackDamage()- allyDefenceBuff - shieldDefence - armourDefence) / 10;

        // If ally defence is greater than damage, set deltaPlayerHealth to 0
        if (allyDefenceBuff >= enemy.getAttackDamage()) {
            deltaPlayerHealth = 0;
        }
    }

    public RoundResponse getRoundResponse() {
        List<ItemResponse> itemResponses = new ArrayList<ItemResponse>();

        for (Weapon w : weaponry) {
            itemResponses.add(w.getItemResponse());
        }
        
        if (potionUsed != null)
            itemResponses.add(potionUsed.getItemResponse());

        if (deltaEnemyHealth == 0.0) {
            return new RoundResponse(-deltaPlayerHealth, deltaEnemyHealth, itemResponses);
        }
        if (deltaPlayerHealth == 0.0) {
            return new RoundResponse(deltaPlayerHealth, -deltaEnemyHealth, itemResponses);
        }
        if (deltaPlayerHealth == 0.0 && deltaEnemyHealth == 0.0) {
            return new RoundResponse(deltaPlayerHealth, deltaEnemyHealth, itemResponses);
        }

            return new RoundResponse(-deltaPlayerHealth, -deltaEnemyHealth, itemResponses);
    }

    public double getDeltaPlayerHealth() {
        return this.deltaPlayerHealth;
    }

    public double getDeltaEnemyHealth() {
        return this.deltaEnemyHealth;
    }

}
