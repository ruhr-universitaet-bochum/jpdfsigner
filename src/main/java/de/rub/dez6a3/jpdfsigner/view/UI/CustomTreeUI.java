/*
 * JPDFSigner - Sign PDFs online using smartcards (CustomTreeUI.java)
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
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author dan
 */
public class CustomTreeUI extends BasicTreeUI {

    private Icon certIco = null;
    private Icon collapsedIco = null;
    private Icon expandedIco = null;
    private JTree ctree = null;

    public CustomTreeUI() {
        certIco = new ImageIcon(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/certificate.png"));
        collapsedIco = new ImageIcon(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/jtreeCollapsed.png"));
        expandedIco = new ImageIcon(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/jtreeExpanded.png"));
    }

    public static ComponentUI createUI(JComponent c) {
        return new CustomTreeUI();
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g;

        int rowHeight = getRowHeight();
        boolean cSwitch = true;
        int i=0;
        while(i*rowHeight<= c.getHeight()) {
            if (cSwitch) {
                g2.setColor(new Color(0, 0, 0, 18));
                g2.fillRect(0, rowHeight * i, c.getWidth(), rowHeight);
                cSwitch=false;
            }else{
                cSwitch=true;
            }
            i++;
        }

        super.paint(g, c);
    }

    @Override
    public void installUI(JComponent c) {
        ctree = (JTree) c;
        UIManager.put("Tree.paintLines", false);
        ctree.setRowHeight(17);

        ctree.setOpaque(false);
        ctree.setCellRenderer(new DefaultTreeCellRenderer() {

            @Override
            public Color getBackground() {
                return null;
            }

            @Override
            public Color getBackgroundNonSelectionColor() {
                return null;
            }

            @Override
            public Color getTextNonSelectionColor() {
//                return Color.white;
                return Color.black;
            }

            @Override
            public Icon getLeafIcon() {             //icon neben jedem nicht expandierbaren objekt
                return null;
            }

            @Override
            public Icon getOpenIcon() {             //icon neben expandierbaren objekt (opened)
                return certIco;
            }

            @Override
            public Icon getClosedIcon() {           //icon neben expandierbaren objekt (closed)
                return certIco;
            }
        });
        super.installUI(ctree);
    }

    @Override
    public Icon getCollapsedIcon() {
        return collapsedIco;
    }

    @Override
    public Icon getExpandedIcon() {
        return expandedIco;
    }
}
