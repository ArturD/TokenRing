/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.poznan.put.cs.sw.tokenring.misra;

import java.io.Serializable;

/**
 *
 ** @author Artur Dwornik inf84789
 */
public class PingToken implements Serializable {
    private int counter;

    public PingToken(int counter) {
        this.counter = counter;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PingToken other = (PingToken) obj;
        if (this.counter != other.counter) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.counter;
        return hash;
    }

    @Override
    public String toString() {
        return "Ping: " + counter;
    }

}
