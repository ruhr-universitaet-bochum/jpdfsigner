/*
 * JPDFSigner - Sign PDFs online using smartcards (ReadersComboBoxListCellRenderer.java)
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

import de.rub.dez6a3.jpdfsigner.control.CardReader;
import de.rub.dez6a3.jpdfsigner.control.Configurator;
import de.rub.dez6a3.jpdfsigner.control.SunPKCS11CardAccessor;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

/**
 *
 * @author dan
 */
public class ReadersComboBoxListCellRenderer implements ListCellRenderer {

    private Color isSelectedColor = null;
    private String cardInsertedIcon = "/de/rub/dez6a3/jpdfsigner/resources/images/sc_inserted.png";
    private String readerErrorIcon = "/de/rub/dez6a3/jpdfsigner/resources/images/sc_error.png";

    public ReadersComboBoxListCellRenderer() {
        isSelectedColor = new Color(230, 230, 230);
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        if (value != null) {
            Component result = null;
            if (value instanceof CardReader) {
                CardReader reader = (CardReader) value;
                JPanel p = new JPanel();
                p.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
                if (isSelected) {
                    p.setBackground(isSelectedColor);
                } else {
                    p.setOpaque(false);
                }
                ImageIcon icon;
                p.setLayout(new BorderLayout());
                if (reader.getSlotID() != -1 && reader.getTokenFlags() != -1 && !cellHasFocus) {
                    icon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource(cardInsertedIcon)).getScaledInstance(12, 12, Image.SCALE_SMOOTH));
                    JLabel iconLabel = new JLabel(icon);
                    iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
                    p.add(iconLabel, BorderLayout.WEST);
                } else if (reader.getSlotID() == -1 && reader.getTokenFlags() == -1 && !cellHasFocus) {
                    icon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource(readerErrorIcon)).getScaledInstance(12, 12, Image.SCALE_SMOOTH));
                    JLabel iconLabel = new JLabel(icon);
                    iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
                    p.add(iconLabel, BorderLayout.WEST);
                }
                JLabel descLabel = new JLabel(reader.getSlotDescription());
                descLabel.setFont(new Font("Arial", Font.BOLD, 11));
                p.add(descLabel, BorderLayout.CENTER);
                result = p;
            } else if (value instanceof String) {
                String str = (String) value;
                JLabel label = new JLabel(str);
                result = label;
            }
            return result;
        } else {
            return new JLabel("");
        }

    }
}
