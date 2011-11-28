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
    double losPorbability = 50;
    private final CopyOnWriteArrayList<TypedMessageListener> listeners;

    public ChannelMock(Timer timer, String inId, String outId) {
        this.timer = timer;
        this.inId = inId;
        this.outId = outId;
        listeners = new CopyOnWriteArrayList<TypedMessageListener>();
    }

    public <T> void addMessageListener(Class<T> type, MessageListener<T> messageListener) {
        listeners.add(new TypedMessageListener(messageListener,type));
    }

    public void sendMessage(final Object message) {
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                int rnd = new Random().nextInt(100);
                if(rnd >= losPorbability) {
                    logger.trace("message " + message + " passed from " + inId + " to " + outId + " ");
                    for(TypedMessageListener listener : listeners) {
                        if(message.getClass() == listener.getType())
                        listener.getListener().onMessage(message);
                    }
                }else {
                    logger.trace("message " + message + " lost from " + inId + " to " + outId + " ");
                }
            }
        }, 0);
    }

    public double getLosPorbability() {
        return losPorbability;
    }

    public void setLosPorbability(double losPorbability) {
        this.losPorbability = losPorbability;
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
