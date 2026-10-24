/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.util;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;


/**
 * Serdes.
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 050921 nsano initial version <br>
 */
public interface Serdes<T, I, O> {

    void serialize(T type, O output) throws IOException;

    void deserialize(I input, T type) throws IOException;

    final class SerdesUtil {

        /** namespace uri -> prefix */
        private static final Map<String, String> prefixes = new ConcurrentHashMap<>();

        /** prefix -> namespace uri */
        private static final Map<String, String> namespaces = new ConcurrentHashMap<>();

        /** for generating prefixes */
        private static final AtomicInteger prefixCount = new AtomicInteger();

        /** */
        private static final NamespaceContext namespaceContext = new NamespaceContext() {
            @Override
            public String getNamespaceURI(String prefix) {
                return namespaces.getOrDefault(prefix, XMLConstants.NULL_NS_URI);
            }

            @Override
            public String getPrefix(String namespaceURI) {
                return prefixes.get(namespaceURI);
            }

            @Override
            public Iterator<String> getPrefixes(String namespaceURI) {
                String prefix = prefixes.get(namespaceURI);
                return prefix == null ? Collections.emptyIterator() : List.of(prefix).iterator();
            }
        };

        /** XPath, a XPath is not thread safe */
        private static final ThreadLocal<XPath> xpath = ThreadLocal.withInitial(() -> {
            XPath xpath = XPathFactory.newInstance().newXPath();
            xpath.setNamespaceContext(namespaceContext);
            return xpath;
        });

        /**
         * Returns the XPath prefix bound to the namespace uri, binds a new one if not exists.
         * a namespace uri itself cannot be used as a prefix in a XPath expression.
         */
        public static String prefix(String xmlns) {
            return prefixes.computeIfAbsent(xmlns, k -> {
                String prefix = "ns" + prefixCount.getAndIncrement();
                namespaces.put(prefix, k);
                return prefix;
            });
        }

        /** XPath, for the current thread */
        public static XPath getXPath() {
            return xpath.get();
        }

        /** */
        public static String getChildNodeValue(Node target, String xmlns, String localName) {
            try {
                return getXPath().evaluate(".//" + prefix(xmlns) + ":" + localName, target);
            } catch (XPathExpressionException e) {
                throw new IllegalStateException(e);
            }
        }

        /** */
        public static boolean isChildNodeExists(Node target, String xmlns, String localName) {
            try {
                return (Boolean) getXPath().evaluate(".//" + prefix(xmlns) + ":" + localName, target, XPathConstants.BOOLEAN);
            } catch (XPathExpressionException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
