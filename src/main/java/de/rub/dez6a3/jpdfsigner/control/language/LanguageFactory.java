/*
 * JPDFSigner - Sign PDFs online using smartcards (LanguageFactory.java)
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
package de.rub.dez6a3.jpdfsigner.control.language;

import de.rub.dez6a3.jpdfsigner.view.AttachmentBarButton;
import de.rub.dez6a3.jpdfsigner.view.GUIConfigView;
import de.rub.dez6a3.jpdfsigner.view.LanguageItemListener;
import de.rub.dez6a3.jpdfsigner.view.SignatureValidPanel;
import java.awt.Component;
import java.awt.Container;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import org.apache.log4j.Logger;
import org.jdom.JDOMException;

/**
 *
 * @author dan
 */
public class LanguageFactory {

    public static int INFO_HL = 0;
    public static int QUESTION_HL = 1;
    public static int WARN_HL = 2;
    public static int ERROR_HL = 3;
    public static int MAIN_HL = 4;
    public static int MAIN_VERSION_NOT_SUPPORTED = 5;
    public static int MAIN_NO_SOURCE_PDF = 6;
    public static int MAIN_NO_PDF_IDENTIFIER = 7;
    public static int MAIN_NO_VALID_DESTINATION_SERVER = 8;
    public static int MAIN_NO_POST_IDENTIFIER = 9;
    public static int MAIN_GENERAL_ERROR = 10;
    public static int ACTIONLISTENEREVENTS_SAVING_PDF_ERROR = 11;
    public static int ACTIONLISTENEREVENTS_SHOW_SIGNDIAG_ERROR = 12;
    public static int CONFIGURATOR_CONFIG_HAS_BEEN_SAVED = 13;
    public static int CONFIGURATOR_ERROR_SAVING_CONFIG = 14;
    public static int ITEXTSIGNER_CERTIFICATE_ERROR = 15;
    public static int ITEXTSIGNER_UNKNOWN_ERROR_DURING_SIGN = 16;
    public static int OPENSCCARDACCESSOR_PIN_EXPIRED = 17;
    public static int OPENSCCARDACCESSOR_PIN_INCORRECT = 18;
    public static int OPENSCCARDACCESSOR_PIN_LOCKED = 19;
    public static int OPENSCCARDACCESSOR_PIN_INVALID = 20;
    public static int OPENSCCARDACCESSOR_ARGUMENTS_BAD = 21;
    public static int OPENSCCARDACCESSOR_TOKEN_NOT_PRESENT = 22;
    public static int OPENSCCARDACCESSOR_TOKEN_NOT_RECOGNIZED = 23;
    public static int OPENSCCARDACCESSOR_FUNCTION_FAILED = 24;
    public static int OPENSCCARDACCESSOR_FUNCTION_CANCELD = 25;
    public static int OPENSCCARDACCESSOR_DEVICE_REMOVED = 26;
    public static int OPENSCCARDACCESSOR_DEVICE_ERROR = 27;
    public static int OPENSCCARDACCESSOR_ALREADY_INIT = 28;
    public static int OPENSCCARDACCESSOR_GENERAL_ERROR = 29;
    public static int OPENSCCARDACCESSOR_GENERAL_ERROR_CRYPTOPATH_INVALID = 30;
    public static int OPENSCCARDACCESSOR_IO_ERROR = 31;
    public static int OPENSCCARDACCESSOR_CP_UNLOADING_ERROR = 32;
    public static int OPENSCCARDACCESSOR_THREAD_HANGS = 33;
    public static int OPENSCCARDACCESSOR_ERROR = 34;
    public static int SASPP_DRAW_STAMP = 35;
    public static int SASPP_ERROR_DURING_SIGN = 36;
    public static int SASPP_ERROR_DURING_UPLOAD = 37;
    public static int SASPP_UPLOAD_SERVER_NOT_REACHABLE = 38;
    public static int SASPP_UPLOADMODULE_ERROR = 39;
    public static int SASPP_SUCCESS_UPLOAD = 40;
    public static int SASPP_NO_ZERO_ERRROCODE_IN_RESPONSE = 41;
    public static int SASPP_WRONG_RESPONSE = 42;
    public static int SASPP_NO_RESPONSE = 43;
    public static int SASPP_GENERAL_ERROR = 44;
    public static int DECORATEDJFILECHOOSER_HL = 45;
    public static int GUICONFIGVIEW_ERROR_ON_EXEC = 46;
    public static int GUICONFIGVIEW_CHOOSEREADER_JLABEL = 47;
    public static int GUICONFIGVIEW_CHOOSEBIBPATH_JLABEL = 48;
    public static int GUICONFIGVIEW_BIB_SEARCH_JBUTTON = 49;
    public static int GUICONFIGVIEW_CARDREADER_TAB_JTABPANE = 50;
    public static int GUICONFIGVIEW_BIBS_TAB_JTABPANE = 51;
    public static int GUICONFIGVIEW_LOADING_CARDREADERS = 52;
    public static int GUICONFIGVIEW_NO_CARDREADERS_FOUND = 53;
    public static int GUICONFIGVIEW_CP_ERROR = 54;
    public static int GUICONFIGVIEW_ERROR_CREATING_GUI = 55;
    public static int GUIPDFVIEW_SPLASH_CONFIGURATING = 56;
    public static int GUIPDFVIEW_SPLASH_PDF = 57;
    public static int GUIPDFVIEW_SPLASH_PDF_VERIFY = 58;
    public static int GUIPDFVIEW_SPLASH_PDF_ATTACHMENTS = 59;
    public static int GUIPDFVIEW_AUTOCONF_ERROR_MANUAL_CPPATH = 60;
    public static int GUIPDFVIEW_AUTOCONF_ERROR_MANUAL_SCREADER = 61;
    public static int GUIPDFVIEW_TOOLTIP_BROWSE_LEFT = 62;
    public static int GUIPDFVIEW_TOOLTIP_BROWSE_RIGHT = 63;
    public static int GUIPDFVIEW_TOOLTIP_ZOOM_IN = 64;
    public static int GUIPDFVIEW_TOOLTIP_ZOOM_OUT = 65;
    public static int GUIPDFVIEW_TOOLTIP_SAVE_SEEN_PDF_LOKAL = 66;
    public static int GUIPDFVIEW_TOOLTIP_OPEN_CONFIG = 67;
    public static int GUIPDFVIEW_TOOLTIP_SET_LANGUAGE = 68;
    public static int GUIPDFVIEW_TOOLTIP_ATTACHMENTS = 69;
    public static int GUIPDFVIEW_TOOLTIP_SIGNATURES = 70;
    public static int GUIPDFVIEW_ERROR_CREATING_GUI = 71;
    public static int GUIPDFVIEW_ERROR_DECODING_B64_PDF = 72;
    public static int GUIPDFVIEW_ERROR_LOADING_LOKAL_PDF = 73;
    public static int GUIPDFVIEW_ERROR_IN_DOWNLOAD_MODULE = 74;
    public static int GUIPDFVIEW_PDF_SERVER_NOT_REACHABLE = 75;
    public static int GUIPDFVIEW_UNKNOWN_PDF_DOWNLOAD_ERROR = 76;
    public static int GUIPDFVIEW_CORRUPT_PDF = 77;
    public static int GUIPDFVIEW_PDF_VIEW_JTABPANE = 78;
    public static int GUIPDFVIEW_XML_VIEW_JTABPANE = 79;
    public static int GUISIGNVIEW_LOADINGTXT_WAIT_MSG = 80;
    public static int GUISIGNVIEW_INFOTXT_LOADING_CP = 81;
    public static int GUISIGNVIEW_INFOTXT_UPLOADING_PDF = 82;
    public static int GUISIGNVIEW_MANUAL_STAMPER_PANEL_TEXT = 83;
    public static int GUISIGNVIEW_INFO_PANEL_TEXT = 84;
    public static int GUISIGNVIEW_WRONG_SIGNED_PDF = 85;
    public static int GUISIGNVIEW_LOADINGTXT_AWAITING_PIN = 86;
    public static int GUISIGNVIEW_INFOTXT_AWAITING_PIN = 87;
    public static int GUISIGNVIEW_INFOTXT_SIGNING = 88;
    public static int GUISIGNATUREVIEW_SIGNATURES = 89;
    public static int SPLASH_LOADING = 90;
    public static int SPLASH_LOADING_DONE = 91;
    public static int JFRAMETITLEDBORDER_DEFAULT_FOOTER_TOOLTIP = 92;
    public static int SIGNATUREVALIDPANEL_INVALID_PDF_SIG = 93;
    public static int SIGNATUREVALIDPANEL_VALID_PDF_SIG = 94;
    public static int SIGNATUREVALIDPANEL_PDF_SIGNED_BY = 95;
    public static int SIGNATUREVALIDPANEL_PDF_SIGNED_FOR = 96;
    public static int SIGNATUREVALIDPANEL_UNKNOWN_USER = 97;
    public static int TOOLBARPOPDOWNBUTTON_DOCUMENT_VALID = 98;
    public static int TOOLBARPOPDOWNBUTTON_DOCUMENT_INVALID = 99;
    public static int TOOLBARPOPDOWNBUTTON_DOCUMENT_UNKNOWN = 100;
    public static int CONFIGURATOR_GETTING_SC_READERS_ERROR = 101;
    private static Hashtable<Integer, Component> components = new Hashtable<Integer, Component>();
    private static Hashtable<String, Language> languages = new Hashtable<String, Language>();
    private static Language language = null; //Default language
    public static Logger log = Logger.getLogger(LanguageFactory.class);


    //new ones
    public static int GUIATTACHMENTBARLIST_FIST_TABLE_COLUMN = 102; //dateiname
    public static int GUIATTACHMENTBARLIST_SECOND_TABLE_COLUMN = 103;   //beschreibung
    public static int GUIATTACHMENTBARLIST_THIRD_TABLE_COLUMN = 104;    //größe
    public static int GUIPDFVIEW_SHOW_CERTIFICATE_DETAILS = 105;    //Klicken Sie auf diese Ansicht um die Zertifikatsdteils anzuzeigen
    public static int DECORATEDJFILECHOOSER_HEADLINE = 106; //Pfad auswählen
    public static int GUISIGNVIEW_WRONG_CARD_PIN = 107; //Die eingegebene PIN ist falsch oder gesperrt.\nBitte beachten sie, dass nach dem dritten velerhafent Versuch ihre PIN gesperrt wird.
    public static int GUISIGNVIEW_ANOTHER_ERROR_ON_CARDLOGON = 108; //Es ist ein Fehler aufgetreten.\nWeitere Informationen finden Sie in den Logs.
    public static int GUISIGNVIEW_CANNOT_INIT_READER = 109; //Das Lesegerät kann nich initialisiert werden
    public static int GUISIGNVIEW_CHOOSE_READER_FIRST = 110;    //Bitte wählen Sie zu erst einen Kartenleser aus
    public static int MULTIDOCUMENTVIEWER_MAX_DOC_LOAD_REACHED = 111;   //Die maximale Anzahl an geladenen Dokumenten wurde erreicht.\nAlle weiteren Dokumente wurden verworfen.
    public static int MULTIDOCUMENTVIEWER_COULDNT_LOAD_SOME_DOCS = 112; //Einige Dokumente konnten nicht geladen werden.
    public static int MULTIDOCUMENTVIEWER_CHOOSE_MIN_ONE_DOC_2_REMOVE = 113;    //Wählen Sie bitte mindestens ein Dokument aus, das entfernt werden soll.
    public static int XMLENRICHER_INFO_TEXT = 114;  //<html><p align=\"justify\">Zur Vervollständigung des Antrages, müssen noch einige Daten erfasst werden. Tragen Sie bitte alle Daten in die Felder ein und klicken Sie anschließend auf \"Speichern\".</p></html>
    public static int XMLENRICHER_FNAME_LABEL = 115;    //Vorname
    public static int XMLENRICHER_LNAME_LABEL = 116;    //Nachname
    public static int XMLENRICHER_BIRTH_LABEL = 117;    //Geb.Datum
    public static int XMLENRICHER_STAFF_COUNCIL_LABEL = 118;    //Zust. Personalrat
    public static int XMLENRICHER_AUHORIZED_SIGNER_LABEL = 119; //Unterschriftsberechtigter
    public static int XMLENRICHER_SAVE_BUTTON = 120; //Speichern
    public static int XMLENRICHER_RESET_BUTTON = 121;    //Zurücksetzen
    public static int XMLENRICHER_FILL_ALL_TEXTFIELDS_TO_PROCESS = 122; //Bitte füllen Sie alle Felder aus
    public static int XMLENRICHER_NO_DOCUMENTS_LOADED = 123;    //Keine Dokumente geladen. Bitte laden Sie mindestens ein Dokument
    public static int XMLENRICHER_OVERRIDING_MARKED_FIELDS = 124;//Achtung: Alle rot markierten Felder werden überschrieben! Fortsetzen?
    public static int XMLENRICHER_ENRICHING_DONE = 125; //Alle Dokumente besitzen nun die benötigten Nutzerdaten.\nMöchten Sie nun mit dem Upload der Dokumente beginnen?\n\nKlicken Sie auf 'NEIN' wenn Sie die Daten überprüfen oder korrigieren möchten.
    public static int GUISIGNVIEW_LOADINGTXT_SIGNING_DONE = 126;   //Signaturvorgang abgeschlossen
    public static int XMLENRICHER_INFOTXT_SIGNING_DONE = 127;    //Bitte beachten Sie die Mitteilungen auf der Folgeseite

    //newer ones
    public static int GUIPDFVIEW_TOOLTIP_FIT_ZOOM_TO_SIZE = 128; //Seiten an Fenstergröße anpassen
    private LanguageFactory() {
    }

    public static Language[] getAllLanguages() {
        Language[] lang = new Language[languages.size()];
        Enumeration e = languages.elements();
        int i = 0;
        while (e.hasMoreElements()) {
            lang[i] = (Language) e.nextElement();
            i++;
        }
        return lang;
    }

    public static Language registerLanguage(InputStream is) throws JDOMException, IOException {
        Language lang = new Language(is);
        languages.put(lang.getLanguageInfo(), lang);
        return lang;
    }

    public static void setLanguage(String langName) {
        language = languages.get(langName);
    }

    public static Language getLanguage() {
        if (language == null) {
            try {
                log.info("No language loaded. Loading default language ...");
                language = registerLanguage(LanguageFactory.class.getResourceAsStream("/de/rub/dez6a3/jpdfsigner/resources/language/German.xml"));
            } catch (Exception e) {
                log.error("No default language loadable. Program will crash ...");
            }
        }
        return language;
    }

    public static String getText(int key) {
        String result = "UNDEFINED";
        if (getLanguage().getTextData().get(key) != null) {
            String value = getLanguage().getTextData().get(key);
            if (value.startsWith("tooltip=")) {
                result = value.split("=")[1];
            } else {
                result = getLanguage().getTextData().get(key);
            }
        }
        return result;
    }

    public static void registerComponent(int id, Component c) {
        components.put(id, c);
    }

    public static void updateLanguage() {
        Enumeration en = components.keys();
        while (en.hasMoreElements()) {
            int id = (Integer) en.nextElement();
            Component c = components.get(id);
            setValue(getLanguage().getTextData().get(id), c);
        }
    }

    private static void setValue(String txt, Component c) {
        if (c instanceof JLabel) {
            ((JLabel) c).setText(txt);
        } else if (c instanceof JButton) {
            String value = null;
            try {
                if (txt.contains("tooltip=")) {
                    value = txt.split("=")[1];
                }
            } catch (Exception e) {
            }
            if (value != null) {
                ((JButton) c).setToolTipText(value);
            } else {
                ((JButton) c).setText(txt);
            }
        } else if (c instanceof JTabbedPane) {
            try {
                String[] hl = txt.split("=");
                int idx = new Integer(hl[0]);
                String value = hl[1];
                ((JTabbedPane) c).setTitleAt(idx, value);
            } catch (Exception e) {
                log.warn("JTabbedPane headline is not valid. Must be in following format: tabIndex=tabHeadline (e.g.: 0=Cardreaders)");
            }
        } else if (c instanceof AttachmentBarButton) {
            ((AttachmentBarButton) c).setToolTipText(txt);
        } else if (c instanceof JFrame) {
            ((JFrame) c).setTitle(txt);
            ((JFrame) c).repaint();
        } else if (c instanceof SignatureValidPanel) {
            ((SignatureValidPanel) c).updateVerfificationText();
        }
    }
}
