/*
 * JPDFSigner - Sign PDFs online using smartcards (PagesPanel.java)
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

import de.rub.dez6a3.jpdfsigner.view.jpodviewer.listener.ScaleChangedListener;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import org.apache.log4j.Logger;

/**
 *
 * @author dan
 */
public class PagesPanel extends JPanel {

    private ArrayList<ScrollablePDPage> pages = new ArrayList<ScrollablePDPage>();
    private ArrayList<ScrollablePDPage> visiblePages = new ArrayList<ScrollablePDPage>();
    private ArrayList<ScaleChangedListener> scaleChangedListeners = new ArrayList<ScaleChangedListener>();
    private Rectangle view = new Rectangle(0, 0, 0, 0);
    private double zoomFactor = 1d;
    private double maxZoomFactor = 4d;
    private int pageSpace = 10;
    private JScrollPane scrollpane = null;
    private Thread T_worker = null;
    public static Logger log = Logger.getLogger(PagesPanel.class);

    public double getZoomFactor() {
        return zoomFactor;
    }

    public PagesPanel(JScrollPane scrollpane) {
        this.scrollpane = scrollpane;
    }

    public void removeAllPages() {
        pages.clear();
    }

    public void addPage(ScrollablePDPage page) {
        page.width = 1;
        page.height = 1; //the height depends on the width. it's important to calculate the width first
        page.y = 0;
        page.x = 0;
        pages.add(page);
    }

    public void addScaleChangedListener(ScaleChangedListener scl) {
        scaleChangedListeners.add(scl);
    }
    private float currScrollY = 0;

    public void scalePages(final int percent) {
        if (percent <= maxZoomFactor * 100d && !rendererLock.isLocked()) {
            log.info("Scale changed to " + percent + "%");
            try {
                currScrollY = (float) scrollpane.getVerticalScrollBar().getModel().getValue() / (float) getHeight();
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (ScrollablePDPage visiblePage : getVisiblePages()) {
                visiblePage.destroyBufferedImage();
            }
            int pPanelWidth = 0;
            int pPanelHeight = 0;
            zoomFactor = (double) percent / 100d;
            int pageY = 0;
            for (ScrollablePDPage page : pages) {
                page.width = calcPageSizeByZoomFactorWidth(page);
                page.height = calcPageSizeByZoomFactorHeight(page);
                page.y = pageY;
                pageY += page.height + pageSpace;
                pPanelHeight += page.height + pageSpace;
                if (page.width > pPanelWidth) {   //determines how width the pagespanel have to be in order to display the widest pages
                    pPanelWidth = page.width;
                }
            }

            Dimension ppDim = new Dimension(pPanelWidth, pPanelHeight);
            setPreferredSize(ppDim);
            setSize(ppDim);
            while (rendererLock.isLocked()) {
                try {
                    Thread.sleep(30);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    displayVisiblePages();
                    scrollpane.getVerticalScrollBar().getModel().setValue(Math.round(currScrollY * (float) getHeight()));
                    for (ScaleChangedListener scl : scaleChangedListeners) {
                        scl.scaleHasChanged(percent);
                    }
                }
            });
        }
    }

    public void showPage(int pagenmbr) {
        scrollpane.getVerticalScrollBar().setValue(getPagesList().get(pagenmbr).y);
        displayVisiblePages();
    }

    public void fitPagesInViewWidth() {
        double broadest = 0;
        while (rendererLock.isLocked()) {
            try {
                Thread.sleep(30);
            } catch (InterruptedException ex) {
                log.error(ex);
            }
        }
        for (ScrollablePDPage page : pages) {
            if (page.getNormalizedRectangle().getWidth() > broadest) {
                broadest = page.getNormalizedRectangle().getWidth();
            }
        }
        zoomFactor = ((double) scrollpane.getViewport().getWidth()) / broadest; //the last value (-3) is just a buffer to supress the horizontal scrollbar
        scalePages((int) (zoomFactor * 100));
    }
    private ReentrantLock rendererLock = new ReentrantLock();

    public ReentrantLock getLock() {
        return rendererLock;
    }

    private Thread createWorkerThread() {
        return new Thread() {

            @Override
            public void run() {

                synchronized (T_worker) {
                    try {
                        wait();
                        rendererLock.tryLock();
                        displayPages();
                    } catch (Exception ex) {
                        log.error("Critical error in page-painter-thread! - " + ex.getMessage(), ex);
                    } finally {
                        rendererLock.unlock();
                    }
                    run();
                }

            }

            @Override
            public synchronized void start() {
                super.start();
            }
        };
    }

//    private void initializeWorkerThread() {
//        T_worker = createWorkerThread();
//        T_worker.start();
//    }
    public void displayVisiblePages() {
        if (T_worker == null || !T_worker.isAlive()) {
            T_worker = createWorkerThread();
            T_worker.start();
        }
        if (!rendererLock.isLocked() && pages.size() > 0) {
            synchronized (T_worker) {
                T_worker.notify();
            }
        }
    }

    private void displayPages() {
        for (ScrollablePDPage visiblePage : getVisiblePages()) {
            if (!visiblePage.isPageDisplayed()) {   //if a page has already painted, it doesnt not have to be painted during scrolling.
                if (!visiblePage.isPageProceeded()) {
                    long time = System.currentTimeMillis();
                    visiblePage.displayPage();
                    visiblePage.setPageDisplayed(true);
                    visiblePage.setPageProceeded(true);
                    System.gc();
                    log.info("New Page loaded: " + visiblePage.getPageNmbr() + " in " + Long.toString(System.currentTimeMillis() - time) + "ms");
                    break;
                }
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        repaint();
                    }
                });
            }
        }
        for (ScrollablePDPage visiblePage : getVisiblePages()) {
            if (!visiblePage.isPageProceeded()) {
                displayPages();
                break;
            }
        }
    }

    private int calcPageSizeByZoomFactorWidth(ScrollablePDPage page) {
        Dimension dim = page.getNormalizedRectangle();
        return new Long(Math.round(dim.getWidth() * zoomFactor)).intValue();
    }

    private int calcPageSizeByZoomFactorHeight(ScrollablePDPage page) {
        Dimension dim = page.getNormalizedRectangle();
        return new Long(Math.round(dim.getHeight() * zoomFactor)).intValue();
    }

    public ArrayList<ScrollablePDPage> getPagesList() {
        return pages;
    }

    public ArrayList<ScrollablePDPage> getVisiblePages() {
        visiblePages.clear();
        int scrollPosY = scrollpane.getVerticalScrollBar().getValue();
        Dimension viewSize = scrollpane.getSize();
        view.setBounds(0, scrollPosY, viewSize.width, viewSize.height);
        for (ScrollablePDPage page : pages) {
            int viewY1 = view.y;
            int viewY2 = viewY1 + view.height;
            int pageY1 = page.y;
            int pageY2 = pageY1 + page.height;
            if (pageY1 >= viewY1 && pageY1 < viewY2 || pageY2 >= viewY1 && pageY2 < viewY2 || pageY1 <= viewY1 && pageY2 >= viewY2) {
                visiblePages.add(page);
            } else {
                page.destroyBufferedImage();
            }
        }
        return visiblePages;
    }
    Font f = new Font("Arial", Font.PLAIN, 30);
    Border border = new AbstractBorder() {

        private Insets insets = new Insets(50, 50, 50, 50);

        @Override
        public Insets getBorderInsets(Component c) {
            return insets;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(Color.red);
            g.fillRect(x - insets.left, y - insets.top, width + insets.left + insets.right, height + insets.top + insets.bottom);
        }
    };

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        for (ScrollablePDPage page : pages) {
            page.x = (getWidth() / 2) - (page.width / 2); //centers every page
            if (page.getRenderedImage() == null) {
                drawPageDummy(g, page.x, page.y, page.width, page.height);
            } else {
                g.drawImage(page.getRenderedImage(), page.x, page.y, this);
            }
        }
    }
    private float rotation = 0.0f;

    private void drawPageDummy(Graphics g, int x, int y, int width, int height) {
        g.setColor(Color.white);
        g.fillRect(x, y, width, height);
        int animSize = (int) (width * 0.2);
        if (loading) {
            drawPageLoadingAnimation(g, x + (width / 2) - (animSize / 2), y + (height / 2) - (animSize / 2), animSize, animSize);
        }
    }

    private void drawPageLoadingAnimation(Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int midSize = (int) (width * 0.3f);
        int visibleArea = (int) (width * 0.7f);
        int pieceSize = 20;
        area.reset();
        for (int i = 0; i < 360; i += pieceSize * 2) {
            area.add(new Area(new Arc2D.Double(x, y, width, height, Math.round(i + rotation), pieceSize, Arc2D.PIE)));
        }
        rectMask = new Area(new RoundRectangle2D.Double(x + ((width / 2) - (visibleArea / 2)), y + ((height / 2) - (visibleArea / 2)), visibleArea, visibleArea, width * 0.2f, width * 0.2f));
        midArea = new Area(new RoundRectangle2D.Double(x + ((width / 2) - (midSize / 2)), y + ((height / 2) - (midSize / 2)), midSize, midSize, midSize * 0.35f, midSize * 0.35f));
        area.intersect(rectMask);
        area.subtract(midArea);
        g2.setColor(animationColor);
        g2.fill(area);
        g2.draw(rectMask);
        g2.draw(midArea);
    }
    private Area area = new Area();
    private Area rectMask = null;
    private Area midArea = null;
    private Thread T_busyAnimator = null;
    private boolean loading = false;
    private Color animationColor = new Color(235, 235, 235);

    public void runBusyIcon(boolean mode) {
        loading = mode;
        if (T_busyAnimator == null || !T_busyAnimator.isAlive()) {
            if (loading) {
                T_busyAnimator = new Thread() {

                    @Override
                    public void run() {
                        while (loading) {
                            if (rotation >= 359) {
                                rotation = 0;
                            }
                            rotation += 2.5;
                            repaint();
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException ex) {
                                java.util.logging.Logger.getLogger(PagesPanel.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                };
                T_busyAnimator.start();
            }
        }
    }
}
