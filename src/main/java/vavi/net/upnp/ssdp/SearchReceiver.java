/*
 * CyberUPnP for Java
 *
 * Copyright (C) Satoshi Konno 2002-2003
 */

package vavi.net.upnp.ssdp;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.event.EventListenerList;

import vavi.net.upnp.event.SearchListener;


/**
 * SearchReceiver.
 *
 * @author Satoshi Konno
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 12/30/02 first revision. <br>
 *          05/13/03 Added support for IPv6. <br>
 *          05/28/03 Moved post() for SSDPSearchRequest to SSDPResponseSocketList. <br>
 */
public class SearchReceiver {

    private static final Logger logger = System.getLogger(SearchReceiver.class.getName());

    /** */
    private final HttpMulticastSocket httpMulticastSocket;

    /** */
    private boolean useIPv6Address;

    /** Constructor */
    public SearchReceiver(String bindAddress) throws IOException {
        this.httpMulticastSocket = new HttpMulticastSocket(bindAddress);
    }

    /** run */
    private final ExecutorService searchReceivingService = Executors.newSingleThreadExecutor();

    /** */
    private Future<?> searchReceiving;

    /** */
    public void start() {
        searchReceiving = searchReceivingService.submit(searchReceiver);
    }

    /** */
    public void stop() throws IOException {
        searchReceiving.cancel(true);
        httpMulticastSocket.close();
    }

    /** */
    private final Runnable searchReceiver = new Runnable() {
        @Override
        public void run() {
            try {
logger.log(Level.DEBUG, "+++ SearchReceiver started");
    
                while (true) {
                    Thread.yield();
        
                    try {
                        SsdpRequest packet = httpMulticastSocket.receive();
                        if (packet.isDiscover()) {
                            performSearchListener(packet);
                        }
                    } catch (IOException e) {
logger.log(Level.ERROR, e.getMessage(), e);
                    }
                }
            } catch (Exception e) {
logger.log(Level.ERROR, e.getMessage(), e);
            } finally {
logger.log(Level.DEBUG, "--- SearchReceiver stopped");
            }
        }
    };

    /** deviceSearch */
    private final EventListenerList searchListenerList = new EventListenerList();

    /** */
    public void addSearchListener(SearchListener listener) {
        searchListenerList.add(SearchListener.class, listener);
    }

    /** */
    public void removeSearchListener(SearchListener listener) {
        searchListenerList.remove(SearchListener.class, listener);
    }

    /** */
    public void performSearchListener(SsdpRequest ssdpPacket) {
        Object[] listeners = searchListenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == SearchListener.class) {
                ((SearchListener) listeners[i + 1]).deviceSearchReceived(ssdpPacket);
            }
        }
    }
}
