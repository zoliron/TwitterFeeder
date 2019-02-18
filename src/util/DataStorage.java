package util;

import il.ac.colman.cs.ExtractedLink;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Abstraction layer for database access
 */
public class DataStorage {
  Connection conn;

  public DataStorage() throws SQLException {
    this("twitterlinks.db");
  }

  DataStorage(String database) throws SQLException {
    String url = "jdbc:sqlite:" + database;
    conn = DriverManager.getConnection(url);
  }

  /**
   * Add link to the database
   */
  public void addLink(ExtractedLink link, String track) {
    /*
    This is where we'll add our link
     */
  }

  /**
   * Search for a link
   * @param query The query to search
   */
  public List<ExtractedLink> search(String query) {
    /*
    Search for query in the database and return the results
     */

    return null;
  }
}
