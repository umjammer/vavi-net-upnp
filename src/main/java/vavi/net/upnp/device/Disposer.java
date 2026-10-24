/*
 * CyberLink for Java
 *
 * Copyright (C) Satoshi Konno 2002-2004
 */

package vavi.net.upnp.device;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import vavi.net.upnp.ControlPoint;
import vavi.net.upnp.Device;


/**
 * Disposer.
 *
 * @author Satoshi Konno
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 01/05/04 first revision.
 */
public class Disposer {

    private static final Logger logger = System.getLogger(Disposer.class.getName());

    /** Member */
    private final ControlPoint controlPoint;

    /** */
    private final static int DEFAULT_EXPIRED_DEVICE_MONITORING_INTERVAL = 60;

    /** */
    private long expiredDeviceMonitoringInterval = DEFAULT_EXPIRED_DEVICE_MONITORING_INTERVAL;

    /** */
    public void setExpiredDeviceMonitoringInterval(long interval) {
        expiredDeviceMonitoringInterval = interval;
    }

    /** Constructor */
    public Disposer(ControlPoint controlPoint) {
        this.controlPoint = controlPoint;
    }

    /** */
    private final ExecutorService disposingService = Executors.newSingleThreadExecutor();

    /** */
    private Future<?> disposing;

    /** */
    public void start() {
        this.disposing = disposingService.submit(disposer);
    }

    /** */
    public void stop() {
        disposing.cancel(true);
    }

    /** Thread */
    private final Runnable disposer = () -> {
        try {
logger.log(Level.DEBUG, "+++ Disposer started");
            long monitorInterval = expiredDeviceMonitoringInterval * 1000;

            while (true) {
                try { Thread.sleep(monitorInterval); } catch (InterruptedException e) {}
                removeExpiredDevices();
            }
        } catch (Exception e) {
logger.log(Level.ERROR, e.getMessage(), e);
        } finally {
logger.log(Level.DEBUG, "--- Disposer stopped");
        }
    };

    /** */
    private void removeExpiredDevices() {
        List<Device> copiedDeviceList = new ArrayList<>(controlPoint.getDeviceList());
        for (Device device : copiedDeviceList) {
            if (device.isExpired()) {
logger.log(Level.DEBUG, "Expired device: " + device.getFriendlyName());
                controlPoint.getDeviceList().remove(device);
            }
        }
    }
}
