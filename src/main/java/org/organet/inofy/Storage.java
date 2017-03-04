package org.organet.inofy;

import java.sql.*;

class Storage {
  private static Connection connection;

  private static final String DB_DRIVER = "org.h2.Driver";
  private static final String DB_CONNECTION = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
  private static final String DB_USER = "sa";
  private static final String DB_PASSWORD = "";

  static void initialize() {
    Storage.connection = null;

    try {
      Class.forName(DB_DRIVER);
    } catch (ClassNotFoundException e) {
      System.out.println(e.getMessage());
    }

    try {
      Storage.connection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

    System.out.println("Connection created.");

    Statement stmt;
    try {
      connection.setAutoCommit(false);

      stmt = connection.createStatement();
      stmt.execute("CREATE TABLE Files (id INT AUTO_INCREMENT PRIMARY KEY, path VARCHAR(256), hash VARCHAR(256));");
    } catch (SQLException e) {
      System.out.println("Can not create statement for 'initialize'.");
    }
  }

  static boolean exists(String key) {
    Statement stmt;
    try {
      connection.setAutoCommit(false);

      stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery(String.format("SELECT COUNT(*) AS CNT FROM Files WHERE path='%s';", key));

      if (rs.next()) {
        return (rs.getInt("CNT") == 1);
      }
    } catch (SQLException e) {
      System.out.println("Can not create statement for 'exists'.");
    }

    return false;
  }

  private static void update(String key, String value) {
    Statement stmt;

    try {
      connection.setAutoCommit(false);

      stmt = connection.createStatement();
      stmt.execute(String.format("UPDATE Files SET path='%s', hash='%s' WHERE path='%s';", key, value, key));
    } catch (SQLException e) {
      System.out.println("Error while updating.");
    }
  }

  private static void insert(String key, String value) {
    Statement stmt;

    try {
      connection.setAutoCommit(false);

      stmt = connection.createStatement();
      stmt.execute(String.format("INSERT INTO Files (path, hash) VALUES('%s', '%s');", key, value));
    } catch (SQLException e) {
      System.out.println("Error while inserting.");
    }
  }

  static void upsert(String key, String value) {
    if (exists(key)) {
      update(key, value);
    } else {
      insert(key, value);
    }
  }

  static String get(String key) {
    Statement stmt;

    try {
      connection.setAutoCommit(false);

      stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery(String.format("SELECT hash FROM Files WHERE path='%s';", key));

      if (rs.next()) {
        return rs.getString("hash");
      }
    } catch (SQLException e) {
      System.out.println("Error while getting.");
    }

    return null;
  }

  static void delete(String key) {
    Statement stmt;

    try {
      connection.setAutoCommit(false);

      stmt = connection.createStatement();
      stmt.execute(String.format("DELETE FROM Files WHERE path='%s';", key));
    } catch (SQLException e) {
      System.out.println("Error while deleting.");
    }
  }

  static String crud(String key) {
    if (key == null) {
      return null;
    }

    return get(key);
  }

  static String crud(String key, String value) {
    if (key == null) {
      return null;
    }

    if (value == null) {
      delete(key);
    } else {
      upsert(key, value);
    }

    return null;
  }
}
