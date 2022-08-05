package dungeonmania.Entities.MoveableEntities;

import org.json.JSONObject;

public class HydraEntity extends ZombieToastEntity{

    private double hydraHealthIncreaseRate;
    private double hydraHealthIncreaseAmount;

    /**
    * A hydra entity that is based on the implementation of zombies with relevant changes made to battles to accomodate for health increases.
    * @author Stan Korotun (z5367728) 
    */

    public HydraEntity(JSONObject object, Integer id, double health, double attackDamage, double hydraHealthIncreaseRate, double hydraHealthIncreaseAmount) {
        super(object, id, health, attackDamage);
        this.hydraHealthIncreaseRate = hydraHealthIncreaseRate;
        this.hydraHealthIncreaseAmount = hydraHealthIncreaseAmount; 
    }

    public double getHydraHealthIncreaseRate() {
        return this.hydraHealthIncreaseRate;
    }

    public double getHydraHealthIncreaseAmount() {
        return this.hydraHealthIncreaseAmount;
    }
    
}
