/*
 * JPDFSigner - Sign PDFs online using smartcards (FileEntity.java)
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
public class FileEntity {

    private byte[] data = null;
    private String filename = null;
    private String entityName = null;
    private String contentType = null;
    private byte[] code = null;

    public FileEntity(byte[] data, String filename, String entityName, String contentType) {
        this.data = data;
        this.filename = filename;
        this.entityName = entityName;
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getData() {
        return data;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    

    public String getEntityName() {
        return entityName;
    }

    public String getFilename() {
        return filename;
    }

    public byte[] getHTTPCode() {
        if (code == null) {
            String codeStr = "Content-Disposition: form-data; name=\"" + entityName + "\"; filename=\"" + filename + "\"\r\n"
                    + "Content-Type: " + contentType + "\r\n\r\n";
            code = new byte[codeStr.length()+data.length];
            System.arraycopy(codeStr.getBytes(), 0, code, 0, codeStr.length());
            System.arraycopy(data, 0, code, codeStr.length(), data.length);
        }
        return code;
    }
}
