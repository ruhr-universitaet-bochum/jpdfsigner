/*
 * JPDFSigner - Sign PDFs online using smartcards (CustomTextFieldUI.java)
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
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.plaf.basic.BasicTextUI;
import javax.swing.text.Element;
import javax.swing.text.FieldView;
import javax.swing.text.View;

/**
 *
 * @author dan
 */
public class CustomTextFieldUI extends BasicTextFieldUI {

    private Insets cInsets = null;

    public CustomTextFieldUI() {
        super();
        cInsets = new Insets(3, 5, 3, 5);
    }

    public static ComponentUI createUI(JComponent c) {
        return new CustomTextFieldUI();
    }

    @Override
    protected void paintSafely(Graphics g) {
        paintCustomBackground(g);
        super.paintSafely(g);
    }

    protected void paintCustomBackground(Graphics g) {
        Rectangle visRect = getVisibleEditorRect();             //kann nur hier ermittelt werden
        int width = visRect.width + cInsets.left + cInsets.right;      //sichtbaren texthintergrund mit insets multiplizieren um gesamtgröße für hintergrund zu ermitteln
        int height = visRect.height + cInsets.top + cInsets.bottom;

        int cornerValue = 0;
        if (width <= height) {        //rundung der ecken ist nur so groß wie die kleinste seitenlänge
            cornerValue = (int) (width * 1.3);
        } else {
            cornerValue = (int) (height * 1.3);
        }
        cornerValue = cornerValue / 3;

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(LAFProperties.getInstance().getTextFieldBorderColor());
        g2.fillRoundRect(0, 0, width, height, cornerValue, cornerValue);        //paint border

        g2.setColor(LAFProperties.getInstance().getTextFieldHighlightColor());
        g2.fillRoundRect(1, 1, width - 2, height - 2, cornerValue-(cornerValue/6), cornerValue-(cornerValue/6));    //paint border highlight

        GradientPaint gp = new GradientPaint(0f, 2f, LAFProperties.getInstance().getTextFieldBackgroundColorTop(), 0f, height - 4, LAFProperties.getInstance().getTextFieldBackgroundColorBottom());
        g2.setPaint(gp);
        g2.fillRoundRect(2, 2, width - 4, height - 4, cornerValue-(cornerValue/6), cornerValue-(cornerValue/6));    //paint background
        g2.setColor(comp.getBackground());
        g2.fillRoundRect(2, 2, width - 4, height - 4, cornerValue-(cornerValue/6), cornerValue-(cornerValue/6));
    }

    private JComponent comp = null;

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        comp = c;
        JTextField f = (JTextField) c;
        f.setOpaque(false);
        f.setBackground(new Color(0,0,0,0));
        Border border = BorderFactory.createEmptyBorder(cInsets.top, cInsets.left, cInsets.bottom, cInsets.right);      //border fungiert als insets weil insets so nicht existieren
        f.setBorder(border);
    }
}
