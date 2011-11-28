/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.poznan.put.cs.sw.tokenring.channels;

/**
 *
 * @author Artur
 */
public interface InChannel {
    <T> void addMessageListener(Class<T> type, MessageListener<T> messageListener);
}
