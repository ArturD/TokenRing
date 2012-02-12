/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.poznan.put.cs.sw.tokenring.channels;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import pl.poznan.put.cs.sw.tokenring.Token;

/**
 *
 ** @author Artur Dwornik inf84789
 */
public class ChannelMock implements InChannel, OutChannel {
    private static final Logger logger = LogManager.getLogger(ChannelMock.class);
    private final Timer timer;
    private final String inId;
    private final String outId;
    double losPorbability = 50;
    private final CopyOnWriteArrayList<TypedMessageListener> listeners;
    private int minWait = 100;
    private int maxWait = 1000;
    private List<Object> messageQueue = new ArrayList();
    public ChannelMock(Timer timer, String inId, String outId) {
        this.timer = timer;
        this.inId = inId;
        this.outId = outId;
        listeners = new CopyOnWriteArrayList<TypedMessageListener>();
    }

    public <T> void addMessageListener(Class<T> type, MessageListener<T> messageListener) {
        listeners.add(new TypedMessageListener(messageListener,type));
    }

    public void sendMessage(Object message) {

        int wait;
        if(maxWait > 0) wait = new Random().nextInt(maxWait-minWait) + minWait;
        else wait = 0;
        messageQueue.add(message);

        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                synchronized(messageQueue) {
                    Object message = messageQueue.get(0);
                    messageQueue.remove(0);
                    int rnd = new Random().nextInt(100);
                    if(rnd >= losPorbability) {
                        logger.info("message " + message + " passed from " + inId + " to " + outId + " ");
                        for(TypedMessageListener listener : listeners) {
                            if(message.getClass() == listener.getType())
                            listener.getListener().onMessage(message);
                        }
                    }else {
                        logger.warn("message " + message + " lost from " + inId + " to " + outId + " ");
                    }
                }
            }
        }, wait);
    }

    public double getLosPorbability() {
        return losPorbability;
    }

    public void setLosPorbability(double losPorbability) {
        this.losPorbability = losPorbability;
    }

    public int getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(int maxWait) {
        this.maxWait = maxWait;
    }

    public int getMinWait() {
        return minWait;
    }

    public void setMinWait(int minWait) {
        this.minWait = minWait;
    }



    class TypedMessageListener {
        MessageListener listener;
        Class type;

        public TypedMessageListener(MessageListener listener, Class type) {
            this.listener = listener;
            this.type = type;
        }

        public MessageListener getListener() {
            return listener;
        }

        public void setListener(MessageListener listener) {
            this.listener = listener;
        }

        public Class getType() {
            return type;
        }

        public void setType(Class type) {
            this.type = type;
        }
    }
}
