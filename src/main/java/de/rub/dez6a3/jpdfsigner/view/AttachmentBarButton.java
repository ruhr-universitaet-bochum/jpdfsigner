/*
 * JPDFSigner - Sign PDFs online using smartcards (AttachmentBarButton.java)
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

import de.rub.dez6a3.jpdfsigner.view.UI.LAFProperties;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class AttachmentBarButton extends JPanel {

    private boolean mouseDown = false;
    private boolean onTop = false;
    private Image[] icons = null;
    private boolean isActive = false;

    public AttachmentBarButton() {
        super();
        setOpaque(false);
        setBackground(new Color(0, 0, 0, 0));
        addMouseListener(new MouseAdapter() {

            public void mouseEntered(MouseEvent e) {
                onTop = true;
                repaint();
            }

            public void mouseExited(MouseEvent e) {
                onTop = false;
                mouseDown = false;
                repaint();
            }
        });
    }

    public void setActive(boolean param) {
        isActive = param;
        repaint();
    }

    public void setIcons(Image[] icons) {
        this.icons = icons;
        repaint();
        onTop = true;
        repaint();
        onTop = false;
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        BufferedImage bi = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB); //paint via double buffering
        ImageObserver biio = new ImageIcon(bi).getImageObserver();
        Graphics2D g2 = (Graphics2D) bi.getGraphics();
//        super.paint(g2);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Image currIcon = null;
        if (onTop) {
            currIcon = icons[1];
        } else {
            currIcon = icons[0];
        }

        if (isActive) {
            int cornerRounding = 18;

            g2.setColor(LAFProperties.getInstance().getAttachmentBtnBorderColorLight());
            RoundRectangle2D.Float outerRectNorthWest = new RoundRectangle2D.Float(2, 0, getWidth(), getHeight() - 1, cornerRounding, cornerRounding);
            g2.fill(outerRectNorthWest);
            g2.fillRect((getWidth() / 2) + 2, 0, getWidth() / 2, getHeight() - 1);

            g2.setColor(LAFProperties.getInstance().getAttachmentBtnBorderColorDark());
            RoundRectangle2D.Float outerRectSouthEast = new RoundRectangle2D.Float(3, 1, getWidth(), getHeight() - 1, cornerRounding, cornerRounding);
            g2.fill(outerRectSouthEast);
            g2.fillRect((getWidth() / 2) + 2, 1, getWidth() / 2, getHeight() - 1);

            g2.setColor(LAFProperties.getInstance().getBluePanelColorE());

            RoundRectangle2D.Float innerRect = new RoundRectangle2D.Float(3, 1, getWidth(), getHeight() - 2, cornerRounding - 5, cornerRounding - 5);
            g2.fill(innerRect);

            g2.fillRect((getWidth() / 2) + 2, 1, getWidth() / 2, getHeight() - 2);
        }

        if (icons != null) {
            int x = ((getWidth() / 2) - (currIcon.getWidth(this) / 2)) + Math.round(getWidth() * (float) 0.05);
            int y = (getHeight() / 2) - (currIcon.getHeight(this) / 2);
            g2.drawImage(currIcon, x, y, currIcon.getWidth(this), currIcon.getHeight(this), new ImageIcon(currIcon).getImageObserver());
        }

        if (onTop) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            int height;
            int length;
            int paddingx;
            int paddingy;
            int posx;
            int posy;
            int direction;
            if (isActive) {
                height = 8;
                length = 4;
                paddingx = 1;
                paddingy = 5;
                posx = getWidth() - length - paddingx;
                posy = getHeight() - height - paddingy;
                direction = -1;
            } else {
                height = 8;
                length = 4;
                paddingx = 5;
                paddingy = 5;
                posx = getWidth() - length - paddingx;
                posy = getHeight() - height - paddingy;
                direction = 1;
            }
            int p1x = posx;
            int p1y = posy;
            int p2x = posx;
            int p2y = posy + height;
            int p3x = posx + (length * direction);
            int p3y = Math.round(posy + (height / 2));

            Polygon arrow = new Polygon();
            arrow.addPoint(p1x, p1y);
            arrow.addPoint(p2x, p2y);
            arrow.addPoint(p3x, p3y);

            g2.setColor(new Color(200, 200, 200));
            g2.fillPolygon(arrow);
        }
        g.drawImage(bi, 0, 0, biio);
    }
}