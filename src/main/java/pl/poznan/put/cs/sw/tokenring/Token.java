/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.poznan.put.cs.sw.tokenring;

import java.io.Serializable;

/**
 *
 ** @author Artur Dwornik inf84789
 */
public class Token implements Serializable {
    private TokenColor color;

    public Token(TokenColor color) {
        this.color = color;
    }

    public TokenColor getColor() {
        return color;
    }

    public void setColor(TokenColor color) {
        this.color = color;
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
        if (this.color != other.color && (this.color == null || !this.color.equals(other.color))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.color != null ? this.color.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "Token { color = " + color + " }";
    }
}
