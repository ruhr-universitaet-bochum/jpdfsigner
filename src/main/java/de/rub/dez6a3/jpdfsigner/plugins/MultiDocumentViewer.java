/*
 * JPDFSigner - Sign PDFs online using smartcards (MultiDocumentViewer.java)
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
package de.rub.dez6a3.jpdfsigner.plugins;

import de.rub.dez6a3.jpdfsigner.control.Configurator;
import de.rub.dez6a3.jpdfsigner.control.MultiDocumentThumb;
import de.rub.dez6a3.jpdfsigner.control.MultiDocumentThumbController;
import de.rub.dez6a3.jpdfsigner.model.IPDFProcessor;
import de.rub.dez6a3.jpdfsigner.view.DecoratedJFileChooser;
import de.rub.dez6a3.jpdfsigner.view.DecoratedJOptionPane;
import de.rub.dez6a3.jpdfsigner.view.RootGlassPane;
import de.rub.dez6a3.jpdfsigner.view.Separator;
import de.rub.dez6a3.jpdfsigner.view.UI.LAFProperties;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.filechooser.FileFilter;
import org.apache.log4j.Logger;

/**
 *
 * @author dan
 */
public class MultiDocumentViewer extends JPanel {

    private JPanel docContainer = null;
    private MultiDocumentThumbController thumbCtrl = null;
    private Container parent = null;
    private int expandBtnIconWidth = 10;
    private int expandBtnIconHeight = 10;
    private Dimension btnDim = new Dimension(21, 21);
    private JLabel docNameLabel = null;
    private DocumentContainer docCScroller = null;
    private JPanel content = null;      //contains thumbs. is child of documentcontainer
    public static Logger log = Logger.getLogger(MultiDocumentViewer.class);

    public MultiDocumentViewer() {
        init();
    }

    public MultiDocumentViewer(Container parent) {
        this.parent = parent;
        init();
    }

    private void init() {
        setUpComponent();
        installComponents();
        installListeners();
    }

    private void installListeners() {
        content.addComponentListener(new ComponentAdapter() {

            public void componentResized(ComponentEvent e) {
                thumbCtrl.updateThumbDimensions(docCScroller.getViewport().getHeight());
            }
        });

    }

    private void setUpComponent() {
        setLayout(new GridBagLayout());
    }

    public void setCurrentDocName(String docName) {
//        docNameLabel.setText(docName);
    }

    private void installComponents() {
        thumbCtrl = new MultiDocumentThumbController(this);
        Configurator.getInstance().setMultiDocCtrl(thumbCtrl);
        content = new JPanel();
        content.setBorder(new Border() {

            private Insets insets = new Insets(0, 1, 0, 1);
            private Color hlColor = new Color(250, 250, 250);

            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                g.setColor(hlColor);
                g.drawLine(0, 0, 0, c.getHeight());
                g.drawLine(c.getWidth() - 1, 0, c.getWidth() - 1, c.getHeight());
            }

            @Override
            public Insets getBorderInsets(Component c) {
                return insets;
            }

            @Override
            public boolean isBorderOpaque() {
                return true;
            }
        });
        content.setPreferredSize(new Dimension(1, 125)); //initial contentsize (where the DocumentViewer-Panel and it's expander is embedded in) this can be hidden
        content.setLayout(new GridBagLayout());
        docContainer = new JPanel();
        docContainer.setOpaque(false);
        docContainer.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        docCScroller = new DocumentContainer();
        docCScroller.setViewportView(docContainer);
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0, 10, 0, 10);
        content.add(docCScroller, c);

        c.weightx = 0.0;
        c.weighty = 0.0;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(0, 0, 0, 0);
        c.gridy = 2;
        content.add(new Expander(), c);
    }

    public void setContentVisible(boolean param) {
        content.setVisible(param);
    }

    public JPanel getContentPanel() {
        return content;
    }

    public JPanel getDocumentContainerPanel() {
        return docContainer;
    }

    public boolean isContentVisible() {
        return content.isVisible();
    }

    public BufferedImage createExpandDocViewImage() {
        BufferedImage biHideBtnImageDown = new BufferedImage(expandBtnIconWidth, expandBtnIconHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D big2D = (Graphics2D) biHideBtnImageDown.getGraphics();
        big2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        big2D.setColor(new Color(40, 40, 40));
        Polygon arrowD = new Polygon();
        arrowD.addPoint((expandBtnIconWidth / 2) - 3, (expandBtnIconHeight / 2) - 2);
        arrowD.addPoint((expandBtnIconWidth / 2) + 3, (expandBtnIconHeight / 2) - 2);
        arrowD.addPoint(expandBtnIconWidth / 2, (expandBtnIconHeight / 2) + 2);
        big2D.fill(arrowD);
        return biHideBtnImageDown;
    }

    public BufferedImage createCollapseDocViewImage() {
        BufferedImage biHideBtnImageUp = new BufferedImage(expandBtnIconWidth, expandBtnIconHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D big2U = (Graphics2D) biHideBtnImageUp.getGraphics();
        big2U.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        big2U.setColor(new Color(40, 40, 40));
        Polygon arrowU = new Polygon();
        arrowU.addPoint((expandBtnIconWidth / 2) - 3, (expandBtnIconHeight / 2) + 2);
        arrowU.addPoint((expandBtnIconWidth / 2) + 3, (expandBtnIconHeight / 2) + 2);
        arrowU.addPoint(expandBtnIconWidth / 2, (expandBtnIconHeight / 2) - 2);
        big2U.fill(arrowU);
        return biHideBtnImageUp;
    }
    private GridBagConstraints docC = null;
    private int docPosInLayout = 0;
    private int currentMouseX = 0;
    private int currentViewPositionX = 0;

    public void addDocument(MultiDocumentThumb mdt) {
        if (docC == null) {
            docC = new GridBagConstraints();
            docC.gridy = 0;
            docC.insets = new Insets(0, 1, 0, 1);
        }
        mdt.setThumbCtrl(thumbCtrl);
        thumbCtrl.addMultiDocumentThumb(mdt);
        docC.gridx = docPosInLayout;
        docContainer.add(mdt, docC);
        docPosInLayout++;
        thumbCtrl.updateThumbDimensions(docCScroller.getViewport().getHeight()); //repaints view and resizes thumb to it's size
    }

    public MultiDocumentThumbController getMultiDocumentThumbController() {
        return thumbCtrl;
    }

    private class Expander extends JPanel {

        private Color topColor = new Color(0, 0, 0, 80);
        private Color bottomColor = new Color(255, 255, 255, 200);
        private int mouseY = 0;

        public Expander() {
            Dimension expDim = new Dimension(20, 11);
            setPreferredSize(expDim);
            setSize(expDim);
            setOpaque(false);
            installListeners();
        }

        private void installListeners() {
            addMouseListener(new MouseAdapter() {

                private boolean isFitToSizeSet = false; //just the initial value

                @Override
                public void mousePressed(MouseEvent e) {
                    IPDFProcessor pdfViewer = Configurator.getInstance().getSignAndStampablePagePanel().getPdfProcessor();
                    isFitToSizeSet = pdfViewer.isFitToSize();
                    if (isFitToSizeSet) {
                        Configurator.getInstance().getSignAndStampablePagePanel().getPdfProcessor().setFitToSize(false);
                    }
                    mouseY = e.getY();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (isFitToSizeSet) {
                        Configurator.getInstance().getSignAndStampablePagePanel().getPdfProcessor().setFitToSize(true);
                    }
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() {

                @Override
                public void mouseDragged(MouseEvent e) {
                    Dimension newMdvDim = new Dimension(content.getWidth(), content.getHeight() + (e.getY() - mouseY));
                    int maxHeight = parent.getSize().height / 2;
                    int minHeight = 75;
                    if (newMdvDim.height > maxHeight) {
                        newMdvDim = new Dimension(content.getWidth(), maxHeight);
                    } else if (newMdvDim.height < minHeight) {
                        newMdvDim = new Dimension(content.getWidth(), minHeight);
                    }
                    content.setPreferredSize(newMdvDim);
                    content.setSize(newMdvDim);
                    content.revalidate();
                }
            });
        }

        @Override
        public void paint(Graphics g) {
            int centery = getHeight() / 2;
            g.setColor(topColor);
            g.drawLine(0, centery - 2, getWidth(), centery - 2);
            g.drawLine(0, centery + 1, getWidth(), centery + 1);
            g.setColor(bottomColor);
            g.drawLine(0, centery - 1, getWidth(), centery - 1);
            g.drawLine(0, centery + 2, getWidth(), centery + 2);

        }
    }

    private class DocumentContainer extends JScrollPane {

        private int currentMouseX = 0;
        private int currentViewPosition = 0;

        public DocumentContainer() {
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2)); //should be as expandBtnIconWidth as the border is - this prevents overlapping the border by object inside the container
            getViewport().setOpaque(false);
            setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
            JScrollBar hsb = new JScrollBar(JScrollBar.HORIZONTAL);
            hsb.setPreferredSize(new Dimension(50, 13));
            setHorizontalScrollBar(hsb);
            addMouseListener(new MouseAdapter() {

                public void mousePressed(MouseEvent e) {
                    currentMouseX = e.getX();
                    currentViewPosition = -getViewport().getViewPosition().x;
                }
            });
            addMouseMotionListener(new MouseMotionAdapter() {

                public void mouseDragged(MouseEvent e) {

                    JViewport vp = getViewport();
                    int maxX = vp.getViewSize().width - vp.getWidth();
                    int newX = -(e.getX() - currentMouseX + currentViewPosition);
                    if (newX < 0) {
                        newX = 0;
                    }
                    if (newX > maxX) {
                        newX = maxX;
                    }
                    Point p = new Point(newX, vp.getViewPosition().y);
                    vp.setViewPosition(p);
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.white);
            g2.fillRoundRect(1, 1, getWidth() - 1, getHeight() - 1, 16, 16);
            g2.setColor(new Color(40, 40, 40));
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            GradientPaint gp = new GradientPaint(1, 1, new Color(65, 65, 65), 0, getHeight() - 2, new Color(55, 55, 55), false);
            g2.setPaint(gp);
            g2.fillRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 14, 14);
            super.paintComponent(g);
        }
    }
}
