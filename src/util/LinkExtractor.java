package util;

import main.ExtractedLink;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

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

	  Document doc = null;
	  try{
		  doc = Jsoup.connect(url).get();
	  } catch (IOException e){
		  e.printStackTrace();
	  }
	  assert doc != null;
	  String title = doc.title();
	  String content = doc.body().text();
      ExtractedLink extractedLink = new ExtractedLink(url, content, title, null, null);

    return extractedLink;
  }
}
