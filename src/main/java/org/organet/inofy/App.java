package org.organet.inofy;

import org.apache.commons.cli.*;
import org.organet.inofy.Persistance.InMemoryStorage;
import org.organet.inofy.Persistance.StringStorage;
import org.organet.inofy.SharedFile.SharedFile;
import org.organet.inofy.SharedFile.SharedFileDeserializer;
import org.organet.inofy.SharedFile.SharedFileSerializer;
import org.organet.inofy.Tuple.TupleType;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class App {
  public static final String FIELDS_SEPARATOR = ",";
  static final String VALUE_SEPARATOR = "=";


  private static String sharedDirPath = "/home/ozan/organet_shared";
  private static File sharedDir;

  static InMemoryStorage<SharedFile> storage = null;
  public static final TupleType Triple = TupleType.DefaultFactory.create(
    String.class,
    String.class,
    String.class
  );

  public static void main(String[] args) {
    Options options = new Options();

    options.addOption("p", "shared-path", true, "Absolute directory path to shared files and folders.");
    options.addOption("w", "no-init-walk", false, "Do not walk the shared directory while application initialization.");
    // TODO Add CLI arguments here

    CommandLineParser parser = new GnuParser();
    CommandLine cmd;
    try {
      cmd = parser.parse(options, args);
    } catch (ParseException e) {
      System.out.println("[ERROR ] main | Could not parse the commandline arguments. Aborting.");

      return;
    }

    // Set global variables by CLI arguments
    if (cmd.hasOption('p')) {
      sharedDirPath = cmd.getOptionValue('p');
    }

    // Update variables which depends CLI arguments
    sharedDir = new File(sharedDirPath);

    if (!sharedDir.isDirectory()) {
      System.out.println("[ERROR ] main | Shared directory was not set properly. Aborting.");

      return;
    }

    // Initialize in-memory DB connection
    storage = new InMemoryStorage<>(new SharedFileSerializer(), new SharedFileDeserializer());

    // Walk shared directory directory for indexing files
    if (cmd.hasOption('w')) {
      System.out.println("[ INFO ] main | No shared directory walk.");
    } else {
      File[] sharedFiles = sharedDir.listFiles();

      if (sharedFiles != null) {
        for (File child : sharedFiles) {
          storage.insert(SharedFile.fromFile(child));
        }
      }
    }

    // TEST - TEST - TEST - TEST - TEST - TEST
    SharedFile foo = storage.get(1);
    if (foo != null) {
      System.out.println(foo.getPath());
    }
    // TEST - TEST - TEST - TEST - TEST - TEST

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

      System.out.println(String.format("[ INFO ] indexFile | SHA-256 hash of '%s' is '%s'.", path, fileHash));

      StringStorage.crud(path, fileHash);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
  }
}
