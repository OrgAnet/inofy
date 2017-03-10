package org.organet.inofy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class Hasher {
  static String calculateFileHash(String path) throws IOException, NoSuchAlgorithmException {
    int buff = 16384;
    try {
      RandomAccessFile file = new RandomAccessFile(path, "r");

      long startTime = System.nanoTime();
      MessageDigest hashSum = MessageDigest.getInstance("SHA-256");

      byte[] buffer = new byte[buff];
      byte[] partialHash = null;

      long read = 0;

      // calculate the hash of the hole file for the test
      long offset = file.length();
      int unitSize;
      while (read < offset) {
        unitSize = (int) (((offset - read) >= buff) ? buff : (offset - read));
        file.read(buffer, 0, unitSize);

        hashSum.update(buffer, 0, unitSize);

        read += unitSize;
      }

      file.close();
      partialHash = new byte[hashSum.getDigestLength()];
      partialHash = hashSum.digest();

      long endTime = System.nanoTime();

      //System.out.println(endTime - startTime);

      return String.format("%064x", new java.math.BigInteger(1, partialHash));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    return null;
  }
}
