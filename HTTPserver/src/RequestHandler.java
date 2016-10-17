import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class RequestHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        URI uri = httpExchange.getRequestURI();
        String remoteIP = httpExchange.getRemoteAddress().toString();//remoete ip
        String localIP = httpExchange.getLocalAddress().toString(); //my server IP
        String path = uri.getPath();

        Map<String, String> parameters = splitQuery(uri); // query map formaadis

        System.out.println("\nRemoteAddress: " + remoteIP);
        System.out.println("LocalAddress: " + localIP);
        System.out.println("path: "+ path);
        System.out.println("Parameters: " + parameters);

        if(path.equals("/download")) {
            Response.makeOKResponse(httpExchange);
            //otsustan kas downloadin file ise või saadan päring edasi naabritele
            System.out.println("saadan p2ring edasi");
        } else if (path.equals("/file")) {
            Response.makeOKResponse(httpExchange);
            //kontrollin kas on id minu oma, kui mitte, siis saadan tagasi sellele kus tuli psama idga päring
            System.out.println("saadan edasi");

        } else {
            Response.makeBadRequestResponse(httpExchange);
        }

    }

    //Splits query and returnd linked hashmap with key and values in JSON
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
}