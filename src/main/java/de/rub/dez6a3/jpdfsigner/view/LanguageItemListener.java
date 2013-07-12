/*
 * JPDFSigner - Sign PDFs online using smartcards (LanguageItemListener.java)
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

import de.rub.dez6a3.jpdfsigner.control.language.Language;
import de.rub.dez6a3.jpdfsigner.control.language.LanguageFactory;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import org.apache.log4j.Logger;

/**
 *
 * @author dan
 */
public class LanguageItemListener extends MouseAdapter {

    private Language lang = null;
    private JButton parentBtn = null;
    public static Logger log = Logger.getLogger(LanguageItemListener.class);

    public LanguageItemListener(Language lang, JButton parentBtn) {
        this.lang = lang;
        this.parentBtn = parentBtn;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (parentBtn != null){
            parentBtn.setIcon(lang.getIcon());
        }
        LanguageFactory.setLanguage(lang.getLanguageInfo());
        LanguageFactory.updateLanguage();
        log.info("Language set to '" + lang.getLanguageInfo() + "'");
    }
}
