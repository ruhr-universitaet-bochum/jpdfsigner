/*
 * JPDFSigner - Sign PDFs online using smartcards (DragableScrollPaneMouseListener.java)
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

import de.rub.dez6a3.jpdfsigner.control.Configurator;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

/**
 *
 * @author dan
 */
public class DragableScrollPaneMouseListener implements MouseListener, MouseMotionListener {

    private int currentMouseX = 0;
    private int currentMouseY = 0;
    private int currentViewPositionX = 0;
    private int currentViewPositionY = 0;
    private Cursor moveCursor = new Cursor(Cursor.MOVE_CURSOR);
    private Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        currentMouseX = e.getX();
        currentMouseY = e.getY();
        currentViewPositionX = -((JScrollPane) e.getSource()).getViewport().getViewPosition().x;
        currentViewPositionY = -((JScrollPane) e.getSource()).getViewport().getViewPosition().y;
    }

    public void mouseReleased(MouseEvent e) {
        ((JScrollPane) e.getSource()).setCursor(handCursor);
    }

    public void mouseEntered(MouseEvent e) {
        ((JScrollPane) e.getSource()).setCursor(handCursor);
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
        ((JScrollPane) e.getSource()).setCursor(moveCursor);
        if (e.getButton() == 0) {
            JViewport vp = ((JScrollPane) e.getSource()).getViewport();
            int maxX = vp.getViewSize().width - vp.getWidth();
            int maxY = vp.getViewSize().height - vp.getHeight();
            int newX = -(e.getX() - currentMouseX + currentViewPositionX);
            int newY = -(e.getY() - currentMouseY + currentViewPositionY);
            if (newX < 0) {
                newX = 0;
            }
            if (newX > maxX) {
                newX = maxX;
            }
            if (newY < 0) {
                newY = 0;
            }
            if (newY > maxY) {
                newY = maxY;
            }
            Point p = new Point(newX, newY);
            vp.setViewPosition(p);
        }
    }

    public void mouseMoved(MouseEvent e) {
        ((JScrollPane) e.getSource()).setCursor(handCursor);
    }
}
