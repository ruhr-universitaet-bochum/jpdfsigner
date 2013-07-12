/*
 * JPDFSigner - Sign PDFs online using smartcards (JFrameTitledComponentBorder.java)
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

import de.rub.dez6a3.jpdfsigner.control.Configurator;
import de.rub.dez6a3.jpdfsigner.model.IPDFProcessor;
import de.rub.dez6a3.jpdfsigner.view.UI.LAFProperties;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRootPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

public class JFrameTitledComponentBorder implements Border, MouseListener, MouseMotionListener, SwingConstants {

    private static JFrameTitledComponentBorder instance = null;
    private JFrame mainFrame = null;
    private Insets insets = null;
    private Color lightBackColor = new Color(235, 235, 235);
    private Font hlFont = null;
    private String tooltipText = "Ruhr-Universität Bochum | JPDFSigner v. 1.2 RC";
    private ArrayList<TitledComponent> comps = new ArrayList<TitledComponent>();
    public final static int DOCK_TOP_LEFT = 0;
    public final static int DOCK_TOP_RIGHT = 1;
    public final static int DOCK_BOTTOM_LEFT = 2;
    public final static int DOCK_BOTTOM_RIGHT = 3;
    private int startMouseX = 0;
    private int startMouseY = 0;
    private boolean mousePressed = false;
    private int grabbableArea = 10;
    private Dimension origDim = null;
    private int NO_RESIZE = -1;
    private int RIGHT_EDGE_DRAG = 0;
    private int BOTTOM_EDGE_DRAG = 1;
    private int LEFT_EDGE_DRAG = 2;
    private int TOP_EDGE_DRAG = 3;
    private int CORNER_DRAG = 4;
    private int resizing = NO_RESIZE;
    private Cursor eastResizeCursor = null;
    private Cursor southResizeCursor = null;
    private Cursor southEastResizeCursor = null;
    private Cursor defaultCursor = null;

    public void addComponent(int pos, JComponent comp) {
        addComponent(pos, comp, 0, 0);
    }

    public void addComponent(int pos, JComponent comp, int xOff, int yOff) {
        TitledComponent tc = new TitledComponent(pos, comp, this, mainFrame);
        tc.setXOffset(xOff);
        tc.setYOffset(yOff);
        comps.add(tc);
    }

    public void removeComponent(TitledComponent comp) {
        comps.remove(comp);
    }

    public ArrayList<TitledComponent> getComponents() {
        return comps;
    }

    public static JFrameTitledComponentBorder getInstance(JComponent container, Insets insets) {
        if (instance == null) {
            instance = new JFrameTitledComponentBorder(container, insets);
        }
        return instance;
    }

    private JFrameTitledComponentBorder(JComponent container, Insets insets) {
        eastResizeCursor = new Cursor(Cursor.E_RESIZE_CURSOR);
        southResizeCursor = new Cursor(Cursor.S_RESIZE_CURSOR);
        southEastResizeCursor = new Cursor(Cursor.SE_RESIZE_CURSOR);
        defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);

        this.insets = insets;
        mainFrame = (JFrame) (((JRootPane) container).getParent());
        hlFont = new Font("Arial", Font.BOLD, 11);
        container.addMouseListener(this);
        container.addMouseMotionListener(this);
        installComponents();
    }

    private void installComponents() {
        ImageIcon emblem = new ImageIcon(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/rubemblem.png")));
        JLabel emblemLabel = new JLabel(emblem);
        addComponent(DOCK_TOP_LEFT, emblemLabel, 9, 8);
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Color borderColor = new Color(40, 40, 40);
        Graphics2D big = (Graphics2D) g;
        big.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //draws border and background
        big.setColor(borderColor);
        if (mainFrame.getExtendedState() == JFrame.MAXIMIZED_BOTH) {
            big.fillRect(0, 0, width, height);
        } else {
            big.fillRoundRect(0, 0, width, height, 18, 18);
        }
        big.setColor(Color.white);
        if (mainFrame.getExtendedState() == JFrame.MAXIMIZED_BOTH) {
            big.fillRect(insets.left, insets.left, width - insets.left - insets.right, insets.top + 10);
        } else {
            big.fillRoundRect(insets.left, insets.left, width - insets.left - insets.right, insets.top + 10, 14, 14);
        }
        GradientPaint bgGradient = new GradientPaint(0, 0, lightBackColor, 0, insets.top, LAFProperties.getInstance().getTitleBarBackground(), false);
        big.setPaint(bgGradient);
        if (mainFrame.getExtendedState() == JFrame.MAXIMIZED_BOTH) {
            big.fillRect(insets.left + 1, insets.left + 1, width - insets.left - insets.right - 2, insets.top + 10);
        } else {
            big.fillRoundRect(insets.left + 1, insets.left + 1, width - insets.left - insets.right - 2, insets.top + 10, 14, 14);
        }
        //draws the static foreground stuff like headline, icons etc...
//        big.drawImage(emblem, 9, 7, c);
        big.setFont(hlFont);
        String text = mainFrame.getTitle();
        FontMetrics fm = big.getFontMetrics();
        big.setColor(new Color(255, 255, 255, 175));
        big.drawString(text, (c.getWidth() / 2) - (fm.stringWidth(text) / 2) + 1, 18 + 1);
        big.setColor(new Color(75, 75, 75));
        big.drawString(text, (c.getWidth() / 2) - (fm.stringWidth(text) / 2), 18);

        drawFooter(c, big, x, y, width, height);

        for (TitledComponent tComp : comps) {
            Rectangle rect = tComp.getRect();
            SwingUtilities.paintComponent(g, tComp.getComponent(), (Container) c, rect);
        }
    }

    private void dispatchEvent(MouseEvent me) {
        for (TitledComponent tComp : comps) {
            Rectangle rect = tComp.getRect();
            if (rect != null && rect.contains(me.getX(), me.getY())) {
                JComponent jComp = tComp.getComponent();
                Point pt = new Point(me.getPoint().x - rect.x, me.getPoint().y - rect.y);
                if (!tComp.isMouseEntered()) {
                    jComp.dispatchEvent(new MouseEvent(jComp, me.MOUSE_ENTERED, me.getWhen(), me.getModifiers(), pt.x, pt.y, me.getClickCount(), me.isPopupTrigger(), me.getButton()));
                    tComp.setMouseEntered(true);
                }
                jComp.setBounds(rect);
                jComp.dispatchEvent(new MouseEvent(jComp, me.getID(), me.getWhen(), me.getModifiers(), pt.x, pt.y, me.getClickCount(), me.isPopupTrigger(), me.getButton()));
                if (!jComp.isValid()) {
                    mainFrame.getRootPane().repaint(tComp.getRect());
                }
            } else {
                if (tComp.isMouseEntered()) {
                    JComponent jComp = tComp.getComponent();
                    jComp.dispatchEvent(new MouseEvent(jComp, me.MOUSE_EXITED, me.getWhen(), me.getModifiers(), 0, 0, me.getClickCount(), me.isPopupTrigger(), me.getButton()));
                    tComp.setMouseEntered(false);
                    mainFrame.getRootPane().repaint(tComp.getRect());
                }
            }
        }
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return insets;
    }

    @Override
    public boolean isBorderOpaque() {
        return true;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
//        dispatchEvent(e);
    }
    private boolean fitToSize = false;

    @Override
    public void mousePressed(MouseEvent e) {
        mousePressed = true;
        startMouseX = e.getX();
        startMouseY = e.getY();
        origDim = new Dimension(mainFrame.getPreferredSize().width, mainFrame.getPreferredSize().height);
        resizing = getMouseDragAction(e);

        fitToSize = Configurator.getInstance().getSignAndStampablePagePanel().getPdfProcessor().isFitToSize();
        if (resizing != NO_RESIZE) {      //prevents recalculating page-size everytime the view is resized. pages will be actualized, when mouse releases
            Configurator.getInstance().getSignAndStampablePagePanel().getPdfProcessor().setFitToSize(false);
        }
        dispatchEvent(e);
    }

    private int getMouseDragAction(MouseEvent e) {
        int action = NO_RESIZE;
        if (e.getX() >= mainFrame.getWidth() - grabbableArea && e.getY() >= mainFrame.getHeight() - grabbableArea) {
            action = CORNER_DRAG;
        } else if (e.getX() >= mainFrame.getWidth() - grabbableArea) {
            action = RIGHT_EDGE_DRAG;
        } else if (e.getY() >= mainFrame.getHeight() - grabbableArea) {
            action = BOTTOM_EDGE_DRAG;
        } else if (e.getX() <= grabbableArea) {
//            action = LEFT_EDGE_DRAG;
            action = NO_RESIZE;         //disabling left-edge resize
        } else if (e.getY() <= grabbableArea) {
//            action = TOP_EDGE_DRAG;
            action = NO_RESIZE;         //disabling top-edge resize
        }
        return action;
    }

    private void setCursor(MouseEvent e) {
        int mouseDragAction = getMouseDragAction(e);
        if (mouseDragAction == RIGHT_EDGE_DRAG) {
            mainFrame.setCursor(eastResizeCursor);
        } else if (mouseDragAction == BOTTOM_EDGE_DRAG) {
            mainFrame.setCursor(southResizeCursor);
        } else if (mouseDragAction == CORNER_DRAG) {
            mainFrame.setCursor(southEastResizeCursor);
        } else {
            mainFrame.setCursor(defaultCursor);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (fitToSize && origDim.width != mainFrame.getPreferredSize().width || origDim.height != mainFrame.getPreferredSize().height) {
            IPDFProcessor pdfViewer = Configurator.getInstance().getSignAndStampablePagePanel().getPdfProcessor();
            pdfViewer.setFitToSize(true);
            pdfViewer.fitPagesInView();
        }
        setCursor(e);
        resizing = NO_RESIZE;
        dispatchEvent(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        dispatchEvent(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        dispatchEvent(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Dimension dim;
        if (resizing == CORNER_DRAG) {
            int x = e.getX() - startMouseX;
            int y = e.getY() - startMouseY;
            dim = new Dimension(origDim.width + x, origDim.height + y);
            mainFrame.setPreferredSize(dim);
            mainFrame.setSize(dim);
        } else if (resizing == RIGHT_EDGE_DRAG) {
            int x = e.getX() - startMouseX;
            dim = new Dimension(origDim.width + x, origDim.height);
            mainFrame.setPreferredSize(dim);
            mainFrame.setSize(dim);
        } else if (resizing == BOTTOM_EDGE_DRAG) {
            int y = e.getY() - startMouseY;
            dim = new Dimension(origDim.width, origDim.height + y);
            mainFrame.setPreferredSize(dim);
            mainFrame.setSize(dim);
        } else if (resizing == LEFT_EDGE_DRAG) {
            int x = e.getX() - startMouseX;
//            mainFrame.setSize(new Dimension(origDim.width + x, origDim.height));
//            frameToMousePosition(e);
        } else if (resizing == NO_RESIZE) {
            frameToMousePosition(e);
            dispatchEvent(e);
        }
    }

    private void frameToMousePosition(MouseEvent e) {
        Point newPos = e.getLocationOnScreen();
        newPos.translate(-startMouseX, -startMouseY);
        mainFrame.setLocation(newPos);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        setCursor(e);
        dispatchEvent(e);
    }

    private void drawFooter(Component c, Graphics2D g, int x, int y, int width, int height) {
        Color highlightColor = new Color(255, 255, 255, 180);

        g.setColor(highlightColor);
        if (mainFrame.getExtendedState() == JFrame.MAXIMIZED_BOTH) {
            g.fillRect(insets.left, height - insets.bottom - 7, width - insets.left - insets.right, insets.bottom - insets.left + 7);
        } else {
            g.fillRoundRect(insets.left, height - insets.bottom - 7, width - insets.left - insets.right, insets.bottom - insets.left + 7, 14, 14);
        }
        GradientPaint back = new GradientPaint(0, height - insets.bottom, new Color(190, 190, 190), 0, height, new Color(138, 138, 138), false);
        g.setPaint(back);
        if (mainFrame.getExtendedState() == JFrame.MAXIMIZED_BOTH) {
            g.fillRect(insets.left + 1, height - insets.bottom - 8, width - insets.left - insets.right - 2, insets.bottom - insets.left + 7);
        } else {
            g.fillRoundRect(insets.left + 1, height - insets.bottom - 8, width - insets.left - insets.right - 2, insets.bottom - insets.left + 7, 14, 14);
        }
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        g.setColor(new Color(255, 255, 255, 75));
        g.drawString(tooltipText, insets.left + 8, height - insets.bottom + g.getFontMetrics().getHeight() + 1);
        g.setColor(new Color(40, 40, 40, 240));
        g.drawString(tooltipText, insets.left + 7, height - insets.bottom + g.getFontMetrics().getHeight());

        g.setColor(new Color(30, 30, 30, 230));
        g.drawLine(insets.left, height - insets.bottom, width - insets.right - 1, height - insets.bottom);

        g.setColor(highlightColor);
        g.drawLine(insets.left, height - insets.bottom + 1, width - insets.right - 1, height - insets.bottom + 1);
    }
}

class TitledComponent {

    private JFrameTitledComponentBorder jftBorder;
    private JFrame mainFrame;
    private JComponent comp;
    private int pos;
    private int xOff = 0;
    private int yOff = 0;
    private boolean mouseEntered = false;

    public TitledComponent(int pos, JComponent comp, JFrameTitledComponentBorder jftBorder, JFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.pos = pos;
        this.comp = comp;
        this.jftBorder = jftBorder;
    }

    public void setXOffset(int xOff) {
        this.xOff = xOff;
    }

    public int getXOffset() {
        return xOff;
    }

    public void setYOffset(int yOff) {
        this.yOff = yOff;
    }

    public int getYOffset() {
        return yOff;
    }

    public boolean isMouseEntered() {
        return mouseEntered;
    }

    public void setMouseEntered(boolean mouseEntered) {
        this.mouseEntered = mouseEntered;
    }

    public JComponent getComponent() {
        return comp;
    }

    public Rectangle getRect() {
        Rectangle rect;
        ArrayList<TitledComponent> topLeftComps = getCompsByPosition(JFrameTitledComponentBorder.DOCK_TOP_LEFT);
        ArrayList<TitledComponent> topRightComps = getCompsByPosition(JFrameTitledComponentBorder.DOCK_TOP_RIGHT);
        ArrayList<TitledComponent> bottomLeftComps = getCompsByPosition(JFrameTitledComponentBorder.DOCK_BOTTOM_LEFT);
        ArrayList<TitledComponent> bottomRightComps = getCompsByPosition(JFrameTitledComponentBorder.DOCK_BOTTOM_RIGHT);
        if (pos == JFrameTitledComponentBorder.DOCK_TOP_LEFT) {
            rect = calcTopLeft(topLeftComps);
        } else if (pos == JFrameTitledComponentBorder.DOCK_TOP_RIGHT) {
            rect = calcTopRightRect(topRightComps);
        } else if (pos == JFrameTitledComponentBorder.DOCK_BOTTOM_LEFT) {
            rect = calcBottomLeft(bottomLeftComps);
        } else if (pos == JFrameTitledComponentBorder.DOCK_BOTTOM_RIGHT) {
            rect = calcBottomRightRect(bottomRightComps);
        } else {
            rect = calcTopLeft(topLeftComps);
        }
        return rect;
    }

    private ArrayList<TitledComponent> getCompsByPosition(int dock) {
        ArrayList<TitledComponent> comps = new ArrayList<TitledComponent>();
        for (TitledComponent comp : jftBorder.getComponents()) {
            if (comp.getPosition() == dock) {
                comps.add(comp);
            }
        }
        return comps;
    }

    private Rectangle calcTopLeft(ArrayList<TitledComponent> borderComps) {
        Rectangle rect;
        int currXTL = xOff;
        int currYTL = yOff;
        for (TitledComponent tComp : borderComps) {
            JComponent jComp = tComp.getComponent();
            if (tComp == this) {
                break;
            }
            currXTL += jComp.getPreferredSize().width + tComp.getXOffset();
        }
        rect = new Rectangle(currXTL, currYTL, comp.getPreferredSize().width, comp.getPreferredSize().height);
        return rect;
    }

    private Rectangle calcTopRightRect(ArrayList<TitledComponent> borderComps) {
        Rectangle rect;
        int currX = mainFrame.getWidth();
        int currY = yOff;
        for (TitledComponent tComp : borderComps) {
            JComponent jComp = tComp.getComponent();
            currX -= (jComp.getPreferredSize().width - tComp.getXOffset());
            if (tComp == this) {
                break;
            }
        }
        rect = new Rectangle(currX, currY, comp.getPreferredSize().width, comp.getPreferredSize().height);
        return rect;
    }

    private Rectangle calcBottomLeft(ArrayList<TitledComponent> borderComps) {
        Rectangle rect;
        int currXTL = xOff;
        int currYTL = mainFrame.getHeight() + yOff - comp.getPreferredSize().height;
        for (TitledComponent tComp : borderComps) {
            JComponent jComp = tComp.getComponent();
            if (tComp == this) {
                break;
            }
            currXTL += jComp.getPreferredSize().width + tComp.getXOffset();
        }
        rect = new Rectangle(currXTL, currYTL, comp.getPreferredSize().width, comp.getPreferredSize().height);
        return rect;
    }

    private Rectangle calcBottomRightRect(ArrayList<TitledComponent> borderComps) {
        Rectangle rect;
        int currX = mainFrame.getWidth();
        int currY = mainFrame.getHeight() + yOff - comp.getPreferredSize().height;
        for (TitledComponent tComp : borderComps) {
            JComponent jComp = tComp.getComponent();
            currX -= (jComp.getPreferredSize().width - tComp.getXOffset());
            if (tComp == this) {
                break;
            }
        }
        rect = new Rectangle(currX, currY, comp.getPreferredSize().width, comp.getPreferredSize().height);
        return rect;
    }

    public int getPosition() {
        return pos;
    }
}
