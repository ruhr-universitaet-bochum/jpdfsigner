/*
 * JPDFSigner - Sign PDFs online using smartcards (SignatureValidPanel.java)
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

import com.itextpdf.text.pdf.PdfReader;
import de.rub.dez6a3.jpdfsigner.control.Configurator;
import de.rub.dez6a3.jpdfsigner.control.GlobalData;
import de.rub.dez6a3.jpdfsigner.control.ITextPDFSignatureVerifier;
import de.rub.dez6a3.jpdfsigner.control.ParamValidator;
import de.rub.dez6a3.jpdfsigner.exceptiontypes.VerifySignatureException;
import de.rub.dez6a3.jpdfsigner.control.language.LanguageFactory;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;

/**
 *
 * @author dan
 */
public class SignatureValidPanel extends JPanel {

    private ImageIcon validPdfIco = new ImageIcon(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/validPdf.png")).getScaledInstance(16, 16, Image.SCALE_SMOOTH));
    private ImageIcon invalidPdfIco = new ImageIcon(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/invalidPdf.png")).getScaledInstance(16, 16, Image.SCALE_SMOOTH));
    private ImageIcon unknownPdfIco = new ImageIcon(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/unknownPdf.png")).getScaledInstance(16, 16, Image.SCALE_SMOOTH));
    private ImageIcon[] icon = new ImageIcon[2];
    private JButton parentBtn = null;
    private int trustCode = -2; //0=valid, -1=invalid, -2=no pdf to verify
    private ITextPDFSignatureVerifier verifyer = null;
    private Color hlColor = new Color(255, 255, 255, 160);
    private Color validTxtColor = new Color(0, 77, 5);
    private Color invalidTxtColor = new Color(137, 7, 0);
    private Area area1 = null;
    private Area area2 = null;
    private Area areaCombined = null;
    private BufferedImage bi = null;
    private Graphics2D big2 = null;
    private JLabel icoLbl = new JLabel();
    private JLabel headline = new JLabel();
    private JLabel issuedByDesc = new JLabel(LanguageFactory.getText(LanguageFactory.SIGNATUREVALIDPANEL_PDF_SIGNED_BY));
    private JLabel issuedForDesc = new JLabel(LanguageFactory.getText(LanguageFactory.SIGNATUREVALIDPANEL_PDF_SIGNED_FOR));
    private JLabel issuer = new JLabel();
    private JLabel issuedFor = new JLabel();
    private GridBagConstraints c = null;
    private JPanel signaturesPanes = null;
    public static Logger log = Logger.getLogger(SignatureValidPanel.class);

    public SignatureValidPanel(JButton parentBtn) {
        //Benötigt kein languagefactory register weil ein globales repaint stattfindet und dadurch die languagefactory
        //direkt in dieser paintmethode ausgelesen wird
        this.parentBtn = parentBtn;
        verifyer = ITextPDFSignatureVerifier.getInstance();
        icon[0] = new ImageIcon(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/validDoc.png")));
        icon[1] = new ImageIcon(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/invalidDoc.png")));

        setupComponents();
        setupLanguageFactory();
    }

    private void setupLanguageFactory() {
        LanguageFactory.registerComponent(LanguageFactory.SIGNATUREVALIDPANEL_PDF_SIGNED_BY, issuedByDesc);
        LanguageFactory.registerComponent(LanguageFactory.SIGNATUREVALIDPANEL_PDF_SIGNED_FOR, issuedForDesc);
    }

    private void setupComponents() {
        setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));

        Font font = new Font("Arial", Font.BOLD, 12);

        setLayout(new GridBagLayout());
        c = new GridBagConstraints();

        c.insets = new Insets(3, 3, 3, 3);
        c.anchor = GridBagConstraints.PAGE_START;
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 3;
        add(icoLbl, c);

        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 3;
        c.gridheight = 1;
        headline.setFont(font);
        add(headline, c);

        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.insets = new Insets(10, 8, 0, 3);
        issuedByDesc.setFont(font.deriveFont(10f));
        add(issuedByDesc, c);

        c.gridx = 3;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        issuedForDesc.setFont(font.deriveFont(10f));
        add(issuedForDesc, c);

        c.gridx = 1;
        c.gridy = 2;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.insets = new Insets(2, 8, 0, 3);
        issuer.setFont(font.deriveFont(11f));
        issuer.setFont(font.deriveFont(Font.PLAIN));
        add(issuer, c);

        c.gridx = 3;
        c.gridy = 2;
        c.gridwidth = 1;
        c.gridheight = 1;
        issuedFor.setFont(font.deriveFont(10f));
        issuedFor.setFont(font.deriveFont(Font.PLAIN));
        add(issuedFor, c);

        c.gridx = 2;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 2;
        c.insets = new Insets(10, 7, 8, 7);
        c.fill = GridBagConstraints.BOTH;
        add(new JPanel() {

            private Color lineShadow = new Color(0, 0, 0, 100);
            private Color lineHL = new Color(255, 255, 255, 160);

            @Override
            public void paint(Graphics g) {
                g.setColor(lineShadow);
                g.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());
                g.setColor(lineHL);
                g.drawLine((getWidth() / 2) + 1, 0, (getWidth() / 2) + 1, getHeight());
            }
        }, c);




        c.gridy = 3;
        c.gridx = 0;
        c.gridwidth = 4;
        c.gridheight = 1;
        JButton l = new JButton("Alle Zertifikate anzeigen");
        l.setMargin(new Insets(-2,-5,-2,-5));
        l.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                if (signaturesPanes != null) {
                    Dimension mainDim = Configurator.getInstance().getPdfViewer().getSize();
                    if (signaturesPanes.isVisible()) {
                        signaturesPanes.setVisible(false);
                    } else {
                        signaturesPanes.setPreferredSize(new Dimension(signaturesPanes.getSize().width, mainDim.height - 250));
                        signaturesPanes.setVisible(true);
                    }
                    JPopupMenu popupm = (JPopupMenu) getParent();
                    popupm.setVisible(false); //updates size
                    popupm.setVisible(true);
                }
            }
        });
        add(l, c);
    }

    public void setSignaturesPanel(JPanel signaturesPanel) {
        signaturesPanel.setVisible(false);
        this.signaturesPanes = signaturesPanel;
        c.gridy = 4;
        add(signaturesPanes, c);
    }

    public void updateVerfificationText() {  //made for the languagefactory... it just needs to call this method and the textlanguage will be updated
        switch (trustCode) {
            case 0:
                parentBtn.setText(LanguageFactory.getText(LanguageFactory.TOOLBARPOPDOWNBUTTON_DOCUMENT_VALID));
                headline.setText(LanguageFactory.getText(LanguageFactory.SIGNATUREVALIDPANEL_VALID_PDF_SIG));
                break;
            case -1:
                parentBtn.setText(LanguageFactory.getText(LanguageFactory.TOOLBARPOPDOWNBUTTON_DOCUMENT_INVALID));
                headline.setText(LanguageFactory.getText(LanguageFactory.SIGNATUREVALIDPANEL_INVALID_PDF_SIG));

                break;
            case -2:
                parentBtn.setText(LanguageFactory.getText(LanguageFactory.TOOLBARPOPDOWNBUTTON_DOCUMENT_UNKNOWN));
                headline.setText(LanguageFactory.getText(LanguageFactory.SIGNATUREVALIDPANEL_INVALID_PDF_SIG));
                break;
        }
        if (verifyer.getIssuerName() == null) {
            issuer.setText(LanguageFactory.getText(LanguageFactory.SIGNATUREVALIDPANEL_UNKNOWN_USER));
        }
        if (verifyer.getIssuerName() == null) {
            issuedFor.setText(LanguageFactory.getText(LanguageFactory.SIGNATUREVALIDPANEL_UNKNOWN_USER));
        }
    }

    public void verifySignature() {
        log.info("Verifying PDF signatures ...");
        try {
            verifyer.verifySignature(new PdfReader(Configurator.getInstance().getPDFFile()), ParamValidator.getInstance().getTrustStore());
            trustCode = 0;
            parentBtn.setEnabled(true);
            parentBtn.setText(LanguageFactory.getText(LanguageFactory.TOOLBARPOPDOWNBUTTON_DOCUMENT_VALID));
            headline.setText(LanguageFactory.getText(LanguageFactory.SIGNATUREVALIDPANEL_VALID_PDF_SIG));
            icoLbl.setIcon(icon[0]);
            this.parentBtn.setIcon(validPdfIco);
            this.parentBtn.setDisabledIcon(validPdfIco);
        } catch (VerifySignatureException ex) {
            trustCode = -1;
            parentBtn.setEnabled(true);
            parentBtn.setText(LanguageFactory.getText(LanguageFactory.TOOLBARPOPDOWNBUTTON_DOCUMENT_INVALID));
            headline.setText(LanguageFactory.getText(LanguageFactory.SIGNATUREVALIDPANEL_INVALID_PDF_SIG));
            icoLbl.setIcon(icon[1]);
            this.parentBtn.setIcon(invalidPdfIco);
            this.parentBtn.setDisabledIcon(invalidPdfIco);
            log.warn("Cannot verify PDF-Signatures against truststore: " + ex.getMessage());
        } catch (Exception ex) {
            trustCode = -2;
            parentBtn.setEnabled(false);
            parentBtn.setText(LanguageFactory.getText(LanguageFactory.TOOLBARPOPDOWNBUTTON_DOCUMENT_UNKNOWN));
            headline.setText(LanguageFactory.getText(LanguageFactory.SIGNATUREVALIDPANEL_INVALID_PDF_SIG));
            icoLbl.setIcon(icon[1]);
            this.parentBtn.setIcon(unknownPdfIco);
            this.parentBtn.setDisabledIcon(unknownPdfIco);
            log.error("Cannot verify PDF-Signature due to a technical exception: " + ex.getMessage());
        }
        Configurator.getInstance().getSignatureView().updateTreeNodes();
        log.info("PDF verification done");

        setFont(new Font("Arial", Font.BOLD, 11));
        setForeground(new Color(60, 60, 60));
        setBackground(new Color(0, 0, 0, 0));
        setOpaque(false);

        if (verifyer.getIssuerName() != null) {
            issuer.setText(verifyer.getIssuerName());
        } else {
            issuer.setText(LanguageFactory.getText(LanguageFactory.SIGNATUREVALIDPANEL_UNKNOWN_USER));
        }
        if (verifyer.getSignedForName() != null) {
            issuedFor.setText(verifyer.getSignedForName());
        } else {
            issuedFor.setText(LanguageFactory.getText(LanguageFactory.SIGNATUREVALIDPANEL_UNKNOWN_USER));
        }

    }

    @Override
    public void paint(Graphics g) {
        bi = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        big2 = (Graphics2D) bi.getGraphics();
        big2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        big2.setColor(new Color(85, 85, 85));
        area1 = new Area(new RoundRectangle2D.Float(0f, 0f, getWidth(), getHeight(), 15f, 15f));
        area2 = new Area(new Rectangle2D.Float(getWidth() / 2, 0, getWidth(), getHeight() / 3));
        areaCombined = new Area();
        areaCombined.add(area1);
        areaCombined.add(area2);
        big2.fill(areaCombined);
        big2.setColor(new Color(255, 255, 255));
        area1 = new Area(new RoundRectangle2D.Float(1f, 1f, getWidth() - 2, getHeight() - 2, 14f, 14f));
        area2 = new Area(new Rectangle2D.Float(getWidth() - parentBtn.getWidth() + 1, 0, parentBtn.getWidth() - 2, getHeight() / 3));
        areaCombined = new Area();
        areaCombined.add(area1);
        areaCombined.add(area2);
        big2.fill(areaCombined);
        GradientPaint gp = new GradientPaint(0, 2, new Color(245, 245, 245), 0, getHeight() - 4, new Color(215, 215, 215), false);
        big2.setPaint(gp);
        area1 = new Area(new RoundRectangle2D.Float(2f, 2f, getWidth() - 4, getHeight() - 4, 13f, 13f));
        area2 = new Area(new Rectangle2D.Float(getWidth() - parentBtn.getWidth() + 2, 0, parentBtn.getWidth() - 4, getHeight() / 3));
        areaCombined = new Area();
        areaCombined.add(area1);
        areaCombined.add(area2);
        big2.fill(areaCombined);
        g.drawImage(bi, 0, 0, this);
        super.paint(g);
    }
}
