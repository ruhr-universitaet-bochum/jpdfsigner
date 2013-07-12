/*
 * JPDFSigner - Sign PDFs online using smartcards (SignButton.java)
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

import de.rub.dez6a3.jpdfsigner.control.RadialGradientPaint;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;

/**
 *
 * @author Daniel
 */
public class SignButton extends JButton implements Runnable {

    Thread thread;
    boolean threadRunning = false;
    boolean enlight = true;
    boolean waiting = false;
    int waitingStuffAlpha = 0;
    Cursor defaultCursor;
    Cursor handCursor;
    Color c1 = new Color(10, 0, 0);
    Color c2 = new Color(255, 33, 33);
    Color c3 = new Color(125, 0, 0);
    Color c4 = new Color(100, 0, 0);
    Color tmpc1 = c1;
    Color tmpc2 = c3;

    public SignButton(String title, int size) {
        super(title);
        thread = new Thread(this);
        defaultCursor = Cursor.getDefaultCursor();
        handCursor = new Cursor(Cursor.HAND_CURSOR);
        Dimension dim = new Dimension(size, size);
        setPreferredSize(dim);
        setSize(dim);
        setOpaque(false);
        setBackground(new Color(0, 0, 0, 0));
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        addMouseListener(new MouseAdapter() {

            public void mouseEntered(MouseEvent e) {
                startThread();
                setCursor(handCursor);
                enlight = true;
            }

            public void mouseExited(MouseEvent e) {
                startThread();
                setCursor(defaultCursor);
                enlight = false;
            }
        });
    }

    public void paint(Graphics g) {
        BufferedImage bi = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D big = (Graphics2D) bi.getGraphics();
        big.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int space = getWidth() / 9;

        GradientPaint gp = new GradientPaint(0, 0, new Color(255, 255, 255, 30), getWidth() - (getWidth() / 7), getHeight() - (getHeight() / 7), new Color(0, 0, 0, 100));
        big.setPaint(gp);
        big.fillOval(0, 0, getWidth(), getHeight());

        gp = new GradientPaint(space + (getWidth() / 5), space + (getHeight() / 5), new Color(0, 0, 0, 100), getWidth(), getHeight(), new Color(255, 255, 255, 80));
        big.setPaint(gp);
        big.fillOval(space, space, getWidth() - (space * 2), getHeight() - (space * 2));

        big.setColor(validateColor(tmpc1.getRed() - 100, tmpc1.getGreen(), tmpc1.getBlue()));
        big.fillOval(space + 2, space + 2, getWidth() - 4 - (space * 2), getHeight() - 4 - (space * 2));

        gp = new GradientPaint(0, (getHeight() / 4) - (space * 2), tmpc1, 0, getHeight() - (space * 2), tmpc2, false);
        big.setPaint(gp);

        big.fillOval(space + 3, space + 3, getWidth() - 6 - (space * 2), getHeight() - 6 - (space * 2));

        if (waiting) {
            drawWaitingStuff(big, space);
        }
        if (!waiting && waitingStuffAlpha > 0) {
            drawWaitingStuff(big, space);

        }

        big.setFont(getFont());
        FontMetrics fm = big.getFontMetrics();
        int txtX = getWidth() / 2 - (fm.stringWidth(getText()) / 2);
        int txtY = getHeight() / 2 + (fm.getHeight() / 4);
        GlyphVector v = getFont().createGlyphVector(fm.getFontRenderContext(), getText());
        Area text = new Area(v.getOutline(txtX, txtY));
        text.transform(AffineTransform.getTranslateInstance(1.0d, 1.0d));
        big.setColor(new Color(0,0,0, 150));
        big.fill(text);
        text.transform(AffineTransform.getTranslateInstance(-1.5d, -1.5d));
        big.setColor(new Color(230, 230, 230));
        big.fill(text);

        gp = new GradientPaint(0, 0 + space, new Color(255, 255, 255, 200), 0, (getHeight() / 2) + (getHeight() / 4) - (space * 2), new Color(255, 255, 255, 20));
        big.setPaint(gp);
        big.fillOval((getWidth() / 13) + space, 3 + space, getWidth() - ((getWidth() / 13) * 2) - (space * 2), (getHeight() / 2) + (getHeight() / 4) - (space * 2));

        g.drawImage(bi, 0, 0, this);
    }
    double pi = Math.PI;
    int angle = 0;
    int segments = 20;

    private void drawWaitingStuff(Graphics g, int space) {
        Graphics2D g2 = (Graphics2D) g;

        int alphpaSpeed = 10;
        int maxAlpha = 50;

        if (waiting) {
            if (waitingStuffAlpha < maxAlpha) {
                if (waitingStuffAlpha + alphpaSpeed > maxAlpha) {
                    alphpaSpeed = maxAlpha;
                } else {
                    waitingStuffAlpha += alphpaSpeed;
                }
            }
        } else {
            if (waitingStuffAlpha > 0) {
                if (waitingStuffAlpha - alphpaSpeed < 0) {
                    waitingStuffAlpha = 0;
                } else {
                    waitingStuffAlpha -= alphpaSpeed;
                }
            }
        }

        int spacer = space + 3;
        int singleAngleWidth = 360 / segments;
        for (int i = 1; i <= segments; i++) {
            if (i % 2 == 0) {
                g2.setColor(new Color(180, 40, 0, waitingStuffAlpha));
            } else {
                g2.setColor(new Color(50, 0, 20, waitingStuffAlpha));
            }
            g2.fillArc(spacer, spacer, getWidth() - (spacer * 2), getHeight() - (spacer * 2), singleAngleWidth * i + angle, singleAngleWidth);
        }
    }

    public void startThread() {
        if (!threadRunning) {
            thread = new Thread(this);
            thread.start();
        }
    }

    public void setWaitingState(boolean param) {
        waiting = param;
        startThread();
    }

    private void rotateAngle() {
        if (angle >= 360) {
            angle = 0;
        }
        angle += 3;
    }

    public void run() {
        threadRunning = true;
        int loops = 7;

        int f1r = ((c1.getRed() - c2.getRed()) / loops) * -1;
        int f1g = ((c1.getGreen() - c2.getGreen()) / loops) * -1;
        int f1b = ((c1.getBlue() - c2.getBlue()) / loops) * -1;

        int f2r = ((c3.getRed() - c4.getRed()) / loops) * -1;
        int f2g = ((c3.getGreen() - c4.getGreen()) / loops) * -1;
        int f2b = ((c3.getBlue() - c4.getBlue()) / loops) * -1;

        while (true) {
            try {
                thread.sleep(50);
            } catch (InterruptedException ex) {
                Logger.getLogger(SignButton.class.getName()).log(Level.SEVERE, null, ex);
            }
            rotateAngle();
            int r1 = tmpc1.getRed();
            int g1 = tmpc1.getGreen();
            int b1 = tmpc1.getBlue();

            int r2 = tmpc2.getRed();
            int g2 = tmpc2.getGreen();
            int b2 = tmpc2.getBlue();

            if (enlight || waiting) {
                r1 += f1r;
                g1 += f1g;
                b1 += f1b;

                r2 += f2r;
                g2 += f2g;
                b2 += f2b;
            } else {
                r1 -= f1r;
                g1 -= f1g;
                b1 -= f1b;

                r2 -= f2r;
                g2 -= f2g;
                b2 -= f2b;
            }
            try {
                Color color1 = new Color(r1, g1, b1);
                tmpc1 = color1;

                try {
                    Color color2 = new Color(r2, g2, b2);
                    tmpc2 = color2;
                } catch (Exception e) {
                }

            } catch (Exception e) {
                if (enlight || waiting) {
                    tmpc1 = c2;
                } else {
                    tmpc1 = c1;
                }
                if (!waiting) {
                    break;
                }
            }
            repaint();
        }
        threadRunning = false;
        repaint();
    }

    private Color validateColor(int r, int g, int b) {
        int tr = r;
        int tg = g;
        int tb = b;

        if (r > 255) {
            tr = 255;
        } else if (r < 0) {
            tr = 0;
        }
        if (g > 255) {
            tg = 255;
        } else if (g < 0) {
            tg = 0;
        }
        if (b > 255) {
            tb = 255;
        } else if (b < 0) {
            tb = 0;
        }

        return new Color(tr, tg, tb);
    }
}
