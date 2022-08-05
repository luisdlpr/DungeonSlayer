package dungeonmania.Goals;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import dungeonmania.Dungeon;

public class ComplexGoal implements GoalComponent{
    private String superGoal;
    private List<GoalComponent> subgoals = new ArrayList<GoalComponent>();

    public ComplexGoal(JSONObject goal) {
        this.superGoal = goal.getString("goal");
        for (Object o : goal.getJSONArray("subgoals")) {
            JSONObject json = (JSONObject) o;
            if (json.has("subgoals")) {
                this.subgoals.add(new ComplexGoal(json));
            } else {
                this.subgoals.add(new BasicGoal(json));
            }
        }
    }

    public Boolean checkSuccess(Dungeon dungeon) {
        switch(this.superGoal) {
            case "AND":
                return this.subgoals.stream().allMatch(g -> g.checkSuccess(dungeon));
            case "OR":
                return this.subgoals.stream().anyMatch(g -> g.checkSuccess(dungeon));
        }
        return false;
    }


    public String getGoal(Dungeon dungeon) {
        String goalString = "";
        if (!this.checkSuccess(dungeon)) {
            for (GoalComponent g : subgoals) {
                if (g.isComplex()) {
                    goalString += "(" + g.getGoal(dungeon) + ")";
                } else {
                    goalString += g.getGoal(dungeon);
                }
    
                goalString += " " + superGoal + " ";
            }
            return (this.superGoal.equals("AND")) ? goalString.substring(0, goalString.length() - 5) : goalString.substring(0, goalString.length() - 4);
        }
        return goalString;

    }

    public Boolean isComplex() {
        return true;
    }

}
