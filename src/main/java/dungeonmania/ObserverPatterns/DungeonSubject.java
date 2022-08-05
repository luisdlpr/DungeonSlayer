package dungeonmania.ObserverPatterns;

/**
 * Subject interface for dungeon-entity observer pattern
 * @author Luis Reyes (z5206766)
 */
public interface DungeonSubject {
    public void addObserver(DungeonObserver obj);
    public void removeObserver(DungeonObserver obj);
    public void notifyObservers();
}
