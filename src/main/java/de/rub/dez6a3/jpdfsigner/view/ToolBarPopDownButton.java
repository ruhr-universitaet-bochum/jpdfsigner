/*
 * JPDFSigner - Sign PDFs online using smartcards (ToolBarPopDownButton.java)
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
package de.rub.dez6a3.jpdfsigner.view;

import de.rub.dez6a3.jpdfsigner.control.language.LanguageFactory;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.PopupMenuUI;
import javax.swing.plaf.basic.BasicPopupMenuUI;

/**
 *
 * @author dan
 */
public class ToolBarPopDownButton extends JButton {

    private boolean isPopUpFlushedLeft = true;
    private boolean poppedUp = false;
    private JPopupMenu signatureValidPopUp = null;
    private BufferedImage bi = null;
    private Graphics2D big2 = null;
    private Area area1 = null;
    private Area area2 = null;
    private Area areaCombined = null;
    private Polygon arrow = null;
    private int arrowStartPtX = 0;
    private int arrowStartPtY = 0;

    public ToolBarPopDownButton() {
        setOpaque(false);
        setBorderPainted(false);

        signatureValidPopUp = new JPopupMenu();
        signatureValidPopUp.addPopupMenuListener(new PopupMenuListener() {

            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                poppedUp = true;
                repaint();
            }

            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                poppedUp = false;
                repaint();
            }

            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });
        signatureValidPopUp.setOpaque(false);
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                if (isEnabled()) {
                    Insets borderInsets = signatureValidPopUp.getBorder().getBorderInsets(signatureValidPopUp);

                    int popupPosX = 0;
                    if (!isPopUpFlushedLeft) {
                        popupPosX = getWidth() - signatureValidPopUp.getPreferredSize().width - borderInsets.left - borderInsets.right;
                    }
                    signatureValidPopUp.show(getThis(), popupPosX, getHeight());
                }
            }
        });
    }

    public void setPopupFlushedLeft(boolean param) {
        isPopUpFlushedLeft = param;
    }

    public void setPopupUI(PopupMenuUI ui) {
        signatureValidPopUp.setUI(ui);
    }

    public void addToPopup(JMenuItem item) {
        signatureValidPopUp.add(item);
//        popupPosX = getWidth() - signatureValidPopUp.getPreferredSize().width;
    }

    public void addToPopup(Component c) {
        signatureValidPopUp.add(c);
//        popupPosX = getWidth() - signatureValidPopUp.getPreferredSize().width;
    }

    public void addSeparatorToPopup() {
        signatureValidPopUp.addSeparator();
    }

    private JButton getThis() {
        return this;
    }

    @Override
    public void paint(Graphics g) {
        bi = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        big2 = (Graphics2D) bi.getGraphics();
        big2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (!poppedUp) {
            super.paint(big2);
        } else {

            big2.setColor(new Color(85, 85, 85));
            area1 = new Area(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
            area2 = new Area(new Rectangle2D.Float(0, getHeight() / 2, getWidth(), (getHeight() / 2) + 1));
            areaCombined = new Area();
            areaCombined.add(area1);
            areaCombined.add(area2);
            big2.fill(areaCombined);

            big2.setColor(new Color(255, 255, 255));
            area1 = new Area(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 1, 9, 9));
            area2 = new Area(new Rectangle2D.Float(1, getHeight() / 2, getWidth() - 2, (getHeight() / 2) + 1));
            areaCombined = new Area();
            areaCombined.add(area1);
            areaCombined.add(area2);
            big2.fill(areaCombined);

            big2.setColor(new Color(245, 245, 245));
            area1 = new Area(new RoundRectangle2D.Float(2, 2, getWidth() - 4, getHeight() - 2, 8, 8));
            area2 = new Area(new Rectangle2D.Float(2, getHeight() / 2, getWidth() - 4, (getHeight() / 2) + 1));
            areaCombined = new Area();
            areaCombined.add(area1);
            areaCombined.add(area2);
            big2.fill(areaCombined);

            super.paint(big2);
        }
        if (isEnabled()) {
            arrowStartPtX = (getWidth() / 2) - 3;
            arrowStartPtY = getHeight() - (getHeight() / 4);
            big2.setColor(new Color(60, 60, 60));
            arrow = new Polygon();
            arrow.addPoint(arrowStartPtX, arrowStartPtY);
            arrow.addPoint(arrowStartPtX + 6, arrowStartPtY);
            arrow.addPoint(arrowStartPtX + 3, arrowStartPtY + 3);
            big2.fillPolygon(arrow);
        }
            g.drawImage(bi, 0, 0, this);
    }
}
