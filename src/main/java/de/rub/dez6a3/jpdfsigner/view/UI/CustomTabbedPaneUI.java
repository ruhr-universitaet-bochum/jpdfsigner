/*
 * JPDFSigner - Sign PDFs online using smartcards (CustomTabbedPaneUI.java)
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

import de.rub.dez6a3.jpdfsigner.control.Configurator;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.InputStream;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

/**
 *
 * @author dan
 */
public class CustomTabbedPaneUI extends BasicTabbedPaneUI {

    Font tabFont;
    Graphics2D big2;
    FontMetrics fm;

    public CustomTabbedPaneUI() {
        tabFont = new Font("Arial", Font.PLAIN, 10);
        BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        big2 = (Graphics2D) bi.getGraphics();
        big2.setFont(tabFont);
        fm = big2.getFontMetrics();
        UIManager.put("TabbedPane.highlight", LAFProperties.getInstance().getBorderColorHighlight().darker());
        UIManager.put("TabbedPane.contentAreaColor", LAFProperties.getInstance().getPdfBackGroundColor().darker());
        super.textIconGap = 0;
    }

    public static ComponentUI createUI(JComponent c) {
        return new CustomTabbedPaneUI();
    }

    @Override
    protected Insets getContentBorderInsets(int tabPlacement) {
        return new Insets(1, 0, 0, 0);
    }

    @Override
    protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
    }
    private Color hlColorLight = new Color(175, 175, 175);
    private Color hlColorDark = new Color(135, 135, 135);

    @Override
    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (isSelected) {
            g2.setColor(hlColorLight);
        } else {
            g2.setColor(hlColorDark);
        }
        g2.fillRoundRect(x, y, w - 1, h - 1, 12, 12);
        g2.fillRect(x, y + (h / 2), w - 1, h);

        g2.setColor(LAFProperties.getInstance().getBorderColor());
        g2.fillRoundRect(x + 1, y + 1, w - 1, h - 1, 12, 12);
        g2.fillRect(x + 1, y + (h / 2) + 1, w - 1, h);

        Color groundColorLight = null;
        Color groundColorDark = null;
        if (isSelected) {
            groundColorLight = LAFProperties.getInstance().getPdfBackGroundColor().brighter();
            groundColorDark = LAFProperties.getInstance().getPdfBackGroundColor();
        } else {
            groundColorLight = LAFProperties.getInstance().getPdfBackGroundColor();
            groundColorDark = LAFProperties.getInstance().getPdfBackGroundColor().darker();
        }

        GradientPaint gp = new GradientPaint(x + 1, y + 1, groundColorLight, x + 1, h - 2, groundColorDark);
        g2.setPaint(gp);

        g2.fillRoundRect(x + 1, y + 1, w - 2, h, 10, 10);
        g2.fillRect(x + 1, y + (h / 2), w - 2, h);

        int hlspacer = 0;
//        g2.setColor(new Color(255, 255, 255, 25));
//        g2.fillRoundRect(x + 1 + hlspacer, y + 1 + hlspacer, (w - 2) - (hlspacer * 2), ((h - 2) / 2) - (hlspacer), 6, 6);
    }

    @Override
    protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected) {
    }
    private Insets cTabAreaInsets = new Insets(9, 6, 0, 0);
    private int tabHeight = 22;
    int bgHeight = 0;

    @Override
    public void paint(Graphics g, JComponent c) {
        bgHeight = tabHeight + cTabAreaInsets.top + cTabAreaInsets.bottom;
        GradientPaint gp = new GradientPaint(0, 0, new Color(0, 0, 0, 50), 0, bgHeight, new Color(0, 0, 0, 80), false);
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(gp);
        g2.fillRect(0, 0, c.getWidth(), bgHeight);
        super.paint(g2, c);
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();
        tabAreaInsets = cTabAreaInsets;
        selectedTabPadInsets = new Insets(3, 2, 2, 2);
    }

    protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
        return super.calculateTabWidth(tabPlacement, tabIndex, fm);
    }

    protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
//        int vHeight = fontHeight;
//        if (vHeight % 5 > 0) {
//            vHeight += 10;
//        }
        return tabHeight;
    }

    protected FontMetrics getFontMetrics() {
        return fm;
    }
    private Color textShadowColor = new Color(0, 0, 0, 85);
    private Color textColorSelected = Color.white;
    private Color textColorBackground = new Color(220, 220, 220);

    protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics, int tabIndex, String title, Rectangle textRect, boolean isSelected) {
        g.setFont(tabFont);

        int textHeight = getFontMetrics().getHeight();
        int y = textRect.y + (textHeight / 2) + (textHeight / 4);
        int strWidth = getFontMetrics().stringWidth(title);

        if (isSelected) {
            drawText(title, g, textShadowColor, textColorSelected, textRect, y, strWidth);
        } else {
            drawText(title, g, textShadowColor, textColorBackground, textRect, y, strWidth);
        }

    }

    private void drawText(String title, Graphics g, Color textShadow, Color textColor, Rectangle textRect, int y, int strWidth) {
        g.setColor(textShadow);
        g.drawString(title, textRect.x + ((textRect.width / 2) - (strWidth / 2)) + 1, y + 1);
        g.setColor(textColor);
        g.drawString(title, textRect.x + ((textRect.width / 2) - (strWidth / 2)), y);
    }

    protected void paintIcon(Graphics g, int tabPlacement, int tabIndex, Icon icon, Rectangle iconRect, boolean isSelected) {
        if (icon != null) {
            ImageIcon ii = (ImageIcon) icon;
            g.drawImage(ii.getImage(), iconRect.x + 8, iconRect.y, ii.getImageObserver());
        }
    }
    int tabArea = 0;

    @Override
    protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
        tabArea = tabHeight + cTabAreaInsets.top + cTabAreaInsets.bottom;
        paintContentBorderTopEdge(g, tabPlacement, selectedIndex, 0, tabArea, tabPane.getWidth(), tabPane.getHeight() - tabArea);
    }
    private Color borderTopEdgeColorHL = new Color(110, 110, 110);

    @Override
    protected void paintContentBorderTopEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
        if (tabPlacement != TOP
                || selectedIndex < 0
                || (rects[selectedIndex].y + rects[selectedIndex].height + 1 < y)) {
        } else {
            Rectangle selRect = rects[selectedIndex];
            g.setColor(borderTopEdgeColorHL);
            g.drawLine(1 + x, y, selRect.x, y);
            g.setColor(new Color(0, 0, 0, 80));
            g.drawLine(1 + x, y - 1, selRect.x - 1, y - 1);
            if (selRect.x + selRect.width < x + w) {
                g.setColor(borderTopEdgeColorHL);
                g.drawLine(selRect.x + selRect.width, y, x + w, y);
                g.setColor(new Color(0, 0, 0, 80));
                g.drawLine(selRect.x + selRect.width, y - 1, x + w, y - 1);
            } else {
//                g.setColor(LAFProperties.getInstance().getBorderColor());
//                g.drawLine(x + w - 2, y, x + w - 2, y);
            }
        }
    }
}
