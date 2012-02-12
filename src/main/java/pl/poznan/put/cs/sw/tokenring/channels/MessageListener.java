/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.poznan.put.cs.sw.tokenring.channels;

/**
 *
 ** @author Artur Dwornik inf84789
 */
public interface MessageListener<T> {
   void onMessage(T message); 
}
