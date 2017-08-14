package com.github.dannil.jdfs.accesser;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.github.dannil.jdfs.model.File;
import com.github.dannil.jdfs.service.FileService;
import com.github.dannil.jdfs.utility.FileUtility;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LocalFileAccesser {

    private static final Logger LOGGER = LogManager.getLogger(LocalFileAccesser.class);

    private static LocalFileAccesser accesser;

    private static Object mutex = new Object();

    private FileService service;

    public static LocalFileAccesser getInstance() {
        if (accesser == null) {
            synchronized (mutex) {
                if (accesser == null) {
                    accesser = new LocalFileAccesser();
                }
            }
        }
        return accesser;
    }

    public LocalFileAccesser() {
        this.service = new FileService();
    }

    public File toModelFile(java.io.File f) {
        // Get path
        String path = f.getPath();

        // Get last changed time
        Instant i = Instant.ofEpochMilli(f.lastModified());

        LocalDateTime t2 = LocalDateTime.ofInstant(i, ZoneId.systemDefault());

        OffsetDateTime t = OffsetDateTime.ofInstant(i, ZoneId.systemDefault());

        // System.out.println(t);
        // System.out.println(t2);

        // Get hash
        String hash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA1");
            hash = FileUtility.getChecksum(digest, f);
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }

        File modelFile = new File(path, t, hash);
        return modelFile;
    }

    public List<File> getModelFiles() {
        LOGGER.info("Retrieving local model files");
        List<java.io.File> localFiles = getFiles();
        List<File> dbFiles = new ArrayList<>(localFiles.size());
        for (java.io.File f : localFiles) {
            dbFiles.add(toModelFile(f));
        }
        LOGGER.info("Local model files retrieved");
        return dbFiles;
    }

    public List<java.io.File> getFiles() {
        synchronized (mutex) {
            LOGGER.info("Retrieving local files");
            Collection<java.io.File> localFiles = FileUtils.listFiles(new java.io.File("files"),
                    TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
            LOGGER.info("Local files retrieved");
            return new ArrayList<>(localFiles);
        }
    }

    public List<java.io.File> getDeletedFiles() {
        synchronized (mutex) {
            List<File> dbFiles = this.service.getAll();
            List<java.io.File> localFiles = getFiles();

            List<String> paths = new ArrayList<>();
            for (java.io.File f : localFiles) {
                paths.add(f.getPath());
            }

            List<java.io.File> deletedFiles = new ArrayList<>();
            for (int i = 0; i < dbFiles.size(); i++) {
                File f = dbFiles.get(i);
                if (!paths.contains(f.getPath())) {
                    deletedFiles.add(new java.io.File(f.getPath()));
                }
            }
            System.out.println("DELETED: " + deletedFiles);
            return deletedFiles;
        }
    }

    public void indexLocalDatabase() {
        synchronized (mutex) {
            LOGGER.info("Started indexing database, this may take awhile...");

            List<java.io.File> localFiles = getFiles();
            int interval = Math.max(localFiles.size() / 10, 10);

            // Delete entries from database which have no local file representation
            // TODO
            List<File> modelFiles = this.service.getAll();
            for (int i = 0; i < localFiles.size(); i++) {
                File f = toModelFile(localFiles.get(i));
                for (int j = 0; j < modelFiles.size(); j++) {

                }
            }

            // Add new or update existing entries in database
            for (int i = 0; i < localFiles.size(); i++) {
                File f = toModelFile(localFiles.get(i));
                if ((i + 1) % interval == 0) {
                    // Alot of files, display progress to user so they know it hasn't
                    // stalled
                    LOGGER.info("Indexing {} of {} files, processing {}", i + 1, localFiles.size(), f.getPath());
                }
                this.service.saveOrUpdate(f);
            }
            LOGGER.info("Indexed database with local files");
        }
    }

}
