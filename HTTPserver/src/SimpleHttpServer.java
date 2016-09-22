import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;


public class SimpleHttpServer {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0",1215), 0);
        server.createContext("/", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("jee");
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            URI uri = t.getRequestURI();
            String uriPath = uri.getPath();
            String method = t.getRequestMethod();
            String remoteAddress= t.getRemoteAddress().toString();
            String localAddress = t.getLocalAddress().toString();
            String protocol = t.getProtocol();
            String query = uri.getQuery();
            Map parameters = splitQuery(uri);


            String response = "OK";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();

            System.out.println("\nMethod: " + method);
            System.out.println("protocol: " + protocol);
            System.out.println("uriPath: " + uriPath);
            System.out.println("RemoteAddress: " + remoteAddress);
            System.out.println("LocalAddress: " + localAddress);
            System.out.println("query: "+ query);
            System.out.println("Parameters: " + parameters);


        }
    }
    public static Map<String, String> splitQuery(URI uri) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String query = uri.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }

    //mis toimub siin ma ei tea
    //http://localhost:8000/test/request?param1=val1&param2=val2

}