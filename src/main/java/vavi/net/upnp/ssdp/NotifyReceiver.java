/*
 * CyberUPnP for Java
 *
 * Copyright (C) Satoshi Konno 2002-2003
 */

package vavi.net.upnp.ssdp;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import vavi.net.upnp.ControlPoint;


/**
 * NotifyReceiver.
 *
 * @author Satoshi Konno
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 11/20/02 first revision. <br>
 *          05/13/03 Added support for IPv6. <br>
 *          02/20/04 Inma Marin Lopez <inma@dif.um.es> <br>
 *                   Added a multicast filter using the SSDP pakcet. <br>
 */
public class NotifyReceiver {

    private static final Logger logger = System.getLogger(NotifyReceiver.class.getName());

    /** */
    private HttpMulticastSocket httpMulticastSocket;

    /** ControlPoint */
    private ControlPoint controlPoint;

    public void setControlPoint(ControlPoint controlPoint) {
        this.controlPoint = controlPoint;
    }

    /** Constructor */
    public NotifyReceiver(String bindAddress) throws IOException {
        this.httpMulticastSocket = new HttpMulticastSocket(bindAddress);
        this.controlPoint = null;
    }

    /** run */
    private final ExecutorService notifyReceivingService = Executors.newSingleThreadExecutor();

    /** */
    private Future<?> notifyReceiving;

    /** */
    public void start() {
        notifyReceivingService.submit(notifyReceiver);
    }

    /** */
    public void stop() {
        notifyReceiving.cancel(true);
    }

    /** */
    private final Runnable notifyReceiver = () -> {
        try {
logger.log(Level.DEBUG, "+++ NotifyReceiver started: " + SSDP.PORT);
    
            while (true) {
                Thread.yield();

                try {
                    SsdpRequest request = httpMulticastSocket.receive();

                    // Thanks for Inma (02/20/2004)
                    InetAddress multicastAddress = httpMulticastSocket.getMulticastInetAddress();
                    InetAddress packetAddress = request.getHostInetAddress();
                    if (!multicastAddress.equals(packetAddress)) {
logger.log(Level.DEBUG, "Invalidate Multicast Received: " + multicastAddress + ", " + packetAddress);
                        continue;
                    }

                    notifyReceived(request);
                } catch (IOException e) {
logger.log(Level.ERROR, e.getMessage(), e);
                }
            }
        } catch (Exception e) {
logger.log(Level.ERROR, e.getMessage(), e);
        } finally {
logger.log(Level.DEBUG, "--- NotifyReceiver stopped");
        }
    };

    /** */
    private void notifyReceived(SsdpRequest request) throws IOException {
        if (controlPoint == null) {
            return;
        }

        if (request.isRootDevice()) {
            if (request.isAlive()) {
                controlPoint.addDevice(request);
            }
            if (request.isByeBye()) {
                controlPoint.removeDevice(request);
            }
        }
        controlPoint.performNotifyListener(request);
    }
}
