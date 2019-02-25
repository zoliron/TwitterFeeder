package util;

import main.ExtractedLink;

import java.sql.*;
import java.util.List;

/**
 * Abstraction layer for database access
 */
public class DataStorage {
	Connection conn;

	public DataStorage() {
		createNewTable();
	}

	/**
	 * Connect to a sample database
	 */
	public static Connection connect() {
		Connection conn = null;
		try {
			// AWS RDS parameters
			String url = "jdbc:mysql://noyishai-db-instance.cwn8zwzjkufz.us-east-1.rds.amazonaws.com/TwitterFeeder";
			String username = "noyIshai";
			String password = "abc552346";

			// create a connection to the database
			conn = DriverManager.getConnection(url, username, password);
			System.out.println("Connection to RDS has been established.");
			return conn;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

		public static void createNewTable() {
		Connection conn = null;
		Statement setupStatement = null;
		try {
//			// AWS RDS parameters
//			String url = "jdbc:mysql://noyishai-db-instance.cwn8zwzjkufz.us-east-1.rds.amazonaws.com/TwitterFeeder";
//			String username = "noyIshai";
//			String password = "abc552346";
//
//			// create a connection to the database
//			conn = DriverManager.getConnection(url, username, password);
			conn = connect();
			setupStatement = conn.createStatement();

			// SQL statement for creating a new table
			String sql = "CREATE TABLE IF NOT EXISTS TF (\n"
//					+ "	ID integer PRIMARY KEY,\n"
					+ "	Link text,\n"
					+ "	Title text,\n"
					+ "	Body text,\n"
					+ " Description text,\n"
					+ " ScreenshotUrl text,\n"
					+ " Track text\n"
					+ ");";

			setupStatement.addBatch(sql);
			setupStatement.executeBatch();
			setupStatement.close();
		} catch (SQLException e) {
			System.out.println("Error Creating New Table: ");
			e.printStackTrace();
		}
	}

	/**
	 * Add link to the database
	 */
	public void addLink(ExtractedLink link, String track) throws SQLException {
    /*
    This is where we'll add our link
     */
		String sql = "INSERT INTO TF(Link,Title,Body,Description,ScreenshotUrl,Track) VALUES(?,?,?,?,?,?)";

		try (Connection conn = connect();
		     PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, link.getUrl());
			pstmt.setString(2, link.getTitle());
			pstmt.setString(3, link.getContent());
			pstmt.setString(4, link.getDescription());
			pstmt.setString(5, link.getScreenshotURL());
			pstmt.setString(6, track);

			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Search for a link
	 *
	 * @param query The query to search
	 */
	public List<ExtractedLink> search(String query) {
    /*
    Search for query in the database and return the results
     */

		return null;
	}

	public static void main(String[] args) {
		connect();
		createNewTable();
	}
}
