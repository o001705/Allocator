package com.rkappagantu.allocator;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.rkappagantu.allocator.model.Item;
import com.rkappagantu.allocator.repository.ControlTableRepository;
import com.rkappagantu.allocator.repository.ItemRepository;
import com.rkappagantu.allocator.service.FileMoverService;
import com.rkappagantu.allocator.util.FileLocker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.rkappagantu.allocator.model.Control_Table;

@SpringBootApplication
public class Allocator implements CommandLineRunner {

    @Autowired
    ControlTableRepository controlTableRepository;
    @Autowired
    ItemRepository itemRepository;

    public static void main(String[] args) {
        SpringApplication.run(Allocator.class, args);
    }

    private void removeDuplicates(List<Item> pendingItems, List<Path> fileList) {
        for (Item item : pendingItems) {
            String itemToRemove = item.getItemData(); // The item value to search and remove
            fileList.removeIf(path -> path.getFileName().toString().equals(itemToRemove));
        }
    }

    private ProcessThread[] createExecutorThreads(String directoryPath, int totalExecutors) {
        ProcessThread[] tList = new ProcessThread[totalExecutors];
        System.out.println("Creating " + tList.length + " Executor Threads");
        for (int i = 0; i < tList.length; i++)
            tList[i] = new ProcessThread();
        int count = 0;
        for ( ProcessThread t : tList) {
            t.setParams(itemRepository, directoryPath, String.valueOf(++count));
            t.start();
        }
        return tList;
    }

    private void waitForExecutors(ProcessThread [] tList) {
        for ( ProcessThread t : tList) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void assignWorkToExecutors(List<Path> fileList, int totalExecutors, int batch) {
        int itemCount = fileList.size();
        int executor = 0;
        int maxAssignment = itemCount / totalExecutors;
        int totalInserted = 0;
        List<Item> itemList = new ArrayList();
        for (Path path : fileList) {
            if ((totalInserted % maxAssignment == 0) && (executor < totalExecutors))
                executor++;
            Item item = new Item("ASPECT", "PENDING", batch, path.getFileName().toString(), String.valueOf(executor));
            itemList.add(item);
            totalInserted++;
            if ((totalInserted % 50 == 0) || (totalInserted >= itemCount)) {
                itemRepository.createItems(itemList);
                itemList.clear();
            }
        }
    }

    @Override
    public void run(String... args) {
        int batch;
        int totalExecutors = 3;
        final String directoryPath = "D:\\CODE\\UPLOAD";

        FileLocker fLock = new FileLocker();
        if (fLock.getExclusiveLock("D:\\LOCK.TXT")) {
            List<Control_Table> l = controlTableRepository.customMethod("ASPECT");
            if (l != null) {
                FileMoverService fms = new FileMoverService();
                // Create Dummy Files for testing
                // create instance of Random class
                Random rand = new Random();
                int numFiles = rand.nextInt(1000);
                fms.createDummyFiles(directoryPath, numFiles);
                batch = l.get(0).getBatchNum() + 1;

                List<Item> pendingItems = itemRepository.getPendingItems("PENDING", "ASPECT");
                List<Path> fileList = fms.listFiles(directoryPath);
                removeDuplicates(pendingItems, fileList);
                ProcessThread[] pList = createExecutorThreads(directoryPath, totalExecutors);
                assignWorkToExecutors(fileList, totalExecutors, batch);
                waitForExecutors(pList);
                l.get(0).setBatchNum(batch);
                controlTableRepository.updateControl(l.get(0));
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            fLock.releaseExclusiveLock();
        } else {
            System.out.println("File lock could not be acquired");
        }
    }
}

class ProcessThread extends Thread {
    private ItemRepository itemRepository;
    private String directoryPath;
    private String executorId;

    public void setParams(ItemRepository itemRepository, String directoryPath, String executorId) {
        this.itemRepository = itemRepository;
        this.directoryPath = directoryPath;
        this.executorId = executorId;
    }

    public void run() {
        List<Item> pendingItems = itemRepository.getPendingItemsByExecutor("PENDING", executorId);
        for (Item item : pendingItems) {
            item.setItemStatus("IN PROGRESS");
            File file = new File(directoryPath, item.getItemData());
            file.delete();
            item.setItemStatus("COMPLETED");
            itemRepository.updateItem(item);
        }
    }
}
