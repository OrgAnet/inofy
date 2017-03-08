package org.organet.inofy;

import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class App {
  private static String sharedDirPath = "/home/ozan/organet_shared";
  private static File sharedDir;

  public static void main(String[] args) {
    Options options = new Options();

    options.addOption("S", "shared-dir", true, "Absolute directory path to shared files and folders.");
    // TODO Add CLI arguments here

    CommandLineParser parser = new GnuParser();
    CommandLine cmd;
    try {
      cmd = parser.parse(options, args);
    } catch (ParseException e) {
      System.out.println("Could not parse the commandline arguments. Aborting.");

      return;
    }

    // Set global variables by CLI arguments
    if (cmd.hasOption('S')) {
      sharedDirPath = cmd.getOptionValue('S');
    }

    // Update variables which depends CLI arguments
    sharedDir = new File(sharedDirPath);

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
