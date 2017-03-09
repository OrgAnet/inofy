package org.organet.inofy;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;

public class SharedFile extends File {
  private String hash = null;
  private String mimeType = "text/plain";

  public SharedFile(String pathname) {
    super(pathname);

    probeMimeType();
  }

  public SharedFile(String parent, String child) {
    super(parent, child);

    probeMimeType();
  }

  public SharedFile(File parent, String child) {
    super(parent, child);

    probeMimeType();
  }

  public SharedFile(URI uri) {
    super(uri);

    probeMimeType();
  }

  private void probeMimeType() {
    // TODO Implement `probeMimeType` method
  }

  public String getMimeType() {
    return mimeType;
  }

  public String getHash(boolean force) {
    if (hash.length() > 0 && !force) {
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

  public String getHash() {
    return getHash(false);
  }

  public double getSize() {
    return this.length();
  }
}
