/*
 * JPDFSigner - Sign PDFs online using smartcards (LoadingView.java)
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
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.JPanel;

/**
 *
 * @author dan
 */
public class LoadingView extends JPanel implements Runnable {

    private Thread thread = null;
    private boolean running = false;
    private double angle = 0;
    private double pi = 0;
    private int segments = 0;

    public LoadingView() {
        setOpaque(false);
        segments = 20;
        pi = Math.PI;
    }

    public void run() {
        angle = 0;
        while (running) {
            if (angle >= 2 * pi) {
                angle = 0;
            }
            angle += (pi * 2) / segments;
            repaint();
            try {
                Thread.sleep(45);
            } catch (InterruptedException ex) {
            }
        }
        thread.interrupt();
    }

    public void start() {
        if (!running) {
            running = true;
            thread = new Thread(this);
            thread.start();
        }
    }

    public void stop() {
        running = false;
        repaint();
    }
    BufferedImage bi;
    Graphics2D big2;
    double segmentAngle;
    int centerX;
    int centerY;
    double radius2;
    double radius1;
    int colorIntense;
    int radiusExpander;
    double x2;
    double y2;
    double x1;
    double y1;
    int x2Rounded;
    int y2Rounded;
    int x1Rounded;
    int y1Rounded;
    GradientPaint gp;

    @Override
    public void paint(Graphics g) {
        bi = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        if (running) {
            big2 = (Graphics2D) bi.getGraphics();
            big2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            segmentAngle = (pi * 2) / segments;

            centerX = getWidth() / 2;
            centerY = getHeight() / 2;

            radius2 = getHeight() / 2;
            radius1 = getHeight() / 6;

            big2.setColor(new Color(0, 0, 0, 35));

            for (float i = 1; i <= segments; i++) {

                colorIntense = (int) (255 * (i / segments));
                radiusExpander = (int) (getWidth() * (i / segments));

                radius2 = (getHeight() / 2f) - (radiusExpander / 3f);
                radius1 = (getHeight() / 10f);

                x2 = centerX + radius2 * Math.cos(segmentAngle * i + angle);
                y2 = centerY + radius2 * Math.sin(segmentAngle * i + angle);

                x1 = centerX + radius1 * Math.cos(segmentAngle * i + angle);
                y1 = centerY + radius1 * Math.sin(segmentAngle * i + angle);

                x2Rounded = new Long(Math.round(x2)).intValue();
                y2Rounded = new Long(Math.round(y2)).intValue();

                x1Rounded = new Long(Math.round(x1)).intValue();
                y1Rounded = new Long(Math.round(y1)).intValue();

                big2.setColor(new Color(0, 0, 0, colorIntense));

                gp = new GradientPaint(x1Rounded, y1Rounded, new Color(200, 200, 255, 0), x2Rounded, y2Rounded, new Color(255, 255, 255, colorIntense));
                big2.setPaint(gp);

                big2.drawLine(x1Rounded, y1Rounded, x2Rounded, y2Rounded);
            }
        }
        g.drawImage(bi, 0, 0, this);
    }
}
