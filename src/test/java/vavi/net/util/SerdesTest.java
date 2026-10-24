package vavi.net.util;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import vavi.net.util.Serdes.SerdesUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class SerdesTest {

    static final String NS1 = "urn:schemas-upnp-org:device-1-0";
    static final String NS2 = "urn:schemas-upnp-org:service-1-0";

    @Test
    void test1() throws Exception {
        assertEquals(SerdesUtil.prefix(NS1), SerdesUtil.prefix(NS1));
        assertNotEquals(SerdesUtil.prefix(NS1), SerdesUtil.prefix(NS2));
        assertEquals(NS1, SerdesUtil.getXPath().getNamespaceContext().getNamespaceURI(SerdesUtil.prefix(NS1)));
    }

    @Test
    void test2() throws Exception {
        String xml = "<a xmlns=\"" + NS1 + "\"><b>foo</b><c xmlns=\"" + NS2 + "\">bar</c></a>";
        Document document = Util.getDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

        assertEquals("foo", SerdesUtil.getChildNodeValue(document, NS1, "b"));
        assertEquals("bar", SerdesUtil.getChildNodeValue(document, NS2, "c"));
        assertEquals("", SerdesUtil.getChildNodeValue(document, NS1, "c"));
        assertTrue(SerdesUtil.isChildNodeExists(document, NS1, "b"));
        assertFalse(SerdesUtil.isChildNodeExists(document, NS2, "b"));
    }
}
