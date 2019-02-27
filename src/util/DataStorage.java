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

//		int tableSize = getTableSize();
//		if (tableSize > 5) {
//			Connection conn = connect();
//			Statement statement = conn.createStatement();
//			ResultSet resultSet = statement.executeQuery("DELETE FROM TwitterFeeder.TF where ID > 5");
//		}
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
					String description = resultSet.getString(5);
					String screenshotURL = resultSet.getString(6);
					ExtractedLink extractedLink = new ExtractedLink(link, body, title, description, screenshotURL);
					extractedLinks.add(extractedLink);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		double endQueryTime = (System.nanoTime() - startQueryTime) / 1000000000;
		Dimension screenshotDimension = new Dimension()
				.withName("Querys Times")
				.withValue("Search Time");

		MetricDatum screenshotDdatum = new MetricDatum()
				.withMetricName("Querys")
				.withUnit(StandardUnit.None)
				.withValue(endQueryTime)
				.withDimensions(screenshotDimension);

		PutMetricDataRequest request = new PutMetricDataRequest()
				.withNamespace("Noy&Ronen")
				.withMetricData(screenshotDdatum);

		PutMetricDataResult response = cw.putMetricData(request);

		return extractedLinks;
	}

	public int getTableSize() {
		int size = 0;
		Statement statement = null;
		try {
			statement = conn.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM TwitterFeeder.TF");
			while (resultSet.next())
				size = resultSet.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return size;
	}

	public static void main(String[] args) {
		DataStorage dataStorage = new DataStorage();
		List<ExtractedLink> extractedLinks = new ArrayList<>();
		extractedLinks = dataStorage.search("SELECT * FROM TwitterFeeder.TF");
		for (ExtractedLink link : extractedLinks) {
			System.out.println(link.getUrl());
		}
		int size = dataStorage.getTableSize();
		System.out.println(size);
	}
}
