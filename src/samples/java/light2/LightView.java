/*
 * CyberUPnP for Java
 *
 * Copyright (C) Satoshi Konno 2002
 */

package light2;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import javax.imageio.ImageIO;
import javax.swing.JPanel;


/**
 * Sample LightPane.
 */
public class LightView extends JPanel implements Light.View {

    private static final Logger logger = System.getLogger(LightView.class.getName());

    /** */
    private final static String LIGHT_ON_PANEL_IMAGE = "/light2/images/lighton.jpg";
    private final static String LIGHT_OFF_PANEL_IMAGE = "/light2/images/lightoff.jpg";

    /** */
    private final BufferedImage[] images = new BufferedImage[2];

    /** */
    public LightView() {
        try {
            images[0] = ImageIO.read(LightView.class.getResource(LIGHT_OFF_PANEL_IMAGE));
            images[1] = ImageIO.read(LightView.class.getResource(LIGHT_ON_PANEL_IMAGE));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        setPreferredSize(new Dimension(images[0].getWidth(), images[0].getHeight()));
    }

    /** */
    private boolean enabled;

    @Override
    public void setPowerEnabled(boolean enabled) {
        this.enabled = enabled;
logger.log(Level.DEBUG, "enabled: " + enabled);
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.GRAY);
        g.clearRect(0, 0, getWidth(), getHeight());
        g.drawImage(images[enabled ? 1 : 0], 0, 0, null);
    }
}
