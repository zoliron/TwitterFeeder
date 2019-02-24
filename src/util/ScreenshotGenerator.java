package util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

public class ScreenshotGenerator {
  public static String takeScreenshot(String url) {
    String screenshotFilePath = null;

      // Creates ID
	  UUID uuid = UUID.randomUUID();

	  // Configure our client
	  AmazonS3 client = AmazonS3ClientBuilder.defaultClient();

    //Run our screenshot generator program
	  screenshotFilePath = "screenshots\\" + uuid.toString() + ".png";
	  String[] cmd = { "node", "screenshots\\screenshot.js", url, screenshotFilePath };
	  try{
		  Process process = Runtime.getRuntime().exec(cmd);
		  process.waitFor();

		  // Upload a file to AWS S3
		  client.putObject( "noyishai-bucket" , screenshotFilePath , new File(screenshotFilePath));

		  // Get the object URL
		  URL s3Url = client.getUrl( "noyishai-bucket" , screenshotFilePath);
		  return s3Url.toString();
	  } catch (IOException e){
		  e.printStackTrace();
	  } catch (InterruptedException e){
		  e.printStackTrace();
	  }
	  return null;
  }
	public static void main(String[] args){
		ScreenshotGenerator screenshotGenerator = new ScreenshotGenerator();
		String filelocation = takeScreenshot("http://google.com");
		System.out.println(filelocation);
	}
}


