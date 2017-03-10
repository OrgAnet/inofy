package org.organet.inofy;

import org.organet.inofy.Tuple.Tuple;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

// TODO May be implement 'java.nio.file.Path'
public class SharedFile extends File implements SerializesFields {
  private String path = null;
  private String mimeType = null;
  private String hash = null;
  private Long size = null;
  private Long lastModified = null;


  SharedFile(String pathname) {
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

  static SharedFile fromFile(File file) {
    return new SharedFile(file.getPath());
  }


  void initialize() {
    probeMimeType();

    path = getPath();
    // `probeMimeType` method sets the `mimeType` field
    hash = getHash();
    size = this.length();
    lastModified = lastModified();
  }


  private void probeMimeType() {
    // TODO Implement `probeMimeType` method

    mimeType = "text/plain";
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


  // TODO May be extract this to SharedFileFieldSerializer class (think SOLID)
  @Override
  public List<Tuple> serializeFields() {
    List<Tuple> ret = new ArrayList<>(5);

    ret.add(App.Triple.createTuple("filepath", getPath(), "String"));
    ret.add(App.Triple.createTuple("mimeType", mimeType, "String"));
    ret.add(App.Triple.createTuple("hash", getHash(), "String"));
    ret.add(App.Triple.createTuple("size", String.valueOf(size), "Long"));
    ret.add(App.Triple.createTuple("last_modified", String.valueOf(lastModified()), "Long"));

    return ret;
  }

  @Override
  public String serializeFields(String fieldsSeparator, String valueSeparator) {
    List<Tuple> serializedFields = serializeFields();
    StringBuilder ret = new StringBuilder();

    serializedFields.forEach((Tuple t) -> {
      String k = t.getNthValue(0),
             v = t.getNthValue(1),
             p = t.getNthValue(2); // type

      ret.append(String.format(
        "%s%s",
        k, valueSeparator
      ));

      ret.append(String.format(
        p.equals("String") ? "'%s'" : "%s",
        v
      ));

      ret.append(fieldsSeparator);
    });

    return ret.substring(0, ret.length() - 1);
  }
}
