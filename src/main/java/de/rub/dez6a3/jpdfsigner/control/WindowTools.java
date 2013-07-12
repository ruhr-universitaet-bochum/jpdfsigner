/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rub.dez6a3.jpdfsigner.control;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;

/**
 *
 * @author daniel
 */
public class WindowTools {

    private static Rectangle getScreenDimension(int screen) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gd = ge.getScreenDevices();
        GraphicsDevice primary = gd[screen]; //zero determines primary screen
        return primary.getDefaultConfiguration().getBounds();
    }

    public static Point getCenterPoint(float w, float h, int screen) {
        Rectangle screenDim = getScreenDimension(screen);
        Dimension windowDim = new Dimension(Math.round((float) screenDim.width * w), Math.round((float) screenDim.height * h));
        Point windowLocation = new Point(((screenDim.width / 2) - (windowDim.width / 2)) + screenDim.x, ((screenDim.height / 2) - (windowDim.height / 2)) + screenDim.y);
        return windowLocation;
    }

    public static Point getCenterPoint(int w, int h, int screen) {
        Rectangle screenDim = getScreenDimension(screen);
        Dimension windowDim = new Dimension(w, h);
        Point windowLocation = new Point(((screenDim.width / 2) - (windowDim.width / 2)) + screenDim.x, ((screenDim.height / 2) - (windowDim.height / 2)) + screenDim.y);
        return windowLocation;
    }

    public static Dimension getWindowSizeFromPercent(float w, float h, int screen) {
        Rectangle screenDim = getScreenDimension(screen);
        Dimension windowDim = new Dimension(Math.round((float) screenDim.width * w), Math.round((float) screenDim.height * h));
        return windowDim;
    }
}
