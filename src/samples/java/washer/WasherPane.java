/*
 * CyberUPnP for Java
 *
 * Copyright (C) Satoshi Konno 2002
 */

package washer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import javax.imageio.ImageIO;
import javax.swing.JPanel;


/**
 * WasherPane.
 *
 * @author Satoshi Konno
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 */
public class WasherPane extends JPanel {

    private static final Logger logger = System.getLogger(WasherPane.class.getName());

    /** */
    public WasherPane() {
        loadImage();
        initPanel();
    }

    /** */
    private final static String WASHER_PANEL_IMAGE = "/images/washer.jpg";
    /** */
    private final static String WASHER_ANIM_IMAGE1 = "/images/washeron1.jpg";
    /** */
    private final static String WASHER_ANIM_IMAGE2 = "/images/washeron2.jpg";
    /** */
    private BufferedImage panelImage;
    /** */
    private BufferedImage animImage;
    /** */
    private BufferedImage anim1Image;
    /** */
    private BufferedImage anim2Image;

    /** */
    private void loadImage() {
        try {
            panelImage = ImageIO.read(washer.WasherFrame.class.getResource(WASHER_PANEL_IMAGE));
            anim1Image = ImageIO.read(washer.WasherFrame.class.getResource(WASHER_ANIM_IMAGE1));
            anim2Image = ImageIO.read(washer.WasherFrame.class.getResource(WASHER_ANIM_IMAGE2));
        } catch (Exception e) {
            logger.log(Level.ERROR, e.getMessage(), e);
        }
    }

    /** */
    private BufferedImage getPaneImage() {
        return panelImage;
    }

    /** */
    public void flipAnimationImage() {
        if (animImage == anim1Image) {
            animImage = anim2Image;
        } else {
            animImage = anim1Image;
        }
    }

    /** Background */
    private void initPanel() {
        BufferedImage panelmage = getPaneImage();
        setPreferredSize(new Dimension(panelmage.getWidth(), panelmage.getHeight()));
    }

    /** LightDevice */
    private washer.WasherDevice washerDevice;

    /** */
    public void setDevice(washer.WasherDevice device) {
        washerDevice = device;
    }

    /** */
    public washer.WasherDevice getDevice() {
        return washerDevice;
    }

    /** paint */
    private void drawInfo(Graphics g) {
        if (washerDevice.isWashing()) {
            g.drawImage(animImage, 0, 0, null);
            g.setColor(Color.YELLOW);
        } else {
            g.drawImage(panelImage, 0, 0, null);
            g.setColor(Color.LIGHT_GRAY);
        }
        g.fillRect(5, 20, 20, 5);
    }

    /** */
    private void clear(Graphics g) {
        g.setColor(Color.GRAY);
        g.clearRect(0, 0, getWidth(), getHeight());
    }

    @Override
    public void paint(Graphics g) {
        clear(g);
        drawInfo(g);
    }
}
