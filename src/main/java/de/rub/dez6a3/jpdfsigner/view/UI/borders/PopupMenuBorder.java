/*
 * JPDFSigner - Sign PDFs online using smartcards (PopupMenuBorder.java)
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
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.border.AbstractBorder;

/**
 *
 * @author dan
 */
public class PopupMenuBorder extends AbstractBorder {

    Insets insets;
    int size;
    int intensity;

    public PopupMenuBorder(int size, int intensity) {
        this.size = size;
        this.intensity = intensity;
        insets=new Insets(0,size,size,size);
    }

    public Insets getBorderInsets(Component c) {
        return insets;
    }

    public void paintBorder(Component comp, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (int i = size; i > 0; i--) {
            g2.setColor(new Color(0, 0, 0, validateColor(intensity + (i + 5))));
            g2.fillRoundRect(x + i, y + i, width - (i * 2), height - (i * 2), 15, 15);
        }
    }

    private int validateColor(int param) {
        int result;
        if (param > 255) {
            result = 255;
        } else if (param < 0) {
            result = 0;
        } else {
            result = param;
        }
        return result;
    }

//    int xoff, yoff;
//		Insets insets;
//		public PopupMenuBorder(int x, int y) {
//			this.xoff = x;
//			this.yoff = y;
//			insets = new Insets(0,0,xoff,yoff);
//
//		}
//		public Insets getBorderInsets( Component c ) {
//			return insets;
//		}
//
//		public void paintBorder(Component comp, Graphics g,
//		int x, int y, int width, int height) {
//		g.setColor(Color.black);
//		g.translate(x,y);
//		// draw right side
//		g.fillRect(width-xoff, yoff, xoff, height-yoff);
//		// draw bottom side
//		g.fillRect(xoff, height-yoff, width-xoff, yoff);
//		g.translate(-x,-y);
//	}

}
