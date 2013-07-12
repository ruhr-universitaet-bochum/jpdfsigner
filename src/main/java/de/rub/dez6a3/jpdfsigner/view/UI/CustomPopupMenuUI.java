/*
 * JPDFSigner - Sign PDFs online using smartcards (CustomPopupMenuUI.java)
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

import de.rub.dez6a3.jpdfsigner.view.UI.borders.PopupMenuBorder;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.geom.Dimension2D;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.Popup;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPopupMenuUI;
import javax.swing.plaf.metal.MetalBorders.ToolBarBorder;

/**
 *
 * @author dan
 */
public class CustomPopupMenuUI extends BasicPopupMenuUI {

    private int btnSpacer = 0;

    public Popup getPopup(JPopupMenu popup, int x, int y) {
        try {
            JButton invoker = (JButton) popup.getInvoker();
            btnSpacer = invoker.getWidth();
        } catch (Exception e) {
        }
        popup.setBorder(new AbstractBorder() {

            private Insets insets = new Insets(2, 2, 2, 2);

            @Override
            public Insets getBorderInsets(Component c) {
                return insets;
            }

            @Override
            public Insets getBorderInsets(Component c, Insets insets) {
                return insets;
            }

            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                g.setColor(new Color(85, 85, 85));
                g.fillRect(0, 0, width, height);
                g.setColor(Color.white);
                g.fillRect(x + 1, y + 1, width - 2, height - 2);
                g.setColor(new Color(245, 245, 245));
                g.fillRect(x + 1, y, btnSpacer - 2, 2);
            }
        });
        Container p = popup.getParent();
        Popup pp = super.getPopup(popup, x, y);
        JPanel panel = (JPanel) popup.getParent();
        panel.setBorder(new PopupMenuBorder(0, 15));
        panel.setOpaque(false);
        return pp;
    }

    public static ComponentUI createUI(JComponent c) {
        return new CustomPopupMenuUI();
    }
}
