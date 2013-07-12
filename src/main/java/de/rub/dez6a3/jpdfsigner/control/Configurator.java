/*
 * JPDFSigner - Sign PDFs online using smartcards (Configurator.java)
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
package de.rub.dez6a3.jpdfsigner.control;

import de.rub.dez6a3.jpdfsigner.exceptiontypes.CardAccessorException;
import de.rub.dez6a3.jpdfsigner.exceptiontypes.NoSuchCardReaderException;
import de.rub.dez6a3.jpdfsigner.model.ICardAccessor;
import de.rub.dez6a3.jpdfsigner.model.IDocumentSigner;
import de.rub.dez6a3.jpdfsigner.view.GUIAttachmentBarList;
import de.rub.dez6a3.jpdfsigner.view.GUIPDFView;
import de.rub.dez6a3.jpdfsigner.view.GUISignView;
import de.rub.dez6a3.jpdfsigner.view.GUISignatureView;
import de.rub.dez6a3.jpdfsigner.view.SignatureValidPanel;
import java.applet.AppletContext;
import java.awt.GraphicsConfiguration;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.zip.DataFormatException;
import javax.swing.JComponent;
import javax.swing.JTextField;
import org.apache.log4j.Logger;

/**
 * @author Daniel Moczarski
 * Diese Klasse wurde nach dem Singleton-Pattern entwickelt.
 */
public class Configurator {

    private static Configurator instance = null;
    private ICardAccessor cardHandler;
    private IDocumentSigner docSigner;
    private Properties loadedConfigProp;
    private File configDirPath;
    private File configFile;
    private Hashtable properties;
    private boolean valuesChanged = false;
    private SignAndStampablePagePanel pp;
    private MultiDocumentThumbController multiDocCtrl = null;
    private byte[] pdfFile;
    private GUISignView signView;
    private GUIAttachmentBarList attachmentBar = null;
    private GUISignatureView signatureView = null;
    private GUIPDFView pdfViewer = null;
    private SignatureValidPanel signatureValidPanel = null;
    private JComponent contentPanel = null;
    private JTextField pagesTextField = null;
    private JTextField scaleFactorTextField = null;
    public static AppletContext APPLET_CONTEXT = null;
    private boolean pdfSignable = false;
    /**
     *Logging per Log4J
     */
    public static GraphicsConfiguration GRAPHICS_CONFIG = null;
    public static Logger log = Logger.getLogger(Configurator.class);

    private Configurator() {
    }

    /**
     * Diese Methode ermöglicht den Zugriff auf das Objekt dieser Klasse.
     * <p>
     * ACHTUNG: Bevor diese Methode aufgerufen werden kann, muss die Klasse
     * initialisiert werden. (siehe {@link runConfigurator()})
     * @return
     * @throws NullPointerException
     */
    public static Configurator getInstance() throws NullPointerException {
        if (instance == null) {
            throw new NullPointerException("Run \"runConfigurator\" first!");
        } else {
            return instance;
        }
    }

    /**
     * Diese Methode initialisiert ein Objekt der Klasse. Sie muss vor dem Aufruf der Methode {@link getInstance()} einmalig aufgerufen werden.
     *Anschließend wird die Konfiguration angestoßen. Angefallende Fehler können der zurückgegebenen Liste entnommen werden.
     * @return  Gibt eine generische Liste mit allen bei diesem Aufruf angefallenen Fehlern zurück.
     *          Sind keine fehler aufgetreten, ist der return-Wert null.
     *
     *          Fehlercodes:
     *          -201    Keine Konfiguration gefunden
     *          -202    Konfiguration ist unvollständig
     *          -203    Trotz existierender Konfigurationsdatei kann auf diese nicht zugegriffen werden.
     */
    public static List<Integer> runConfigurator() {
        List<Integer> result = null;
        if (instance == null) {
            instance = new Configurator();
            result = instance.initConfigurator();

        }
        return result;
    }

    public GUIPDFView getPdfViewer() {
        return pdfViewer;
    }

    public void setPdfViewer(GUIPDFView pdfViewer) {
        this.pdfViewer = pdfViewer;
    }

    public GUISignatureView getSignatureView() {
        return signatureView;
    }

    public void setSignatureView(GUISignatureView signatureView) {
        this.signatureView = signatureView;
    }

    public JTextField getPagesTextField() {
        return pagesTextField;
    }

    public void setPagesTextField(JTextField pagesTextField) {
        this.pagesTextField = pagesTextField;
    }

    public JTextField getScaleFactorTextField() {
        return scaleFactorTextField;
    }

    public void setScaleFactorTextField(JTextField scaleFactorTextField) {
        this.scaleFactorTextField = scaleFactorTextField;
    }

    public JComponent getContentPanel() {
        return contentPanel;
    }

    public void setContentPanel(JComponent contentPanel) {
        this.contentPanel = contentPanel;
    }

    public MultiDocumentThumbController getMultiDocCtrl() {
        return multiDocCtrl;
    }

    public void setMultiDocCtrl(MultiDocumentThumbController multiDocCtrl) {
        this.multiDocCtrl = multiDocCtrl;
    }

    public SignatureValidPanel getSignatureValidPanel() {
        return signatureValidPanel;
    }

    public void setSignatureValidPanel(SignatureValidPanel signatureValidPanel) {
        this.signatureValidPanel = signatureValidPanel;
    }

    public GUIAttachmentBarList getAttachmentBar() {
        return attachmentBar;
    }

    public void setAttachmentBar(GUIAttachmentBarList attachmentBar) {
        this.attachmentBar = attachmentBar;
    }

    public ICardAccessor getCardHandler() {
        return cardHandler;
    }

    public void setSignAndStampablePagePanel(SignAndStampablePagePanel param) {
        pp = param;
    }

    public SignAndStampablePagePanel getSignAndStampablePagePanel() {
        return pp;
    }

    public void setPDFFile(byte[] param) {
        pdfFile = param;
    }

    public byte[] getPDFFile() {
        return pdfFile;
    }

    public void setDocumentSigner(IDocumentSigner param) {
        docSigner = param;
    }

    public IDocumentSigner getDocumentSigner() {
        return docSigner;
    }

    public void setGUISignView(GUISignView param) {
        signView = param;
    }

    public GUISignView getGUISignView() {
        return signView;
    }

    public void setPdfValidity(boolean param) {
        pdfSignable = param;
    }

    public boolean isPdfValid() {
        return pdfSignable;
    }

    public void hideShowGUISignView() {
        if (pdfSignable) {
            if (signView.isVisible()) {
                signView.setVisible(false);
            } else {
                signView.setVisible(true);
            }
        }
    }

    public static void closeProgram(String error) {
        if (instance != null && getInstance().haveValuesChanged()) {
            getInstance().savePropertiesToFile(true);
        }
        try {
            String valError = "";
            URL resultUrl = null;
            if (error == null) {
                resultUrl = new URL(ParamValidator.getInstance().getResultUrl().toString());
            } else {
                String getSeparator = "?";
                if (ParamValidator.getInstance().getResultUrl().toString().contains("?")) {
                    getSeparator = "&";
                }
                resultUrl = new URL(ParamValidator.getInstance().getResultUrl().toString() + getSeparator + "error=" + error);
            }
            log.info("Redirecting with errorcode: " + error);
            APPLET_CONTEXT.showDocument(resultUrl, ParamValidator.getInstance().getResultTarget());
        } catch (Exception e) {
            log.warn("Cannot show document - " + e.getMessage());
        }
        System.exit(0);
    }

    /**
     * Diese Methode wird von {@link runConfigurator()} aufgerufen.
     * @return
     */
    private List<Integer> initConfigurator() {
        List<Integer> errorStack = new ArrayList<Integer>();
        try {
            cardHandler = new SunPKCS11CardAccessor();
        } catch (NullPointerException ex) {
            log.error(ex);
            Configurator.closeProgram("-940");
        }
        loadedConfigProp = new Properties();
        properties = new Hashtable();

        String configDirName = "JPDFSigner";
        String configFileName = "jpdfsigner.cfg";
        String userDirPath = System.getProperty("user.home");

        if (!userDirPath.substring(userDirPath.length() - 1, userDirPath.length()).equals("/")) {
            userDirPath = userDirPath + "/";
        }
        configDirPath = new File(userDirPath + configDirName);
        configFile = new File(userDirPath + configDirName + "/" + configFileName);

        try {
            loadConfigFile();
        } catch (FileNotFoundException ex) {
            errorStack.add(-201);
            log.info("No configuration found - try to autoconfigurate");
            try {
                setStandardConfig();
                savePropertiesToFile(false);
            } catch (CardAccessorException e) {
                log.error("Cannot determine cardreader - " + ex.getMessage(), e);
            } catch (Exception e) {
                log.info("Properties not saved. The configuration must be complete to save it.");
                try {
                    errorStack.add(new Integer(e.getMessage()));
                } catch (Exception exc) {
                    log.error(exc);
                }
            }
        } catch (IOException ex) {
            errorStack.add(-203);
            log.info("Can not access configurationfile - try to autoconfigurate");
            try {
                setStandardConfig();
                savePropertiesToFile(false);
            } catch (CardAccessorException e) {
                log.error("Cannot determine cardreader - " + ex.getMessage());
            } catch (Exception e) {
                log.info("Properties not saved. The configuration must be complete to save it.");
                try {
                    errorStack.add(new Integer(e.getMessage()));
                } catch (Exception exc) {
                }
            }
        } catch (DataFormatException ex) {
            errorStack.add(-202);
            log.info("Configurationfile is not valid - try to autoconfigurate");
            try {
                setStandardConfig();
                savePropertiesToFile(false);
            } catch (CardAccessorException e) {
                log.error("Cannot determine cardreader - " + ex.getMessage());
            } catch (Exception e) {
                log.info("Properties not saved. The configuration must be complete to save it.");
                try {
                    errorStack.add(new Integer(e.getMessage()));
                } catch (Exception exc) {
                }
            }
        } catch (CardAccessorException e) {
            errorStack.add(-204);
            log.info("The cryptoprovider can not be loaded");
            try {
                setStandardConfig();
                savePropertiesToFile(false);
            } catch (CardAccessorException ex) {
                log.error("Cannot determine cardreader - " + ex.getMessage());
            } catch (Exception ex) {
                log.info("Properties not saved. The configuration must be complete to save it. (" + e.getMessage() + ")");
                try {
                    errorStack.add(new Integer(e.getMessage()));
                } catch (Exception exc) {
                }
            }
        } catch (NoSuchCardReaderException e) {
            errorStack.add(-205);
            log.info("The cardreader stored in the config file is not attached to the computer");
            try {
                setStandardConfig();
                savePropertiesToFile(false);
            } catch (CardAccessorException ex) {
                log.error("Cannot determine cardreader - " + ex.getMessage());
            } catch (Exception ex) {
                log.info("Properties not saved. The configuration must be complete to save it. (" + e.getMessage() + ")");
                try {
                    errorStack.add(new Integer(e.getMessage()));
                } catch (Exception exc) {
                }
            }
        }
        return errorStack;
    }

    private void setStandardConfig() throws CardAccessorException, Exception {
        setProperty("language", "DEU", false);
        for (String currLibPath : cardHandler.getStandardLibPath()) {
            if (new File(currLibPath).isFile()) {
                setProperty("libpath", currLibPath, false);
                getCardHandler().setProvPath(currLibPath);
                break;
            }
        }
        if (properties.get("libpath") == null) {
            log.info("No lib found - please choose the path to the cryptolibrary manually");
            throw new Exception("-301");                    //Bibliothek nicht gefunden
        } else {
            log.info("Determining usable smartcard-reader...");
            ArrayList<CardReader> readers;
            try {
                cardHandler.load();
                readers = cardHandler.getReaders();
            } catch (CardAccessorException e) {
                throw e;
            } finally {
                try {
                    cardHandler.unload();
                } catch (CardAccessorException e) {
                }
            }
            boolean readerSet = false;
            if (readers.size() > 0) {
                for (CardReader cardReader : readers) {
                    if (!readerSet) {
                        getCardHandler().setSelectedReader(cardReader);
                        readerSet = true;
                        setProperty("screader", cardReader.getSlotDescription(), false);
                        setProperty("slotid", new Long(cardReader.getSlotID()).toString(), false);
                    }
                }
                log.info("Loaded reader: " + getProperty("screader") + " (slot-ID: " + getProperty("slotid") + ")");
            } else {
                log.info("No reader found");
                throw new Exception("-302");            //Kein Reader gefunden
            }
        }
    }

    /**
     * Diese Methode speichert die gewählte Einstellung auf die Festplatte. Der Speicherort ist das Home-Verzeichnis des jeweiligen Anwenders.
     *
     * @throws Exception
     */
    public void savePropertiesToFile(boolean showResult) {
        try {
            configDirPath.mkdir();
            loadedConfigProp.clear();
            for (Enumeration e = properties.keys(); e.hasMoreElements();) {
                String key = (String) e.nextElement();
                String value = (String) properties.get(key);
                log.trace("'" + key + "' will be saved");
                loadedConfigProp.setProperty(key, value);
            }
            loadedConfigProp.store(new FileOutputStream(configFile.getPath()), null);
            valuesChanged = false;
            log.info("Properties saved to file.");
            if (showResult) {
                //-102
//                DecoratedJOptionPane.showMessageDialog(null, LanguageFactory.getText(LanguageFactory.CONFIGURATOR_CONFIG_HAS_BEEN_SAVED), LanguageFactory.getText(LanguageFactory.INFO_HL), JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            log.warn("Cannot save configuration. - " + e.getMessage());
            if (showResult) {
                //-103
//                DecoratedJOptionPane.showMessageDialog(null, LanguageFactory.getText(LanguageFactory.CONFIGURATOR_ERROR_SAVING_CONFIG), LanguageFactory.getText(LanguageFactory.ERROR_HL), JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Diese Methode fügt dem zentralen Datenobjekt die Konfigurationsparameter hinzu. Zusätzlich wird der Parameter {@link valuesChanged} auf True gesetzt.
     * @param key
     * @param value
     */
    public void setProperty(String key, String value, boolean valuesChanged) {
        if (key != null) {
            if (value == null) {
                value = "";
            }
            properties.put(key, value);
            this.valuesChanged = valuesChanged;
            log.info("Property added - Key: " + key + " - Value: " + value);
        }
    }

    /**
     * Diese Methode gibt zurück, ob die Einstellungen seit des letzten lokalen Speichervorgangs der {@link savePropertiesToFile()}-Methode verändert wurden.
     * @return
     */
    public boolean haveValuesChanged() {
        return valuesChanged;
    }

    /**
     * Diese Methode ruft einen gesetzten Paramter des zentralen Datenobjekts ab und gibt diesen aus.
     * @param key
     * @return
     */
    public String getProperty(String key) {
        return (String) properties.get(key);
    }

    /**
     * Diese Methode lädt die Konfigurationsdatei von der Festplatte und fügt sie dem zentralen Datenobjekt hinzu.
     * @throws FileNotFoundException
     * @throws IOException
     * @throws DataFormatException
     */
    private void loadConfigFile() throws FileNotFoundException, IOException, DataFormatException, CardAccessorException, NoSuchCardReaderException {
        loadedConfigProp.load(new FileInputStream(configFile));
        if (!loadedConfigProp.containsKey("screader") || !loadedConfigProp.containsKey("slotid") || !loadedConfigProp.containsKey("language") || !loadedConfigProp.containsKey("libpath")) {
            throw new DataFormatException("Configurationfile is invalid.");
        }
        setProperty("screader", loadedConfigProp.getProperty("screader"), false);
        setProperty("slotid", loadedConfigProp.getProperty("slotid"), false);
        setProperty("language", loadedConfigProp.getProperty("language"), false);
        setProperty("libpath", loadedConfigProp.getProperty("libpath"), false);
        getCardHandler().setProvPath(loadedConfigProp.getProperty("libpath"));
        long slotid = -1;
        try {
            slotid = new Long(getProperty("slotid"));
        } catch (Exception e) {
            throw new DataFormatException("SlotID is invalid");
        }
        getCardHandler().setSelectedReader(new CardReader(slotid, getProperty("screader")));
    }
}
