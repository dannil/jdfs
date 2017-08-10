package com.github.dannil.jdfs.accesser;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;

import com.github.dannil.jdfs.dba.FileDBA;
import com.github.dannil.jdfs.model.File;
import com.github.dannil.jdfs.utility.FileUtility;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileAccesser {

    private static final Logger LOGGER = LogManager.getLogger(FileAccesser.class);

    private static FileAccesser accesser;

    private static Object mutex = new Object();

    private FileDBA dba;

    public static FileAccesser getInstance() {
        if (accesser == null) {
            synchronized (mutex) {
                if (accesser == null) {
                    accesser = new FileAccesser();
                }
            }
        }
        return accesser;
    }

    public FileAccesser() {
        this.dba = new FileDBA();
    }

    public File toModelFile(java.io.File f) {
        // Get path
        String path = f.getPath();

        // Get last changed time
        Instant i = Instant.ofEpochMilli(f.lastModified());

        LocalDateTime t2 = LocalDateTime.ofInstant(i, ZoneId.systemDefault());

        ZonedDateTime t = ZonedDateTime.ofInstant(i, ZoneId.systemDefault());

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

        File modelFile = new File(path, t);
        modelFile.setHash(hash);
        return modelFile;
    }

    public Collection<File> getModelFiles() {
        LOGGER.info("Retrieving local model files");
        Collection<java.io.File> localFiles = getLocalFiles();
        Collection<File> dbFiles = new ArrayList<>(localFiles.size());
        for (java.io.File f : localFiles) {
            dbFiles.add(toModelFile(f));
        }
        LOGGER.info("Local model files retrieved");
        return dbFiles;
    }

    public Collection<java.io.File> getLocalFiles() {
        synchronized (mutex) {
            LOGGER.info("Retrieving local files");
            Collection<java.io.File> localFiles = FileUtils.listFiles(new java.io.File("files"),
                    TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
            LOGGER.info("Local files retrieved");
            return localFiles;
        }
    }

    public void indexLocalDatabase() {
        synchronized (mutex) {
            LOGGER.info("Started indexing database, this may take awhile...");
            ArrayList<java.io.File> localFiles = new ArrayList<>(getLocalFiles());
            int interval = Math.max(localFiles.size() / 10, 10);
            for (int i = 0; i < localFiles.size(); i++) {
                File f = toModelFile(localFiles.get(i));
                if ((i + 1) % interval == 0) {
                    // Alot of files, display progress to user so they know it hasn't
                    // stalled
                    LOGGER.info("Indexing {} of {} files, processing {}", i + 1, localFiles.size(), f.getPath());
                }
                this.dba.saveOrUpdate(f);
            }
            LOGGER.info("Indexed database with local files");
        }
    }

}
