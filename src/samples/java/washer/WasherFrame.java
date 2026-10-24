/*
 * CyberUPnP for Java
 *
 * Copyright (C) Satoshi Konno 2002-2003
 */

package washer;

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
 * WasherFrame.
 *
 * @author Satoshi Konno
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 */
public class WasherFrame extends JFrame {

    private static final Logger logger = System.getLogger(WasherFrame.class.getName());

    /** */
    private final static String TITLE = "CyberLink Sample Washer";

    /** */
    private final WasherDevice washerDevice;

    /** */
    private final WasherPane washerPane;

    /** */
    public WasherFrame() throws IOException {
        super(TITLE);

        washerDevice = new WasherDevice();

        getContentPane().setLayout(new BorderLayout());

        washerPane = new WasherPane();
        washerPane.setDevice(washerDevice);
        washerDevice.setPanel(washerPane);
        getContentPane().add(washerPane, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(windowListener);

        pack();
        setVisible(true);

        washerDevice.start();
    }

    /** */
    public WasherPane getPanel() {
        return washerPane;
    }

    /** */
    public WasherDevice getDevice() {
        return washerDevice;
    }

    /** main */
    private final WindowListener windowListener = new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent event) {
            try {
                washerDevice.stop();
            } catch (IOException e) {
logger.log(Level.ERROR, e.getMessage(), e);
            }
        }
    };

    /** main */
    public static void main(String[] args) throws Exception {
        UPnP.setProperty(UPnP.Flag.USE_ONLY_IPV4_ADDR, true);
        WasherFrame wahser = new WasherFrame();
logger.log(Level.DEBUG, wahser);
    }
}
