/*
 * JPDFSigner - Sign PDFs online using smartcards (SignHintAnimationPanel.java)
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

import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import org.apache.log4j.Logger;

/**
 *
 * @author dan
 */
public class SignHintAnimationPanel extends JPanel implements Runnable {

    private int speed = 500;

    private Thread thread = null;
    private boolean running = false;
    private boolean usable = false;
    private List<ImageIcon> frames = null;
    private ImageIcon currFrame = null;
    Logger log = Logger.getLogger(SignHintAnimationPanel.class);

    public SignHintAnimationPanel() throws Exception {
        frames = new ArrayList<ImageIcon>();
        setOpaque(false);
        try {
            frames.add(new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/stampScreens/1.png"))));
            frames.add(new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/stampScreens/2.png"))));
            frames.add(new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/stampScreens/3.png"))));
            frames.add(new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/stampScreens/4.png"))));
            currFrame = frames.get(0);
            usable = true;
        } catch (Exception e) {
            throw e;
        }

        addAncestorListener(new AncestorListener() {

            public void ancestorAdded(AncestorEvent event) {
                try {
                    start();
                } catch (Exception e) {
                    log.error(e);
                }
            }

            public void ancestorRemoved(AncestorEvent event) {
                try {
                    stop();
                } catch (Exception e) {
                    log.error(e);
                }
            }

            public void ancestorMoved(AncestorEvent event) {
//                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
    }

    public void run() {
        running = true;
        int i = 0;
        while (running) {
            if (i > 3) {
                i = 0;
            }
            currFrame = frames.get(i);
            repaint();
            i++;
            try {
                Thread.sleep(speed);
            } catch (InterruptedException ex) {
                log.error(ex);
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.drawImage(currFrame.getImage(), (getWidth() / 2) - (currFrame.getIconWidth() / 2), 0, this);
    }

    public void start() throws Exception {
        if (!running && usable) {
            thread = new Thread(this);
            thread.start();
        } else {
            throw new Exception("Not all images are in the right package. Sign-hint-panel Can not be shown.");
        }
    }

    public void stop() {
        running = false;
    }
}
