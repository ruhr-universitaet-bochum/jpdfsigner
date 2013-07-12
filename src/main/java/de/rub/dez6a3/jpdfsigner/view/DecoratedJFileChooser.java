/*
 * JPDFSigner - Sign PDFs online using smartcards (DecoratedJFileChooser.java)
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

import de.rub.dez6a3.jpdfsigner.control.AWTUtilitiesValidator;
import de.rub.dez6a3.jpdfsigner.control.GlobalData;
import de.rub.dez6a3.jpdfsigner.view.UI.LAFProperties;
import de.rub.dez6a3.jpdfsigner.view.UI.borders.HighlightBorder;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

/**
 *
 * @author dan
 */
public class DecoratedJFileChooser extends JFileChooser {

    public DecoratedJFileChooser() {
        super();
    }

    @Override
    protected JDialog createDialog(Component parent) throws HeadlessException {
        Frame toUse = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);
        String title = "Pfad auswählen ...";
        if (getDialogTitle() != null) {
            title = getDialogTitle();
        }
        JDialog dialog = new JDialog(toUse, title) {

            @Override
            public void paint(Graphics g) {
                if (AWTUtilitiesValidator.getInstance().isAWTUtlitiesAvailable() && !AWTUtilitiesValidator.getInstance().isTranslucencySupported()) {
                    Area shape = new Area();
                    Area shapeTop = new Area(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 13, 13));
                    shape.add(shapeTop);
                    AWTUtilitiesValidator.getInstance().setWindowShape(this, shape);
                }
                super.paint(g);
            }
        };
        dialog.setBackground(LAFProperties.getInstance().getDialogBackground());
        dialog.setUndecorated(true);
        if (AWTUtilitiesValidator.getInstance().isTranslucencySupported()) {
            AWTUtilitiesValidator.getInstance().setWindowOpaque(dialog, false);
        }
        Dimension dim = new Dimension(600, 400);
        dialog.setSize(dim);
        dialog.setPreferredSize(dim);
        setSelectedFile(null);
        dialog.getContentPane().add(this);
        dialog.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                cancelSelection();
            }
        });
        dialog.setModal(true);
        dialog.invalidate();
        dialog.repaint();
        return dialog;
    }
}
