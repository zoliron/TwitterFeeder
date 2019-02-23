package main;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import util.DataStorage;
import util.LinkExtractor;
import util.ScreenshotGenerator;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class LinkListener {
  public static void main(String[] args) throws SQLException, InterruptedException {
    // Connect to the database
//    DataStorage dataStorage = new DataStorage("noyishai-db-instance.cwn8zwzjkufz.us-east-1.rds.amazonaws.com");

    // Initiate our link extractor
    LinkExtractor linkExtractor = new LinkExtractor();

    // Listen to SQS for arriving links
    // Configure our client
    AmazonSQS client = AmazonSQSClientBuilder.defaultClient();
    // Extract the link content
    while(true) {
	    ReceiveMessageResult result = client.receiveMessage("https://sqs.us-east-1.amazonaws.com/135062767808/NoyIshai");

	    List<Message> messages = result.getMessages();
	    if (messages.size() == 0){
	    	Thread.sleep(5000);
	    } else {
	    	for (Message message : messages){
	    		String url = message.getBody();
	    		// Extracting the Url content
				ExtractedLink extractedLink = linkExtractor.extractContent(url);
			    System.out.println("URL: " + extractedLink.getUrl());
			    System.out.println("Title: " + extractedLink.getTitle());
			    System.out.println("Content: " + extractedLink.getContent());
			    // Take screenshot
			    String fileLocation = ScreenshotGenerator.takeScreenshot(url);
			    System.out.println("Screenshot location: " + fileLocation);
		    }
	    }
    }

    // Save everything in the database
  }
}
