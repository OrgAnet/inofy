package org.organet.inofy.Tuple;

/**
 * Represents a type of tuple.  Used to define a type of tuple and then
 * create tuples of that type.
 *
 * http://stackoverflow.com/a/3642623/250453
 */
public interface TupleType {
  int size();

  Class<?> getNthType(int i);

  /**
   * Tuple are immutable objects.  Tuples should contain only immutable objects or
   * objects that won't be modified while part of a tuple.
   *
   * @param values
   * @return Tuple with the given values
   * @throws IllegalArgumentException if the wrong # of arguments or incompatible tuple values are provided
   */
  Tuple createTuple(Object... values);

  class DefaultFactory {
    public static TupleType create(final Class<?>... types) {
      return new TupleTypeImpl(types);
    }
  }
}
