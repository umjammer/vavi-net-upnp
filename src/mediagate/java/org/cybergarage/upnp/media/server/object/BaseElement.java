/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package org.cybergarage.upnp.media.server.object;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;


/**
 * BaseElement. 
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 050609 nsano initial version <br>
 */
public class BaseElement implements Element {

    /** */
    protected String prefix;

    /** */
    protected String namespaceURI;

    /** */
    protected String textContent;

    /** */
    protected Node parentNode;

    /** */
    protected short nodeType;

    /** */
    protected String nodeName;

    /** */
    protected String nodeValue;

    /** */
    protected List<Node> childNodes = new ArrayList<>();

    /** */
    protected Map<String, Attr> attributes = new HashMap<>();

    /** */
    private static class MyAttr extends BaseElement implements Attr {
        MyAttr(Element ownerElement, String name, String value) {
            this.nodeType = Node.ATTRIBUTE_NODE;
            this.parentNode = ownerElement;
            this.nodeName = name;
            this.nodeValue = value;
        }
        @Override
        public String getName() {
            return nodeName;
        }
        @Override
        public Element getOwnerElement() {
            return (Element) parentNode;
        }
        @Override
        public boolean getSpecified() {
            // TODO Auto-generated method stub
            return false;
        }
        @Override
        public String getValue() {
            return nodeValue;
        }
        @Override
        public boolean isId() {
            // TODO Auto-generated method stub
            return false;
        }
        @Override
        public void setValue(String value) throws DOMException {
            this.nodeValue = value;
        }
    }

    /* */
    @Override
    public String getNodeName() {
        return nodeName;
    }

    /* */
    @Override
    public String getNodeValue() throws DOMException {
        return nodeValue;
    }

    /* */
    @Override
    public void setNodeValue(String nodeValue) throws DOMException {
        this.nodeValue = nodeValue;
    }

    /* */
    @Override
    public short getNodeType() {
        return nodeType;
    }

    /* */
    @Override
    public Node getParentNode() {
        return parentNode;
    }

    /* */
    @Override
    public NodeList getChildNodes() {
        return new NodeList() {
            @Override
            public int getLength() {
                return childNodes.size();
            }
            @Override
            public Node item(int index) {
                return childNodes.get(index);
            }
        };
    }

    /* */
    @Override
    public Node getFirstChild() {
        return childNodes.get(0);
    }

    /* */
    @Override
    public Node getLastChild() {
        return childNodes.get(childNodes.size() - 1);
    }

    /** @see org.w3c.dom.Node#getPreviousSibling() */
    @Override
    public Node getPreviousSibling() {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see org.w3c.dom.Node#getNextSibling() */
    @Override
    public Node getNextSibling() {
        // TODO Auto-generated method stub
        return null;
    }

    /* */
    @Override
    public NamedNodeMap getAttributes() {
        return new NamedNodeMap() {

            @Override
            public int getLength() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public Node getNamedItem(String name) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Node getNamedItemNS(String namespaceURI, String localName) throws DOMException {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Node item(int index) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Node removeNamedItem(String name) throws DOMException {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Node removeNamedItemNS(String namespaceURI, String localName) throws DOMException {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Node setNamedItem(Node arg) throws DOMException {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Node setNamedItemNS(Node arg) throws DOMException {
                // TODO Auto-generated method stub
                return null;
            }
            
        };
    }

    /** @see org.w3c.dom.Node#getOwnerDocument() */
    @Override
    public Document getOwnerDocument() {
        // TODO Auto-generated method stub
        return null;
    }

    /* */
    @Override
    public Node insertBefore(Node newChild, Node refChild) throws DOMException {
        int index = childNodes.indexOf(refChild);
        if (index != -1) {
            childNodes.add(index, newChild);
            return newChild;
        } else {
            throw new DOMException((short) 0, "insertBefore");
        }
    }

    /* */
    @Override
    public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
        int index = childNodes.indexOf(oldChild);
        if (index != -1) {
            childNodes.set(index, newChild);
            return newChild;
        } else {
            throw new DOMException((short) 0, "replaceChild");
        }
    }

    /* */
    @Override
    public Node removeChild(Node oldChild) throws DOMException {
        int index = childNodes.indexOf(oldChild);
        if (index != -1) {
            childNodes.remove(index);
            return oldChild;
        } else {
            throw new DOMException((short) 0, "removeChild");
        }
    }

    /* */
    @Override
    public Node appendChild(Node newChild) throws DOMException {
        childNodes.add(newChild);
        return newChild;
    }

    /* */
    @Override
    public boolean hasChildNodes() {
        return !childNodes.isEmpty();
    }

    /** @see org.w3c.dom.Node#cloneNode(boolean) */
    @Override
    public Node cloneNode(boolean deep) {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see org.w3c.dom.Node#normalize() */
    @Override
    public void normalize() {
        // TODO Auto-generated method stub
    }

    /** @see org.w3c.dom.Node#isSupported(java.lang.String, java.lang.String) */
    @Override
    public boolean isSupported(String feature, String version) {
        // TODO Auto-generated method stub
        return false;
    }

    /* */
    @Override
    public String getNamespaceURI() {
        return namespaceURI;
    }

    /* */
    @Override
    public String getPrefix() {
        return prefix;
    }

    /* */
    @Override
    public void setPrefix(String prefix) throws DOMException {
        this.prefix = prefix;
    }

    /** @see org.w3c.dom.Node#getLocalName() */
    @Override
    public String getLocalName() {
        // TODO Auto-generated method stub
        return null;
    }

    /* */
    @Override
    public boolean hasAttributes() {
        return !attributes.isEmpty();
    }

    /** @see org.w3c.dom.Node#getBaseURI() */
    @Override
    public String getBaseURI() {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see org.w3c.dom.Node#compareDocumentPosition(org.w3c.dom.Node) */
    @Override
    public short compareDocumentPosition(Node other) throws DOMException {
        // TODO Auto-generated method stub
        return 0;
    }

    /* */
    @Override
    public String getTextContent() throws DOMException {
        return textContent;
    }

    /* */
    @Override
    public void setTextContent(String textContent) throws DOMException {
        this.textContent = textContent;
    }

    /** @see org.w3c.dom.Node#isSameNode(org.w3c.dom.Node) */
    @Override
    public boolean isSameNode(Node other) {
        // TODO Auto-generated method stub
        return false;
    }

    /** @see org.w3c.dom.Node#lookupPrefix(java.lang.String) */
    @Override
    public String lookupPrefix(String namespaceURI) {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see org.w3c.dom.Node#isDefaultNamespace(java.lang.String) */
    @Override
    public boolean isDefaultNamespace(String namespaceURI) {
        // TODO Auto-generated method stub
        return false;
    }

    /** @see org.w3c.dom.Node#lookupNamespaceURI(java.lang.String) */
    @Override
    public String lookupNamespaceURI(String prefix) {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see org.w3c.dom.Node#isEqualNode(org.w3c.dom.Node) */
    @Override
    public boolean isEqualNode(Node arg) {
        // TODO Auto-generated method stub
        return false;
    }

    /** */
    private final Map<String, Object> features = new HashMap<>();

    /* */
    @Override
    public Object getFeature(String feature, String version) {
        return features.get(feature + "/" + version);
    }

    /** */
    private final Map<String, Object> userData = new HashMap<>();

    /* TODO handler */
    @Override
    public Object setUserData(String key, Object data, UserDataHandler handler) {
        userData.put(key, data);
        return data;
    }

    /* */
    @Override
    public Object getUserData(String key) {
        return userData.get(key);
    }

    /** @see org.w3c.dom.Element#getTagName() */
    @Override
    public String getTagName() {
        // TODO Auto-generated method stub
        return null;
    }

    /* */
    @Override
    public String getAttribute(String name) {
        return attributes.get(name).getValue();
    }

    /* */
    @Override
    public void setAttribute(String name, String value) throws DOMException {
        attributes.put(name, new MyAttr(this, name, value));
    }

    /* */
    @Override
    public void removeAttribute(String name) throws DOMException {
        attributes.remove(name);
    }

    /* */
    @Override
    public Attr getAttributeNode(String name) {
        return attributes.get(name);
    }

    /* */
    @Override
    public Attr setAttributeNode(Attr newAttr) throws DOMException {
        attributes.put(newAttr.getName(), newAttr);
        return newAttr;
    }

    /** @see org.w3c.dom.Element#removeAttributeNode(org.w3c.dom.Attr) */
    @Override
    public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
        if (attributes.containsValue(oldAttr)) {
            return null; // TODO
        } else {
            return null;
        }
    }

    /** @see org.w3c.dom.Element#getElementsByTagName(java.lang.String) */
    @Override
    public NodeList getElementsByTagName(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see org.w3c.dom.Element#getAttributeNS(java.lang.String, java.lang.String) */
    @Override
    public String getAttributeNS(String namespaceURI, String localName) throws DOMException {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see org.w3c.dom.Element#setAttributeNS(java.lang.String, java.lang.String, java.lang.String) */
    @Override
    public void setAttributeNS(String namespaceURI, String qualifiedName, String value) throws DOMException {
        // TODO Auto-generated method stub
        
    }

    /** @see org.w3c.dom.Element#removeAttributeNS(java.lang.String, java.lang.String) */
    @Override
    public void removeAttributeNS(String namespaceURI, String localName) throws DOMException {
        // TODO Auto-generated method stub
        
    }

    /** @see org.w3c.dom.Element#getAttributeNodeNS(java.lang.String, java.lang.String) */
    @Override
    public Attr getAttributeNodeNS(String namespaceURI, String localName) throws DOMException {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see org.w3c.dom.Element#setAttributeNodeNS(org.w3c.dom.Attr) */
    @Override
    public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see org.w3c.dom.Element#getElementsByTagNameNS(java.lang.String, java.lang.String) */
    @Override
    public NodeList getElementsByTagNameNS(String namespaceURI, String localName) throws DOMException {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see org.w3c.dom.Element#hasAttribute(java.lang.String) */
    @Override
    public boolean hasAttribute(String name) {
        // TODO Auto-generated method stub
        return false;
    }

    /** @see org.w3c.dom.Element#hasAttributeNS(java.lang.String, java.lang.String) */
    @Override
    public boolean hasAttributeNS(String namespaceURI, String localName) throws DOMException {
        // TODO Auto-generated method stub
        return false;
    }

    /** @see org.w3c.dom.Element#getSchemaTypeInfo() */
    @Override
    public TypeInfo getSchemaTypeInfo() {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see org.w3c.dom.Element#setIdAttribute(java.lang.String, boolean) */
    @Override
    public void setIdAttribute(String name, boolean isId) throws DOMException {
        throw new UnsupportedOperationException("setIdAttribute");
    }

    /** @see org.w3c.dom.Element#setIdAttributeNS(java.lang.String, java.lang.String, boolean) */
    @Override
    public void setIdAttributeNS(String namespaceURI, String localName, boolean isId) throws DOMException {
        throw new UnsupportedOperationException("setIdAttributeNS");
    }

    /** @see org.w3c.dom.Element#setIdAttributeNode(org.w3c.dom.Attr, boolean) */
    @Override
    public void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException {
        throw new UnsupportedOperationException("setIdAttributeNode");
    }
}
