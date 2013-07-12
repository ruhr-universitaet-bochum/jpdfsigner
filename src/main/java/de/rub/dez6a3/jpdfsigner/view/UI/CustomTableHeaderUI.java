/*
 * JPDFSigner - Sign PDFs online using smartcards (CustomTableHeaderUI.java)
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
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author dan
 */
public class CustomTableHeaderUI extends BasicTableHeaderUI {

    public static ComponentUI createUI(JComponent c) {
        return new CustomTableHeaderUI();
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        JTableHeader th = (JTableHeader) c;
        th.setOpaque(false);
        th.setBackground(new Color(0, 0, 0, 0));
        th.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        th.setDefaultRenderer(new TableCellRenderer() {

            public Component getTableCellRendererComponent(final JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel l = new JLabel((String) value) {

                    @Override
                    public void paint(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g;
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        g2.setColor(LAFProperties.getInstance().getMainFrameBackgroundColor());
                        g2.fillRect(0, 0, getWidth(), getWidth());

                        Area shapeBtm = new Area(new Rectangle2D.Float(0, getHeight() / 2, getWidth(), getHeight()));
                        Area shapeTop = new Area(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                        shapeTop.add(shapeBtm);
                        GradientPaint gp = new GradientPaint(0, 0, new Color(100, 100, 100), 0, getHeight(), LAFProperties.getInstance().getTitleBarBackground(), false);
                        g2.setPaint(gp);
                        g2.fill(shapeTop);

                        shapeBtm = new Area(new Rectangle2D.Float(0, getHeight() / 2, getWidth() - 1 , getHeight()));
                        shapeTop = new Area(new RoundRectangle2D.Float(0, 1, getWidth() - 1, getHeight() - 1, 20, 20));
                        shapeTop.add(shapeBtm);
                        GradientPaint gp2 = new GradientPaint(0, 0, LAFProperties.getInstance().getBorderColorHighlight(), 0, getHeight(), LAFProperties.getInstance().getTitleBarBackground(), false);
                        g2.setPaint(gp2);
                        g2.fill(shapeTop);

                        shapeBtm = new Area(new Rectangle2D.Float(1, getHeight() / 2, getWidth() - 3, getHeight()));
                        shapeTop = new Area(new RoundRectangle2D.Float(1, 2, getWidth() - 3, getHeight() - 2, 20, 20));
                        shapeTop.add(shapeBtm);
                        GradientPaint gp3 = new GradientPaint(0, 0, new Color(193, 193, 193), 0, getHeight(), LAFProperties.getInstance().getTitleBarBackground(), false);
                        g2.setPaint(gp3);
                        g2.fill(shapeTop);

                        g2.setColor(Color.white);
                        g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);

                        g2.setColor(new Color(230, 230, 230));
                        g2.setFont(getFont());
                        FontMetrics fm = getFontMetrics(getFont());
                        int fontX = (getWidth() / 2) - (fm.stringWidth(getText()) / 2);
                        int fontY = fm.getHeight() + (fm.getHeight() / 2) - 1;
                        g2.drawString(getText(), fontX, fontY);
                        g2.setColor(new Color(35, 35, 35));
                        g2.drawString(getText(), fontX - 1, fontY - 1);
                    }
                };
                l.setHorizontalAlignment(JLabel.CENTER);
                l.setFont(table.getFont().deriveFont(Font.BOLD));
                l.setBorder(BorderFactory.createEmptyBorder(10, 2, 4, 2));
                l.setOpaque(false);
                return l;
            }
        });
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        g.setColor(Color.gray);
        g.fillRect(0, 0, c.getWidth(), c.getHeight());
        super.paint(g, c);
    }
}
