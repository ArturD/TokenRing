package pl.poznan.put.cs.sw.tokenring;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import pl.poznan.put.cs.sw.tokenring.channels.ChannelMock;

/**
 *** @author Artur Dwornik inf84789
 *
 */
public class App 
{
    private static final Logger logger = LogManager.getLogger(App.class);
    public static void main( String[] args )
    {
        int nodesNo = args.length >= 1 ? Integer.parseInt(args[0]) : 5;
        logger.info("Starting " + nodesNo + " nodes");
        
        Timer timer = new Timer();
        List<ChannelMock> channels = new ArrayList<ChannelMock>();
        List<Node> nodes = new ArrayList<Node>();
        for(int i = 0; i<nodesNo; i++) {
            ChannelMock channel = new ChannelMock(timer, "" +((i)%nodesNo) , "" +((1+i)%nodesNo));
            channel.setLosPorbability(0);
            channel.setMinWait(0);
            channel.setMaxWait(100);
            channels.add(channel);
        }
        for(int i = 0; i<nodesNo; i++) {
            nodes.add(new Node(channels.get((nodesNo + i - 1) %nodesNo),channels.get(i), timer, 100, "" + i, "" + ((i+1)%nodesNo)));
        }
        nodes.get(0).init();
    }
}
