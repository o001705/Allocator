package com.rkappagantu.allocator.model;

import javax.persistence.*;

@Entity (name = "Item")
@Table (name = "ITEM")
@NamedNativeQueries({
        @NamedNativeQuery(name="getPendingItems",
                query="SELECT ITEM_ID, ITEM_TYPE, ITEM_STATUS, ITEM_BATCH, ITEM_DATA, ITEM_EXECUTOR FROM mydb.ITEM WHERE ITEM_STATUS = ?1 AND ITEM_TYPE = ?2", resultClass=Item.class),
        @NamedNativeQuery(name="getPendingItemsByExecutor",
                query="SELECT ITEM_ID, ITEM_TYPE, ITEM_STATUS, ITEM_BATCH, ITEM_DATA, ITEM_EXECUTOR FROM mydb.ITEM WHERE ITEM_STATUS = ?1 AND ITEM_EXECUTOR = ?2", resultClass=Item.class)
})
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int itemId;
    private String itemType;
    private String itemStatus;
    private String itemData;
    private String itemExecutor;
    private int itemBatch;

    public Item() {

    }
    public Item(String itemType, String itemStatus, int itemBatch, String itemData, String itemExecutor) {
        this.itemType = itemType;
        this.itemStatus = itemStatus;
        this.itemData = itemData;
        this.itemExecutor = itemExecutor;
        this.itemBatch = itemBatch;
    }
    public void setItemStatus(String itemStatus) {
        this.itemStatus = itemStatus;
    }
    public void setItemType(String itemType) {
        this.itemType = itemType;
    }
    public void setItemData(String itemData) {
        this.itemData = itemData;
    }
    public void setItemExecutor(String itemExecutor) {
        this.itemExecutor = itemExecutor;
    }
    public void setItemBatch(int itemBatch) {
        this.itemBatch = itemBatch;
    }
    public String getItemData() {
        return itemData;
    }
}
