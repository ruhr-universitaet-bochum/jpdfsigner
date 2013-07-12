/*
 * JPDFSigner - Sign PDFs online using smartcards (CustomRootPaneUI.java)
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

import de.rub.dez6a3.jpdfsigner.control.ParamValidator;
import de.rub.dez6a3.jpdfsigner.view.UI.borders.JDialogTitledBorder;
import java.beans.PropertyChangeEvent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicRootPaneUI;
import de.rub.dez6a3.jpdfsigner.view.UI.borders.JFrameTitledComponentBorder;
import java.awt.Container;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JRootPane;

/**
 *
 * @author dan
 */
public class CustomRootPaneUI extends BasicRootPaneUI {

    private JRootPane rootPane;
    private Container parent;

    public static ComponentUI createUI(JComponent c) {
        return new CustomRootPaneUI();
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
    }

    public void propertyChange(PropertyChangeEvent e) {
        super.propertyChange(e);
        rootPane = (JRootPane) e.getSource();
        String propertyName = e.getPropertyName();
        if (propertyName.equals("ancestor")) {
            parent = rootPane.getParent();
            if (parent instanceof JFrame && !ParamValidator.getInstance().getEmbedded()) {
                rootPane.setBorder(JFrameTitledComponentBorder.getInstance(rootPane, new Insets(25, 2, 20, 2)));

            } else if (parent instanceof JDialog) {
                rootPane.setBorder(new JDialogTitledBorder(rootPane, new Insets(25, 3, 9, 3)));
            }
        }
    }
}

