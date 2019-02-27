package main;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.*;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterListener {
	public static void main(String[] args) {
		// Create our twitter configuration
		final ConfigurationBuilder cb = new ConfigurationBuilder();

		// Create AmazonSQS
		final AmazonSQS client = AmazonSQSAsyncClientBuilder.defaultClient();

		// Create Amazon CloudWatch
		final AmazonCloudWatch cw = AmazonCloudWatchClientBuilder.defaultClient();

		cb.setDebugEnabled(true)
				.setOAuthConsumerKey(System.getProperty("config.twitter.consumer.key"))
				.setOAuthConsumerSecret(System.getProperty("config.twitter.consumer.secret"))
				.setOAuthAccessToken(System.getProperty("config.twitter.access.token"))
				.setOAuthAccessTokenSecret(System.getProperty("config.twitter.access.secret"));

		// Create our Twitter stream
		TwitterStreamFactory tf = new TwitterStreamFactory(cb.build());
		final TwitterStream twitterStream = tf.getInstance();

    /*
      This is where we should start fetching the tweets using the Streaming API
      See Example 9 on this page: http://twitter4j.org/en/code-examples.html#streaming
    */

		final StatusListener listener = new StatusListener() {
			public void onStatus(Status status) {
				if (status.getLang().equals("en")) {
					URLEntity[] urlEntities = status.getURLEntities();
					if (urlEntities.length > 0) {
						for (URLEntity entity : urlEntities) {
							String link = entity.getExpandedURL();
							// Sending the links to SQS
							String track = System.getProperty("config.twitter.track");
							JSONObject message = new JSONObject();
							try {
								message.put("link", link);
								message.put("track", track);
							} catch (JSONException e) {
							}

							System.out.println(message.toString());
							client.sendMessage(System.getProperty("config.sqs.url"), message.toString());

							Dimension dimension = new Dimension()
									.withName("LinksCount")
									.withValue(track);

							MetricDatum datum = new MetricDatum()
									.withMetricName("TwitterListener")
									.withUnit(StandardUnit.None)
									.withValue(1.0)
									.withDimensions(dimension);

							PutMetricDataRequest request = new PutMetricDataRequest()
									.withNamespace("Noy&Ronen")
									.withMetricData(datum);

							PutMetricDataResult response = cw.putMetricData(request);
						}
					}
				}
			}

			public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
			}


			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
			}


			public void onScrubGeo(long l, long l1) {
			}


			public void onStallWarning(StallWarning stallWarning) {
			}


			public void onException(Exception ex) {
				ex.printStackTrace();
			}
		};

		twitterStream.addListener(listener);
		FilterQuery tweetFilterQuery = new FilterQuery(); // See
		tweetFilterQuery.track(System.getProperty("config.twitter.track")); // OR on keywords
		tweetFilterQuery.language("en"); // Note that language does not work properly on Norwegian tweets
		twitterStream.filter(tweetFilterQuery);


	}
}
