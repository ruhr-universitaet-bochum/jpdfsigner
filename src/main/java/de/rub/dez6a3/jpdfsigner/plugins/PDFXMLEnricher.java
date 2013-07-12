/*
 * JPDFSigner - Sign PDFs online using smartcards (PDFXMLEnricher.java)
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
package de.rub.dez6a3.jpdfsigner.plugins;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import de.rub.dez6a3.jpdfsigner.control.Configurator;
import de.rub.dez6a3.jpdfsigner.control.DocURLConnector;
import de.rub.dez6a3.jpdfsigner.control.FileEntity;
import de.rub.dez6a3.jpdfsigner.control.GlobalData;
import de.rub.dez6a3.jpdfsigner.control.MultiDocumentThumb;
import de.rub.dez6a3.jpdfsigner.control.MultiDocumentThumbController;
import de.rub.dez6a3.jpdfsigner.control.ParamValidator;
import de.rub.dez6a3.jpdfsigner.control.language.LanguageFactory;
import de.rub.dez6a3.jpdfsigner.model.MDTControllerListener;
import de.rub.dez6a3.jpdfsigner.view.DecoratedJOptionPane;
import de.rub.dez6a3.jpdfsigner.view.RootGlassPane;
import de.rub.dez6a3.jpdfsigner.view.UI.LAFProperties;
import java.applet.Applet;
import java.applet.AppletContext;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import sun.misc.BASE64Encoder;

/**
 *
 * @author dan
 */
public class PDFXMLEnricher extends JPanel {

    private MultiDocumentThumbController mdtCtrl = null;
    private Color invisibleTxtBG = new Color(0, 0, 0, 0);
    private Color errorTxtBG = new Color(255, 0, 0, 40);
    private String[] paramsToFetch = {"staffcouncil", "authsigner"};
    public static Logger log = Logger.getLogger(PDFXMLEnricher.class);

    public PDFXMLEnricher() {
        setPreferredSize(new Dimension(200, 1));
        setBackground(new Color(0, 0, 0));
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        ImageIcon panelIcon = new ImageIcon(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/label.png"));
        JPanel formPanel = createFormular();

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.anchor = GridBagConstraints.PAGE_START;
        c.insets = new Insets(0, 10, 0, 10);
        c.gridx = 0;
        c.gridy = 0;

        add(new ImagePanel(panelIcon.getImage()), c);

        c.gridy = 1;
        c.weighty = 1.0;
        add(formPanel, c);

        mdtCtrl.addMDTControllerListener(new MDTControllerListener() {

            @Override
            public void newDocumentLoaded(Object e) {
                MultiDocumentThumb displayedMdt = mdtCtrl.getCurrentDisplayedDocumentAsThumb();
                if (displayedMdt != null) {
                    actualizeInputFields(displayedMdt);
                } else {
                    resetInputFields(); //resets everything back to default.
                }
            }
        });
    }

    private boolean actualizeInputFields(MultiDocumentThumb mdt) {
        boolean result = true;
        fnameTxt.setBackground(invisibleTxtBG);
        lnameTxt.setBackground(invisibleTxtBG);
        birthTxt.setBackground(invisibleTxtBG);
        staffCouncilCbo.setBackground(invisibleTxtBG);
        authSignerCbo.setBackground(invisibleTxtBG);
        fnametxtlistener.setCompareStr("");
        lnametxtlistener.setCompareStr("");
        birthtxtlistener.setCompareStr("");
        staffCouncilCbolistener.setCompareStr("");
        authSignerCbolistener.setCompareStr("");

        if (!mdt.getXmlEnrichFName().equals("")) {
            fnametxtlistener.setCompareStr(mdt.getXmlEnrichFName());
            fnameTxt.setText(mdt.getXmlEnrichFName());
        } else {
            fnameTxt.setText("");
            result = false;
        }
        if (!mdt.getXmlEnrichLName().equals("")) {
            lnametxtlistener.setCompareStr(mdt.getXmlEnrichLName());
            lnameTxt.setText(mdt.getXmlEnrichLName());
        } else {
            lnameTxt.setText("");
            result = false;
        }
        if (!mdt.getXmlEnrichBirth().equals("")) {
            birthtxtlistener.setCompareStr(mdt.getXmlEnrichBirth());
            birthTxt.setText(mdt.getXmlEnrichBirth());
        } else {
            birthTxt.setText("");
            result = false;
        }
        if (!mdt.getXmlEnrichPR().equals("")) {
            staffCouncilCbolistener.setCompareStr(mdt.getXmlEnrichPR());
            int count = staffCouncilCbo.getItemCount();
            for (int i = 0; i < count; i++) {
                if (((String) staffCouncilCbo.getItemAt(i)).equals(mdt.getXmlEnrichPR())) {
                    staffCouncilCbo.setSelectedIndex(i);
                    staffCouncilCbo.repaint();
                    break;
                }
            }
        } else {
            staffCouncilCbo.setSelectedIndex(0);
            staffCouncilCbo.repaint();
            result = false;
        }
        if (!mdt.getXmlEnrichSigner().equals("")) {
            authSignerCbolistener.setCompareStr(mdt.getXmlEnrichSigner());
            int count = authSignerCbo.getItemCount();
            boolean containsElement = false;
            for (int i = 0; i < count; i++) {
                if (((String) authSignerCbo.getItemAt(i)).equals(mdt.getXmlEnrichSigner())) {
                    authSignerCbo.setSelectedIndex(i);
                    authSignerCbo.repaint();
                    containsElement = true;
                    authSignerCbo.setVisibleUnknownAuthSign(false);
                    authSignerCbo.setUnknownAuthSign("");
                    break;
                }
            }
            if (!containsElement) {
                authSignerCbo.setVisibleUnknownAuthSign(true);
                authSignerCbo.setUnknownAuthSign(mdt.getXmlEnrichSigner());
            }
        } else {
            authSignerCbo.setVisibleUnknownAuthSign(false);
            authSignerCbo.setUnknownAuthSign("");
            authSignerCbo.setSelectedIndex(0);
            authSignerCbo.repaint();
            result = false;
        }
        return result;
    }
    private TextFieldKeyListener fnametxtlistener = new TextFieldKeyListener();
    private TextFieldKeyListener lnametxtlistener = new TextFieldKeyListener();
    private TextFieldKeyListener birthtxtlistener = new TextFieldKeyListener();
    private ComboBoxItemListener staffCouncilCbolistener = new ComboBoxItemListener();
    private ComboBoxItemListener authSignerCbolistener = new ComboBoxItemListener();

    private JPanel createFormular() {
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        NiceJLabel head = new NiceJLabel("Antragsdaten:");
        head.setFont(new Font("Arial", Font.BOLD, 12));
        head.setForeground(new Color(155, 190, 20));

        JLabel infoText = new JLabel("<html><p align=\"justify\">Zur Vervollständigung des Antrages, müssen noch einige Daten erfasst werden. Tragen Sie bitte alle Daten in die Felder ein und klicken Sie anschließend auf \"Speichern\".</p></html>");
        infoText.setForeground(Color.white);
        infoText.setFont(new Font("Arial", Font.PLAIN, 10));

        NiceJLabel lnameLbl = new NiceJLabel("Name:");
        NiceJLabel fnameLbl = new NiceJLabel("Vorname:");
        NiceJLabel birthLbl = new NiceJLabel("Geb.Datum:");
        NiceJLabel staffCouncilLbl = new NiceJLabel("Zust. Personalrat:");
        NiceJLabel authSignerLbl = new NiceJLabel("Unterschriftsberechtigter:");

        fnameTxt = new JTextField();
        lnameTxt = new JTextField();
        birthTxt = new JTextField();
        staffCouncilCbo = new JComboBox();
        authSignerCbo = new CustomJComboBox();

        fnameTxt.addKeyListener(fnametxtlistener);
        lnameTxt.addKeyListener(lnametxtlistener);
        birthTxt.addKeyListener(birthtxtlistener);
        staffCouncilCbo.addItemListener(staffCouncilCbolistener);
        authSignerCbo.addItemListener(authSignerCbolistener);

        //reads the sign-authorized and the staffcouncil from the applet parameters.
        HashMap<String, String> params = getParams();
        Set<String> keys = params.keySet();
        Iterator<String> it = keys.iterator();
        while(it.hasNext()){
            String key = it.next();
            if(key.startsWith(paramsToFetch[0])){
                staffCouncilCbo.addItem(params.get(key));
            }else if(key.startsWith(paramsToFetch[1])){
                authSignerCbo.addItem(params.get(key));
            }
        }

        final ImageIcon attachImage = new ImageIcon(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/accept.png")));
        JButton attachData = new JButton("Speichern") {

            private ImageIcon scaledIcon = null;

            @Override
            public void paint(Graphics g) {
                super.paint(g);
                if (scaledIcon == null) {
                    scaledIcon = new ImageIcon(attachImage.getImage().getScaledInstance(-1, getHeight() - 2, Image.SCALE_SMOOTH));
                }
                int iconX = getWidth() - (scaledIcon.getIconWidth()) - 10;
                int iconY = (getHeight() / 2) - (scaledIcon.getIconHeight() / 2);
                g.drawImage(scaledIcon.getImage(), iconX, iconY, this);
            }
        };
        attachData.addMouseListener(new ButtonActionListener());

        final ImageIcon clearImage = new ImageIcon(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/clear.png")));
        JButton clearFields = new JButton("Zurücksetzen") {

            private ImageIcon scaledIcon = null;

            @Override
            public void paint(Graphics g) {
                super.paint(g);
                if (scaledIcon == null) {
                    scaledIcon = new ImageIcon(clearImage.getImage().getScaledInstance(-1, getHeight() - 2, Image.SCALE_SMOOTH));
                }
                int iconX = getWidth() - (scaledIcon.getIconWidth()) - 10;
                int iconY = (getHeight() / 2) - (scaledIcon.getIconHeight() / 2);
                g.drawImage(scaledIcon.getImage(), iconX, iconY, this);
            }
        };
        clearFields.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                if (mdtCtrl.getCurrentDisplayedDocumentAsThumb() != null) {
                    actualizeInputFields(mdtCtrl.getCurrentDisplayedDocumentAsThumb());
                } else {
                    resetInputFields();
                }
            }
        });

        Insets topSpacedInset = new Insets(10, 10, 0, 10);
        Insets noSpacedInset = new Insets(0, 10, 0, 10);
        Insets bothSpacedInsets = new Insets(30, 10, 0, 10);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.anchor = GridBagConstraints.PAGE_START;
        c.insets = noSpacedInset;
        c.gridx = 0;
        c.gridy = 0;
        formPanel.add(head, c);

        c.gridy = 1;
        formPanel.add(infoText, c);

        c.insets = bothSpacedInsets;
        c.gridy = 2;
        formPanel.add(fnameLbl, c);

        c.anchor = GridBagConstraints.LINE_START;
        c.insets = noSpacedInset;
        c.gridy = 3;
        formPanel.add(fnameTxt, c);

        c.insets = topSpacedInset;
        c.gridy = 4;
        formPanel.add(lnameLbl, c);

        c.insets = noSpacedInset;
        c.gridy = 5;
        formPanel.add(lnameTxt, c);

        c.insets = topSpacedInset;
        c.gridy = 6;
        formPanel.add(birthLbl, c);

        c.insets = noSpacedInset;
        c.gridy = 7;
        formPanel.add(birthTxt, c);

        c.insets = topSpacedInset;
        c.gridy = 8;
        formPanel.add(staffCouncilLbl, c);

        c.insets = noSpacedInset;
        c.gridy = 9;
        formPanel.add(staffCouncilCbo, c);

        c.insets = topSpacedInset;
        c.gridy = 10;
        formPanel.add(authSignerLbl, c);

        c.insets = noSpacedInset;
        c.gridy = 11;
        formPanel.add(authSignerCbo, c);

        c.insets = bothSpacedInsets;
        c.gridy = 12;
        formPanel.add(attachData, c);

        c.insets = topSpacedInset;
        c.gridy = 13;
        formPanel.add(clearFields, c);

        c.gridy = 14;
        c.weighty = 1.0;
        JPanel btmSpacer = new JPanel();
        btmSpacer.setOpaque(false);
        formPanel.add(btmSpacer, c);

        return formPanel;
    }

    private HashMap<String, String> getParams() {
        log.info("Fetching parameters for the XMLEnricher...");
        HashMap<String, String> params = new HashMap<String, String>();
        Configurator conf = Configurator.getInstance();
        AppletContext appletContext = conf.APPLET_CONTEXT;
        if (appletContext != null) {
            Enumeration<Applet> applets = appletContext.getApplets();
            if (applets != null) {
                while (applets.hasMoreElements()) {
                    Applet applet = applets.nextElement();
                    for (String specKey : paramsToFetch) {
                        String value = null;
                        int it = 0;
                        while ((value = applet.getParameter(specKey + it)) != null) {
                            params.put(specKey+it, value);
                            log.info("Parameter found - value: " + value + " - key: " + specKey+it);
                            it++;
                        }
                    }
                }
            }
        }
        log.info("Fetching parameters for the XMLEnricher done!");
        return params;
    }

    protected void paintComponent(Graphics g) {
        if (!isOpaque()) {
            super.paintComponent(g);
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint gp = new GradientPaint(0, 0, LAFProperties.getInstance().getBluePanelColorW(), getWidth(), 0, LAFProperties.getInstance().getBluePanelColorE());
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setColor(LAFProperties.getInstance().getBorderColor());
        g2.drawLine(0, 0, 0, getHeight());
        g2.setColor(LAFProperties.getInstance().getBluePanelBorderHighlightColor());
        g2.drawLine(1, 0, 1, getHeight());

        Polygon p = new Polygon();
        p.addPoint(0, 0);
        p.addPoint(0, getHeight());
        p.addPoint(getWidth(), 0);
        g2.setColor(LAFProperties.getInstance().getBluePanelHighlightColor());
        g2.fillPolygon(p);

        setOpaque(false);
        super.paintComponent(g);
        setOpaque(true);

    }

    private void resetInputFields() {
        fnameTxt.setText("");
        lnameTxt.setText("");
        birthTxt.setText("");
    }

    private class NiceJLabel extends JLabel {

        BufferedImage bi = null;
        Graphics2D big2 = null;

        public NiceJLabel(String text) {
            super(text);
            setFont(new Font("Arial", Font.BOLD, 10));
            setForeground(Color.white);
        }

        @Override
        public void paint(Graphics g) {
            bi = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            big2 = (Graphics2D) bi.getGraphics();
            big2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            big2.setColor(getForeground());
            big2.setFont(getFont());
            FontMetrics fm = this.getFontMetrics(getFont());
            int h = fm.getAscent();
            big2.setColor(Color.black);
            big2.drawString(getText(), 1, h + 1);
            big2.setColor(getForeground());
            big2.drawString(getText(), 0, h);
            g.drawImage(bi, 0, 0, this);
        }
    }

    private class ImagePanel extends JPanel {

        private Image image;

        public ImagePanel(Image image) {
            this.image = image;
            this.setOpaque(false);

            Dimension iDim = new Dimension(50, 130);
            setPreferredSize(iDim);
            setSize(iDim);
            setMaximumSize(iDim);
            setMinimumSize(iDim);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.drawImage(image, getWidth() / 14, getHeight() / 19, this);
        }
    }

    private class ComboBoxItemListener implements ItemListener {

        private String compareStr = "";

        public void setCompareStr(String compareStr) {
            this.compareStr = compareStr;
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (mdtCtrl != null && mdtCtrl.getCurrentDisplayedDocumentAsThumb() != null) {
                if (!compareStr.equals("")) {
                    JComboBox parentCB = (JComboBox) e.getSource();
                    String currSel = (String) parentCB.getSelectedItem();
                    if (currSel.equals(compareStr)) {
                        parentCB.setBackground(invisibleTxtBG);
                    } else {
                        parentCB.setBackground(errorTxtBG);
                    }
                }
            }
        }
    }

    private class TextFieldKeyListener extends KeyAdapter {

        private String compareStr = "";

        public void setCompareStr(String compareStr) {
            this.compareStr = compareStr;
        }

        @Override
        public void keyReleased(KeyEvent e) {

            if (mdtCtrl.getCurrentDisplayedDocumentAsThumb() != null) {
                JTextField tf = (JTextField) e.getSource();
                String txt = tf.getText();
                String mdtVal = compareStr;
                if (!mdtVal.equals("")) {
                    if (txt.equals(mdtVal)) {
                        tf.setBackground(invisibleTxtBG);
                    } else {
                        tf.setBackground(errorTxtBG);
                    }
                }
            }
        }
    }
    private JTextField fnameTxt = null;
    private JTextField lnameTxt = null;
    private JTextField birthTxt = null;
    private JComboBox staffCouncilCbo = null;
    private CustomJComboBox authSignerCbo = null;

    private class ButtonActionListener extends DocURLConnector implements MouseListener {

        private boolean testerMode = false;
        private String testUserName = "Systemadministrator"; //if strg + Save button is pressed, the signatory authorized is set to tester

        public ButtonActionListener() {
            mdtCtrl = Configurator.getInstance().getMultiDocCtrl();
        }

        private void processDocument(MultiDocumentThumb displayedMdt) throws IOException, DocumentException {
            HashMap<String, String> fieldData = fetchFormularData();
            displayedMdt.setXmlEnrichFName(fieldData.get("forename"));
            displayedMdt.setXmlEnrichLName(fieldData.get("surname"));
            displayedMdt.setXmlEnrichBirth(fieldData.get("birthday"));
            displayedMdt.setXmlEnrichPR(fieldData.get("staffcouncil"));
            displayedMdt.setXmlEnrichSigner(fieldData.get("authorizedsignatory"));
            String requestXML = generateXMLFromFormular(fieldData);
            ByteArrayOutputStream xmlAttachedPDF = attachXML(displayedMdt, requestXML);
            displayedMdt.setNewDocBytes(xmlAttachedPDF.toByteArray());
            displayedMdt.setDocPrepared(true);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if ((e.getModifiers() & ActionEvent.CTRL_MASK) > 0) {  //Just to give someone the possibilty of using a hidden testuser as authorized signatory
                testerMode = true;
            } else {
                testerMode = false;
            }
            if (fnameTxt.getText().equals("") || lnameTxt.getText().equals("")
                    || birthTxt.getText().equals("")) {
                DecoratedJOptionPane.showMessageDialog(null, "Bitte füllen Sie alle Felder aus.", "Information", DecoratedJOptionPane.INFORMATION_MESSAGE);
            } else {
                MultiDocumentThumb displayedMdt = mdtCtrl.getCurrentDisplayedDocumentAsThumb();
                if (displayedMdt == null) {
                    DecoratedJOptionPane.showMessageDialog(null, "Keine Dokumente geladen. Bitte laden mindestens ein Dokument.", "Information", DecoratedJOptionPane.INFORMATION_MESSAGE);
                } else {
                    try {
                        if (mdtCtrl.getCurrentDisplayedDocumentAsThumb().getNewDocBytes() == null) {
                            processDocument(displayedMdt);
                        } else {
                            if (!displayedMdt.getXmlEnrichFName().equals(fnameTxt.getText())
                                    || !displayedMdt.getXmlEnrichLName().equals(lnameTxt.getText())
                                    || !displayedMdt.getXmlEnrichBirth().equals(birthTxt.getText())
                                    || !displayedMdt.getXmlEnrichPR().equals((String) staffCouncilCbo.getSelectedItem())
                                    || !displayedMdt.getXmlEnrichSigner().equals((String) authSignerCbo.getSelectedItem())) {
                                int answer = DecoratedJOptionPane.showConfirmDialog(null, "Achtung: Alle rot markierten Felder werden überschrieben! Fortsetzen?", "Frage", DecoratedJOptionPane.YES_NO_OPTION, DecoratedJOptionPane.QUESTION_MESSAGE);
                                if (answer == DecoratedJOptionPane.YES_OPTION) {
                                    processDocument(displayedMdt);
                                }
                            } else if (testerMode) {
                                processDocument(displayedMdt);
                            }
                        }
                        MultiDocumentThumb[] mdts = mdtCtrl.getDocuments().toArray(new MultiDocumentThumb[0]);
                        MultiDocumentThumb toModify = null;
                        for (MultiDocumentThumb mdt : mdts) {
                            if (mdt.getNewDocBytes() == null) {
                                toModify = mdt;
                                break;
                            }
                        }
                        if (toModify != null) {
                            mdtCtrl.loadPDF(toModify.getCurrentPageOpened(), toModify);
                            resetInputFields();
                        } else {
                            int answer = DecoratedJOptionPane.showConfirmDialog(null, "Alle Dokumente besitzen nun die benötigten Nutzerdaten.\nMöchten Sie nun mit dem Upload der Dokumente beginnen?\n\nKlicken Sie auf 'NEIN' wenn Sie die Daten überprüfen oder korrigieren möchten.", "Frage", DecoratedJOptionPane.YES_NO_OPTION, DecoratedJOptionPane.QUESTION_MESSAGE);
                            if (answer == DecoratedJOptionPane.YES_OPTION) {
                                uploadDocuments();
                            }
                        }
                    } catch (Exception ex) {
                        log.error("An exception occured while xml data were collected/generated/attached. - " + ex.getMessage());
                    }
                }
                if (mdtCtrl.getCurrentDisplayedDocumentAsThumb() != null) {
                    actualizeInputFields(mdtCtrl.getCurrentDisplayedDocumentAsThumb());
                }
            }
        }
        private BASE64Encoder b64dec = new BASE64Encoder();

        private void uploadDocuments() {
            final RootGlassPane rgp = (RootGlassPane) Configurator.getInstance().getPdfViewer().getGlassPane();
            rgp.setViewTo(RootGlassPane.LOCKED);
            rgp.setLoadingText(LanguageFactory.getText(LanguageFactory.GUISIGNVIEW_LOADINGTXT_WAIT_MSG));
            rgp.setInfoText(LanguageFactory.getText(LanguageFactory.GUISIGNVIEW_INFOTXT_UPLOADING_PDF));
            setDestURL(ParamValidator.getInstance().getPostDestination());

            setAction(DOCUMENT_TO_DESTURL);

            for (MultiDocumentThumb mdt : mdtCtrl.getDocuments()) {
                addFileEntity(new FileEntity(mdt.getNewDocBytes(), mdt.getDocName(), mdt.getDocPostID(), "application/pdf"));
            }
            try {
                TrustManager[] trustManagers = GlobalData.getTrustAllCertsTrustManager();
                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, trustManagers, new SecureRandom());
                setSSLContext(sc);
            } catch (KeyManagementException ex) {
            } catch (NoSuchAlgorithmException ex) {
            }
            start();
            new Thread() {

                @Override
                public void run() {
                    while (isRunning()) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                        }
                    }
                    try {
                        throwExceptions();
                    } catch (Exception e) {
                        log.error("An exception occured during document upload: " + e.getMessage());
                    }
                    rgp.setAnimationRunning(false);
                    rgp.setLoadingText("Upload abgeschlossen");
                    rgp.setInfoText("Sie können das Programm nun schließen");

                    String ulResult = getULResult();
                    String[] errorCodes = ulResult.split(",");
                    boolean uploadOK = false;
                    for (String currEC : errorCodes) {
                        if (currEC.trim().contains("0")) {
                            uploadOK = true;
                            break;
                        }
                    }
                    if (uploadOK) {
                        DecoratedJOptionPane.showMessageDialog(null, "Der Upload der aller Dokumente wurde erfolgreich abgeschlossen.\nDas Programm wird nun beendet.", LanguageFactory.getText(LanguageFactory.INFO_HL), DecoratedJOptionPane.INFORMATION_MESSAGE);
                    } else {
                        DecoratedJOptionPane.showMessageDialog(null, "Die Dokumente wurden hochgeladen. Jedoch antwortet der Server mit einem Fehler.\nBitte beachten Sie die Informationen auf der Folgeseite.\n\nDas Programm wird nun beendet.", LanguageFactory.getText(LanguageFactory.ERROR_HL), DecoratedJOptionPane.ERROR_MESSAGE);
                    }
                    Configurator.getInstance().closeProgram(ulResult);
                }
            }.start();
        }

        private HashMap<String, String> fetchFormularData() {
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("forename", fnameTxt.getText());
            data.put("surname", lnameTxt.getText());
            data.put("birthday", birthTxt.getText());
            data.put("staffcouncil", (String) staffCouncilCbo.getSelectedItem());
            String authSign = (String) authSignerCbo.getSelectedItem();
            if (testerMode) {             //for testing mode. becomes activated when strg+alt + save btn is clicked
                authSign = testUserName;
                log.info("The authorized signatory is set to '" + testUserName + "' due to activated testing mode");
            }
            data.put("authorizedsignatory", authSign);
            return data;
        }

        private String generateXMLFromFormular(HashMap<String, String> data) {
            data.put("staffcouncil", (String) staffCouncilCbo.getSelectedItem());
            Element root = new Element("requestdata");
            Set<String> keySet = data.keySet();
            Iterator<String> keyIt = keySet.iterator();
            while (keyIt.hasNext()) {
                String key = keyIt.next();
                String value = data.get(key);
                Element currElement = new Element(key);
                currElement.setText(value);
                root.addContent(currElement);
            }
            XMLOutputter outputter = new XMLOutputter();
            return outputter.outputString(root);
        }

        private ByteArrayOutputStream attachXML(MultiDocumentThumb mdt, String xml) throws IOException, DocumentException {
            ByteArrayOutputStream outputFile = new ByteArrayOutputStream();
            PdfReader reader = new PdfReader(mdt.getDocBytes());
            PdfStamper stamper = new PdfStamper(reader, outputFile, '\0', true);
            stamper.getWriter().addFileAttachment("Antragsdaten", xml.getBytes(), "", "requestdata.xml");
            stamper.close();
            return outputFile;
        }

        public void mouseClicked(MouseEvent e) {
//            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void mousePressed(MouseEvent e) {
//            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void mouseEntered(MouseEvent e) {
//            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void mouseExited(MouseEvent e) {
//            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    class CustomJComboBox extends JComboBox {

        private boolean visibleUnknownAuthSign = false;
        private String unknownAuthSign = "";

        public String getUnknownAuthSign() {
            return unknownAuthSign;
        }

        public void setUnknownAuthSign(String unknownAuthSign) {
            this.unknownAuthSign = unknownAuthSign;
            repaint();
        }

        public boolean isVisibleUnknownAuthSign() {
            return visibleUnknownAuthSign;
        }

        public void setVisibleUnknownAuthSign(boolean visibleUnknownAuthSign) {
            this.visibleUnknownAuthSign = visibleUnknownAuthSign;
            repaint();
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            if (visibleUnknownAuthSign) {
                drawUnknownAuthSign(g);
            }
        }
        private Font font = new Font("Arial", Font.BOLD, 9);

        private void drawUnknownAuthSign(Graphics g) {
            int x = 2;
            int y = 2;
            int width = getWidth() - 25;
            int height = getHeight() - (y * 2);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(220, 0, 0, 175));
            g2.fillRoundRect(x, y, width, height, 5, 5);
            g2.setFont(font);
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(new Color(255, 255, 255, 240));
            g2.drawString(unknownAuthSign, (width / 2) - (fm.stringWidth(unknownAuthSign) / 2), fm.getHeight() + 1);
        }
    }
}
