/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.poznan.put.cs.sw.tokenring;

import java.util.Random;
import java.util.logging.Level;
import pl.poznan.put.cs.sw.tokenring.channels.OutChannel;
import pl.poznan.put.cs.sw.tokenring.channels.InChannel;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import pl.poznan.put.cs.sw.tokenring.channels.MessageListener;

/**
 *
 * @author Artur
 */
public class Node {

    private static final Logger logger = LogManager.getLogger(Node.class);
    private final InChannel inChannel;
    private final OutChannel outChannel;
    private final Timer timer;
    private final long repeatPeriod;
    private final String nodeId;
    private Token token;
    private boolean resend = false;

    public Node(InChannel inChannel, OutChannel outChannel, final Timer timer, long repeatPeriod, String nodeId) {
        this.inChannel = inChannel;
        this.outChannel = outChannel;
        this.timer = timer;
        this.repeatPeriod = repeatPeriod;
        this.nodeId = nodeId;

        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                repeatToken();
            }
        }, 0, repeatPeriod);

        inChannel.addMessageListener(new MessageListener<Token>() {

            public void onMessage(final Token message) {
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        receive(message);
                    }
                }, 0);
            }
        });
    }

    private synchronized void repeatToken() {
        if (token != null) {
            outChannel.sendMessage(token);
        }
    }

    private synchronized void receive(final Token newToken) {
        if (token == null || token.getCounter() < newToken.getCounter()) {
            logger.trace("new token accepted at " + nodeId);
            token = new Token(newToken.getCounter()+1);;
            resend = false;
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    doStuff();
                    passToken();
                }
            }, 0);
        } else {
            logger.trace("token repeat at " + nodeId);
        }
    }

    private void doStuff() {
        logger.info("using token on " + nodeId);
        try {
            Thread.sleep(1000 + new Random().nextInt(1000));
        } catch (InterruptedException ex) {
            logger.fatal("interrupted exception on doStuff", ex);
        }
    }

    private synchronized void passToken() {
        resend = true;
        outChannel.sendMessage(token);
    }

    void init() {
        logger.info("init " + nodeId);
        token = new Token(0);
        passToken();
    }
}
