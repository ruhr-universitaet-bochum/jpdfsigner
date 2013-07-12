/*
 * JPDFSigner - Sign PDFs online using smartcards (CustomPopupMenuSeparatorUI.java)
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
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPopupMenuSeparatorUI;

/**
 *
 * @author dan
 */
public class CustomPopupMenuSeparatorUI extends BasicPopupMenuSeparatorUI{

    public static ComponentUI createUI(JComponent c) {
        return new CustomPopupMenuSeparatorUI();
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        g.setColor(new Color(150,150,150));
        g.fillRect(0, 0, c.getWidth(), c.getHeight()/2);
        g.setColor(new Color(230,230,230));
        g.fillRect(0, (c.getHeight()/2)+1, c.getWidth(), c.getHeight()/2);
    }
}
