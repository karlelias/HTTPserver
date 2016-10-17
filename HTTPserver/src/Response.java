import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

import com.sun.net.httpserver.HttpExchange;

public class Response {


    public static void makeOKResponse(HttpExchange httpExchange) throws IOException{
        String response = "OK";
        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    public static void makeBadRequestResponse(HttpExchange httpExchange) throws IOException {
        String response = "BAD REQUEST";
        httpExchange.sendResponseHeaders(400, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }



}