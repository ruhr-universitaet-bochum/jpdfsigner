/*
 * JPDFSigner - Sign PDFs online using smartcards (AttachmentListCellRenderer.java)
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

import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class AttachmentListCellRenderer extends DefaultTableCellRenderer {

    private String[] formats = {"zip", "rar", "avi", "mpg", "mpeg", "mp3", "wav", "mov", "jpg", "jpeg", "gif", "png", "bmp", "xml", "pdf"};
    private ImageIcon[] formatsIcons;
    private ImageIcon viewingIcon = null;
    private int viewing = -1;

    public AttachmentListCellRenderer() {
        viewingIcon = new ImageIcon(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/selattachment.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH));

        formatsIcons = new ImageIcon[]{new ImageIcon(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/attachmenticons/file-zip.png"))),
                    new ImageIcon(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/attachmenticons/file-rar.png"))),
                    new ImageIcon(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/attachmenticons/file-avi.png"))),
                    new ImageIcon(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/attachmenticons/file-mpg.png"))),
                    new ImageIcon(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/attachmenticons/file-mpg.png"))),
                    new ImageIcon(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/attachmenticons/file-mp3.png"))),
                    new ImageIcon(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/attachmenticons/file-wav.png"))),
                    new ImageIcon(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/attachmenticons/file-mov.png"))),
                    new ImageIcon(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/attachmenticons/file-jpg.png"))),
                    new ImageIcon(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/attachmenticons/file-jpg.png"))),
                    new ImageIcon(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/attachmenticons/file-gif.png"))),
                    new ImageIcon(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/attachmenticons/file-png.png"))),
                    new ImageIcon(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/attachmenticons/file-bmp.png"))),
                    new ImageIcon(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/attachmenticons/file-xml.png"))),
                    new ImageIcon(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/attachmenticons/file-pdf.png")))};
    }

    @Override
    public Component getTableCellRendererComponent(final JTable table, Object value, final boolean isSelected, boolean hasFocus, int row, final int column) {

        Component c = null;

        if (value instanceof Object[]) {
            Object[] objArr = (Object[]) value;
            if (objArr != null) {
                if (objArr.length == 3) {
                    if (objArr[0] instanceof String) {
                        c = createTableCellComponent(table, (String) objArr[0], isSelected, hasFocus, row, column);
                    }
                }
            }
        }
        if (value instanceof String) {
            c = createTableCellComponent(table, value, isSelected, hasFocus, row, column);
        }
        return c;
    }

    private Component createTableCellComponent(final JTable table, final Object value, final boolean isSelected, boolean hasFocus, final int row, final int column) {
        Component c = null;
        JLabel l = new JLabel((String) value) {

            @Override
            public void paint(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp;
                if (isSelected) {
                    g2.setColor(new Color(200, 220, 240));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.setColor(new Color(210, 225, 245));
                    int startpoint = 0;
                    int brickWidth = 20;
                    int brickSlant = 20;
                    Polygon brick = new Polygon();
                    brick.addPoint(startpoint, getHeight());
                    brick.addPoint(brickSlant, 0);
                    brick.addPoint(brickSlant + brickWidth, 0);
                    brick.addPoint(brickWidth, getHeight());

                    while (startpoint < getWidth()) {
                        g2.fillPolygon(brick);
                        startpoint += brickWidth * 2;
                        brick.translate(brickWidth * 2, 0);
                    }
                } else {
                    gp = new GradientPaint(0, 0, new Color(255, 255, 255), 0, getHeight(), new Color(230, 230, 230), false);
                    g2.setPaint(gp);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }

                g2.setFont(table.getFont());

                int iconSpace = 0;
                if (column == 0) {
                    for (int i = 0; i < formats.length; i++) {
                        if (((String) value).endsWith("." + formats[i])) {
                            ImageIcon icon = formatsIcons[i];
                            int x = 2;
                            int y = (getHeight() / 2) - (icon.getIconHeight() / 2);

                            if (row == viewing && column == 0) {
                                g2.drawImage(viewingIcon.getImage(), x, y, viewingIcon.getImageObserver());
                                x += viewingIcon.getIconWidth() + 2;      
                            }

                            iconSpace = icon.getIconWidth() + x;
                            g2.drawImage(icon.getImage(), x, y, icon.getImageObserver());
                        }
                    }
                }

                g2.setColor(table.getBackground().brighter());
                FontMetrics fm = getFontMetrics(table.getFont());
                g2.drawString(getText(), iconSpace + 4, fm.getHeight() + (fm.getHeight() / 3) + 1);
                g2.setColor(Color.black);
                g2.drawString(getText(), iconSpace + 3, fm.getHeight() + (fm.getHeight() / 3));
            }
        };
        l.setBorder(BorderFactory.createEmptyBorder());
        c = l;
        return c;
    }

    public void setViewing(int i) {
        viewing = i;
    }

    public int getViewing() {
        return viewing;
    }
}
