/*
 * JPDFSigner - Sign PDFs online using smartcards (CustomProgressBarUI.java)
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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicProgressBarUI;

/**
 *
 * @author dan
 */
public class CustomProgressBarUI extends BasicProgressBarUI {

    public static ComponentUI createUI(JComponent c) {
        return new CustomProgressBarUI();
    }
    private Color outlineColor = new Color(75, 75, 75);
    private Color highlightColor = new Color(230,230,230);
    private Color bgColor = new Color(210,210,210);

    @Override
    public void paint(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int arc = 0;
        if (c.getWidth() > c.getHeight()) {
            arc = c.getHeight();
        } else {
            arc = c.getWidth();
        }
        g2.setColor(outlineColor);
        g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), arc, arc);
        g2.setColor(highlightColor);
        g2.fillRoundRect(1, 1, c.getWidth()-2, c.getHeight()-2, arc, arc);
        g2.setColor(bgColor);
        g2.fillRoundRect(2, 2, c.getWidth()-4, c.getHeight()-4, arc, arc);
    }

    @Override
    public void installUI(JComponent c) {
        JProgressBar pb = (JProgressBar) c;
        pb.setOpaque(false);
        pb.setBorderPainted(false);
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        return c.getPreferredSize();
    }


}
