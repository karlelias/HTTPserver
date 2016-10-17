import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class Request {
    //returnd neighbour peers in String array
    public static String[] getPeers(){
        try {
            URL url = new URL(" http://192.168.3.11:1215/getpeers");
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String peers = "";

            while (null != (peers = br.readLine())) {
                String[] peersArray =  peers.replaceAll("\\s+","").replace("\"", "").replace("]", "").replace("[","").split(",");;
                return peersArray;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void download(String id, String url) {
        P2PNode.returnNodes();//for all nodes
        //makes http request to all nodes
        ///do
    }
}