import java.net.InetSocketAddress;
import java.util.*;

import com.sun.net.httpserver.HttpServer;

public class P2PNode {
    public static List<String> neighbors = new ArrayList<String>();
    public static List<Integer> myRequestsIDs = new ArrayList<Integer>();
    public static double lazyness = 0.5;
    public static void main(String[] args) throws Exception {
        String url =  "";

        HttpServer server = HttpServer.create(new InetSocketAddress(1215), 0);
        server.createContext("/", new RequestHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("P2P node started successfully!");
        RequestSender.sendNewDownloadRequest("http://ttu.ee");

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    neighbors.clear();
                    String[] peers = RequestSender.getPeers();
                    Logger.write("PEERS UPDATED: "+ Arrays.toString(peers));
                    for(String peer:peers){
                        neighbors.add(peer);
                        //System.out.println(neighbors);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 60000);

        RequestSender.sendNewDownloadRequest("http://ttu.ee");

    }



}
