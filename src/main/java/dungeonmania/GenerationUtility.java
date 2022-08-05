package dungeonmania;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import dungeonmania.util.Direction;
import dungeonmania.util.Position;

/**
 * Class to handle random maze generation
 * @authors Jordan Liang (z5254761)
 */
public class GenerationUtility {

    /**
     * Given a dungeon map, generates the JSON file for this dungeon and places the player
     * at the start position and an exit at the exit position
     * @param map randomly generated maze map of dungeon
     * @return The dungeon map converted to json
     */
    public static JSONObject convertMapToJson(Map<Position, String> map) {
        JSONObject randomDungeon = new JSONObject();

        // entities (on-map)
        JSONArray entities = new JSONArray();
        for (Map.Entry<Position, String> entry : map.entrySet()) {
            Position pos = entry.getKey();
            String objectAtPos = entry.getValue();

            if (objectAtPos.equals("empty")) {
                // Skip empty squares
                continue;
            }
            JSONObject object = new JSONObject();
            object.put("x", pos.getX());
            object.put("y", pos.getY());
            object.put("type", objectAtPos);

            entities.put(object);
        }

        // Goal conditions
        JSONObject goalCondition = new JSONObject();
        goalCondition.put("goal", "exit");

        // Adding everything to the dungeon
        randomDungeon.put("entities", entities);
        randomDungeon.put("goal-condition", goalCondition);
        return randomDungeon;
    }

    
    /**
     * Implementation of randomised Prim's algorithm for generating a random dungeon
     * @param xStart
     * @param yStart
     * @param xEnd
     * @param yEnd
     * @return Dungeon map with fields indicating "wall", "empty", "player" and "exit"
     */
    public static Map<Position, String> randomisedPrims(int xStart, int yStart, int xEnd, int yEnd) {
        // The start is at the top left of the maze
        // The end is at the bottom right
        Random rand = new Random(System.currentTimeMillis());
        // Initialise the maze 
        Map<Position, String> maze = initialiseMap(xStart, yStart, xEnd, yEnd);
        int width = xEnd - xStart + 1;
        int height = yEnd - yStart + 1;
        Position start = new Position(xStart, yStart);
        Position end = new Position(xEnd, yEnd);
        
        List<Position> options = new LinkedList<Position>();
        options.addAll(getNeighbours("wall", maze, start, start, end));

        while (!options.isEmpty()) {
            Position next = options.remove(rand.nextInt(options.size()));
            List<Position> neighbours = getNeighbours("empty", maze, next, start, end);
            if (!neighbours.isEmpty()) {
                int randomIndex = rand.nextInt(neighbours.size());
                Position neighbour = neighbours.get(randomIndex);
                maze.put(next, "empty");
                // Get position between next and neighbour 
                Position inBetween = new Position((next.getX() + neighbour.getX())/2, (next.getY() + neighbour.getY())/2);
                maze.put(inBetween, "empty");
                maze.put(neighbour, "empty");
            }
            options.addAll(getNeighbours("wall", maze, next, start, end));
        }
        if (maze.get(end).equals("wall")) {
            maze.put(end, "exit");
            // Get neighbours of the end that are within bound
            List<Position> endNeighbours = end.getCardinallyAdjacentPositions().stream()
                                              .filter(e -> withinBounds(e, start, end))
                                              .collect(Collectors.toList());
            // Get neighbours that are empty
            List<Position> endNeighboursEmpty = endNeighbours.stream()
                                                             .filter(e -> maze.get(e).equals("empty"))
                                                             .collect(Collectors.toList());
            if (endNeighboursEmpty.isEmpty()) {
                // No empty neighbours - connect end to grid
                Position finalAddition = endNeighbours.get(rand.nextInt(endNeighbours.size()));
                maze.put(finalAddition, "empty");
            }
        } else {
            maze.put(end, "exit");
        }
        // Put player at the start
        maze.put(start, "player");
        // Create a border wall
        generateBoundary(start, width, height, maze);
        return maze;
    }


    /**
     * Given a position, check if it is within the bounds of the maze
     * @param p position
     * @param start start of maze
     * @param end end of maze
     * @return True if within bounds, false otherwise
     */
    private static boolean withinBounds(Position p, 
                                        Position start,
                                        Position end) {     
        if (!(p.getX() > start.getX()-1 && p.getX() < end.getX()+1)) {
            return false;
        } else if (!(p.getY() > start.getY()-1 && p.getY() < end.getY()+1)) {
            return false;
        } else {
            return true;
        }                    
    }

    /**
     * Get cardinally adjacent members within bounds of the given type
     * @param requiredNeighbour type of neighbour you're looking for
     * @param maze
     * @param source neighbours adjacent to
     * @param start
     * @param end
     * @return list of neighbours
     */
    private static List<Position> getNeighbours(String requiredNeighbour, Map<Position, String> maze,
                                         Position source, Position start, Position end) {

        List<Position> neighbours = new ArrayList<Position>();

        for (Position p : source.getCardinallyAdjacentByDistance(2)) {
            if (withinBounds(p, start, end) &&
                (maze.get(p).equals(requiredNeighbour))) {
                neighbours.add(p);
            }
        }
        return neighbours;
    }

    /**
     * Initialise the dungeon map with given constraints
     * @param xStart
     * @param yStart
     * @param xEnd
     * @param yEnd
     * @return dungeon map with walls in every position, but the start as empty
     */
    private static Map<Position, String> initialiseMap(int xStart, int yStart, int xEnd, int yEnd) {
        Map<Position, String> maze = new HashMap<>();
        int x = xStart;
        int y = yStart;
        while (y <= yEnd) {
            x = xStart;
            while (x <= xEnd) {
                maze.put(new Position(x, y), "wall");
                x = x + 1;
            }
            y = y + 1;
        }
        // Initialise the start as empty
        maze.put(new Position(xStart, yStart), "empty");
        return maze;
    }

    /**
     * Generate boundary wall for the dungeon
     * @param start
     * @param width
     * @param height
     * @param maze
     */
    private static void generateBoundary(Position start, int width, int height, 
                                         Map<Position, String> maze) {
        Position border = start.translateBy(new Position(-1, -1));
        Position oppositeSide;

        // Initialise top/bottom borders
        for (int i = 0; i < width + 2; i++) {
            maze.put(border, "wall");
            oppositeSide = border.translateBy(new Position(0, height + 1));
            maze.put(oppositeSide, "wall");
            border = border.translateBy(Direction.RIGHT);
        }

        // Initialise side borders
        border = start.translateBy(new Position(-1, 0));
        for (int i = 0; i < height; i++) {
            maze.put(border, "wall");
            oppositeSide = border.translateBy(new Position(width + 1, 0));
            maze.put(oppositeSide, "wall");
            border = border.translateBy(Direction.DOWN);
        }
    }
}
