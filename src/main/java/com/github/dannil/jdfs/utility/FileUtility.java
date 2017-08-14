package com.github.dannil.jdfs.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

import com.github.dannil.jdfs.model.FileModel;

public class FileUtility {

    public static FileModel toModelFile(java.io.File f) {
        // Get path
        String path = f.getPath();

        // Get last changed time
        Instant i = Instant.ofEpochMilli(f.lastModified());
        OffsetDateTime t = OffsetDateTime.ofInstant(i, ZoneId.systemDefault());

        // Get hash
        String hash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA1");
            hash = getChecksum(digest, f);
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }

        FileModel modelFile = new FileModel(path, t, hash);
        return modelFile;
    }

    public static String getChecksum(MessageDigest digest, File file) throws IOException {
        // Get file input stream for reading the file content
        FileInputStream fis = new FileInputStream(file);

        // Create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;

        // Read file data and update in message digest
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        }

        // close the stream; We don't need it now.
        fis.close();

        // Get the hash's bytes
        byte[] bytes = digest.digest();

        // This bytes[] has bytes in decimal format;
        // Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        // return complete hash
        return sb.toString();
    }

}
