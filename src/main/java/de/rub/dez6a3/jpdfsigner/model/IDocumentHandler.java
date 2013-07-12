/*
 * JPDFSigner - Sign PDFs online using smartcards (IDocumentHandler.java)
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

import de.rub.dez6a3.jpdfsigner.control.FileEntity;
import de.rub.dez6a3.jpdfsigner.control.StringEntity;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 *
 * @author dan
 */
public interface IDocumentHandler{
    
    
    public void throwExceptions() throws Exception;
    
    public boolean isRunning();

    public void addFileEntity(FileEntity fe);
    public void addStringEntity(StringEntity se);
    
    public void setSSLContext(SSLContext sslContext);
    public void setPDFDestIdentifier(String param);
    public void setDestURL(URL url);
    public void setAction(int param);

    public ByteArrayOutputStream getPDFInByteBuffer();
    public int getProgressInPercent();
    public String getULResult();

    public void start(); // is implemented by Thread
}
