package main;

import com.amazonaws.regions.Region;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.IOException;

public class TwitterListener {
  public static void main(String[] args)  {
    // Create our twitter configuration
    final ConfigurationBuilder cb = new ConfigurationBuilder();

    // Create AmazonSQS
    final AmazonSQS client = AmazonSQSClientBuilder.defaultClient();

    cb.setDebugEnabled(true)
        .setOAuthConsumerKey("LFAkkd57cv7VqF7UJuTyAQ7Ry")
        .setOAuthConsumerSecret("eQnYQZeQZLwbRZtL63UHZN5QmSW8vOobFpqqP0ikGxByg3UHcd")
        .setOAuthAccessToken("1060192009867198465-a1t1RHXd2uDbBXTC45YavDs1YRHyF6")
        .setOAuthAccessTokenSecret("mO1GDUrVhhiEAEazm6FvbPsuBs9BOy3lpJWYLGu8vDBdg");

    // Create our Twitter stream
    TwitterStreamFactory tf = new TwitterStreamFactory(cb.build());
    final TwitterStream twitterStream = tf.getInstance();

    /*
      This is where we should start fetching the tweets using the Streaming API
      See Example 9 on this page: http://twitter4j.org/en/code-examples.html#streaming
    */

      final StatusListener listener = new StatusListener(){
          public void onStatus(Status status){
              if (status.getLang().equals("en")){
                  URLEntity[] urlEntities = status.getURLEntities();
                  if (urlEntities.length > 0){
                      for (URLEntity entity : urlEntities){
                          String link = entity.getExpandedURL();
                          // Sending the links to SQS
	                      client.sendMessage("https://sqs.us-east-1.amazonaws.com/135062767808/NoyIshai", link);

//                          String screenshot = Screenshot.take(link);
//                          IA.insert(status.getId(), status.getCreatedAt().toString(), link, title, body, screenshot);
//                          System.out.println("TweetID: " +  status.getId() + " CreatedAt:  " + status.getCreatedAt().toString() + System.lineSeparator() + "Link: " + link  +  System.lineSeparator() + "Title: " + title + System.lineSeparator() + "Body: " + body);
//                           System.out.println("Link: " + link);
                      }
                  }
              }
          }

          public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice){}



          public void onTrackLimitationNotice(int numberOfLimitedStatuses){}



          public void onScrubGeo(long l, long l1){}



          public void onStallWarning(StallWarning stallWarning){}



          public void onException(Exception ex){
              ex.printStackTrace();
          }
      };
	  twitterStream.addListener(listener);
	  FilterQuery tweetFilterQuery = new FilterQuery(); // See
	  tweetFilterQuery.track("USA"); // OR on keywords
	  tweetFilterQuery.language("en"); // Note that language does not work properly on Norwegian tweets
	  twitterStream.filter(tweetFilterQuery);



  }
}
