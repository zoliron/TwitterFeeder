package main;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import org.json.JSONObject;
import util.DataStorage;
import util.LinkExtractor;

import java.sql.SQLException;
import java.util.List;

public class LinkListener {
	public static void main(String[] args) throws SQLException, InterruptedException {
		// Connect to the database
		DataStorage dataStorage = new DataStorage();

		// Initiate our link extractor
		LinkExtractor linkExtractor = new LinkExtractor();

		// Listen to SQS for arriving links
		// Configure our client
		AmazonSQS client = AmazonSQSAsyncClientBuilder.defaultClient();

		// Extract the link content
		while (true) {
			ReceiveMessageResult result = client.receiveMessage(System.getProperty("config.sqs.url"));

			List<Message> messages = result.getMessages();
			if (messages.size() == 0) {
				Thread.sleep(5000);
			} else {
				for (Message message : messages) {
					String messageBody = message.getBody();
					JSONObject messageBodyJSON = new JSONObject(messageBody);
					String url = messageBodyJSON.get("link").toString();
					String track = messageBodyJSON.get("track").toString();
					// Extracting the Url content
					ExtractedLink extractedLink = linkExtractor.extractContent(url);
					dataStorage.addLink(extractedLink, track);
//			    System.out.println("URL: " + extractedLink.getUrl());
//			    System.out.println("Title: " + extractedLink.getTitle());
//			    System.out.println("Content: " + extractedLink.getContent());
//			    System.out.println("Description: " + extractedLink.getDescription());
//			    System.out.println("Screenshot URL: " + extractedLink.getScreenshotURL());
				}
			}
		}
	}
}
