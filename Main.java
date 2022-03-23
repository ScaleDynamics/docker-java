import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Main {
  static final int PORT = 8080;
  public static void main(String[] args) throws Exception {

    HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
    server.createContext("/", new MyHandler());
    server.setExecutor(null);
    server.start();
    System.out.println("Server started on port " + PORT);
  }

  static class MyHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
      Map<String, String> params = Main.parseQueryString(exchange.getRequestURI().getQuery());
      String version = System.getProperty("java.version");
      String name = params.get("name") != null ? params.get("name") : "unknown";

      String response = "Hello " + name + " from Java " + version;

      exchange.sendResponseHeaders(200, response.length());
      OutputStream os = exchange.getResponseBody();
      os.write(response.getBytes());

      System.out.println("Response: " + response + " sent to " + exchange.getRemoteAddress());

      os.close();
    }
  }

  public static Map<String, String> parseQueryString(String qs) {
    Map<String, String> result = new HashMap<>();
    if (qs == null)
      return result;

    int last = 0, next, l = qs.length();
    while (last < l) {
        next = qs.indexOf('&', last);
        if (next == -1)
            next = l;

        if (next > last) {
            int eqPos = qs.indexOf('=', last);
            try {
                if (eqPos < 0 || eqPos > next)
                    result.put(URLDecoder.decode(qs.substring(last, next), "utf-8"), "");
                else
                    result.put(URLDecoder.decode(qs.substring(last, eqPos), "utf-8"), URLDecoder.decode(qs.substring(eqPos + 1, next), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        last = next + 1;
    }
    return result;
  }
}
