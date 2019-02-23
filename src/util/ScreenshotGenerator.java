package util;

import java.io.IOException;
import java.util.UUID;

public class ScreenshotGenerator {
  public static String takeScreenshot(String url) {
    String screenshotFilePath = null;

    //Run our screenshot generator program
	  UUID uuid = UUID.randomUUID();
	  screenshotFilePath = "screenshot\\" + uuid.toString() + ".png";
	  String[] cmd = { "node", "F:\\Workspace\\TwitterNew\\screenshots\\screenshot.js", url, screenshotFilePath };
	  try{
		  Process process = Runtime.getRuntime().exec(cmd);
		  process.waitFor();
		  return screenshotFilePath;
	  } catch (IOException e){
		  e.printStackTrace();
	  } catch (InterruptedException e){
		  e.printStackTrace();
	  }
	  return null;
  }
	public static void main(String[] args){
		ScreenshotGenerator screenshotGenerator = new ScreenshotGenerator();
		String filelocation = takeScreenshot("www.google.com");
		System.out.println(filelocation);
	}
}


