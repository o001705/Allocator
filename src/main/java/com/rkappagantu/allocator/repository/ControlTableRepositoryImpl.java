package com.rkappagantu.allocator.repository;

import javax.persistence.*;

import com.rkappagantu.allocator.model.Control_Table;
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
        if (getExclusiveLock()) {
            Query q = manager.createNamedQuery("customMethod", Control_Table.class);
            q.setParameter(1, itemType);
            List<Control_Table> result = q.getResultList();
            return result;
        }
        return null;
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
        releaseExclusiveLock();
        return true;
    }

    private boolean getExclusiveLock() {
        bGotLock = false;
        try {
            /*
            //https://u.pcloud.link/publink/show?code=XZa6CEVZExlJqd15XbFGqUKUqO6VMB88R2RV
            SmbFile f = new SmbFile("https://u.pcloud.link/publink/show?code=XZa6CEVZExlJqd15XbFGqUKUqO6VMB88R2RV");
            auth = new NtlmPasswordAuthentication(null, "user", "password");
            SmbFile dir = new SmbFile(url, auth);
            for (SmbFile f : dir.listFiles())
            {
                System.out.println(f.getName());
            }
            */
            file = new RandomAccessFile("d:\\lock.txt", "rw");
            channel = file.getChannel();

            // Acquire the file lock
            lock = channel.tryLock();
            if (lock != null) {
                bGotLock = true;
                System.out.println("File lock is acquired.");
            } else {
                System.out.println("File lock can not be acquired.");
            }
        }catch (FileNotFoundException e) {
            System.out.println("File not found. Create file first");
        } catch (IOException e) {
            System.out.println("File lock can not be acquired.");
        }
        return bGotLock;
    }

    private boolean releaseExclusiveLock() {
        // Release the lock
        if (bGotLock) {
            try {
                lock.release();
                bGotLock = false;
                channel.close();
                file.close();
            } catch (IOException e) {
            }
        }
        return (!bGotLock);
    }
}
