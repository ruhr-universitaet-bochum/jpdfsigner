/*
 * JPDFSigner - Sign PDFs online using smartcards (ColorFactory.java)
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

import de.rub.dez6a3.jpdfsigner.colors.Colors;
import de.rub.dez6a3.jpdfsigner.colors.Plastic;

/**
 *
 * @author dan
 */
public class ColorFactory {
    public static int Plastic = 0;

    public static Colors getColorScheme(int param){
        Colors scheme = null;

        switch(param){
            case 0:
                scheme = new Plastic();
        }

        return scheme;
    }
}
