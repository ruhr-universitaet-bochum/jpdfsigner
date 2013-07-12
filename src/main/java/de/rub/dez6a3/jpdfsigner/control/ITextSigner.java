/*
 * JPDFSigner - Sign PDFs online using smartcards (ITextSigner.java)
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

package de.rub.dez6a3.jpdfsigner.control;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SignatureException;
import java.security.cert.Certificate;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfPKCS7;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignature;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfString;
import com.itextpdf.text.pdf.TSAClient;
import com.itextpdf.text.pdf.TSAClientBouncyCastle;
import de.rub.dez6a3.jpdfsigner.exceptiontypes.PKCS11Exception;
import de.rub.dez6a3.jpdfsigner.control.language.LanguageFactory;
import de.rub.dez6a3.jpdfsigner.model.IDocumentSigner;
import de.rub.dez6a3.jpdfsigner.view.DecoratedJOptionPane;
import java.awt.Rectangle;
import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;

public class ITextSigner implements IDocumentSigner {

    private Configurator config;
    private Provider provider = null;
    private ArrayList<Certificate> signCert;
    private Key signPrivKey = null;
    private char[] pinCache = "0".toCharArray();
    public static Logger log = Logger.getLogger(ITextSigner.class);

    public ITextSigner(byte[] file) {
        config = Configurator.getInstance();
    }

    public void loginCard(char[] pin) throws PKCS11Exception, CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
        log.info("Reading card ...");
        KeyStore pkcs11Keystore = config.getCardHandler().getPKCS11KeyStore();
        if (pin != null) {
            if (Arrays.equals(pinCache, pin) && pin.length != 6) {
                throw new PKCS11Exception("CKR_ARGUMENTS_BAD");
            } else if (pin.length != 6) {
                throw new PKCS11Exception("CKR_ARGUMENTS_BAD");
            } else if (Arrays.equals(pinCache, pin) && pin.length == 6) {
                throw new PKCS11Exception("CKR_PIN_INCORRECT");
            }
        }
        provider = config.getCardHandler().getProvider();
        log.info("Opening PKCS11 session ...");
        if (pin == null) {
            log.info("Awaiting PIN ...");
        } else {
            log.info("Logging in to PKCS11 session ...");
        }
        pkcs11Keystore.load(null, pin);
        log.info("Cardlogin done!");
        Enumeration aliaes = null;
        aliaes = pkcs11Keystore.aliases();

        Certificate certList[] = new Certificate[pkcs11Keystore.size()];
        ArrayList<Key> keys = new ArrayList<Key>();
        int i = 0;
        log.info("Reading cardcertificates from token...");
        while (aliaes.hasMoreElements()) {
            String label = (String) aliaes.nextElement();
            X509Certificate cert = null;
            try {
                cert = (X509Certificate) pkcs11Keystore.getCertificate(label);
            } catch (Exception e) {
                log.error("Cannot find certificate with label: " + label, e);
            }
            if (label.equals("RUBSIGNCERT")) { //Signcertificate must be at top position in signchain
                certList[0] = cert;
                log.info("'RUBSIGNCERT' added to signchain");
                try {
                    signPrivKey = pkcs11Keystore.getKey(label, null);
                    log.info("Private key found and set for 'RUBSIGNCERT'");
                } catch (Exception e) {
                    log.error("Cannot find private key with label: " + label, e);
                }
            }
            if (label.equals("RUBCACERT")) {
                certList[1] = cert;
                log.info("'RUBCACERT' added to signchain");
            }
            if (label.equals("DFN-Verein PCA Global - G01")) {
                certList[2] = cert;
                log.info("'DFN-Verein PCA Global - G01' added to signchain");
            }
            if (label.equals("Deutsche Telekom Root CA 2")) {
                certList[3] = cert;
                log.info("'Deutsch Telekom Root CA 2' added to signchain");
            }
            i++;
        }
        if (certList.length == 0) {
            throw new PKCS11Exception("NO_RUBSIGNCERT");
        }

        if (signPrivKey == null) {
            throw new PKCS11Exception("NO_RUBSIGNCERT");
        }

        signCert = new ArrayList<Certificate>();
        for (Certificate cert : certList) {
            if (cert != null) {
                signCert.add(cert);
            }
        }
        log.info("Certificate- & keyloading done!");
    }

    public static void handleException(Exception e) {
        if (e instanceof CertificateException) {
            //-110
            DecoratedJOptionPane.showMessageDialog(null, LanguageFactory.getText(LanguageFactory.ITEXTSIGNER_CERTIFICATE_ERROR), LanguageFactory.getText(LanguageFactory.ERROR_HL), JOptionPane.ERROR_MESSAGE);
        } else {
            //-111
            DecoratedJOptionPane.showMessageDialog(null, LanguageFactory.getText(LanguageFactory.ITEXTSIGNER_UNKNOWN_ERROR_DURING_SIGN), LanguageFactory.getText(LanguageFactory.ERROR_HL), JOptionPane.ERROR_MESSAGE);
        }
    }

    public ByteArrayOutputStream doSign(byte[] pdf, Rectangle stampPos, int pageNmbrForStamp) throws IOException, DocumentException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Certificate[] chain = signCert.toArray(new Certificate[0]);

        PdfReader reader = new PdfReader(pdf);
        ByteArrayOutputStream byteOS = new ByteArrayOutputStream();
        PdfStamper stp = PdfStamper.createSignature(reader, byteOS, '\0', null, true);
        PdfSignatureAppearance sap = stp.getSignatureAppearance();
        if (stampPos != null) {
            sap.setVisibleSignature(new com.itextpdf.text.Rectangle(stampPos.x, stampPos.y, stampPos.width, stampPos.height), pageNmbrForStamp, null);
            sap.setRenderingMode(PdfSignatureAppearance.RenderingMode.NAME_AND_DESCRIPTION);
            sap.setAcro6Layers(true);
        }
//        Siganture Appearance

        PdfSignature dic = new PdfSignature(PdfName.ADOBE_PPKLITE, new PdfName("adbe.pkcs7.detached"));
        log.info("Creating signature with reason: " + ParamValidator.getInstance().getSignatureReason());
        sap.setReason(ParamValidator.getInstance().getSignatureReason());
        sap.setLocation("Ruhr-Universität Bochum");
        Image i = Image.getInstance(getClass().getResource("/de/rub/dez6a3/jpdfsigner/resources/images/sign.png"));
        sap.setImage(i);
        sap.setCrypto((PrivateKey) signPrivKey, chain, null, PdfSignatureAppearance.WINCER_SIGNED);
        dic.setReason(ParamValidator.getInstance().getSignatureReason());
        dic.setLocation("Ruhr-Universität Bochum");
        sap.setCryptoDictionary(dic);
        // preserve some space for the contents
        int contentEstimated = 15000;
        HashMap<PdfName, Integer> exc = new HashMap<PdfName, Integer>();
        exc.put(PdfName.CONTENTS, new Integer(contentEstimated * 2 + 2));
        sap.preClose(exc);
        // make the digest
        InputStream data = sap.getRangeStream();
        MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
        byte buf[] = new byte[8192];
        int n;
        while ((n = data.read(buf)) > 0) {
            messageDigest.update(buf, 0, n);
        }
        byte hash[] = messageDigest.digest();
        Calendar cal = Calendar.getInstance();
        // If we add a time stamp:
        TSAClient tsc = new TSAClientBouncyCastle("http://zeitstempel.dfn.de/");
        // Create the signature

        PdfPKCS7 sgn;
        try {
            sgn = new PdfPKCS7((PrivateKey) signPrivKey, chain, null, "SHA1", null, false);
            byte sh[] = sgn.getAuthenticatedAttributeBytes(hash, cal, null);
            sgn.update(sh, 0, sh.length);
            byte[] encodedSig = sgn.getEncodedPKCS7(hash, cal, tsc, null);

            if (contentEstimated + 2 < encodedSig.length) {
                throw new DocumentException("Not enough space");
            }

            byte[] paddedSig = new byte[contentEstimated];
            System.arraycopy(encodedSig, 0, paddedSig, 0, encodedSig.length);
            // Replace the contents
            PdfDictionary dic2 = new PdfDictionary();
            dic2.put(PdfName.CONTENTS, new PdfString(paddedSig).setHexWriting(true));
            sap.close(dic2);
        } catch (NoSuchProviderException ex) {
            ex.printStackTrace();
        }
        return byteOS;
    }

    public void setCardHandle(Provider param) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void getSignedDocument() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
