package dungeonmania.Entities;

import dungeonmania.ObserverPatterns.DungeonSubject;
import dungeonmania.exceptions.InvalidActionException;
/**
 * Interactable interface for entities that can be interacted with
 * @author Jordan Liang (z5254761)
 */
public interface Interactable {
    public void interact(DungeonSubject obj) throws InvalidActionException;
}
