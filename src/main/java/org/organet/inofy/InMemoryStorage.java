package org.organet.inofy;

import org.organet.inofy.Tuple.Tuple;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

class InMemoryStorage<V extends SerializesFields> implements Storage<V> {
  private static final String DB_DRIVER = "org.h2.Driver";
  private static final String DB_CONNECTION = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
  private static final String DB_USER = "sa";
  private static final String DB_PASSWORD = "";
  private static final String DB_TABLE = "File";

  private static Connection connection = null;


  InMemoryStorage() {
    initialize();
  }


  private boolean connect() {
    try {
      Class.forName(DB_DRIVER);
    } catch (ClassNotFoundException e) {
      System.out.println("[ERROR ] InMemoryStorage.connect | Storage driver class could not be found.");

      return false;
    }

    try {
      connection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
    } catch (SQLException e) {
      System.out.println("[ERROR ] InMemoryStorage.connect | Could not connect to the database.");

      return false;
    }

    try {
      connection.setAutoCommit(false);
    } catch (SQLException e) {
      System.out.println("[ERROR ] InMemoryStorage.connect | Failed to turn off auto commit.");
    }

    System.out.println("[ INFO ] InMemoryStorage.connect | Database connection established.");

    return true;
  }

  private boolean createTheTable() {
    String query = String.format(
      "CREATE TABLE %s (id INT AUTO_INCREMENT PRIMARY KEY, key VARCHAR(256), filepath VARCHAR(256), " +
        "mimetype VARCHAR(64), hash VARCHAR(256), size INT, last_modified BIGINT, keywords VARCHAR(256));",
      DB_TABLE
    );

    if (!execute(query)) {
      return false;
    }

    System.out.println("[ INFO ] InMemoryStorage.createTheTable | Database table created.");

    return true;
  }

  @Override
  public boolean initialize() {
    if (!connect()) {
      return false;
    }

    try {
      connection.setAutoCommit(false);
    } catch (SQLException e) {
      System.out.println("[ERROR ] InMemoryStorage.initialize | Failed to turn off auto commit.");
    }

    return createTheTable();
  }


  @Override
  public void insert(V value) {
    // Construct query
    List<Tuple> serializedFields = value.serializeFields();
    StringBuilder fieldNames = new StringBuilder();
    StringBuilder serializedFieldsString = new StringBuilder();

    // Extract column names
    serializedFields.forEach((Tuple t) -> {
      String k = t.getNthValue(0),
             v = t.getNthValue(1),
             p = t.getNthValue(2); // type

      fieldNames.append(String.format(
        "%s%s",
        k.toLowerCase(Locale.ENGLISH), ",")
      );

      serializedFieldsString.append(String.format(
        p.equals("String") ? "'%s'" : "%s",
        p.equals("String") ? v : String.valueOf(v) // TODO Just write `v` if it is applicable also for 'Double' class
      ));

      serializedFieldsString.append(App.FIELDS_SEPARATOR);
    });

    // Execute the query
    String query = String.format(
      "INSERT INTO %s (%s) VALUES (%s);",
      DB_TABLE,
      fieldNames.substring(0, fieldNames.length() - 1),
      serializedFieldsString.substring(0, serializedFieldsString.length() - 1) //value.serializeFields(App.FIELDS_SEPARATOR, App.VALUE_SEPARATOR)
    );

    execute(query);
  }


  // TODO 1st Find out is this method should return V or not
  @Override
  public V get(Object id) {
    // Construct query
    String query = String.format(
      "SELECT * FROM %s WHERE id=%s",
      DB_TABLE, id.toString()
    );

    // Extract column names
    Map<String, String> results = executeQuery(query);

    if (results == null) {
      System.out.println(String.format(
        "[ERROR ] InMemoryStorage.get | No results were returned for ID of '%s'.",
        id.toString()
      ));

      return null;
    }

    // Create and return the instance
    SharedFile v = new SharedFile(results.get("filepath"));
    return (V) v;
  }

  @Override
  public V getBy(Object fieldName, Object fieldValue) {
    // TODO Construct query

    // TODO Extract column names

    return null;
  }


  @Override
  public void update(Object id, V value) {
    // TODO Construct query

    // TODO Extract column names
  }


  @Override
  public V delete(Object id) {
    // TODO Construct query

    // TODO Extract column names

    return null;
  }


  @Override
  public boolean exists(Object id) {
    // TODO Construct query

    // TODO Extract column names

    return false;
  }


  @Override
  public void close() {
    // TODO Implement `close` method
  }


  private boolean execute(String query) {
    Statement stmt;

    try {
      stmt = connection.createStatement();
    } catch (SQLException e) {
      System.out.println("[ERROR ] InMemoryStorage.execute | Failed to create the statement.");

      return false;
    }

    try {
      stmt.execute(query);
    } catch (SQLException e) {
      System.out.println(String.format(
        "[ERROR ] InMemoryStorage.execute | Failed to execute query. '%s'",
        query
      ));

      return false;
    }

    System.out.println(String.format(
      "[ INFO ] InMemoryStorage.execute | Query executed successfully '%s'",
      query
    ));

    try {
      stmt.close();
    } catch (SQLException e) {
      System.out.println("[ERROR ] InMemoryStorage.execute | Failed to finalize statement.");

      return false;
    }

    return true;
  }

  private Map<String, String> executeQuery(String query) {
    Statement stmt;
    Map<String, String> results = new HashMap<>();
    ResultSet resultSet;

    try {
      stmt = connection.createStatement();
    } catch (SQLException e) {
      System.out.println("[ERROR ] InMemoryStorage.execute | Failed to create the statement.");

      return null;
    }

    try {
      resultSet = stmt.executeQuery(query);
    } catch (SQLException e) {
      System.out.println(String.format(
        "[ERROR ] InMemoryStorage.execute | Failed to execute query. '%s'",
        query
      ));

      return null;
    }

    try {
      if (resultSet.next()) {
        for (int i = 1; i < resultSet.getMetaData().getColumnCount(); i++) {
          results.put(resultSet.getMetaData().getColumnName(i).toLowerCase(Locale.ENGLISH), resultSet.getString(i));
        }
      }
    } catch (SQLException e) {
      System.out.println(String.format(
        "[ WARN ] InMemoryStorage.get | Failed to process results for the query. '%s'.",
        query
      ));

      return null;
    }

    System.out.println(String.format(
      "[ INFO ] InMemoryStorage.executeQuery | Query executed successfully '%s'",
      query
    ));

    try {
      stmt.close();
    } catch (SQLException e) {
      System.out.println("[ERROR ] InMemoryStorage.execute | Failed to finalize statement.");
    }

    return results;
  }
}
