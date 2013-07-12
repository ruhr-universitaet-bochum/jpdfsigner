/*
 * JPDFSigner - Sign PDFs online using smartcards (CardReader.java)
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

import java.awt.Component;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author dan
 */
public class CardReader{

    private String slotDescription = "UNKNOWN";
    private long slotID = -1;
    private long tokenFlags = -1;
    private boolean isHWSlot = false;
    private boolean isTokenPresent = false;

    public CardReader() {
    }

    public CardReader(long slotID, String slotDescription) {
        this.slotID = slotID;
        this.slotDescription = slotDescription;
    }

    public String getSlotDescription() {
        return slotDescription;
    }

    public long getSlotID() {
        return slotID;
    }

    public void setSlotDescription(String slotDescription) {
        this.slotDescription = slotDescription;
    }

    public void setSlotID(long slotID) {
        this.slotID = slotID;
    }

    public long getTokenFlags() {
        return tokenFlags;
    }

    public void setTokenFlags(long tokenFlags) {
        this.tokenFlags = tokenFlags;
    }

    public boolean isIsHWSlot() {
        return isHWSlot;
    }

    public void setIsHWSlot(boolean isHWSlot) {
        this.isHWSlot = isHWSlot;
    }

    public boolean isIsTokenPresent() {
        return isTokenPresent;
    }

    public void setIsTokenPresent(boolean isTokenPresent) {
        this.isTokenPresent = isTokenPresent;
    }
}
