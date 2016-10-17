import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


import com.sun.net.httpserver.HttpServer;

public class P2PNode {
    public static Map<String,String> nodes = new HashMap();

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(new InetSocketAddress(1215), 0);
        server.createContext("/", new RequestHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("P2P node started successfully!\n");

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                String[] peers= Request.getPeers();
                for(int i = 0; i< peers.length;i++){
                    System.out.print(peers[i]);
                }
                System.out.println();
            }
        }, 0, 60000);

    }

    public static Map<String,String> returnNodes(){
        return nodes;

    }
}