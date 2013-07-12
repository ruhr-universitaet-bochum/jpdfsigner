/*
 * JPDFSigner - Sign PDFs online using smartcards (CustomComboBoxUI.java)
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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.CellRendererPane;
import javax.swing.ComboBoxEditor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.plaf.metal.MetalComboBoxButton;
import sun.swing.DefaultLookup;

/**
 *
 * @author dan
 * ACHTUNG: Diese UI ist propräitär und sollte vor Benutzung in anderen Programmen evtl überarbeitet werden!!!
 */
public class CustomComboBoxUI extends BasicComboBoxUI {

    private Insets cInsets = new Insets(0, 5, 0, 0);

    public static ComponentUI createUI(JComponent c) {
        return new CustomComboBoxUI();
    }

//    public CustomComboBoxUI() {
//        super();
//    }

    @Override
    public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
        ListCellRenderer renderer = comboBox.getRenderer();
        Component c;

        if (hasFocus && !isPopupVisible(comboBox)) {
            c = renderer.getListCellRendererComponent(listBox,
                    comboBox.getSelectedItem(),
                    -1,
                    true,
                    false);
        } else {
            c = renderer.getListCellRendererComponent(listBox,
                    comboBox.getSelectedItem(),
                    -1,
                    false,
                    false);
            c.setBackground(UIManager.getColor("ComboBox.background"));
        }
        c.setFont(comboBox.getFont());
        if (hasFocus && !isPopupVisible(comboBox)) {
            c.setForeground(listBox.getSelectionForeground());
            c.setBackground(listBox.getSelectionBackground());
        } else {
            if (comboBox.isEnabled()) {
                c.setForeground(comboBox.getForeground());
                c.setBackground(comboBox.getBackground());
            } else {
                c.setForeground(DefaultLookup.getColor(
                        comboBox, this, "ComboBox.disabledForeground", null));
                c.setBackground(DefaultLookup.getColor(
                        comboBox, this, "ComboBox.disabledBackground", null));
            }
        }

        // Fix for 4238829: should lay out the JPanel.
        boolean shouldValidate = false;
        if (c instanceof JPanel) {
            shouldValidate = true;
        }
        if (c instanceof JComponent) {              // zeichnet den hintergrund nicht im auswahlfenster
            JComponent jc = (JComponent) c;
            jc.setBackground(new Color(0, 0, 0, 0));
        }
        currentValuePane.paintComponent(g, c, comboBox, bounds.x, bounds.y,
                bounds.width, bounds.height, shouldValidate);
    }

    @Override
    public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Rectangle insetBounds = comboBox.getBounds();
        int cornerValue = 0;
        if (insetBounds.width <= insetBounds.height) {        //rundung der ecken ist nur so groß wie die kleinste seitenlänge
            cornerValue = (int) (insetBounds.width * 1.3);
        } else {
            cornerValue = (int) (insetBounds.height * 1.3);
        }
        cornerValue = cornerValue / 3;

        if (popup.isVisible()) {
            g2.setColor(LAFProperties.getInstance().getTextFieldBorderColor());
            g2.fillRoundRect(0, 0, insetBounds.width, insetBounds.height, cornerValue, cornerValue);        //paint border
            g2.fillRect(0, (int) (insetBounds.getHeight() / 2), insetBounds.width, insetBounds.height);                   //rechte und linke seite sind nicht abgerundet

            g2.setColor(LAFProperties.getInstance().getTextFieldHighlightColor());
            g2.fillRoundRect(1, 1, insetBounds.width - 2, insetBounds.height - 2, cornerValue - (cornerValue / 6), cornerValue - (cornerValue / 6));    //paint border highlight
            g2.fillRect(1, (int) (insetBounds.getHeight() / 2), insetBounds.width - 2, insetBounds.height - 2);                 //rechte und linke seite sind nicht abgerundet

            GradientPaint gp = new GradientPaint(0f, 2f, LAFProperties.getInstance().getTextFieldBackgroundColorTop(), 0f, insetBounds.height - 4, LAFProperties.getInstance().getTextFieldBackgroundColorBottom());
            g2.setPaint(gp);
            g2.fillRoundRect(2, 2, insetBounds.width - 4, insetBounds.height - 4, cornerValue - (cornerValue / 6), cornerValue - (cornerValue / 6));    //paint background
            g2.fillRect(2, (int) (insetBounds.getHeight() / 2), insetBounds.width - 4, insetBounds.height - 4);                 //rechte und linke seite sind abgerundet
        } else {
            g2.setColor(LAFProperties.getInstance().getTextFieldBorderColor());
            g2.fillRoundRect(0, 0, insetBounds.width, insetBounds.height, cornerValue, cornerValue);        //paint border

            g2.setColor(LAFProperties.getInstance().getTextFieldHighlightColor());
            g2.fillRoundRect(1, 1, insetBounds.width - 2, insetBounds.height - 2, cornerValue - (cornerValue / 6), cornerValue - (cornerValue / 6));    //paint border highlight

            GradientPaint gp = new GradientPaint(0f, 2f, LAFProperties.getInstance().getTextFieldBackgroundColorTop(), 0f, insetBounds.height - 4, LAFProperties.getInstance().getTextFieldBackgroundColorBottom());
            g2.setPaint(gp);
            g2.fillRoundRect(2, 2, insetBounds.width - 4, insetBounds.height - 4, cornerValue - (cornerValue / 6), cornerValue - (cornerValue / 6));    //paint background

            g2.setColor(comp.getBackground());
            g2.fillRoundRect(2, 2, insetBounds.width - 4, insetBounds.height - 4, cornerValue - (cornerValue / 6), cornerValue - (cornerValue / 6));
        }
    }

    @Override
    protected JButton createArrowButton() {
        JButton arrowBtn = new JButton();
        arrowBtn.setUI(new BasicButtonUI() {

            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (popup.isVisible()) {
                    paintButton(g2, c.getWidth(), c.getHeight(), true);
                } else {
                    paintButton(g2, c.getWidth(), c.getHeight(), false);
                }
            }

            private void paintButton(Graphics2D g2, int width, int height, boolean isPressed) {
                int size = 5;       //Dieser wert ist ein teil der gesamtgröße ( 1/size quasi)

                Rectangle rect = new Rectangle(width / size, height / size, width - ((width / size) * 2), height - ((height / size) * 2));
                int scaledX = rect.x;
                int scaledY = rect.y;
                int scaledWidth = rect.width;
                int scaledHeight = rect.height;
                g2.setColor(LAFProperties.getInstance().getTextFieldBackgroundColorBottom().darker());
                g2.fillOval(scaledX, scaledY, scaledWidth, scaledHeight);

                Color lightColor = new Color(255, 255, 255, 120);       //Die alpha farbe für den button - heller und dunkler bleuchtungsbereich
                Color darkColor = new Color(100, 100, 100, 120);
                Color northColor = null;
                Color southColor = null;
                if (isPressed) {
                    northColor = darkColor;
                    southColor = lightColor;
                } else {
                    northColor = lightColor;
                    southColor = darkColor;
                }
                GradientPaint hl = new GradientPaint(scaledX, scaledY, northColor, scaledX + scaledWidth, scaledY + scaledHeight, southColor, false);
                g2.setPaint(hl);
                g2.fillOval(scaledX, scaledY, scaledWidth, scaledHeight);

                Polygon arrow = new Polygon();
                int offsetX = 0;    //verschiebt den pfeil um diesen Wert auf der jeweiligen achse
                int offsetY = 1;
                int widthOffset = 1;    //diese werte werden aufaddiert. je höher der wert, desto länger die jeweilige achse.
                int heightOffset = 0;
                arrow.addPoint((((scaledWidth / 3) + scaledX) - widthOffset) + offsetX, (((scaledHeight / 3) + scaledY) - heightOffset) + offsetY);
                arrow.addPoint((((scaledWidth - (scaledWidth / 3)) + scaledX) + widthOffset) + offsetX, (((scaledHeight / 3) + scaledY) - heightOffset) + offsetY);
                arrow.addPoint(((scaledWidth / 2) + scaledX) + offsetX, (((scaledHeight - (scaledHeight / 3)) + scaledY) + heightOffset) + offsetY);

                g2.setColor(Color.white);   //pfeilfarbe
                g2.fill(arrow);
            }
        });
        arrowBtn.setOpaque(false);
        return arrowBtn;
    }

    private JComponent comp = null;

    @Override
    public void installUI(JComponent c) {
        comp = c;
        UIManager.put("ComboBox.selectionForeground", Color.black);
        UIManager.put("ComboBox.selectionBackground", Color.lightGray);
        UIManager.put("ComboBox.background", Color.white);
        UIManager.put("ComboBox.disabledBackground", Color.white);
        UIManager.put("ComboBox.disabledForeground", Color.lightGray);
        UIManager.put("ComboBox.font", new Font("Arial", Font.BOLD, 11));
        super.installUI(c);
        JComboBox combo = (JComboBox) c;
        combo.setBorder(BorderFactory.createEmptyBorder(cInsets.top, cInsets.left, cInsets.bottom, cInsets.right));     //border wird als margin (inset) genutzt
        Dimension dim = new Dimension(21, 21);                                                                          //höhe wird auf jtextfield höhe gesetzt
        combo.setPreferredSize(dim);
        combo.setSize(dim);
        c.setBackground(new Color(0,0,0,0));
//        combo.setMaximumSize(dim);
        combo.setMinimumSize(dim);
        c.setOpaque(false);
    }

    @Override
    protected ComboPopup createPopup() {
        BasicComboPopup popup = new BasicComboPopup(comboBox);
        Border border = new AbstractBorder() {

            private Insets insets = new Insets(2, 1, 1, 1);

            @Override
            public Insets getBorderInsets(Component c) {
                return insets;
            }

            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                int realWidth = width - 1;
                int realHeight = height - 1;
                g.setColor(LAFProperties.getInstance().getTextFieldBorderColor());
                g.drawLine(x, y, x, realHeight);
                g.drawLine(x, realHeight, realWidth, realHeight);
                g.drawLine(realWidth, realHeight, realWidth, y);
            }
        };
        popup.setBorder(border);
        popup.getAccessibleContext().setAccessibleParent(comboBox);
        return popup;
    }

    @Override
    protected void installListeners() {
        comboBox.addPopupMenuListener(new PopupMenuListener() {     //für den fall das dass das popupmnenu ausserhalb der cmbobox weggeklickt wird
            // wird die ganze box gerepainted

            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                try {
                    comboBox.repaint();
                } catch (Exception ex) {
                }
            }

            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                try {
                    comboBox.repaint();
                } catch (Exception ex) {
                }
            }

            public void popupMenuCanceled(PopupMenuEvent e) {
                try {
                    comboBox.repaint();
                } catch (Exception ex) {
                }
            }
        });
    }
}
