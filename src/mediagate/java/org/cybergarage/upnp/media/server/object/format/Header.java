/*
 * MediaServer for CyberLink
 *
 * Copyright (C) Satoshi Konno 2003
 */

package org.cybergarage.upnp.media.server.object.format;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.file.Files;


/**
 * Header
 * 
 * @version 12/04/03 first revision.
 */
public abstract class Header {

    private static final Logger logger = System.getLogger(Header.class.getName());

    // Header ID

    public static byte[] getID(InputStream inputStream, int headerOffset, int headerSize) {
        int headerCnt = headerOffset + headerSize;
        byte[] header = new byte[headerCnt];
        try {
            DataInputStream dataIn = new DataInputStream(inputStream);
            for (int n = 0; n < headerCnt; n++) {
                byte readByte = dataIn.readByte();
                if (n < headerOffset) {
                    continue;
                }
                header[n - headerOffset] = readByte;
            }
            dataIn.close();
        } catch (EOFException eofe) {
        } catch (Exception e) {
            logger.log(Level.ERROR, e.getMessage(), e);
        }
        return header;
    }

    public static byte[] getID(InputStream inputStream, int headerSize) {
        return getID(inputStream, 0, headerSize);
    }

    public static String getIDString(InputStream inputStream, int headerOffset, int headerSize) {
        return new String(getID(inputStream, headerOffset, headerSize));
    }

    public static String getIDString(InputStream inputStream, int headerSize) {
        return new String(getID(inputStream, headerSize));
    }

    public static byte[] getID(File file, int headerSize) {
        try {
            return getID(Files.newInputStream(file.toPath()), headerSize);
        } catch (Exception e) {
            logger.log(Level.DEBUG, e);
            return new byte[0];
        }
    }

    public static String getIDString(File file, int headerOffset, int headerSize) {
        try {
            return getIDString(Files.newInputStream(file.toPath()), headerOffset, headerSize);
        } catch (Exception e) {
            logger.log(Level.DEBUG, e);
            return "";
        }
    }

    public static String getIDString(File file, int headerSize) {
        return getIDString(file, 0, headerSize);
    }

    // Suffix

    public static String getSuffix(File file) {
        String fname = file.getName();
        int idx = fname.lastIndexOf(".");
        if (idx < 0) {
            return "";
        }
        return fname.substring(idx + 1);
    }
}
