/*
 * Copyright (c) 2026 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.upnp.di;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;
import vavi.net.upnp.Device;
import vavi.util.Singleton;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;


/**
 * DeviceSerdesTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2026-10-24 nsano initial version <br>
 */
class DeviceSerdesTest {

    static final DeviceSerdes deviceSerdes = Singleton.getInstance(DeviceSerdes.class);

    static final String MINIMUM = """
            <?xml version="1.0"?>
            <root xmlns="%s">
              <specVersion><major>1</major><minor>0</minor></specVersion>
              <device>
                <deviceType>urn:schemas-upnp-org:device:Basic:1</deviceType>
                <friendlyName>minimum</friendlyName>
                <UDN>uuid:1</UDN>
              </device>
            </root>
            """;

    static Device deserialize(String xml) throws Exception {
        Device device = new Device();
        deviceSerdes.deserialize(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)), device);
        return device;
    }

    @Test
    void testDefaultNamespace() throws Exception {
        Device device = deserialize(MINIMUM.formatted(Device.XMLNS));
        assertEquals("minimum", device.getFriendlyName());
        assertEquals("uuid:1", device.getUDN());
        assertEquals("urn:schemas-upnp-org:device:Basic:1", device.getDeviceType());
    }

    @Test
    void testPrefixedNamespace() throws Exception {
        String xml = """
                <?xml version="1.0"?>
                <u:root xmlns:u="urn:schemas-upnp-org:device-1-0">
                  <u:device>
                    <u:friendlyName>prefixed</u:friendlyName>
                  </u:device>
                </u:root>
                """;
        Device device = deserialize(xml);
        assertEquals("prefixed", device.getFriendlyName());
    }

    @Test
    void testRealWorld() throws Exception {
        Device device = new Device();
        try (InputStream is = DeviceSerdesTest.class.getResourceAsStream("mediarenderer.xml")) {
            deviceSerdes.deserialize(is, device);
        }
        assertEquals("Living Room TV", device.getFriendlyName());
        assertEquals("urn:schemas-upnp-org:device:MediaRenderer:1", device.getDeviceType());
        assertEquals("vavi", device.getManufacture());
        assertEquals("TV-1", device.getModelName());
        assertEquals("uuid:00000000-0000-0000-0000-000000000001", device.getUDN());

        assertEquals(1, device.getIconList().size());
        assertEquals("/icon.png", device.getIconList().get(0).getURL());
        assertEquals("image/png", device.getIconList().get(0).getMimeType());

        assertEquals(1, device.getServiceList().size());
        assertEquals("urn:schemas-upnp-org:service:RenderingControl:1", device.getServiceList().get(0).getServiceType());
        assertEquals("/RenderingControl.xml", device.getServiceList().get(0).getSCPDURL());
        assertEquals("/RenderingControl/control", device.getServiceList().get(0).getControlURL());

        assertEquals(1, device.getChildDevices().size());
        Device child = device.getChildDevices().get(0);
        assertEquals("Embedded", child.getFriendlyName());
        assertEquals("uuid:00000000-0000-0000-0000-000000000002", child.getUDN());
    }

    /** same path as ControlPoint#addDevice */
    @Test
    void testInjectUrl() throws Exception {
        URL url = DeviceSerdesTest.class.getResource("mediarenderer.xml");
        Device device = new Device();
        DeviceFactory.inject(url, device);
        assertEquals("Living Room TV", device.getFriendlyName());
    }

    /** search responses and notifies are received by several threads */
    @Test
    void testConcurrent() throws Exception {
        ExecutorService es = Executors.newFixedThreadPool(8);
        try {
            List<Future<String>> futures = new ArrayList<>();
            for (int i = 0; i < 200; i++) {
                String name = "device" + i;
                futures.add(es.submit(() -> deserialize(MINIMUM.formatted(Device.XMLNS).replace("minimum", name)).getFriendlyName()));
            }
            for (int i = 0; i < futures.size(); i++) {
                assertEquals("device" + i, futures.get(i).get());
            }
        } finally {
            es.shutdown();
        }
    }

    /** a description served by a device of this library must be read by a control point of this library */
    @Test
    void testRoundTrip() throws Exception {
        Device device = new Device();
        try (InputStream is = DeviceSerdesTest.class.getResourceAsStream("mediarenderer.xml")) {
            deviceSerdes.deserialize(is, device);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        deviceSerdes.serialize(device, baos);
        String xml = baos.toString(StandardCharsets.UTF_8);
        assertFalse(xml.contains("xmlns=\"\""), xml);

        Device actual = deserialize(xml);
        assertEquals(device.getFriendlyName(), actual.getFriendlyName());
        assertEquals(device.getUDN(), actual.getUDN());
        assertEquals(device.getServiceList().size(), actual.getServiceList().size());
        assertEquals(device.getServiceList().get(0).getControlURL(), actual.getServiceList().get(0).getControlURL());
        assertEquals(device.getIconList().size(), actual.getIconList().size());
        assertEquals(device.getIconList().get(0).getURL(), actual.getIconList().get(0).getURL());
        assertEquals(device.getChildDevices().size(), actual.getChildDevices().size());
        assertEquals(device.getChildDevices().get(0).getUDN(), actual.getChildDevices().get(0).getUDN());
    }

    /** the description of the reported issue, elements are not in the upnp namespace */
    @Test
    void testNoNamespace() {
        String xml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <root xmlns="urn:schemas-upnp-org:device-1-0">
                   <device xmlns="">
                      <friendlyName>CyberGarage Light Device</friendlyName>
                   </device>
                </root>
                """;
        assertThrows(IllegalArgumentException.class, () -> deserialize(xml));
    }

    @Test
    void testNotDescription() {
        assertThrows(IllegalArgumentException.class, () -> deserialize("<html><body>404</body></html>"));
    }
}
