/*
 * JPDFSigner - Sign PDFs online using smartcards (PDFOptimizedViewer.java)
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

import de.rub.dez6a3.jpdfsigner.control.Configurator;
import de.rub.dez6a3.jpdfsigner.control.SignAndStampablePagePanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 *
 * @author dan
 */
public class PDFOptimizedViewer extends JPanel {

    private Color gpColorLight = new Color(0, 0, 0, 0);
    private Color gpColorDark = new Color(0, 0, 0, 100);
    private GradientPaint gp = null;
    private Color shadowC = null;
    private int thisW = 0;
    private int thisH = 0;
    private int ppW = 0;
    private int ppH = 0;
    private int shadowSize = 0;
    private int pageArea = 0;
    private float alpha = 0;
    private float alphaSteps = 0;
    private int i = 0;
    private Area mask = null;
    private Area shadowParts = null;
    private int corners = 0;
    private float shadowDepth = 0.075f;  //in percent; 0-100
    private int shadowSizeDiv = 18;     //divider for shadow size - the higher the smaller the shadow
    private int cornerRoundness = 18;   //divider for shadow corner roundness - the higher the less the shadow roundness
    private Dimension prevPanelDim = new Dimension(0, 0);
    private Dimension prevPDFPageDim = new Dimension(0, 0);
    private BufferedImage buffer = null;
    private Graphics2D bufferg2 = null;
    private BufferedImage bgGradient = null;
    private Graphics2D bgGradientg2 = null;
    private SignAndStampablePagePanel pp_PageView = null;

    public PDFOptimizedViewer() {
        pp_PageView = Configurator.getInstance().getSignAndStampablePagePanel();
    }

    @Override
    public void paint(Graphics g) {
        if (getWidth() != prevPanelDim.width || getHeight() != prevPanelDim.height) {
            bgGradient = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            bgGradientg2 = (Graphics2D) bgGradient.getGraphics();
            prevPanelDim = getSize();
            gp = new GradientPaint(0, 0, gpColorLight, 0, getHeight(), gpColorDark, false);
            bgGradientg2.setPaint(gp);
            bgGradientg2.fillRect(0, 0, getWidth(), getHeight());
            drawPDFPage();
        }

        if (pp_PageView.getWidth() != prevPDFPageDim.width || pp_PageView.getHeight() != prevPDFPageDim.height) {
            drawPDFPage();
        }
        super.paint(bufferg2);
        g.drawImage(buffer, 0, 0, null);
    }

    private void drawPDFPage() {
        buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        bufferg2 = (Graphics2D) buffer.getGraphics();
        bufferg2.drawImage(bgGradient, 0, 0, this);
        prevPDFPageDim = pp_PageView.getSize();
        pageArea = ((pp_PageView.getWidth() + pp_PageView.getHeight()) / 2);
        corners = pageArea / cornerRoundness;
        shadowSize = pageArea / shadowSizeDiv;
        if (shadowSize > 30) {
            shadowSize = 30;
        }
        if (pp_PageView.getWidth() <= 1) {
            shadowSize = 0;
        }
        alpha = 0;
        if (shadowSize > 0) {
            alphaSteps = (255f * shadowDepth) / shadowSize;
        } else {
            alphaSteps = 0;
        }
        mask = new Area(new Rectangle2D.Float((getWidth() / 2) - (pp_PageView.getWidth() / 2), (getHeight() / 2) - (pp_PageView.getHeight() / 2), pp_PageView.getWidth(), pp_PageView.getHeight()));
        for (i = shadowSize; i > 0; i -= 2) {
            alpha += alphaSteps;
            if (alpha > 255) {
                alpha = 255;
            }
            bufferg2.setColor(new Color(0, 0, 0, Math.round(alpha)));
            ppW = pp_PageView.getWidth() + i;
            ppH = pp_PageView.getHeight() + i;

            shadowParts = new Area(new RoundRectangle2D.Float(((getWidth() / 2) - (ppW / 2)), ((getHeight() / 2) - (ppH / 2)), ppW, ppH, corners, corners));
            shadowParts.subtract(mask);
            bufferg2.fill(shadowParts);
        }
    }
}
