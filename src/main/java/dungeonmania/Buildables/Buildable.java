package dungeonmania.Buildables;

import dungeonmania.Entities.Player;
import dungeonmania.response.models.ItemResponse;

/**
 * Buildable interface for builable items
 * @author Nancy Huynh (z5257042)
 */
public interface Buildable {
    public Boolean playerHasMaterials(Player player);
    public void build(Player player);
    public String getType();
    public String getId();
    public void setId(String id);
    public ItemResponse getItemResponse();
}
