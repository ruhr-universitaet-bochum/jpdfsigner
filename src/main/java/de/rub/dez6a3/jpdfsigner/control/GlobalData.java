/*
 * JPDFSigner - Sign PDFs online using smartcards (GlobalData.java)
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

import com.itextpdf.text.pdf.PdfPKCS7;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author dan
 */
public class GlobalData {

    public static boolean USE_DEFAULT_DECORATION = true;
    private static List<X509Certificate[]> signerChain;
    public static SSLContext uploadSSLContext = null;
    private static TrustManager[] trustAllCertsTM = null;
    private static HostnameVerifier trustAllHV = null;

    public static void setSignerChain(List<X509Certificate[]> param) {
        signerChain = param;
                System.out.println("Chain updated");
    }

    public static List<X509Certificate[]> getSignerChain() {
        return signerChain;
    }

    public static TrustManager[] getTrustAllCertsTrustManager() {
        if (trustAllCertsTM == null) {
            trustAllCertsTM = new TrustManager[]{
                        new X509TrustManager() {

                            public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                            }

                            public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                            }

                            public X509Certificate[] getAcceptedIssuers() {
                                return null;
                            }
                        }
                    };
        }
        return trustAllCertsTM;
    }

    public static HostnameVerifier getTrustAllHostNameVerifier() {
        if (trustAllHV == null) {

        }
        return trustAllHV;
    }
}
