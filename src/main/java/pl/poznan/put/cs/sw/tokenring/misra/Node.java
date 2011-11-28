/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.poznan.put.cs.sw.tokenring.misra;

import java.util.Random;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import pl.poznan.put.cs.sw.tokenring.channels.InChannel;
import pl.poznan.put.cs.sw.tokenring.channels.MessageListener;
import pl.poznan.put.cs.sw.tokenring.channels.OutChannel;

/**
 *
 * @author Artur
 */
public class Node {

    private static final Logger logger = LogManager.getLogger(Node.class);
    private final InChannel inChannel;
    private final OutChannel outChannel;
    private String nodeId;
    private boolean hasToken = false;
    private int ping = 0;
    private int pong = 0;
    private int m =0;
   
    
    public Node(InChannel inChannel, OutChannel outChannel, String nodeId) {
        this.inChannel = inChannel;
        this.outChannel = outChannel;
        this.nodeId = nodeId;

        inChannel.addMessageListener(PingToken.class, new MessageListener<PingToken>() {

            @Override
            public void onMessage(PingToken message) {
                receive(message);
            }
        });
        inChannel.addMessageListener(PongToken.class, new MessageListener<PongToken>() {

            @Override
            public void onMessage(PongToken message) {
                receive(message);
            }
        });
    }

    private synchronized void receive(final PingToken newToken) {
        ping = newToken.getCounter();
        if(m == newToken.getCounter()) {
            regenerate(newToken.getCounter());
            passPong();
        }else {
            hasToken = true;
            Thread th = new Thread(new Runnable() {

                @Override
                public void run() {
                    doStuff();
                    passPong();
                }
            });
        }
    }
    private synchronized void receive(final PongToken newToken) {
      pong = newToken.getCounter();
      if(m == newToken.getCounter()) {
            regenerate(newToken.getCounter());
      } else if(hasToken) {
          incarnate(pong);
      }
      passPong();
    }

    private void doStuff() {
        logger.info("using token on " + nodeId);
        try {
            Thread.sleep(1000 + new Random().nextInt(1000));
        } catch (InterruptedException ex) {
            logger.fatal("interrupted exception on doStuff", ex);
        }
    }

    void init() {
        logger.info("init " + nodeId);
        incarnate(0);
        passPing();
        passPong();

    }

    private synchronized void passPing() {
        m = ping;
        outChannel.sendMessage(new PingToken(ping));
    }

    private synchronized void passPong() {
        hasToken= false;
        m = pong;
        outChannel.sendMessage(new PongToken(pong));
    }

    private synchronized void regenerate(int counter) {
        ping = Math.abs(pong);
        pong = -ping;
    }

    private synchronized void incarnate(int counter) {
        ping = Math.abs(counter)+1;
        pong = -ping;
    }
}
