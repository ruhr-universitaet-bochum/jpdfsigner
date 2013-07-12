/*
 * JPDFSigner - Sign PDFs online using smartcards (Colors.java)
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
package de.rub.dez6a3.jpdfsigner.colors;

import java.awt.Color;

/**
 *
 * @author dan
 */
public class Colors {

    private Color panelColor;
    private Color borderColor;
    private Color attachmentColor;
    private Color attachmentBorderColor;
    private Color contentColor;
    private int pr, pg, pb;
    private int br, bg, bb;
    private int ar, ag, ab;
    private int abr, abg, abb;
    private int cr, cg, cb;

    private int validate(int color) {
        int result = color;
        if (color < 0) {
            result = 0;
        }
        if (color > 255) {
            result = 255;
        }
        return result;
    }

    public void setBasicPanelColor(Color param) {
        panelColor = param;
        pr = panelColor.getRed();
        pg = panelColor.getGreen();
        pb = panelColor.getBlue();
    }

    public void setBasicBorderColor(Color param) {
        borderColor = param;
        br = borderColor.getRed();
        bg = borderColor.getGreen();
        bb = borderColor.getBlue();
    }

    public void setBasicAttachmentColor(Color param) {
        attachmentColor = param;
        ar = attachmentColor.getRed();
        ag = attachmentColor.getGreen();
        ab = attachmentColor.getBlue();
    }

    public void setBasicAttachmentBorderColor(Color param) {
        attachmentBorderColor = param;
        abr = attachmentBorderColor.getRed();
        abg = attachmentBorderColor.getGreen();
        abb = attachmentBorderColor.getBlue();
    }

    public void setContentColor(Color param) {
        contentColor = param;
        cr = contentColor.getRed();
        cg = contentColor.getGreen();
        cb = contentColor.getBlue();
    }

//Get panelcolors
    //Toolbar
    public Color getToolBarColorDark() {
        int r = pr - 30;
        int g = pg - 30;
        int b = pb - 30;
        return new Color(validate(r), validate(g), validate(b));
    }

    public Color getToolBarColorLight() {
        int r = pr + 70;
        int g = pg + 70;
        int b = pb + 70;
        return new Color(validate(r), validate(g), validate(b));
    }

    public Color getToolBarColorHighlight() {
        int r = pr + 160;
        int g = pg + 160;
        int b = pb + 160;
        int alpha = 50;
        return new Color(validate(r), validate(g), validate(b), alpha);
    }

    //Textfield
    public Color getTextFieldColorDark() {
        int r = pr + 130;
        int g = pg + 130;
        int b = pb + 130;
        return new Color(validate(r), validate(g), validate(b));
    }

    public Color getTextFieldColorLight() {
        int r = pr + 255;
        int g = pg + 255;
        int b = pb + 255;
        return new Color(validate(r), validate(g), validate(b));
    }

    //Menubar
    public Color getMenuBarColorDark() {
        int r = pr + 30;
        int g = pg + 30;
        int b = pb + 30;
        return new Color(validate(r), validate(g), validate(b));
    }

    public Color getMenuBarColorLight() {
        int r = pr + 50;
        int g = pg + 50;
        int b = pb + 50;
        return new Color(validate(r), validate(g), validate(b));
    }

    //FeaturesBar
    public Color getFeaturesBarColorDark() {
        int r = cr;
        int g = cg;
        int b = cb;
        return new Color(validate(r), validate(g), validate(b));
    }

    public Color getFeaturesBarColorLight() {
        int r = cr + 30;
        int g = cg + 30;
        int b = cb + 30;
        return new Color(validate(r), validate(g), validate(b));
    }

    //AttachmentBar
    public Color getAttachmentBarColorDark() {
        int r = cr + 55;
        int g = cg + 55;
        int b = cb + 55;
        return new Color(validate(r), validate(g), validate(b));
    }

    public Color getAttachmentBarColorLight() {
        int r = cr + 55;
        int g = cg + 55;
        int b = cb + 55;
        return new Color(validate(r), validate(g), validate(b));
    }

    //PDF Background
    public Color getPDFBackgroundColor() {
        int r = cr;
        int g = cg;
        int b = cb;
        return new Color(validate(r), validate(g), validate(b));
    }

    //Mainframe Background
    public Color getMainFrameBackgroundColor() {
        int r = cr + 29;
        int g = cg + 29;
        int b = cb + 29;
        return new Color(validate(r), validate(g), validate(b));
    }

    //Inactive Tab
    public Color getInactiveTabColor() {
        int r = pr - 80;
        int g = pg - 80;
        int b = pb - 80;
        return new Color(validate(r), validate(g), validate(b));
    }

    //Marginline Tab
    public Color getMarginLineTabColor() {
        int r = pr + 30;
        int g = pg + 30;
        int b = pb + 30;
        return new Color(validate(r), validate(g), validate(b));
    }

    //Focus Tab
    public Color getFocusTabColor() {
        int r = pr + 30;
        int g = pg + 30;
        int b = pb + 30;
        return new Color(validate(r), validate(g), validate(b));
    }

//Get bordercolors
    //FeaturesBar BorderColor
    public Color getFeaturesBarBorderColorDark() {
        int r = br - 130;
        int g = bg - 130;
        int b = bb - 130;
        return new Color(validate(r), validate(g), validate(b));
    }

    public Color getFeaturesBarBorderColorLight() {
        int r = br - 65;
        int g = bg - 65;
        int b = bb - 65;
        return new Color(validate(r), validate(g), validate(b));
    }

    //AttachmentBar Bordercolor
    public Color getAttachmentBarBorderColorDark() {
        int r = br - 130;
        int g = bg - 130;
        int b = bb - 130;
        return new Color(validate(r), validate(g), validate(b));
    }

    public Color getAttachmentBarBorderColorLight() {
        int r = br - 25;
        int g = bg - 25;
        int b = bb - 25;
        return new Color(validate(r), validate(g), validate(b));
    }

    //TabbedPane BorderColor
    public Color getTabbedPaneBorderColorDark() {
        int r = br - 130;
        int g = bg - 130;
        int b = bb - 130;
        return new Color(validate(r), validate(g), validate(b));
    }

    public Color getTabbedPaneBorderColorLight() {
        int r = br - 10;
        int g = bg - 10;
        int b = bb - 10;
        return new Color(validate(r), validate(g), validate(b));
    }

    //ToolBar BorderColor
    public Color getToolBarBorderColorSouthInner() {
        int r = br + 25;
        int g = bg + 25;
        int b = bb + 25;
        return new Color(validate(r), validate(g), validate(b));
    }

    public Color getToolBarBorderColorSouthOuter() {
        int r = br - 255;
        int g = bg - 255;
        int b = bb - 255;
        return new Color(validate(r), validate(g), validate(b));
    }

    //MenuBar BorderColor
    public Color getMenuBarBorderColorDark() {
        int r = br + 25;
        int g = bg + 25;
        int b = bb + 25;
        return new Color(validate(r), validate(g), validate(b));
    }

    public Color getMenuBarBorderColorLight() {
        int r = br + 110;
        int g = bg + 110;
        int b = bb + 110;
        return new Color(validate(r), validate(g), validate(b));
    }

//Button Colors
    //Attachment Buttons
    public Color getAttachmentOffTopColorDark() {
        int r = ar - 60;
        int g = ag - 60;
        int b = ab - 60;
        return new Color(validate(r), validate(g), validate(b));
    }

    public Color getAttachmentOffTopColorLight() {
        int r = ar + 10;
        int g = ag + 10;
        int b = ab + 10;
        return new Color(validate(r), validate(g), validate(b));
    }

    public Color getAttachmentOnTopColorDark() {
        int r = ar - 55;
        int g = ag - 55;
        int b = ab - 55;
        return new Color(validate(r), validate(g), validate(b));
    }

    public Color getAttachmentOnTopColorLight() {
        int r = ar + 20;
        int g = ag + 20;
        int b = ab + 20;
        return new Color(validate(r), validate(g), validate(b));
    }

    public Color getAttachmentClickColorDark() {
        int r = ar - 35;
        int g = ag - 35;
        int b = ab - 35;
        return new Color(validate(r), validate(g), validate(b));
    }
    public Color getAttachmentClickColorLight() {
        int r = ar + 85;
        int g = ag + 85;
        int b = ab + 70;
        return new Color(validate(r), validate(g), validate(b));
    }

    public Color getAttachmentHighlight() {
        int r = ar + 255;
        int g = ag + 255;
        int b = ab + 255;
        int alpha = 14;
        return new Color(validate(r), validate(g), validate(b), validate(alpha));
    }

//Button Borders
    //Attachment Buttons BorderColor
    public Color getAttachmentBorderColorNorthWest() {
        int r = abr + 70;
        int g = abg + 70;
        int b = abb + 70;
        return new Color(validate(r), validate(g), validate(b));
    }

    public Color getAttachmentBorderColorSouthEast() {
        int r = abr-35;
        int g = abg-35;
        int b = abb-35;
        return new Color(validate(r), validate(g), validate(b));
    }
    //AttachmentExpanderButton BorderColor

    public Color getAttachmentsButtonBorderColorDark() {
        int r = br - 255;
        int g = bg - 255;
        int b = bb - 255;
        return new Color(validate(r), validate(g), validate(b));
    }

    public Color getAttachmentsButtonBorderColorLight() {
        int r = br - 15;
        int g = bg - 15;
        int b = bb - 15;
        return new Color(validate(r), validate(g), validate(b));
    }
//Font
    //Dark Font

    public Color getFontColorDark() {
        int r = br + 110;
        int g = bg + 110;
        int b = bb + 110;
        return new Color(validate(r), validate(g), validate(b));
    }
}
