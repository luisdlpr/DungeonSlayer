package dungeonmania.Battles;

import java.util.ArrayList;
import java.util.List;

import dungeonmania.Dungeon;
import dungeonmania.Entities.Player;
import dungeonmania.Entities.CollectableEntities.Weapons.Weapon;
import dungeonmania.Entities.MoveableEntities.MoveableEntity;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.RoundResponse;

/**
 * Responsible for creating Battles, Rounds, fighting Battles and notifying the Dungeon if the player dies. Generates BattleResponse to be used in DungeonResponse.
 * @authors Stan Korotun (z5637728) & Luis Reyes (z5206766) & Nancy Huynh (z5257042) & Jordan Liang (z5254761)
 */

public class Battle {
    
    private Dungeon dungeon;
    private Player player;
    private MoveableEntity enemy;
    private double initialPlayerHealth;
    private double initialEnemyHealth;
    private List<Round> rounds = new ArrayList<Round>();

    public Battle (Dungeon dungeon, Player player, MoveableEntity enemy) {
        this.player = player;
        this.enemy = enemy;
        initialPlayerHealth = player.getHealth();
        initialEnemyHealth = enemy.getHealth();
        this.dungeon = dungeon;
    }

    public void fightBattle() {
        double currPlayerHealth = initialPlayerHealth;
        double currEnemyHealth = initialEnemyHealth;

        List<Weapon> weaponsUsed = new ArrayList<>();

        // Battle until one side is dead
        while (currPlayerHealth > 0 && currEnemyHealth > 0) {
            Round r = new Round(player, enemy, dungeon);
            r.fightRound();
            rounds.add(r);
            currPlayerHealth -= r.getDeltaPlayerHealth();
            currEnemyHealth -= r.getDeltaEnemyHealth();
            weaponsUsed = r.getWeaponry();
        }

        // Reduce durability of all weapons used after the battle
        for (Weapon w : weaponsUsed) {
            player.reduceDurability(w);
        }

        // Remove the entity that lost the battle
        if (currPlayerHealth <= 0) {
            player.setIsAlive();
            enemy.setHealth(currEnemyHealth);
            return;
        }

        if (currEnemyHealth <= 0) {
            dungeon.removeObserver(enemy);
            player.setHealth(currPlayerHealth);
            return;
        }
        
    }

    public BattleResponse getBattleResponse() {
        List <RoundResponse> roundResponses = new ArrayList<RoundResponse>();
        for (Round r : rounds) {
            roundResponses.add(r.getRoundResponse());
        }
        return new BattleResponse(enemy.getType(), roundResponses, initialPlayerHealth, initialEnemyHealth);
    }
    
}
