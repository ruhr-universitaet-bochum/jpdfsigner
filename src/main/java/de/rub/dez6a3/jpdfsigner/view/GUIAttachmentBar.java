/*
 * JPDFSigner - Sign PDFs online using smartcards (GUIAttachmentBar.java)
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

import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfReader;
import de.rub.dez6a3.jpdfsigner.control.Configurator;
import de.rub.dez6a3.jpdfsigner.model.IPDFProcessor;
import java.awt.BorderLayout;
import java.awt.Image;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JScrollPane;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import de.rub.dez6a3.jpdfsigner.control.RadialGradientPaint;
import de.rub.dez6a3.jpdfsigner.view.UI.LAFProperties;
import java.awt.GradientPaint;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

/**
 *
 * @author dan
 */
public class GUIAttachmentBar extends JPanel {

    private int attachmentCount;
    private IPDFProcessor pdfHandler;
    private RSyntaxTextArea tfield;
    private JLabel labelDesc;
    private boolean signable = false;
    private ArrayList<AttachmentsBGLayer> allIcons;
    private Configurator config;
    private List<JPanel> barViewControl = null;

    public GUIAttachmentBar(IPDFProcessor pdfHandler) {
        this.pdfHandler = pdfHandler;
        setBackground(new Color(0, 0, 0, 0));
        setOpaque(false);
        config = Configurator.getInstance();
        setBorder(null);
    }

    public void setBarViewController(List<JPanel> param) {
        barViewControl = param;
    }

    @Override
    public void setVisible(boolean param) {
        if (param) {
            for (JPanel currBar : barViewControl) {
                currBar.setVisible(false);
            }
        }
        super.setVisible(param);
    }

    public void loadAttachments() {
        tfield = new RSyntaxTextArea();
        labelDesc = new JLabel();
        tfield.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
        tfield.setFont(new Font("Arial", Font.PLAIN, 13));
        tfield.setTextAntiAliasHint("VALUE_TEXT_ANTIALIAS_ON");
        tfield.setHyperlinksEnabled(true);
        tfield.setEditable(false);
        allIcons = new ArrayList<AttachmentsBGLayer>();
        JScrollPane scroller = new JScrollPane();
        scroller.setBackground(new Color(0, 0, 0, 0));
        scroller.setOpaque(false);
        scroller.setBorder(null);
        scroller.setViewportBorder(null);
        JPanel content = new JPanel() {

            public void paint(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(getWidth() / 3, 0, LAFProperties.getInstance().getBluePanelColorE(), getWidth(), 0, LAFProperties.getInstance().getBluePanelColorW());
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setColor(LAFProperties.getInstance().getBluePanelHighlightColor());
                Polygon hl = new Polygon();
                hl.addPoint(200, 0);
                hl.addPoint(getWidth(), 0);
                hl.addPoint(getWidth(), getHeight());
                hl.addPoint(135, getHeight());
                g2.fillPolygon(hl);

                g2.setColor(LAFProperties.getInstance().getBorderColor());
                g2.drawLine(0, 0, getWidth(), 0);
                g2.setColor(LAFProperties.getInstance().getBluePanelBorderHighlightColor());
                g2.drawLine(0, 1, getWidth(), 1);

                super.paint(g);
            }
        };
        Dimension contentDim = new Dimension(50, 110);
        content.setPreferredSize(contentDim);
        content.setSize(contentDim);
        content.setOpaque(false);
        content.setBackground(new Color(0, 0, 0, 0));

        content.setLayout(new FlowLayout(FlowLayout.CENTER));
        scroller.setViewportView(content);
        content.setAlignmentX(0);
        setLayout(new BorderLayout());
        add(scroller, BorderLayout.CENTER);

        try {
            ArrayList<Hashtable> attachments = pdfHandler.getAttachments();
            attachmentCount = attachments.size();
            int col = 0;
            int count = 0; //um zu entscheidne wenn der letzte separator angefügt wird (ganz unten soll keiner hin)

            for (Hashtable currAttachment : attachments) {
                PRStream stream = (PRStream) currAttachment.get("pdfobject");
                byte[] contentBytes = PdfReader.getStreamBytes(stream);
                String stringAttach = new String(contentBytes);
                if (currAttachment.get("filename").equals("out.xml") && attachmentCount == 1) {
                    String parsedXML = "";
                    try {
                        parsedXML = parseXML(stringAttach);
                        signable = true;
                    } catch (Exception e) {
                        parsedXML = stringAttach;
                    }
                    tfield.setText(parsedXML);
                    labelDesc.setText(currAttachment.get("description").toString());
                }


                JLabel separator = new JLabel(new ImageIcon(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/seperator.png")));
                Image scaled = new ImageIcon(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/attachment.png")).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
                AttachmentsBGLayer panel = new AttachmentsBGLayer(new ImageIcon(scaled), currAttachment.get("filename").toString(), currAttachment.get("description").toString(), stringAttach);
                allIcons.add(panel);


                content.add(panel);
                col++;
                count++;
                if (count < attachments.size()) {
                    content.add(separator);
                }
                col++;
            }
        } catch (IOException ex) {
        }
    }

    public boolean areAttachmentsValid() {
        return signable;
    }

    public int getAttachmentCount() {
        return attachmentCount;
    }

    public RSyntaxTextArea getSyntaxTextArea() {
        return tfield;
    }

    public JLabel getDescriptionLabel() {
        return labelDesc;
    }

    public String parseXML(String xmlStr) throws JDOMException, Exception {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new StringReader(xmlStr));
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        return outputter.outputString(doc);
    }

    private class AttachmentsBGLayer extends JPanel {

        private Color mouseOutColorDark;
        private Color mouseOutColorLight;
        private Color mouseClickColorCenter;
        private Color mouseClickColorDark;
        private boolean onTop = false;
        private boolean mouseDown = false;
        private String content;
        private String description;
        private boolean lockButtonDown;
        private AttachmentsBGLayer cThis;

        public AttachmentsBGLayer(ImageIcon icon, String filename, final String description, final String content) {
            super();
            setOpaque(false);
            this.description = description;
            cThis = this;
            this.content = content;
            setBackground(new Color(0, 0, 0, 0));
            setPreferredSize(new Dimension(115, 100));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setToolTipText(filename + ": " + description);
            setLayout(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridy = 0;
            gbc.gridx = 0;
            add(new JLabel(icon), gbc);
            gbc.gridy = 1;

            String shownFileName = "";
            int maxFileNameLength = 11;
            if (filename.length() > maxFileNameLength) {
                shownFileName = filename.substring(0, maxFileNameLength - 3) + " ...";
            } else {
                shownFileName = filename;
            }


            String shownDescr = "";
            int maxDescLength = 30;
            if (description.length() > maxDescLength) {
                shownDescr = description.substring(0, maxDescLength - 3) + " ...";
            } else {
                shownDescr = description;
            }
            add(new JLabel("<html><table width=\"100\"><tr><td align=\"center\"><font face=\"ARIAL\" color=\"#ffffff\" size=\"3\"><b>" + shownFileName + "</b></font><br/><font face=\"ARIAL\" color=\"#c9c9c9\" size=\"2\">" + shownDescr + "</font></td></tr></table></html>"), gbc);
            addMouseListener(new MouseListener() {

                public void mouseClicked(MouseEvent e) {
                }

                public void mousePressed(MouseEvent e) {
                    if (!lockButtonDown) {
                        setMouseDown(true);
                    }
                }

                public void mouseReleased(MouseEvent e) {
                    if (onTop && !lockButtonDown) {
                        for (AttachmentsBGLayer button : allIcons) {
                            if (button != cThis) {
                                button.lockButtonDown(false, true);
                            }
                        }
                        lockButtonDown(true, false);
                        try {
                            tfield.setText(parseXML(content));
                            labelDesc.setText(description);
                        } catch (Exception ex) {
                            tfield.setText(content);
                            labelDesc.setText(description);
                        }
                    }
                }

                public void mouseEntered(MouseEvent e) {
                    if (!lockButtonDown) {
                        setMouseOnTop(true);
                    }
                }

                public void mouseExited(MouseEvent e) {
                    if (!lockButtonDown) {
                        setMouseDown(false);
                        setMouseOnTop(false);
                    }
                }
            });
        }

        public void lockButtonDown(boolean param, boolean reset) {
            lockButtonDown = param;
            if (reset) {
                setMouseDown(false);
                setMouseOnTop(false);
            }
            repaint();
        }

        private void setMouseOnTop(boolean param) {
            onTop = param;
            repaint();
        }

        private void setMouseDown(boolean param) {
            mouseDown = param;
            repaint();
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int cornerRounding = 15;

            if (mouseDown) {
                g2.setColor(LAFProperties.getInstance().getAttachmentBtnBorderColorSE());
            } else {
                g2.setColor(LAFProperties.getInstance().getAttachmentBtnBorderColorNW());
            }
            RoundRectangle2D.Float outerRectNorthWest = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, cornerRounding, cornerRounding);
            g2.fill(outerRectNorthWest);

            if (mouseDown) {
                g2.setColor(LAFProperties.getInstance().getAttachmentBtnBorderColorNW());
            } else {
                g2.setColor(LAFProperties.getInstance().getAttachmentBtnBorderColorSE());
            }
            RoundRectangle2D.Float outerRectSouthEast = new RoundRectangle2D.Float(1, 1, getWidth() - 1, getHeight() - 1, cornerRounding, cornerRounding);
            g2.fill(outerRectSouthEast);

            Color[] colorsd = new Color[2];
            if (!onTop) {
                colorsd[0] = LAFProperties.getInstance().getAttachmentBtnOffTopColorDark();
                colorsd[1] = LAFProperties.getInstance().getAttachmentBtnOffTopColorLight();
            } else {
                colorsd[0] = LAFProperties.getInstance().getAttachmentBtnOnTopColorDark();
                colorsd[1] = LAFProperties.getInstance().getAttachmentBtnOnTopColorLight();
            }
            float[] distd = {0.0f, 1.0f};
            Point2D center = new Point2D.Float(getWidth() / 2, getHeight());

            Color[] colorClick = {LAFProperties.getInstance().getAttachmentBtnClickColorLight(), LAFProperties.getInstance().getAttachmentBtnClickColorDark()};

            Color[] color;
            if (mouseDown) {
                color = colorClick;
            } else {
                color = colorsd;
            }

            RadialGradientPaint rgpd = new RadialGradientPaint(getWidth() / 2, getHeight(), color[0], new Point2D.Double(getWidth() * 0.95, getHeight() * 0.95), color[1]);
            g2.setPaint(rgpd);
            RoundRectangle2D.Float innerRect = new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, cornerRounding - 4, cornerRounding - 4);
            g2.fill(innerRect);


            super.paint(g);

            g2.setColor(LAFProperties.getInstance().getAttachmentBtnHighlightColor());
            RoundRectangle2D highlight = new RoundRectangle2D.Float(2, 2, getWidth() - 3, getHeight() / 3, cornerRounding - 8, cornerRounding - 8);
            g2.fill(highlight);
        }
    }
}

class WrapLayout extends FlowLayout {

    private Dimension preferredLayoutSize;

    /**
     * Constructs a new <code>WrapLayout</code> with a left
     * alignment and a default 5-unit horizontal and vertical gap.
     */
    public WrapLayout() {
        super();
    }

    /**
     * Constructs a new <code>FlowLayout</code> with the specified
     * alignment and a default 5-unit horizontal and vertical gap.
     * The value of the alignment argument must be one of
     * <code>WrapLayout</code>, <code>WrapLayout</code>,
     * or <code>WrapLayout</code>.
     * @param align the alignment value
     */
    public WrapLayout(int align) {
        super(align);
    }

    /**
     * Creates a new flow layout manager with the indicated alignment
     * and the indicated horizontal and vertical gaps.
     * <p>
     * The value of the alignment argument must be one of
     * <code>WrapLayout</code>, <code>WrapLayout</code>,
     * or <code>WrapLayout</code>.
     * @param align the alignment value
     * @param hgap the horizontal gap between components
     * @param vgap the vertical gap between components
     */
    public WrapLayout(int align, int hgap, int vgap) {
        super(align, hgap, vgap);
    }

    /**
     * Returns the preferred dimensions for this layout given the
     * <i>visible</i> components in the specified target container.
     * @param target the component which needs to be laid out
     * @return the preferred dimensions to lay out the
     * subcomponents of the specified container
     */
    @Override
    public Dimension preferredLayoutSize(Container target) {
        return layoutSize(target, true);
    }

    /**
     * Returns the minimum dimensions needed to layout the <i>visible</i>
     * components contained in the specified target container.
     * @param target the component which needs to be laid out
     * @return the minimum dimensions to lay out the
     * subcomponents of the specified container
     */
    @Override
    public Dimension minimumLayoutSize(Container target) {
        return layoutSize(target, false);
    }

    /**
     * Returns the minimum or preferred dimension needed to layout the target
     * container.
     *
     * @param target target to get layout size for
     * @param preferred should preferred size be calculated
     * @return the dimension to layout the target container
     */
    private Dimension layoutSize(Container target, boolean preferred) {
        synchronized (target.getTreeLock()) {
            //  Each row must fit with the width allocated to the containter.
            //  When the container width = 0, the preferred width of the container
            //  has not yet been calculated so lets ask for the maximum.

            int targetWidth = target.getSize().width;

            if (targetWidth == 0) {
                targetWidth = Integer.MAX_VALUE;
            }

            int hgap = getHgap();
            int vgap = getVgap();
            Insets insets = target.getInsets();
            int horizontalInsetsAndGap = insets.left + insets.right + (hgap * 2);
            int maxWidth = targetWidth - horizontalInsetsAndGap;

            //  Fit components into the allowed width

            Dimension dim = new Dimension(0, 0);
            int rowWidth = 0;
            int rowHeight = 0;

            int nmembers = target.getComponentCount();

            for (int i = 0; i < nmembers; i++) {
                Component m = target.getComponent(i);

                if (m.isVisible()) {
                    Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();

                    //  Can't add the component to current row. Start a new row.

                    if (rowWidth + d.width > maxWidth) {
                        addRow(dim, rowWidth, rowHeight);
                        rowWidth = 0;
                        rowHeight = 0;
                    }

                    //  Add a horizontal gap for all components after the first

                    if (rowWidth != 0) {
                        rowWidth += hgap;
                    }

                    rowWidth += d.width;
                    rowHeight = Math.max(rowHeight, d.height);
                }
            }

            addRow(dim, rowWidth, rowHeight);

            dim.width += horizontalInsetsAndGap;
            dim.height += insets.top + insets.bottom + vgap * 2;

            //	When using a scroll pane or the DecoratedLookAndFeel we need to
            //  make sure the preferred size is less than the size of the
            //  target containter so shrinking the container size works
            //  correctly. Removing the horizontal gap is an easy way to do this.

            dim.width -= (hgap + 1);

            return dim;
        }
    }

    /**
     *  Layout the components in the Container using the layout logic of the
     *  parent FlowLayout class.
     *
     *	@param target the Container using this WrapLayout
     */
    @Override
    public void layoutContainer(Container target) {
        Dimension size = preferredLayoutSize(target);

        //  When a frame is minimized or maximized the preferred size of the
        //  Container is assumed not to change. Therefore we need to force a
        //  validate() to make sure that space, if available, is allocated to
        //  the panel using a WrapLayout.

        if (size.equals(preferredLayoutSize)) {
            super.layoutContainer(target);
        } else {
            preferredLayoutSize = size;
            Container top = target;

            while (top.getParent() != null) {
                top = top.getParent();
            }

            top.validate();
        }
    }

    /*
     *  A new row has been completed. Use the dimensions of this row
     *  to update the preferred size for the container.
     *
     *  @param dim update the width and height when appropriate
     *  @param rowWidth the width of the row to add
     *  @param rowHeight the height of the row to add
     */
    private void addRow(Dimension dim, int rowWidth, int rowHeight) {
        dim.width = Math.max(dim.width, rowWidth);

        if (dim.height > 0) {
            dim.height += getVgap();
        }

        dim.height += rowHeight;
    }
}
