import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.util.Base64;

import javax.net.ssl.HttpsURLConnection;

public class RequestSender {

    //teeb uus downl päring ja salvestab päringu id
    public static void sendNewDownloadRequest(String url) throws Exception{
        int myRequestId = IdGenerator();
        P2PNode.myRequestsIDs.add(myRequestId);
        Logger.write("NEW DOWNLOAD REQUEST FOR "+url);
        String pathQuery = "/download?"+"id="+myRequestId+"&url="+encodeURL(url);

        for(String node:P2PNode.neighbors){
            Logger.write("SENDING NEW /download REQUEST " +pathQuery+" TO "+node);
            String response = sendGetRequest(new URL("http://"+node+pathQuery));
            Logger.write(node+" responses: "+response);
        }
    }

    //saadab getPeers päring
    public static String[] getPeers() throws Exception{
        URL url = new URL("http://192.168.3.11:1215/getpeers");
        String peers = sendGetRequest(url);
        String[] peersArray =  peers.replaceAll("\\s+","").replace("\"", "").replace("]", "").replace("[","").split(",");;
        return peersArray;
    }

    //tagastab Base64 kodeeritud sisu
    public static String donwloadFile(URL url) throws Exception{
        String response = sendGetRequest(url);

        if(response.equals("Error")) {
            return "{\"status\":404}";
        } else {
            return "{\"status\":200, \"mime-type\":\"text/html\", \"content\":\"" + encodeToBase64(response) +"\"}";
        }
    }

    // saadab POST request
    public static String sendPostRequest(URL url, String body) throws Exception {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        //add reuqest header
        con.setRequestMethod("POST");

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(body);
        wr.flush();
        wr.close();

        try {
            int responseCode = con.getResponseCode();
            if(responseCode==200){
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                return response.toString();
            } else {
                return "Error";
            }
        } catch (UnknownHostException e) {
            return "Error";
        }

    }

    //saadab GET päring ja tagastab vastus strignina
    public static String sendGetRequest(URL url) throws Exception {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");
        try {
            int responseCode = con.getResponseCode();
            if(responseCode==200){
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                return response.toString();
            } else {
                return "Error";
            }
        } catch (UnknownHostException e) {
            return "Error";
        }
    }

    //genereerib random ID
    public static int IdGenerator(){
        return (int) Math.floor(Math.random() * (99999999 - 00000001 + 1)) + 00000001;
    }
    //kodeerib url turvaliseks
    public static URL encodeURL(String string){
        try {
            String decodedURL = URLDecoder.decode(string, "UTF-8");
            URL url = new URL(decodedURL);
            URI uri = new URI(url.getProtocol(), url.getUserInfo(),
                    url.getHost(), url.getPort(), url.getPath(),
                    url.getQuery(), url.getRef());
            return uri.toURL();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    //kodeerib string Base64ks
    public static String encodeToBase64(String string) {
        return Base64.getEncoder().encodeToString(string.getBytes());
    }

}