/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;
import java.util.Map.Entry;

import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.MimeHeaders;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPFactory;
import jakarta.xml.soap.SOAPMessage;

import vavi.net.http.HttpContext;
import vavi.net.http.HttpUtil;


/**
 * SoapUtil. 
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 050930 nsano initial version <br>
 */
public final class SoapUtil {

    private static final Logger logger = System.getLogger(SoapUtil.class.getName());

    /** SOAP */
    private static MessageFactory mf;

    /** SOAP */
    private static SOAPFactory sf;

    static {
        try {
            mf = MessageFactory.newInstance();
        } catch (SOAPException e) {
logger.log(Level.DEBUG, e.getMessage(), e);
        }
        try {
            sf = SOAPFactory.newInstance();
        } catch (SOAPException e) {
logger.log(Level.DEBUG, e.getMessage(), e);
        }
    }

    /** SOAP */
    public static MessageFactory getMessageFactory() {
        return mf;
    }

    /** SOAP */
    public static SOAPFactory getSOAPFactory() {
        return sf;
    }

    //----

    /**
     * Requests SOAP message.
     * (for client)
     */
    public static HttpContext postSoapRequest(SOAPRequest request) throws IOException {

logger.log(Level.DEBUG, ">>>> POST " + request.getClass() + ": " + request.getRemoteHost() + ":" + request.getRemotePort() + ": ");
        Socket socket = new Socket(request.getRemoteHost(), request.getRemotePort());
        OutputStream os = socket.getOutputStream();
        InputStream is = socket.getInputStream();

        request.setLocalHost(((InetSocketAddress) socket.getLocalSocketAddress()).getHostName());
        request.setLocalPort(((InetSocketAddress) socket.getLocalSocketAddress()).getPort());

        // soap temporary
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        try {
            SOAPMessage soapMessage = request.getSoapMessage();
            soapMessage.writeTo(ps);
        } catch (SOAPException e) {
            socket.close();
            throw new IOException(e);
        }
        ps.flush();
        // header: content-length
        byte[] soap = baos.toByteArray();
        request.setHeader("content-length", String.valueOf(soap.length));
        request.setHeader("content-type", "text/xml");

        // request temporary
        baos = new ByteArrayOutputStream();
        ps = new PrintStream(baos);
        HttpUtil.printRequestHeader(ps, request);
        ps.write(soap);
        ps.flush();

        // request
logger.log(Level.DEBUG, "-------- soap request: " + baos.size() + " bytes");
logger.log(Level.DEBUG, baos);
        os.write(baos.toByteArray());
        os.flush();
logger.log(Level.DEBUG, "--------");

        // response header
//logger.log(Level.DEBUG, "waiting for response: " + is.available() + " bytes");
        HttpContext response = new HttpContext();
        response.setRemoteHost(((InetSocketAddress) socket.getRemoteSocketAddress()).getHostName());
        response.setRemotePort(((InetSocketAddress) socket.getRemoteSocketAddress()).getPort());
        response.setLocalHost(((InetSocketAddress) socket.getLocalSocketAddress()).getHostName());
        response.setLocalPort(((InetSocketAddress) socket.getLocalSocketAddress()).getPort());
        HttpUtil.parseResponseHeader(is, response);

        // response content
        String lengthString = response.getHeader("content-length");
        if (lengthString != null) {
            // has content-length
            int length = Integer.parseInt(lengthString);
            if (length > 0) {
                response.setInputStream(getFixedInputStream3(is, Integer.parseInt(lengthString)));
            } else {
                response.setInputStream(null);
logger.log(Level.DEBUG, "content length is 0");
            }
        } else {
            // not have content-length
            response.setInputStream(getFixedInputStream2(is));
        }

        socket.close();
logger.log(Level.DEBUG, ">>>> " + request.getClass() + " POST done");

        return response;
    }

    /**
     * Responses SOAP message.
     * (for server)
     */
    public static void postSoapResponse(SOAPResponse response) throws IOException {
        OutputStream os = response.getOutputStream();

        // request temporary
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        try {
            SOAPMessage soapMessage = response.getSoapMessage();
            soapMessage.writeTo(ps);
        } catch (SOAPException e) {
            throw new IOException(e);
        }
        ps.flush();

        // request
//logger.log(Level.DEBUG, "-------- soap response: " + baos.size() + " bytes: " + response.getOutputStream());
        os.write(baos.toByteArray());
        os.flush(); // write status line, headers
//logger.log(Level.DEBUG, new String(baos.toByteArray()));
//logger.log(Level.DEBUG, "--------");
    }

    /**
     * copy, cause MessageFactory#createMessage() may need InputStream#available()
     * (for client)
     */
    private static InputStream getFixedInputStream3(InputStream is, int length) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while (length > 0) {
            int c = is.read();
            if (c == -1) {
                break;
            }
            baos.write(c);
            length--;
        }
logger.log(Level.DEBUG, "-------- soap response: " + baos.size() + " bytes");
logger.log(Level.DEBUG, baos);
logger.log(Level.DEBUG, "--------");
        return new ByteArrayInputStream(baos.toByteArray());
    }

    /**
     * copy, cause MessageFactory#createMessage() may need InputStream#available()
     * (for client)
     */
    private static InputStream getFixedInputStream2(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while (true) {
            int c = is.read();
            if (c == -1) {
                break;
            }
            baos.write(c);
        }
logger.log(Level.DEBUG, "-------- soap response: " + baos.size() + " bytes");
logger.log(Level.DEBUG, baos);
logger.log(Level.DEBUG, "--------");
        return new ByteArrayInputStream(baos.toByteArray());
    }

    /**
     * copy, cause MessageFactory#createMessage() may need InputStream#available()
     * (for server)
     */
    public static InputStream getFixedInputStream(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[is.available()];
        while (is.available() > 0) {
            int r = is.read(buffer, 0, is.available());
            baos.write(buffer, 0, r);
        }
        return new ByteArrayInputStream(baos.toByteArray());
    }

    /**
     * Parses SOAP message from InputStream with headers.
     * @param headerMap headers
     * @param is InputStream
     * (for client) 
     */
    public static SOAPMessage getSOAPMessage(Map<String, String> headerMap, InputStream is) throws SOAPException, IOException {
        MimeHeaders mimeHeaders = new MimeHeaders();
        for (Entry<String, String> entry : headerMap.entrySet()) {
            mimeHeaders.addHeader(entry.getKey(), entry.getValue());
        }

        SOAPMessage soapMessage = mf.createMessage(mimeHeaders, is);
        soapMessage.setProperty(SOAPMessage.WRITE_XML_DECLARATION, "true");
//soapMessage.writeTo(System.out);

        return soapMessage;
    }
}
