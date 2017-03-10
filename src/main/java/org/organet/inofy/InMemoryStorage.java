package org.organet.inofy;

import com.sun.istack.internal.Nullable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

// TODO Rename to StringStorage
class InMemoryStringStorage implements Storage<String> {
  private static Connection connection = null;

  private final String DB_DRIVER = "org.h2.Driver";
  private final String DB_CONNECTION = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
  private final String DB_USER = "sa";
  private final String DB_PASSWORD = "";

  private boolean connect() {
    try {
      Class.forName(DB_DRIVER);
    } catch (ClassNotFoundException e) {
      System.out.println("[ERROR ] InMemoryStringStorage.initialize | Storage driver class could not be found.");

      return false;
    }

    try {
      connection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
    } catch (SQLException e) {
      System.out.println("[ERROR ] InMemoryStringStorage.initialize | Could not connect to the database.");

      return false;
    }

    System.out.println("[ INFO ] InMemoryStringStorage.initialize | Database connection established.");

    return true;
  }

  private boolean createTheTable() {
    Statement stmt;

    try {
      connection.setAutoCommit(false);

      stmt = connection.createStatement();
      stmt.execute("CREATE TABLE Files (id INT AUTO_INCREMENT PRIMARY KEY, key VARCHAR(256), path VARCHAR(256), mime VARCHAR(64), hash VARCHAR(256), size INT, keywords VARCHAR(256));");
    } catch (SQLException e) {
      System.out.println("[ERROR ] InMemoryStringStorage.createTheTable | Can not execute SQL statement for table creation.");

      return false;
    }

    return true;
  }

  @Override
  public boolean initialize() {
    return (connect() && createTheTable());
  }

  @Override
  public void insert(@Nullable String id, String value) {
    // TODO Serialize the `value` if not a primitive type
    // TODO Prepare SQL statement
    // TODO Execute the statement
  }

  @Override
  public String get(String id) {
    return null;
  }

  @Override
  public String getBy(String fieldName, String fieldValue) {
    return null;
  }

  @Override
  public void update(String id, String value) {

  }

  @Override
  public String delete(String id) {
    return null;
  }

  @Override
  public boolean exists(String id) {
    return false;
  }

  @Override
  public void close() {

  }
}
