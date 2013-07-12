/*
 * JPDFSigner - Sign PDFs online using smartcards (CustomToolBarSeparatorUI.java)
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
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JSeparator;

import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolBarSeparatorUI;
import org.apache.log4j.Logger;

/**
 *
 * @author dan
 */
public class CustomToolBarSeparatorUI extends BasicToolBarSeparatorUI {

    private ImageIcon separator = null;
    public static Logger log = Logger.getLogger(CustomToolBarSeparatorUI.class);

    public CustomToolBarSeparatorUI() {
        try {
            separator = new ImageIcon(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/toolbar_separator.png"));
        } catch (Exception e) {
            separator = new ImageIcon();
            log.warn("Cannot set ToolBar separator-icon. - " + e);
        }
    }

    public static ComponentUI createUI(JComponent c) {
        return new CustomToolBarSeparatorUI();
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        g.drawImage(separator.getImage(), 0, 0, separator.getImageObserver());
    }

    @Override
    protected void installDefaults(JSeparator s) {
        ((JToolBar.Separator) s).setSeparatorSize(new Dimension(separator.getIconWidth(), separator.getIconHeight()));
    }
}
