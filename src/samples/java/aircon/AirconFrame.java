/*
 * CyberUPnP for Java
 *
 * Copyright (C) Satoshi Konno 2002-2003
 */

package aircon;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import javax.swing.JFrame;

import vavi.net.upnp.UPnP;


/**
 * AirconFrame.
 *
 * @author Satoshi Konno
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 */
public class AirconFrame extends JFrame {

    private static final Logger logger = System.getLogger(AirconFrame.class.getName());

    private final static String TITLE = "CyberLink Sample Airconditoner";

    private final AirconDevice airconDev;

    private final AirconPane airconPane;

    public AirconFrame() throws IOException {
        super(TITLE);

        airconDev = new AirconDevice();

        getContentPane().setLayout(new BorderLayout());

        airconPane = new AirconPane();
        airconPane.setDevice(airconDev);
        airconDev.setComponent(airconPane);
        getContentPane().add(airconPane, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // main
        WindowListener windowListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent ev) {
                try {
                    airconDev.stop();
                } catch (IOException e) {
                    logger.log(Level.ERROR, e.getMessage(), e);
                }
            }
        };
        addWindowListener(windowListener);

        pack();
        setVisible(true);

        airconDev.start();
    }

    public AirconPane getClockPanel() {
        return airconPane;
    }

    public AirconDevice getClockDevice() {
        return airconDev;
    }

    // main
    public static void main(String[] args) throws Exception {
        UPnP.setProperty(UPnP.Flag.USE_ONLY_IPV4_ADDR, true);
        AirconFrame sampClock = new AirconFrame();
logger.log(Level.DEBUG, sampClock);
    }
}
