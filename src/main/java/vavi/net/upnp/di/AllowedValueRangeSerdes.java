/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.upnp.di;

import org.w3c.dom.Document;
import org.w3c.dom.Node;


import vavi.net.upnp.AllowedValueRange;
import vavi.net.upnp.Service;
import vavi.net.util.Serdes;
import vavi.util.Singleton;


/**
 * AllowedValueRange. 
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 050921 nsano initial version <br>
 */
public class AllowedValueRangeSerdes extends Singleton implements Serdes<AllowedValueRange, Node, Node> {

    @Override
    public void serialize(AllowedValueRange allowedValueRange, Node allowdValueRangeNode) {
        Document document = allowdValueRangeNode.getOwnerDocument();

        Node node = document.createElementNS(Service.XMLNS, "minimum");
        node.setTextContent(allowedValueRange.getMinimum());
        allowdValueRangeNode.appendChild(node);
        node = document.createElementNS(Service.XMLNS, "maximum");
        node.setTextContent(allowedValueRange.getMaximum());
        allowdValueRangeNode.appendChild(node);
        node = document.createElementNS(Service.XMLNS, "step");
        node.setTextContent(allowedValueRange.getStep());
        allowdValueRangeNode.appendChild(node);
    }
    
    @Override
    public void deserialize(Node allowdValueRangeNode, AllowedValueRange allowdValueRange) {
        allowdValueRange.setMinimum(SerdesUtil.getChildNodeValue(allowdValueRangeNode, Service.XMLNS, "minimum"));
        allowdValueRange.setMaximum(SerdesUtil.getChildNodeValue(allowdValueRangeNode, Service.XMLNS, "maximum"));
        allowdValueRange.setStep(SerdesUtil.getChildNodeValue(allowdValueRangeNode, Service.XMLNS, "step"));
    }
}
