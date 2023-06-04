package com.rkappagantu.allocator.repository;

import com.rkappagantu.allocator.model.Item;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class ItemRepositoryImpl implements ItemRepository {
    @PersistenceContext
    private EntityManager manager;

    @Override
    public List<Item> getPendingItems(String itemStatus, String itemType) {
        Query q = manager.createNamedQuery("getPendingItems", Item.class);
        q.setParameter(1, itemStatus);
        q.setParameter(2, itemType);
        return q.getResultList();
    }

    @Override
    public List<Item> getPendingItemsByExecutor(String itemStatus, String itemExecutor) {
        Query q = manager.createNamedQuery("getPendingItemsByExecutor", Item.class);
        q.setParameter(1, itemStatus);
        q.setParameter(2, itemExecutor);
        return q.getResultList();
    }

    @Override
    @Transactional
    public List<Item> updateItemsBulk(List<Item> items) {
        List <Item> result = new ArrayList<>();
        try {
            for (Item item : items) {
                manager.persist(item);
                result.add(item);
            }
        } catch (Exception e) {
            result = null;
            e.printStackTrace();
        }
        return result;
    }

    @Override
    @Transactional
    public Item updateItem(Item item) {
        Item result = manager.merge(item);
        return result;
    }

    @Override
    @Transactional
    public void createItems(List<Item> items) {
        try {
            for (Item item : items) {
                manager.persist(item);
            }
        } catch(ConstraintViolationException ignore) {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
