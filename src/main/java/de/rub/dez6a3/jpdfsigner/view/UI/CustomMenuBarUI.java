/*
 * JPDFSigner - Sign PDFs online using smartcards (CustomMenuBarUI.java)
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
package de.rub.dez6a3.jpdfsigner.view.UI;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuBarUI;

/**
 *
 * @author dan
 */
public class CustomMenuBarUI extends BasicMenuBarUI {

    public void installUI(JComponent c) {
        super.installUI(c);
        
    }

    public void paint(Graphics g, JComponent c){
        Graphics2D g2 = (Graphics2D)g;
        GradientPaint gp = new GradientPaint(0, 0, new Color(215, 215, 215), 0, c.getHeight(), new Color(190, 190, 190));
        g2.setPaint(gp);
        g2.fillRect(0, 0, c.getWidth(), c.getHeight());
        g2.setColor(Color.white);
        g2.drawLine(0, 0, 0, c.getHeight());
        g2.drawLine(c.getWidth()-1,0, c.getWidth()-1, c.getHeight());
    }

    public static ComponentUI createUI(JComponent c) {
        return new CustomMenuBarUI();
    }
}
