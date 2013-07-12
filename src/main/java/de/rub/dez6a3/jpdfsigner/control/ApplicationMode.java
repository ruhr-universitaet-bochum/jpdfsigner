/*
 * JPDFSigner - Sign PDFs online using smartcards (ApplicationMode.java)
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

/**
 *
 * @author dan
 */
public class ApplicationMode {

    private int modeNmbr = -1;
    private String modeName = "UNSET";

    public ApplicationMode(int modeNmbr, String modeName){
        this.modeNmbr = modeNmbr;
        this.modeName = modeName;
    }

    public String getModeName() {
        return modeName;
    }

    public void setModeName(String modeName) {
        this.modeName = modeName;
    }

    public int getModeNmbr() {
        return modeNmbr;
    }

    public void setModeNmbr(int modeNmbr) {
        this.modeNmbr = modeNmbr;
    }

    

}
