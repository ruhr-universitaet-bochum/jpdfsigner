/*
 * JPDFSigner - Sign PDFs online using smartcards (JPodPDFViewer.java)
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

import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.sun.pdfview.PDFFile;
import de.rub.dez6a3.jpdfsigner.model.IPDFProcessor;
import de.rub.dez6a3.jpdfsigner.view.UI.LAFProperties;
import de.rub.dez6a3.jpdfsigner.view.jpodviewer.JPodViewer;
import de.rub.dez6a3.jpdfsigner.view.jpodviewer.listener.PageChangedListener;
import de.rub.dez6a3.jpdfsigner.view.jpodviewer.listener.ScaleChangedListener;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import org.apache.log4j.Logger;

/**
 *
 * @author dan
 */
public class JPodPDFViewer implements IPDFProcessor {

    private JPodViewer viewer = null;
    private Configurator conf = Configurator.getInstance();
    private byte[] loadedPDF = null;
    public static Logger log = Logger.getLogger(JPodPDFViewer.class);

    @Override
    public void loadPDFByteBuffer(ByteBuffer buffer) throws IOException { //reloadview decides if the pdf should be rendererd after load
        if (buffer == null) {
            loadedPDF = null;
            viewer.unloadDocument();
            log.info("Document unloaded");
            conf.setPDFFile(null);
            conf.getSignatureValidPanel().verifySignature();
        } else {
            try {
                conf.setPDFFile(buffer.array());
                loadedPDF = buffer.array();
                viewer.loadDocument(loadedPDF, null, null);
                conf.getSignatureValidPanel().verifySignature();
                try {
                    conf.getAttachmentBar().loadAttachments();
                } catch (IOException ex) {
                    log.error("Cannot load document-attachments due to following exception: " + ex.getMessage());
                }
                if (conf.getAttachmentBar().areAttachmentsValid()) {
                    conf.setPdfValidity(true);
                }
            } catch (Exception e) {
                throw new IOException("Error on rendering PDF - " + e.getMessage(), e);
            }
        }
    }

    @Override
    public JComponent getContentPanel() {
        if (viewer == null) {
            conf.setDocumentSigner(new ITextSigner(conf.getPDFFile()));
            conf.setSignAndStampablePagePanel(new SignAndStampablePagePanel(conf.getDocumentSigner(), this));
            JViewport viewport = new JViewport() {

                private Color viewportBGDark = new Color(58, 58, 58);
                private Color viewportBGLight = LAFProperties.getInstance().getPdfBackGroundColor();

                @Override
                public void paint(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    GradientPaint gp = new GradientPaint(0, 0, viewportBGLight, 0, getHeight(), viewportBGDark);
                    g2.setPaint(gp);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    super.paint(g2);
                }
            };
            viewer = new JPodViewer(viewport);
            Color transparentColor = new Color(0, 0, 0, 0);
            viewer.getPagesPanel().setOpaque(false);
            viewer.getPagesPanel().setBackground(transparentColor);
            viewer.getViewport().setOpaque(false);
            viewer.getViewport().setBackground(transparentColor);
            viewer.setViewport(viewport);
        }
        return viewer;
    }

    @Override
    public void showPage(int pagenmbr) {
        viewer.getPagesPanel().showPage(pagenmbr - 1);    //first page is index 0
    }

    @Override
    public boolean isFirstPageCalled() {
        if (viewer.getPrimaryPageNumber() == 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int getCurrentPageNum() {
        return viewer.getPrimaryPageNumber() + 1;
    }

    @Override
    public int getPageCount() throws IOException {
        return viewer.getPagesPanel().getPagesList().size();
    }

    @Override
    public float getAspectRatio(int pagenmbr) {
//        throw new UnsupportedOperationException("Not supported yet.");
        return 0.5f;
    }

    @Override
    public void setPanelDimension(Dimension panelDim) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Dimension getPanelDimension() {
        return viewer.getSize();
    }

    @Override
    public ArrayList getAttachments() throws IOException {
        ArrayList files = new ArrayList();
        PdfReader reader = new PdfReader(conf.getPDFFile());
        PdfDictionary root = reader.getCatalog();
        PdfDictionary documentnames = root.getAsDict(PdfName.NAMES);
        PdfDictionary embeddedfiles = documentnames.getAsDict(PdfName.EMBEDDEDFILES);
        PdfArray filespecs = embeddedfiles.getAsArray(PdfName.NAMES);
        PdfDictionary filespec;
        PdfDictionary refs;
        for (int i = 0; i < filespecs.size();) {
            filespecs.getAsName(i++);
            filespec = filespecs.getAsDict(i++);
            refs = filespec.getAsDict(PdfName.EF);
            Iterator it = refs.getKeys().iterator();
            while (it.hasNext()) {
                PdfName key = (PdfName) it.next();
                if (key.toString().equals("/F")) {

                    String filename = "-";
                    String desc = "-";
                    int size = -1;
                    String moddate = "-";
                    String compsize = "-";
                    PdfObject pdfobj = null;

                    try {
                        filename = filespec.getAsString(key).toString();
                    } catch (Exception e) {
                        log.warn("Cannot load attachment-name - " + e.getMessage());
                    }
                    try {
                        desc = filespec.getAsString(PdfName.DESC).toString();
                    } catch (Exception e) {
                        log.warn("Cannot load attachment-description - " + e.getMessage());
                    }
                    byte[] attBytes = null;
                    try {
                        PRStream stream = (PRStream) PdfReader.getPdfObject(refs.getAsIndirectObject(key));
                        attBytes = PdfReader.getStreamBytes(stream);
                        size = attBytes.length;
                    } catch (Exception e) {
                        log.warn("Cannot load attachment-size - " + e.getMessage());
                    }
                    try {
                        pdfobj = PdfReader.getPdfObject(refs.getAsIndirectObject(key));
                    } catch (Exception e) {
                        log.warn("Cannot load attachment-pdfobject - " + e.getMessage());
                    }

                    Hashtable fileData = new Hashtable();
                    fileData.put(ATTACHMENT_FILENAME_STRING, filename); //filename
                    fileData.put(ATTACHMENT_DESCRIPTION_STRING, desc);  //Description
                    fileData.put(ATTACHMENT_SIZE_INT, size);         //size
                    fileData.put(ATTACHMENT_BYTES_ARR, attBytes);    //bytes
                    files.add(fileData);
                }
            }
        }
        return files;
    }

    @Override
    public void addPageChangedListener(Object pcl) {
        viewer.addPageChangedListener((PageChangedListener) pcl);
    }

    @Override
    public void addScaleChangedListener(Object scl) {
        viewer.getPagesPanel().addScaleChangedListener((ScaleChangedListener) scl);
    }

    @Override
    public void scale(double scaleFactor) {
        viewer.getPagesPanel().scalePages(new Long(Math.round(scaleFactor * 100d)).intValue());
    }

    @Override
    public double getCurrentScale() {
        return viewer.getPagesPanel().getZoomFactor();
    }

    @Override
    public void setMaximumScale(double maxScale) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setFitToSize(boolean fitToSize) {
        viewer.setFitToView(fitToSize);
    }

    @Override
    public boolean isFitToSize() {
        return viewer.isFitToView();
    }

    @Override
    public void fitPagesInView() {
        viewer.getPagesPanel().fitPagesInViewWidth();
    }
}
