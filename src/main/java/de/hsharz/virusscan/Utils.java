package de.hsharz.virusscan;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {

    public static String getMD5Hash(File f){
        byte[] data = null;
        try{
            data = Files.readAllBytes(f.toPath());
            byte[] hash = MessageDigest.getInstance("MD5").digest(data);
            return new BigInteger(1, hash).toString(16);
        } catch (IOException e  ) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
