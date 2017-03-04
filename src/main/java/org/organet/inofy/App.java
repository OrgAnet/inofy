package org.organet.inofy;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class App {
  private static final String SHARED_DIR_PATH = "/home/ozan/organet_shared";
  private static File sharedDir;

  public static void main(String[] args) {
    sharedDir = new File(SHARED_DIR_PATH);

    if (!sharedDir.isDirectory()) {
      System.out.println("Error: OrgAnet shared directory was not set properly. Aborting.");

      return;
    }

    // Initialize in-memory DB connection
    Storage.initialize();

    // Walk shared directory directory for indexing files
    File[] sharedFiles = sharedDir.listFiles();
    if (sharedFiles != null) {
      for (File child : sharedFiles) {
        String filePath = child.toString();
        indexFile(filePath);
      }
    }

    // Watch the shared directory directory recursively for changes
    // (create, modify and delete) and update DB accordingly
    try {
      new Watcher(sharedDir.getPath(), true).processEvents();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  static void indexFile(String path) {
    try {
      String fileHash = Hasher.calculateFileHash(path);

      System.out.println(String.format("SHA-256 hash of '%s' is '%s'.", path, fileHash));

      Storage.crud(path, fileHash);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
  }
}
