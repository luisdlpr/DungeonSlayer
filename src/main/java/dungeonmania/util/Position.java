package dungeonmania.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Position {
    private final int x, y, layer;

    public Position(int x, int y, int layer) {
        this.x = x;
        this.y = y;
        this.layer = layer;
    }

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
        this.layer = 0;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(x, y, layer);
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Position other = (Position) obj;

        // z doesn't matter
        return x == other.x && y == other.y;
    }

    public final int getX() {
        return x;
    }

    public final int getY() {
        return y;
    }

    public final int getLayer() {
        return layer;
    }

    public final Position asLayer(int layer) {
        return new Position(x, y, layer);
    }

    public final Position translateBy(int x, int y) {
        return this.translateBy(new Position(x, y));
    }

    public final Position translateBy(Direction direction) {
        return this.translateBy(direction.getOffset());
    }

    public final Position translateBy(Position position) {
        return new Position(this.x + position.x, this.y + position.y, this.layer + position.layer);
    }

    // (Note: doesn't include z)

    /**
     * Calculates the position vector of b relative to a (ie. the direction from a
     * to b)
     * @return The relative position vector
     */
    public static final Position calculatePositionBetween(Position a, Position b) {
        return new Position(b.x - a.x, b.y - a.y);
    }

    public static final boolean isAdjacent(Position a, Position b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y) == 1;
    }

    // (Note: doesn't include z)
    public final Position scale(int factor) {
        return new Position(x * factor, y * factor, layer);
    }

    @Override
    public final String toString() {
        return "Position [x=" + x + ", y=" + y + ", z=" + layer + "]";
    }

    // Return Adjacent positions in an array list with the following element positions:
    // 0 1 2
    // 7 p 3
    // 6 5 4
    public List<Position> getAdjacentPositions() {
        List<Position> adjacentPositions = new ArrayList<>();
        adjacentPositions.add(new Position(x-1, y-1));
        adjacentPositions.add(new Position(x  , y-1));
        adjacentPositions.add(new Position(x+1, y-1));
        adjacentPositions.add(new Position(x+1, y));
        adjacentPositions.add(new Position(x+1, y+1));
        adjacentPositions.add(new Position(x  , y+1));
        adjacentPositions.add(new Position(x-1, y+1));
        adjacentPositions.add(new Position(x-1, y));
        return adjacentPositions;
    }

    /**
     * Get distance between thi and other entity
     * @param other
     * @return
     */
    public double getDistanceBetween(Position other) {
        double distance = Math.sqrt( (this.y - other.getY())*(this.y - other.getY()) + (this.x - other.getX())*(this.x - other.getX()));
        return distance;
    }
    
    public List<Position> getiAdjacentPositions(int i) { 
        if (i == 1) {
            return this.getAdjacentPositions();
        } else {
            List<Position> adjacentPositions = new ArrayList<>();
            adjacentPositions.add(new Position(x-i, y-i));
            adjacentPositions.add(new Position(x  , y-i));
            adjacentPositions.add(new Position(x+i, y-i));
            adjacentPositions.add(new Position(x+i, y));
            adjacentPositions.add(new Position(x+i, y+i));
            adjacentPositions.add(new Position(x  , y+i));
            adjacentPositions.add(new Position(x-i, y+i));
            adjacentPositions.add(new Position(x-i, y));
            adjacentPositions.addAll(getiAdjacentPositions(i - 1));

            return adjacentPositions;
        }

    }

    /**
     * Get list of cardinally adjacent positions
     * @return
     */
    public List<Position> getCardinallyAdjacentPositions() {
        List<Position> cardinallyAdjacentPositions = new ArrayList<Position>();
        cardinallyAdjacentPositions.add(new Position(this.x+1, this.y)); // right
        cardinallyAdjacentPositions.add(new Position(this.x-1, this.y)); // left
        cardinallyAdjacentPositions.add(new Position(this.x, this.y+1)); // down
        cardinallyAdjacentPositions.add(new Position(this.x, this.y-1)); // up
        return cardinallyAdjacentPositions;
    }

    /**
     * Get list of cardinally adjacent positions a given distance away from the source
     * @return
     */
    public List<Position> getCardinallyAdjacentByDistance(int distance) { 
        List<Position> neighbours = new ArrayList<>();
        neighbours.add(new Position(this.x+distance, this.y)); // right
        neighbours.add(new Position(this.x-distance, this.y)); // left
        neighbours.add(new Position(this.x, this.y+distance)); // down
        neighbours.add(new Position(this.x, this.y-distance)); // up

        return neighbours;
    }

    public boolean isCardinallyAdjacent( Position other) {

        if (this.getX() - other.getX() == 0 && this.getY() - other.getY() == -1) return true;
        if (this.getX() - other.getX() == -1 && this.getY() - other.getY() == 0) return true;
        if (this.getX() - other.getX() == 0 && this.getY() - other.getY() == 1) return true;
        if (this.getX() - other.getX() == 1 && this.getY() - other.getY() == 0) return true;

        return false;
    }

    public List<Position> getPositionsWithinRadius(int radius) {
        List<Position> positionsWithinRadius = new ArrayList<Position>();

        Position currPos = new Position(this.x - radius, this.y + radius);
        
        while (currPos.x <= this.x + radius) {
            while (currPos.y >= this.y - radius) {
                positionsWithinRadius.add(currPos);
                currPos = new Position(currPos.x, currPos.y -1);
            }
            currPos = new Position(currPos.x + 1, this.y + radius);
        }

        return positionsWithinRadius;
    }

}
