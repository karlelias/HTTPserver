import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.Timer;
import java.util.TimerTask;


import com.sun.net.httpserver.HttpServer;

public class P2PNode {
    public static List<String> neighbors = new ArrayList<String>();
    public static List<Integer> myRequestsIDs = new ArrayList<Integer>();
    public static double lazyness = 0.5;
    public static void main(String[] args) throws Exception {
        String url =  "";
//
//	    if(args.length == 0 || args.length==6)
//	    {
//	        System.out.println("Proper usage is: java P2PNode -u url -l lazyness ");
//	        System.exit(0);
//		}else {
//			url = args[1].toString();
//			//lazyness = Integer.valueOf(args[3]);
//		}


        HttpServer server = HttpServer.create(new InetSocketAddress(1215), 0);
        server.createContext("/", new RequestHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("P2P node started successfully!");

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    neighbors.clear();
                    String[] peers = RequestSender.getPeers();
                    //Logger.write("PEERS UPDATED: "+ Arrays.toString(peers));
                    for(String peer:peers){
                        neighbors.add(peer);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 60000);

        RequestSender.sendNewDownloadRequest("http://ttu.ee");

    }



}
