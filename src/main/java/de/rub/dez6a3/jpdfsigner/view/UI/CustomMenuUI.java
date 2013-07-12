/*
 * JPDFSigner - Sign PDFs online using smartcards (CustomMenuUI.java)
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

import com.sun.org.apache.regexp.internal.REProgram;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuUI;

/**
 *
 * @author dan
 */
public class CustomMenuUI extends BasicMenuUI {

    private Color foreground;
    private Color foregroundShadow;
    private Color foregroundLight;
    private Color foregroundShadowLight;
    private boolean mouseOver = false;
    static Timer openMenuTimer;

    public CustomMenuUI() {
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
        c.setBorder(BorderFactory.createEmptyBorder(3, 5, 1, 3));
    }

    @Override
    protected void paintBackground(Graphics g,
            JMenuItem menuItem,
            Color bgColor) {
        Graphics2D g2 = (Graphics2D) g;
        ButtonModel model = menuItem.getModel();
        int menuWidth = menuItem.getWidth();
        int menuHeight = menuItem.getHeight();
        if(mouseOver){
            g2.setColor(new Color(0,0,0,58));
            g2.fillRoundRect(2, 1, menuWidth - 4, menuHeight - 2, 8, 8);
        }
        if (model.isSelected()) {
            g2.setColor(new Color(70, 70, 70));
            g2.fillRoundRect(2, 1, menuWidth - 4, menuHeight - 2, 8, 8);
            GradientPaint gp = new GradientPaint(0, 0, new Color(120, 120, 120), 0, menuHeight, new Color(87, 87, 87));
            g2.setPaint(gp);
            g2.fillRoundRect(3, 2, menuWidth - 6, menuHeight - 3, 8, 8);
        }
    }

    @Override
    protected void paintText(Graphics g, JMenuItem menuItem, Rectangle textRect, String text) {
        ButtonModel model = menuItem.getModel();
        if (model.isSelected() || mouseOver) {
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

    @Override
    protected void installListeners() {
        super.installListeners();
        menuItem.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                mouseOver = true;
                menuItem.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                mouseOver = false;
                menuItem.repaint();
            }

        });
    }

    public static ComponentUI createUI(JComponent c) {
        return new CustomMenuUI();
    }
}
