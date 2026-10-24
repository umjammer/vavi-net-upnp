/*
 * CyberUPnP for Java
 *
 * Copyright (C) Satoshi Konno 2002
 */

package light;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JPanel;


/**
 * Sample LightPane.
 *
 * @author Satoshi Konno
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 */
public class LightPane extends JPanel {

    private static final Logger logger = System.getLogger(LightPane.class.getName());

    /** Images */
    private final static String LIGHT_ON_PANEL_IMAGE = "/light/images/lighton.jpg";

    /** */
    private final static String LIGHT_OFF_PANEL_IMAGE = "/light/images/lightoff.jpg";

    /** Constructor */
    public LightPane() {
        loadImage(LIGHT_OFF_PANEL_IMAGE);
        initPanel();
    }

    /** Background */
    private BufferedImage panelmage;

    /** */
    private void loadImage(String finename) {
        URL f = LightPane.class.getResource(finename);
        try {
            panelmage = ImageIO.read(f);
        } catch (Exception e) {
            logger.log(Level.ERROR, e.getMessage(), e);
        }
    }

    /** */
    private BufferedImage getPaneImage() {
        return panelmage;
    }

    /** Background */
    private void initPanel() {
        BufferedImage panelmage = getPaneImage();
        setPreferredSize(new Dimension(panelmage.getWidth(), panelmage.getHeight()));
    }

    /** LightDevice */
    private LightDevice lightDev = null;

    /** */
    public void setDevice(LightDevice dev) {
        lightDev = dev;
    }

    /** */
    public LightDevice getDevice() {
        return lightDev;
    }

    /** paint */
    private void clear(Graphics g) {
        g.setColor(Color.GRAY);
        g.clearRect(0, 0, getWidth(), getHeight());
    }

    /** */
    private void drawPanelImage(Graphics g) {
        g.drawImage(getPaneImage(), 0, 0, null);
    }

    @Override
    public void paint(Graphics g) {
        LightDevice dev = getDevice();
        if (dev.isOn() == true) {
            loadImage(LIGHT_ON_PANEL_IMAGE);
        } else {
            loadImage(LIGHT_OFF_PANEL_IMAGE);
        }

        clear(g);
        drawPanelImage(g);
    }
}
