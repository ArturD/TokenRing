/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.poznan.put.cs.sw.tokenring;

/**
 *
 * @author Artur
 */
public class Token {
    long counter;

    public Token(long counter) {
        this.counter = counter;
    }

    public long getCounter() {
        return counter;
    }

    public void setCounter(long counter) {
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
        final Token other = (Token) obj;
        if (this.counter != other.counter) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + (int) (this.counter ^ (this.counter >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        return "Token{" + "counter=" + counter + '}';
    }
}
