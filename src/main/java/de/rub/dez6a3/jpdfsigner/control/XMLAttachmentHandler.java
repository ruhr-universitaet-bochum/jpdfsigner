/*
 * JPDFSigner - Sign PDFs online using smartcards (XMLAttachmentHandler.java)
 * Copyright (C) 2013  Ruhr-Universitaet Bochum - Daniel Moczarski, Haiko te Neues
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/gpl.txt>.
 *
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rub.dez6a3.jpdfsigner.control;

import de.rub.dez6a3.jpdfsigner.model.IAttachmentHandler;
import de.rub.dez6a3.jpdfsigner.view.AttachmentListCellRenderer;
import java.awt.Component;
import java.io.StringReader;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.apache.log4j.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 *
 * @author dan
 */
public class XMLAttachmentHandler implements IAttachmentHandler {

    private String[] handledFileTypes = new String[]{"xml"};
    private String xml = null;
    public static Logger log = Logger.getLogger(XMLAttachmentHandler.class);

    public void setAttachmentData(byte[] attachBytes) {
        xml = new String(attachBytes);
        try {
            xml = parseXML(xml);
        } catch (Exception e) {
            log.warn("Cannot parse XML - Displaying plain text without parsing");
        }
    }

    public void showAttachment(Object[] viewers) {
        RSyntaxTextArea textArea = (RSyntaxTextArea) viewers[0];
        textArea.setText(xml);
        JTable table = (JTable) viewers[1];
        AttachmentListCellRenderer cellRenderer = (AttachmentListCellRenderer) table.getCellRenderer(table.getSelectedRow(), 0);
        cellRenderer.setViewing(table.getSelectedRow());
        table.repaint();
    }

    public String[] getHandledFileTypes() {
        return handledFileTypes;
    }

    private String parseXML(String xmlStr) throws JDOMException, Exception {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new StringReader(xmlStr));
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        return outputter.outputString(doc);
    }
}
