/*
 * JPDFSigner - Sign PDFs online using smartcards (CustomLabelUI.java)
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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.LabelUI;
import javax.swing.plaf.basic.BasicLabelUI;

/**
 *
 * @author dan
 */
public class CustomLabelUI extends BasicLabelUI {

    public static ComponentUI createUI(JComponent c) {
        return new CustomLabelUI();
    }

//    @Override
//    protected void installDefaults(JLabel c) {
//        c.setFont(new Font("Arial", Font.PLAIN, 12));
//    }

    protected void paintEnabledText(JLabel l, Graphics g, String s, int textX, int textY) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color origColor = g2.getColor();

        g2.setColor(new Color(0, 0, 0, 150));
        g2.drawString(s, textX + 1, textY + 1);

        g2.setColor(origColor);
        g2.drawString(s, textX, textY);
    }
}
