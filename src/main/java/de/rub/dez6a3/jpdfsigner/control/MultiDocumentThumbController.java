/*
 * JPDFSigner - Sign PDFs online using smartcards (MultiDocumentThumbController.java)
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

import de.rub.dez6a3.jpdfsigner.model.IPDFProcessor;
import de.rub.dez6a3.jpdfsigner.model.MDTControllerListener;
import de.rub.dez6a3.jpdfsigner.plugins.MultiDocumentViewer;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.EventObject;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;

/**
 *
 * @author dan
 */
public class MultiDocumentThumbController {

    private ArrayList<MultiDocumentThumb> thumbs = new ArrayList<MultiDocumentThumb>();
    private IPDFProcessor pdfProcessor = Configurator.getInstance().getSignAndStampablePagePanel().getPdfProcessor();
    private MultiDocumentViewer mdv = null;
    private MultiDocumentThumb currentDocThumb = null;
    private ArrayList<MDTControllerListener> listeners = new ArrayList();

    public static Logger log = Logger.getLogger(MultiDocumentThumbController.class);

    public void addMDTControllerListener(MDTControllerListener listener) {
        listeners.add(listener);
    }

    public void removeMDTControllerListener(MDTControllerListener listener) {
        listeners.remove(listener);
    }

    public ArrayList<MultiDocumentThumb> getDocuments() {
        return thumbs;
    }

    public MultiDocumentThumb getCurrentDisplayedDocumentAsThumb() {
        return currentDocThumb;
    }

    public int getCurrentThumbNmbr() { //returns the current index of thumb in thumbs-list
        return thumbs.indexOf(currentDocThumb);
    }

    public MultiDocumentThumbController(MultiDocumentViewer mdv) {
        this.mdv = mdv;
    }

    public void addMultiDocumentThumb(MultiDocumentThumb mdt) {
        thumbs.add(mdt);
    }

    public void removeMultiDocumentThumb(MultiDocumentThumb mdt) {
        thumbs.remove(mdt);
        if (thumbs.size() == 0) {
            loadPDF(-1, null);
        }
    }

    public MultiDocumentThumb getMultiDocumentThumb(int docThumb) {
        return thumbs.get(docThumb);
    }

    public String getMultiDocumentThumbName(int docThumb) {
        return thumbs.get(docThumb).getDocName();
    }

    public int getMultiDocumentCount() {
        return thumbs.size();
    }

    public void disableAllThumbs() {

        for (MultiDocumentThumb thumb : thumbs) {
            thumb.setIsSelected(false);
        }
    }

    public void showDocument(int docNmbr) {
        if (thumbs.size() > 0) {
            MultiDocumentThumb curMdt = thumbs.get(docNmbr);
            loadPDF(curMdt.getCurrentPageOpened(), curMdt);
        } else {
            loadPDF(-1, null); //unloads viewer
        }

    }

    public void showNextDoc() {
        int currentIndex = thumbs.indexOf(currentDocThumb);
        if (currentIndex + 1 <= thumbs.size() - 1) {
            MultiDocumentThumb nextThumb = thumbs.get(currentIndex + 1);
            loadPDF(nextThumb.getCurrentPageOpened(), nextThumb);
        }
    }

    public void showPreviousDoc() {
        int currentIndex = thumbs.indexOf(currentDocThumb);
        if (currentIndex - 1 >= 0) {
            MultiDocumentThumb prevDoc = thumbs.get(currentIndex - 1);
            loadPDF(prevDoc.getCurrentPageOpened(), prevDoc);
        }
    }

    public void loadPDF(int pageNmbr, MultiDocumentThumb thumb) {
             createPDFLoader(pageNmbr, thumb);
    }

    private void createPDFLoader(int pageNmbr, MultiDocumentThumb thumb) {
                try {
                    if (thumb == null) {
                        pdfProcessor.loadPDFByteBuffer(null);
                        mdv.setCurrentDocName("");
                    } else {
                        disableAllThumbs();
                        thumb.setIsSelected(true);
                        mdv.setCurrentDocName(thumb.getDocName() + "  [" + (thumbs.indexOf(thumb) + 1) + "/" + thumbs.size() + "]");
                        pdfProcessor.loadPDFByteBuffer(ByteBuffer.wrap(thumb.getDocBytes()));
                        pdfProcessor.showPage(pageNmbr);    //Swing operations
                    }
                    currentDocThumb = thumb;
                    fireDocumentLoadedEvent();
                    if (currentDocThumb == null) {
                        Configurator.getInstance().getPagesTextField().setText("");
                        Configurator.getInstance().getPagesTextField().setEnabled(false);
                        Configurator.getInstance().getScaleFactorTextField().setText("");
                        Configurator.getInstance().getScaleFactorTextField().setEnabled(false);
                    } else {
                        Configurator.getInstance().getPagesTextField().setEnabled(true); //text will be set in containerlistener defined in guipdfview
                        Configurator.getInstance().getScaleFactorTextField().setEnabled(true);
                    }
                } catch (Exception e) {
                    log.error("Cannot load Document due to following exception: " + e.getMessage(), e);
                }

    }

    private void fireDocumentLoadedEvent() {
        for (MDTControllerListener currListener : listeners) {
            currListener.newDocumentLoaded(this);
        }
    }

    public void updateThumbDimensions(int parentHeight) {
        for (MultiDocumentThumb thumb : thumbs) {
            int newCWidth = (int) (((float) parentHeight) * thumb.getAspectRatio());  //can only scale the thumbs when thumb has a fixed aspect ratio
            thumb.setPreferredSize(new Dimension(newCWidth, parentHeight));           //because of setting the preferredsize it cant be calculated correctly after resizing
            thumb.revalidate();
        }
    }
}
