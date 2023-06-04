package com.rkappagantu.allocator.model;

import javax.persistence.*;
import java.util.Date;

@Entity (name = "Control_Table")
@Table (name = "CONTROL_TABLE")
@NamedNativeQueries({
        @NamedNativeQuery(name="findAll",
                query="SELECT ID, ITEM_TYPE, BATCH_NUMBER FROM mydb.Control_Table", resultClass=Control_Table.class),
        @NamedNativeQuery(name="customMethod",
                query="SELECT ID, ITEM_TYPE, BATCH_NUMBER FROM mydb.Control_Table t WHERE t.ITEM_TYPE = ? FOR UPDATE NOWAIT", resultClass=Control_Table.class),
        @NamedNativeQuery(name="startTransaction",
                query="START TRANSACTION")
})
public class Control_Table {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String itemType;
    private int batchNumber;

    public Control_Table() {

    }
    public Control_Table(String itemType, int batchNum) {
        this.itemType = itemType;
        this.batchNumber = batchNum;
    }
    public long getId() {
        return id;
    }
    public String getItemType() {
        return itemType;
    }
    public int getBatchNum() {
        return batchNumber;
    }
    public void setBatchNum(int batch) {batchNumber = batch;}
}
