/*
 * JPDFSigner - Sign PDFs online using smartcards (CustomMetalRootPaneUI.java)
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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalRootPaneUI;
import javax.swing.text.Utilities;

/**
 *
 * @author dan
 */
public class CustomMetalRootPaneUI extends MetalRootPaneUI {

    private JRootPane root;

    public static ComponentUI createUI(JComponent c) {
        return new CustomMetalRootPaneUI();
    }
    public void propertyChange(PropertyChangeEvent e) {
        root = (JRootPane) e.getSource();
    }

}
