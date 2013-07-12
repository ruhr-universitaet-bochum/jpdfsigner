/*
 * JPDFSigner - Sign PDFs online using smartcards (GUIPDFView.java)
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

import de.rub.dez6a3.jpdfsigner.plugins.PDFXMLEnricher;
import de.rub.dez6a3.jpdfsigner.control.AWTUtilitiesValidator;
import de.rub.dez6a3.jpdfsigner.control.ActionListenerEvents;
import de.rub.dez6a3.jpdfsigner.control.Configurator;
import de.rub.dez6a3.jpdfsigner.control.DocURLConnector;
import de.rub.dez6a3.jpdfsigner.control.JPodPDFViewer;
import de.rub.dez6a3.jpdfsigner.control.MaxDocSigns;
import de.rub.dez6a3.jpdfsigner.control.MultiDocumentThumb;
import de.rub.dez6a3.jpdfsigner.control.MultiDocumentThumbController;
import de.rub.dez6a3.jpdfsigner.control.ParamValidator;
import de.rub.dez6a3.jpdfsigner.control.WindowTools;
import de.rub.dez6a3.jpdfsigner.exceptiontypes.CardAccessorException;
import de.rub.dez6a3.jpdfsigner.control.language.Language;
import de.rub.dez6a3.jpdfsigner.control.language.LanguageFactory;
import de.rub.dez6a3.jpdfsigner.model.IDocumentHandler;
import de.rub.dez6a3.jpdfsigner.model.IPDFProcessor;
import de.rub.dez6a3.jpdfsigner.plugins.MultiDocumentViewer;
import de.rub.dez6a3.jpdfsigner.view.UI.LAFProperties;
import de.rub.dez6a3.jpdfsigner.view.UI.borders.JFrameTitledComponentBorder;
import de.rub.dez6a3.jpdfsigner.view.jpodviewer.listener.PageChangedListener;
import de.rub.dez6a3.jpdfsigner.view.jpodviewer.listener.ScaleChangedListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicPopupMenuUI;
import org.apache.log4j.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import sun.swing.SwingUtilities2;

public class GUIPDFView extends JFrame {

    /**
     * Creates new form GUIView2
     */
    private JMenuBar menubar;
    private JToolBar toolbar;
    private IPDFProcessor pdfViewer;
    private GUIConfigView configView;
    private Configurator config;
    private JComponent pdfView;
    private GUISignView signPanel;
    private JTextField pages;
    private JTextField scaleFactorTxt;
    private GUIAttachmentBarList attachmentBar;
    private JTabbedPane tabs;
    private AttachmentBarButton attachB;
    private AttachmentBarButton expandSignatureButton;
    private Splash splashScreen;
    public static Logger log = Logger.getLogger(GUIPDFView.class);
    private GraphicsConfiguration gc;
    private MultiDocumentViewer mdv = null;
    private boolean isConfigurationComplete = false;

    public GUIPDFView(String title) {
        super(title, AWTUtilitiesValidator.getInstance().getTranslucentGraphicsConfig());

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(800, 575));

        splashScreen = new Splash();

        //------------------------------------------- Applet boot begins here
        splashScreen.start();
        waitForSplashGrafixInitialisation();
        splashScreen.setLoadingText(LanguageFactory.getText(LanguageFactory.GUIPDFVIEW_SPLASH_CONFIGURATING));
        configure();
        if (config.getInstance().getProperty("libpath") == null || config.getInstance().getProperty("screader") == null) {
            configView.setVisible(true);
        } else {
            configView.setVisible(false);
            isConfigurationComplete = true;
        }
        config.setPdfViewer(this);
        splashScreen.setPercentBar(10);
        splashScreen.setLoadingText(LanguageFactory.getText(LanguageFactory.GUIPDFVIEW_SPLASH_PDF));
        getPDFDocument();
        splashScreen.setPercentBar(70);
        splashScreen.setPercentBar(75);
        splashScreen.setPercentBar(85);
        initActionComponents();
        splashScreen.setPercentBar(90);
        loadPDF();
        splashScreen.setLoadingText(LanguageFactory.getText(LanguageFactory.GUIPDFVIEW_SPLASH_PDF_VERIFY));
        splashScreen.setPercentBar(95);
        splashScreen.setLoadingText(LanguageFactory.getText(LanguageFactory.GUIPDFVIEW_SPLASH_PDF_ATTACHMENTS));
        initSignPanel();
        splashScreen.setPercentBar(100);
        setUndecorated(true);
        combineViews();
        splashScreen.close();
//------------------------------------------------------------------ Applet boot ends here
    }

    public boolean isConfigurationComplete() {
        return isConfigurationComplete;
    }

    private void waitForSplashGrafixInitialisation() {
        while (!splashScreen.isGraphicInit()) {
            log.trace("Splashscreen - waiting for graphics initialized");
            try {
                Thread.sleep(250);
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void setUndecorated(boolean param) {
        super.setUndecorated(param);
        if (AWTUtilitiesValidator.getInstance().isTranslucencySupported()) {
            pack();
            AWTUtilitiesValidator.getInstance().setWindowOpaque(this, false);
        }
    }

    public void paint(Graphics g) {
        if (AWTUtilitiesValidator.getInstance().isAWTUtlitiesAvailable()) {
            if (getExtendedState() == JFrame.MAXIMIZED_BOTH) {
                AWTUtilitiesValidator.getInstance().setWindowShape(this, null);
            } else {
                Area shape = new Area();
                Area shapeTop = new Area(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 13, 13));
                shape.add(shapeTop);
                AWTUtilitiesValidator.getInstance().setWindowShape(this, shape);
            }
        }
        super.paint(g);

    }

    private void configure() {
        List<Integer> confErrorStack = Configurator.runConfigurator();

        config = Configurator.getInstance();

        Font tabFont = new Font("Arial", Font.PLAIN, 11);

        configView = new GUIConfigView(this, true);
        if (!confErrorStack.isEmpty()) {
            for (int errorCode : confErrorStack) {
                if (errorCode == -301) {
                    //-107
                    //Bei einkommentierung werden keine listener gesetzt im rootpane (programm kann nicht verschoben werden etc)
//                    DecoratedJOptionPane.showMessageDialog(null, LanguageFactory.getText(LanguageFactory.GUIPDFVIEW_AUTOCONF_ERROR_MANUAL_CPPATH), LanguageFactory.getText(LanguageFactory.INFO_HL), JOptionPane.INFORMATION_MESSAGE);
                }
                if (errorCode == -302) {
                    //-108
//                    DecoratedJOptionPane.showMessageDialog(null, LanguageFactory.getText(LanguageFactory.GUIPDFVIEW_AUTOCONF_ERROR_MANUAL_SCREADER), LanguageFactory.getText(LanguageFactory.INFO_HL), JOptionPane.INFORMATION_MESSAGE);
                }
            }

        }
    }

    private void initActionComponents() {
        try {
            setBackground(LAFProperties.getInstance().getBorderColor());
            RootGlassPane glassPane = new RootGlassPane(this);
            getRootPane().setGlassPane(glassPane);

            pdfViewer = new JPodPDFViewer();
            pdfView = pdfViewer.getContentPanel();

            config.setContentPanel(pdfView);
            pdfView.setBorder(null);
            attachmentBar = new GUIAttachmentBarList(pdfViewer);
            config.setAttachmentBar(attachmentBar);
            int toolbarIconX = 16;
            int toolbarIconY = 16;
            int flagIconX = 16;
            int flagIconY = 11;
            int signButtonX = 92;
            int signButtonY = 22;
            toolbar = new JToolBar();
            ActionListenerEvents attachmentJustifyAction = new ActionListenerEvents("show attach", new ImageIcon(new ImageIcon(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/toolbar_attachment_light.png")).getImage().getScaledInstance(toolbarIconX, toolbarIconY, Image.SCALE_SMOOTH)), "tooltip : show attach", 'a', "showAttach", attachmentBar);
            ActionListenerEvents pageLeftJustifyAction = new ActionListenerEvents(null, new ImageIcon(new ImageIcon(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/toolbar_arrow_left.png")).getImage().getScaledInstance(toolbarIconX, toolbarIconY, Image.SCALE_SMOOTH)), LanguageFactory.getText(LanguageFactory.GUIPDFVIEW_TOOLTIP_BROWSE_LEFT), 'y', "page_left", pdfViewer);
            ActionListenerEvents pageRightJustifyAction = new ActionListenerEvents(null, new ImageIcon(new ImageIcon(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/toolbar_arrow_right.png")).getImage().getScaledInstance(toolbarIconX, toolbarIconY, Image.SCALE_SMOOTH)), LanguageFactory.getText(LanguageFactory.GUIPDFVIEW_TOOLTIP_BROWSE_RIGHT), 'x', "page_right", pdfViewer);
            ActionListenerEvents saveDocJustifyAction = new ActionListenerEvents(null, new ImageIcon(new ImageIcon(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/toolbar_savedoc.png")).getImage().getScaledInstance(toolbarIconX, toolbarIconY, Image.SCALE_SMOOTH)), LanguageFactory.getText(LanguageFactory.GUIPDFVIEW_TOOLTIP_SAVE_SEEN_PDF_LOKAL), null, "save_doc", null);
            ActionListenerEvents openConfigMenu = new ActionListenerEvents(null, new ImageIcon(new ImageIcon(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/toolbar_config.png")).getImage().getScaledInstance(toolbarIconX, toolbarIconY, Image.SCALE_SMOOTH)), LanguageFactory.getText(LanguageFactory.GUIPDFVIEW_TOOLTIP_OPEN_CONFIG), 'C', "open_config", configView);
            ImageIcon gerIcon = new ImageIcon(new ImageIcon(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/menubar_lang_ger.png")).getImage().getScaledInstance(flagIconX, flagIconY, Image.SCALE_SMOOTH));

            pageRightJustifyAction.setOptObjetcts("toolbar", toolbar);
            pageRightJustifyAction.setOptObjetcts("frame", this);
            toolbar.setFloatable(false);

            JButton jbut_Attachment = new JButton(attachmentJustifyAction);
            jbut_Attachment.setBorderPainted(false);
            jbut_Attachment.setText(null);

            UIManager.put("Button.background", new Color(0, 0, 0, 0));

            JButton jbut_PageLeft = new JButton(pageLeftJustifyAction);
            jbut_PageLeft.setOpaque(false);
            jbut_PageLeft.setBorderPainted(false);
            LanguageFactory.registerComponent(LanguageFactory.GUIPDFVIEW_TOOLTIP_BROWSE_LEFT, jbut_PageLeft);

            JButton jbut_PageRight = new JButton(pageRightJustifyAction);
            jbut_PageRight.setOpaque(false);
            jbut_PageRight.setBorderPainted(false);
            LanguageFactory.registerComponent(LanguageFactory.GUIPDFVIEW_TOOLTIP_BROWSE_RIGHT, jbut_PageRight);

            final JToggleButton jbut_FitToSize = new JToggleButton("", true);
            jbut_FitToSize.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/fittosizebtnico.png")).getImage().getScaledInstance(toolbarIconX, toolbarIconY, Image.SCALE_SMOOTH)));
            jbut_FitToSize.setToolTipText(LanguageFactory.getText(LanguageFactory.GUIPDFVIEW_TOOLTIP_FIT_ZOOM_TO_SIZE));
            jbut_FitToSize.setOpaque(false);
            jbut_FitToSize.setBorderPainted(false);
            LanguageFactory.registerComponent(LanguageFactory.GUIPDFVIEW_TOOLTIP_FIT_ZOOM_TO_SIZE, jbut_FitToSize);
            jbut_FitToSize.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    ButtonModel m = jbut_FitToSize.getModel();
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        pdfViewer.setFitToSize(true);
                        pdfViewer.fitPagesInView();
                    } else {
                        pdfViewer.setFitToSize(false);
                    }
                }
            });

            JButton jbut_ZoomIn = new JButton();
            jbut_ZoomIn.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/toolbar_zoom_in.png")).getImage().getScaledInstance(toolbarIconX, toolbarIconY, Image.SCALE_SMOOTH)));
            jbut_ZoomIn.setToolTipText(LanguageFactory.getText(LanguageFactory.GUIPDFVIEW_TOOLTIP_ZOOM_IN));
            jbut_ZoomIn.setOpaque(false);
            jbut_ZoomIn.setBorderPainted(false);
            LanguageFactory.registerComponent(LanguageFactory.GUIPDFVIEW_TOOLTIP_ZOOM_IN, jbut_ZoomIn);
            jbut_ZoomIn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (config.getPDFFile() != null) {
                        pdfViewer.scale(pdfViewer.getCurrentScale() + 0.1d);
                        pdfViewer.setFitToSize(false);
                        jbut_FitToSize.setSelected(false);  //causes disabling of fit-to-size in togglebutton's itemlistener
                    }
                }
            });

            JButton jbut_ZoomOut = new JButton();
            jbut_ZoomOut.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/toolbar_zoom_out.png")).getImage().getScaledInstance(toolbarIconX, toolbarIconY, Image.SCALE_SMOOTH)));
            jbut_ZoomOut.setToolTipText(LanguageFactory.getText(LanguageFactory.GUIPDFVIEW_TOOLTIP_ZOOM_OUT));
            jbut_ZoomOut.setOpaque(false);
            jbut_ZoomOut.setBorderPainted(false);
            LanguageFactory.registerComponent(LanguageFactory.GUIPDFVIEW_TOOLTIP_ZOOM_OUT, jbut_ZoomOut);
            jbut_ZoomOut.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (config.getPDFFile() != null) {
                        pdfViewer.scale(pdfViewer.getCurrentScale() - 0.1d);
                        pdfViewer.setFitToSize(false);
                        jbut_FitToSize.setSelected(false);  //causes disabling of fit-to-size in togglebutton's itemlistener
                    }
                }
            });

            JButton jbut_OpenConfig = new JButton(openConfigMenu);
            jbut_OpenConfig.setOpaque(false);
            jbut_OpenConfig.setBorderPainted(false);
            LanguageFactory.registerComponent(LanguageFactory.GUIPDFVIEW_TOOLTIP_OPEN_CONFIG, jbut_OpenConfig);

            JButton jbut_SaveDoc = new JButton(saveDocJustifyAction);
            jbut_SaveDoc.setOpaque(false);
            jbut_SaveDoc.setBorderPainted(false);
            LanguageFactory.registerComponent(LanguageFactory.GUIPDFVIEW_TOOLTIP_SAVE_SEEN_PDF_LOKAL, jbut_SaveDoc);

            ToolBarPopDownButton jbut_SignValid = new ToolBarPopDownButton();
            jbut_SignValid.setPopupUI(new BasicPopupMenuUI() {
                @Override
                public void installUI(JComponent c) {
                    super.installUI(c);
                    c.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
                }
            });
            jbut_SignValid.setToolTipText(LanguageFactory.getText(LanguageFactory.GUIPDFVIEW_TOOLTIP_SIGNATURES));
            LanguageFactory.registerComponent(LanguageFactory.GUIPDFVIEW_TOOLTIP_SIGNATURES, jbut_SignValid);
            SignatureValidPanel signatureValidPanel = new SignatureValidPanel(jbut_SignValid);
            GUISignatureView signView = new GUISignatureView();
            config.setSignatureView(signView);
            signatureValidPanel.setSignaturesPanel(signView);
            signatureValidPanel.verifySignature(); //used to set initial values of the this panel.at this point no pdf is loaded. so initial value is "unknown pdf"
            LanguageFactory.registerComponent(LanguageFactory.TOOLBARPOPDOWNBUTTON_DOCUMENT_VALID, signatureValidPanel);
            config.setSignatureValidPanel(signatureValidPanel);
            signatureValidPanel.setToolTipText("Klicken Sie auf diese Ansicht um die Zertifikatsdetails einzusehen");
            jbut_SignValid.addToPopup(signatureValidPanel);
            jbut_SignValid.setPopupFlushedLeft(false);
            jbut_SignValid.setUI(new BasicButtonUI() {
                private Color textHlColor = new Color(255, 255, 255, 80);
                private Color textColor = new Color(40, 40, 40);

                @Override
                protected void paintButtonPressed(Graphics g, AbstractButton b) {
                }

                @Override
                protected void paintText(Graphics g, JComponent c, Rectangle textRect, String text) {
                    AbstractButton b = (AbstractButton) c;
                    ButtonModel model = b.getModel();
                    FontMetrics fm = SwingUtilities2.getFontMetrics(c, g);
                    int mnemonicIndex = b.getDisplayedMnemonicIndex();

                    g.setColor(textHlColor);
                    SwingUtilities2.drawStringUnderlineCharAt(c, g, text, mnemonicIndex, textRect.x + getTextShiftOffset() + 1, textRect.y + fm.getAscent() + getTextShiftOffset() + 1);

                    g.setColor(textColor);
                    SwingUtilities2.drawStringUnderlineCharAt(c, g, text, mnemonicIndex, textRect.x + getTextShiftOffset(), textRect.y + fm.getAscent() + getTextShiftOffset());
                }
            });

            final ToolBarPopDownButton langBtn = new ToolBarPopDownButton();
            langBtn.setToolTipText(LanguageFactory.getText(LanguageFactory.GUIPDFVIEW_TOOLTIP_SET_LANGUAGE));
            LanguageFactory.registerComponent(LanguageFactory.GUIPDFVIEW_TOOLTIP_SET_LANGUAGE, langBtn);
            langBtn.setText(null);
            langBtn.setIcon(LanguageFactory.getLanguage().getIcon());

            Language[] langs = LanguageFactory.getAllLanguages();

            JMenuItem item = null;
            for (Language lang : langs) {
                item = new JMenuItem(lang.getLanguageInfo());
                item.setName(lang.getLanguageInfo());
                item.setIcon(lang.getIcon());
                item.addMouseListener(new LanguageItemListener(lang, langBtn));
                langBtn.addToPopup(item);
            }
//            langBtn.addSeparatorToPopup();
//            JMenuItem addLangItem = new JMenuItem("Sprache hinzufügen ...");
//            langBtn.addToPopup(addLangItem);


            pages = new JTextField();
            pdfViewer.addPageChangedListener(new PageChangedListener() {
                @Override
                public void pageHasChanged(int pageNmbr) {
                    try {
                        pages.setText((pageNmbr + 1) + "/" + pdfViewer.getPageCount());
                    } catch (IOException ex) {
                        log.error("Cannot display selected page in the pages textfield: " + ex.getMessage());
                    }
                }
            });
            config.setPagesTextField(pages);
            pages.setDocument(new MaxDocSigns(11, MaxDocSigns.EVERY_SIGN));
            Dimension pagesDim = new Dimension(70, 23);
            pages.setMaximumSize(pagesDim);
            pages.setPreferredSize(pagesDim);
            pages.setHorizontalAlignment(JTextField.CENTER);
            pages.setFont(new Font(null, Font.PLAIN, 11));
            pages.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent arg0) {
                    JTextField pages = (JTextField) arg0.getSource();
                    if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
                        if (pages.getText().matches("[0-9]{1,}+[ ]{0,}+[/]{0,}+[ ]{0,}+[0-9]{0,}")) {
                            try {
                                Pattern getPageExp = Pattern.compile("[0-9]+");
                                Matcher matcher = getPageExp.matcher(pages.getText());
                                matcher.find(0);
                                Integer page = Integer.valueOf(pages.getText().substring(matcher.start(), matcher.end()));

                                if (page <= pdfViewer.getPageCount() && page >= 1 && !config.getSignAndStampablePagePanel().isDocFixed()) {
                                    pdfViewer.showPage(page);
                                    log.info("Changed to page '" + page + "'");
                                } else {
                                    pages.setText(pdfViewer.getCurrentPageNum() + "/" + pdfViewer.getPageCount());
                                    log.info("Change to page '" + page + "' suppressed");
                                }
                            } catch (Exception ex) {
                                log.warn(ex);
                            }
                        } else {
                            try {
                                pages.setText(pdfViewer.getCurrentPageNum() + "/" + pdfViewer.getPageCount());
                            } catch (Exception ex) {
                                log.warn(ex);
                            }
                        }
                    }
                }
            });

            scaleFactorTxt = new JTextField("100%");
            config.setScaleFactorTextField(scaleFactorTxt);
            scaleFactorTxt.setDocument(new MaxDocSigns(4, MaxDocSigns.EVERY_SIGN));
            Dimension scaleFactorTxtDim = new Dimension(45, 23);
            scaleFactorTxt.setMaximumSize(scaleFactorTxtDim);
            scaleFactorTxt.setPreferredSize(scaleFactorTxtDim);
            scaleFactorTxt.setHorizontalAlignment(JTextField.CENTER);
            scaleFactorTxt.setFont(new Font(null, Font.PLAIN, 11));
            scaleFactorTxt.addActionListener(new ActionListener() {
                private Pattern getScaleExp = Pattern.compile("[0-9]+");

                @Override
                public void actionPerformed(ActionEvent e) {
                    scaleFactorTxt.setText(scaleFactorTxt.getText().replaceAll(" ", ""));
                    if (scaleFactorTxt.getText().matches("\\d{1,3}%?")) {
                        Matcher matcher = getScaleExp.matcher(scaleFactorTxt.getText());
                        matcher.find(0);
                        double newScale = Integer.valueOf(scaleFactorTxt.getText().substring(matcher.start(), matcher.end()));
                        pdfViewer.scale(newScale / 100d);
                        jbut_FitToSize.setSelected(false);
                    } else {
                        scaleFactorTxt.setText(Long.toString(Math.round(pdfViewer.getCurrentScale() * 100d)) + "%");
                    }
                }
            });

            pdfViewer.addScaleChangedListener(new ScaleChangedListener() {
                @Override
                public void scaleHasChanged(int scaleFactor) {
                    scaleFactorTxt.setText(scaleFactor + "%");
                }
            });

            jbut_PageLeft.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            jbut_PageLeft.setMargin(new Insets(7, 7, 7, 7));

            jbut_PageRight.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            jbut_PageRight.setMargin(new Insets(7, 7, 7, 7));

            jbut_ZoomIn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            jbut_ZoomIn.setMargin(new Insets(7, 7, 7, 7));

            jbut_FitToSize.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            jbut_FitToSize.setMargin(new Insets(7, 7, 7, 7));

            jbut_ZoomOut.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            jbut_ZoomOut.setMargin(new Insets(7, 7, 7, 7));

            jbut_OpenConfig.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            jbut_OpenConfig.setMargin(new Insets(7, 7, 7, 7));

            langBtn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            langBtn.setMargin(new Insets(7, 7, 7, 7));

            jbut_Attachment.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            jbut_Attachment.setMargin(new Insets(7, 7, 7, 7));

            final AboutDialog infoDiag = new AboutDialog(this);

            JButton aboutBtn = new JButton();
            aboutBtn.setOpaque(false);
            aboutBtn.setBorderPainted(false);
            ImageIcon aboutIcon = new ImageIcon(new ImageIcon(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/info.png")).getImage().getScaledInstance(toolbarIconX, toolbarIconY, Image.SCALE_SMOOTH));
            aboutBtn.setIcon(aboutIcon);
            aboutBtn.setToolTipText("Über ...");
            aboutBtn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    infoDiag.pack();
                    infoDiag.setVisible(true);
                }
            });

            toolbar.add(jbut_PageLeft);
            toolbar.add(pages);
            toolbar.add(jbut_PageRight);
            toolbar.addSeparator();
            toolbar.add(jbut_ZoomIn);
            toolbar.add(scaleFactorTxt);
            toolbar.add(jbut_ZoomOut);
            toolbar.add(jbut_FitToSize);
            toolbar.addSeparator();
            toolbar.add(jbut_OpenConfig);
            toolbar.add(langBtn);
            toolbar.addSeparator();
            toolbar.add(jbut_Attachment);
            toolbar.addSeparator();
            toolbar.add(aboutBtn);
            toolbar.add(Box.createHorizontalGlue());
            toolbar.add(jbut_SignValid);

            JPanel appHeadPanel = new JPanel();
            appHeadPanel.setLayout(new BorderLayout());
            mdv = new MultiDocumentViewer(this);
            mdv.setContentVisible(false);
            MultiDocumentThumb[] mdts = ParamValidator.getInstance().getSourceContent().toArray(new MultiDocumentThumb[0]);
            for (MultiDocumentThumb mdt : mdts) {
                mdv.addDocument(mdt);
            }

            appHeadPanel.add(mdv.getContentPanel(), BorderLayout.CENTER);
            appHeadPanel.add(toolbar, BorderLayout.SOUTH);
            this.add(appHeadPanel, BorderLayout.PAGE_START);
//            toolbar.add(jbut_SaveDoc);        //Speichert geladenes, unsigniertes dokument lokal - noch nicht verfügbar


//            menubar = new JMenuBar();
//            menubar.setBorder(BorderFactory.createEmptyBorder());
//            JMenu fileMenu = new JMenu();

//            fileMenu.setText("Datei");
//            fileMenu.add(doSignJustifyAction).setIcon(null);
//            fileMenu.add(saveDocJustifyAction).setIcon(null);
//            fileMenu.addSeparator();
//            fileMenu.add(close);

//            JMenu langList = new JMenu();
//            langList.setText("Sprachen");
//            langList.add(germanLangAction);
//            langList.add(englishLangAction);

//            JMenu configMenu = new JMenu();
//            configMenu.setText("Konfiguration");
//            configMenu.add(openConfigMenu);

//            JMenu uberMenu = new JMenu();
//            uberMenu.setText("Über");

//            menubar.add(fileMenu);
//            menubar.add(configMenu);
//            menubar.add(langList);
//            menubar.add(uberMenu);


//            this.setJMenuBar(menubar);


//            this.add(toolbar, BorderLayout.PAGE_START); //commented because of multidocumentviewer (this toolbar must be placed in another jpanel with the viewer above.
        } catch (Exception ex) {
            log.error(ex, ex);
            DecoratedJOptionPane.showMessageDialog(this, LanguageFactory.getText(LanguageFactory.GUIPDFVIEW_ERROR_CREATING_GUI), LanguageFactory.getText(LanguageFactory.ERROR_HL), JOptionPane.ERROR_MESSAGE);
            Configurator.closeProgram("-942");
        }
    }

    private void getPDFDocument() {
        ParamValidator pv = ParamValidator.getInstance();
        if (pv.getSourceContent() != null) {
            try {
                log.info("Document loaded from base64");
            } catch (Exception ex) {
                log.fatal(ex);
                DecoratedJOptionPane.showMessageDialog(this, LanguageFactory.getText(LanguageFactory.GUIPDFVIEW_ERROR_DECODING_B64_PDF), LanguageFactory.getText(LanguageFactory.ERROR_HL), JOptionPane.ERROR_MESSAGE);
                Configurator.closeProgram("-920");
            }
//        } else if (pv.getSourceUrl() != null) {
//            downloadPDF(ParamValidator.getInstance().getSourceUrl());
//            log.info("Document loaded from URL");
//        } else if (pv.getSourceLocal() != null) {
//            try {
//                File localPDF = ParamValidator.getInstance().getSourceLocal();
//                loadedDocuments = new byte[(int) localPDF.length()];
//                FileInputStream fis = new FileInputStream(localPDF);
//                fis.read(loadedDocuments);
//                fis.close();
//                log.info("Document loaded from local file");
//            } catch (Exception e) {
//                log.fatal(e);
//                DecoratedJOptionPane.showMessageDialog(this, LanguageFactory.getText(LanguageFactory.GUIPDFVIEW_ERROR_LOADING_LOKAL_PDF), LanguageFactory.getText(LanguageFactory.GUIPDFVIEW_ERROR_LOADING_LOKAL_PDF), JOptionPane.ERROR_MESSAGE);
//                Configurator.closeProgram("921");
//            }
        }
//        config.setPDFFile(loadedDocuments);
    }

    private void downloadPDF(URL pdfSourceURL) {
        IDocumentHandler connector = new DocURLConnector();
        connector.setDestURL(pdfSourceURL);
        connector.setAction(DocURLConnector.DOCUMENT_TO_BYTEBUFFER);
        connector.start();
        int progressBuffer = splashScreen.getPercent();
        while (connector.isRunning()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                splashScreen.close();
                log.fatal(ex);
                DecoratedJOptionPane.showMessageDialog(this, LanguageFactory.getText(LanguageFactory.GUIPDFVIEW_ERROR_IN_DOWNLOAD_MODULE), LanguageFactory.getText(LanguageFactory.ERROR_HL), JOptionPane.ERROR_MESSAGE);
                Configurator.closeProgram("-943");
            }
            int percent = connector.getProgressInPercent();
            if (percent < 0) {
                percent = 0;
            } else if (percent > 100) {
                percent = 100;
            }
            splashScreen.setPercentBar(progressBuffer + (connector.getProgressInPercent() / 2));
        }
        try {
            config.getCardHandler().unload();
            connector.throwExceptions();
//            loadedDocuments = connector.getPDFInByteBuffer().toByteArray();
        } catch (CardAccessorException e) {
            splashScreen.close();
            log.error(e);
        } catch (SocketTimeoutException ex) {
            splashScreen.close();
            log.error(ex);
            DecoratedJOptionPane.showMessageDialog(this, LanguageFactory.getText(LanguageFactory.GUIPDFVIEW_PDF_SERVER_NOT_REACHABLE), LanguageFactory.getText(LanguageFactory.ERROR_HL), JOptionPane.ERROR_MESSAGE);
            Configurator.closeProgram("-922");
        } catch (Exception ex) {
            splashScreen.close();
            log.error(ex);
            DecoratedJOptionPane.showMessageDialog(this, LanguageFactory.getText(LanguageFactory.GUIPDFVIEW_UNKNOWN_PDF_DOWNLOAD_ERROR), LanguageFactory.getText(LanguageFactory.ERROR_HL), JOptionPane.ERROR_MESSAGE);
            Configurator.closeProgram("-923");
        }
    }

    private void loadPDF() {
        try {
            pdfView.addContainerListener(new ContainerAdapter() {
                @Override
                public void componentAdded(ContainerEvent arg0) {
                    try {
                        pages.setText(pdfViewer.getCurrentPageNum() + "/" + pdfViewer.getPageCount());
                    } catch (IOException ex) {
                        log.error(ex);
                    }
                }
            });
//
//            scrollpane.setViewportView(contentPanel);
//            scrollpane.getViewport().setBackground(LAFProperties.getInstance().getPdfBackGroundColor());
//            scrollpane.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
        } catch (Exception ex) {
            log.error(ex);
            DecoratedJOptionPane.showMessageDialog(this, LanguageFactory.getText(LanguageFactory.GUIPDFVIEW_CORRUPT_PDF), LanguageFactory.getText(LanguageFactory.ERROR_HL), JOptionPane.ERROR_MESSAGE);
            Configurator.closeProgram("-924");
        }
    }

    private void initSignPanel() {
//        if (config.isPdfValid()) {
        signPanel = new GUISignView();
        config.setGUISignView(signPanel);
        signPanel.setVisible(true);
//        }
    }
    private JPanel contentContainer;

    public JPanel getContentPanel() {
        return contentContainer;
    }

    private void combineViews() {
        RSyntaxTextArea sta = attachmentBar.getSyntaxTextArea();


        List<JPanel> barViewControl = new ArrayList<JPanel>();

        attachmentBar.setVisible(false);

        tabs = new JTabbedPane() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                g.setColor(new Color(255, 255, 255, 40));
                g.drawLine(0, 0, 0, getHeight());
            }
        };

        JScrollPane xmlViewScroller = new JScrollPane();
        xmlViewScroller.setBorder(BorderFactory.createEmptyBorder());
        xmlViewScroller.setViewportView(sta);

        String hl0 = "UNKNOWN";
        String hl1 = "UNKNOWN";
        try {
            hl0 = LanguageFactory.getText(LanguageFactory.GUIPDFVIEW_PDF_VIEW_JTABPANE).split("=")[1];
        } catch (Exception e) {
            log.warn("JTabbedPane headline is not valid. Must be in following format: tabIndex=tabHeadline (e.g.: 0=Cardreaders)");
        }
        try {
            hl1 = LanguageFactory.getText(LanguageFactory.GUIPDFVIEW_XML_VIEW_JTABPANE).split("=")[1];
        } catch (Exception e) {
            log.warn("JTabbedPane headline is not valid. Must be in following format: tabIndex=tabHeadline (e.g.: 0=Cardreaders)");
        }
        tabs.addTab(hl0, pdfView);
        tabs.addTab(hl1, xmlViewScroller);
        LanguageFactory.registerComponent(LanguageFactory.GUIPDFVIEW_PDF_VIEW_JTABPANE, tabs);
        LanguageFactory.registerComponent(LanguageFactory.GUIPDFVIEW_XML_VIEW_JTABPANE, tabs);
        tabs.setIconAt(0, new ImageIcon(new ImageIcon(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/ico_pdf.png")).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
        tabs.setIconAt(1, new ImageIcon(new ImageIcon(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/ico_xml.png")).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
        JPanel contCont = new JPanel();
        contCont.setLayout(new BorderLayout());
        contCont.setOpaque(false);
        contCont.add(configView, BorderLayout.NORTH);
        contCont.add(tabs, BorderLayout.CENTER);

//        final GUISignatureView signView = new GUISignatureView();
//        config.setSignatureView(signView);
//        signView.setVisible(true);

        contentContainer = new JPanel();
        contentContainer.setBackground(LAFProperties.getInstance().getMainFrameBackgroundColor());
        contentContainer.setBorder(null);
        contentContainer.setLayout(new BorderLayout());
        contentContainer.add(contCont, BorderLayout.CENTER);                                                            //HERE
        contCont.add(attachmentBar, BorderLayout.SOUTH);
        if (ParamValidator.getInstance().getApplicationMode() == ParamValidator.ATTACH_USER_DATA_TO_PDF_MODE) {
            contentContainer.add(new PDFXMLEnricher(), BorderLayout.EAST);
        } else if (ParamValidator.getInstance().getApplicationMode() == ParamValidator.SIGNATURE_MODE) {
            contentContainer.add(signPanel, BorderLayout.EAST);
        }
        getContentPane().setBackground(new Color(75, 75, 75));
        add(contentContainer, BorderLayout.CENTER);

        JPanel smallAttachBarContainer = new JPanel();
        smallAttachBarContainer.setLayout(new BorderLayout());
        smallAttachBarContainer.setBorder(BorderFactory.createEmptyBorder());



        SmallAttachmentBar smallAttachBar = new SmallAttachmentBar();
        smallAttachBar.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        Dimension smallAttachBarDim = new Dimension(35, 35);
        smallAttachBar.setPreferredSize(smallAttachBarDim);
        smallAttachBar.setSize(smallAttachBarDim);

        attachB = new AttachmentBarButton();
        attachmentBar.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                attachB.setActive(true);
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                attachB.setActive(false);
            }
        });
        Dimension buttonDimension = new Dimension(smallAttachBar.getSize().width, smallAttachBar.getSize().height);
        attachB.setPreferredSize(buttonDimension);
        attachB.setSize(buttonDimension);
        attachB.setToolTipText(LanguageFactory.getText(LanguageFactory.GUIPDFVIEW_TOOLTIP_ATTACHMENTS));
        LanguageFactory.registerComponent(LanguageFactory.GUIPDFVIEW_TOOLTIP_ATTACHMENTS, attachB);
        Image[] attachBIcon = {
            new ImageIcon(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/toolbar_attachment_dark.png")).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH),
            new ImageIcon(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/toolbar_attachment_light.png")).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)
        };
        attachB.setIcons(attachBIcon);
        attachB.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (attachmentBar.isVisible()) {
                    attachmentBar.setVisible(false);
                } else {
                    attachmentBar.setVisible(true);

                }
            }
        });

        expandSignatureButton = new AttachmentBarButton();
        Image[] expandSignatureButtonIcon = {
            new ImageIcon(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/toolbar_signature_dark.png")).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH),
            new ImageIcon(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/toolbar_signature_light.png")).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)
        };
        expandSignatureButton.setPreferredSize(buttonDimension);
        expandSignatureButton.setSize(buttonDimension);
        expandSignatureButton.setIcons(expandSignatureButtonIcon);
//        expandSignatureButton.setToolTipText(LanguageFactory.getText(LanguageFactory.GUIPDFVIEW_TOOLTIP_SIGNATURES));
//        LanguageFactory.registerComponent(LanguageFactory.GUIPDFVIEW_TOOLTIP_SIGNATURES, expandSignatureButton);
//        expandSignatureButton.addMouseListener(new MouseAdapter() {
//
//            public void mouseClicked(MouseEvent e) {
//                if (signView.isVisible()) {
//                    signView.setVisible(false);
//                } else {
//                    signView.setVisible(true);
//                }
//            }
//        });


        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(5, 0, 5, 0);
//        smallAttachBar.add(expandSignatureButton, gbc);
        gbc.gridy = 1;
        gbc.weighty = 0;
        smallAttachBar.add(attachB, gbc);

//        barViewControl.add(attachmentBar);
//        barViewControl.add(signView);
//
//
//        signView.setBarViewController(barViewControl);
        attachmentBar.setBarViewController(barViewControl);
//        signView.setVisible(false);         //must be called after defining the component listener

//        smallAttachBarContainer.add(smallAttachBar, BorderLayout.WEST);
//        mdv.getMultiDocumentThumbController().showDocument(0);
        add(smallAttachBarContainer, BorderLayout.WEST);

        try {
            log.info("Calculating windowposition and -size ...");
            setLocation(WindowTools.getCenterPoint(0.9f, 0.75f, 0));
            setPreferredSize(WindowTools.getWindowSizeFromPercent(0.9f, 0.75f, 0));
            log.info("Calculating windowposition and -size ...");
        } catch (Exception e) {
            log.error("Error on calculating windowposition and -size - " + e.getMessage() + "Fallback to 800x500px and 0,0 location");
            Dimension fallbackWindowDim = new Dimension(800, 500);
            setLocation(0, 0);
            setPreferredSize(fallbackWindowDim);
            setSize(fallbackWindowDim);
        }
        signPanel.setCParent(this);

        //init titlebar components

        //init decorator buttons
        Dimension decoBtnDim = new Dimension(9, 9);
        Dimension btnImageDim = new Dimension(9, 9);
        Color btnForeColorClose = new Color(75, 75, 75);
        Color btnForeColorAll = new Color(0, 80, 230);
        Color btnForeColorOver = new Color(210, 0, 0);
        Color hlColor = new Color(255, 255, 255, 175);
        //close button icon
        BufferedImage closeBtnIco = new BufferedImage(btnImageDim.width, btnImageDim.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D cbIcog2 = closeBtnIco.createGraphics();
        cbIcog2.setColor(hlColor);
        cbIcog2.drawLine(1, 1, closeBtnIco.getWidth() - 1, closeBtnIco.getHeight() - 1);    //-1 musst be used. or the point is one pixel outside of the visible area
        cbIcog2.drawLine(closeBtnIco.getWidth() - 1, 1, 1, closeBtnIco.getHeight() - 1);
        cbIcog2.setColor(btnForeColorClose);
        cbIcog2.drawLine(1, 1, closeBtnIco.getWidth() - 2, closeBtnIco.getHeight() - 2);
        cbIcog2.drawLine(closeBtnIco.getWidth() - 2, 1, 1, closeBtnIco.getHeight() - 2);

        BufferedImage closeBtnOver = new BufferedImage(btnImageDim.width, btnImageDim.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D cbOverg2 = closeBtnOver.createGraphics();
        cbOverg2.setColor(hlColor);
        cbOverg2.drawLine(1, 1, closeBtnOver.getWidth() - 1, closeBtnOver.getHeight() - 1);    //-1 musst be used. or the point is one pixel outside of the visible area
        cbOverg2.drawLine(closeBtnOver.getWidth() - 1, 1, 1, closeBtnOver.getHeight() - 1);
        cbOverg2.setColor(btnForeColorOver);
        cbOverg2.drawLine(1, 1, closeBtnOver.getWidth() - 2, closeBtnOver.getHeight() - 2);
        cbOverg2.drawLine(closeBtnOver.getWidth() - 2, 1, 1, closeBtnOver.getHeight() - 2);


        BufferedImage closeBtnPressed = new BufferedImage(btnImageDim.width, btnImageDim.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D cbPressedg2 = closeBtnPressed.createGraphics();
        cbPressedg2.setColor(btnForeColorOver);
        cbPressedg2.drawLine(2, 2, closeBtnPressed.getWidth() - 2, closeBtnPressed.getHeight() - 2);
        cbPressedg2.drawLine(closeBtnPressed.getWidth() - 1, 2, 2, closeBtnPressed.getHeight() - 1);

        DecoratorButton closeBtn = new DecoratorButton();
        closeBtn.setCompImage(new ImageIcon(closeBtnIco));
        closeBtn.setMouseOverImage(new ImageIcon(closeBtnOver));
        closeBtn.setMousePressedImage(new ImageIcon(closeBtnPressed));
        closeBtn.setPreferredSize(decoBtnDim);
        closeBtn.setSize(decoBtnDim);
        closeBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                Configurator.getInstance().closeProgram("-900");
            }
        });

        //maxbutton
        BufferedImage maxBtnIco = new BufferedImage(btnImageDim.width, btnImageDim.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D mbIcog2 = maxBtnIco.createGraphics();
        mbIcog2.setColor(hlColor);
        mbIcog2.drawRect(2, 2, maxBtnIco.getWidth() - 3, maxBtnIco.getHeight() - 3);
        mbIcog2.drawLine(2, 3, maxBtnIco.getWidth() - 2, 3);
        mbIcog2.setColor(btnForeColorClose);
        mbIcog2.drawRect(1, 1, maxBtnIco.getWidth() - 3, maxBtnIco.getHeight() - 3);
        mbIcog2.drawLine(1, 2, maxBtnIco.getWidth() - 3, 2);

        BufferedImage maxBtnOver = new BufferedImage(btnImageDim.width, btnImageDim.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D mbOverg2 = maxBtnOver.createGraphics();
        mbOverg2.setColor(hlColor);
        mbOverg2.drawRect(2, 2, maxBtnOver.getWidth() - 3, maxBtnOver.getHeight() - 3);
        mbOverg2.drawLine(2, 3, maxBtnOver.getWidth() - 2, 3);
        mbOverg2.setColor(btnForeColorAll);
        mbOverg2.drawRect(1, 1, maxBtnOver.getWidth() - 3, maxBtnOver.getHeight() - 3);
        mbOverg2.drawLine(1, 2, maxBtnOver.getWidth() - 3, 2);

        BufferedImage maxBtnPressed = new BufferedImage(btnImageDim.width, btnImageDim.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D mbPressedg2 = maxBtnPressed.createGraphics();
        mbPressedg2.setColor(btnForeColorAll);
        mbPressedg2.drawRect(2, 2, maxBtnPressed.getWidth() - 3, maxBtnPressed.getHeight() - 3);
        mbPressedg2.drawLine(2, 3, maxBtnPressed.getWidth() - 2, 3);

        DecoratorButton maxBtn = new DecoratorButton();
        maxBtn.setCompImage(new ImageIcon(maxBtnIco));
        maxBtn.setMouseOverImage(new ImageIcon(maxBtnOver));
        maxBtn.setMousePressedImage(new ImageIcon(maxBtnPressed));
        maxBtn.setPreferredSize(decoBtnDim);
        maxBtn.setSize(decoBtnDim);
        maxBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (getExtendedState() == Frame.MAXIMIZED_BOTH) {
                    setExtendedState(Frame.NORMAL);
                } else {
                    setExtendedState(Frame.MAXIMIZED_BOTH);
                }
            }
        });

        //min-button
        BufferedImage minBtnIco = new BufferedImage(btnImageDim.width, btnImageDim.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D minIcog2 = minBtnIco.createGraphics();
        minIcog2.setColor(hlColor);
        minIcog2.drawLine(2, minBtnIco.getHeight() - 1, minBtnIco.getWidth() - 2, minBtnIco.getHeight() - 1);
        minIcog2.setColor(btnForeColorClose);
        minIcog2.drawLine(1, minBtnIco.getHeight() - 2, minBtnIco.getWidth() - 3, minBtnIco.getHeight() - 2);

        BufferedImage minBtnOver = new BufferedImage(btnImageDim.width, btnImageDim.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D minOverg2 = minBtnOver.createGraphics();
        minOverg2.setColor(hlColor);
        minOverg2.drawLine(2, minBtnIco.getHeight() - 1, minBtnIco.getWidth() - 2, minBtnIco.getHeight() - 1);
        minOverg2.setColor(btnForeColorAll);
        minOverg2.drawLine(1, minBtnIco.getHeight() - 2, minBtnIco.getWidth() - 3, minBtnIco.getHeight() - 2);

        BufferedImage minBtnPressed = new BufferedImage(btnImageDim.width, btnImageDim.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D minPressed2 = minBtnPressed.createGraphics();
        minPressed2.setColor(btnForeColorAll);
        minPressed2.drawLine(2, minBtnIco.getHeight() - 1, minBtnIco.getWidth() - 2, minBtnIco.getHeight() - 1);

        DecoratorButton minBtn = new DecoratorButton();
        minBtn.setCompImage(new ImageIcon(minBtnIco));
        minBtn.setMouseOverImage(new ImageIcon(minBtnOver));
        minBtn.setMousePressedImage(new ImageIcon(minBtnPressed));
        minBtn.setPreferredSize(decoBtnDim);
        minBtn.setSize(decoBtnDim);
        minBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                setExtendedState(Frame.ICONIFIED);
            }
        });

        //init multidocument viewer-buttons
        pack();
        JFrameTitledComponentBorder fBorder = (JFrameTitledComponentBorder) getRootPane().getBorder();
        Insets btnInsets = new Insets(2, 2, 2, 2);
        Border btnBorder = BorderFactory.createEmptyBorder(4, 4, 4, 4);
        Dimension btnDim = new Dimension(21, 21);

        //create hide-button
        final ImageIcon arrowDown = new ImageIcon(mdv.createExpandDocViewImage());
        final ImageIcon arrowUp = new ImageIcon(mdv.createCollapseDocViewImage());
        final JButton hideBtn = new JButton();
        if (mdv.getContentPanel().isVisible()) {
            hideBtn.setIcon(arrowUp);
        } else {
            hideBtn.setIcon(arrowDown);
        }
        hideBtn.setPreferredSize(btnDim);
        hideBtn.setToolTipText("Anzeigen/Ausblenden des Dokumentenbetrachters");
        hideBtn.setBorder(btnBorder);
        hideBtn.setMargin(btnInsets);
        mdv.getContentPanel().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
                hideBtn.setIcon(arrowDown);
                rootPane.repaint();
            }

            @Override
            public void componentShown(ComponentEvent e) {
                hideBtn.setIcon(arrowUp);
                rootPane.repaint();
            }
        });
        hideBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (mdv.getContentPanel().isVisible()) {
                    mdv.getContentPanel().setVisible(false);
                } else {
                    mdv.getContentPanel().setVisible(true);
                }
            }
        });

        //create previous-doc button
        ImageIcon prevDocBtnIco = new ImageIcon(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/toolbar_arrow_left.png")).getScaledInstance(10, 10, Image.SCALE_SMOOTH));
        JButton prevDocBtn = new JButton();
        prevDocBtn.setIcon(prevDocBtnIco);
        prevDocBtn.setBorder(btnBorder);
        prevDocBtn.setMargin(btnInsets);
        prevDocBtn.setPreferredSize(btnDim);
        prevDocBtn.setToolTipText("Vorheriges Dokument öffnen");
        prevDocBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                mdv.getMultiDocumentThumbController().showPreviousDoc();
            }
        });

        //create next-doc button
        ImageIcon nextDocBtnIco = new ImageIcon(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/toolbar_arrow_right.png")).getScaledInstance(10, 10, Image.SCALE_SMOOTH));
        JButton nextDocBtn = new JButton();
        nextDocBtn.setIcon(nextDocBtnIco);
        nextDocBtn.setBorder(btnBorder);
        nextDocBtn.setMargin(btnInsets);
        nextDocBtn.setPreferredSize(btnDim);
        nextDocBtn.setToolTipText("Nächstes Dokument öffnen");
        nextDocBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                mdv.getMultiDocumentThumbController().showNextDoc();
            }
        });

        //create add-document-button
        ImageIcon addDocIco = new ImageIcon(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/toolbar_add_document.png")).getScaledInstance(10, 10, Image.SCALE_SMOOTH));
        JButton addDocBtn = new JButton();
        addDocBtn.setIcon(addDocIco);
        addDocBtn.setBorder(btnBorder);
        addDocBtn.setMargin(btnInsets);
        addDocBtn.setPreferredSize(btnDim);
        addDocBtn.setToolTipText("Dokumente laden");
        addDocBtn.addMouseListener(new MouseAdapter() {
            private ArrayList<String> discardedDocNames = null;
            private boolean running = false;

            public void mouseReleased(MouseEvent e) {
                DecoratedJFileChooser jc = new DecoratedJFileChooser();
                jc.setMultiSelectionEnabled(true);
                jc.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        if (f.getName().toLowerCase().endsWith(".pdf") || f.isDirectory()) {
                            return true;
                        } else {
                            return false;
                        }
                    }

                    @Override
                    public String getDescription() {
                        return "*.pdf";
                    }
                });

                discardedDocNames = new ArrayList<String>();
                int result = jc.showOpenDialog(null);
                if (result == DecoratedJFileChooser.APPROVE_OPTION) {

                    final File[] docs = jc.getSelectedFiles();
                    final RootGlassPane gp = (RootGlassPane) Configurator.getInstance().getPdfViewer().getGlassPane();
                    gp.setLoadingText("Lade Dokumente ...");
                    gp.setInfoText("");
                    gp.setViewTo(RootGlassPane.LOCKED);
                    Thread t_loadDocs = new Thread() {
                        @Override
                        public void run() {
                            running = true;
                            int i = 0;
                            for (File doc : docs) {
                                i++;
                                gp.setInfoText(doc.getName() + " (" + i + "/" + docs.length + ")");
                                try {
                                    if (mdv.getMultiDocumentThumbController().getMultiDocumentCount() >= 50) {
                                        DecoratedJOptionPane.showMessageDialog(null, "Die maximale Anzahl an geladenen Dokumenten wurde erreicht.\nAlle weiteren Dokumente wurden verworfen.", "Information", DecoratedJOptionPane.INFORMATION_MESSAGE);
                                        break;
                                    }
                                    FileInputStream fis = new FileInputStream(doc);
                                    byte[] bytes = new byte[(int) doc.length()];
                                    fis.read(bytes);
                                    fis.close();
                                    MultiDocumentThumb mdt = new MultiDocumentThumb();
                                    mdt.setDocBytes(bytes);
                                    mdt.setDocName(doc.getName());
                                    mdt.setThumbCtrl(mdv.getMultiDocumentThumbController());
                                    mdv.addDocument(mdt);
                                } catch (Exception ex) {
                                    log.error("Cannot load document \"" + doc.getName() + "\". Reason: " + ex.getMessage());
                                    discardedDocNames.add(doc.getName());
                                }
                            }
                            if (discardedDocNames.size() > 0) {
                                String discDocStr = "";
                                for (String curr : discardedDocNames) {
                                    discDocStr += "<b>&middot;</b>  " + curr + "<br>";
                                }
                                DecoratedJOptionPane.showMessageDialog(null, "<html>Einige Dokumente konnten nicht geladen werden:<br><br>" + discDocStr + "</html>", "Fehler", DecoratedJOptionPane.ERROR_MESSAGE);
                            }
                            if (mdv.getMultiDocumentThumbController().getCurrentDisplayedDocumentAsThumb() == null && mdv.getMultiDocumentThumbController().getMultiDocumentCount() > 0) {
                                mdv.getMultiDocumentThumbController().showDocument(0);
                            }
                            gp.setViewTo(RootGlassPane.UNLOCKED);
                            running = false;
                        }
                    };
                    t_loadDocs.start();
                }
            }
        });

        //create remove-document-button
        ImageIcon remDocIco = new ImageIcon(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/toolbar_rem_document.png")).getScaledInstance(10, 10, Image.SCALE_SMOOTH));
        JButton remDocBtn = new JButton();
        remDocBtn.setIcon(remDocIco);
        remDocBtn.setBorder(btnBorder);
        remDocBtn.setMargin(btnInsets);
        remDocBtn.setPreferredSize(btnDim);
        remDocBtn.setToolTipText("Dokumente entfernen");
        remDocBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                Component[] comps = mdv.getDocumentContainerPanel().getComponents();
                boolean moveToNextAvailableDoc = false;  // if a displayed document is removed, this boolean turns to true und forces to open a new document
                int displayedDocNmbrToRemove = 0; //will be set if moveToNextAvailableDoc becomes true. its used to determine the next nearest document.
                boolean minOneSelectedDoc = false;
                for (Component comp : comps) {
                    if (comp instanceof MultiDocumentThumb) {
                        MultiDocumentThumb mdt = (MultiDocumentThumb) comp;
                        if (mdt.isMarked()) {
                            minOneSelectedDoc = true;
                            if (mdt == mdv.getMultiDocumentThumbController().getCurrentDisplayedDocumentAsThumb()) {
                                moveToNextAvailableDoc = true;
                                displayedDocNmbrToRemove = mdv.getMultiDocumentThumbController().getCurrentThumbNmbr();
                            }
                            mdv.getDocumentContainerPanel().remove(mdt);               //removes thumb from component
                            mdv.getMultiDocumentThumbController().removeMultiDocumentThumb(mdt);//removes thumb from documentlist
                            mdv.getDocumentContainerPanel().revalidate();              //revalidates the document container und repaints it
                            mdv.getDocumentContainerPanel().repaint();
                        }
                    }
                }
                if (moveToNextAvailableDoc) {
                    if (mdv.getMultiDocumentThumbController().getMultiDocumentCount() - 1 > displayedDocNmbrToRemove) {
                        mdv.getMultiDocumentThumbController().showDocument(displayedDocNmbrToRemove + 1);
                    } else {
                        mdv.getMultiDocumentThumbController().showDocument(mdv.getMultiDocumentThumbController().getMultiDocumentCount() - 1);
                    }
                }
                if (!minOneSelectedDoc) {
                    DecoratedJOptionPane.showMessageDialog(null, "Wählen Sie bitte mindestens ein Dokument aus, das entfernt werden soll.", "Information", DecoratedJOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        //draw resizecorner
        Color cornerShadow = new Color(0, 0, 0, 80);
        Color cornerHl = new Color(255, 255, 255, 110);
        BufferedImage rCornerImg = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        ImageIcon rCornerIco = new ImageIcon(rCornerImg);
        Graphics2D rCornerImgG2 = rCornerImg.createGraphics();
        rCornerImgG2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        rCornerImgG2.setColor(cornerShadow);
        rCornerImgG2.drawLine(0, rCornerImg.getHeight(), rCornerImg.getWidth(), 0);
        rCornerImgG2.setColor(cornerHl);
        rCornerImgG2.drawLine(1, rCornerImg.getHeight(), rCornerImg.getWidth(), 1);

        rCornerImgG2.setColor(cornerShadow);
        rCornerImgG2.drawLine(4, rCornerImg.getHeight(), rCornerImg.getWidth(), 4);
        rCornerImgG2.setColor(cornerHl);
        rCornerImgG2.drawLine(5, rCornerImg.getHeight(), rCornerImg.getWidth(), 5);

        rCornerImgG2.setColor(cornerShadow);
        rCornerImgG2.drawLine(8, rCornerImg.getHeight(), rCornerImg.getWidth(), 8);
        rCornerImgG2.setColor(cornerHl);
        rCornerImgG2.drawLine(9, rCornerImg.getHeight(), rCornerImg.getWidth(), 9);

        JLabel resizeCorner = new JLabel(rCornerIco);   //listening is implemented in border. this only paints the dragicon

        //Put all together
        //decorator buttons
        fBorder.addComponent(JFrameTitledComponentBorder.DOCK_TOP_RIGHT, closeBtn, -13, 8);
        fBorder.addComponent(JFrameTitledComponentBorder.DOCK_TOP_RIGHT, maxBtn, -5, 8);
        fBorder.addComponent(JFrameTitledComponentBorder.DOCK_TOP_RIGHT, minBtn, -7, 8);
        fBorder.addComponent(JFrameTitledComponentBorder.DOCK_BOTTOM_RIGHT, resizeCorner, -5, -5);
        //multidocumentviewer buttons
        int yOff = 3;
        fBorder.addComponent(0, hideBtn, 0, yOff);
        fBorder.addComponent(0, prevDocBtn, 3, yOff);
        fBorder.addComponent(0, nextDocBtn, 0, yOff);
        fBorder.addComponent(0, addDocBtn, 5, yOff);
        fBorder.addComponent(0, remDocBtn, 0, yOff);

        log.info("Application loaded");


    }

    public void showDocuments() {
        mdv.setContentVisible(false);
        pages.setEnabled(false);
        scaleFactorTxt.setEnabled(false);
        MultiDocumentThumbController mdvCtrl = mdv.getMultiDocumentThumbController();
        if (mdvCtrl.getMultiDocumentCount() == 1) {
            mdvCtrl.showDocument(0);
        } else if (mdvCtrl.getMultiDocumentCount() > 1) {
            mdv.setContentVisible(true);
            mdvCtrl.showDocument(0);
        }
    }
}

class SmallAttachmentBar extends JPanel {

    public SmallAttachmentBar() {
        super();
        setOpaque(true);
        super.setBackground(new Color(255, 255, 255, 0));
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        GradientPaint gp = new GradientPaint(0, 0, LAFProperties.getInstance().getFeauturesBarColorDark(), 45, 0, LAFProperties.getInstance().getFeauturesBarColorLight(), false);

        g2.setPaint(gp);

        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(LAFProperties.getInstance().getFeauturesBarColorDark());
        g2.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
        g2.setColor(LAFProperties.getInstance().getFeauturesBarColorLight());
        g2.drawLine(getWidth() - 2, 0, getWidth() - 2, getHeight());
        super.paint(g);
    }
}
