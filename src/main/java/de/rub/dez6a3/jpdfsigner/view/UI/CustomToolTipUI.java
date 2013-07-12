/*
 * JPDFSigner - Sign PDFs online using smartcards (CustomToolTipUI.java)
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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolTipUI;

/**
 *
 * @author dan
 */
public class CustomToolTipUI extends BasicToolTipUI {

    private BufferedImage bi = null;
    private Graphics2D big2 = null;

    public static ComponentUI createUI(JComponent c) {
        return new CustomToolTipUI();
    }

    @Override
    public void installUI(JComponent c) {
        c.setOpaque(false);
        c.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        c.setBackground(new Color(0, 0, 0, 0));
        c.setFont(new Font("Arial", Font.PLAIN, 10));
        super.installUI(c);
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        bi = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_ARGB);
        big2 = (Graphics2D) bi.getGraphics();
        big2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //outer
        big2.setColor(new Color(80,80,80));
        big2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 6,6);
        //hl
        big2.setColor(new Color(255,255,255));
        big2.fillRoundRect(1, 1, c.getWidth()-2, c.getHeight()-2, 5,5);
        //inner area
        Graphics2D g2 = (Graphics2D) big2;
        GradientPaint gp = new GradientPaint(0, 0, new Color(235,235,235), 0, c.getHeight(), new Color(192,192,192), false);
        g2.setPaint(gp);
        big2.fillRoundRect(2, 2, c.getWidth()-4, c.getHeight()-4, 4,4);
        //draw text
        big2.setColor(new Color(230,230,230));
        big2.setFont(c.getFont());
        FontMetrics fm = big2.getFontMetrics();
        big2.drawString(((JToolTip)c).getTipText(), c.getBorder().getBorderInsets(c).left+3, fm.getHeight());
        big2.setColor(new Color(60,60,60));
        big2.drawString(((JToolTip)c).getTipText(), c.getBorder().getBorderInsets(c).left+2, fm.getHeight()-1);

        g.drawImage(bi, 0, 0, null);
//        super.paint(big2, c);
    }
}
