/*
 * JPDFSigner - Sign PDFs online using smartcards (Splash.java)
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

import de.rub.dez6a3.jpdfsigner.control.language.LanguageFactory;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JWindow;

/**
 *
 * @author dan
 */
public class Splash extends Thread {

    private JWindow splashFrame;
    private Image splashImg;
    private String loadingMessage = LanguageFactory.getText(LanguageFactory.SPLASH_LOADING);
    private int percentBar = 0;
    private int splashWidth = 0;
    private int splashHeight = 0;
    private BufferedImage bufferedImage;
    private Graphics2D bg;
    private boolean graphicsInit = false;

    public Splash() {
        splashImg = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/jpdfsignersplash12.png"));
        ImageIcon splashImgIcon = new ImageIcon(splashImg);
        splashWidth = splashImgIcon.getIconWidth();
        splashHeight = splashImgIcon.getIconHeight();
        bufferedImage = new BufferedImage(splashWidth, splashHeight, BufferedImage.TYPE_INT_RGB);
        bg = (Graphics2D) bufferedImage.getGraphics();
        bg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        bg.setFont(new Font("Arial", Font.PLAIN, 9));
        splashFrame = new JWindow() {

            @Override
            public void paint(Graphics g) {
                bg.drawImage(splashImg, 0, 0, this);
                bg.setColor(new Color(0, 0, 0, 50));
                bg.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

                int x = 27;
                int y = 105;
                int width = ((splashWidth - (x * 2)) * percentBar) / 100;
                int height = 7;

                if (percentBar == 100) {
                    loadingMessage = LanguageFactory.getText(LanguageFactory.SPLASH_LOADING_DONE);
                }

                bg.setColor(new Color(150, 150, 150));
                bg.drawString(loadingMessage, x, 205);

                if (percentBar < 0) {
                    percentBar = 0;
                }
                if (percentBar > 100) {
                    percentBar = 100;
                }


                bg.setColor(new Color(210, 210, 210));
                bg.fillRoundRect(x, y, getWidth() - (x * 2), height, 8, 8);
                bg.setColor(Color.white);
                bg.fillRoundRect(x + 1, y + 1, (getWidth() - (x * 2)) - 2, height - 2, 7, 7);
                bg.setColor(new Color(255, 0, 0, 40));
                bg.fillRoundRect(x + 2, y + 2, width - 4, height - 4, 6, 6);
                g.drawImage(bufferedImage, 0, 0, this);
                graphicsInit = true;
            }
        };

        Dimension splashDimension = new Dimension(splashWidth, splashHeight);
        splashFrame.setSize(splashDimension);
        splashFrame.setPreferredSize(splashDimension);
        splashFrame.setLocationRelativeTo(null);
    }

    @Override
    public void run() {
        splashFrame.setVisible(true);
    }

    public void close() {
        splashFrame.setVisible(false);
        this.interrupt();
    }

    public void setLoadingText(String param) {
        loadingMessage = LanguageFactory.getText(LanguageFactory.SPLASH_LOADING) + param;
        splashFrame.repaint();
    }

    public void setPercentBar(int param) {
        percentBar = param;
        splashFrame.repaint();
    }

    public boolean isGraphicInit() {
        return graphicsInit;
    }

    public int getPercent() {
        return percentBar;
    }
}
