/*
 * JPDFSigner - Sign PDFs online using smartcards (JDialogTitledBorder.java)
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
package de.rub.dez6a3.jpdfsigner.view.UI.borders;

import de.rub.dez6a3.jpdfsigner.control.AWTUtilitiesValidator;
import de.rub.dez6a3.jpdfsigner.view.UI.LAFProperties;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.Border;

public class JDialogTitledBorder implements Border, MouseListener, MouseMotionListener, SwingConstants {

    Component closeBtn;
    Component maxBtn;
    Component minBtn;
    Rectangle closeBtnRect;
    Rectangle maxBtnRect;
    Rectangle minBtnRect;
    BufferedImage closeBtnImg;
    Graphics closeBtnG;
    Graphics maxBtnG;
    Graphics minBtnG;
    int localx;
    int localy;
    int mainx = 0;
    int mainy = 0;
    Rectangle origMainFrameSize;
    JComponent container;
    JDialog mainFrame;
    Rectangle rect;
    Insets insets;
    Image emblem;

    public JDialogTitledBorder(JComponent container, Insets insets) {
        closeBtn = new JButton("close");
        maxBtn = new JButton("max");
        minBtn = new JButton("min");
        this.container = container;
        mainFrame = (JDialog) (((JRootPane) container).getParent());
        this.insets = insets;

        container.addMouseMotionListener(this);

        container.addMouseListener(this);

        emblem = Toolkit.getDefaultToolkit().createImage(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/rubemblem.png"));

    }

    public boolean isBorderOpaque() {
        return true;
    }
    private Color borderHLColor = new Color(240, 240, 240);

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {

        int btnWidth = 9;
        int btnHeight = 9;
        int corners = 18;

        int btnXOffset = -12;
        int btnYOffset = 8;
        int btnSpacer = 6;
        int btnX = c.getWidth() - btnWidth;
        int btnY = 0 + btnYOffset;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (AWTUtilitiesValidator.getInstance().isAWTUtlitiesAvailable() && !AWTUtilitiesValidator.getInstance().isTranslucencySupported()) {
            g2.setColor(LAFProperties.getInstance().getBorderColor());
            g2.fillRect(0, 0, width, height);
        }

        g2.setColor(LAFProperties.getInstance().getBorderColor());
        g2.fillRoundRect(0, 0, width, height, corners, corners);

        g2.setColor(borderHLColor);
        g2.fillRoundRect(insets.left - 1, insets.left - 1, width - ((insets.left - 1) * 2), height - ((insets.left - 1) * 2), corners - (corners / 5), corners - (corners / 5));

        g2.setColor(LAFProperties.getInstance().getTitleBarBackground());
        g2.fillRoundRect(insets.left, insets.left, width - (insets.left * 2), height - (insets.left * 2), corners - (corners / 5), corners - (corners / 5));

        closeBtnRect = new Rectangle(btnX + btnXOffset, btnY, btnWidth, btnHeight);
        drawCloseButton(g2, closeBtnRect, 0);
        if (mainFrame.isResizable()) {
            btnX = btnX - btnWidth - btnSpacer;
            maxBtnRect = new Rectangle(btnX + btnXOffset, btnY, btnWidth, btnHeight);
            drawMaxButton(g2, maxBtnRect, 0);
        } else {
            maxBtnRect = new Rectangle(0, 0, 0, 0);
        }

        g2.drawImage(emblem, 9, 7, c);
        g2.setFont(new Font("Arial", Font.BOLD, 11));
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        String text = mainFrame.getTitle();
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(new Color(255, 255, 255, 175));
        g2.drawString(text, (c.getWidth() / 2) - (fm.stringWidth(text) / 2) + 1, 18 + 1);
        g2.setColor(new Color(75, 75, 75));
        g2.drawString(text, (c.getWidth() / 2) - (fm.stringWidth(text) / 2), 18);
    }

    public Insets getBorderInsets(Component c) {

        return insets;
    }

    private void dispatchEvent(MouseEvent me) {
        if (closeBtnRect != null && closeBtnRect.contains(me.getX(), me.getY())) {      // wenn closebtn gedrückt
            if (me.getID() == 501) {
                drawCloseButton(container.getGraphics(), closeBtnRect, 2);
            }
            if (me.getID() == 502) {
                drawCloseButton(container.getGraphics(), closeBtnRect, 0);
                if (mainFrame.getDefaultCloseOperation() == WindowConstants.DISPOSE_ON_CLOSE) {
                    mainFrame.dispose();
                } else if (mainFrame.getDefaultCloseOperation() == WindowConstants.EXIT_ON_CLOSE) {
                    mainFrame.dispose();
                } else if (mainFrame.getDefaultCloseOperation() == WindowConstants.HIDE_ON_CLOSE) {
                    mainFrame.setVisible(false);
                }
            }
        }

        if (maxBtnRect != null && maxBtnRect.contains(me.getX(), me.getY()) && mainFrame.isResizable()) {      // wenn maxbtn gedrückt
            if (me.getID() == 501) {
                drawMaxButton(container.getGraphics(), maxBtnRect, 2);
            }
//            if (me.getID() == 502) {
//                drawMaxButton(container.getGraphics(), maxBtnRect, 0);
//                if (mainFrame.getExtendedState() == Frame.MAXIMIZED_BOTH) {
//                    mainFrame.setExtendedState(Frame.NORMAL);
//                } else {
//                    mainFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
//                }
//            }
        }
    }

    public void mouseClicked(MouseEvent me) {
    }

    public void mouseEntered(MouseEvent me) {
    }

    public void mouseExited(MouseEvent me) {
        if (!isMousePressed) {
            mainFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
        dispatchEvent(me);
    }

    public void mousePressed(MouseEvent me) {
        isMousePressed = true;
        localx = me.getX();
        localy = me.getY();
        eastScaleLocOnScreen = MouseInfo.getPointerInfo().getLocation().x;
        mainx = mainFrame.getX();
        mainy = mainFrame.getY();
        mainw = mainFrame.getWidth();
        mainh = mainFrame.getHeight();
        if (mainFrame.isResizable()) {
            if (localx >= mainFrame.getWidth() - insets.right) {
                doEScale = true;
            } else if (localx <= insets.left - 1) {
                doWScale = true;
            } else if (localy >= mainFrame.getHeight() - insets.bottom) {
                doSScale = true;
            }
        }
        dispatchEvent(me);
    }

    public void mouseReleased(MouseEvent me) {
        doEScale = false;
        doWScale = false;
        doSScale = false;
        isMousePressed = false;
        mainFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        dispatchEvent(me);
    }
    int mainw = 0;
    int mainh = 0;
    int eastScaleLocOnScreen = 0;
    boolean doEScale = false;
    boolean doWScale = false;
    boolean doSScale = false;
    boolean cHasRepainted = false;
    boolean mHasRepainted = false;
    boolean iHasRepainted = false;
    boolean isMousePressed = false;

    public void mouseDragged(MouseEvent e) {
        if (closeBtnRect != null && maxBtnRect != null) {
            if (!closeBtnRect.contains(localx, localy) && !maxBtnRect.contains(localx, localy)) {
                if (doEScale) {
                    mainFrame.setSize(mainw + (e.getX() - mainw), mainFrame.getHeight());
                } else if (doWScale) {
                    int xpos = eastScaleLocOnScreen - MouseInfo.getPointerInfo().getLocation().x;
                    mainFrame.setSize(mainw + xpos, mainFrame.getHeight());
                    mainFrame.setLocation(MouseInfo.getPointerInfo().getLocation().x, mainFrame.getLocation().y);
                } else if (doSScale) {
                    mainFrame.setSize(mainFrame.getWidth(), mainh + (e.getY() - mainh));
                } else {
                    Point p = MouseInfo.getPointerInfo().getLocation();
                    p.translate(-localx, -localy);
                    mainFrame.setLocation(p);
                }
            } else {
                if (closeBtnRect != null && closeBtnRect.contains(e.getX(), e.getY())) {
                    if (!cHasRepainted) {
                        drawCloseButton(container.getGraphics(), closeBtnRect, 2);
                        cHasRepainted = true;
                    }
                } else if (cHasRepainted) {
                    drawCloseButton(container.getGraphics(), closeBtnRect, 0);
                    cHasRepainted = false;
                }

                if (maxBtnRect != null && maxBtnRect.contains(e.getX(), e.getY())) {
                    if (!mHasRepainted) {
                        drawMaxButton(container.getGraphics(), maxBtnRect, 2);
                        mHasRepainted = true;
                    }
                } else if (mHasRepainted) {
                    drawMaxButton(container.getGraphics(), maxBtnRect, 0);
                    mHasRepainted = false;
                }
            }
        }
    }

    public void mouseMoved(MouseEvent e) {
        if (!isMousePressed) {
            Cursor cursor = new Cursor(Cursor.DEFAULT_CURSOR);
            if (e.getX() >= mainFrame.getWidth() - insets.right) {
                cursor = new Cursor(Cursor.E_RESIZE_CURSOR);
            } else if (e.getX() <= insets.left - 1) {
                cursor = new Cursor(Cursor.W_RESIZE_CURSOR);
            } else if (e.getY() >= mainFrame.getHeight() - insets.bottom) {
                cursor = new Cursor(Cursor.S_RESIZE_CURSOR);
            }
            mainFrame.setCursor(cursor);
        }

        if (closeBtnRect != null && closeBtnRect.contains(e.getX(), e.getY())) {
            if (!cHasRepainted) {
                drawCloseButton(container.getGraphics(), closeBtnRect, 1);
                cHasRepainted = true;
            }
        } else if (cHasRepainted) {
            drawCloseButton(container.getGraphics(), closeBtnRect, 0);
            cHasRepainted = false;
        }

        if (maxBtnRect != null && maxBtnRect.contains(e.getX(), e.getY())) {
            if (!mHasRepainted) {
                drawMaxButton(container.getGraphics(), maxBtnRect, 1);
                mHasRepainted = true;
            }
        } else if (mHasRepainted) {
            drawMaxButton(container.getGraphics(), maxBtnRect, 0);
            mHasRepainted = false;
        }
    }

    protected void drawCloseButton(Graphics g, Rectangle rect, int modus) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g2.setColor(new Color(215, 215, 215));
        g2.fillRect(rect.x, rect.y, rect.width, rect.height);

        BufferedImage bi = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_INT_ARGB);
        ImageObserver observer = new ImageIcon(bi).getImageObserver();
        Graphics2D gbi = (Graphics2D) bi.getGraphics();

        int x1 = 1;
        int y1 = 1;
        int x2 = rect.width - 2;
        int y2 = rect.height - 2;
        int x3 = 1;
        int y3 = rect.height - 2;
        int x4 = rect.width - 2;
        int y4 = 1;

        switch (modus) {
            case 0:
                gbi.setColor(new Color(255, 255, 255, 175));
                gbi.drawLine(x1 + 1, y1 + 1, x2 + 1, y2 + 1);
                gbi.drawLine(x3 + 1, y3 + 1, x4 + 1, y4 + 1);

                gbi.setColor(new Color(75, 75, 75));
                gbi.drawLine(x1, y1, x2, y2);
                gbi.drawLine(x3, y3, x4, y4);
                break;
            case 1:
                gbi.setColor(new Color(255, 255, 255, 175));
                gbi.drawLine(x1 + 1, y1 + 1, x2 + 1, y2 + 1);
                gbi.drawLine(x3 + 1, y3 + 1, x4 + 1, y4 + 1);

                gbi.setColor(new Color(188, 13, 0));
                gbi.drawLine(x1, y1, x2, y2);
                gbi.drawLine(x3, y3, x4, y4);
                break;
            case 2:
                gbi.setColor(new Color(188, 13, 0));
                gbi.drawLine(x1 + 1, y1 + 1, x2 + 1, y2 + 1);
                gbi.drawLine(x3 + 1, y3 + 1, x4 + 1, y4 + 1);
                break;
        }
        g2.drawImage(bi, rect.x, rect.y, observer);
    }

    protected void drawMaxButton(Graphics g, Rectangle rect, int modus) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(215, 215, 215));
        g2.fillRect(rect.x, rect.y, rect.width, rect.height);

        BufferedImage bi = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_INT_ARGB);
        ImageObserver observer = new ImageIcon(bi).getImageObserver();
        Graphics2D gbi = (Graphics2D) bi.getGraphics();

        int x1 = 1;
        int y1 = 1;
        int x2 = rect.width - 3;
        int y2 = rect.height - 3;

        switch (modus) {
            case 0:
                gbi.setColor(new Color(255, 255, 255, 175));
                gbi.drawRoundRect(x1 + 1, y1 + 2, x2, y2 - 1, 0, 0);

                gbi.setColor(new Color(75, 75, 75));
                gbi.drawLine(x1, y1 + 1, x2, x1 + 1);
                gbi.drawRoundRect(x1, y1, x2, y2, 0, 0);
                break;
            case 1:
                gbi.setColor(new Color(255, 255, 255, 175));
                gbi.drawRoundRect(x1 + 1, y1 + 2, x2, y2 - 1, 0, 0);

                gbi.setColor(new Color(0, 111, 205));
                gbi.drawLine(x1, y1 + 1, x2, x1 + 1);
                gbi.drawRoundRect(x1, y1, x2, y2, 0, 0);
                break;
            case 2:
                gbi.setColor(new Color(0, 111, 205));
                gbi.drawLine(x1 + 1, y1 + 2, x2 + 1, y1 + 2);
                gbi.drawRoundRect(x1 + 1, y1 + 1, x2, y2, 0, 0);
                break;
        }
        g2.drawImage(bi, rect.x, rect.y, observer);
    }
}
