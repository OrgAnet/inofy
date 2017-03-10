package org.organet.inofy.Tuple;

/**
 * Tuple are immutable objects.  Tuples should contain only immutable objects or
 * objects that won't be modified while part of a tuple.
 *
 * http://stackoverflow.com/a/3642623/250453
 */
public interface Tuple {
  TupleType getType();

  int size();

  <T> T getNthValue(int i);
}
