/*
 * JPDFSigner - Sign PDFs online using smartcards (Language.java)
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

import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import javax.swing.ImageIcon;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import sun.misc.BASE64Decoder;

/**
 *
 * @author dan
 */
public class Language {

    protected Hashtable<Integer, String> data = null;
    private String displayedLanguageText = "UNDEFINED";
    private ImageIcon icon = null;
    public static Logger log = Logger.getLogger(Language.class);

    public Language(InputStream is) throws JDOMException, IOException {
        data = new Hashtable<Integer, String>();
        SAXBuilder builder = new SAXBuilder();
        Document langXML = builder.build(is);
        Element root = langXML.getRootElement();
        displayedLanguageText = root.getChildText("displayedLanguageText");
        try {
            icon = new ImageIcon(Toolkit.getDefaultToolkit().createImage(new BASE64Decoder().decodeBuffer(root.getChildText("base64Icon"))));
        } catch (Exception e) {
            log.warn("Cannot create languageicon of language: " + displayedLanguageText);
        }
        List fieldList = root.getChild("fields").getChildren();
        Iterator it = fieldList.iterator();
        while (it.hasNext()) {
            Element currEl = (Element) it.next();
            try {
                int id = new Integer(currEl.getAttributeValue("id"));
                data.put(id, currEl.getText());
            } catch (Exception e) {
                log.warn("Missing field of language: " + displayedLanguageText);
            }
        }
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public String getLanguageInfo() {
        return displayedLanguageText;
    }

    public Hashtable<Integer, String> getTextData() {
        return data;
    }
}
