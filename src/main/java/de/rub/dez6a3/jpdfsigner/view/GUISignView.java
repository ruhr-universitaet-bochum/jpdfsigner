/*
 * JPDFSigner - Sign PDFs online using smartcards (GUISignView.java)
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
import de.rub.dez6a3.jpdfsigner.control.CX509KeyManager;
import de.rub.dez6a3.jpdfsigner.control.Configurator;
import de.rub.dez6a3.jpdfsigner.control.GlobalData;
import de.rub.dez6a3.jpdfsigner.control.MaxDocSigns;
import de.rub.dez6a3.jpdfsigner.control.SignAndStampablePagePanel;
import de.rub.dez6a3.jpdfsigner.exceptiontypes.CardAccessorException;
import de.rub.dez6a3.jpdfsigner.exceptiontypes.PKCS11Exception;
import de.rub.dez6a3.jpdfsigner.exceptiontypes.SignerException;
import de.rub.dez6a3.jpdfsigner.control.language.LanguageFactory;
import de.rub.dez6a3.jpdfsigner.model.ICardAccessor;
import de.rub.dez6a3.jpdfsigner.view.UI.LAFProperties;
import de.rub.dez6a3.jpdfsigner.view.UI.borders.SignPanelBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.Random;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509KeyManager;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import org.apache.log4j.Logger;

public class GUISignView extends JPanel {

    private JFrame cParent;
    private JPanel startSignPanel;
    private JPanel keyPadPanel;
    private JPanel infoPanel;
    private SignHintAnimationPanel manualStampHintPanel;
    private JLabel infoTxt;
    private JPasswordField pinField = null;
    private Boolean protectedAuthPath;
    private static Logger log = Logger.getLogger(GUISignView.class);

    public GUISignView() {
        setPreferredSize(new Dimension(200, 1));
        setBackground(new Color(0, 0, 0));
        setBorder(new SignPanelBorder());
        try {
            manualStampHintPanel = new SignHintAnimationPanel();
        } catch (Exception e) {
            log.error(e);
        }
        startSignPanel = createManualStamperPanel();
        keyPadPanel = createPINPanel();
        createInfoPanel();

        ImageIcon panelIcon = new ImageIcon(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/label.png"));

        setLayout(new GridLayout(3, 1));
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        keyPadPanel.setOpaque(false);

        add(new ImagePanel(panelIcon.getImage()), 0);
        add(startSignPanel, 1);
        add(infoPanel, 2);

        super.setVisible(false);
    }

    public void setCParent(JFrame cParent) {
        this.cParent = cParent;
    }

    private Thread installSignerThread() {

        Thread signerTh = new Thread() {

            public void run() {
                Configurator config = Configurator.getInstance();
                setGlassPaneLocked(true);
                try {
                    config.getPdfViewer().getContentPanel().setVisible(false);
                    setGlassPaneLoadingText(LanguageFactory.getText(LanguageFactory.GUISIGNVIEW_LOADINGTXT_WAIT_MSG));
                    setGlassPaneInfoText(LanguageFactory.getText(LanguageFactory.GUISIGNVIEW_INFOTXT_LOADING_CP));
                    ICardAccessor cardHandler = config.getCardHandler();

                    cardHandler.load();

                    cardHandler.getTokenFlags(cardHandler.getSelectedReader()); //trys to load the token... if no one is present it will be fired a CKR_TOKEN_NOT_PRESENT notification
                    handleSigning();    //hier werden auch info und loading texte gesetzt
                    setGlassPaneLoadingText(LanguageFactory.getText(LanguageFactory.GUISIGNVIEW_LOADINGTXT_WAIT_MSG));
                    setGlassPaneInfoText(LanguageFactory.getText(LanguageFactory.GUISIGNVIEW_INFOTXT_UPLOADING_PDF));
                    handleUpload();
                } catch (CardAccessorException e) {
                    String msg = e.getMessage();
                    if(msg == null){
                        msg = "";
                    }
                    if (msg.equals("CKR_TOKEN_NOT_PRESENT")) {
                        log.info(e);
                        DecoratedJOptionPane.showMessageDialog(null, "Bitte stecken Sie ihre RUBCard in den Smartcard-Leser.", LanguageFactory.getText(LanguageFactory.INFO_HL), DecoratedJOptionPane.INFORMATION_MESSAGE);
                    } else if (msg.equals("CKR_SLOT_ID_INVALID")) {
                        log.warn(e);
                        DecoratedJOptionPane.showMessageDialog(null, "Der ausgewählte Smartcart-Leser ist nicht an dem damit verbundenen Slot angeschlossen.\nBitte wählen Sie ein anderes Lesegerät.", LanguageFactory.getText(LanguageFactory.WARN_HL), DecoratedJOptionPane.WARNING_MESSAGE);
                        } else if (msg.equals("INSTANCIATE_WRAPPER_ERROR")) {
                        log.error(e);
                        DecoratedJOptionPane.showMessageDialog(null, "Der Kryptoprovider konnte nicht geladen werden.\nBitte stellen Sie sicher, dass Sie einen PKCS11-Provider nach dem\n'PKCS #11: Cryptographic Token Interface Standard' verwenden.", "Fehler", DecoratedJOptionPane.ERROR_MESSAGE);
                    } else {
                        log.error(e, e);
                        DecoratedJOptionPane.showMessageDialog(null, "Es ist ein Fehler aufgetreten.\nWeitere Informationen finden Sie in den Logs.", "Fehler", DecoratedJOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException e) {
                    log.error(e);
                    DecoratedJOptionPane.showMessageDialog(null, "Ihre RUBCard konnte nicht geladen werden (PIN falsch?).\nBitte beachten sie, dass nach dem dritten fehlerhaften Versuch ihre PIN gesperrt wird.", "Warnung", DecoratedJOptionPane.WARNING_MESSAGE);
                } catch (Throwable e) {
                    log.error(e);
                    DecoratedJOptionPane.showMessageDialog(null, "Es ist ein Fehler aufgetreten.\nWeitere Informationen finden Sie in den Logs.", "Fehler", DecoratedJOptionPane.ERROR_MESSAGE);
                } finally {
                    config.getCardHandler().resetPKCS11KeyStoreAndProvider();
                    try {
                        config.getCardHandler().unload();
                    } catch (Throwable e) {
                        log.error(e);
//                        OpenSCCardAccessor.handleException(e);
                    }
                    setGlassPaneLocked(false);
                    config.getPdfViewer().getContentPanel().setVisible(true);
                    pinField.setText("");

                }
            }
        };
        return signerTh;
    }

    public void setGlassPaneLocked(boolean param) {
        Component rgp = cParent.getGlassPane();
        if (rgp != null && rgp instanceof RootGlassPane) {
            if (param) {
                ((RootGlassPane) rgp).setVisible(true);
                ((RootGlassPane) rgp).setViewTo(RootGlassPane.LOCKED);
            } else {
                ((RootGlassPane) rgp).setViewTo(RootGlassPane.UNLOCKED);
            }
        }
    }

    public void setGlassPaneAnimationRunning(boolean param) {
        Component rgp = cParent.getGlassPane();
        if (rgp != null && rgp instanceof RootGlassPane) {
            ((RootGlassPane) rgp).setAnimationRunning(param);
        }
    }

    public void setGlassPaneLoadingText(String param) {
        Component rgp = cParent.getGlassPane();
        if (rgp != null && rgp instanceof RootGlassPane) {
            ((RootGlassPane) rgp).setLoadingText(param);
        }
    }

    public void setGlassPaneInfoText(String param) {
        Component rgp = cParent.getGlassPane();
        if (rgp != null && rgp instanceof RootGlassPane) {
            ((RootGlassPane) rgp).setInfoText(param);
        }
    }

    public void setInfoText(String param) {
        infoTxt.setText("<html><table><tr><td align=\"justify\"><br /><br /><div align=\"center\"><font size=\"6\" color=\"8DAE10\"><b>INFO</b></font></div><br /><br /><font size=\"3\">" + param + "</font></td></tr></table></html>");
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

    @Override
    public void setVisible(boolean param) {
        super.setVisible(param);
        if (cParent != null) {
            Dimension newDim = null;
            if (param) {
                newDim = new Dimension(cParent.getSize().width + getPreferredSize().width, cParent.getSize().height);
            } else {
                newDim = new Dimension(cParent.getSize().width - getPreferredSize().width, cParent.getSize().height);
            }
            cParent.setPreferredSize(newDim);
            cParent.setSize(newDim);
        }
    }
    private Thread cryptoLoaderControllor = null;
    private Thread cryptoLoader = null;
    private boolean exceptionInCryptoLoader = false;

    public void initSigner(final JComponent animation) {
        if (animation instanceof SignButton) {
            ((SignButton) animation).setWaitingState(true);
        } else if (animation instanceof SignAndStampablePagePanel) {
            ((SignAndStampablePagePanel) animation).runAnimation(true);
        }

        cryptoLoader = new Thread() {

            public void run() {
                protectedAuthPath = null;
                Configurator.getInstance().getSignAndStampablePagePanel().setDocFixed(true);
                ICardAccessor cardHandler = Configurator.getInstance().getCardHandler();
                try {
                    cardHandler.load();
                    long tokenFlags = cardHandler.getTokenFlags(cardHandler.getSelectedReader());
                    protectedAuthPath = cardHandler.readTokenFlagsInfo(tokenFlags, ICardAccessor.CKF_PROTECTED_AUTHENTICATION_PATH);
                } catch (CardAccessorException e) {
                    String msg = e.getMessage();
                    if (msg == null) {
                        msg = "";
                    }
                    if (msg.equals("CKR_TOKEN_NOT_PRESENT")) {
                        log.info(e);
                        DecoratedJOptionPane.showMessageDialog(null, "Bitte stecken Sie ihre RUBCard in den Smartcard-Leser.", LanguageFactory.getText(LanguageFactory.INFO_HL), DecoratedJOptionPane.INFORMATION_MESSAGE);
                    } else if (msg.equals("CKR_SLOT_ID_INVALID")) {
                        log.warn(e);
                        DecoratedJOptionPane.showMessageDialog(null, "Der ausgewählte Smartcart-Leser ist nicht an dem damit verbundenen Slot angeschlossen.\nBitte wählen Sie ein anderes Lesegerät.", LanguageFactory.getText(LanguageFactory.WARN_HL), DecoratedJOptionPane.WARNING_MESSAGE);
                    } else if (msg.equals("INSTANCIATE_WRAPPER_ERROR")) {
                        log.error(e);
                        DecoratedJOptionPane.showMessageDialog(null, "Der Kryptoprovider konnte nicht geladen werden.\nBitte stellen Sie sicher, dass Sie einen PKCS11-Provider nach dem\n'PKCS #11: Cryptographic Token Interface Standard' verwenden.", "Fehler", DecoratedJOptionPane.ERROR_MESSAGE);
                    } else {
                        log.error(e);
                        DecoratedJOptionPane.showMessageDialog(null, "Es ist ein Fehler aufgetreten.\nWeitere Informationen finden Sie in den Logs.", "Fehler", DecoratedJOptionPane.ERROR_MESSAGE);
                    }
                    exceptionInCryptoLoader = true;
                } catch (Throwable t) {
                    log.error(t);
//                    OpenSCCardAccessor.handleException(t);
                    exceptionInCryptoLoader = true;
                    DecoratedJOptionPane.showMessageDialog(null, "Es ist ein Fehler aufgetreten.\nWeitere Informationen können den Logs entnommen werden.", LanguageFactory.getText(LanguageFactory.ERROR_HL), DecoratedJOptionPane.ERROR_MESSAGE);
                    Configurator.getInstance().getSignAndStampablePagePanel().setDocFixed(false);
                } finally {
                    cardHandler.resetPKCS11KeyStoreAndProvider();
                    try {
                        cardHandler.unload();
                    } catch (CardAccessorException e) {
                    }
                }
                if (protectedAuthPath != null) {
                    if (protectedAuthPath) {
                        installSignerThread().start();
                        Configurator.getInstance().getSignAndStampablePagePanel().setDocFixed(false);
                    } else {
                        changeViewTo(keyPadPanel);
                        pinField.requestFocus();
                    }
                }
            }
        };
        cryptoLoaderControllor = new Thread() {

            public void run() {
                try {
                    cryptoLoader.join(20000);
                    if (cryptoLoader.isAlive()) {
                        cryptoLoader.interrupt();
                        if (!exceptionInCryptoLoader) {
                            DecoratedJOptionPane.showMessageDialog(null, "Der Ladevorgang hat das Zeitlimit überschritten.\nMöglicherweise kann der Kryptoprovider nicht geladen werden.\n\nBitte überprüfen Sie ihre Hardware, Treiber und ihren Kryptoprovider.", LanguageFactory.getText(LanguageFactory.ERROR_HL), DecoratedJOptionPane.ERROR_MESSAGE);
//                        OpenSCCardAccessor.handleException(new ThreadHangsException());
                        }
                    }
                    if (animation instanceof SignButton) {
                        ((SignButton) animation).setWaitingState(false);
                    } else if (animation instanceof SignAndStampablePagePanel) {
                        ((SignAndStampablePagePanel) animation).runAnimation(false);
                    }
                } catch (InterruptedException ex) {
                }
            }
        };
        cryptoLoader.start();
        cryptoLoaderControllor.start();
    }

    private JPanel createManualStamperPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        final SignButton signBtn = new SignButton("Sign", 120);
        Font font = LAFProperties.getInstance().getMainFontBold().deriveFont(Font.BOLD, 30);
        signBtn.setFont(font);
        signBtn.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {
                if (Configurator.getInstance().getCardHandler().getSelectedReader() != null) {
//                    if (specs.getData(PdfSignSpecifications.STAMP_POSITION) != null) {
                    if (Configurator.getInstance().getMultiDocCtrl().getMultiDocumentCount() > 0) {
                        if (cryptoLoader == null && cryptoLoaderControllor == null) {
                            initSigner(signBtn);
                        } else {    //both thread shouldnt be null
                            if (!cryptoLoader.isAlive() && !cryptoLoaderControllor.isAlive()) {
                                initSigner(signBtn);
                            }
                        }
                    } else {
                        DecoratedJOptionPane.showMessageDialog(null, "Bitte laden Sie mindestens ein Dokument um den Signierprozess zu starten.", "Information", DecoratedJOptionPane.INFORMATION_MESSAGE);
                    }
//                    } else {
//                        changeViewTo(manualStampHintPanel);
//                        Configurator.getInstance().getSignAndStampablePagePanel().setDocStampable(true);
//                        setInfoText(LanguageFactory.getText(LanguageFactory.GUISIGNVIEW_MANUAL_STAMPER_PANEL_TEXT));
//                    }
                } else {
                    DecoratedJOptionPane.showMessageDialog(null, "Bitte wählen Sie zu erst einen Kartenleser aus.", "Info", DecoratedJOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        panel.add(signBtn);
        return panel;
    }

    private void createInfoPanel() {
        infoPanel = new JPanel();
        infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        infoPanel.setLayout(new BorderLayout());
        infoPanel.setOpaque(false);
        infoTxt = new JLabel();
        infoTxt.setFont(LAFProperties.getInstance().getMainFontRegular());
        infoTxt.setForeground(Color.white);
        infoTxt.setOpaque(false);
        setInfoText(LanguageFactory.getText(LanguageFactory.GUISIGNVIEW_INFO_PANEL_TEXT));
        infoPanel.add(infoTxt, BorderLayout.NORTH);
    }

    private JPanel createPINPanel() {
        ImageIcon acceptIcon = new ImageIcon(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/accept.png"));
        ImageIcon clearIcon = new ImageIcon(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/clear.png"));


        JPanel keyPad = new JPanel();

        keyPad.setLayout(new BorderLayout());
        keyPad.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BorderLayout());

        pinField = new JPasswordField(new MaxDocSigns(6, MaxDocSigns.JUST_INT), "", 5) {

            @Override
            public void paint(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.white);
                g2.setPaint(new GradientPaint(0, 0, new Color(220, 220, 220), 0, getHeight(), new Color(240, 240, 240)));
                g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 7, 7);
                g2.setColor(new Color(60, 60, 60));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.setColor(new Color(240, 240, 240));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 8, 8);
                super.paint(g2);
            }
        };
        pinField.setBackground(new Color(0, 0, 0, 0));
        pinField.setOpaque(false);
        pinField.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        pinField.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {
                ((JPasswordField) e.getComponent()).setText("");
            }
        });
//        pinField.setDocument(new MaxDocSigns(6, MaxDocSigns.JUST_INT));
        pinField.setSize(new Dimension(1, 500));
        pinField.setHorizontalAlignment(JPasswordField.CENTER);
        pinField.select(0, 1);
        textPanel.add(pinField);

        JPanel keyPanel = new JPanel();
        keyPanel.setOpaque(false);
        keyPanel.setLayout(new GridLayout(4, 3));

        ImageIcon okBtnIcon = new ImageIcon(acceptIcon.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));
        ImageIcon okBtnIconPressed = new ImageIcon(okBtnIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        ImageIcon clearBtnIcon = new ImageIcon(clearIcon.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));
        ImageIcon clearBtnIconPressed = new ImageIcon(clearBtnIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        ActionListenerEvents zeroBtnJustifyAction = new ActionListenerEvents("0", null, null, null, "zeroBtn", pinField);
        ActionListenerEvents oneBtnJustifyAction = new ActionListenerEvents("1", null, null, null, "oneBtn", pinField);
        ActionListenerEvents twoBtnJustifyAction = new ActionListenerEvents("2", null, null, null, "twoBtn", pinField);
        ActionListenerEvents threeBtnJustifyAction = new ActionListenerEvents("3", null, null, null, "threeBtn", pinField);
        ActionListenerEvents fourBtnJustifyAction = new ActionListenerEvents("4", null, null, null, "fourBtn", pinField);
        ActionListenerEvents fiveBtnJustifyAction = new ActionListenerEvents("5", null, null, null, "fiveBtn", pinField);
        ActionListenerEvents sixBtnJustifyAction = new ActionListenerEvents("6", null, null, null, "sixBtn", pinField);
        ActionListenerEvents sevenBtnJustifyAction = new ActionListenerEvents("7", null, null, null, "sevenBtn", pinField);
        ActionListenerEvents eightBtnJustifyAction = new ActionListenerEvents("8", null, null, null, "eightBtn", pinField);
        ActionListenerEvents nineBtnJustifyAction = new ActionListenerEvents("9", null, null, null, "nineBtn", pinField);
        ActionListenerEvents clearBtnJustifyAction = new ActionListenerEvents("", clearBtnIcon, null, null, "clearBtn", pinField);

        JButton[] btnArray = new JButton[12];
        JButton oneBtn = new JButton(oneBtnJustifyAction);
        JButton twoBtn = new JButton(twoBtnJustifyAction);
        JButton threeBtn = new JButton(threeBtnJustifyAction);
        JButton fourBtn = new JButton(fourBtnJustifyAction);
        JButton fiveBtn = new JButton(fiveBtnJustifyAction);
        JButton sixBtn = new JButton(sixBtnJustifyAction);
        JButton sevenBtn = new JButton(sevenBtnJustifyAction);
        JButton eightBtn = new JButton(eightBtnJustifyAction);
        JButton nineBtn = new JButton(nineBtnJustifyAction);
        JButton zeroBtn = new JButton(zeroBtnJustifyAction);
        JButton clearBtn = new JButton(clearBtnJustifyAction);
        final JButton okBtn = new JButton();
        pinField.addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 10) {
                    okBtn.doClick();
                }
            }
        });
        okBtn.setToolTipText("");       //bewirkt, dass die customUI den button nicht wie ein toolbarbutton zeichnet
        okBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (Configurator.getInstance().getMultiDocCtrl().getMultiDocumentCount() > 0) {
                    installSignerThread().start();
                } else {
                    DecoratedJOptionPane.showMessageDialog(null, "Bitte laden Sie mindestens ein Dokument um den Signierprozess zu starten.", "Information", DecoratedJOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        okBtn.setIcon(okBtnIcon);
        okBtn.setPressedIcon(okBtnIconPressed);
        clearBtn.setToolTipText("");                //bewirkt, dass die customUI den button nicht wie ein toolbarbutton zeichnet
        clearBtn.setPressedIcon(clearBtnIconPressed);

        btnArray[0] = zeroBtn;
        btnArray[1] = oneBtn;
        btnArray[2] = twoBtn;
        btnArray[3] = threeBtn;
        btnArray[4] = fourBtn;
        btnArray[5] = fiveBtn;
        btnArray[6] = sixBtn;
        btnArray[7] = sevenBtn;
        btnArray[8] = eightBtn;
        btnArray[9] = nineBtn;
        btnArray[10] = clearBtn;
        btnArray[11] = okBtn;
        int gridNmbr = 0;
        Random rand = new Random();
        while (gridNmbr < btnArray.length) {
            int currIndex = rand.nextInt(btnArray.length);
            if (btnArray[currIndex] != null) {
                if (gridNmbr == 9 || gridNmbr == 11) {
                    if (gridNmbr == 9) {
                        keyPanel.add(btnArray[10]);
                        btnArray[10] = null;
                    } else if (gridNmbr == 11) {
                        keyPanel.add(btnArray[11]);
                        btnArray[11] = null;
                    }
                    gridNmbr++;
                } else {
                    if (currIndex != 10 && currIndex != 11) {
                        keyPanel.add(btnArray[currIndex]);
                        btnArray[currIndex] = null;
                        gridNmbr++;
                    }
                }
            }
        }

        keyPad.add(textPanel, BorderLayout.NORTH);
        keyPad.add(keyPanel, BorderLayout.CENTER);

        return keyPad;
    }

    private void setSSLContext() {

        try {
            KeyStore iks = Configurator.getInstance().getCardHandler().getPKCS11KeyStore(); //should be already initialized during cardlogin
            TrustManager[] trustManager = null;

            trustManager = GlobalData.getTrustAllCertsTrustManager();       //creates trustall-cert truststore - SHOULDNT BE USED IN PRODUCTIVE ENVIRONMENT

//            KeyStore tks = ParamValidator.getInstance().getTrustStore();  //... use this in productive environment (needs a correction of implementation)
//            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//            tmf.init(tks);
//            trustManager = tmf.getTrustManagers();

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(iks, null);
            KeyManager[] km = kmf.getKeyManagers();
            KeyManager[] x509km = {new CX509KeyManager((X509KeyManager) km[0])};

            SSLContext ctx = SSLContext.getInstance("SSLv3");
            ctx.init(x509km, trustManager, new SecureRandom());
            GlobalData.uploadSSLContext = ctx;
            log.info("PKCS11-Based SSLContext set");
        } catch (Exception e) {
            log.fatal("Setting SSLContext failed - " + e.getMessage());
        }

    }

    private void handleUpload() {
        Configurator.getInstance().getSignAndStampablePagePanel().upload();
    }

    private void handleSigning() throws PKCS11Exception, CertificateException, SignerException, KeyStoreException, IOException, NoSuchAlgorithmException {
        Configurator config = Configurator.getInstance();
        char[] pin = null;
        if (!protectedAuthPath) {
            pin = pinField.getPassword();
        } else {
            setGlassPaneLoadingText(LanguageFactory.getText(LanguageFactory.GUISIGNVIEW_LOADINGTXT_AWAITING_PIN));
            setGlassPaneInfoText(LanguageFactory.getText(LanguageFactory.GUISIGNVIEW_INFOTXT_AWAITING_PIN));
        }
        config.getDocumentSigner().loginCard(pin);
        setSSLContext();        //must be set before signingprocess. if not the signer cannnot sign the document.
        config.getSignAndStampablePagePanel().doSign(); //sets glasspane messages too
    }

    public JPanel getKeyPadPanel() {
        return keyPadPanel;
    }

    public void changeViewTo(JComponent c) {
        remove(1);
        add(c, 1);
        validate();
        repaint();
    }

    private class ImagePanel extends JPanel {

        Image image;

        public ImagePanel(Image image) {
            this.image = image;
            this.setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.drawImage(image, getWidth() / 14, getHeight() / 19, this);
        }
    }
}
