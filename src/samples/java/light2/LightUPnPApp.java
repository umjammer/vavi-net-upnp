/*
 * CyberUPnP for Java
 *
 * Copyright (C) Satoshi Konno 2002-2003
 */

package light2;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import javax.swing.JFrame;

import vavi.net.upnp.Device;
import vavi.net.upnp.UPnP;
import vavi.net.upnp.annotation.UPnPFactory;


/**
 * Sample Light.
 */
public class LightUPnPApp extends JFrame {

    private static final Logger logger = System.getLogger(LightUPnPApp.class.getName());

    public LightUPnPApp() throws IOException {
        setTitle("Sample Light");

        Light model = new Light();
        Device device = UPnPFactory.getDevice(model);

        getContentPane().setLayout(new BorderLayout());

        LightView view = new LightView();
        model.setView(view);
        getContentPane().add(view, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent ev) {
                try {
                    device.stop();
                } catch (IOException e) {
logger.log(Level.ERROR, e.getMessage(), e);
                }
            }
        });

        pack();
        setVisible(true);

        device.start();
    }

    // main
    public static void main(String[] args) throws Exception {
        UPnP.setProperty(UPnP.Flag.USE_ONLY_IPV4_ADDR, true);
        LightUPnPApp upnpApp = new LightUPnPApp();
logger.log(Level.DEBUG, upnpApp.getTitle());
    }
}
