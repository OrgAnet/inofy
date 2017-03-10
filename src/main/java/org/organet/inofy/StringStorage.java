package org.organet.inofy;

import java.sql.*;

// TODO Write Storage interface and SharedFileStorage class
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

    System.out.println("[ INFO ] Storage.initialize | Connection created.");

    Statement stmt;
    try {
      connection.setAutoCommit(false);

      stmt = connection.createStatement();
      stmt.execute("CREATE TABLE Files (id INT AUTO_INCREMENT PRIMARY KEY, key VARCHAR(256), path VARCHAR(256), mime VARCHAR(64), hash VARCHAR(256), size INT, keywords VARCHAR(256));");
    } catch (SQLException e) {
      System.out.println("[ERROR ] Storage.initialize | Can not create SQL statement.");
    }
  }

  static boolean exists(String key) {
    Statement stmt;
    try {
      connection.setAutoCommit(false);

      stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery(String.format("SELECT COUNT(*) AS CNT FROM Files WHERE key='%s';", key));

      if (rs.next()) {
        return (rs.getInt("CNT") == 1);
      }
    } catch (SQLException e) {
      System.out.println(String.format(
        "[ERROR ] Storage.exists | Can not create SQL statement where key is '%s'.",
        key
      ));
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
      System.out.println(String.format(
        "[ERROR ] Storage.update | Can not update where key is '%s' and value is '%s'.",
        key, value
      ));
    }
  }

  private static void insert(String key, String value) {
    Statement stmt;

    try {
      connection.setAutoCommit(false);

      stmt = connection.createStatement();
      stmt.execute(String.format("INSERT INTO Files (path, hash) VALUES('%s', '%s');", key, value));
    } catch (SQLException e) {
      System.out.println(String.format(
        "[ERROR ] Storage.insert | Error while inserting where key is '%s'.",
        key
      ));
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
      System.out.println(String.format(
        "[ERROR ] Storage.get | Error while getting where key is '%s'.",
        key
      ));
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
      System.out.println(String.format(
        "[ERROR ] Storage.delete | Error while deleting where key is '%s'.",
        key
      ));
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
