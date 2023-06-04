package com.rkappagantu.allocator.repository;
import org.springframework.data.jpa.repository.*;
import com.rkappagantu.allocator.model.Control_Table;
import java.util.List;

public interface ControlTableRepository { //extends JpaRepository<Control_Table, Long> {
    List<Control_Table> findAll();
    List<Control_Table> customMethod(String itemType);
    void startTransaction();
    boolean updateControl(Control_Table c);
}