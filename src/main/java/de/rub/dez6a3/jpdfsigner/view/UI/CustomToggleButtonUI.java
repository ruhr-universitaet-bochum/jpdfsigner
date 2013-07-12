/*
 * JPDFSigner - Sign PDFs online using smartcards (CustomToggleButtonUI.java)
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
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToggleButtonUI;
import org.apache.log4j.Logger;

/**
 *
 * @author dan
 */
public class CustomToggleButtonUI extends BasicToggleButtonUI {

    private boolean mouseOver = false;
    private boolean mouseDown = false;
    public static Logger log = Logger.getLogger(CustomButtonUI.class);

    public static ComponentUI createUI(JComponent c) {
        return new CustomToggleButtonUI();
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        JToggleButton b = (JToggleButton) c;

        if (b.getText() != null && !b.getText().equals("") || b.getToolTipText() != null && b.getToolTipText().equals("")) {
            paintTextButtonBorder(g, c.getWidth(), c.getHeight());
            paintTextButton(g, c.getWidth(), c.getHeight(), new Color(150, 150, 150), new Color(210, 210, 210));  //Zeichne Menü-Button
        } else {
            boolean buttonActive = false;
            ButtonModel bModel = b.getModel();
            if (bModel.isArmed() && bModel.isPressed() || bModel.isSelected()) {
                buttonActive = true;
            } else {
                buttonActive = false;
            }
            if (mouseOver && !mouseDown && !buttonActive) {
                paintEmptyButtonBGMouseOver(g, b);
            } else if (mouseDown && !buttonActive) {
                paintEmptyButtonBGMouseDown(g, b);
            } else if (buttonActive) {
                paintEmptyButtonBGMouseDown(g, b);
            }
        }
        super.paint(g, c);
    }

    private void paintTextButtonBorder(Graphics g, int width, int height) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(0, 0, 0, 90));
        g2.fillRoundRect(0, 0, width, height, 19, 19);

        GradientPaint gp2 = new GradientPaint(0, 0, new Color(245, 245, 245), width / 2, height / 2, new Color(205, 205, 205), true);
        g2.setPaint(gp2);
        g2.fillRoundRect(1, 1, width - 2, height - 2, 18, 18);
    }

    protected void paintButtonPressed(final Graphics g, final AbstractButton button) {
        if (button.getText() != null && !button.getText().equals("")) {
            paintTextButton(g, button.getWidth(), button.getHeight(), new Color(150, 182, 227), new Color(227, 239, 255));
        }
    }
    float arcSize = 0.33f;    //Size of corner roundings

    private void paintEmptyButtonBGMouseDown(Graphics g, AbstractButton b) {
        Insets in = null;
        if (b.getMargin() != null) {
            in = b.getMargin();
        } else {
            in = new Insets(0, 0, 0, 0);
        }
        Graphics2D g2 = (Graphics2D) g;
        GradientPaint gp = new GradientPaint(in.top, in.left, new Color(0, 0, 0, 90), b.getWidth() - in.left - in.right, b.getHeight() - in.top - in.bottom, new Color(255, 255, 255, 150), false);
        g2.setPaint(gp);
        int arc = calculateArcSize(b.getWidth(), b.getHeight());
        g2.fillRoundRect(in.top, in.left, b.getWidth() - in.left - in.right, b.getHeight() - in.top - in.bottom, arc, arc);
    }

    private void paintEmptyButtonBGMouseOver(Graphics g, AbstractButton b) {
        Insets in = null;
        if (b.getMargin() != null) {
            in = b.getMargin();
        } else {
            in = new Insets(0, 0, 0, 0);
        }
        Graphics2D g2 = (Graphics2D) g;
        GradientPaint gp = new GradientPaint(in.top, in.left, new Color(255, 255, 255, 150), b.getWidth() - in.left - in.right, b.getHeight() - in.top - in.bottom, new Color(0, 0, 0, 90), false);
        g2.setPaint(gp);
        int arc = calculateArcSize(b.getWidth(), b.getHeight());
        g2.fillRoundRect(in.top, in.left, b.getWidth() - in.left - in.right, b.getHeight() - in.top - in.bottom, arc, arc);
    }

    private int calculateArcSize(int width, int height) {
        int arc = 0;
        if (height < width) {
            arc = (int) ((float) height * arcSize);
        } else {
            arc = (int) ((float) width * arcSize);
        }
        return arc;
    }

    private void paintTextButton(Graphics g, int width, int height, Color f, Color f2) {
        Graphics2D g2 = (Graphics2D) g;

        GradientPaint gp = new GradientPaint(0, height / 4, f, 0, height - (height / 4), f2, false);
        g2.setPaint(gp);
        g2.fillRoundRect(2, 2, width - 4, height - 4, 17, 17);

        g2.setColor(new Color(255, 255, 255, 100));
        g2.fillRoundRect(2, 2, width - 4, height / 2, 17, 17);
    }

    @Override
    protected void installListeners(final AbstractButton b) {
        super.installListeners(b);
        b.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                mouseDown = false;
                b.repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                mouseDown = true;
                b.repaint();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                mouseOver = true;
                b.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                mouseOver = false;
                b.repaint();
            }
        });
    }
}
