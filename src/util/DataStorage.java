package util;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.*;
import main.ExtractedLink;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstraction layer for database access
 */
public class DataStorage {
	Connection conn;

	// Create Amazon CloudWatch
	final AmazonCloudWatch cw = AmazonCloudWatchClientBuilder.defaultClient();

	public DataStorage() {
		conn = connect();
		createNewTable();
	}

	/**
	 * Connect to a sample database
	 */
	public Connection connect() {
		try {
			// AWS RDS parameters
			String url = "jdbc:mysql://" + System.getProperty("config.rds.url");
			String username = System.getProperty("config.username");
			String password = System.getProperty("config.password");

			try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			// create a connection to the database
			conn = DriverManager.getConnection(url, username, password);
			System.out.println("Connection to RDS has been established.");
			return conn;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void createNewTable() {
		Statement setupStatement = null;
		try {
			setupStatement = conn.createStatement();

			// SQL statement for creating a new table
			String sql = "CREATE TABLE IF NOT EXISTS TF (\n"
					+ "	ID integer NOT NULL AUTO_INCREMENT PRIMARY KEY,\n"
					+ "	Link text,\n"
					+ "	Title text,\n"
					+ "	Body text,\n"
					+ " Description text,\n"
					+ " ScreenshotUrl text,\n"
					+ " Track text,\n"
					+ " Timestamp text\n"
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
		String sql = "INSERT INTO TF(Link,Title,Body,Description,ScreenshotUrl,Track,Timestamp) VALUES(?,?,?,?,?,?,?)";
		Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

		int tableSize = getTableSize();
		if (tableSize >= 1000) {
			Statement statement = conn.createStatement();
			int delete = statement.executeUpdate("DELETE FROM TwitterFeeder.TF LIMIT 1");
		}
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, link.getUrl());
			pstmt.setString(2, link.getTitle());
			pstmt.setString(3, link.getContent());
			pstmt.setString(4, link.getDescription());
			pstmt.setString(5, link.getScreenshotURL());
			pstmt.setString(6, track);
			pstmt.setString(7, String.valueOf(currentTimestamp));

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
		List<ExtractedLink> extractedLinks = new ArrayList<>();

		double startQueryTime = System.nanoTime();
		try {
			Statement statement = conn.createStatement();
			if (query.equals("")) {
				ResultSet resultSet = statement.executeQuery("SELECT * FROM TwitterFeeder.TF");
				while (resultSet.next()) {
					String link = resultSet.getString(2);
					String title = resultSet.getString(3);
					String body = resultSet.getString(4);
					String description = resultSet.getString(5);
					String screenshotURL = resultSet.getString(6);
					ExtractedLink extractedLink = new ExtractedLink(link, body, title, description, screenshotURL);
					extractedLinks.add(extractedLink);
				}
			} else {
				ResultSet resultSet = statement.executeQuery(query);
				while (resultSet.next()) {
					String link = resultSet.getString(2);
					String title = resultSet.getString(3);
					String body = resultSet.getString(4);
					String shortBody = body.substring(0, 99);
					String description = resultSet.getString(5);
					String screenshotURL = resultSet.getString(6);
					ExtractedLink extractedLink = new ExtractedLink(link, shortBody, title, description, screenshotURL);
					extractedLinks.add(extractedLink);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		double endQueryTime = (System.nanoTime() - startQueryTime) / 1000000000;
		Dimension screenshotDimension = new Dimension()
				.withName("Queries Times")
				.withValue("Search Time");

		MetricDatum screenshotDatum = new MetricDatum()
				.withMetricName("Queries")
				.withUnit(StandardUnit.None)
				.withValue(endQueryTime)
				.withDimensions(screenshotDimension);

		PutMetricDataRequest request = new PutMetricDataRequest()
				.withNamespace("Noy&Ronen")
				.withMetricData(screenshotDatum);

		PutMetricDataResult response = cw.putMetricData(request);

		return extractedLinks;
	}

	public int getTableSize() {
		int size = 0;
		Statement statement = null;
		try {
			statement = conn.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM TwitterFeeder.TF");
			if(resultSet.next()){
				size = resultSet.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return size;
	}

	public static void main(String[] args) {
		DataStorage dataStorage = new DataStorage();
//		List<ExtractedLink> extractedLinks = new ArrayList<>();
//		extractedLinks = dataStorage.search("SELECT * FROM TwitterFeeder.TF");
//		for (ExtractedLink link : extractedLinks) {
//			System.out.println(link.getUrl());
//		}
		int size = dataStorage.getTableSize();
		System.out.println(size);
	}
}
