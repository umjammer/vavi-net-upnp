/*
 * CyberUPnP for Java
 *
 * Copyright (C) Satoshi Konno 2002
 */

package controlpoint;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import vavi.net.upnp.ssdp.SSDP;


/**
 * MenuBar.
 *
 * @author Satoshi Konno
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * TODO to be deprecated
 */
public class MenuBar extends JMenuBar {

    private static final Logger logger = System.getLogger(MenuBar.class.getName());

    final ControlPoint controlPoint;

    final JMenu fileMenu;

    final JMenu searchMenu;

    final JMenu logMenu;

    final JMenuItem quitItem;

    final JMenuItem searchRootDeviceItem;

    final JMenuItem searchAllItem;

    final JMenuItem clearItem;

    final JCheckBoxMenuItem stopUpdate;

    public MenuBar(ControlPoint controlPoint) {
        this.controlPoint = controlPoint;

        fileMenu = new JMenu("File");
        add(fileMenu);
        quitItem = new JMenuItem("Quit");
        quitItem.addActionListener(quitAction);
        fileMenu.add(quitItem);

        searchMenu = new JMenu("Search");
        add(searchMenu);
        searchRootDeviceItem = new JMenuItem("upnp:rootdevice");
        searchRootDeviceItem.addActionListener(searchAction);
        searchMenu.add(searchRootDeviceItem);

        searchAllItem = new JMenuItem(SSDP.ALL_DEVICE);
        searchAllItem.addActionListener(searchAllAction);
        searchMenu.add(searchAllItem);

        stopUpdate = new JCheckBoxMenuItem("Stop Update");
        stopUpdate.addActionListener(stopUpdateAction);
        searchMenu.add(stopUpdate);

        logMenu = new JMenu("Log");
        add(logMenu);
        clearItem = new JMenuItem("Clear");
        clearItem.addActionListener(clearAction);
        logMenu.add(clearItem);
    }

    final Action quitAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent ev) {
            System.exit(0);
        }
    };

    final Action clearAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent ev) {
            controlPoint.clearConsole();
        }
    };

    final Action stopUpdateAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent ev) {
            controlPoint.setStopUpdate(stopUpdate.getState());
        }
    };

    final Action searchAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent ev) {
            try {
                controlPoint.search("upnp:rootdevice", SSDP.DEFAULT_MSEARCH_MX);
            } catch (IOException e) {
                logger.log(Level.ERROR, e.getMessage(), e);
            }
        }
    };

    final Action searchAllAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent ev) {
            try {
                controlPoint.search(SSDP.ALL_DEVICE, SSDP.DEFAULT_MSEARCH_MX);
            } catch (IOException e) {
                logger.log(Level.ERROR, e.getMessage(), e);
            }
        }
    };
}
