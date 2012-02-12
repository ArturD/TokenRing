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
 ** @author Artur Dwornik inf84789
 */
public class Node {

    private static final Logger logger = LogManager.getLogger(Node.class);
    private final InChannel inChannel;
    private final OutChannel outChannel;
    private final int mod;
    private String nodeId;
    private boolean hasToken = false;
    private int ping = 0;
    private int pong = 0;
    private int m =0;
    private boolean hasPong = false;
   
    
    public Node(InChannel inChannel, OutChannel outChannel, String nodeId, int mod) {
        this.inChannel = inChannel;
        this.outChannel = outChannel;
        this.nodeId = nodeId;
        this.mod = mod;

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
            logger.debug("regenerate pong on " + nodeId);
            regenerate(newToken.getCounter());
            passPong();
            passPing();
        }else {
            logger.debug("absorbing ping token on " + nodeId);
            hasToken = true;
            final Node self = this;
            Thread th = new Thread(new Runnable() {

                @Override
                public void run() {
                    synchronized(self) {
                        doStuff();
                        passPing();
                    }
                }
            });
            th.start();
        }
    }
    private synchronized void receive(final PongToken newToken) {
      pong = newToken.getCounter();
      if(hasToken) {
          logger.debug("pong incarnate on " + nodeId);
          hasPong = true;
          incarnate(pong);
      } else if(m == newToken.getCounter()) {
        logger.debug("ping regenerate on " + nodeId);
        regenerate(newToken.getCounter());
        passPong();
      } else {
        logger.debug("pong just pass on " + nodeId);
        passPong();
      }
    }

    private void doStuff() {
        logger.info("using token on " + nodeId);
        try {
            this.wait(1000 + new Random().nextInt(1000));
        } catch (InterruptedException ex) {
            logger.fatal("interrupted exception on doStuff", ex);
        }
        logger.info("end using token on " + nodeId);
    }

    void init() {
        logger.info("init " + nodeId);
        incarnate(0);
        passPong();
        passPing();

    }

    private synchronized void passPing() {
        //logger.info("passing ping on " + nodeId);
        hasToken= false;
        if(hasPong) passPong();
        m = ping;
        outChannel.sendMessage(new PingToken(ping));
    }

    private synchronized void passPong() {
        hasPong = false;
        m = pong;
        outChannel.sendMessage(new PongToken(pong));
    }

    private synchronized void regenerate(int counter) {
        ping = Math.abs(pong);
        pong = -ping;
    }

    private synchronized void incarnate(int counter) {
        ping = (Math.abs(counter)) % mod + 1;
        pong = -ping;
    }
}
