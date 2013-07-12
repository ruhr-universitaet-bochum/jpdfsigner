/*
 * JPDFSigner - Sign PDFs online using smartcards (RadialGradientPaint.java)
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

package de.rub.dez6a3.jpdfsigner.control;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

public class RadialGradientPaint implements Paint {

    protected Point2D mPoint;
    protected Point2D mRadius;
    protected Color mPointColor, mBackgroundColor;

    public RadialGradientPaint(double x, double y, Color pointColor,
            Point2D radius, Color backgroundColor) {
        if (radius.distance(0, 0) <= 0) {
            throw new IllegalArgumentException("Radius must be greater than 0.");
        }
        mPoint = new Point2D.Double(x, y);
        mPointColor = pointColor;
        mRadius = radius;
        mBackgroundColor = backgroundColor;
    }

    public PaintContext createContext(ColorModel cm,
            Rectangle deviceBounds,
            Rectangle2D userBounds,
            AffineTransform xform,
            RenderingHints hints) {
        Point2D transformedPoint = xform.transform(mPoint, null);
        Point2D transformedRadius = xform.deltaTransform(mRadius, null);
        return new OvalGradientContext(transformedPoint, mPointColor,
                transformedRadius, mBackgroundColor);
    }

    public int getTransparency() {
        int a1 = mPointColor.getAlpha();
        int a2 = mBackgroundColor.getAlpha();
        return (((a1 & a2) == 0xff) ? OPAQUE : TRANSLUCENT);
    }
}

class OvalGradientContext implements PaintContext {
    protected Point2D mPoint;
    protected Point2D mRadius;
    protected Color mC1, mC2;
    Ellipse2D.Double ellipse;
    Line2D.Double line;
    Map<Double, Double> lookup;
    double R;

    public OvalGradientContext(Point2D p, Color c1, Point2D r, Color c2) {
        mPoint = p;
        mC1 = c1;
        mRadius = r;
        mC2 = c2;
        double x = p.getX() - mRadius.getX();
        double y = p.getY() - mRadius.getY();
        double w = 2*mRadius.getX();
        double h = 2*mRadius.getY();
        ellipse = new Ellipse2D.Double(x,y,w,h);
        line = new Line2D.Double();
        R = Point2D.distance(0, 0, r.getX(), r.getY());
        initLookup();
    }

    public void dispose() { }

    public ColorModel getColorModel() {
        return ColorModel.getRGBdefault();
    }

    public Raster getRaster(int x, int y, int w, int h) {
        WritableRaster raster = getColorModel().createCompatibleWritableRaster(w,h);
        int[] data = new int[w*h*4];

        for(int j = 0; j < h; j++) {
            for(int i = 0; i < w; i++) {
                double distance = mPoint.distance(x+i,y+j);
                double dy = y+j - mPoint.getY();
                double dx = x+i - mPoint.getX();
                double theta = Math.atan2(dy, dx);
                double xp = mPoint.getX() + R * Math.cos(theta);
                double yp = mPoint.getY() + R * Math.sin(theta);
                line.setLine(mPoint.getX(), mPoint.getY(), xp, yp);
                double roundDegrees = Math.round(Math.toDegrees(theta));
                double radius = lookup.get(Double.valueOf(roundDegrees));
                double ratio = distance / radius;

                if(ratio > 1.0)
                    ratio = 1.0;

                int base = (j * w + i) * 4;
                data[base + 0] = (int)(mC1.getRed() +
                              ratio * (mC2.getRed() - mC1.getRed()));
                data[base + 1] = (int)(mC1.getGreen() +
                              ratio * (mC2.getGreen() - mC1.getGreen()));
                data[base + 2] = (int)(mC1.getBlue() +
                              ratio * (mC2.getBlue() - mC1.getBlue()));
                data[base + 3] = (int)(mC1.getAlpha() +
                              ratio * (mC2.getAlpha() - mC1.getAlpha()));
            }
        }
        raster.setPixels(0,0,w,h,data);
        return raster;
    }

    private double getRadius() {
        double[] coords = new double[6];
        Point2D.Double p = new Point2D.Double();
        double minDistance = Double.MAX_VALUE;
        double flatness = 0.005;
        PathIterator pit = ellipse.getPathIterator(null, flatness);
        while(!pit.isDone()) {
            int segment = pit.currentSegment(coords);
            switch(segment) {
                case PathIterator.SEG_CLOSE:
                case PathIterator.SEG_MOVETO:
                case PathIterator.SEG_LINETO:
                    break;
                default:
                    System.out.printf("unexpected segment: %d%n", segment);
            }
            double distance = line.ptSegDist(coords[0], coords[1]);
            if(distance < minDistance) {
                minDistance = distance;
                p.x = coords[0];
                p.y = coords[1];
            }
            pit.next();
        }
        return mPoint.distance(p);
    }

    private void initLookup() {
        lookup = new HashMap<Double, Double>();
        for(int j = -180; j <= 180; j++) {
            Double key = Double.valueOf(j);
            double theta = Math.toRadians(j);
            double xp = mPoint.getX() + R * Math.cos(theta);
            double yp = mPoint.getY() + R * Math.sin(theta);
            line.setLine(mPoint.getX(), mPoint.getY(), xp, yp);
            Double value = Double.valueOf(getRadius());
            lookup.put(key, value);
        }
        double theta = -0.0;  // avoids NullPointerException
        Double key = Double.valueOf(theta);
        double xp = mPoint.getX() + R * Math.cos(theta);
        double yp = mPoint.getY() + R * Math.sin(theta);
        line.setLine(mPoint.getX(), mPoint.getY(), xp, yp);
        Double value = Double.valueOf(getRadius());
        lookup.put(key, value);
    }
}

