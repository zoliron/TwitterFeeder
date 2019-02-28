package util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Random;
import java.util.UUID;

public class ScreenshotGenerator {
	public static String takeScreenshot(String url) {
		String screenshotFilePath = null;

		// Creates ID
//		UUID uuid = UUID.randomUUID();
		Random screenshotID = new Random();

		// Configure our client
		AmazonS3 client = AmazonS3ClientBuilder.defaultClient();

		//Run our screenshot generator program
//		screenshotFilePath = uuid.toString() + ".png";
		screenshotFilePath = "Screenshot" + screenshotID.nextInt() + ".png";

		String[] cmdWindows = {"node", "screenshot.js", url, screenshotFilePath};
		String[] cmdUbunto = {"/bin/bash", "-c", "xvfb-run --server-args=\"-screen 0 1024x768x24\" node screenshot.js " + url + " " + screenshotFilePath};
		String[] cmdUbuntoNew = {"/bin/bash", "-c", "xvfb-run --server-args=\"-screen 0 1024x768x24\" wkhtmltoimage --format png --crop-w 1024 --crop-h 768 --quiet --quality 60 " + url + " " + screenshotFilePath};

//		try {
//			Process process = Runtime.getRuntime().exec(cmdWindows);
//			process.waitFor();
//
//			// Upload a file to AWS S3
//			client.putObject("noyishai-bucket", screenshotFilePath, new File(screenshotFilePath));
//
//			// Get the object URL
//			URL s3Url = client.getUrl("noyishai-bucket", screenshotFilePath);
//			return s3Url.toString();
//		} catch (IOException | InterruptedException e) {
//			e.printStackTrace();
//		}

		try {
			Process process = Runtime.getRuntime().exec(cmdUbuntoNew);
			process.waitFor();

			// Upload a file to AWS S3
			try {
				client.putObject("noyishai-bucket", screenshotFilePath, new File(screenshotFilePath));
			} finally {}
			// Get the object URL
			try {
				URL s3Url = client.getUrl("noyishai-bucket", screenshotFilePath);
				return s3Url.toString();
			} finally {}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return "No Screenshot";
	}

	public static void main(String[] args) {
		ScreenshotGenerator screenshotGenerator = new ScreenshotGenerator();
		String filelocation = takeScreenshot("http://google.com");
		System.out.println(filelocation);
	}
}


