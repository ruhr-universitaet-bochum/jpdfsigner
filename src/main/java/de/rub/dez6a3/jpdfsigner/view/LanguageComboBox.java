/*
 * JPDFSigner - Sign PDFs online using smartcards (LanguageComboBox.java)
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

import de.rub.dez6a3.jpdfsigner.control.language.Language;
import de.rub.dez6a3.jpdfsigner.view.UI.CustomComboBoxUI;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.ComboBoxEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicEditorPaneUI;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.plaf.metal.MetalComboBoxUI;

/**
 *
 * @author dan
 */
public class LanguageComboBox extends JComboBox {

    private JButton arrowBtn = null;

    public LanguageComboBox() {
        arrowBtn = new JButton(){};
        setUI(new BasicComboBoxUI() {

            @Override
            protected JButton createArrowButton() {
                return arrowBtn;
            }

            @Override
            public void paint(Graphics g, JComponent c) {
                super.paint(g,c);
            }

            @Override
            public void installUI(JComponent c) {
                super.installUI(c);
                Dimension dim = new Dimension(50, 30);
                c.setMaximumSize(dim);
                c.setMinimumSize(dim);
                c.setSize(dim);
                c.setPreferredSize(dim);
            }
        });
        setRenderer(new ListCellRenderer() {

            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Language lang = (Language) value;
                JLabel imageCont = new JLabel();
                imageCont.setOpaque(false);
                imageCont.setIcon(lang.getIcon());
                imageCont.setText(lang.getLanguageInfo());
                arrowBtn.setIcon(lang.getIcon());
                return imageCont;
            }
        });
//        arrowBtn.setIcon();
    }
private void removeListeners(Component c){

    MouseListener[] mls = c.getMouseListeners();
    for(MouseListener ml: mls){
        c.removeMouseListener(ml);
    }
    if(c instanceof JComponent && !(c instanceof JButton)){
        Component[] comps = ((JComponent)c).getComponents();
        for(Component comp: comps){
            removeListeners(comp);
        }
    }
}

    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }
}
