/*
 * JPDFSigner - Sign PDFs online using smartcards (IDocumentSigner.java)
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

import com.itextpdf.text.DocumentException;
import de.rub.dez6a3.jpdfsigner.exceptiontypes.PKCS11Exception;
import java.awt.Rectangle;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SignatureException;
import java.security.cert.CertificateException;

/**
 *
 * @author dan
 */
public interface IDocumentSigner {
    public ByteArrayOutputStream doSign(byte[] pdf, Rectangle stampPos, int pageNmbrForStamp) throws IOException, DocumentException, NoSuchAlgorithmException, InvalidKeyException, SignatureException;
    public void setCardHandle(Provider provider);
    public void getSignedDocument();
    public void loginCard(char[] pin) throws PKCS11Exception, CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException;
}
