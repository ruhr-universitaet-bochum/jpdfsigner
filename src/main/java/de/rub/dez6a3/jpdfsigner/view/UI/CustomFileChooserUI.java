/*
 * JPDFSigner - Sign PDFs online using smartcards (CustomFileChooserUI.java)
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.basic.BasicFileChooserUI;
import javax.swing.plaf.metal.MetalFileChooserUI;
import org.apache.log4j.Logger;
import sun.swing.FilePane;

/**
 *
 * @author dan
 */
public class CustomFileChooserUI extends MetalFileChooserUI {

    private Font font = new Font("Arial", Font.PLAIN, 11);
    public static Logger log = Logger.getLogger(CustomFileChooserUI.class);

    public CustomFileChooserUI(JFileChooser b) {
        super(b);
    }

    public static ComponentUI createUI(JComponent c) {
        return new CustomFileChooserUI((JFileChooser) c);
    }

    @Override
    public void installComponents(JFileChooser fc) {
        super.installComponents(fc);


        setFontForAll(fc.getComponents());
    }

    private void setFontForAll(Component[] c) {
        for (int i = 0; i < c.length; i++) {
            if (c[i] instanceof JComponent) {
                setFontForAll(((JComponent) c[i]).getComponents());
            }
            try {
                c[i].setFont(font);
            } catch (Exception e) {
                log.warn("Cannot set FileChooser font. - " + e.getMessage());
            }
        }
    }

    @Override
    public void installUI(JComponent c) {

        try {
            UIManager.put("FileChooser.listFont", new FontUIResource(new Font("Arial", Font.PLAIN, 11)));
        } catch (Exception e) {
            log.warn("Cannot set FileChooser Font - " + e.getMessage());
        }

        try {
            Icon file = new ImageIcon(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/filechoosericons/file.png"));
            UIManager.put("FileView.fileIcon", file);
        } catch (Exception e) {
            log.warn("Cannot set FileIcon - " + e.getMessage());
        }

        try {
            Icon dir = new ImageIcon(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/filechoosericons/folder.png"));
            UIManager.put("FileView.directoryIcon", dir);
        } catch (Exception e) {
            log.warn("Cannot set DirectoryIcon - " + e.getMessage());
        }

        try {
            Icon dirHome = new ImageIcon(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/filechoosericons/folderHome.png"));
            UIManager.put("FileChooser.homeFolderIcon", dirHome);
        } catch (Exception e) {
            log.warn("Cannot set HomeFolderIcon - " + e.getMessage());
        }

        try {
            Icon dirUp = new ImageIcon(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/filechoosericons/folderUp.png"));
            UIManager.put("FileChooser.upFolderIcon", dirUp);
        } catch (Exception e) {
            log.warn("Cannot set UpFolderIcon - " + e.getMessage());
        }

        try {
            Icon dirAdd = new ImageIcon(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/filechoosericons/folderAdd.png"));
            UIManager.put("FileChooser.newFolderIcon", dirAdd);
        } catch (Exception e) {
            log.warn("Cannot set NewFolderIcon - " + e.getMessage());
        }

        try {
            Icon computer = new ImageIcon(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/filechoosericons/computer.png"));
            UIManager.put("FileView.computerIcon", computer);
        } catch (Exception e) {
            log.warn("Cannot set ComputerIcon - " + e.getMessage());
        }

        try {
            Icon floppyDrive = new ImageIcon(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/filechoosericons/floppyDrive.png"));
            UIManager.put("FileView.floppyDriveIcon", floppyDrive);
        } catch (Exception e) {
            log.warn("Cannot set FloppyDriveIcon - " + e.getMessage());
        }

        try {
            Icon disk = new ImageIcon(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/filechoosericons/disk.png"));
            UIManager.put("FileView.hardDriveIcon", disk);
        } catch (Exception e) {
            log.warn("Cannot set HardDriveIcon - " + e.getMessage());
        }
        super.installUI(c);
    }
}
