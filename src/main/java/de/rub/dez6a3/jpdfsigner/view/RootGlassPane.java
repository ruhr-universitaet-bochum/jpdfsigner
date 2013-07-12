/*
 * JPDFSigner - Sign PDFs online using smartcards (RootGlassPane.java)
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

import de.rub.dez6a3.jpdfsigner.view.UI.LAFProperties;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.apache.log4j.Logger;

/**
 *
 * @author dan
 */
public class RootGlassPane extends JPanel {

    Point origPointOnScreen = null;
    Dimension origPanelDim = null;
    GUIPDFView parent = null;
    private String infoText = "";
    private String loadingText = "";
    private LoadingView loading = null;
    private int screenStatus = 0;
    private JPanel lockPanel = null;
    public static int LOCKED = 1;
    public static int UNLOCKED = 0;

    public static Logger log = Logger.getLogger(RootGlassPane.class);

    public RootGlassPane(final JFrame parent) {
        this.parent = (GUIPDFView) parent;

        screenStatus = RootGlassPane.UNLOCKED;

        setOpaque(false);
        setLayout(new BorderLayout());

        createLoadingView();
        createLockView();

        add(lockPanel, BorderLayout.CENTER);

        setViewTo(screenStatus);
    }

    public void setAnimationRunning(boolean param){
        if(param){
            loading.start();
        }else{
            loading.stop();
        }
    }

    public void setViewTo(int param) {
        screenStatus = param;
        if (param == RootGlassPane.LOCKED) {
            screenStatus = param;
            loading.start();
            lockPanel.setVisible(true);
            setVisible(true);
        } else if (param == RootGlassPane.UNLOCKED) {
            screenStatus = param;
            loading.stop();
            setVisible(false);
            lockPanel.setVisible(false);
        }
    }
    

    public void createLockView() {
        lockPanel = new JPanel() {

            @Override
            public void paint(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(30, 30, 30, 220));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255, 255, 255, 65));
                g2.drawLine(0, 1, getWidth(), 1);
                g2.drawLine(0, 2, 0, getHeight()-2);
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
                g2.drawLine(getWidth()-1, 2, getWidth()-1, getHeight()-2);
                
                Polygon poly = new Polygon();
                poly.addPoint(0, 0);
                poly.addPoint(getWidth(), 0);
                poly.addPoint(getWidth(), getHeight() / 12);
                poly.addPoint(0, getHeight() - (getWidth() / 6));

                Polygon poly2 = new Polygon();
                poly2.addPoint(getWidth(), getHeight());
                poly2.addPoint(getWidth(), getHeight() / 12);
                poly2.addPoint(0, getHeight() - (getWidth() / 6));
                poly2.addPoint(0, getHeight());

                

                super.paint(g2);

                g2.setFont(LAFProperties.getInstance().getMainFontBold().deriveFont(Font.PLAIN, 14));
                int waitTextX = (getWidth() / 2) - (g2.getFontMetrics().stringWidth(loadingText) / 2);
                int waitTextY = (getHeight() / 2) + (loading.getHeight() / 2) + 18;
                g2.setColor(new Color(0, 0, 0));
                g2.drawString(loadingText, waitTextX + 1, waitTextY + 1);
                g2.setColor(new Color(230, 230, 230));
                g2.drawString(loadingText, waitTextX, waitTextY);

                g2.setFont(LAFProperties.getInstance().getMainFontRegular().deriveFont(Font.ITALIC, 11));
                int textX = (getWidth() / 2) - (g2.getFontMetrics().stringWidth(infoText) / 2);
                int textY = (getHeight() / 2) + (loading.getHeight() / 2) + 42;
                g2.setColor(new Color(213, 229, 158));
                g2.drawString(infoText, textX, textY);
            }
        };

        lockPanel.setOpaque(false);
        lockPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(1, 1, 1, 1);
        JPanel p = new JPanel();
        p.setBackground(Color.red);
        Dimension d = new Dimension(500,500);
        lockPanel.add(loading);

        lockPanel.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
                log.info("User clicked on panel: Panel is locked");
            }
        });
    }

    public void createLoadingView() {
        loading = new LoadingView();
        int animSize = parent.getWidth()/12;

        Dimension loadingDim = new Dimension(animSize, animSize);
        loading.setPreferredSize(loadingDim);
        loading.setSize(loadingDim);
    }

    /**
     * Sets info text below the loading-text.
     * @param param
     */
    public void setInfoText(String param) {
        infoText = param;
        repaint();
    }

    /**
     * Sets the big headline in the loadingview.
     * @param param
     */
    public void setLoadingText(String param){
        loadingText = param;
        repaint();
    }
}
