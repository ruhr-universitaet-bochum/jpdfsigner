/*
 * JPDFSigner - Sign PDFs online using smartcards (HighlightBorder.java)
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
package de.rub.dez6a3.jpdfsigner.view.UI.borders;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.border.AbstractBorder;

/**
 *
 * @author dan
 */
public class HighlightBorder extends AbstractBorder {

    private Insets insets;
    private Color color;

    public HighlightBorder(Insets insets, Color color) {
        this.insets = insets;
        this.color = color;
    }

    public Insets getBorderInsets(Component c) {
        return insets;
    }

    public void paintBorder(Component comp, Graphics g, int x, int y, int width, int height) {
        g.setColor(color);
        g.drawRect(x,y-1,width-1,height);
    }
}
