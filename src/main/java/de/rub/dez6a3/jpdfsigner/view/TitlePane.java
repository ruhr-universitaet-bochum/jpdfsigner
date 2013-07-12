/*
 * JPDFSigner - Sign PDFs online using smartcards (TitlePane.java)
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


import java.awt.Color;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRootPane;

/**
 *
 * @author dan
 */
public class TitlePane extends JPanel {

    private JRootPane rootp;
    private int x = 0;
    private int y =0;

    public TitlePane(JRootPane rootp) {
        super();
        this.rootp = rootp;
        Dimension dim = new Dimension(50, 50);
        setPreferredSize(dim);
        setSize(dim);
        setBackground(Color.red);

        installDragListener();
//        initActions();
    }

    private void installDragListener() {
        addMouseMotionListener(new MouseMotionAdapter() {

            public void mouseDragged(MouseEvent e) {
                rootp.getParent().setLocation(MouseInfo.getPointerInfo().getLocation().x-x, MouseInfo.getPointerInfo().getLocation().y-y);
            }
        });
        addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e) {
                x=e.getX();
                y=e.getY();
            }
        });
    }

    private void initActions(){
        add(new JButton(new MaximizeAction()));

    }

    private class MaximizeAction extends AbstractAction{

        public MaximizeAction(){
            super("Maximize");
        }

        public void actionPerformed(ActionEvent e) {
//            throw new UnsupportedOperationException("Not supported yet.");
        }

    }
}
