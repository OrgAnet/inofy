package org.organet.inofy;

import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.LinkOption.*;
import java.nio.file.attribute.*;
import java.io.*;
import java.util.*;

class Watcher {
  private final WatchService watcher;
  private final Map<WatchKey, Path> keys;
  private boolean recursive = false;
  private boolean trace = false;

  @SuppressWarnings("unchecked")
  static <T> WatchEvent<T> cast(WatchEvent<?> event) {
    return (WatchEvent<T>)event;
  }

  /**
   * Creates a WatchService and registers the given directory
   */
  Watcher(Path dir, boolean recursive) throws IOException {
    this.watcher = FileSystems.getDefault().newWatchService();
    this.keys = new HashMap<>();
    this.recursive = recursive;

    if (recursive) {
      System.out.println(String.format(
        "[ INFO ] Watcher | Scanning %s...",
        dir
      ));

      registerAll(dir);

      System.out.println("[ INFO ] Watcher | Scan is done.");
    } else {
      register(dir);
    }

    // enable trace after initial registration
    this.trace = true;
  }

  Watcher(String path, boolean recursive) throws IOException {
    this(Paths.get(path), recursive);
  }

  /**
   * Register the given directory with the WatchService
   */
  private void register(Path dir) throws IOException {
    WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
    if (trace) {
      Path prev = keys.get(key);
      if (prev == null) {
        // TODO Continue formatting printed text
        System.out.format("register: %s\n", dir);
      } else {
        if (!dir.equals(prev)) {
          System.out.format("update: %s -> %s\n", prev, dir);
        }
      }
    }
    keys.put(key, dir);
  }

  /**
   * Register the given directory, and all its sub-directories, with the
   * WatchService.
   */
  private void registerAll(final Path start) throws IOException {
    // register directory and sub-directories
    Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        register(dir);

        return FileVisitResult.CONTINUE;
      }
    });
  }

  /**
   * Process all events for keys queued to the watcher
   */
  void processEvents() {
    for (;;) {
      // wait for key to be signalled
      WatchKey key;
      try {
        key = watcher.take();
      } catch (InterruptedException x) {
        return;
      }

      Path dir = keys.get(key);
      if (dir == null) {
        System.err.println("WatchKey not recognized!!");
        continue;
      }

      for (WatchEvent<?> event: key.pollEvents()) {
        WatchEvent.Kind kind = event.kind();

        // TBD - provide example of how OVERFLOW event is handled
        if (kind == OVERFLOW) {
          continue;
        }

        // Context for directory entry event is the file name of entry
        WatchEvent<Path> ev = cast(event);
        Path name = ev.context();
        Path child = dir.resolve(name);

        // print out event
        System.out.format("%s: %s\n", event.kind().name(), child);

        // HACK The line below added
        if (kind == ENTRY_MODIFY && !Files.isDirectory(child, NOFOLLOW_LINKS)) {
          SharedFileStorage.store(new SharedFile(child.toString()));
          //String filePath = child.toString();
          //App.indexFile(filePath);
        }

        // if directory is created, and watching recursively, then
        // register it and its sub-directories
        if (recursive && (kind == ENTRY_CREATE)) {
          try {
            if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
              registerAll(child);
            }
          } catch (IOException x) {
            // ignore to keep sample readable
          }
        }
      }

      // reset key and remove from set if directory no longer accessible
      boolean valid = key.reset();
      if (!valid) {
        keys.remove(key);

        // all directories are inaccessible
        if (keys.isEmpty()) {
          break;
        }
      }
    }
  }
}
