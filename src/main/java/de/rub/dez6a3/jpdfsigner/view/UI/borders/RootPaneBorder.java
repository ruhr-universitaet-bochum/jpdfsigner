/*
 * JPDFSigner - Sign PDFs online using smartcards (RootPaneBorder.java)
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
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.border.AbstractBorder;

/**
 *
 * @author dan
 */
public class RootPaneBorder extends AbstractBorder{

    Insets insets;

    public RootPaneBorder() {
        insets = new Insets(20,1,1,1);

    }

    public Insets getBorderInsets(Component c) {
        return insets;
    }

    public void paintBorder(Component comp, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(50,50,50));
        g2.fillRect(0, 0, width, height);
        JButton closeBtn = new JButton("close");
        SwingUtilities.paintComponent(g, closeBtn, (Container)comp, new Rectangle(0,0,15, 15));
        closeBtn.dispatchEvent(new MouseEvent(closeBtn, y, width, height, x, y, height, true));
    }
}
