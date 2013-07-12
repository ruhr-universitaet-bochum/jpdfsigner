/*
 * JPDFSigner - Sign PDFs online using smartcards (ILanguages.java)
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

package de.rub.dez6a3.jpdfsigner.model;

/**
 *
 * @author dan
 */
public interface ILanguages {
    public String getPage_Left_Tooltip();
    public String getPage_Right_Tooltip();
    public String getZoom_In_Tooltip();
    public String getZoom_Out_Tooltip();
    public String getSign_Tooltip();
    public String getSave_Doc_Tooltip();
    public String getClose_Tooltip();
    public String getShow_Toolbar_Tooltip();
    public String getOpen_Config_GUI_Tooltip();

    public String getPage_Left_Label();
    public String getPage_Right_Label();
    public String getZoom_In_Label();
    public String getZoom_Out_Label();
    public String getSign_Label();
    public String getSave_Doc_Label();
    public String getClose_Label();
    public String getShow_Toolbar_Label();
    public String getOpen_Config_GUI_Label();

    public String getMenubar_File_Text();
    public String getMenubar_Config_Text();


    public String getProgressBar_Download_Label();


    public String getConfig_Close_Button_Label();
    public String getConfig_SCReader_Tab_Label();
    public String getConfig_SCReader_Label();
    public String getConfig_Config_Tab_Label();
    public String getConfig_Config_Label();
    public String getConfig_Config_Search_Button_Label();

    //Exceptions

    public String getConfigurator_MakeDirEx();
    public String getURLConnector_ServerNotReachableEx();
}
