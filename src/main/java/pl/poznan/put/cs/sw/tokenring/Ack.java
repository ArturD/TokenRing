/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.poznan.put.cs.sw.tokenring;

import java.io.Serializable;

/**
 *
 * @author inf84789
 */
public class Ack implements Serializable {
    private String sender;

    public Ack(String sender) {
        this.sender = sender;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Ack other = (Ack) obj;
        if ((this.sender == null) ? (other.sender != null) : !this.sender.equals(other.sender)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + (this.sender != null ? this.sender.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "Avk { sender = " + sender +" }";
    }
}
