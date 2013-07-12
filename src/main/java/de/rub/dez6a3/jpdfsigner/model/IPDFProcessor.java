/*
 * JPDFSigner - Sign PDFs online using smartcards (IPDFProcessor.java)
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
package de.rub.dez6a3.jpdfsigner.model;

import java.awt.Dimension;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 *
 * @author dan
 */
public interface IPDFProcessor {

    public static int ATTACHMENT_FILENAME_STRING = 0;
    public static int ATTACHMENT_DESCRIPTION_STRING = 1;
    public static int ATTACHMENT_SIZE_INT = 2;
    public static int ATTACHMENT_BYTES_ARR = 3;

    public void loadPDFByteBuffer(ByteBuffer buffer) throws IOException;

    public JComponent getContentPanel();

    public void showPage(int pagenmbr);

    public boolean isFirstPageCalled();

    public int getCurrentPageNum();

    public int getPageCount() throws IOException;

    public float getAspectRatio(int pagenmbr);

    public void setPanelDimension(Dimension panelDim);

    public Dimension getPanelDimension();

    public ArrayList getAttachments() throws IOException;

    public void addPageChangedListener(Object pcl);

    public void addScaleChangedListener(Object scl);

    public void scale(double scaleFactor);

    public void setFitToSize(boolean fitToSize);

    public boolean isFitToSize();

    public void fitPagesInView();

    public double getCurrentScale();

    public void setMaximumScale(double maxScale);
}
