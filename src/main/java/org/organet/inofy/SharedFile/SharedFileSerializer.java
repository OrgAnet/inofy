package org.organet.inofy.SharedFile;

import org.organet.inofy.App;
import org.organet.inofy.Serializer;
import org.organet.inofy.Tuple.Tuple;

import java.util.ArrayList;
import java.util.List;

public class SharedFileSerializer implements Serializer<SharedFile> {
  @Override
  public List<Tuple> serializeFields(SharedFile sharedFile) {
    List<Tuple> ret = new ArrayList<>(5);

    ret.add(App.Triple.createTuple("filepath", sharedFile.getPath(), "String"));
    ret.add(App.Triple.createTuple("mimeType", sharedFile.getMimeType(), "String"));
    ret.add(App.Triple.createTuple("hash", sharedFile.getHash(), "String"));
    ret.add(App.Triple.createTuple("size", String.valueOf(sharedFile.getSize()), "Long"));
    ret.add(App.Triple.createTuple("last_modified", String.valueOf(sharedFile.getLastModified()), "Long"));

    return ret;
  }

  @Override
  public String serializeFields(SharedFile sharedFile, String fieldsSeparator, String valueSeparator) {
    List<Tuple> serializedFields = serializeFields(sharedFile);
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
