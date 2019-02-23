package util;

import main.ExtractedLink;

import javax.xml.crypto.Data;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

/**
 * Abstraction layer for database access
 */
public class DataStorage {
	private static final Logger logger = Logger.getLogger(DataStorage.class.getName());
    Connection conn;

	public DataStorage() throws SQLException {
    this("twitterlinks.db");
  }

  public DataStorage(String database) throws SQLException {
    String url = "jdbc:sqlserver:" + database;
    conn = DriverManager.getConnection(url);
  }

  private static Connection getRemoteConnection() {
      try {
        Class.forName("org.sqlserver.Driver");
        String dbName = "noyishai-db-instance";
        String userName = "noyishai1@gmail.com";
        String password = "abc552346";
        String hostname = "noyishai-db-instance.cwn8zwzjkufz.us-east-1.rds.amazonaws.com";
        String port = "3306";
        String jdbcUrl = "jdbc:sqlserver://" + hostname + ":" + port + "/" + dbName + "?user=" + userName + "&password=" + password;
        //logger.trace("Getting remote connection with connection string from environment variables.");
        Connection con = DriverManager.getConnection(jdbcUrl);
        logger.info("Remote connection successful.");
        return con;
      }
      catch (ClassNotFoundException e) { /*logger.warn(e.toString());*/}
      catch (SQLException e) { /*logger.warn(e.toString());*/}

      return null;
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
