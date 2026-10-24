/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.util;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPFactory;
import jakarta.xml.soap.SOAPMessage;

import vavi.net.http.HttpContext;
import vavi.net.http.HttpProtocol;
import vavi.net.http.HttpUtil;


/**
 * SOAPRequest. 
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 050914 nsano initial version <br>
 */
public abstract class SOAPRequest extends HttpContext {

    private static final Logger logger = System.getLogger(SOAPRequest.class.getName());

    /** */
    protected static final MessageFactory messageFactory;

    /** */
    protected static final SOAPFactory soapFactory;

    /** SOAP message */
    protected SOAPMessage soapMessage;

    /** Gets the SOAP message. */
    public SOAPMessage getSoapMessage() {
        return soapMessage;
    }

    static {
        messageFactory = SoapUtil.getMessageFactory();
        soapFactory = SoapUtil.getSOAPFactory();
    }

    /** Creates a SOAP request. */
    public SOAPRequest() {
        this.protocol = new HttpProtocol();
        ((HttpProtocol) this.protocol).setHttp11(true);
    }

    /**
     * Creates a SOAP request.
     * @see #soapMessage
     * @throws IllegalArgumentException when SOAPException occurs. 
     */
    public SOAPRequest(HttpServletRequest request) {
        HttpUtil.copy(request, this);
        try {
logger.log(Level.DEBUG, "-------- request: " + is.available() + " bytes");
            soapMessage = SoapUtil.getSOAPMessage(headers, SoapUtil.getFixedInputStream(is));
soapMessage.writeTo(System.out);
logger.log(Level.DEBUG, "\n--------");
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } catch (SOAPException e) {
            throw new IllegalArgumentException(e);
        }
    }

    //----

    /**
     * Sets a header value named "SOAPACTION". 
     */
    protected void setSOAPAction(String soapAction) {
        setHeader("SOAPACTION", soapAction);
    }

    /**
     *  Gets a header value named "SOAPACTION".
     */
    public String getSOAPAction() {
        return getHeader("SOAPACTION");
    }
}
