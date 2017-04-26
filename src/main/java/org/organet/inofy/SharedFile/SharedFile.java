package org.organet.inofy.SharedFile;

import org.organet.inofy.Hasher;
import org.organet.inofy.Persistable;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;

// TODO May be implement 'java.nio.file.Path'
public class SharedFile extends File implements Persistable {
  private String mimeType = null;
  private String hash = null;


  public SharedFile(String pathname) {
    super(pathname);

    initialize();
  }

  public SharedFile(String parent, String child) {
    super(parent, child);

    initialize();
  }

  public SharedFile(File parent, String child) {
    super(parent, child);

    initialize();
  }

  public SharedFile(URI uri) {
    super(uri);

    initialize();
  }

  public static SharedFile fromFile(File file) {
    return new SharedFile(file.getPath());
  }


  void initialize() {
    probeMimeType();

    // `probeMimeType` method sets the `mimeType` field
    hash = getHash();
  }


  private void probeMimeType() {
    // TODO Implement `probeMimeType` method

    mimeType = "text/plain";
  }


  String getMimeType() {
    return mimeType;
  }

  String getHash(boolean force) {
    if ((hash != null && hash.length() > 0) && !force) {
      return hash;
    }

    try {
      hash = Hasher.calculateFileHash(this.getPath());
    } catch (IOException e) {
      e.printStackTrace();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }

    return hash;
  }

  String getHash() {
    return getHash(false);
  }

  Long getSize() {
    return length();
  }

  Long getLastModified() {
    return lastModified();
  }
}
