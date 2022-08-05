package dungeonmania.Entities.StaticEntities;

import org.json.JSONObject;

import dungeonmania.ObserverPatterns.DungeonSubject;

public class ExitEntity extends StaticEntity {
    /**
     * Construction with a JSONObject
     * @param object JSONObject specifying type, x, and y pos
     * @param id unique id
     */
    public ExitEntity(JSONObject object, Integer id) {
        super(object, id, false);
    }

    /**
     * Update goal to set BasicGoal with type exit to success
     */
    @Override
    public void update(DungeonSubject obj) {}
}
