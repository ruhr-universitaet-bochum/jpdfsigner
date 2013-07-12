/*
 * JPDFSigner - Sign PDFs online using smartcards (CustomSplitPaneDivider.java)
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

/**
 *
 * @author dan
 */
public class CustomSplitPaneDivider extends BasicSplitPaneDivider {

    private int orientation = 0;
    private Color color1 = new Color(185, 185, 185);
    private Color color2 = new Color(155, 155, 155);

    public CustomSplitPaneDivider(BasicSplitPaneUI ui, int orientation) {
        super(ui);
        this.orientation = orientation;
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        switch (orientation) {
            case 0:
                paintHorizontalDevider(g2);
                break;
            case 1:
                paintVerticalDevider(g2);
        }
    }

    private void paintHorizontalDevider(Graphics2D g2) {
        GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2, true);
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setColor(new Color(255, 255, 255, 180));
        g2.drawLine(0, 0, getWidth(), 0);

        g2.setColor(new Color(0, 0, 0, 135));
        g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);

        int dotSpace = 3;
        int start = getWidth() / 3;
        int end = (getWidth() / 3) * 2;

        for (int i = start; i < end; i += dotSpace) {
            g2.setColor(new Color(100, 100, 100));
            g2.drawLine(i, (getHeight() / 2) - 1, i, (getHeight() / 2) - 1);
            g2.setColor(new Color(210, 210, 210));
            g2.drawLine(i + 1, (getHeight() / 2), i + 1, (getHeight() / 2));
        }
    }

    private void paintVerticalDevider(Graphics2D g2) {
        GradientPaint gp = new GradientPaint(0, 0, color1, getWidth(), 0, color2, true);
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setColor(new Color(0, 0, 0, 135));
        g2.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());

        g2.setColor(new Color(255, 255, 255, 180));
        g2.drawLine(0, 0, 0, getHeight());

        int dotSpace = 3;
        int start = getHeight() / 3;
        int end = (getHeight() / 3) * 2;

        for (int i = start; i < end; i += dotSpace) {
            g2.setColor(new Color(100, 100, 100));
            g2.drawLine((getWidth() / 2) - 1, i, (getWidth() / 2) - 1, i);
            g2.setColor(new Color(210, 210, 210));
            g2.drawLine((getWidth() / 2), i + 1, (getWidth() / 2), i + 1);
        }
    }
}
