package dungeonmania.MovementBehaviours;
import dungeonmania.util.Position;

public class MapNode {
    private Position position;
    private Double distance;
    public MapNode(Position position, Double distance) {
        this.position = position;
        this.distance = distance;
    }
    public Position getPosition() {
        return position;
    }
    public void setPosition(Position position) {
        this.position = position;
    }
    public Double getDistance() {
        return distance;
    }
    public void setDistance(Double distance) {
        this.distance = distance;
    }
}
