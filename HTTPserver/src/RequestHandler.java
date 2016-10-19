import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;


public class RequestHandler implements HttpHandler {

    public static final String OK = "HTTP/1.1 200 OK";
    public static final String BAD_REQUEST = "HTTP/1.1 400 BAD REQUEST";

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        try {
            URI uri = httpExchange.getRequestURI();
            String path = uri.getPath();
            //String query = uri.getQuery();
            String remoteIP = httpExchange.getRemoteAddress().toString();
            remoteIP=remoteIP.substring(1, remoteIP.length());//remote ip
            String method = httpExchange.getRequestMethod();
            String response;
            Map<String, String> parameters = splitQuery(uri);
            int responseCode;

            Logger.write("RECEIVED NEW "+httpExchange.getRequestMethod()+" REQUEST FROM " + remoteIP+" URI: "+uri.toString());

            //kui on tulnud GET päring uriga /download?id=&url=
            if(path.equals("/download") && method.equals("GET")
                    && parameters.containsKey("url") && parameters.containsKey("id")){

                String id = parameters.get("id");
                String url = parameters.get("url");

                responseCode = 200;
                response = OK;

                System.out.println("sain GET päringu");
                System.out.println("\n" + parameters.get("url"));


                if(!P2PNode.downloadRequestsRoutingTable.containsKey(id)) {
                    //salvestan tabelisse uue päringu id ja IP kust päring tuli
                    P2PNode.downloadRequestsRoutingTable.put(id, remoteIP);

                    if(Math.random()>P2PNode.lazyness){ //Otsustan file allalaadida
                        Logger.write("DOWNLOADING URL "+url+" AND SENDING IT BACK TO "+remoteIP);
                        String body = RequestSender.donwloadFile(new URL(url));
                        Logger.write("RETURNING DOWNLOADED FILE WITH /file POST REQUEST "
                                + "/file?id="+id +" TO "+remoteIP);
                        String temp = RequestSender.sendPostRequest(new URL("http://"+remoteIP+"/file?id="+id),body);
                        Logger.write(remoteIP+"RESPONSEs "+temp);
                    } else {
                        //saadab päring edasi kõikidele naabritele va naabri kust päring tuli
                        Logger.write("FORWARDING /download REQUEST TO NEIGHBORS: ");
                        for(String node:P2PNode.neighbors){
                            if(!node.equals(remoteIP)){
                                String nodeResponse = RequestSender.sendGetRequest(new URL("http://"+node+uri));
                                Logger.write(node+" RESPONSES: "+nodeResponse);
                            }
                        }
                    }
                } else {
                    //ignores request
                    Logger.write("IGNORING REQUEST");
                }



                //kui on tulnug POST päring uriga /file?id=
            } else if (path.equals("/file") && method.equals("POST")
                    && parameters.containsKey("id")) {

                String id = parameters.get("id");

                responseCode = 200;
                response = OK;

                InputStream is = httpExchange.getRequestBody();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String body = (br.readLine());

                if(P2PNode.myRequestsIDs.contains(Integer.parseInt(id))){

                    System.out.println(body.substring(0,1024));//kuvab mitte terve keha, vaid ainult keha esimesed 1024 sümbolit
                    Logger.write("RECIEVED REQUESTED FILE");

                } else if(P2PNode.downloadRequestsRoutingTable.containsKey(id) //Kui sama ID-ga asi on juba downlRif entry with same request ID is already in downl routing table
                        && !P2PNode.fileRequestsRoutingTable.containsKey(id)){

                    String destinationIP = P2PNode.downloadRequestsRoutingTable.get(id);
                    P2PNode.fileRequestsRoutingTable.put(id, remoteIP);//salvestab requesti ID ja IP millega see requesti ruutingu tabelisse läks                    Logger.write("SENDING /file REQUEST BACK ");
                    String nodeResponse = RequestSender.sendPostRequest(new URL("http://"+destinationIP+uri),body);
                    Logger.write(destinationIP + " RESPONSES: "+nodeResponse);

                } else if (!P2PNode.fileRequestsRoutingTable.containsKey(id) ////request ID is new, no entries in downlReqRoutingTable nor fileReqRoutingTable
                        && !P2PNode.downloadRequestsRoutingTable.containsKey(id)) {
                    //saving new entry in fileRequRoutingTable and sending to neighbors
                    P2PNode.fileRequestsRoutingTable.put(id, remoteIP);

                    for(String node:P2PNode.neighbors){
                        if(!node.equals(remoteIP)){
                            String nodeResponse = RequestSender.sendPostRequest(new URL("http://"+node+uri),body);
                            Logger.write(node+" RESPONSES: "+nodeResponse);
                        }
                    }

                } else {
                    //ignores request
                    Logger.write("IGNORING REQUEST");
                }


                is.close();
                br.close();

            } else {
                responseCode = 400;
                response = BAD_REQUEST;
            }

            httpExchange.sendResponseHeaders(responseCode, response.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes());
            os.flush();
            os.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Splits query and returnd linked hashmap with key and values in JSON
    public  Map<String, String> splitQuery(URI uri) throws UnsupportedEncodingException {
        if(uri.getPath().equals("/download") || uri.getPath().equals("/file")) {
            Map<String, String> query_pairs = new LinkedHashMap<String, String>();
            String query = uri.getQuery();
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
            }
            return query_pairs;
        } else return null;
    }

}
