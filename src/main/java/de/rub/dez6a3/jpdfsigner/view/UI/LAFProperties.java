/*
 * JPDFSigner - Sign PDFs online using smartcards (LAFProperties.java)
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
import java.awt.Font;
import javax.swing.ToolTipManager;
import org.apache.log4j.Logger;

/**
 *
 * @author dan
 */
public class LAFProperties {
    //Data

    private static boolean initialized = false; //Prüft ob alles bereits einmal initialisiert wurde
    //Colors
    private static LAFProperties instance = null;
    private Color dialogBackground = new Color(215, 215, 215);
    private Color titleBarBackground = new Color(215, 215, 215);
    private Color borderColor = new Color(40, 40, 40);
    private Color borderColorHighlight = new Color(190, 190, 190);
    private Color mainFontColor = new Color(0, 0, 0);
    private Color pdfBackGroundColor = new Color(95, 95, 95);
    private Color attachmentBarColorDark = new Color(100, 100, 100);
    private Color attachmentBarColorLight = new Color(120, 120, 120);
    private Color attachmentBarBorderColorLight = new Color(180, 180, 180);
    private Color mainFrameBackgroundColor = new Color(100, 100, 100);
    //Textfield und Combobox farben
    private Color textFieldBorderColor = new Color(65, 65, 65, 130);
    private Color textFieldHighlightColor = new Color(250, 250, 250);
    private Color textFieldBackgroundColorTop = new Color(225, 225, 225);
    private Color textFieldBackgroundColorBottom = new Color(245, 245, 245);
    //Scrollbar farben
    private Color scrollBarOutLine = new Color(160, 160, 160);
    private Color scrollBarBackground = getPdfBackGroundColor();
    private Color scrollBarTrackAndThumbWestColor = new Color(75, 75, 75);
    private Color scrollBarTrackAndThumbEastColor = new Color(220, 220, 220);
    private Color scrollBarTrackAndThumbHighlightColor = new Color(255, 255, 255, 80);
    private Color scrollBarTrackBackgroundEastColor = new Color(130, 130, 130);
    private Color scrollBarTrackBackgroundWestColor = new Color(85, 85, 85);
    private Color scrollBarCenterTrackLinesNorthColor = new Color(0, 0, 0, 70);
    private Color scrollBarCenterTrackLinesSouthColor = new Color(255, 255, 255, 70);
    private Color scrollBarThumbArrowColor = new Color(90, 90, 90);
    private Color scrollBarSmallOutlineColor = new Color(115, 115, 115);
    //propreitäre Farben (Custom Buttons etc.) 
    //Attachment Button
    private Color attachmentBtnHighlightColor = new Color(255, 255, 255, 50);
    private Color attachmentBtnBorderColorNW = new Color(215, 215, 255);
    private Color attachmentBtnBorderColorSE = new Color(30, 30, 70);
    private Color attachmentBtnOffTopColorDark = new Color(0, 61, 110);
    private Color attachmentBtnOffTopColorLight = new Color(133, 177, 190);
    private Color attachmentBtnOnTopColorDark = new Color(0, 61, 110);
    private Color attachmentBtnOnTopColorLight = new Color(155, 220, 245);
    private Color attachmentBtnClickColorDark = new Color(155, 220, 245);
    private Color attachmentBtnClickColorLight = new Color(0, 61, 110);
    private Color attachmentBtnBorderColorDark = new Color(15, 15, 30);
    private Color attachmentBtnBorderColorLight = new Color(105, 105, 125);
    //Attachment Button ENDE
    //Feautures Bar
    private Color feauturesBarColorDark = new Color(50, 50, 50);
    private Color feauturesBarColorLight = new Color(75, 75, 75);
    //Feautures Bar ENDE
    //Blaue Panels
    private Color bluePanelColorW = new Color(21, 76, 122);
    private Color bluePanelColorE = new Color(0, 34, 61);
    private Color bluePanelHighlightColor = new Color(255, 255, 255, 20);
    private Color bluePanelBorderHighlightColor = new Color(255, 255, 255, 75);
    //Blaue Panels ENDE
    //ToolTip
//    private Color
    //ToolTip ENDE
    //---------propreitäre Farben ende--------
    private Font mainFontRegular;
    private Font mainFontBold;
    public static Logger log = Logger.getLogger(LAFProperties.class);

    public static void initStaticData() {
        if (!initialized) {
            ToolTipManager.sharedInstance().setInitialDelay(1000);
            ToolTipManager.sharedInstance().setDismissDelay(3500);
        } else {
            log.warn("LookAndFeel already initialized");
        }
    }

    public Color getDialogBackground() {
        return dialogBackground;
    }

    public Color getTitleBarBackground() {
        return titleBarBackground;
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public Color getBorderColorHighlight() {
        return borderColorHighlight;
    }

    public Color getMainFontColor() {
        return mainFontColor;
    }

    public Color getPdfBackGroundColor() {
        return pdfBackGroundColor;
    }

    public Color getAttachmentBarColorDark() {
        return attachmentBarColorDark;
    }

    public Color getAttachmentBarColorLight() {
        return attachmentBarColorLight;
    }

    public Color getAttachmentBarBorderColorLight() {
        return attachmentBarBorderColorLight;
    }

    public Color getMainFrameBackgroundColor() {
        return mainFrameBackgroundColor;
    }

    public Color getTextFieldBorderColor() {
        return textFieldBorderColor;
    }

    public Color getTextFieldHighlightColor() {
        return textFieldHighlightColor;
    }

    public Color getTextFieldBackgroundColorTop() {
        return textFieldBackgroundColorTop;
    }

    public Color getTextFieldBackgroundColorBottom() {
        return textFieldBackgroundColorBottom;
    }

    public Color getScrollBarOutLine() {
        return scrollBarOutLine;
    }

    public Color getScrollBarBackground() {
        return scrollBarBackground;
    }

    public Color getScrollBarTrackAndThumbWestColor() {
        return scrollBarTrackAndThumbWestColor;
    }

    public Color getScrollBarTrackAndThumbEastColor() {
        return scrollBarTrackAndThumbEastColor;
    }

    public Color getScrollBarTrackAndThumbHighlightColor() {
        return scrollBarTrackAndThumbHighlightColor;
    }

    public Color getScrollBarTrackBackgroundEastColor() {
        return scrollBarTrackBackgroundEastColor;
    }

    public Color getScrollBarTrackBackgroundWestColor() {
        return scrollBarTrackBackgroundWestColor;
    }

    public Color getScrollBarCenterTrackLinesNorthColor() {
        return scrollBarCenterTrackLinesNorthColor;
    }

    public Color getScrollBarCenterTrackLinesSouthColor() {
        return scrollBarCenterTrackLinesSouthColor;
    }

    public Color getScrollBarThumbArrowColor() {
        return scrollBarThumbArrowColor;
    }

    public Color getScrollBarSmallOutlineColor() {
        return scrollBarSmallOutlineColor;
    }

    public Color getAttachmentBtnHighlightColor() {
        return attachmentBtnHighlightColor;
    }

    public Color getAttachmentBtnBorderColorNW() {
        return attachmentBtnBorderColorNW;
    }

    public Color getAttachmentBtnBorderColorSE() {
        return attachmentBtnBorderColorSE;
    }

    public Color getAttachmentBtnOffTopColorDark() {
        return attachmentBtnOffTopColorDark;
    }

    public Color getAttachmentBtnOffTopColorLight() {
        return attachmentBtnOffTopColorLight;
    }

    public Color getAttachmentBtnOnTopColorDark() {
        return attachmentBtnOnTopColorDark;
    }

    public Color getAttachmentBtnOnTopColorLight() {
        return attachmentBtnOnTopColorLight;
    }

    public Color getAttachmentBtnClickColorDark() {
        return attachmentBtnClickColorDark;
    }

    public Color getAttachmentBtnClickColorLight() {
        return attachmentBtnClickColorLight;
    }

    public Color getAttachmentBtnBorderColorDark() {
        return attachmentBtnBorderColorDark;
    }

    public Color getAttachmentBtnBorderColorLight() {
        return attachmentBtnBorderColorLight;
    }

    public Color getFeauturesBarColorDark() {
        return feauturesBarColorDark;
    }

    public Color getFeauturesBarColorLight() {
        return feauturesBarColorLight;
    }

    public Color getBluePanelColorW() {
        return bluePanelColorW;
    }

    public Color getBluePanelColorE() {
        return bluePanelColorE;
    }

    public Color getBluePanelHighlightColor() {
        return bluePanelHighlightColor;
    }

    public Color getBluePanelBorderHighlightColor() {
        return bluePanelBorderHighlightColor;
    }

    public Font getMainFontRegular() {
        return mainFontRegular;
    }

    public Font getMainFontBold() {
        return mainFontBold;
    }

    private LAFProperties() {
        mainFontRegular = new Font("Arial", Font.PLAIN, 10);
        mainFontBold = new Font("Arial", Font.BOLD, 10);
    }

    public static LAFProperties getInstance() {
        if (instance == null) {
            instance = new LAFProperties();
        }
        return instance;
    }
}
