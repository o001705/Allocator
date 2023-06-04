package com.rkappagantu.allocator.repository;

import javax.persistence.*;

import com.rkappagantu.allocator.model.Control_Table;
import com.rkappagantu.allocator.service.FileMoverService;
import org.hibernate.LockOptions;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.List;

@Repository
@Transactional
public class ControlTableRepositoryImpl implements ControlTableRepository {

    @PersistenceContext
    private EntityManager manager;
    private boolean bGotLock = false;
    FileChannel channel;
    FileLock lock;
    RandomAccessFile file;

    @Override
    public List<Control_Table> findAll() {
        Query q = manager.createNamedQuery("findAll", Control_Table.class);
        return q.getResultList();
    }

    @Override
    @Lock(value = LockModeType.PESSIMISTIC_WRITE) // adds 'FOR UPDATE' statement
    @QueryHints(value={@QueryHint(name = "javax.persistence.lock.timeout", value = LockOptions.SKIP_LOCKED+"")})
    public List<Control_Table> customMethod(String itemType) {
            Query q = manager.createNamedQuery("customMethod", Control_Table.class);
            q.setParameter(1, itemType);
            List<Control_Table> result = q.getResultList();
            return result;
    }
    @Override
    @Transactional
    public void startTransaction() {
        Query q = manager.createNamedQuery("startTransaction");
        q.executeUpdate();
    }

    @Override
    @Transactional
    @Modifying
    public boolean updateControl(Control_Table c) {
        manager.merge(c);
        return true;
    }

}
