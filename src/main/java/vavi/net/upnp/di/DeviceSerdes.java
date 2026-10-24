/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.upnp.di;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;
import vavi.net.upnp.Device;
import vavi.net.upnp.Icon;
import vavi.net.upnp.Service;
import vavi.net.upnp.UPnP;
import vavi.net.util.Serdes;
import vavi.net.util.Util;
import vavi.util.Singleton;
import vavi.xml.util.PrettyPrinter;


/**
 * DeviceSerdes. 
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 050920 nsano initial version <br>
 */
public class DeviceSerdes extends Singleton implements Serdes<Device, InputStream, OutputStream> {

    private static final Logger logger = System.getLogger(DeviceSerdes.class.getName());

    /** */
    private static final ServiceSerdes serviceSerdes = Singleton.getInstance(ServiceSerdes.class);

    /** */
    private static final IconSerdes iconSerdes = Singleton.getInstance(IconSerdes.class);

    @Override
    public void serialize(Device device, OutputStream os) throws IOException {
        //
        Document document = Util.getDocumentBuilder().newDocument();

        Node rootNode = document.createElementNS(Device.XMLNS, "root"); 
        document.appendChild(rootNode);

        Node deviceNode = document.createElementNS(Device.XMLNS, "device"); 
        copyProperties(device, deviceNode);
        rootNode.appendChild(deviceNode);

        //
        PrintWriter pw = new PrintWriter(os);
        PrettyPrinter pp = new PrettyPrinter(pw);
        pp.print(document);
    }

    /** */
    private static void copyProperties(Device device, Node deviceNode) throws IOException {
        Document document = deviceNode.getOwnerDocument();
    
        Node node = document.createElementNS(Device.XMLNS, "deviceType");
        node.setTextContent(device.getDeviceType());
        deviceNode.appendChild(node);
        node = document.createElementNS(Device.XMLNS, "friendlyName");
        node.setTextContent(device.getFriendlyName());
        deviceNode.appendChild(node);
        node = document.createElementNS(Device.XMLNS, "manufacturer");
        node.setTextContent(device.getManufacture());
        deviceNode.appendChild(node);
        node = document.createElementNS(Device.XMLNS, "manufacturerURL");
        node.setTextContent(device.getManufactureURL());
        deviceNode.appendChild(node);
        node = document.createElementNS(Device.XMLNS, "modelDescription");
        node.setTextContent(device.getModelDescription());
        deviceNode.appendChild(node);
        node = document.createElementNS(Device.XMLNS, "modelName");
        node.setTextContent(device.getModelName());
        deviceNode.appendChild(node);
        node = document.createElementNS(Device.XMLNS, "modelNumber");
        node.setTextContent(device.getModelNumber());
        deviceNode.appendChild(node);
        node = document.createElementNS(Device.XMLNS, "modelURL");
        node.setTextContent(device.getModelURL());
        deviceNode.appendChild(node);
        node = document.createElementNS(Device.XMLNS, "serialNumber");
        node.setTextContent(device.getSerialNumber());
        deviceNode.appendChild(node);
        node = document.createElementNS(Device.XMLNS, "UDN");
        node.setTextContent(device.getUDN());
        deviceNode.appendChild(node);
        node = document.createElementNS(Device.XMLNS, "UPC");
        node.setTextContent(device.getUPC());
        deviceNode.appendChild(node);
        node = document.createElementNS(Device.XMLNS, "presentationURL");
        node.setTextContent(device.getPresentationURL());
        deviceNode.appendChild(node);
    
        Node childDeviceList = document.createElementNS(Device.XMLNS, "deviceList");
        deviceNode.appendChild(childDeviceList);
        for (Device childDevice : device.getChildDevices()) {
            Node childDeviceNode = document.createElementNS(Device.XMLNS, "device");
            copyProperties(childDevice, childDeviceNode);
            childDeviceList.appendChild(childDeviceNode);
        }
        //
        Node serviceList = document.createElementNS(Device.XMLNS, "serviceList");
        deviceNode.appendChild(serviceList);
        for (Service service : device.getServiceList()) {
            Node serviceNode = document.createElementNS(Device.XMLNS, "service"); 
            serviceSerdes.serialize(service, serviceNode);
            serviceList.appendChild(serviceNode);
        }
        // 
        Node iconList = document.createElementNS(Device.XMLNS, "iconList");
        deviceNode.appendChild(iconList);
        for (Icon icon : device.getIconList()) {
            Node iconNode = document.createElementNS(Device.XMLNS, "icon");
            iconSerdes.serialize(icon, iconNode);
            iconList.appendChild(iconNode);
        }

        //
        if (device.isRootDevice()) {
            node = document.createElementNS(Device.XMLNS, "URLBase");
            node.setTextContent(device.getURLBase());
            deviceNode.appendChild(node);
        }

        // userData
//      node.setUserData("deviceData", device.getDeviceData(), null); // TODO handler?

        //
        if (device.isNMPRMode()) {
            node = document.createElementNS(Device.XMLNS, UPnP.INMPR03);
            node.setTextContent(UPnP.INMPR03_VERSION);
            deviceNode.appendChild(node);
        }
    }

    @Override
    public void deserialize(InputStream is, Device device) throws IOException {
        try {
            DocumentBuilder parser = Util.getDocumentBuilder();
            Document document = parser.parse(is);
            if (document == null) {
                throw new IllegalArgumentException("no root node");
            }
            copyProperties(document, device);

        } catch (SAXException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /** */
    private static void copyProperties(Node document, Device device) throws IOException {
        try {
            //
            final String ELEM_XPATH = "/" + SerdesUtil.prefix(Device.XMLNS) + ":" + "root" + "/" + SerdesUtil.prefix(Device.XMLNS) + ":" + "device";
logger.log(Level.DEBUG, "ELEM_XPATH: " + ELEM_XPATH);
            Node deviceNode = (Node) SerdesUtil.getXPath().evaluate(ELEM_XPATH, document, XPathConstants.NODE);
            if (deviceNode == null) {
new PrettyPrinter(System.err).print(document);
                throw new IllegalArgumentException("no root device node");
            }
            copyPropertiesInternal(deviceNode, device);
logger.log(Level.DEBUG, "Device [root]: " + device.getFriendlyName());
            // TODO check
            final String DEVICE_LIST_XPATH = ".//" + SerdesUtil.prefix(Device.XMLNS) + ":" + "deviceList" + "/" + SerdesUtil.prefix(Device.XMLNS) + ":" + "device";
            NodeList childDeviceNodes = (NodeList) SerdesUtil.getXPath().evaluate(DEVICE_LIST_XPATH, deviceNode, XPathConstants.NODESET);
            for (int n = 0; n < childDeviceNodes.getLength(); n++) {
                Node childDeviceNode = childDeviceNodes.item(n);

                Device childDevice = new Device();
                childDevice.setParentDevice(device);
                copyPropertiesInternal(childDeviceNode, childDevice);
                device.getChildDevices().add(childDevice);
logger.log(Level.DEBUG, "Device [" + n + "]: " + childDevice.getFriendlyName());
            }
        } catch (XPathExpressionException e) {
            throw new IllegalStateException(e);
        }
    }

    /** copy one device */
    private static void copyPropertiesInternal(Node deviceNode, Device device) throws XPathExpressionException, IOException {

        //
        device.setDeviceType(SerdesUtil.getChildNodeValue(deviceNode, Device.XMLNS, "deviceType"));
        device.setFriendlyName(SerdesUtil.getChildNodeValue(deviceNode, Device.XMLNS, "friendlyName"));
        device.setManufacture(SerdesUtil.getChildNodeValue(deviceNode, Device.XMLNS, "manufacturer"));
        device.setManufactureURL(SerdesUtil.getChildNodeValue(deviceNode, Device.XMLNS, "manufacturerURL"));
        device.setModelDescription(SerdesUtil.getChildNodeValue(deviceNode, Device.XMLNS, "modelDescription"));
        device.setModelName(SerdesUtil.getChildNodeValue(deviceNode, Device.XMLNS, "modelName"));
        device.setModelNumber(SerdesUtil.getChildNodeValue(deviceNode, Device.XMLNS, "modelNumber"));
        device.setModelURL(SerdesUtil.getChildNodeValue(deviceNode, Device.XMLNS, "modelURL"));
        device.setSerialNumber(SerdesUtil.getChildNodeValue(deviceNode, Device.XMLNS, "serialNumber"));
        device.setUDN(SerdesUtil.getChildNodeValue(deviceNode, Device.XMLNS, "UDN"));
        device.setUPC(SerdesUtil.getChildNodeValue(deviceNode, Device.XMLNS, "UPC"));
        device.setPresentationURL(SerdesUtil.getChildNodeValue(deviceNode, Device.XMLNS, "presentationURL"));

        device.setNMPRMode(SerdesUtil.isChildNodeExists(deviceNode, Device.XMLNS, UPnP.INMPR03));

        // serviceList
        final String SERVICE_XPATH = ".//" + SerdesUtil.prefix(Device.XMLNS) + ":" + "serviceList" + "/" + SerdesUtil.prefix(Device.XMLNS) + ":" + "service";
        NodeList serviceNodes = (NodeList) SerdesUtil.getXPath().evaluate(SERVICE_XPATH, deviceNode, XPathConstants.NODESET);
        for (int n = 0; n < serviceNodes.getLength(); n++) {
            Node serviceNode = serviceNodes.item(n);
            
            Service service = new Service();
            service.setDevice(device);
            serviceSerdes.deserialize(serviceNode, service);
            device.getServiceList().add(service);
        }

        // iconList
        final String ICON_XPATH = ".//" + SerdesUtil.prefix(Device.XMLNS) + ":" + "iconList" + "/" + SerdesUtil.prefix(Device.XMLNS) + ":" + "icon";
        NodeList iconNodes = (NodeList) SerdesUtil.getXPath().evaluate(ICON_XPATH, deviceNode, XPathConstants.NODESET);
        for (int n = 0; n < iconNodes.getLength(); n++) {
            Node iconNode = iconNodes.item(n);
            
            Icon icon = new Icon();
            iconSerdes.deserialize(iconNode, icon);
            device.getIconList().add(icon);
        }
    }
}
