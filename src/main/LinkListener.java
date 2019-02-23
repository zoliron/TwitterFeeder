package main;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import util.DataStorage;
import util.LinkExtractor;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

public class LinkListener {
  public static void main(String[] args) throws SQLException {
    // Connect to the database
    DataStorage dataStorage = new DataStorage();

    // Initiate our link extractor
    LinkExtractor linkExtractor = new LinkExtractor();

    // Listen to SQS for arriving links

    // Configure our client
    AmazonSQS client = AmazonSQSClientBuilder.defaultClient();
// Send message to a Queue
    client.sendMessage("https://sqs.us-east-1.amazonaws.com/135062767808/NoyIshai", "Our Message");

    // Extract the link content
    // ...

    // Take screenshot
//	  static String take(String url){
//      UUID uuid = UUID.randomUUID();
//      String filename = "screenshots\\" + uuid.toString() + ".png";
//      String[] cmd = { "node", "screenshot\\screenshot.js", url, filename };
//      try{
//        Process process = Runtime.getRuntime().exec(cmd);
//        process.waitFor();
//
////        return filename;
//      } catch (IOException e){
//        e.printStackTrace();
//      } catch (InterruptedException e){
//        e.printStackTrace();
//      }
//
//      return null;
//    }

    // Save everything in the database
  }
}
