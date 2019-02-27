package util;

import main.ExtractedLink;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;
import com.amazonaws.services.cloudwatch.model.PutMetricDataResult;
import com.amazonaws.services.cloudwatch.model.StandardUnit;

/**
 * Extract content from links
 */
public class LinkExtractor {
  public ExtractedLink extractContent(String url) {
    /*
    Use JSoup to extract the text, title and description from the URL.

    Extract the page's content, without the HTML tags.
    Extract the title from title tag or meta tags, prefer the meta title tags.
    Extract the description the same as you would the title.

    For title and description tags, if there are multiple (which is usually the case)
    take the first.
     */

	  // Create Amazon CloudWatch
	  final AmazonCloudWatch cw = AmazonCloudWatchClientBuilder.defaultClient();

	  double startScanTime = System.nanoTime();

	  Document doc = null;
	  try{
		  doc = Jsoup.connect(url).get();
	  } catch (IOException e){
		  e.printStackTrace();
	  }
	  assert doc != null;
	  String title = doc.title();
	  String content = doc.body().text();
	  String description = "No Description";
	  try {
		   description = doc.select("meta[name=description]").get(0)
				  .attr("content");
	  } catch (Exception e){
	  }
	  double endScanTime = (System.nanoTime() - startScanTime) / 1000000000;

	  Dimension scanningDimension = new Dimension()
			  .withName("ExtractTime")
			  .withValue("Scanning Time");

	  MetricDatum scanningDatum = new MetricDatum()
			  .withMetricName("Site Scanning")
			  .withUnit(StandardUnit.None)
			  .withValue(endScanTime)
			  .withDimensions(scanningDimension);

	  PutMetricDataRequest scanningRequest = new PutMetricDataRequest()
			  .withNamespace("Noy&Ronen")
			  .withMetricData(scanningDatum);

	  PutMetricDataResult scanningResponse = cw.putMetricData(scanningRequest);

	  // Take screenshot
	  double startScreenshotTime = System.nanoTime();
	  String screenshotUrl = ScreenshotGenerator.takeScreenshot(url);
	  double endScreenshotTime = (System.nanoTime() - startScreenshotTime) / 1000000000;

	  Dimension screenshotDimension = new Dimension()
			  .withName("ExtractTime")
			  .withValue("Screenshot Time");

	  MetricDatum screenshotDdatum = new MetricDatum()
			  .withMetricName("Screenshot")
			  .withUnit(StandardUnit.None)
			  .withValue(endScreenshotTime)
			  .withDimensions(screenshotDimension);

	  PutMetricDataRequest request = new PutMetricDataRequest()
			  .withNamespace("Noy&Ronen")
			  .withMetricData(screenshotDdatum);

	  PutMetricDataResult response = cw.putMetricData(request);
      ExtractedLink extractedLink = new ExtractedLink(url, content, title, description, screenshotUrl);

    return extractedLink;
  }
}
