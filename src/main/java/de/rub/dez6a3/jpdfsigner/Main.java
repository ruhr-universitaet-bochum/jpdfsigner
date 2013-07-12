/*
 * JPDFSigner - Sign PDFs online using smartcards (Main.java)
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
package de.rub.dez6a3.jpdfsigner;

import de.rub.dez6a3.jpdfsigner.control.Configurator;
import de.rub.dez6a3.jpdfsigner.control.ParamValidator;
import de.rub.dez6a3.jpdfsigner.exceptiontypes.InvalidParamException;
import de.rub.dez6a3.jpdfsigner.control.language.LanguageFactory;
import de.rub.dez6a3.jpdfsigner.view.DecoratedJOptionPane;
import de.rub.dez6a3.jpdfsigner.view.GUIPDFView;
import de.rub.dez6a3.jpdfsigner.view.RootGlassPane;
import de.rub.dez6a3.jpdfsigner.view.UI.LAFProperties;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.io.File;
import java.io.FileInputStream;
import javax.swing.JApplet;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import org.apache.log4j.Logger;
import sun.misc.BASE64Encoder;

//    @Override
/**
 *
 * @author dan
 */
public class Main extends JApplet {

    private int majorToUse = 1;     //Hier wird die Mindestversion angegeben die genutzt werden muss
    private int minorToUse = 5;
    private GUIPDFView viewer = null;
    public static Logger log = Logger.getLogger(Main.class);

    private boolean isJavaVersionSupported() {
        boolean result = false;

        int major = -1;
        int minor = -1;
        int update = -1;
        try {
            String[] version = System.getProperty("java.version").split("\\.");
            major = new Integer(version[0]);
            minor = new Integer(version[1]);
            update = new Integer(version[2].substring(version[2].lastIndexOf("_") + 1));
            if (major == majorToUse) {
                if (minor >= minorToUse) {
                    result = true;
                }
            } else if (major > majorToUse) {
                result = true;
            }
        } catch (Exception e) {
            System.out.println("Cannot determine JRE-Version. Version should be in format: X.X.X_XX - " + e.getMessage());
        }
        if (result) {
            System.out.println("Java-Version supported: " + major + "." + minor + " update " + update);
        } else {
            System.out.println("Java-Version not supported. Please use JRE version " + majorToUse + "." + minorToUse + " or newer");
        }

        return result;
    }

    private void setCustomLookAndFeel() {                   //Prüfen ob der Custom Decorator geladen wird. Dies passiert nur wenn es sich um java 1.6 und neuer handelt
        try {
            log.info("Loading custom LAF ...");
            LAFProperties.initStaticData();
            UIManager.put("RootPaneUI", "de.rub.dez6a3.jpdfsigner.view.UI.CustomRootPaneUI");
            UIManager.put("TextFieldUI", "de.rub.dez6a3.jpdfsigner.view.UI.CustomTextFieldUI");
            UIManager.put("FileChooserUI", "de.rub.dez6a3.jpdfsigner.view.UI.CustomFileChooserUI");
            UIManager.put("ComboBoxUI", "de.rub.dez6a3.jpdfsigner.view.UI.CustomComboBoxUI");
            UIManager.put("TableHeaderUI", "de.rub.dez6a3.jpdfsigner.view.UI.CustomTableHeaderUI");
            UIManager.put("TreeUI", "de.rub.dez6a3.jpdfsigner.view.UI.CustomTreeUI");
            UIManager.put("ButtonUI", "de.rub.dez6a3.jpdfsigner.view.UI.CustomButtonUI");
            UIManager.put("ToggleButtonUI", "de.rub.dez6a3.jpdfsigner.view.UI.CustomToggleButtonUI");
            UIManager.put("ScrollBarUI", "de.rub.dez6a3.jpdfsigner.view.UI.CustomScrollBarUI");
            UIManager.put("ScrollPaneUI", "de.rub.dez6a3.jpdfsigner.view.UI.CustomScrollPaneUI");
            UIManager.put("MenuUI", "de.rub.dez6a3.jpdfsigner.view.UI.CustomMenuUI");
            UIManager.put("MenuItemUI", "de.rub.dez6a3.jpdfsigner.view.UI.CustomMenuItemUI");
            UIManager.put("MenuBarUI", "de.rub.dez6a3.jpdfsigner.view.UI.CustomMenuBarUI");
            UIManager.put("PopupMenuUI", "de.rub.dez6a3.jpdfsigner.view.UI.CustomPopupMenuUI");
            UIManager.put("PopupMenuSeparatorUI", "de.rub.dez6a3.jpdfsigner.view.UI.CustomPopupMenuSeparatorUI");
            UIManager.put("TabbedPaneUI", "de.rub.dez6a3.jpdfsigner.view.UI.CustomTabbedPaneUI");
            UIManager.put("ToolBarSeparatorUI", "de.rub.dez6a3.jpdfsigner.view.UI.CustomToolBarSeparatorUI");
            UIManager.put("ToolBarUI", "de.rub.dez6a3.jpdfsigner.view.UI.CustomToolBarUI");
            UIManager.put("ToolTipUI", "de.rub.dez6a3.jpdfsigner.view.UI.CustomToolTipUI");
            UIManager.put("OptionPaneUI", "de.rub.dez6a3.jpdfsigner.view.UI.CustomOptionPaneUI");
            UIManager.put("PanelUI", "de.rub.dez6a3.jpdfsigner.view.UI.CustomPanelUI");
            UIManager.put("SplitPaneUI", "de.rub.dez6a3.jpdfsigner.view.UI.CustomSplitPaneUI");
            UIManager.put("ProgressBarUI", "de.rub.dez6a3.jpdfsigner.view.UI.CustomProgressBarUI");
            log.info("... Loading LAF done!");
        } catch (Exception ex) {
            log.error("Error during LAF-Loading: " + ex);
        }
    }

    @Override
    public String getParameter(String name) {
        try {
            return super.getParameter(name);
        } catch (Exception e) {
            return "";
        }
    }

    private void catchParamsAndLoadLanguages() throws InvalidParamException {                        //Applet params werden ermittelt, gesetzt und validiert
        //Bei einem falschen zwingend erforderlichen param wird eine
        ParamValidator pv = ParamValidator.getInstance();                            //Exception geworfen und das Programm beendet
        //sollte zuerst aufgerufen werden. konfiguriert den logger.
        pv.setLogConfig(getParameter("logConfig"));
        loadLanguages();
        log.info("Loading and validating applet-params ...");
        pv.setSourceContent(getParameter("sourceContent"));
        pv.setSignatureReason(getParameter("signatureReason"));
        pv.setTrustStore();
        pv.setPostDestination(getParameter("postDestination"));
        pv.setSessionID(getParameter("sessionID"));
        pv.setResultUrl(getParameter("resultUrl"));
        pv.setResultTarget(getParameter("resultTarget"));
        pv.setEmbedded(getParameter("embedded"));
        pv.setApplicationMode(getParameter("mode"));
    }

    public static void main(String[] args) {
        Main m = new Main();
        m.init();
        m.start();
    }

    private void loadLanguages() {
//        String[] langsFromPackage = {"/de/rub/dez6a3/jpdfsigner/resources/language/German.xml", "/de/rub/dez6a3/jpdfsigner/resources/language/English.xml"}; //Complete language support not implemented yet
        String[] langsFromPackage = {"/de/rub/dez6a3/jpdfsigner/resources/language/German.xml"};
        for (String lang : langsFromPackage) {
            try {
                LanguageFactory.registerLanguage(getClass().getResourceAsStream(lang));
            } catch (Exception e) {
                log.error("Cannot load language: " + "- " + e.getMessage());
            }
        }
    }

    public void init() {
        try {
            catchParamsAndLoadLanguages();
            setCustomLookAndFeel();
            if (!isJavaVersionSupported()) {
                DecoratedJOptionPane.showMessageDialog(null, LanguageFactory.getText(LanguageFactory.MAIN_VERSION_NOT_SUPPORTED) + majorToUse + "." + minorToUse, LanguageFactory.getText(LanguageFactory.ERROR_HL), JOptionPane.ERROR_MESSAGE);
                Configurator.closeProgram("-901");
            }
            try {
                Configurator.APPLET_CONTEXT = getAppletContext();
                log.info("Starting in applet-mode");
            } catch (Exception e) {
                log.info("Starting in standalone-mode");
            }
            viewer = new GUIPDFView(LanguageFactory.getText(LanguageFactory.MAIN_HL));
            LanguageFactory.registerComponent(LanguageFactory.MAIN_HL, viewer);
            if (ParamValidator.getInstance().getEmbedded()) {
                runEmbedded();
            }
        } catch (InvalidParamException ex) {
            setCustomLookAndFeel();
            String msg = ex.getMessage();
            log.error("Cannot validate following param: " + ex.getMessage());
            if (msg.equals("sourceUrl-sourceContent-sourceLocal")) {
                DecoratedJOptionPane.showMessageDialog(null, LanguageFactory.getText(LanguageFactory.MAIN_NO_SOURCE_PDF), LanguageFactory.getText(LanguageFactory.ERROR_HL), JOptionPane.ERROR_MESSAGE);
                Configurator.closeProgram("-910");
            }
            if (msg.equals("sourceContent-sourceName")) {
                DecoratedJOptionPane.showMessageDialog(null, LanguageFactory.getText(LanguageFactory.MAIN_NO_PDF_IDENTIFIER), LanguageFactory.getText(LanguageFactory.ERROR_HL), JOptionPane.ERROR_MESSAGE);
                Configurator.closeProgram("-911");
            }
            if (msg.equals("postDestination")) {
                DecoratedJOptionPane.showMessageDialog(null, LanguageFactory.getText(LanguageFactory.MAIN_NO_VALID_DESTINATION_SERVER), LanguageFactory.getText(LanguageFactory.ERROR_HL), JOptionPane.ERROR_MESSAGE);
                Configurator.closeProgram("-912");
            }
            if (msg.equals("postName")) {
                DecoratedJOptionPane.showMessageDialog(null, LanguageFactory.getText(LanguageFactory.MAIN_NO_POST_IDENTIFIER), LanguageFactory.getText(LanguageFactory.ERROR_HL), JOptionPane.ERROR_MESSAGE);
                Configurator.closeProgram("-913");
            }
        } catch (Throwable ex) {
            log.fatal(ex);
            ex.printStackTrace();
            DecoratedJOptionPane.showMessageDialog(null, LanguageFactory.getText(LanguageFactory.MAIN_GENERAL_ERROR), LanguageFactory.getText(LanguageFactory.ERROR_HL), JOptionPane.ERROR_MESSAGE);
            Configurator.closeProgram("-945");
        }
    }

    @Override
    public void start() {
        Component crgp = viewer.getGlassPane();
        if (crgp instanceof RootGlassPane) {
            RootGlassPane rgp = (RootGlassPane) crgp;
            rgp.setInfoText("Loading GUI ...");
            rgp.setLoadingText("Please Wait");
        }
        ((RootGlassPane) viewer.getGlassPane()).setViewTo(RootGlassPane.LOCKED);
        if (ParamValidator.getInstance().getEmbedded()) {
            setVisible(true);
        } else {
            viewer.setVisible(true);
        }
        ((RootGlassPane) viewer.getGlassPane()).setViewTo(RootGlassPane.UNLOCKED);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                viewer.showDocuments();
                if (!viewer.isConfigurationComplete()) {
                    DecoratedJOptionPane.showMessageDialog(viewer, "<html><table width=\"400\"><tr><td align=\"justify\"><b>Die Konfiguration konnte nicht vollständig durchgeführt werden!</b><br><br>"
                            + "Bitte vervollständigen Sie die Konfiguration bevor Sie fortfahren."
                            + "Das Konfigurationsfenster ist bereits geöffnet. Bitte geben Sie ggf. folgende Daten an:<br><br>"
                            + "<b>&middot;</b> Pfad zum Kryptoprovider<br>"
                            + "<b>&middot;</b> Smartcard-Leser<br><br>"
                            + "Schließen Sie das Konfigurationsfenster um die Angaben zu speichern. Klicken Sie dazu bitte auf das rote Kästchen mit dem X welches sich in der rechten, oberen, Ecke des Konfigurationsfensters befindet.</td></tr></table></html>", "Warnung", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

    }

    public void runEmbedded() {
        setLayout(new BorderLayout());
        JRootPane p = viewer.getRootPane();
        p.setBorder(new AbstractBorder() {
            private Insets cInsets = new Insets(1, 1, 1, 1);

            @Override
            public Insets getBorderInsets(Component c) {
                return cInsets;
            }

            @Override
            public Insets getBorderInsets(Component c, Insets insets) {
                return cInsets;
            }

            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                g.setColor(new Color(30, 30, 30));
                g.drawLine(0, 0, width, 0);
                g.drawLine(0, 0, 0, height);
                g.drawLine(1, height - 1, width - 2, height - 1);
                g.drawLine(width - 1, height, width - 1, 0);
            }
        });
        add(p);
    }
}
