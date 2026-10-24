/*
 * CyberUPnP for Java
 *
 * Copyright (C) Satoshi Konno 2002
 */

package vavi.net.upnp.soap;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import vavi.net.upnp.ControlPoint;
import vavi.net.upnp.Device;
import vavi.net.upnp.Service;
import vavi.net.upnp.gena.Subscription;


/**
 * SubscriberRenewer.
 *
 * @author Satoshi Konno
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 07/07/04 first revision.
 */
public class SubscriberRenewer {

    private static final Logger logger = System.getLogger(SubscriberRenewer.class.getName());

    /** */
    private final static long INTERVAL = 120;

    /** Member */
    private final ControlPoint controlPoint;

    /** Constructor */
    public SubscriberRenewer(ControlPoint controlPoint) {
        this.controlPoint = controlPoint;
    }

    /** Subscriber */
    private final ExecutorService subscriberRenewalService = Executors.newSingleThreadExecutor();

    /** */
    private Future<?> subscriberRenewal;

    /** */
    public void start() {
        this.subscriberRenewal = subscriberRenewalService.submit(subscriberRenewer);
    }

    /** */
    public void stop() {
        subscriberRenewal.cancel(true);
    }

    /** Thread */
    private final Runnable subscriberRenewer = () -> {
        try {
logger.log(Level.DEBUG, "+++ SubscriberRenewer started");
            while (true) {
                try {
                    try { Thread.sleep(INTERVAL * 1000); } catch (InterruptedException e) {}
                    renewSubscriberService();
                } catch (IOException e) {
logger.log(Level.ERROR, e.getMessage(), e);
                }
            }
        } catch (Exception e) {
logger.log(Level.ERROR, e.getMessage(), e);
        } finally {
logger.log(Level.DEBUG, "--- SubscriberRenewer stopped");
        }
    };

    /** */
    private void renewSubscriberService() throws IOException {
        renewSubscriberService(Subscription.INFINITE_VALUE);
    }

    /** */
    private void renewSubscriberService(long timeout) throws IOException {
        for (Device device : controlPoint.getDeviceList()) {
            renewSubscriberService(device, timeout);
        }
    }

    /** getSubscriberService */
    private void renewSubscriberService(Device device, long timeout) throws IOException {
        for (Service service : device.getServiceList()) {
            if (!service.isSubscribed()) {
                continue;
            }

            String sid = service.getSID();
            if (!controlPoint.subscribe(service, sid, timeout)) { // means "renewed ?"
                controlPoint.subscribe(service, timeout);
            }
        }

        for (Device childDevice : device.getChildDevices()) {
            renewSubscriberService(childDevice, timeout);
        }
    }
}
