/*
 * MediaGate for CyberLink
 *
 * Copyright (C) Satoshi Konno 2004
 */

package org.cybergarage.mediagate.frame.swing;

import javax.swing.table.AbstractTableModel;

import org.cybergarage.upnp.media.server.Directory;
import org.cybergarage.upnp.media.server.object.ContentNode;
import org.cybergarage.upnp.media.server.object.item.ItemNode;


/**
 * ContentTable.
 *
 * @version 01/28/04 first revision.
 */
public class ContentTable extends AbstractTableModel {

    // Member
    private final String[] tableColum = {
        "title", "creator", "date", "size"
    };

    private Directory directory;

    // Constructor
    public ContentTable() {
        setDirectory(null);
    }

    // Directory
    public Directory getDirectory() {
        return directory;
    }

    public void setDirectory(Directory dir) {
        directory = dir;
    }

    public boolean hasDirectory() {
        return directory != null;
    }

    // ActionListener
    @Override
    public int getColumnCount() {
// Debug.message("getColumnCount = " + tableColum.length);
        return tableColum.length;
    }

    @Override
    public int getRowCount() {
// Debug.message("getRowCount = " + tableData.length);
        if (!hasDirectory()) {
            return 0;
        }
        return getDirectory().getChildNodes().getLength();
    }

    @Override
    public Object getValueAt(int row, int col) {
// Debug.message("getValueAt(" + row + "," + col + ") = " + tableData[row][col]);
        if (!hasDirectory()) {
            return "";
        }

        ContentNode cnode = getDirectory().findContentNodeByID(String.valueOf(row));
        if ((cnode instanceof ItemNode) == false) {
            return "";
        }

        ItemNode itemNode = (ItemNode) cnode;
        return switch (col) {
            case 0 -> itemNode.getTitle();
            case 1 -> itemNode.getCreator();
            case 2 -> itemNode.getDate();
            case 3 -> Long.toString(itemNode.getStorageUsed());
            default -> "";
        };
    }

    @Override
    public String getColumnName(int col) {
// Debug.message("getColumnName(" + col + ") = " + tableColum[col]);
        return tableColum[col];
    }

    // exit
    public void update(Directory dir) {
        setDirectory(dir);
    }
}
