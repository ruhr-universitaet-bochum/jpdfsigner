/*
 * JPDFSigner - Sign PDFs online using smartcards (AboutDialog.java)
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

import de.rub.dez6a3.jpdfsigner.control.AWTUtilitiesValidator;
import de.rub.dez6a3.jpdfsigner.control.FontFactory;
import de.rub.dez6a3.jpdfsigner.control.WindowTools;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URI;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

/**
 *
 * @author daniel
 */
public class AboutDialog extends JDialog {

    private JDialog licenesDiag = null;
    private String license = "";

    public AboutDialog(Frame owner) throws FileNotFoundException, IOException {
        super(owner, "Über ...", true);
        setUndecorated(true);
        setPreferredSize(new Dimension(380, 290));
        loadLicense();
        initLicenseDialog();
        init();
        setLocation(WindowTools.getCenterPoint(380, 290, 0));
    }

    private void loadLicense() throws FileNotFoundException, IOException {
        InputStreamReader inReader = new InputStreamReader(getClass().getResourceAsStream("/de/rub/dez6a3/jpdfsigner/LICENSE.txt"));
        BufferedReader reader = new BufferedReader(inReader);
        String line = "";
        license = "";
        while ((line = reader.readLine()) != null) {
            license += line + "\n";
        }
    }

    private void initLicenseDialog() {
        licenesDiag = new JDialog(this, "Lizenz", true) {
            @Override
            public void paint(Graphics g) {
                if (AWTUtilitiesValidator.getInstance().isAWTUtlitiesAvailable()) {
                    Area shape = new Area();
                    Area shapeTop = new Area(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 13, 13));
                    shape.add(shapeTop);
                    AWTUtilitiesValidator.getInstance().setWindowShape(this, shape);
                }
                super.paint(g);
            }
        };
        licenesDiag.setLayout(new BorderLayout());
        licenesDiag.setPreferredSize(new Dimension(570, 400));
        licenesDiag.setUndecorated(true);
        licenesDiag.setLocation(WindowTools.getCenterPoint(570, 400, 0));

        JTextArea textField = new JTextArea();
        textField.setLineWrap(true);
        textField.setWrapStyleWord(true);
        textField.setText(license);
        textField.setEditable(false);

        JScrollPane scroller = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroller.setViewportView(textField);
        scroller.setBorder(null);

        JButton closeBtn = new JButton("Schließen");
        closeBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                licenesDiag.dispose();
            }
        });
        licenesDiag.add(scroller, BorderLayout.CENTER);
        licenesDiag.add(closeBtn, BorderLayout.SOUTH);
    }

    private void init() {

        JLabel logoLbl = new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/jpdfsignerlogo.png"))));
        String contentStr = ""
                + "<html>"
                + "<table width=\"380\">"
                + "<tr><td align=\"center\">"
                + "<font size=\"3\">"
                + "<br />"
                + "<br />"
                + "<b>Signieren von PDFs in Webanwendungen per Smartcard</b>"
                + "</font>"
                + "<br />"
                + "<br />"
                + "<font size=\"2\">"
                + "Copyright &copy; 2013"
                + "<br />"
                + "Ruhr-Universität Bochum"
                + "<br />"
                + "Dezernat 6 Abteilung 3"
                + "<br />"
                + "Daniel Moczarski, Haiko te Neues"
                + "<br />"
                + "<br />"
                + "http://www.ruhr-uni-bochum.de/dezernat6/abteilung3"
                + "<br />"
                + "<br />"
                + "<b>Veröffentlicht unter der GNU GPL v.3</b>"
                + "</font>"
                + "</td></tr>"
                + "</table>"
                + "</html>"
                + "";
        JLabel contentLbl = new JLabel(contentStr);
        contentLbl.setFont(FontFactory.getMainFont());
        contentLbl.setVerticalAlignment(SwingConstants.TOP);

        JButton licenseBtn = new JButton("Lizenz");
        JButton websiteBtn = new JButton("Website");
        JButton closeBtn = new JButton("Schließen");

        licenseBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                licenesDiag.pack();
                licenesDiag.setVisible(true);
            }
        });

        websiteBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
                if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                    try {
                        desktop.browse(new URI("http://www.ruhr-uni-bochum.de/dezernat6/abteilung3"));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        closeBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                dispose();
            }
        });

        JPanel btnPanel = new JPanel();
        btnPanel.add(licenseBtn);
        btnPanel.add(websiteBtn);
        btnPanel.add(closeBtn);

        add(logoLbl, BorderLayout.NORTH);
        add(contentLbl, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }

    public void paint(Graphics g) {
        if (AWTUtilitiesValidator.getInstance().isAWTUtlitiesAvailable()) {
            Area shape = new Area();
            Area shapeTop = new Area(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 13, 13));
            shape.add(shapeTop);
            AWTUtilitiesValidator.getInstance().setWindowShape(this, shape);
        }
        super.paint(g);
    }
}
