package com.rkappagantu.allocator.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileMoverService {
    public FileMoverService() {

    }
    public void createDummyFiles(String directoryPath, int count) {
        System.out.println(directoryPath);
        for (int i = 0; i < count; i++) {
            String name = "dummy" + i;
            try {
                File tmp = File.createTempFile(name, ".txt", new File(directoryPath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public List<Path> listFiles(String directoryPath) {
        List <Path> result = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directoryPath))) {
            for (Path filePath : directoryStream) {
                if (Files.isRegularFile(filePath)) {
                    result.add(filePath.getFileName());
                    // Perform desired operations on the file here
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = null;
        }
        return result;
    }
    public boolean deleteFile(Path filePath) {
        try {
            File file = filePath.toFile();
            return file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
