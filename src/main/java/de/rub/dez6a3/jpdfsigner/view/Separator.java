/*
 * JPDFSigner - Sign PDFs online using smartcards (Separator.java)
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 *
 * @author dan
 */
public class Separator extends JPanel {

    private int cols = 1;   //number of cols
    private int colSpace = 3; //space between each col
    private BufferedImage col = null;
    private Graphics gcol = null;
    private int i = 0;
    private int currColPos = 0;

    public Separator() {
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setForeground(new Color(255, 255, 255, 185));
        setBackground(new Color(0, 0, 0, 60));
        setInitDimension(1);
    }

    public Separator(int cols) {
        this();
        this.cols = cols;
        setInitDimension(1);
    }

    public Separator(int cols, int height) {
        this();
        this.cols = cols;
        setInitDimension(height);
    }

    private void setInitDimension(int height) {
        Dimension tDim = new Dimension((cols * 2) + ((colSpace - 2) * cols), height); //-2 because one col has a width of 2 pixels and -1 because the last space isnt neccessary
        setPreferredSize(tDim);
        setSize(tDim);
    }

    @Override
    public void paint(Graphics g) {
        col = new BufferedImage(2, getHeight(), BufferedImage.TYPE_INT_ARGB);
        gcol = col.getGraphics();

        for (i = 0; i < getHeight(); i++) {
            gcol.setColor(getBackground());
            gcol.drawLine(1, i + 1, 1, i + 1);
            gcol.setColor(getForeground());
            gcol.drawLine(0, i, 0, i);
            i++;
        }

        currColPos = 0;
        for (i = 0; i < cols; i++) {
            g.drawImage(col, ((getWidth() / 2) - ((cols / 2) * colSpace)) + currColPos, 0, this);
            currColPos += colSpace;
        }
    }
}
