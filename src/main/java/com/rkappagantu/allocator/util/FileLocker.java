package com.rkappagantu.allocator.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class FileLocker {
    RandomAccessFile file;
    FileChannel channel;
    FileLock lock;

    public FileLocker() {

    }

    public boolean getExclusiveLock(String fileName) {
        boolean retVal = false;
        try {
            file = new RandomAccessFile(fileName, "rw");
            channel = file.getChannel();
            // Acquire the file lock
            lock = channel.tryLock();
            if (lock != null) {
                retVal = true;
                System.out.println("File lock is acquired.");
            } else {
                System.out.println("File lock can not be acquired.");
            }
        }catch (FileNotFoundException e) {
            System.out.println("File not found. Create file first");
        } catch (IOException e) {
            System.out.println("File lock can not be acquired.");
        }
        return retVal;
    }

    public boolean releaseExclusiveLock() {
        boolean retVal = false;
        // Release the lock
        if (lock != null) {
            try {
                lock.release();
                channel.close();
                file.close();
                retVal = true;
            } catch (IOException e) {
            }
        }
        return retVal;
    }

}
