/*
 * JPDFSigner - Sign PDFs online using smartcards (MaxDocSigns.java)
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

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 *
 * @author dan
 */
public class MaxDocSigns extends PlainDocument {

    public static int EVERY_SIGN = 0;
    public static int JUST_INT = 1;
    private int maxLength;
    private int method;

    public MaxDocSigns(int maxLength, int method) {
        this.maxLength = maxLength;
        this.method = method;
    }

    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        try {
            if (method == JUST_INT) {
                Integer.parseInt(str);
                if (str.length() == 0) {
                    return;
                }
            }
            if (getLength() + str.length() <= maxLength) {
                super.insertString(offs, str, (javax.swing.text.AttributeSet) a);
            }
        } catch (Exception ex) {
        }
    }
}
