/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.poznan.put.cs.sw.tokenring.dijkstra;

import java.util.Random;
import java.util.logging.Level;
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
    private int state = 0;
    private int prevstate = 0;
    boolean first = false;
    private int processNo;

    public Node(InChannel inChannel, OutChannel outChannel, int processNo) {
        this.inChannel = inChannel;
        this.outChannel = outChannel;
        this.processNo = processNo;
        this.first = processNo == 0;

        inChannel.addMessageListener(UpdateState.class, new MessageListener<UpdateState>() {
            @Override
            public void onMessage(UpdateState message) {
                recive(message.getCounter());
            }
        });
    }

    protected synchronized void recive(int state) {
        prevstate = state;
        logger.debug("In " + processNo +"recived " + state);
        this.notifyAll();
    }

    protected synchronized void makeProgress(){
        logger.debug("changing state from " + state);
        if(first) {
            state = prevstate +1;
        }
        else state = prevstate;
        logger.debug("changing state to " + state);
        send(state);
    }

    private void send(int state) {
        outChannel.sendMessage(new UpdateState(state));
    }
    
    private synchronized void criticalSection(){
        try {
            while ((!(first && (prevstate == state))) && (!(!first && (prevstate != state)))) {
                wait();
            }
            logger.info("entering critical section in " + processNo);
            Thread.sleep(1000);
            logger.info("leave critical section in " + processNo);
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void run() {
        Thread th = new Thread(new Runnable() {

            @Override
            public void run() {
                while(true){
                    criticalSection();
                    makeProgress();
                }
                
            }
        } );
        th.start();
    }

}
