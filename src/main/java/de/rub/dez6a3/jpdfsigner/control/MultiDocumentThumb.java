/*
 * JPDFSigner - Sign PDFs online using smartcards (MultiDocumentThumb.java)
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

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import de.rub.dez6a3.jpdfsigner.model.IPDFProcessor;
import de.rub.dez6a3.jpdfsigner.view.PDFOptimizedViewer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.AbstractBorder;
import org.icepdf.core.pobjects.Document;
import org.icepdf.core.pobjects.Page;
import org.icepdf.core.util.GraphicsRenderingHints;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 *
 * @author dan
 */
public class MultiDocumentThumb extends JPanel {

    private Image thumbImage = null;
    private Document currentPdfPage = null;
    private byte[] docBytes = null;
    private byte[] newDocBytes = null;
    private String docName = null;
    private String docPostID = null;
    private String docSignSpec = null;
    private MultiDocumentThumbController thumbCtrl = null;
    private String xmlEnrichFName = "";
    private String xmlEnrichLName = "";
    private String xmlEnrichBirth = "";
    private String xmlEnrichPR = "";
    private String xmlEnrichSigner = "";
    private boolean isSelected = false;
    private int currentPageOpened = 1;
    private float aspectRatio = -1f;
    private Image renderedImage = null;
    private Image docPreparedIco = null;
    private boolean marked = false;
    private boolean docPrepared = false;
    private int pageCount = 0;

    public MultiDocumentThumb() {
        setToolTipText(docName);
        setLayout(new BorderLayout());
        setBorder(new ShadowBorder());
        setOpaque(false);
        long timestamp = System.currentTimeMillis();
        docPostID = new Integer(new Random(timestamp).nextInt(9999999)).toString() + timestamp;
        setDocPostID(docPostID);
        docPreparedIco = Toolkit.getDefaultToolkit().createImage(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/ready2ul.png")).getScaledInstance(18, 18, Image.SCALE_SMOOTH);

        //if now signspec is given, this one is set. just to use until the manual stamper is implemented!!!!
        Element root = new Element("jpdfsigner_pdf_specs");
        Element stampPos = new Element("stampPosition");
        Element x = new Element("x");
        Element y = new Element("y");
        Element width = new Element("width");
        Element height = new Element("height");
        Element page = new Element("page");

        stampPos.setAttribute("visibleSignature", "false");
        x.setText("1");
        y.setText("1");
        width.setText("1");
        height.setText("1");
        page.setText("1");

        stampPos.addContent(x);
        stampPos.addContent(y);
        stampPos.addContent(width);
        stampPos.addContent(height);
        stampPos.addContent(page);

        root.addContent(stampPos);

        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        docSignSpec = outputter.outputString(root);
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public String getXmlEnrichBirth() {
        return xmlEnrichBirth;
    }

    public void setXmlEnrichBirth(String xmlEnrichBirth) {
        this.xmlEnrichBirth = xmlEnrichBirth;
    }

    public String getXmlEnrichFName() {
        return xmlEnrichFName;
    }

    public void setXmlEnrichFName(String xmlEnrichFName) {
        this.xmlEnrichFName = xmlEnrichFName;
    }

    public String getXmlEnrichLName() {
        return xmlEnrichLName;
    }

    public void setXmlEnrichLName(String xmlEnrichLName) {
        this.xmlEnrichLName = xmlEnrichLName;
    }

    public String getXmlEnrichPR() {
        return xmlEnrichPR;
    }

    public void setXmlEnrichPR(String xmlEnrichPR) {
        this.xmlEnrichPR = xmlEnrichPR;
    }

    public String getXmlEnrichSigner() {
        return xmlEnrichSigner;
    }

    public void setXmlEnrichSigner(String xmlEnrichSigner) {
        this.xmlEnrichSigner = xmlEnrichSigner;
    }

    public byte[] getNewDocBytes() {
        return newDocBytes;
    }

    public void setNewDocBytes(byte[] signedDocBytes) {
        this.newDocBytes = signedDocBytes;
    }

    public String getDocPostID() {
        return docPostID;
    }

    public void setDocPostID(String docPostID) {
        this.docPostID = docPostID;
    }

    public String getDocSignSpec() {
        return docSignSpec;
    }

    public void setDocSignSpec(String docSignSpec) {
        this.docSignSpec = docSignSpec;
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
        repaint();
    }

    public boolean isDocPrepared() {
        return docPrepared;
    }

    public void setDocPrepared(boolean docPrepared) {
        this.docPrepared = docPrepared;
        repaint();
    }

    public int getCurrentPageOpened() {
        return currentPageOpened;
    }

    public void setCurrentPageOpened(int currentPageOpened) {
        this.currentPageOpened = currentPageOpened;
    }

    public float getAspectRatio() {
        return aspectRatio;
    }

    public boolean isIsSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        if (this.isSelected && !isSelected) {   //saving which page is shown
            currentPageOpened = Configurator.getInstance().getSignAndStampablePagePanel().getPdfProcessor().getCurrentPageNum();
        }
        this.isSelected = isSelected;
        repaint();
    }

    public void setThumbCtrl(MultiDocumentThumbController thumbContr) {
        this.thumbCtrl = thumbContr;
    }

    public byte[] getDocBytes() {
        return docBytes;
    }

    public void setDocBytes(byte[] docBytes) throws IOException {
        this.docBytes = docBytes;
        System.out.println("LOGGING AND ERRORIMAGE MUST BE IMPLEMENTED");
        currentPdfPage = loadPDFPage(docBytes);
        Dimension calcThumbDim = calculateThumbSize(currentPdfPage, 100);
        thumbImage = currentPdfPage.getPageImage(0, GraphicsRenderingHints.SCREEN, Page.BOUNDARY_CROPBOX, 0, 0.75f);
        setPreferredSize(calcThumbDim);
        setSize(calcThumbDim);
        Insets insets;
        if (getBorder() != null) {
            insets = getBorder().getBorderInsets(this);
        } else {
            insets = getInsets();
        }

        renderedImage = thumbImage.getScaledInstance(calcThumbDim.width - insets.left - insets.right, calcThumbDim.height - insets.top - insets.bottom, Image.SCALE_SMOOTH);

        final ThumbImageContainer thumbContainer = new ThumbImageContainer();
        add(thumbContainer);
        addComponentListener(new ComponentAdapter() {

            private Thread scaler = createThumbScaleThread();

            public void componentResized(ComponentEvent e) {
                if (scaler.isAlive()) {
                    scaler.interrupt();
                    scaler = createThumbScaleThread();
                    scaler.start();
                } else {
                    scaler = createThumbScaleThread();
                    scaler.start();
                }
            }

            private Thread createThumbScaleThread() {
                return new Thread() {

                    @Override
                    public void run() {
                        int w = thumbContainer.getWidth();
                        int h = thumbContainer.getHeight();

                        if (w <= 0 || h <= 0) {
                            return;
                        }

                        try {
                            Thread.sleep(1000); //ms to wait until thumbs repaint is fired after the viewer resize stops
                            renderedImage = thumbImage.getScaledInstance(w, h, Image.SCALE_SMOOTH);
                            thumbContainer.repaint();
                        } catch (InterruptedException ex) {
                        }

                    }
                };
            }
        });
        installListeners();
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
        setToolTipText(docName);
    }

    private Document loadPDFPage(byte[] docBytes) throws IOException {
        try {
            Document doc = new Document();
            doc.setByteArray(docBytes, 0, docBytes.length, "");
            return doc;
        } catch (Exception e) {
            throw new IOException("Cannot load document thumb. - " + e.getMessage());
        }
    }

    private Dimension calculateThumbSize(Document pdfPage, int maxPageLength) {
        float ratio = pdfPage.getPageDimension(0, 0).getWidth() / pdfPage.getPageDimension(0, 0).getHeight();
        int pWidth = 0;
        int pHeight = 0;
        if (ratio < 1f) {
            pHeight = maxPageLength;
            pWidth = (int) (maxPageLength * ratio);
        } else {
            pHeight = (int) (maxPageLength / ratio);
            pWidth = maxPageLength;
        }
        if (aspectRatio == -1) {
            aspectRatio = (float) pWidth / (float) pHeight;
        }
        return new Dimension(pWidth, pHeight);
    }

    private void installListeners() {
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                if (checkBoxPosition.contains(e.getPoint())) {
                    if (isMarked()) {
                        setMarked(false);
                    } else {
                        setMarked(true);
                    }
                } else {
                    MultiDocumentThumb m = thumbCtrl.getCurrentDisplayedDocumentAsThumb();
                    if (docBytes != thumbCtrl.getCurrentDisplayedDocumentAsThumb().getDocBytes()) {
                        thumbCtrl.loadPDF(currentPageOpened, MultiDocumentThumb.this);
                    }
                }
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (isSelected) {
            drawSelectedMask(g);
        }
        drawCheckBox(g);

        if (docPrepared) {
            if (getBorder() != null && getBorder().getBorderInsets(this) != null) {
                insets = getBorder().getBorderInsets(this);
            } else {
                insets = emptyBorderInsets;
            }
            g.drawImage(docPreparedIco, insets.left, insets.top, this);
        }

    }
    private BufferedImage bi = null;
    private Graphics2D big2 = null;

    private void drawSelectedMask(Graphics g) {
        int x = getBorder().getBorderInsets(this).left;
        int y = getBorder().getBorderInsets(this).top;
        int width = getWidth() - (getBorder().getBorderInsets(this).right * 2);
        int height = getHeight() - (getBorder().getBorderInsets(this).bottom * 2);

        bi = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        big2 = (Graphics2D) g;
        int glasBorderWidth = width / 18;
        int glassX = x - glasBorderWidth;
        int glassY = y - glasBorderWidth;
        int glassW = width + (glasBorderWidth * 2);
        int glassH = height + (glasBorderWidth * 2);
        Polygon highlight = new Polygon();
        highlight.addPoint(x - glasBorderWidth, y + (height - (height / 3)));
        highlight.addPoint(x - glasBorderWidth, y - glasBorderWidth);
        highlight.addPoint(x + width + glasBorderWidth, y - glasBorderWidth);
        highlight.addPoint(x + width + glasBorderWidth, y + (height / 3));
        if (marked) {
            big2.setColor(new Color(200, 0, 0, 25));
        } else if (docPrepared) {
            big2.setColor(new Color(0, 255, 130, 40));
        } else {
            big2.setColor(new Color(0, 130, 255, 40));
        }
        big2.fillRect(glassX, glassY, glassW, glassH);
        GradientPaint gp = new GradientPaint(0, glassY, new Color(255, 255, 255, 10), 0, y + (height - (height / 3)), new Color(255, 255, 255, 40), false);
        big2.setPaint(gp);
        big2.fill(highlight);

        big2.setColor(new Color(0, 0, 0, 85));
        big2.drawRect(glassX - 1, glassY - 1, glassW + 1, glassH + 1);
        big2.setColor(new Color(255, 255, 255, 50));
        big2.drawRect(glassX, glassY, glassW - 1, glassH - 1);

        g.drawImage(bi, 0, 0, this);
    }
    private int checkBoxDim = 20; //width and height of checkbox
    private int x = 0;
    private int y = 0;
    private int width = 0;
    private int height = 0;
    private int offsetX = -1;
    private int offsetY = 1;
    private float roundingStrength = 0.5f;
    private int corners = 0;
    private Rectangle checkBoxPosition = null;
    private Insets insets = null;
    private Insets emptyBorderInsets = new Insets(0, 0, 0, 0); //will be used when there is no border or the border insets are null

    private void drawCheckBox(Graphics g) {
        if (getBorder() != null && getBorder().getBorderInsets(this) != null) {
            insets = getBorder().getBorderInsets(this);
        } else {
            insets = emptyBorderInsets;
        }
        x = getWidth() - insets.right - checkBoxDim + offsetX;
        y = insets.top + offsetY;
        width = checkBoxDim;
        height = checkBoxDim;
        checkBoxPosition = new Rectangle(x, y, width, height);
        if (width > height) {
            corners = (int) ((float) height * roundingStrength);
        } else {
            corners = (int) ((float) width * roundingStrength);
        }
        Graphics2D g2 = (Graphics2D) g;
        GradientPaint gp = new GradientPaint(x, y, new Color(0, 0, 0, 30), x + width, y + height, new Color(255, 255, 255, 70), false);
        g2.setPaint(gp);
        g2.fillRoundRect(x, y, width, height, corners, corners);
        g2.setColor(new Color(135, 135, 135));
        g2.fillRoundRect(x + 3, y + 3, width - 6, height - 6, corners - 3, corners - 3);
        g2.setColor(Color.white);
        g2.fillRoundRect(x + 4, y + 4, width - 8, height - 8, corners - 4, corners - 4);

        if (marked) {
            g2.setColor(new Color(220, 0, 0));
            g2.fillOval(x + 7, y + 7, width - 14, height - 14);
        }

    }

    private class ThumbImageContainer extends JLabel {

        private MultiDocumentThumb parent = null;
        private Font f = null;
        private FontMetrics fm = null;
        private Rectangle r = null;
        private int bottomLeft = 0;
        private Polygon docNameBGArea = null;
        private int strX = 0;
        private int strY = 0;
        private RescaleOp rop = null;
        private float[] offset = new float[4];
        private float[] scales = null;
        private int alphaFrom = 65;
        private int alphaTo = 85;
        private BufferedImage bi = null;
        private Graphics big = null;

        public ThumbImageContainer() {
            parent = MultiDocumentThumb.this;
            f = new Font("Arial", Font.PLAIN, 10);
            fm = getFontMetrics(f);
        }
        private float range = 0f;
        private float currentRange = 0f;
        private float newAlpha = 0f;

        @Override
        public void paint(Graphics g) {
            g.drawImage(renderedImage, 0, 0, getWidth(), getHeight(), null);
            newAlpha = 0.0f;
            if (getWidth() >= alphaFrom && getWidth() <= alphaTo) {
                range = alphaTo - alphaFrom;
                currentRange = getWidth() - alphaFrom;
                newAlpha = currentRange / range;
            } else {
                if (getWidth() < alphaFrom) {
                    newAlpha = 0.0f;
                } else if (getWidth() > alphaTo) {
                    newAlpha = 1.0f;
                }
            }
            scales = new float[4];
            scales[0] = 1.0f;
            scales[1] = 1.0f;
            scales[2] = 1.0f;
            scales[3] = newAlpha;
            rop = new RescaleOp(scales, offset, null);
            ((Graphics2D) g).drawImage(drawDocumentName(), rop, 0, 0);
        }

        private BufferedImage drawDocumentName() {
            bi = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            big = bi.getGraphics();
            big.setFont(f);
            big.setColor(new Color(0, 0, 0, 50));
            r = new Rectangle(0, getHeight() - fm.getHeight(), getWidth(), fm.getHeight());
            big.fillRect(r.x, r.y, r.width, r.height);
            bottomLeft = 0;
            docNameBGArea = new Polygon();
            docNameBGArea.addPoint(bottomLeft, getHeight());
            docNameBGArea.addPoint(fm.getHeight(), getHeight() - fm.getHeight());
            docNameBGArea.addPoint(fm.getHeight() * 2, getHeight() - fm.getHeight());
            docNameBGArea.addPoint(fm.getHeight(), getHeight());
            big.setColor(new Color(0, 0, 0, 18));
            while (bottomLeft < getWidth()) {
                ((Graphics2D) big).fill(docNameBGArea);
                docNameBGArea.translate(fm.getHeight() * 2, 0);
                bottomLeft += fm.getHeight() * 2;
            }

            big.setColor(new Color(255, 255, 255, 100));
            String correctString = docName;
            int docNameInsetLR = 15;    //15 is the margin of the left and right side of the text to the outer lines of the thumbimage
            if (fm.stringWidth(docName) > (getWidth() - docNameInsetLR)) {
                int i = 0;
                while (true) {
                    correctString = docName.substring(0, i) + "...";
                    if (fm.stringWidth(correctString) >= getWidth() - docNameInsetLR) {
                        break;
                    }
                    i++;
                }
            }
            strX = (getWidth() / 2) - (fm.stringWidth(correctString) / 2);
            strY = getHeight() - 3;
            big.drawString(correctString, strX + 1, strY + 1);
            big.setColor(new Color(40, 40, 40));
            big.drawString(correctString, strX, strY);
            return bi;
        }
    }

    private class ShadowBorder extends AbstractBorder {

        private Insets insets = null;
        private int initSizeH = 0;
        private int initSizeW = 0;
        private float percentW = 0f;
        private float percentH = 0f;

        @Override
        public Insets getBorderInsets(Component c) {
            if (initSizeW == 0) {
                initSizeW = c.getSize().width;
            }
            if (initSizeH == 0) {
                initSizeH = c.getSize().height;
            }

            float ratio = (float) c.getSize().width / (float) c.getSize().height;
            int maxInsetValue = 15;
            int iTop = 0;
            int iLeft = 0;
            percentW = (float) c.getSize().width / (float) initSizeW;
            percentH = (float) c.getSize().height / (float) initSizeH;
            if (ratio < 1.0f) { //Vertical page format        
                iTop = (int) ((float) maxInsetValue * percentH);
                iLeft = (int) ((maxInsetValue * ratio) * percentW);
            } else {    //Horizontal page format
                iTop = (int) ((maxInsetValue * (ratio / 2)) * initSizeH);
                iLeft = maxInsetValue * initSizeW;
            }
            insets = new Insets(iTop, iLeft, iTop, iLeft);
            return insets;
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            return insets;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
//            int borderWidth = 0;
//            int max = 35;
//            if (height > width) {
//                if (insets.left > max) {
//                    borderWidth = max;
//                } else {
//                    borderWidth = insets.left;
//                }
//            } else {
//                if (insets.top > max) {
//                    borderWidth = 20;
//                } else {
//                    borderWidth = insets.top;
//                }
//            }
//
//            int cx = insets.left;
//            int cy = insets.top;
//            int cw = width - (insets.right * 2);
//            int ch = height - (insets.bottom * 2);
//
//
//            float a = 0.5f;
//            int maxAlphaValue = 10;
//            int iterations = borderWidth;
//            for (int i = iterations; i > 0; i -= 2) {
//                float result = (float) (a * Math.pow((float) i / ((float) iterations / 10), 2) + 10f * (1 - a) * ((float) i / ((float) iterations / 10)));
//                int alpha = (Math.round((maxAlphaValue * (result / 100f))));
//                g.setColor(new Color(0, 0, 0, alpha));
//                int way = -(i - iterations);
//                g.fillRoundRect(cx - way, cy - way, cw + (way * 2), ch + (way * 2), way * 2, way * 2);
//            }
        }
    }
}
