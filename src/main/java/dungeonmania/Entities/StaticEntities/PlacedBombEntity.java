package dungeonmania.Entities.StaticEntities;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dungeonmania.Dungeon;
import dungeonmania.Entities.Entity;
import dungeonmania.ObserverPatterns.DungeonSubject;
import dungeonmania.util.Position;

/**
 * The bomb after place is called on a UnplacedBombEntity instance, this class handles checking if an active switch is present and detonation including removal of entities.
 * @author Stan Korotun (z5637728)
 */

public class PlacedBombEntity extends StaticEntity {

    List<FloorSwitchEntity> switchesInRange = new ArrayList<FloorSwitchEntity>();

    public PlacedBombEntity(String id, String type, Position position, boolean isInteractable, DungeonSubject obj) { //dungeonSubject is needed to load in floor switches
        super(id, type, position, isInteractable);
        updateSwitches(obj);
    }

    public PlacedBombEntity(String id, String type, Position position, boolean isInteractable) { //dungeonSubject is needed to load in floor switches
        super(id, type, position, isInteractable);
    }
    
    public void updateSwitches(DungeonSubject obj) {
        Dungeon dungeon = (Dungeon) obj;
        List<FloorSwitchEntity> floorSwitches = dungeon.getEntitiesOfType("switch").stream()
                                                                                    .map(e -> (FloorSwitchEntity) e)
                                                                                    .collect(Collectors.toList());
        for (FloorSwitchEntity s : floorSwitches) {
            if (this.getPosition().isCardinallyAdjacent(s.getPosition())) {
                this.switchesInRange.add(s);
                if(s.getState() == s.getActiveState()) {
                    detonate(obj);
                }
            }
        }
    }

    @Override
    public void update(DungeonSubject obj) {
        for (FloorSwitchEntity s : switchesInRange) {
            if(s.getState() == s.getActiveState()) {
                detonate(obj);
                break;
            }
        } 
    }

    public void detonate(DungeonSubject obj) {
        
        Dungeon dungeon = (Dungeon) obj;

        List<Entity> entityList = dungeon.getEntities();
        List<Entity> toDetonateInDungeon = new ArrayList<Entity>();
        List<Position> positionsWithinRadius = this.getPosition().getPositionsWithinRadius((int) dungeon.getConfigParam("bomb_radius"));
        
        for (Entity item : entityList) {
            if (positionsWithinRadius.contains(item.getPosition())) {
                toDetonateInDungeon.add(item);
            }
        }

        dungeon.removeObserver(this);
        dungeon.setEntitiesToDetonate(toDetonateInDungeon);

    }

}
