/*
 * JPDFSigner - Sign PDFs online using smartcards (GUIAttachmentBarList.java)
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
package de.rub.dez6a3.jpdfsigner.view;

import de.rub.dez6a3.jpdfsigner.control.XMLAttachmentHandler;
import de.rub.dez6a3.jpdfsigner.control.language.LanguageFactory;
import de.rub.dez6a3.jpdfsigner.model.IAttachmentHandler;
import de.rub.dez6a3.jpdfsigner.model.IPDFProcessor;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.plaf.basic.BasicRootPaneUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.apache.log4j.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 *
 * @author dan
 */
public class GUIAttachmentBarList extends JPanel {

    private IPDFProcessor pdfHandler = null;
    private DefaultTableModel tableModel = null;
    private JLabel labelDesc = null;
    private RSyntaxTextArea tfield = null;
    private boolean corruptXml = true;
    private JTable table = null;
    private Object[] xmlViewers = new Object[2];
    public static Logger log = Logger.getLogger(GUIAttachmentBarList.class);

    public GUIAttachmentBarList(IPDFProcessor pdfHandler) {
        try {
            this.pdfHandler = pdfHandler;
            setupView();
            setupListView();
            aHandlers.add(new XMLAttachmentHandler()); //register attachmenthandlers
        } catch (Exception e) {
            log.error(e);
        }
    }

    private void setupView() {
        Dimension thisdim = new Dimension(250, 150);
        setPreferredSize(thisdim);
        setSize(thisdim);
        setBorder(null);
        setLayout(new BorderLayout());
        setBackground(Color.white);
    }

    private void setupListView() {
        tfield = new RSyntaxTextArea();
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Dateiname");
        tableModel.addColumn("Beschreibung");
        tableModel.addColumn("Größe");

        table = new JTable(tableModel) {

            private Color shineColor = new Color(50, 100, 200, 50);
            private Color scanLineColor = new Color(200, 200, 200, 50);

            @Override
            public void paint(Graphics g) {
                GradientPaint gp = new GradientPaint(getWidth() / 2, 0, new Color(30, 60, 100, 25), getWidth(), getHeight(), new Color(50, 100, 140, 5), false);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(gp);
                Polygon poly = new Polygon();
                poly.addPoint(getWidth() / 3, getHeight());
                poly.addPoint(getWidth() / 2, 0);
                poly.addPoint(getWidth(), 0);
                poly.addPoint(getWidth(), getHeight());


                Point p1 = new Point(getWidth() / 3, getHeight());
                Point p2 = new Point(getWidth() / 2, 0);
                Point p3 = new Point(0, 0);
                Point p4 = new Point(getWidth(), 0);

                while (p2.x < getWidth()) {
                    g2.setColor(shineColor);
                    g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                    g2.setColor(scanLineColor);
                    g2.drawLine(p3.x, p3.y, p4.x, p4.y);
                    p1.translate(4, 0);
                    p2.translate(3, 0);
                    p3.translate(0, 2);
                    p4.translate(0, 2);
                }
                super.paint(g2);
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        xmlViewers[0] = tfield;
        xmlViewers[1] = table;
        table.addMouseListener(new MouseAdapter() {

            public void mouseReleased(MouseEvent e) {
                try {
                    JTable table = (JTable) e.getSource();
                    Object[] rowData = (Object[]) tableModel.getValueAt(table.getSelectedRow(), 0);
                    IAttachmentHandler aHandler = (IAttachmentHandler) rowData[1];
                    if (aHandler != null) {
                        aHandler.setAttachmentData((byte[]) rowData[2]);
                        aHandler.showAttachment(xmlViewers);
                    } else {
                        DecoratedJOptionPane.showMessageDialog(null, "Dieses Dateiformat kann nicht angezeigt sondern nur gespeichert werden.", "Fehler", DecoratedJOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception ex) {
                }
            }
        });
        table.setOpaque(false);
        table.setDefaultRenderer(Object.class, new AttachmentListCellRenderer());
        table.getTableHeader().setVisible(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(Color.white);
        table.setFillsViewportHeight(true);
        table.setFont(new Font("Arial", Font.PLAIN, 11));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        table.setRowHeight(30);
        JScrollPane tableScroller = new JScrollPane();
        tableScroller.getViewport().setBackground(Color.white);
        tableScroller.setBorder(BorderFactory.createEmptyBorder());
        tableScroller.setViewportView(table);
        add(tableScroller, BorderLayout.CENTER);
    }

    public boolean areAttachmentsValid() {
        return true;
    }

    public JLabel getDescriptionLabel() {
        return labelDesc;
    }

    public void setBarViewController(List<JPanel> viewController) {
    }

    public RSyntaxTextArea getSyntaxTextArea() {
        return tfield;
    }
    private ArrayList<IAttachmentHandler> aHandlers = new ArrayList<IAttachmentHandler>();

    public void loadAttachments() throws IOException {
        tableModel.getDataVector().clear();
        table.repaint();
        labelDesc = new JLabel();
        tfield.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
        tfield.setFont(new Font("Arial", Font.PLAIN, 13));
        tfield.setTextAntiAliasHint("VALUE_TEXT_ANTIALIAS_ON");
        tfield.setHyperlinksEnabled(true);
        tfield.setEditable(false);
        try {
            log.info("Loading PDF-attachments...");
            ArrayList<Hashtable> attachments = pdfHandler.getAttachments();

            for (Hashtable current : attachments) {
                String filename = (String) current.get(IPDFProcessor.ATTACHMENT_FILENAME_STRING);
                String desc = (String) current.get(IPDFProcessor.ATTACHMENT_DESCRIPTION_STRING);
                Integer size = (Integer) current.get(IPDFProcessor.ATTACHMENT_SIZE_INT);
                String strSize = "-";
                DecimalFormat df = new DecimalFormat("0.000");
                if (size > 1000000000) {
                    strSize = df.format(((float) size) / 1000000000f) + " GB";
                } else if (size > 1000000) {
                    strSize = df.format(((float) size) / 1000000f) + " MB";
                } else {
                    strSize = df.format(((float) size) / 1000f) + " KB";
                }
                Object[] columnOne = new Object[3];
                columnOne[0] = filename;

                for (IAttachmentHandler aHandler : aHandlers) {
                    for (String handledFileType : aHandler.getHandledFileTypes()) {
                        if (filename.toLowerCase().endsWith(handledFileType)) {
                            columnOne[1] = aHandler;
                            columnOne[2] = (byte[]) current.get(IPDFProcessor.ATTACHMENT_BYTES_ARR);
                            aHandler.setAttachmentData((byte[]) columnOne[2]);
                            log.info(filename + " - handled by: " + XMLAttachmentHandler.class);
                            if (filename.equals("out.xml")) {
                                aHandler.showAttachment(xmlViewers);
                            }
                        }
                    }
                }
                if (columnOne[1] == null) {
                    log.info(filename + " - no attachmenthandler found");
                }
                tableModel.addRow(new Object[]{columnOne, desc, strSize});
                log.info(filename + " - Loading done!");
            }
            log.info("Loading PDF-attachments done!");
        } catch (Exception e) {
            log.info("No attachments found");
        }
    }
}
