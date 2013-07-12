/*
 * JPDFSigner - Sign PDFs online using smartcards (DecoratedJOptionPane.java)
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
import de.rub.dez6a3.jpdfsigner.control.Configurator;
import de.rub.dez6a3.jpdfsigner.control.GlobalData;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author dan
 */
public class DecoratedJOptionPane extends JOptionPane {

    private static JFrame translucentEmptyParent = null;

    public static int showConfirmDialog(Component parentComponent, Object message, String title, int optionType, int messageType) {
        if (translucentEmptyParent == null) {
            translucentEmptyParent = new JFrame(AWTUtilitiesValidator.getInstance().getTranslucentGraphicsConfig());
        }

        if (parentComponent == null) {
            parentComponent = translucentEmptyParent;
        }
        JOptionPane oPane = new JOptionPane(((String) message).replace("-br-", "\n"), messageType, optionType) {

            @Override
            public JDialog createDialog(Component parentComponent, String title) throws HeadlessException {
                Frame toUse = getFrameForComponent(parentComponent);
                if (toUse == null) {
                    toUse = getRootFrame();
                }

                JDialog dialog = new JDialog(toUse, title) {

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
                inputValue = UNINITIALIZED_VALUE;
                value = UNINITIALIZED_VALUE;


                dialog.getContentPane().add(this);
                dialog.setModal(true);
                dialog.setResizable(false);
                dialog.pack();
                dialog.setLocationRelativeTo(parentComponent);

                addPropertyChangeListener(new ValuePropertyHandler(dialog));
                return dialog;

            }
        };
        JDialog oDiag = oPane.createDialog(parentComponent, title);
        oDiag.setResizable(false);
        oDiag.dispose();
        oDiag.setUndecorated(true);
        if (AWTUtilitiesValidator.getInstance().isTranslucencySupported()) {
            AWTUtilitiesValidator.getInstance().setWindowOpaque(oDiag, false);
        }
        oDiag.pack();
        oDiag.setVisible(true);
        if (oPane.getValue() instanceof Integer) {
            return (Integer) oPane.getValue();
        } else {
            return -1;
        }
    }

    public static void showMessageDialog(Component parentComponent, Object message, String title, int messageType) {
        if (translucentEmptyParent == null) {
            translucentEmptyParent = new JFrame(AWTUtilitiesValidator.getInstance().getTranslucentGraphicsConfig());
        }

        if (parentComponent == null) {
            parentComponent = translucentEmptyParent;
        }

        JOptionPane oPane = new JOptionPane(((String) message).replace("-br-", "\n"), messageType) {

            @Override
            public JDialog createDialog(Component parentComponent, String title) throws HeadlessException {
                Frame toUse = getFrameForComponent(parentComponent);
                if (toUse == null) {
                    toUse = getRootFrame();
                }

                JDialog dialog = new JDialog(toUse, title) {

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
                inputValue = UNINITIALIZED_VALUE;
                value = UNINITIALIZED_VALUE;


                dialog.getContentPane().add(this);
                dialog.setModal(true);
                dialog.setResizable(false);
                dialog.pack();
                dialog.setLocationRelativeTo(parentComponent);

                addPropertyChangeListener(new ValuePropertyHandler(dialog));
                return dialog;

            }
        };
        JDialog oDiag = oPane.createDialog(parentComponent, title);
        oDiag.setResizable(false);
        oDiag.dispose();
        oDiag.setUndecorated(true);
        if (AWTUtilitiesValidator.getInstance().isTranslucencySupported()) {
            AWTUtilitiesValidator.getInstance().setWindowOpaque(oDiag, false);
        }
        oDiag.pack();
        oDiag.setVisible(true);
    }

    private static class ValuePropertyHandler implements PropertyChangeListener {

        JDialog dialog;

        ValuePropertyHandler(JDialog d) {
            dialog = d;
        }

        public void propertyChange(PropertyChangeEvent p) {
            String prop = p.getPropertyName();
            Object val = p.getNewValue();
            if (prop.equals(VALUE_PROPERTY) && val != null
                    && val != UNINITIALIZED_VALUE) {
                dialog.setVisible(false);
            }
        }
    }
}
