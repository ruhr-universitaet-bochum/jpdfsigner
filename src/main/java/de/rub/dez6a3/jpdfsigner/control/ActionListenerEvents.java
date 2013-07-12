/*
 * JPDFSigner - Sign PDFs online using smartcards (ActionListenerEvents.java)
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

import de.rub.dez6a3.jpdfsigner.control.language.LanguageFactory;
import de.rub.dez6a3.jpdfsigner.model.IPDFProcessor;
import de.rub.dez6a3.jpdfsigner.view.DecoratedJFileChooser;
import de.rub.dez6a3.jpdfsigner.view.DecoratedJOptionPane;
import de.rub.dez6a3.jpdfsigner.view.GUIAttachmentBarList;
import de.rub.dez6a3.jpdfsigner.view.GUIConfigView;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.KeyStroke;
import org.apache.log4j.Logger;

public class ActionListenerEvents extends AbstractAction {

    private String identifier;
    private Object object;
    private Configurator config;
    private Hashtable optObjects;
    private Logger log = Logger.getLogger(ActionListenerEvents.class);

    public ActionListenerEvents(String text, Icon icon, String discription, Character shortcut, String ident, Object obj) {
        super(text, icon);
        config = Configurator.getInstance();
        identifier = ident;
        object = obj;
        optObjects = new Hashtable();
        if (shortcut != null) {
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(shortcut, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        }
        putValue(SHORT_DESCRIPTION, discription);
    }

    public void actionPerformed(ActionEvent e) {
        if (identifier.equals("showAttach")) {
            showAttach();
        } else if (identifier.equals("close_app")) {
            Configurator.closeProgram("-900");
        } else if (identifier.equals("page_left")) {
            try {
//                if (!config.getSignAndStampablePagePanel().isDocFixed()) {
                scrollleft();
//                }
            } catch (Exception ex) {
//                log.error(ex);
            }
        } else if (identifier.equals("page_right")) {
            try {
//                if (!config.getSignAndStampablePagePanel().isDocFixed()) {
                scrollRight();
//                }
            } catch (Exception ex) {
//                log.error(ex);
            }
        } else if (identifier.equals("open_config")) {
            openConfig();
        } else if (identifier.equals("close_config")) {
            closeConfig();
        } else if (identifier.equals("config_search_btn")) {
            config_search_btn();
        } else if (identifier.equals("sign_diag")) {
            showHideSignDiag();
        } else if (identifier.equals("zeroBtn")) {
            setSignToPINPanelPWField(0);
        } else if (identifier.equals("oneBtn")) {
            setSignToPINPanelPWField(1);
        } else if (identifier.equals("twoBtn")) {
            setSignToPINPanelPWField(2);
        } else if (identifier.equals("threeBtn")) {
            setSignToPINPanelPWField(3);
        } else if (identifier.equals("fourBtn")) {
            setSignToPINPanelPWField(4);
        } else if (identifier.equals("fiveBtn")) {
            setSignToPINPanelPWField(5);
        } else if (identifier.equals("sixBtn")) {
            setSignToPINPanelPWField(6);
        } else if (identifier.equals("sevenBtn")) {
            setSignToPINPanelPWField(7);
        } else if (identifier.equals("eightBtn")) {
            setSignToPINPanelPWField(8);
        } else if (identifier.equals("nineBtn")) {
            setSignToPINPanelPWField(9);
        } else if (identifier.equals("clearBtn")) {
            clearPINPanelPWField();
        } else if (identifier.equals("okBtn")) {
            if (config.isPdfValid()) {
//                doSignClick();   //überflüssig?
            }
        } else if (identifier.equals("save_doc")) {
            doSaveDoc();
        }
    }

    public void doSaveDoc() {
        byte[] pdfFile = (byte[]) object;
        JFileChooser fc = new JFileChooser();
        int ok = 0;
        while (ok == 0) {
            fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            int result = fc.showSaveDialog(new JFrame());
            if (result == JFileChooser.APPROVE_OPTION) {
                File choosenFile = fc.getSelectedFile();
                try {
                    FileOutputStream out = new FileOutputStream(choosenFile);
                    out.write(pdfFile);
                    ok = 1;
                } catch (Exception ex) {
                    log.warn(ex);
                    //-100
                    DecoratedJOptionPane.showMessageDialog(null, LanguageFactory.getText(LanguageFactory.ACTIONLISTENEREVENTS_SAVING_PDF_ERROR), LanguageFactory.getText(LanguageFactory.ERROR_HL), JOptionPane.ERROR_MESSAGE);
                }
            } else if (result == JFileChooser.CANCEL_OPTION) {
                ok = 1;
            }
        }
    }

    public void setOptObjetcts(Object key, Object value) {
        optObjects.put(key, value);
    }

    private void showAttach() {
        GUIAttachmentBarList bar = (GUIAttachmentBarList) object;
        if (bar.isVisible()) {
            bar.setVisible(false);
        } else {
            bar.setVisible(true);
        }
    }

    private void scrollRight() throws IOException {
        if (((IPDFProcessor) object).getCurrentPageNum() < ((IPDFProcessor) object).getPageCount()) {
            ((IPDFProcessor) object).showPage(((IPDFProcessor) object).getCurrentPageNum() + 1);
        }
    }

    private void scrollleft() throws IOException {
        if (((IPDFProcessor) object).getCurrentPageNum() > 1) {
            ((IPDFProcessor) object).showPage(((IPDFProcessor) object).getCurrentPageNum() - 1);
        }
    }

    private void openConfig() {
        GUIConfigView configView = (GUIConfigView) object;
        if (configView.isVisible()) {
            configView.setVisible(false);
            if (config.haveValuesChanged()) {
                config.savePropertiesToFile(true);
            }
        } else {
            configView.setVisible(true);
        }
    }

    private void closeConfig() {
        JDialog panel = (JDialog) object;
        panel.setVisible(false);
        if (config.haveValuesChanged()) {
            config.savePropertiesToFile(true);
        }
    }

    private void config_search_btn() {
        DecoratedJFileChooser bibFC = new DecoratedJFileChooser();
        GUIConfigView guiConfigViewParent = (GUIConfigView) object;
        int fcResult = bibFC.showOpenDialog(guiConfigViewParent);

        if (fcResult == JFileChooser.APPROVE_OPTION) {
            String selFile = bibFC.getSelectedFile().getPath();
            guiConfigViewParent.setBibPathText(selFile);
            config.setProperty("libpath", selFile, true);
            config.getCardHandler().setProvPath(selFile);
            guiConfigViewParent.actualizeReadersFoundCBox();
        }
    }

    private void showHideSignDiag() {
        config.hideShowGUISignView();
        if (!config.isPdfValid()) {
            //-101
            DecoratedJOptionPane.showMessageDialog(null, LanguageFactory.getText(LanguageFactory.ACTIONLISTENEREVENTS_SHOW_SIGNDIAG_ERROR), LanguageFactory.getText(LanguageFactory.ERROR_HL), JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setSignToPINPanelPWField(int number) {
        JPasswordField pwField = (JPasswordField) object;
        if (pwField.getPassword().length < 6) {
            String str = new String(pwField.getPassword());
            pwField.setText(str + String.valueOf(number));
        }
    }

    private void clearPINPanelPWField() {
        JPasswordField pwField = (JPasswordField) object;
        pwField.setText("");
    }

}
