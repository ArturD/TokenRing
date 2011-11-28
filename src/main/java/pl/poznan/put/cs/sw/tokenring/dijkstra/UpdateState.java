package pl.poznan.put.cs.sw.tokenring.dijkstra;

import java.io.Serializable;

public class UpdateState implements Serializable {
    private int counter;

    public UpdateState(int counter) {
        this.counter = counter;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    @Override
    public String toString() {
        return "UpdateState: " + counter;
    }


}