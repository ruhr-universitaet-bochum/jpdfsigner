/*
 * JPDFSigner - Sign PDFs online using smartcards (CustomScrollBarUI.java)
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
package de.rub.dez6a3.jpdfsigner.view.UI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import sun.awt.RepaintArea;

/**
 *
 * @author dan
 */
public class CustomScrollBarUI extends BasicScrollBarUI {

    private Color outLine = LAFProperties.getInstance().getScrollBarOutLine();
    private Color background = LAFProperties.getInstance().getScrollBarBackground();
    private Color trackAndThumbWestColor = LAFProperties.getInstance().getScrollBarTrackAndThumbWestColor();
    private Color trackAndThumbEastColor = LAFProperties.getInstance().getScrollBarTrackAndThumbEastColor();
    private Color trackAndThumbHighlightColor = LAFProperties.getInstance().getScrollBarTrackAndThumbHighlightColor();
    private Color trackBackgroundEastColor = LAFProperties.getInstance().getScrollBarTrackBackgroundEastColor();
    private Color trackBackgroundWestColor = LAFProperties.getInstance().getScrollBarTrackBackgroundWestColor();
    private Color centerTrackLinesNorthColor = LAFProperties.getInstance().getScrollBarCenterTrackLinesNorthColor();
    private Color centerTrackLinesSouthColor = LAFProperties.getInstance().getScrollBarCenterTrackLinesSouthColor();
    private Color thumbArrowColor = LAFProperties.getInstance().getScrollBarThumbArrowColor();
    private Color smallOutlineColor = LAFProperties.getInstance().getScrollBarSmallOutlineColor();

    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        if (trackBounds.getHeight() >= trackBounds.getWidth()) {
            paintVerticalTrack(g, trackBounds);
        } else {
            paintHorizontalTrack(g, trackBounds);
        }
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        c.setOpaque(false);
        c.addComponentListener(new ComponentListener() {

            public void componentResized(ComponentEvent e) {
                Dimension sbDim = null;
                if (scrollbar.getOrientation() == JScrollBar.HORIZONTAL) {
                    sbDim = new Dimension(scrollbar.getHeight(), scrollbar.getHeight());
                    decreaseButton.setPreferredSize(sbDim);
                    increaseButton.setPreferredSize(sbDim);
                } else {
                    sbDim = new Dimension(scrollbar.getWidth(), scrollbar.getWidth());
                    decreaseButton.setPreferredSize(sbDim);
                    increaseButton.setPreferredSize(sbDim);
                }
                decreaseButton.revalidate();
                increaseButton.revalidate();
            }

            public void componentMoved(ComponentEvent e) {
//                throw new UnsupportedOperationException("Not supported yet.");
            }

            public void componentShown(ComponentEvent e) {
//                throw new UnsupportedOperationException("Not supported yet.");
            }

            public void componentHidden(ComponentEvent e) {
//                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
    }

    private void paintVerticalTrack(Graphics g, Rectangle trackBounds) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint backGradient = new GradientPaint(trackBounds.x, 0, trackBackgroundWestColor, trackBounds.width, 0, trackBackgroundEastColor);
        g2.setPaint(backGradient);
        g2.fill(trackBounds);
//top
        g2.setColor(outLine);
        g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.width / 2);

        GradientPaint innerPaint = new GradientPaint(1, 0, trackAndThumbWestColor, trackBounds.width - 2, 0, trackAndThumbEastColor);
        g2.setPaint(innerPaint);
        g2.fillRect(trackBounds.x + 1, trackBounds.y, trackBounds.width - 2, trackBounds.width / 2);

        g2.setColor(outLine);
        g2.fillOval(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.width);


        g2.setPaint(backGradient);
        g2.fillOval(trackBounds.x, trackBounds.y + 1, trackBounds.width, trackBounds.width);
//bottom
        g2.setColor(outLine);
        g2.fillRect(trackBounds.x, trackBounds.height + trackBounds.y - (trackBounds.width / 2), trackBounds.width, trackBounds.width / 2);

        g2.setPaint(innerPaint);
        g2.fillRect(trackBounds.x + 1, trackBounds.height + trackBounds.y - (trackBounds.width / 2), trackBounds.width - 2, trackBounds.width / 2);

        g2.setColor(outLine);
        g2.fillOval(trackBounds.x, (trackBounds.y + trackBounds.height) - trackBounds.width, trackBounds.width, trackBounds.width);
//
//
        g2.setPaint(backGradient);
        g2.fillOval(trackBounds.x, ((trackBounds.y + trackBounds.height) - trackBounds.width) - 1, trackBounds.width, trackBounds.width);
//
        g2.setColor(smallOutlineColor);
        g2.drawLine(0, trackBounds.y + (trackBounds.width / 2) - 1, 0, (trackBounds.y + trackBounds.height) - (trackBounds.width / 2));
    }

    private void paintHorizontalTrack(Graphics g, Rectangle trackBounds) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint backGradient = new GradientPaint(0, trackBounds.y, trackBackgroundWestColor, 0, trackBounds.height, trackBackgroundEastColor);
        g2.setPaint(backGradient);
        g2.fill(trackBounds);
//left
        g2.setColor(outLine);
        g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.height / 2, trackBounds.height);

        GradientPaint innerPaint = new GradientPaint(0, 1, trackAndThumbWestColor, 0, trackBounds.height - 2, trackAndThumbEastColor);
        g2.setPaint(innerPaint);
        g2.fillRect(trackBounds.x, trackBounds.y + 1, trackBounds.height / 2, trackBounds.height - 2);

        g2.setColor(outLine);
        g2.fillOval(trackBounds.x, trackBounds.y, trackBounds.height, trackBounds.height);

        g2.setPaint(backGradient);
        g2.fillOval(trackBounds.x + 1, trackBounds.y, trackBounds.height, trackBounds.height);
//right
        g2.setColor(outLine);
        g2.fillRect((trackBounds.x + trackBounds.width) - (trackBounds.height / 2), trackBounds.y, trackBounds.height / 2, trackBounds.height);

        g2.setPaint(innerPaint);
        g2.fillRect((trackBounds.x + trackBounds.width) - (trackBounds.height / 2), trackBounds.y + 1, trackBounds.height / 2, trackBounds.height - 2);

        g2.setColor(outLine);
        g2.fillOval((trackBounds.x + trackBounds.width) - trackBounds.height, trackBounds.y, trackBounds.height, trackBounds.height);

        g2.setPaint(backGradient);
        g2.fillOval(((trackBounds.x + trackBounds.width) - trackBounds.height) - 1, trackBounds.y, trackBounds.height, trackBounds.height);

        g2.setColor(smallOutlineColor);
        g2.drawLine(trackBounds.x + (trackBounds.height / 2) - 1, 0, (trackBounds.x + trackBounds.width) - (trackBounds.height / 2), 0);
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (thumbBounds.getHeight() >= thumbBounds.getWidth()) {
            paintVerticalThumb(g, thumbBounds);
        } else {
            paintHorizontalThumb(g, thumbBounds);
        }
    }

    private void paintVerticalThumb(Graphics g, Rectangle thumbBounds) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int x = thumbBounds.x;
        int y = thumbBounds.y;
        int width = thumbBounds.width;
        int height = thumbBounds.height;

        RoundRectangle2D.Float border = new RoundRectangle2D.Float(x, y, width, height, 15, 15);
        RoundRectangle2D.Float inner = new RoundRectangle2D.Float(x + 1, y + 1, width - 2, height - 2, 15, 15);

        g2.setColor(outLine);
        g2.fill(border);

        GradientPaint innerPaint = new GradientPaint(2, 0, trackAndThumbWestColor, width - 4, 0, trackAndThumbEastColor);
        g2.setPaint(innerPaint);
        g2.fill(inner);

        g2.setColor(trackAndThumbHighlightColor);
        g2.fillRoundRect(x + 2, y + 5, (width / 2) - 3, height - 10, 30, 10);

        paintVerticalThumbIcon(g, thumbBounds, 0);
        paintVerticalThumbIcon(g, thumbBounds, -3);
        paintVerticalThumbIcon(g, thumbBounds, 3);
    }

    private void paintVerticalThumbIcon(Graphics g, Rectangle thumbBounds, int offset) {
        Graphics g2 = (Graphics2D) g;
        g2.setColor(centerTrackLinesNorthColor);
        g2.drawLine(5, (thumbBounds.height / 2) + thumbBounds.y + offset, thumbBounds.width - 7, (thumbBounds.height / 2) + thumbBounds.y + offset);
        g2.setColor(centerTrackLinesSouthColor);
        g2.drawLine(5, ((thumbBounds.height / 2) + thumbBounds.y) + 1 + offset, thumbBounds.width - 7, ((thumbBounds.height / 2) + thumbBounds.y) + 1 + offset);
    }

    private void paintHorizontalThumbIcon(Graphics g, Rectangle thumbBounds, int offset) {
        Graphics g2 = (Graphics2D) g;
        g2.setColor(centerTrackLinesNorthColor);
        g2.drawLine((thumbBounds.width / 2) + thumbBounds.x + offset, 5, (thumbBounds.width / 2) + thumbBounds.x + offset, thumbBounds.height - 7);
        g2.setColor(centerTrackLinesSouthColor);
        g2.drawLine((thumbBounds.width / 2) + thumbBounds.x + 1 + offset, 5, (thumbBounds.width / 2) + thumbBounds.x + 1 + offset, thumbBounds.height - 7);
    }

    private void paintHorizontalThumb(Graphics g, Rectangle thumbBounds) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int x = thumbBounds.x;
        int y = thumbBounds.y;
        int width = thumbBounds.width;
        int height = thumbBounds.height;

        RoundRectangle2D.Float border = new RoundRectangle2D.Float(x, y, width, height, 15, 15);
        RoundRectangle2D.Float inner = new RoundRectangle2D.Float(x + 1, y + 1, width - 2, height - 2, 15, 15);

        g2.setColor(outLine);
        g2.fill(border);

        GradientPaint innerPaint = new GradientPaint(0, 2, trackAndThumbWestColor, 0, height - 4, trackAndThumbEastColor);
        g2.setPaint(innerPaint);
        g2.fill(inner);

        g2.setColor(trackAndThumbHighlightColor);
        g2.fillRoundRect(x + 5, y + 2, width - 10, (height / 2) - 3, 10, 30);

        paintHorizontalThumbIcon(g, thumbBounds, 0);
        paintHorizontalThumbIcon(g, thumbBounds, -3);
        paintHorizontalThumbIcon(g, thumbBounds, 3);
    }
    private JButton increaseButton = null;

    @Override
    protected JButton createIncreaseButton(int orientation) {
        if (orientation == 5) {                          // 5 = Vertical orientation
            increaseButton = createVerticalIncreaseButton();
        } else if (orientation == 3) {                      // 3 = Horizontal orientation
            increaseButton = createHorizontalIncreaseButton();
        } else {
            increaseButton = new JButton();
        }
        increaseButton.setOpaque(false);
        Dimension dim = new Dimension(18, 18);
        increaseButton.setSize(dim);
        increaseButton.setPreferredSize(dim);
        increaseButton.setVisible(true);
        return increaseButton;
    }

    private JButton createHorizontalIncreaseButton() {
        return new JButton() {

            @Override
            public void paint(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(outLine);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.fillRect(0, 0, getWidth() / 2, getHeight());

                GradientPaint innerPaint = new GradientPaint(0, 1, trackAndThumbWestColor, 0, getHeight() - 2, trackAndThumbEastColor);
                g2.setPaint(innerPaint);
                g2.fillOval(1, 1, getWidth() - 2, getHeight() - 2);
                g2.fillRect(0, 1, (getWidth() / 2) - 2, getHeight() - 2);

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                Polygon arrow = new Polygon();
                arrow.addPoint((getWidth() / 2) + 2, (getHeight() / 2));
                arrow.addPoint((getWidth() / 2) - 2, (getHeight() / 2) + -4);
                arrow.addPoint((getWidth() / 2) - 2, (getHeight() / 2) + 4);
                g2.setColor(thumbArrowColor);
                g2.fill(arrow);

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(trackAndThumbHighlightColor);
                g2.fillRoundRect(3, 2, getWidth() - 8, (getHeight() / 2 - 2), 10, 10);
            }
        };
    }

    private JButton createVerticalIncreaseButton() {
        return new JButton() {

            @Override
            public void paint(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(outLine);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.fillRect(0, 0, getWidth(), getHeight() / 2);

                GradientPaint innerPaint = new GradientPaint(1, 0, trackAndThumbWestColor, getWidth() - 2, 0, trackAndThumbEastColor);
                g2.setPaint(innerPaint);
                g2.fillOval(1, 0, getWidth() - 2, getHeight() - 1);
                g2.fillRect(1, 0, getWidth() - 2, (getHeight() / 2));

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                Polygon arrow = new Polygon();
                arrow.addPoint(getWidth() / 2, (getHeight() / 2) + 2);
                arrow.addPoint((getWidth() / 2) - 3, (getHeight() / 2) - 2);
                arrow.addPoint((getWidth() / 2) + 4, (getHeight() / 2) - 2);
                g2.setColor(thumbArrowColor);
                g2.fill(arrow);

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(trackAndThumbHighlightColor);
                g2.fillRoundRect(2, 4, (getWidth() / 2) - 2, getHeight() - 8, 10, 10);
            }
        };
    }
    private JButton decreaseButton = null;

    @Override
    protected JButton createDecreaseButton(int orientation) {
        if (orientation == 1) {                          // 1 = Vertical orientation
            decreaseButton = createVerticalDecreaseButton();
        } else if (orientation == 7) {                      // 7 = Horizontal orientation
            decreaseButton = createHorizonalDecreaseButton();
        } else {
            decreaseButton = new JButton();
        }
        decreaseButton.setOpaque(false);
        Dimension dim = new Dimension(18, 18);
        decreaseButton.setSize(dim);
        decreaseButton.setPreferredSize(dim);
        decreaseButton.setVisible(true);
        return decreaseButton;
    }

    private JButton createHorizonalDecreaseButton() {
        return new JButton() {

            @Override
            public void paint(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(outLine);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.fillRect(getWidth() / 2, 0, getWidth(), getHeight());

                GradientPaint innerPaint = new GradientPaint(0, 1, trackAndThumbWestColor, 0, getHeight() - 2, trackAndThumbEastColor);
                g2.setPaint(innerPaint);
                g2.fillOval(1, 1, getWidth() - 2, getHeight() - 2);
                g2.fillRect((getWidth() / 2) - 2, 1, getWidth() - 2, getHeight() - 2);

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                Polygon arrow = new Polygon();
                arrow.addPoint((getWidth() / 2) - 2, (getHeight() / 2));
                arrow.addPoint((getWidth() / 2) + 3, (getHeight() / 2) + -4);
                arrow.addPoint((getWidth() / 2) + 3, (getHeight() / 2) + 4);
                g2.setColor(thumbArrowColor);
                g2.fill(arrow);

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(trackAndThumbHighlightColor);
                g2.fillRoundRect(5, 2, getWidth() - 8, (getHeight() / 2 - 2), 10, 10);
            }
        };
    }

    private JButton createVerticalDecreaseButton() {
        return new JButton() {

            @Override
            public void paint(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(outLine);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.fillRect(0, getHeight() / 2, getWidth(), getHeight());

                GradientPaint innerPaint = new GradientPaint(1, 0, trackAndThumbWestColor, getWidth() - 2, 0, trackAndThumbEastColor);
                g2.setPaint(innerPaint);
                g2.fillOval(1, 1, getWidth() - 2, getHeight() - 2);
                g2.fillRect(1, (getHeight() / 2) - 2, getWidth() - 2, getHeight() - 2);

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                Polygon arrow = new Polygon();
                arrow.addPoint(getWidth() / 2, (getHeight() / 2) - 2);
                arrow.addPoint((getWidth() / 2) - 4, (getHeight() / 2) + 3);
                arrow.addPoint((getWidth() / 2) + 4, (getHeight() / 2) + 3);
                g2.setColor(thumbArrowColor);
                g2.fill(arrow);

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(trackAndThumbHighlightColor);
                g2.fillRoundRect(2, 5, (getWidth() / 2) - 2, getHeight() - 8, 10, 10);
            }
        };
    }

    @Override
    protected Dimension getMinimumThumbSize() {
        return new Dimension(50, 50);
    }

    public static ComponentUI createUI(JComponent c) {
        return new CustomScrollBarUI();
    }
}
