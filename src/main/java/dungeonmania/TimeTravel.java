package dungeonmania;

import java.util.LinkedList;
import java.util.Queue;

import org.json.JSONObject;

/**
 * Class that manages time travel
 * @author Nancy Huynh (z5257042)
 */
public class TimeTravel {
    
    Dungeon dungeon;
    private JSONObject lastTick;
    private Queue<JSONObject> lastFiveTicks = new LinkedList<>();
    private Queue<JSONObject> lastThirtyTicks = new LinkedList<>();

    public TimeTravel(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    public void updateTimeTravelSaves() {
        lastTick = SaveUtility.savePastStatetoJSON(dungeon);

        if (lastFiveTicks.size() == 5) {
            lastFiveTicks.remove(); // Remove least recent save
        }

        if (lastThirtyTicks.size() == 30) {
            lastThirtyTicks.remove(); // Remove least recent save
        }

        lastFiveTicks.add(lastTick);
        lastThirtyTicks.add(lastTick);
    }

    public JSONObject rewind(int ticks) {
        // Rewind buttons
        if (ticks == 1) return lastTick;

        return lastFiveTicks.peek();
    }

    public JSONObject timeTravelPortal() {
        return lastThirtyTicks.peek();
    }

    public Queue<JSONObject> getJSONforTicks(int ticks) {
        switch(ticks) {
            case 1:
                Queue<JSONObject> states = new LinkedList<>();
                states.add(lastTick);
                return states;
            case 5:
                return lastFiveTicks;
            case 30:
                return lastThirtyTicks;
        }
        return null;
    }


}
