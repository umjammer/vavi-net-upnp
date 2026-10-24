/*
 * CyberUPnP for Java
 *
 * Copyright (C) Satoshi Konno 2002
 */

package tv;

import java.awt.Component;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;

import vavi.net.upnp.Action;
import vavi.net.upnp.Argument;
import vavi.net.upnp.ControlPoint;
import vavi.net.upnp.Device;
import vavi.net.upnp.Service;
import vavi.net.upnp.event.ActionListener;
import vavi.net.upnp.event.EventListener;
import vavi.net.upnp.event.NotifyListener;
import vavi.net.upnp.event.QueryListener;
import vavi.net.upnp.event.SearchResponseListener;
import vavi.net.upnp.gena.Subscription;
import vavi.net.upnp.ssdp.SsdpRequest;


/**
 * TvDevice.
 *
 * @author Satoshi Konno
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 */
public class TvDevice {

    private static final Logger logger = System.getLogger(TvDevice.class.getName());

    private final static String DESCRIPTION_FILE_NAME = "/tv/description/description.xml";

    private final static String CLOCK_DEVICE_TYPE = "urn:schemas-upnp-org:device:clock:1";
    private final static String CLOCK_SERVICE_TYPE = "urn:schemas-upnp-org:service:timer:1";
    private final static String LIGHT_DEVICE_TYPE = "urn:schemas-upnp-org:device:light:1";
    private final static String LIGHT_SERVICE_TYPE = "urn:schemas-upnp-org:service:power:1";
    private final static String AIRCON_DEVICE_TYPE = "urn:schemas-upnp-org:device:aircon:1";
    private final static String AIRCON_SERVICE_TYPE = "urn:schemas-upnp-org:service:temp:1";
    private final static String WASHER_DEVICE_TYPE = "urn:schemas-upnp-org:device:washer:1";
    private final static String WASHER_SERVICE_TYPE = "urn:schemas-upnp-org:service:state:1";

    private final ControlPoint controlPoint;

    private final Device tvDevice;

    public TvDevice() throws IOException {

        // Control Point
        controlPoint = new ControlPoint();

        controlPoint.addNotifyListener(notifyListener);
        controlPoint.addSearchResponseListener(searchResponseListener);
        controlPoint.addEventListener(eventListener);

        // Device
        tvDevice = new Device(TvDevice.class.getResource(DESCRIPTION_FILE_NAME));

        Action getPowerAction = tvDevice.getAction("GetPower");
        getPowerAction.setActionListener(actionListener);

        Action setPowerAction = tvDevice.getAction("SetPower");
        setPowerAction.setActionListener(actionListener);

        List<Service> serviceList = tvDevice.getServiceList();
        Service service = serviceList.get(0);
        service.setQueryListener(queryListener);

        on();
    }

    protected void dispose() {
        off();
    }

    /** Component */
    private Component comp;

    public void setComponent(Component comp) {
        this.comp = comp;
    }

    public Component getComponent() {
        return comp;
    }

    /** on/off */
    private boolean onFlag = false;

    public void on() {
        onFlag = true;
        try {
            controlPoint.search();
        } catch (IOException e) {
logger.log(Level.ERROR, e.getMessage(), e);
        }
    }

    public void off() {
        onFlag = false;
        try {
            controlPoint.unsubscribe();
        } catch (IOException e) {
logger.log(Level.ERROR, e.getMessage(), e);
        }
    }

    public boolean isOn() {
        return onFlag;
    }

    public void setPowerState(String state) {
        if (state == null) {
            off();
        } else if (state.equals("1")) {
            on();
        } else if (state.equals("0")) {
            off();
        } else {
logger.log(Level.DEBUG, "unknown state: " + state);
        }
    }

    public String getPowerState() {
        return onFlag ? "1" : "0";
    }

    // Clock
    private String clockTime = "";

    public String getClockTime() {
        return clockTime;
    }

    // Aircon
    private String airconTemp = "";

    public String getAirconTempture() {
        return airconTemp;
    }

    // Message
    private String message = "";

    public void setMessage(String msg) {
        message = msg;
    }

    public String getMessage() {
        return message;
    }

    // Device (Common)
    public boolean isDevice(SsdpRequest packet, String deviceType) {
        String usn = packet.getUSN();
        if (usn.endsWith(deviceType)) {
            return true;
        }
        return false;
    }

    public Service getDeviceService(String deviceType, String serviceType) {
        Device device = controlPoint.getDevice(deviceType);
        if (device == null) {
logger.log(Level.DEBUG, "no such device: " + deviceType);
            return null;
        }

        Service service = device.getService(serviceType);
        if (service == null) {
logger.log(Level.DEBUG, "no such service: " + serviceType);
            return null;
        }
        return service;
    }

    public boolean subscribeService(SsdpRequest packet, String deviceType, String serviceType) throws IOException {
        Service service = getDeviceService(deviceType, serviceType);
        if (service == null) {
logger.log(Level.DEBUG, "no such service: " + deviceType + ", " + serviceType);
            return false;
        }
        return controlPoint.subscribe(service, Subscription.INFINITE_VALUE);
    }

    // SSDP Listener
    public void checkNewDevices(SsdpRequest packet) throws IOException {
        subscribeService(packet, CLOCK_DEVICE_TYPE, CLOCK_SERVICE_TYPE);
        subscribeService(packet, AIRCON_DEVICE_TYPE, AIRCON_SERVICE_TYPE);
        subscribeService(packet, LIGHT_DEVICE_TYPE, LIGHT_SERVICE_TYPE);
        subscribeService(packet, WASHER_DEVICE_TYPE, WASHER_SERVICE_TYPE);
    }

    public void checkRemoveDevices(SsdpRequest packet) {
        if (isDevice(packet, CLOCK_DEVICE_TYPE)) {
            clockTime = "";
        } else if (isDevice(packet, AIRCON_DEVICE_TYPE)) {
            airconTemp = "";
        }
    }

    // Control Point Listener
    private final SearchResponseListener searchResponseListener = packet -> {
        try {
            checkNewDevices(packet);
        } catch (IOException e) {
            logger.log(Level.ERROR, e.getMessage(), e);
        }
    };

    private final NotifyListener notifyListener = packet -> {
        try {
            if (packet.isAlive()) {
                checkNewDevices(packet);
            } else if (packet.isByeBye()) {
                checkRemoveDevices(packet);
            }
        } catch (IOException e) {
            logger.log(Level.ERROR, e.getMessage(), e);
        }
    };

    private final EventListener eventListener = new EventListener() {
        @Override
        public void eventNotifyReceived(String uuid, long seq, String name, String value) {
// logger.log(Level.DEBUG, "Notify = " + uuid + ", " + seq + "," + name + "," + value);
            Service service = getSubscriberService(uuid);
            if (service == null) {
logger.log(Level.DEBUG, "no service for: " + uuid);
                return;
            }
            if (service.isServiceOf(CLOCK_SERVICE_TYPE)) {
                clockTime = value;
            } else if (service.isServiceOf(AIRCON_SERVICE_TYPE)) {
                airconTemp = value;
            } else {
                if ((value != null) && (!value.isEmpty())) {
                    Device device = service.getDevice();
                    String deviceName = device.getFriendlyName();
                    message = deviceName + ":" + value;
                }
            }
            comp.repaint();
        }
        /** */
        private Service getSubscriberService(String uuid) {
            for (Device device : controlPoint.getDeviceList()) {
                Service service = device.getSubscriberService(uuid);
                if (service != null) {
                    return service;
                }
            }
            return null;
        }
    };

    // ActionListener
    private final ActionListener actionListener = new ActionListener() {

        @Override
        public boolean actionControlReceived(Action action) {
            String actionName = action.getName();
            if (actionName.equals("GetPower")) {
                String state = getPowerState();
                Argument powerArgument = action.getArgument("Power");
                powerArgument.setValue(state);
            } else if (actionName.equals("SetPower")) {
                Argument powerArgument = action.getArgument("Power");
                String state = powerArgument.getValue();
                setPowerState(state);
                state = getPowerState();

                Argument resultArgument = action.getArgument("Result");
                resultArgument.setValue(state);
            } else {
                return false;
            }

            comp.repaint();
            return true;
        }
    };

    // QueryListener
    private final QueryListener queryListener = stateVariable -> {
        try {
            // TODO always return power state
            stateVariable.setValue(getPowerState());
            return true;
        } catch (IOException e) {
logger.log(Level.ERROR, e.getMessage(), e);
            return false;
        }
    };

    // start/stop
    public void start() throws IOException {
        controlPoint.start();
        tvDevice.start();
    }

    public void stop() throws IOException {
        controlPoint.stop();
        tvDevice.stop();
    }
}
