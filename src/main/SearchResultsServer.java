package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import util.DataStorage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class SearchResultsServer extends AbstractHandler {
  public static void main(String[] args) throws Exception {
    // Connect to the database
    DataStorage dataStorage = new DataStorage();

    // Start the http server on port 8080
    Server server = new Server(8080);

    server.setHandler(new SearchResultsServer());

    server.start();
    server.join();
  }

  private DataStorage storage;

  SearchResultsServer() throws SQLException {
    storage = new DataStorage();
  }

  public void handle(String s, Request request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
    // Set the content type to JSON
    httpServletResponse.setContentType("application/json;charset=UTF-8");

    // Set the status to 200 OK
    httpServletResponse.setStatus(HttpServletResponse.SC_OK);

    // Build data from request
//    List<ExtractedLink> results = storage.search(httpServletRequest.getParameter("query"));

    List<ExtractedLink> results = null;
    try {
      if (s.equals("/results")) {
        // Record the start time
        long startTime = System.nanoTime();
        results = storage.search(httpServletRequest.getParameter("query"));
        // Record end time
        long endTime = (System.nanoTime() - startTime) / 1000000;
      }
    }
    catch (Exception e){
//            System.out.println("EXCEPTION");
      e.printStackTrace();
    }

    // Notify that this request was handled
    request.setHandled(true);

    // Convert data to JSON string and write to output
    ObjectMapper mapper = new ObjectMapper();
    mapper.writeValue(httpServletResponse.getWriter(), results);
  }
}
