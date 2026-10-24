/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.upnp.di;

import java.io.File;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

import vavi.net.upnp.Action;
import vavi.net.upnp.Device;
import vavi.net.upnp.Service;
import vavi.net.upnp.StateVariable;
import vavi.net.util.Serdes;
import vavi.net.util.Util;
import vavi.util.Singleton;


/**
 * ServiceSerdes. 
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 050921 nsano initial version <br>
 */
public class ServiceSerdes extends Singleton implements Serdes<Service, Node, Node> {

    private static final Logger logger = System.getLogger(ServiceSerdes.class.getName());

    /** */
    private static final ActionSerdes actionSerdes = Singleton.getInstance(ActionSerdes.class);

    /** */
    private static final StateVariableSerdes stateVariableSerdes = Singleton.getInstance(StateVariableSerdes.class);

    /**
     * only device node
     * <pre>
     * /root/device/serviceList/service
     * </pre> 
     */
    @Override
    public void serialize(Service service, Node serviceNode) throws IOException {
        Document document = serviceNode.getOwnerDocument();

        //
        Node node = document.createElementNS(Device.XMLNS, "serviceType");
        node.setTextContent(service.getServiceType());
        serviceNode.appendChild(node);
        node = document.createElementNS(Device.XMLNS, "serviceId");
        node.setTextContent(service.getServiceID());
        serviceNode.appendChild(node);
        node = document.createElementNS(Device.XMLNS, "SCPDURL");
        node.setTextContent(service.getSCPDURL());
        serviceNode.appendChild(node);
        node = document.createElementNS(Device.XMLNS, "controlURL");
        node.setTextContent(service.getControlURL());
        serviceNode.appendChild(node);
        node = document.createElementNS(Device.XMLNS, "eventSubURL");
        node.setTextContent(service.getEventSubURL());
        serviceNode.appendChild(node);

        // TODO /scpd//actionList

        // TODO /scpd//serviceStateTable

        // userData
//      serviceNode.setUserData("serviceData", service.getServiceData(), null); // TODO handler?
    }

    @Override
    public void deserialize(Node serviceNode, Service service) throws IOException {
        try {
            //
            service.setServiceType(SerdesUtil.getChildNodeValue(serviceNode, Device.XMLNS, "serviceType"));
            service.setServiceID(SerdesUtil.getChildNodeValue(serviceNode, Device.XMLNS, "serviceId"));
            service.setSCPDURL(SerdesUtil.getChildNodeValue(serviceNode, Device.XMLNS, "SCPDURL"));
            service.setControlURL(SerdesUtil.getChildNodeValue(serviceNode, Device.XMLNS, "controlURL"));
            service.setEventSubURL(SerdesUtil.getChildNodeValue(serviceNode, Device.XMLNS, "eventSubURL"));
    
            // scpd
            Node scdpNode = getSCPDNode(service);
            if (scdpNode == null) {
logger.log(Level.DEBUG, "could not get scpd");
                return; // TODO just return ?
            }

            // /scpd//actionList
            final String ACTION_XPATH = ".//" + SerdesUtil.prefix(Service.XMLNS) + ":" + "actionList" + "/" + SerdesUtil.prefix(Service.XMLNS) + ":" + "action";
            NodeList actionNodes = (NodeList) SerdesUtil.getXPath().evaluate(ACTION_XPATH, scdpNode, XPathConstants.NODESET);
            for (int n = 0; n < actionNodes.getLength(); n++) {
                Node actionNode = actionNodes.item(n);
                
                Action action = new Action();
                action.setService(service);
                actionSerdes.deserialize(actionNode, action);
//logger.log(Level.DEBUG, "Action[" + n + "]: " + action.getName() + ", " + action.getArgumentList().size());
                service.getActionList().add(action);
            }
    
            // /scpd//serviceStateTable
            final String STATE_VARIABLE_XPATH = ".//" + SerdesUtil.prefix(Service.XMLNS) + ":" + "serviceStateTable" + "/" + SerdesUtil.prefix(Service.XMLNS) + ":" + "stateVariable";
            NodeList stateVariableNodes = (NodeList) SerdesUtil.getXPath().evaluate(STATE_VARIABLE_XPATH, scdpNode, XPathConstants.NODESET);
            for (int n = 0; n < stateVariableNodes.getLength(); n++) {
                Node stateVariableNode = stateVariableNodes.item(n);
                
                StateVariable stateVariable = new StateVariable();
                stateVariable.setService(service);
                stateVariableSerdes.deserialize(stateVariableNode, stateVariable);
//logger.log(Level.DEBUG, "StateVariable[" + n + "]: " + stateVariable.getDataType() + " " + stateVariable.getName());
                service.getStateVariableList().add(stateVariable);
            }
    
            // TODO userData
    
        } catch (XPathExpressionException e) {
            throw new IllegalStateException(e);
        } catch (SAXException e) {
            throw new IllegalArgumentException(e); // TODO
        }
    }

    /**
     * @throws IllegalStateException could not get scpd
     */
    private static Node getSCPDNode(Service service) throws IOException, SAXException {
        Node scpdNode = service.getSCPDNode();
        if (scpdNode != null) {
logger.log(Level.DEBUG, "TRY 0 OK: " + scpdNode);
            return scpdNode;
        }

        String scpdURLString = service.getSCPDURL();
        try {
            scpdNode = getSCPDNode(scpdURLString);
//logger.log(Level.DEBUG, "TRY 1 OK: " + scpdURLString);
        } catch (Exception e1) {
//logger.log(Level.DEBUG, "TRY 1 NG: " + scpdURLString);
            Device rootDevice = service.getDevice().getRootDevice();
            String urlBaseString = rootDevice.getURLBase();
//logger.log(Level.DEBUG, "urlBaseString 1: " + urlBaseString + ", " + rootDevice.getFriendlyName());

            // Thanks for Steven Yen (2003/09/03)
            if (urlBaseString == null || urlBaseString.isEmpty()) {
                String locationHost = "";
                int locationPort = 80;
                URL location = null;
                try {
                    location = new URL(rootDevice.getLocation());
                    locationHost = location.getHost();
                    locationPort = location.getPort();
                } catch (MalformedURLException e) {
//logger.log(Level.DEBUG, e);
                }
                urlBaseString = "http://" + locationHost + ":" + locationPort;
//logger.log(Level.DEBUG, "urlBaseString 2: " + urlBaseString + ", " + StringUtil.paramString(rootDevice));
            }

            scpdURLString = Util.toRelativeURL(scpdURLString);
            String newScpdURLString = urlBaseString + scpdURLString;

            try {
                scpdNode = getSCPDNode(newScpdURLString);
//logger.log(Level.DEBUG, "TRY 2 OK: " + newScpdURLString);
            } catch (IOException e2) {
//logger.log(Level.DEBUG, "TRY 2 NG: " + newScpdURLString);
                newScpdURLString = Util.getAbsoluteURL(urlBaseString, scpdURLString);
                try {
                    scpdNode = getSCPDNode(newScpdURLString);
//logger.log(Level.DEBUG, "TRY 3 OK: " + newScpdURLString);
                } catch (IOException e3) {
//logger.log(Level.DEBUG, "TRY 3 NG: " + newScpdURLString);
                    newScpdURLString = rootDevice.getDescriptionFilePath() + scpdURLString;
                    File file = null;
                    try {
                        file = new File(newScpdURLString);
                        scpdNode = getSCPDNode(file);
//logger.log(Level.DEBUG, "TRY 4 OK: " + file);
                    } catch (IOException e4) {
logger.log(Level.DEBUG, "TRY 4 NG: " + file);
                        return null;
                    }
                }
            }
        }

        service.setSCPDNode(scpdNode);

        return scpdNode;
    }

    /** SCPD node */
    private static Node getSCPDNode(String urlString) throws SAXException, IOException {
        URLConnection uc = new URL(urlString).openConnection();
        DocumentBuilder parser = Util.getDocumentBuilder();
        return parser.parse(uc.getInputStream());
    }

    /** */
    private static Node getSCPDNode(File file) throws SAXException, IOException {
        DocumentBuilder parser = Util.getDocumentBuilder();
        return parser.parse(Files.newInputStream(file.toPath()));
    }
}
