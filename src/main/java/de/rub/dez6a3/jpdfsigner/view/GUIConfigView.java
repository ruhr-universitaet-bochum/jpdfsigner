/*
 * JPDFSigner - Sign PDFs online using smartcards (GUIConfigView.java)
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

import de.rub.dez6a3.jpdfsigner.control.ActionListenerEvents;
import de.rub.dez6a3.jpdfsigner.control.CardReader;
import de.rub.dez6a3.jpdfsigner.control.Configurator;
import de.rub.dez6a3.jpdfsigner.exceptiontypes.CardAccessorException;
import de.rub.dez6a3.jpdfsigner.control.language.LanguageFactory;
import de.rub.dez6a3.jpdfsigner.model.ICardAccessor;
import de.rub.dez6a3.jpdfsigner.view.UI.CustomLabelUI;
import de.rub.dez6a3.jpdfsigner.view.UI.LAFProperties;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.logging.Level;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.AbstractBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import org.apache.log4j.Logger;

/**
 *
 * @author dan
 */
public class GUIConfigView extends JPanel {

    private JTabbedPane tabPane;
    private ICardAccessor cardHandler;
    private Configurator config;
    private JComboBox readersFoundComboBox = null;
    private JTextField libPathTextField;
    private ArrayList<CardReader> readers;
    public static Logger log = Logger.getLogger(GUIConfigView.class);

    public GUIConfigView(Frame frame, boolean modal) {
        config = Configurator.getInstance();
        setBorder(new AbstractBorder() {

            private Insets insets = new Insets(0, 0, 2, 0);

            @Override
            public Insets getBorderInsets(Component c) {
                return insets;
            }

            @Override
            public Insets getBorderInsets(Component c, Insets insets) {
                return insets;
            }

            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                g.setColor(new Color(0,0,0,100));
                g.drawLine(0, c.getHeight()-1, c.getWidth(), c.getHeight()-1);
                g.setColor(new Color(255,255,255,20));
                g.drawLine(0, c.getHeight()-2, c.getWidth(), c.getHeight()-2);
            }
        });
        setBackground(LAFProperties.getInstance().getMainFrameBackgroundColor());
        try {
            config = Configurator.getInstance();
            cardHandler = config.getCardHandler();
            initComponents();
            initActionComponents();
            readersFoundComboBox.addItem(config.getCardHandler().getSelectedReader());
        } catch (NullPointerException ex) {
            log.warn(ex);
        } catch (Exception ex) {
            log.error(ex);
            DecoratedJOptionPane.showMessageDialog(this, LanguageFactory.getText(LanguageFactory.GUICONFIGVIEW_ERROR_ON_EXEC), LanguageFactory.getText(LanguageFactory.ERROR_HL), JOptionPane.ERROR_MESSAGE);
            Configurator.closeProgram("-941");
        }
    }

    public void setBibPathText(String param) {
        libPathTextField.setText(param);
    }
    int rnd = 0;

    private void initComponents() {
        setLayout(new BorderLayout());
        tabPane = new CloseBtnTabbedPane(this);
        JPanel readerPanel = new JPanel();
        readerPanel.setBackground(LAFProperties.getInstance().getPdfBackGroundColor());
        JPanel libPanel = new JPanel();
        libPanel.setBackground(LAFProperties.getInstance().getPdfBackGroundColor());
        readerPanel.setLayout(new GridBagLayout());
        libPanel.setLayout(new GridBagLayout());
        GridBagConstraints gBC = new GridBagConstraints();
        gBC.fill = GridBagConstraints.HORIZONTAL;

        JLabel readerInfoLabel = new JLabel(LanguageFactory.getText(LanguageFactory.GUICONFIGVIEW_CHOOSEREADER_JLABEL));
        LanguageFactory.registerComponent(LanguageFactory.GUICONFIGVIEW_CHOOSEREADER_JLABEL, readerInfoLabel);
        Font labelfont = new Font("Arial", Font.PLAIN, 11);
        readerInfoLabel.setFont(labelfont);
        readerInfoLabel.setForeground(new Color(230, 230, 230));
        readerInfoLabel.setUI(new CustomLabelUI());
        //Ladeanimation-Informationen in ComboBox
        final Color path = new Color(195, 195, 195);
        final Color blur = new Color(0, 0, 0, 10);
        final Color pin = new Color(80, 80, 80);
        final Color middleHL = Color.white;
        final Color middle = new Color(235, 235, 235);
        final Dimension rfcDim = new Dimension(1, 21);
        readersFoundComboBox = new JComboBox() {

            @Override
            public void paint(Graphics g) {
                super.paint(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (loading) {
                    if (rnd >= 360) {
                        rnd = 0;
                    }
                    int x = getWidth() - 17;
                    int y = (getHeight() / 2) - 6;
                    g2.setColor(path);
                    g2.fillOval(x, y, 14, 14);
                    g2.setColor(blur);
                    for (int i = 60; i > 0; i -= 3) {
                        g2.fillArc(x, y, 14, 14, rnd - i, 45);
                    }
                    g2.setColor(pin);
                    g2.fillArc(x, y, 14, 14, rnd, 45);
                    g2.setColor(middleHL);
                    g2.fillOval(x + 3, y + 3, 8, 8);
                    g2.setColor(middle);
                    g2.fillOval(x + 4, y + 4, 7, 7);
                    rnd += 12;
                }
            }
        };

        readersFoundComboBox.setPreferredSize(rfcDim);
        readersFoundComboBox.setSize(rfcDim);
//        readersFoundComboBox = new JComboBox();
        readersFoundComboBox.setRenderer(new ReadersComboBoxListCellRenderer());

        readersFoundComboBox.setEditable(false);
        gBC.insets = new Insets(10, 8, 0, 8);
        gBC.anchor = GridBagConstraints.PAGE_START;
        gBC.weightx = 1;
        gBC.weighty = 0;
        gBC.gridx = 0;
        gBC.gridy = 0;
        readerPanel.add(readerInfoLabel, gBC);
        gBC.insets = new Insets(0, 8, 10, 8);
        gBC.anchor = GridBagConstraints.CENTER;
        gBC.weightx = 1;
        gBC.weighty = 1;
        gBC.gridx = 0;
        gBC.gridy = 1;
        readerPanel.add(readersFoundComboBox, gBC);


        JLabel libInfoLabel = new JLabel(LanguageFactory.getText(LanguageFactory.GUICONFIGVIEW_CHOOSEBIBPATH_JLABEL));
        LanguageFactory.registerComponent(LanguageFactory.GUICONFIGVIEW_CHOOSEBIBPATH_JLABEL, libInfoLabel);
        libInfoLabel.setFont(labelfont);
        libInfoLabel.setForeground(new Color(230, 230, 230));
        libInfoLabel.setUI(new CustomLabelUI());
        libPathTextField = new JTextField();
        JButton searchBtn = new JButton(new ActionListenerEvents(LanguageFactory.getText(LanguageFactory.GUICONFIGVIEW_BIB_SEARCH_JBUTTON), null, null, 'S', "config_search_btn", this));
        LanguageFactory.registerComponent(LanguageFactory.GUICONFIGVIEW_BIB_SEARCH_JBUTTON, searchBtn);
        libPathTextField.setEditable(false);

        gBC.insets = new Insets(10, 8, 0, 8);
        gBC.weightx = 1;
        gBC.weighty = 0;
        gBC.gridx = 0;
        gBC.gridy = 0;
        libPanel.add(libInfoLabel, gBC);

        gBC.insets = new Insets(0, 8, 10, 8);
        gBC.weightx = 1;
        gBC.weighty = 1;
        gBC.gridx = 0;
        gBC.gridy = 1;
        libPanel.add(libPathTextField, gBC);

        gBC.weightx = 0;
        gBC.weighty = 1;
        gBC.insets = new Insets(0, 0, 10, 8);
        gBC.gridx = 1;
        gBC.gridy = 1;
        libPanel.add(searchBtn, gBC);

        String hl0 = "UNDEFINED";
        String hl1 = "UNDEFINED";
        try {
            hl0 = LanguageFactory.getText(LanguageFactory.GUICONFIGVIEW_CARDREADER_TAB_JTABPANE).split("=")[1];
        } catch (Exception e) {
            log.warn("JTabbedPane headline is not valid. Must be in following format: tabIndex=tabHeadline (e.g.: 0=Cardreaders)");
        }
        try {
            hl1 = LanguageFactory.getText(LanguageFactory.GUICONFIGVIEW_BIBS_TAB_JTABPANE).split("=")[1];
        } catch (Exception e) {
            log.warn("JTabbedPane headline is not valid. Must be in following format: tabIndex=tabHeadline (e.g.: 0=Cardreaders)");
        }
        tabPane.add(hl0, readerPanel);
        tabPane.add(hl1, libPanel);
        LanguageFactory.registerComponent(LanguageFactory.GUICONFIGVIEW_CARDREADER_TAB_JTABPANE, tabPane);
        LanguageFactory.registerComponent(LanguageFactory.GUICONFIGVIEW_BIBS_TAB_JTABPANE, tabPane);

        add(tabPane);
    }
    boolean loading = false;

    private void removeMouseListeners(JComponent c) {

        Component[] c2 = c.getComponents();
        for (Component curr : c2) {
            for (MouseListener ml : curr.getMouseListeners()) {
                curr.removeMouseListener(ml);
            }
            if (curr instanceof JComponent) {
                removeMouseListeners((JComponent) curr);
            }
        }
    }

    private void initActionComponents() {
        removeMouseListeners(readersFoundComboBox);
        readersFoundComboBox.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (readersFoundComboBox.isPopupVisible()) {
                    readersFoundComboBox.hidePopup();
                } else if (!loading) {
                    loading = true;
                    readersFoundComboBox.removeAllItems();
                    readersFoundComboBox.setForeground(new Color(6, 129, 20));
                    readersFoundComboBox.addItem(LanguageFactory.getText(LanguageFactory.GUICONFIGVIEW_LOADING_CARDREADERS));
                    new Thread() {

                        @Override
                        public void run() {
                            while (loading) {
                                readersFoundComboBox.repaint();
                                try {
                                    Thread.sleep(30);
                                } catch (InterruptedException ex) {
                                    java.util.logging.Logger.getLogger(GUIConfigView.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    }.start();
                    new Thread() {

                        @Override
                        public void run() {
                            log.info("Loading sc reader list");
                            actualizeReadersFoundCBox();
                            readersFoundComboBox.showPopup();
                            loading = false;
                            readersFoundComboBox.repaint();
                        }
                    }.start();
                }
            }
        });

        readersFoundComboBox.addPopupMenuListener(new PopupMenuListener() {

            public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
                actualizeReadersFoundCBox();
            }

            public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {
                if (((JComboBox) arg0.getSource()).getSelectedItem() instanceof CardReader) {
                    CardReader choosenReader = (CardReader) ((JComboBox) arg0.getSource()).getSelectedItem();
                    config.setProperty("screader", choosenReader.getSlotDescription(), true);
                    config.setProperty("slotid", new Long(choosenReader.getSlotID()).toString(), true);
                    config.getCardHandler().setSelectedReader(choosenReader);
                }
                repaint();
            }

            public void popupMenuCanceled(PopupMenuEvent arg0) {
            }
        });
    }

    public void actualizeReadersFoundCBox() {
        try {
            readersFoundComboBox.setForeground(Color.black);
            cardHandler.load();
            readers = cardHandler.getReaders();
            readersFoundComboBox.removeAllItems();
            for (CardReader cardReader : readers) {
                readersFoundComboBox.addItem(cardReader);
            }
            if (readers.size() > 0) {
                tabPane.setSelectedIndex(0);
            } else {
                readersFoundComboBox.addItem(LanguageFactory.getText(LanguageFactory.GUICONFIGVIEW_NO_CARDREADERS_FOUND));
            }
        } catch (Throwable t) {
            readersFoundComboBox.removeAllItems();
            readersFoundComboBox.setForeground(Color.red);
            readersFoundComboBox.addItem(LanguageFactory.getText(LanguageFactory.GUICONFIGVIEW_CP_ERROR));
            log.error(t);
        } finally {
            try {
                cardHandler.unload();
            } catch (CardAccessorException e) {
            }
        }
        try {
        } catch (Throwable t) {
            log.error(t);
        }
    }

    @Override
    public void setVisible(boolean param) {
        try {
            int jo = readersFoundComboBox.getItemCount();
            int selReader = 0;
            if (config.getProperty("screader") != null && readersFoundComboBox.getItemCount() > 0) {
                for (int i = 0; i < readersFoundComboBox.getItemCount(); i++) {
                    CardReader r = (CardReader) readersFoundComboBox.getItemAt(i);
                    String desc = r.getSlotDescription();
                    String cdesc = config.getProperty("screader");
//                    if (((CardReader) readersFoundComboBox.getItemAt(i)).getSlotDescription().equals((String) config.getProperty("screader"))) {
                    if (desc.equals(cdesc)) {
                        selReader = i;
                    }
                }
                readersFoundComboBox.setSelectedIndex(selReader);
            }
            if (config.getProperty("libpath") != null) {
                libPathTextField.setText(config.getProperty("libpath"));
            }
            super.setVisible(param);
        } catch (Exception ex) {
            log.fatal(ex);
            //-105
            DecoratedJOptionPane.showMessageDialog(this, LanguageFactory.getText(LanguageFactory.GUICONFIGVIEW_ERROR_CREATING_GUI), LanguageFactory.getText(LanguageFactory.ERROR_HL), JOptionPane.ERROR_MESSAGE);
        }
    }

    class CloseBtnTabbedPane extends JTabbedPane {

        private JPanel parent = null;
        private int cBtnSizeX = 15;
        private int cBtnSizeY = 15;
        private int spacerX = 9;
        private int spacerY = 5;
        private boolean mouseOver = false;
        private boolean mouseDown = false;

        public CloseBtnTabbedPane(JPanel p) {
            this.parent = p;
            addMouseListener(new MouseAdapter() {

                public void mousePressed(MouseEvent e) {
                    if (e.getX() >= getWidth() - cBtnSizeX - spacerX && e.getX() <= getWidth() - spacerX && e.getY() >= spacerY && e.getY() <= spacerY + cBtnSizeY) {
                        mouseDown = true;
                        repaint();
                    }
                }

                public void mouseReleased(MouseEvent e) {
                    if (e.getX() >= getWidth() - cBtnSizeX - spacerX && e.getX() <= getWidth() - spacerX && e.getY() >= spacerY && e.getY() <= spacerY + cBtnSizeY) {
                        parent.setVisible(false);
                        if (config.haveValuesChanged()) {
                            try {
                                if (config.getProperty("slotid") == null || config.getProperty("screader") == null) {
                                    CardReader reader = (CardReader) readersFoundComboBox.getSelectedItem();
                                    config.setProperty("slotid", new Long(reader.getSlotID()).toString(), true);
                                    config.setProperty("screader", reader.getSlotDescription(), true);
                                }
                            } catch (Exception ex) {
                            }
                            config.savePropertiesToFile(true);
                        }
                    }
                    mouseDown = false;
                    repaint();
                }
            });
//            addMouseMotionListener(new MouseMotionAdapter() {         //Mouseover effekt für den X-Button (erzeugt für den kompletten Config bereich ein repaint - unperformant(nicht nutzen!))
//
//                @Override
//                public void mouseMoved(MouseEvent e) {
//
//                    if (e.getX() >= getWidth() - cBtnSizeX - spacerX && e.getX() <= getWidth() - spacerX && e.getY() >= spacerY && e.getY() <= spacerY + cBtnSizeY) {
//                        mouseOver = true;
//                        repaint();
//                    } else {
//                        mouseOver = false;
//                        repaint();
//                    }
//                }
//            });
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            int x = getWidth() - cBtnSizeX - spacerX;
            int y = spacerY;
            int w = cBtnSizeX;
            int h = cBtnSizeY;

            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(255, 255, 255, 35));
            g2.fillRoundRect(x, y, w, h, 6, 6);

            g2.setColor(new Color(60, 60, 60));
            g2.fillRoundRect(x + 1, y + 1, w - 2, h - 2, 5, 5);

            GradientPaint gp = null;
            if (mouseDown) {
                if (config.haveValuesChanged()) {
                    gp = new GradientPaint(x + 1, y + 1, new Color(150, 0, 0), x + w + 1, y + h + 1, new Color(210, 65, 65), false);
                } else {
                    gp = new GradientPaint(x + 1, y + 1, new Color(85, 85, 85), x + w + 1, y + h + 1, new Color(180, 180, 180), false);
                }
            } else if (mouseOver) {
                if (config.haveValuesChanged()) {
                    gp = new GradientPaint(x + 1, y + 1, new Color(225, 75, 75), x + w + 1, y + h + 1, new Color(185, 0, 0), false);
                } else {
                    gp = new GradientPaint(x + 1, y + 1, new Color(210, 210, 210), x + w + 1, y + h + 1, new Color(120, 120, 120), false);
                }
            } else {
                if (config.haveValuesChanged()) {
                    gp = new GradientPaint(x + 1, y + 1, new Color(210, 65, 65), x + w + 1, y + h + 1, new Color(150, 0, 0), false);
                } else {
                    gp = new GradientPaint(x + 1, y + 1, new Color(195, 195, 195), x + w + 1, y + h + 1, new Color(95, 95, 95), false);
                }
            }
            g2.setPaint(gp);
            g2.fillRoundRect(x + 2, y + 2, w - 4, h - 4, 4, 4);

            g2.setStroke(new BasicStroke(2));
            g2.setColor(new Color(70, 70, 70));
            g2.drawLine(x + 4, y + 4, x + w - 5, y + h - 5);
            g2.drawLine(x + 4, y + h - 5, x + w - 5, y + 4);

            g2.setStroke(new BasicStroke(1));
            g2.setColor(Color.white);
            g2.drawLine(x + 4, y + 4, x + w - 5, y + h - 5);
            g2.drawLine(x + 4, y + h - 5, x + w - 5, y + 4);
        }
    }
}
