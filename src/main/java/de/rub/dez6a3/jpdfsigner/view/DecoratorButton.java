/*
 * JPDFSigner - Sign PDFs online using smartcards (DecoratorButton.java)
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

import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 *
 * @author dan
 */
public class DecoratorButton extends JButton {

    private ImageIcon compImage = null;
    private ImageIcon mouseOverImage = null;
    private ImageIcon mousePressedImage = null;
    
    private boolean mouseOver = false;
    private boolean mousePressed = false;

    public DecoratorButton() {
        installListeners();
    }

    public ImageIcon getCompImage() {
        return compImage;
    }

    public void setCompImage(ImageIcon compImage) {
        this.compImage = compImage;
    }

    public ImageIcon getMouseOverImage() {
        return mouseOverImage;
    }

    public void setMouseOverImage(ImageIcon mouseOverImage) {
        this.mouseOverImage = mouseOverImage;
    }

    public ImageIcon getMousePressedImage() {
        return mousePressedImage;
    }

    public void setMousePressedImage(ImageIcon mousePressedImage) {
        this.mousePressedImage = mousePressedImage;
    }

    private void installListeners() {
        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                mousePressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mousePressed = false;
                repaint();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                mouseOver = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                mouseOver = false;
                repaint();
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (!mouseOver && !mousePressed) {
            drawImageIcon(g, compImage);
        } else if (mousePressed) {
            drawImageIcon(g, mousePressedImage);
        } else if (mouseOver) {
            drawImageIcon(g, mouseOverImage);
        }
    }

    private void drawImageIcon(Graphics g, ImageIcon img) {
        g.drawImage(img.getImage(), (getWidth() / 2) - (img.getIconWidth() / 2), (getHeight() / 2) - (img.getIconHeight() / 2), this);
    }
}
