/*
 * CyberUPnP for Java
 *
 * Copyright (C) Satoshi Konno 2002
 */

package aircon;

import java.awt.Component;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;


import vavi.net.upnp.Action;
import vavi.net.upnp.Argument;
import vavi.net.upnp.Device;
import vavi.net.upnp.Service;
import vavi.net.upnp.StateVariable;
import vavi.net.upnp.event.ActionListener;
import vavi.net.upnp.event.QueryListener;


/**
 * AirconDevice.
 *
 * @author Satoshi Konno
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 */
public class AirconDevice extends Device {

    private static final Logger logger = System.getLogger(AirconDevice.class.getName());

    /** */
    private final static String DESCRIPTION_FILE_NAME = "/aircon/description/description.xml";
    /** */
    private final StateVariable tempVar;

    public AirconDevice() throws IOException {
        super(AirconDevice.class.getResource(DESCRIPTION_FILE_NAME));

        Action getPowerAction = getAction("GetPower");
        // ActionListener
        ActionListener actionListener = action -> {
            String actionName = action.getName();

            switch (actionName) {
                case "GetPower": {
                    String state = getPowerState();
                    Argument powerArg = action.getArgument("Power");
                    powerArg.setValue(state);
                    break;
                }
                case "SetPower": {
                    Argument powerArg = action.getArgument("Power");
                    String state = powerArg.getValue();
                    setPowerState(state);
                    state = getPowerState();

                    Argument resultArg = action.getArgument("Result");
                    resultArg.setValue(state);
                    break;
                }
                case "GetTemp": {
                    int temp = getTemperature();
                    Argument tempArg = action.getArgument("Temp");
                    tempArg.setValue(String.valueOf(temp));
                    break;
                }
                case "SetTemp": {
                    Argument powerArg = action.getArgument("Temp");
                    int temp = Integer.parseInt(powerArg.getValue());
                    setTemperature(temp);
                    temp = getTemperature();

                    Argument resultArg = action.getArgument("Result");
                    resultArg.setValue(String.valueOf(temp));
                    break;
                }
                default:
                    return false;
            }

            comp.repaint();
            return true;
        };
        getPowerAction.setActionListener(actionListener);

        Action setPowerAction = getAction("SetPower");
        setPowerAction.setActionListener(actionListener);

        Action getTempAction = getAction("GetTemp");
        getTempAction.setActionListener(actionListener);

        Action setTempAction = getAction("SetTemp");
        setTempAction.setActionListener(actionListener);

        List<Service> serviceList = getServiceList();
        Service service = serviceList.get(0);
        /** QueryListener */
        QueryListener queryListener = stateVar -> {
            try {
                stateVar.setValue(getPowerState());
                return true;
            } catch (IOException e) {
                logger.log(Level.ERROR, e.getMessage(), e);
                return false;
            }
        };
        service.setQueryListener(queryListener);

        tempVar = getStateVariable("Temp");

        setTemperature(18);
    }

    //	Component
    private Component comp;

    public void setComponent(Component comp) {
        this.comp = comp;
    }

    /** on/off */
    private boolean onFlag = true;

    public void on() {
        onFlag = true;
    }

    public void off() {
        onFlag = false;
    }

    public boolean isOn() {
        return onFlag;
    }

    /**
     * @param state "1" on, "0" off.
     */
    public void setPowerState(String state) {
        if (state == null) {
            off();
        } else if (state.equals("1")) {
            on();
        } else if (state.equals("0")) {
            off();
        } else {
            logger.log(Level.DEBUG, "illegal state: " + state);
        }
    }

    /** */
    public String getPowerState() {
        return onFlag ? "1" : "0";
    }

    // on/off
    private int temperature = 18;

    public void setTemperature(int state) {
        if (!isOn()) {
            return;
        }

        if (state == 1) {
            temperature++;
        } else if (state == -1) {
            temperature--;
        } else {
            temperature = state;
        }

        try {
            tempVar.setValue(String.valueOf(temperature));
        } catch (IOException e) {
            logger.log(Level.ERROR, e.getMessage(), e);
        }
    }

    public int getTemperature() {
        return temperature;
    }

    /** update */
    public void update() {
    }
}
