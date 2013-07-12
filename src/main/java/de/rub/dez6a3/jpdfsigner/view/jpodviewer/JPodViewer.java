/*
 * JPDFSigner - Sign PDFs online using smartcards (JPodViewer.java)
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
package de.rub.dez6a3.jpdfsigner.view.jpodviewer;

import de.rub.dez6a3.jpdfsigner.view.jpodviewer.listener.PageChangedListener;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import org.icepdf.core.pobjects.Document;

/**
 *
 * @author dan
 */
public class JPodViewer extends JScrollPane {

    private Cursor openHandCursor = null;
    private Cursor closeHandCursor = null;
    private PagesPanel pagesPanel = null;
    private boolean fitToView = true;
    private int tmpScrollBarDistanceY = 0;
    private int tmpScrollBarPositionY = 0;
    private int tmpScrollBarDistanceX = 0;
    private int tmpScrollBarPositionX = 0;
    private ArrayList<PageChangedListener> pageChangedListeners = new ArrayList<PageChangedListener>();
    private int primaryPageNmbr = -1;
    private boolean showPagesLoadingText = false;
    private Font loadingTextFont = new Font("Arial", Font.BOLD, 10);
    private Color loadingTxtBGColor = new Color(0, 0, 0, 100);
    private Color loadingTxtShadowColor = new Color(0, 0, 0, 50);
    private Color loadingTxtForgroundColor = Color.white;

    public JPodViewer(JViewport viewport) {
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        getVerticalScrollBar().setUnitIncrement(10);
        loadCursor();
        pagesPanel = new PagesPanel(this);
        setViewport(viewport);
        viewport.setView(pagesPanel);
        setCursor(openHandCursor);
        installListeners();
    }

    public void showPagesLoadingText(boolean showPagesLoadingText) {
        this.showPagesLoadingText = showPagesLoadingText;
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if(showPagesLoadingText){
            drawPagesLoadingText(g);
        }
    }

    public void drawPagesLoadingText(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(loadingTextFont);
        g.setColor(loadingTxtBGColor);
        int txtOffX = (getWidth()/2)-(155/2);
        int txtOffY = -4;
        g.fillRoundRect(txtOffX, getHeight() - 15, 155, 20, 12, 12);
        String loadingTxt = "Seiten werden geladen...";
        g.setColor(loadingTxtShadowColor);
        g.drawString(loadingTxt, txtOffX+5, getHeight() + txtOffY);
        g.setColor(loadingTxtForgroundColor);
        g.drawString(loadingTxt, txtOffX + 6, getHeight() + txtOffY + 1);
    }

    private void loadCursor() {
        ImageIcon openHandCursorImg = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/open_hand_cursor.png")));
        ImageIcon closeHandCursorImg = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/close_hand_cursor.png")));

        BufferedImage prepOpenedHandCursorBi = prepareCursorIcon(openHandCursorImg);
        BufferedImage prepClosedHandCursorBi = prepareCursorIcon(closeHandCursorImg);

        openHandCursor = Toolkit.getDefaultToolkit().createCustomCursor(prepOpenedHandCursorBi, new Point(prepOpenedHandCursorBi.getWidth() / 2, prepOpenedHandCursorBi.getHeight() / 2), "open_hand_cursor");
        closeHandCursor = Toolkit.getDefaultToolkit().createCustomCursor(prepClosedHandCursorBi, new Point(prepClosedHandCursorBi.getWidth() / 2, prepClosedHandCursorBi.getWidth() / 2), "close_hand_cursor");
    }

    private BufferedImage prepareCursorIcon(ImageIcon cursorImg) {
        BufferedImage bi = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);  //icon size musst be 32x32. If it has another size, windows will scale it to 32x32
        Graphics2D big2 = bi.createGraphics();                                      //the cursorimage will be painted to the bi to prevent scaling of the image
        big2.drawImage(cursorImg.getImage(), (bi.getWidth() / 2) - (cursorImg.getIconWidth() / 2), (bi.getHeight() / 2) - (cursorImg.getIconHeight() / 2), this);
        removeTranslucentPixels(bi);
        return bi;
    }

    //sets all translucent pixels to non-opaque (all pixels with alpha-value bigger than 0 are completely opaque in java cursors. looks nasty! so they are removed
    private void removeTranslucentPixels(BufferedImage bi) {
        for (int i = 0; i < bi.getHeight(); i++) {
            int[] rgb = bi.getRGB(0, i, bi.getWidth(), 1,
                    null, 0,
                    bi.getWidth() * 4);
            for (int j = 0; j < rgb.length; j++) {
                int alpha = (rgb[j] >> 24) & 255;
                if (alpha < 0x80) {
                    alpha = 0;
                } else {
                    alpha = 255;
                }
                rgb[j] &= 0x00ffffff;
                rgb[j] = (alpha << 24) | rgb[j];
            }
            bi.setRGB(0, i, bi.getWidth(), 1, rgb, 0,
                    bi.getWidth() * 4);
        }
    }

    public PagesPanel getPagesPanel() {
        return pagesPanel;
    }

    public void setFitToView(boolean param) {
        fitToView = param;
    }

    public int getPrimaryPageNumber() {
        return primaryPageNmbr;
    }

    public boolean isFitToView() {
        return fitToView;
    }

    public void addPageChangedListener(PageChangedListener pcl) {
        pageChangedListeners.add(pcl);
    }

    public void loadDocument(byte[] document, String name, String type) throws IOException { //have to be run from edt to prevent congruentmodification exception
        while (pagesPanel.getLock().isLocked()) {
            try {
                Thread.sleep(30);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        try {
            pagesPanel.removeAllPages();
            Document doc = new Document();
            doc.setByteArray(document, 0, document.length, "");
            int pageCount = doc.getNumberOfPages();
            int height = 0;
            for (int i = 0; i < pageCount; i++) {
                ScrollablePDPage spdpage = new ScrollablePDPage(doc, i, pagesPanel);
                height += spdpage.height + 10;
                pagesPanel.addPage(spdpage);
            }
            if (fitToView) {
                pagesPanel.fitPagesInViewWidth();
            } else {
                pagesPanel.scalePages(new Long(Math.round(pagesPanel.getZoomFactor() * 100d)).intValue());
            }
        } catch (Exception e) {
            throw new IOException("Exception during pdf-load! - " + e.getMessage());
        }
    }

    public void unloadDocument() {
        pagesPanel.removeAllPages();
        Dimension d = new Dimension(1, 1);
        pagesPanel.setPreferredSize(d);
        pagesPanel.setSize(d);
        pagesPanel.repaint();
    }

    private void installListeners() {
        //install listeners
        getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {

            public void adjustmentValueChanged(AdjustmentEvent e) {
                for (ScrollablePDPage visiblePage : pagesPanel.getVisiblePages()) {
                    if (primaryPageNmbr != visiblePage.getPageNmbr()) {
                        for (PageChangedListener pcl : pageChangedListeners) {
                            pcl.pageHasChanged(visiblePage.getPageNmbr());
                        }
                        primaryPageNmbr = visiblePage.getPageNmbr();
                    }
                    break;
                }
                if (!e.getValueIsAdjusting() && pagesPanel.getPagesList().size() > 0) {
                    pagesPanel.displayVisiblePages();
                }
            }
        });

        getVerticalScrollBar().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                pagesPanel.displayVisiblePages();
            }
        });

        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                setCursor(closeHandCursor);
                tmpScrollBarDistanceY = e.getY();
                tmpScrollBarDistanceX = e.getX();
                tmpScrollBarPositionY = getVerticalScrollBar().getValue();
                tmpScrollBarPositionX = getHorizontalScrollBar().getValue();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                setCursor(openHandCursor);
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {

            private int lastPage = -1;

            @Override
            public void mouseDragged(MouseEvent e) {
                int newScrollBarPositionY = tmpScrollBarPositionY + (tmpScrollBarDistanceY - e.getY());
                int newScrollBarPositionX = tmpScrollBarPositionX + (tmpScrollBarDistanceX - e.getX());
                getVerticalScrollBar().setValue(newScrollBarPositionY);
                getHorizontalScrollBar().setValue(newScrollBarPositionX);
                pagesPanel.displayVisiblePages();
            }
//            @Override //click to expand small page
//            public void mouseMoved(MouseEvent e) {
//                for (ScrollablePDPage page : pagesPanel.getVisiblePages()) {
//                    if (e.getX() >= page.x && e.getX() <= page.x + page.width & e.getY() >= page.y && e.getY() <= page.y + page.height && page.getPageNmbr() != lastPage) {
//                        System.out.println("Hit pagenmbr " + page.getPageNmbr());
//                        lastPage = page.getPageNmbr();
//                        break;
//                    }
//                }
//            }
        });

        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                if (fitToView && pagesPanel.getPagesList().size() > 0) {
                    pagesPanel.fitPagesInViewWidth();
                }
            }
        });
    }
}
