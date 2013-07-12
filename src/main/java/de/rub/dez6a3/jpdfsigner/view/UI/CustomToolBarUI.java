/*
 * JPDFSigner - Sign PDFs online using smartcards (CustomToolBarUI.java)
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

import com.itextpdf.text.Image;
import de.rub.dez6a3.jpdfsigner.control.Configurator;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolBarUI;
import javax.swing.plaf.metal.MetalToolBarUI;
import javax.swing.plaf.multi.MultiToolBarUI;
import org.apache.log4j.Logger;

/**
 *
 * @author dan
 */
public class CustomToolBarUI extends BasicToolBarUI {

    private ImageIcon draggerIcon = null;
    private int leftIconMargin = 4;                 //6 ist der margin rechts und links vom icon

    public static Logger log = Logger.getLogger(CustomToolBarUI.class);

    public CustomToolBarUI() {
        try {
            draggerIcon = new ImageIcon(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/toolbar_dragger.png"));
        } catch (Exception e) {
            draggerIcon = new ImageIcon();
            log.warn("Cannot set ToolBar Drag-icon. - " + e);
        }
    }

    public static ComponentUI createUI(JComponent c) {
        return new CustomToolBarUI();

    }

    @Override
    public void paint(Graphics g, JComponent c) {
//        super.paint(g, c);
        BufferedImage bi = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D big2 = (Graphics2D) bi.getGraphics();

        big2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint back = new GradientPaint(0, 0, LAFProperties.getInstance().getTitleBarBackground(), 0, c.getHeight(), new Color(165, 165, 165), false);
        big2.setPaint(back);
        big2.fillRect(0, 0, c.getWidth(), c.getHeight());

        big2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        big2.setColor(LAFProperties.getInstance().getBorderColorHighlight());
        big2.drawLine(0, c.getHeight() - 2, c.getWidth(), c.getHeight() - 2);

        big2.setColor(LAFProperties.getInstance().getBorderColor());
        big2.drawLine(0, c.getHeight() - 1, c.getWidth(), c.getHeight() - 1);

        big2.setColor(Color.white);
        big2.drawLine(0, 0, 0, c.getHeight() - 2);
        big2.drawLine(c.getWidth() - 1, 0, c.getWidth() - 1, c.getHeight() - 2);

        big2.drawImage(draggerIcon.getImage(), leftIconMargin, (c.getHeight()/2)-(draggerIcon.getIconHeight()/2), draggerIcon.getImageObserver());

        g.drawImage(bi, 0, 0, new ImageIcon(bi).getImageObserver());        
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        c.setBorder(BorderFactory.createEmptyBorder());
        Dimension toolbarDim = new Dimension(100, 38);
        c.setSize(toolbarDim);
        c.setPreferredSize(toolbarDim);
        c.setBorder(BorderFactory.createEmptyBorder(0, draggerIcon.getIconWidth(), 0, 0));
    }
}
