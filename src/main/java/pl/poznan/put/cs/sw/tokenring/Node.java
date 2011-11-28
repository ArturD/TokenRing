/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.poznan.put.cs.sw.tokenring;

import java.util.Random;
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
    private boolean resendToken = false;
    private TokenColor inColor = TokenColor.Black;
    private TokenColor outColor = TokenColor.Black;
    private boolean hasToken = false;
    private boolean sendAck = false;
    private final String nextId;
    
    public Node(InChannel inChannel, OutChannel outChannel, final Timer timer, long repeatPeriod, String nodeId, String nextId) {
        this.inChannel = inChannel;
        this.outChannel = outChannel;
        this.timer = timer;
        this.repeatPeriod = repeatPeriod;
        this.nodeId = nodeId;
        this.nextId = nextId;


        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                repeatToken();
            }
        }, 0, repeatPeriod);

        inChannel.addMessageListener(Token.class,new MessageListener<Token>() {

            public void onMessage(final Token message) {
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        receive(message);
                    }
                }, 0);
            }
        });


        inChannel.addMessageListener(Ack.class,new MessageListener<Ack>() {

            public void onMessage(final Ack message) {
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        receiveAck(message);
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
        if (token == null || token.getColor() == inColor) {
            logger.trace("new token accepted at " + nodeId);
            token = new Token(outColor);

            resendToken = false;
            sendAck = false;
            outColor = change(outColor);
            inColor = change(inColor);
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    hasToken = true;
                    doStuff();
                    hasToken = false;
                    passToken();
                }
            }, 0);

        } else {
            logger.trace("token repeat at " + nodeId);
            passAck(nodeId);
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
        resendToken = true;
        outChannel.sendMessage(token);
    }

    void init() {
        logger.info("init " + nodeId);
        token = new Token(outColor);
        outColor = change(outColor);
        passToken();
    }

    private TokenColor change(TokenColor color) {
        if(color == TokenColor.Black) return TokenColor.Red;
        if(color == TokenColor.Red) return TokenColor.Black;
        throw new IllegalStateException();
    }

    private void passAck(String nodeId) {
        outChannel.sendMessage(new Ack(nodeId));
    }
    private void receiveAck(Ack message) {
        resendToken = false;
        if(message.getSender().equals(nextId)) {
            passAck(message.getSender());
        }
    }
}
