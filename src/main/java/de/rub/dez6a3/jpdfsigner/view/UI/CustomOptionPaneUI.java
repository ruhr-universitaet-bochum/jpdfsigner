/*
 * JPDFSigner - Sign PDFs online using smartcards (CustomOptionPaneUI.java)
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
package de.rub.dez6a3.jpdfsigner.view.UI;

import de.rub.dez6a3.jpdfsigner.view.UI.borders.HighlightBorder;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicOptionPaneUI;
import javax.swing.plaf.basic.BasicPanelUI;
import org.apache.log4j.Logger;
import sun.font.Font2D;

/**
 *
 * @author dan
 */
public class CustomOptionPaneUI extends BasicOptionPaneUI {

    private ImageIcon errorIcon = null;
    private ImageIcon infoIcon = null;
    private ImageIcon questionIcon = null;
    private ImageIcon warnIcon = null;
    public static Logger log = Logger.getLogger(CustomOptionPaneUI.class);

    public CustomOptionPaneUI() {
        try {
            errorIcon = new ImageIcon(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/dialog/error.png"));
        } catch (Exception e) {
            log.warn("Cannot set OptionPane-Erroricon - " + e.getMessage());
        }
        try {
            infoIcon = new ImageIcon(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/dialog/info.png"));
        } catch (Exception e) {
            log.warn("Cannot set OptionPane-Infoicon - " + e.getMessage());
        }
        try {
            questionIcon = new ImageIcon(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/dialog/question.png"));
        } catch (Exception e) {
            log.warn("Cannot set OptionPane-Questionicon - " + e.getMessage());
        }
        try {
            warnIcon = new ImageIcon(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/dialog/warn.png"));
        } catch (Exception e) {
            log.warn("Cannot set OptionPane-Warnicon - " + e.getMessage());
        }
    }

    public static ComponentUI createUI(JComponent c) {
        return new CustomOptionPaneUI();
    }

    public void installUI(JComponent c) {
        UIManager.put("OptionPane.messageForeground", LAFProperties.getInstance().getMainFontColor());
        UIManager.put("OptionPane.messageFont", LAFProperties.getInstance().getMainFontRegular().deriveFont(Font.PLAIN, 12));
        super.installUI(c);
        c.setBackground(LAFProperties.getInstance().getDialogBackground());
    }

    @Override
    protected Icon getIconForType(int messageType) {
        Icon ico = null;
        switch (messageType) {
            case JOptionPane.ERROR_MESSAGE:
                ico = errorIcon;
                break;
            case JOptionPane.INFORMATION_MESSAGE:
                ico = infoIcon;
                break;
            case JOptionPane.QUESTION_MESSAGE:
                ico = questionIcon;
                break;
            case JOptionPane.WARNING_MESSAGE:
                ico = warnIcon;
                break;
            default:
                ico = super.getIconForType(messageType);
        }
        if (ico == null) {
            ico = super.getIconForType(messageType);
        }
        return ico;
    }
}
