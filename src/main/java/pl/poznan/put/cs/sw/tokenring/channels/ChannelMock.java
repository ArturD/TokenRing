/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.poznan.put.cs.sw.tokenring.channels;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import pl.poznan.put.cs.sw.tokenring.Token;

/**
 *
 * @author Artur
 */
public class ChannelMock implements InChannel, OutChannel {
    private static final Logger logger = LogManager.getLogger(ChannelMock.class);
    private final Timer timer;
    private final String inId;
    private final String outId;
    private final CopyOnWriteArrayList<MessageListener> listeners;

    public ChannelMock(Timer timer, String inId, String outId) {
        this.timer = timer;
        this.inId = inId;
        this.outId = outId;
        listeners = new CopyOnWriteArrayList<MessageListener>();
    }

    public void addMessageListener(MessageListener messageListener) {
        listeners.add(messageListener);
    }

    public void sendMessage(final Object message) {
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                if(new Random().nextInt(100) < 50) {
                    logger.trace("message " + message + " passed from " + inId + " to " + outId + " ");
                    for(MessageListener listener : listeners) {
                        listener.onMessage(message);
                    }
                }else {
                    logger.trace("message " + message + " lost from " + inId + " to " + outId + " ");
                }
            }
        }, 0);
    }
    
}