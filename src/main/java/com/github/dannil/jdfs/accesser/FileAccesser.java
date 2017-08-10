package com.github.dannil.jdfs.accesser;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    public Collection<File> getLocalFiles() {
        synchronized (mutex) {
            LOGGER.info("Retrieving local files");
            Collection<java.io.File> localFiles = FileUtils.listFiles(new java.io.File("files"),
                    TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);

            Collection<File> dbFiles = new ArrayList<>(localFiles.size());
            for (java.io.File f : localFiles) {
                // Get path
                String path = f.getPath();

                // Get last changed time
                Instant i = Instant.ofEpochMilli(f.lastModified());
                ZonedDateTime t = ZonedDateTime.ofInstant(i, ZoneId.systemDefault());

                System.out.println(t);

                // Get hash
                String hash = null;
                try {
                    MessageDigest digest = MessageDigest.getInstance("SHA1");
                    hash = FileUtility.getFileChecksum(digest, f);
                } catch (NoSuchAlgorithmException | IOException e) {
                    throw new RuntimeException(e);
                }

                File dbFile = new File(path, t);
                dbFile.setHash(hash);
                dbFiles.add(dbFile);
            }
            LOGGER.info("Local files retrieved");
            return dbFiles;
        }
    }

    public void indexLocalDatabase() {
        synchronized (mutex) {
            LOGGER.info("Started indexing database, this may take awhile...");
            List<File> files = new ArrayList<>(getLocalFiles());
            int interval = Math.max(files.size() / 10, 10);
            for (int i = 0; i < files.size(); i++) {
                File f = files.get(i);
                if ((i + 1) % interval == 0) {
                    // Alot of files, display progress to user so they know it hasn't
                    // stalled
                    LOGGER.info("Indexing {} of {} files, processing {}", i + 1, files.size(), f.getPath());
                }
                this.dba.saveOrUpdate(f);
            }
            LOGGER.info("Indexed database with local files");
        }
    }

}
