/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.upnp.di;

import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import vavi.net.upnp.Device;
import vavi.net.upnp.Icon;
import vavi.net.util.Serdes;
import vavi.util.Singleton;


/**
 * IconSerdes. 
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 050921 nsano initial version <br>
 */
public class IconSerdes extends Singleton implements Serdes<Icon, Node, Node> {

    @Override
    public void serialize(Icon icon, Node iconNode) throws IOException {
        Document document = iconNode.getOwnerDocument();

        //
        Node node = document.createElementNS(Device.XMLNS, "url");
        node.setTextContent(icon.getURL());
        iconNode.appendChild(node);
        node = document.createElementNS(Device.XMLNS, "depth");
        node.setTextContent(icon.getDepth());
        iconNode.appendChild(node);
        node = document.createElementNS(Device.XMLNS, "height");
        node.setTextContent(icon.getHeight());
        iconNode.appendChild(node);
        node = document.createElementNS(Device.XMLNS, "width");
        node.setTextContent(icon.getWidth());
        iconNode.appendChild(node);
        node = document.createElementNS(Device.XMLNS, "mimetype");
        node.setTextContent(icon.getMimeType());
        iconNode.appendChild(node);
    }
    
    @Override
    public void deserialize(Node iconNode, Icon icon) throws IOException {
        icon.setURL(SerdesUtil.getChildNodeValue(iconNode, Device.XMLNS, "url"));
        icon.setDepth(SerdesUtil.getChildNodeValue(iconNode, Device.XMLNS, "depth"));
        icon.setHeight(SerdesUtil.getChildNodeValue(iconNode, Device.XMLNS, "height"));
        icon.setWidth(SerdesUtil.getChildNodeValue(iconNode, Device.XMLNS, "width"));
        icon.setMimeType(SerdesUtil.getChildNodeValue(iconNode, Device.XMLNS, "mimetype"));
    }
}
