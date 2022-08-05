package dungeonmania.ObserverPatterns;

/**
 * Observer interface for dungeon-entity observer pattern
 * @author Luis Reyes (z5206766)
 */
public interface DungeonObserver {
    public void update(DungeonSubject obj);
}
