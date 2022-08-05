package dungeonmania.Entities.StaticEntities;

import org.json.JSONObject;

import dungeonmania.ObserverPatterns.DungeonSubject;

/**
 * Swamp tile entity class
 * @author Jordan Liang (z5254761)
 */
public class SwampTileEntity extends StaticEntity{
    private int movementFactor;
    public SwampTileEntity(JSONObject object, Integer id, int movementFactor) {
        super(object, id, false);
        this.movementFactor = movementFactor;
    }

    public int getMovementFactor() {
        return movementFactor;
    }

    public void setMovementFactor(int movementFactor) {
        this.movementFactor = movementFactor;
    }

    @Override
    public void update(DungeonSubject obj) {
        // Use this to figure out if something is in the swamp?
    }
    
}
