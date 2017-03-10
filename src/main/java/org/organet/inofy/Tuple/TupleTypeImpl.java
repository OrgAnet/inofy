package org.organet.inofy.Tuple;

// http://stackoverflow.com/a/3642623/250453
class TupleTypeImpl implements TupleType {
  final Class<?>[] types;

  TupleTypeImpl(Class<?>[] types) {
    this.types = (types != null ? types : new Class<?>[0]);
  }

  public int size() {
    return types.length;
  }

  public Class<?> getNthType(int i) {
    return types[i];
  }

  public Tuple createTuple(Object... values) {
    if ((values == null && types.length == 0) ||
      (values != null && values.length != types.length)) {
      throw new IllegalArgumentException(
        "Expected "+types.length+" values, not "+
          (values == null ? "(null)" : values.length) + " values");
    }

    if (values != null) {
      for (int i = 0; i < types.length; i++) {
        final Class<?> nthType = types[i];
        final Object nthValue = values[i];

        if (nthValue != null && ! nthType.isAssignableFrom(nthValue.getClass())) {
          throw new IllegalArgumentException(
            "Expected value #"+i+" ('"+
              nthValue+"') of new Tuple to be "+
              nthType+", not " +
              (nthValue != null ? nthValue.getClass() : "(null type)"));
        }
      }
    }

    return new TupleImpl(this, values);
  }
}
