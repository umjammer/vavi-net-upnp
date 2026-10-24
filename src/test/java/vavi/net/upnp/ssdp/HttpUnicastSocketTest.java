/*
 * Copyright (c) 2026 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.upnp.ssdp;

import java.net.BindException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;


/**
 * HttpUnicastSocketTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2026-10-24 nsano initial version <br>
 */
class HttpUnicastSocketTest {

    /**
     * search responses are unicast, when two control points share the port,
     * only one of them receives a response. (e.g. remotectrl and tv samples)
     */
    @Test
    void testPortIsNotShared() throws Exception {
        HttpUnicastSocket socket1 = new HttpUnicastSocket("127.0.0.1", 0);
        try {
            int port = socket1.getDatagramSocket().getLocalPort();
            assertThrows(BindException.class, () -> new HttpUnicastSocket("127.0.0.1", port));
        } finally {
            socket1.close();
        }
    }
}
