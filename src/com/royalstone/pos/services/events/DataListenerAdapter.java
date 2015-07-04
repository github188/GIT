

package com.royalstone.pos.services.events;

/**
 *
 * @author  Quentin Olson
 * @see
 */
public class DataListenerAdapter implements jpos.events.DataListener {

    public void dataOccurred(jpos.events.DataEvent e) {
        System.out.println("Data event : " + e.toString());
    }
}


