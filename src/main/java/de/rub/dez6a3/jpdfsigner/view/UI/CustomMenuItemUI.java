/*
 * JPDFSigner - Sign PDFs online using smartcards (CustomMenuItemUI.java)
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

/**
 *
 * @author dan/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rub.dez6a3.jpdfsigner.view.UI;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuItemUI;

/**
 *
 * @author dan
 */
public class CustomMenuItemUI extends BasicMenuItemUI {

    private Color foreground;
    private Color foregroundShadow;
    private Color foregroundLight;
    private Color foregroundShadowLight;
    static Timer openMenuTimer;

    public CustomMenuItemUI() {
        super();
        foreground = new Color(40, 40, 40);
        foregroundShadow = new Color(240, 240, 240);
        foregroundLight = new Color(245, 245, 245);
        foregroundShadowLight = new Color(50, 50, 50);
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        c.setForeground(foreground);
        Font font = new Font("Liberation Sans", Font.PLAIN, 11);
        c.setFont(font);
        c.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
    }

    @Override
    protected void paintBackground(Graphics g, JMenuItem menuItem, Color bgColor) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.black);
        ButtonModel model = menuItem.getModel();
        int menuWidth = menuItem.getWidth();
        int menuHeight = menuItem.getHeight();
        if (model.isArmed()) {
            GradientPaint gp = new GradientPaint(0, 0, new Color(120, 120, 120), 0, menuHeight, new Color(87, 87, 87));
            g2.setPaint(gp);
            g2.fillRect(0, 0, menuWidth, menuHeight);
            g2.setColor(new Color(200,200,200));
        } else {
            GradientPaint gp2 = new GradientPaint(0, 0, new Color(230, 230, 230), 0, menuHeight, new Color(215, 215, 215));
            g2.setPaint(gp2);
            g2.fillRect(0, 0, menuWidth, menuHeight);
        }
    }

    @Override
    protected void paintText(Graphics g, JMenuItem menuItem, Rectangle textRect, String text) {
        ButtonModel model = menuItem.getModel();
        if (model.isArmed()) {
            g.setColor(foregroundShadowLight);
            g.drawString(text, textRect.x + 1, ((menuItem.getHeight() / 2) + (g.getFontMetrics().getHeight() / 3)) + 1);
            g.setColor(foregroundLight);
            g.drawString(text, textRect.x, ((menuItem.getHeight() / 2) + (g.getFontMetrics().getHeight() / 3)));
        } else {
            g.setColor(foregroundShadow);
            g.drawString(text, textRect.x + 1, ((menuItem.getHeight() / 2) + (g.getFontMetrics().getHeight() / 3)) + 1);
            g.setColor(foreground);
            g.drawString(text, textRect.x, ((menuItem.getHeight() / 2) + (g.getFontMetrics().getHeight() / 3)));
        }
    }

    public void paint(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        super.paint(g2, c);
    }

    public static ComponentUI createUI(JComponent c) {
        return new CustomMenuItemUI();
    }
}
