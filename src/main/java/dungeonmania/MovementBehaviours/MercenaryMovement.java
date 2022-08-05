package dungeonmania.MovementBehaviours;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import dungeonmania.Dungeon;
import dungeonmania.Entities.Entity;
import dungeonmania.Entities.StaticEntities.DoorEntity;
import dungeonmania.Entities.StaticEntities.PortalEntity;
import dungeonmania.Entities.StaticEntities.SwampTileEntity;
import dungeonmania.util.Position;

/**
 * Parent class for mercenary movement behaviour
 * @author Jordan Liang (z5254761) & Nancy Huynh (z5257042)
 */

public class MercenaryMovement {

    /**
     * Pathfinding using Djikstra's algorithm
     */
    public Position pathToPlayer(Position currentPosition, Position playerPos, Dungeon dungeon) {
        Map<Position, Position> previous = new HashMap<>();
        Map<Position, Double> distance = new HashMap<>();
        initialiseMaps(previous, distance, currentPosition);
        distance.put(currentPosition, 0.0);

        // First item in queue is the source
        Queue<MapNode> priorityQueue = new PriorityQueue<MapNode>(queueComparator);    
        priorityQueue.add(new MapNode(currentPosition, 0.0));

        while (!priorityQueue.isEmpty()) {
            Position u = priorityQueue.poll().getPosition();
            if (u.equals(playerPos)) {
                // We have found the player position
                // Generate the final path to them.
                return generateNextPosition(u, previous);
            }

            for(Position v : getNextPositions(u, dungeon)) {
                // Check if the position is within our search space
                if (!distance.containsKey(v)) {
                    continue;
                }
                double cost = 1;
                boolean addToQueue = true;
                if (distance.get(u) + cost < distance.get(v)) {
                    // The cost is typically just 1, but if there is a swamp tile involved,
                    // add its movement factor to the cost
                    if (dungeon.getEntityAtPosition("swamp_tile", v) != null) {
                        SwampTileEntity swampTile = (SwampTileEntity) dungeon.getEntityAtPosition("swamp_tile", v);
                        cost = cost + swampTile.getMovementFactor();
                    }
                    
                    if (dungeon.getEntityAtPosition("portal", v) != null) {
                        // A neighbour is a portal
                        // Check where it comes out
                        PortalEntity portal = (PortalEntity) dungeon.getEntityAtPosition("portal", v);
                        Position portalExit = portal.portalRecursion(dungeon, v);         
                        if (portalExit != null) {
                            // If the exit of the portal is closer to the player than the portal entrance,
                            // consider going through the portal (add to priority queue)
                            if (playerPos.getDistanceBetween(portalExit) < playerPos.getDistanceBetween(v)) {
                                distance.put(portalExit, distance.get(u) + cost);
                                // Portal exit is the key, prev value is u
                                previous.put(portalExit, v);
                                priorityQueue.add(new MapNode(portalExit, distance.get(u) + cost));
                            }
                        } else {
                            // There is no exit to this portal, avoid going to it
                            addToQueue = false;
                        }
                    }      
                    if (addToQueue) {
                        distance.put(v, distance.get(u) + cost);
                        previous.put(v, u);
                        priorityQueue.add(new MapNode(v, distance.get(u) + cost));
                    }
                }
            }
            sortQueue(priorityQueue);
        }
        return null;
    }

    /**
     * Generate the next position to move to based on final path to the player
     */

    private Position generateNextPosition(Position u, Map<Position, Position> previous) {
        List<Position> finalPath = new ArrayList<>();
        if (previous.get(u) == null) {
            // No path found
            return null;
        }
        while(previous.get(u) != null) {
            // Build the path backwards to get it the right way around
            finalPath.add(0, u);
            u = previous.get(u);
        }
        return finalPath.get(0);
    }

    /**
     * initialise distance and previous maps
     */
    private void initialiseMaps(Map<Position, Position> previous, Map<Position, Double> distance, Position currentPosition) {
        Position searchStart = currentPosition.translateBy(new Position(-15, -15));
        int x = searchStart.getX();
        int y = searchStart.getY();
        int searchDiameter = 30;
        int endX = x + searchDiameter;
        int endY = y + searchDiameter;
        int originalX = x;

        while (y <= endY) {
            x = originalX;
            while (x <= endX) {
                distance.put(new Position(x, y), Double.POSITIVE_INFINITY);
                previous.put(new Position(x, y), null);
                
                x = x + 1;
            }
            y = y + 1;
        }
        distance.put(searchStart, 0.0);
    }

    /**
     * Sort the queue by order of distance
     */
    private void sortQueue(Queue<MapNode> priorityQueue) {
        List<MapNode> sortMe = new ArrayList<MapNode>();
        while (!priorityQueue.isEmpty()) {
            sortMe.add(priorityQueue.peek());
            priorityQueue.poll();
        }

        Collections.sort(sortMe, queueComparator);
        for (MapNode yep : sortMe) {
            priorityQueue.add(yep);
        }
    }

    Comparator<MapNode> queueComparator = new Comparator<MapNode>() {
        @Override
        public int compare(MapNode n1, MapNode n2) {
            return n1.getDistance().compareTo(n2.getDistance());
        }
    };
    
    // Get cardinally adjacent cells for pathfinding
    private List<Position> getNextPositions(Position current, Dungeon dungeon) {
        List<Position> neighbours = current.getCardinallyAdjacentPositions();
        // Loop through and remove if there is something on it that is not a switch or portal
        List<Position> tilesToRemove = new ArrayList<Position>();

        for (Position tile : neighbours) {
            if ((dungeon.getEntityAtPosition("wall", tile) != null ||
                dungeon.getEntityAtPosition("boulder", tile) != null ||
                dungeon.getEntityAtPosition("zombie_toast_spawner", tile) != null ||
                checkIfLockedDoor(dungeon.getEntityAtPosition("door", tile))
                )){
                tilesToRemove.add(tile);
            }
        }

        neighbours.removeAll(tilesToRemove);

        return neighbours;
    }

    // If not a door, return false
    // If a door, return true if locked, else false
    private Boolean checkIfLockedDoor(Entity entity) {
        if (entity instanceof DoorEntity) {
            DoorEntity door = (DoorEntity) entity;
            return !door.getIsUnlocked();
        } else {
            return false;
        }
    }
}
