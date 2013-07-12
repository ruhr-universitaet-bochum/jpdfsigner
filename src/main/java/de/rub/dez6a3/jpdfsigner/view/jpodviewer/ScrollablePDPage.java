/*
 * JPDFSigner - Sign PDFs online using smartcards (ScrollablePDPage.java)
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

import de.rub.dez6a3.jpdfsigner.control.Configurator;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import javax.swing.JComponent;
import org.apache.log4j.Logger;
import org.icepdf.core.pobjects.Document;
import org.icepdf.core.pobjects.Page;
import org.icepdf.core.util.GraphicsRenderingHints;

/**
 *
 * @author dan
 */
public class ScrollablePDPage {

    private PagesPanel pagesPanel = null;
    private Document doc = null;
    private int pagenmbr = 0;
    private BufferedImage renderedImage = null;
    private boolean isPageDisplayed = false;
    private boolean isPageProceeded = false;
    public int x = 0;
    public int y = 0;
    public int width = 1;
    public int height = 1;
    public static Logger log = Logger.getLogger(ScrollablePDPage.class);

    public ScrollablePDPage(Document pdpage, int pagenmbr, PagesPanel pagesPanel) {
        this.pagesPanel = pagesPanel;
        this.doc = pdpage;
        this.pagenmbr = pagenmbr;
    }

    public boolean isPageDisplayed() {
        return isPageDisplayed;
    }

    public void setPageDisplayed(boolean param) {
        isPageDisplayed = param;
    }

    public BufferedImage getRenderedImage() {
        return renderedImage;
    }

    public Dimension getNormalizedRectangle() {
        return doc.getPageDimension(pagenmbr, 0).toDimension();
    }

    public boolean isPageProceeded() {
        return isPageProceeded;
    }

    public void setPageProceeded(boolean param) {
        isPageProceeded = param;
    }

    public void destroyBufferedImage() {
        renderedImage = null;
        isPageProceeded = false;
        isPageDisplayed = false;
    }

    public int getPageNmbr() {
        return pagenmbr;
    }

    public void displayPage() {
        isPageDisplayed = true;
        if (renderedImage == null) {
            calculateAndDrawPage();
        } else {
            if (renderedImage.getWidth() != width || renderedImage.getHeight() != height) {
                calculateAndDrawPage();
            }
        }
        pagesPanel.repaint();
    }

    private void calculateAndDrawPage() {
        JComponent pagesView = Configurator.getInstance().getSignAndStampablePagePanel().getPdfProcessor().getContentPanel();
        if(pagesView instanceof JComponent){
            ((JPodViewer)pagesView).showPagesLoadingText(true);
        }
        pagesPanel.runBusyIcon(true);
        renderedImage = (BufferedImage) doc.getPageImage(pagenmbr, GraphicsRenderingHints.SCREEN, Page.BOUNDARY_CROPBOX, 0, (float) pagesPanel.getZoomFactor());
        pagesPanel.runBusyIcon(false);
        if(pagesView instanceof JComponent){
            ((JPodViewer)pagesView).showPagesLoadingText(false);
        }
    }
}
