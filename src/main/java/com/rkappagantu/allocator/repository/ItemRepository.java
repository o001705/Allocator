package com.rkappagantu.allocator.repository;

import com.rkappagantu.allocator.model.Item;
import java.util.List;

public interface ItemRepository {
    List<Item> getPendingItems(String itemStatus, String itemType);
    List<Item> getPendingItemsByExecutor(String itemStatus, String itemExecutor);
    List<Item> updateItemsBulk(List<Item> items);
    Item updateItem(Item item);
    void createItems(List<Item> items);
}
