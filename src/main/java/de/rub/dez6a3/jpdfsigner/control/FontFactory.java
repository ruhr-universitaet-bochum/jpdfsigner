/*
 * JPDFSigner - Sign PDFs online using smartcards (FontFactory.java)
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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rub.dez6a3.jpdfsigner.control;

import java.awt.Font;

/**
 *
 * @author daniel
 */
public class FontFactory {

    private static Font mainFont = null;

    public static Font getMainFont() {
        if (mainFont == null) {
            mainFont = new Font("Arial", Font.PLAIN, 10);
        }
        return mainFont;
    }
}