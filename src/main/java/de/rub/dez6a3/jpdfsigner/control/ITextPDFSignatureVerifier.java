/*
 * JPDFSigner - Sign PDFs online using smartcards (ITextPDFSignatureVerifier.java)
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

import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfPKCS7;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.codec.Base64.InputStream;
import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;
import de.rub.dez6a3.jpdfsigner.exceptiontypes.VerifySignatureException;
import de.rub.dez6a3.jpdfsigner.model.IPDFSignatureVerifier;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.String;
import java.math.BigInteger;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchProviderException;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.ess.ESSCertID;
import org.bouncycastle.asn1.ess.SigningCertificate;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TSPValidationException;
import org.bouncycastle.tsp.TimeStampRequest;
import org.bouncycastle.tsp.TimeStampResponse;
import org.bouncycastle.tsp.TimeStampToken;

/**
 *
 * @author dan
 */
public class ITextPDFSignatureVerifier implements IPDFSignatureVerifier {

    private String issuerName = null;
    private String signedForName = null;
    private static ITextPDFSignatureVerifier instance = null;
    public static Logger log = Logger.getLogger(ITextPDFSignatureVerifier.class);

    private ITextPDFSignatureVerifier() {
    }

    public static ITextPDFSignatureVerifier getInstance() {
        if (instance == null) {
            instance = new ITextPDFSignatureVerifier();
        }
        return instance;
    }

    public String getIssuerName() {
        return issuerName;
    }

    public String getSignedForName() {
        return signedForName;
    }

    public void verifySignature(PdfReader reader, KeyStore ks) throws VerifySignatureException {
        issuerName = null;
        signedForName = null;
        boolean result = true;
        List<X509Certificate[]> validatedChainCerts = new ArrayList<X509Certificate[]>();

        AcroFields af = reader.getAcroFields();
        ArrayList<String> names = af.getSignatureNames();
        if (names.size() < 1) {
            GlobalData.setSignerChain(null);
            throw new VerifySignatureException("PDF doesn't contain a signature");
        }

        for (String currName : names) {
            log.info("-------------Reading following Documentsignature: " + currName + "------------------");
            log.info("Signature name: " + currName);
            log.info("Signature covers whole document: " + af.signatureCoversWholeDocument(currName));
            log.info("Current Documentrevision: " + af.getRevision(currName));
            PdfPKCS7 pkcs7 = af.verifySignature(currName);
            log.info("Building whole chain ...");
            Certificate[] certs = pkcs7.getSignCertificateChain();      //Um zu überprüfen ob alle Certs auch X509Certificate - typen sind
            X509Certificate[] validatedX509Certs = new X509Certificate[certs.length];
            for (int i = 0; i < certs.length; i++) {
                Certificate currCert = certs[i];
                if (currCert instanceof X509Certificate) {
                    if (issuerName == null && af.getRevision(currName) == 1) {
                        issuerName = PdfPKCS7.getSubjectFields((X509Certificate) currCert).getField("CN");

                    }
                    try {
                        String[] reasonField = pkcs7.getReason().split(":");
                        if (reasonField[0].trim().equals("Signature Userid")) {
                            if (signedForName == null) {
                                signedForName = reasonField[1].trim();
                            }
                        }
                    } catch (Exception e) {
                    }
                    log.info("Adding certificate with following CN to chain: " + PdfPKCS7.getSubjectFields((X509Certificate) currCert).getField("CN"));
                    validatedX509Certs[i] = (X509Certificate) currCert;
                } else {
                    log.error("Certificate must be instance of X509Certificate... The verification will fail!");
                    result = false;
                }
            }
            validatedChainCerts.add(validatedX509Certs);
            X509Certificate[] pdfCerts = (X509Certificate[]) pkcs7.getCertificates();
            ArrayList<X509Certificate> pdfCertList = new ArrayList<X509Certificate>();
            for(X509Certificate pdfCert: pdfCerts){
                pdfCertList.add(pdfCert);
            }

            try {
                log.info("Timestamp is NOT verified! Will be implemented soon!");
            } catch (NullPointerException e) {
                log.info("No timestamp found! Signature contains the date of the signers pc.");
            } catch (Exception e) {
                log.error(e);
            }
            Object fails[] = PdfPKCS7.verifyCertificates(pdfCertList.toArray(new X509Certificate[pdfCertList.size()]), ks, null, pkcs7.getSignDate());
            if (fails == null) {
                log.info("Certification verification succeeded: " + currName);
            } else {
                result = false;
                log.info("Certificate verification failed: " + fails[1]);
            }
            log.info("--------------------------------------");
        }
        GlobalData.setSignerChain(validatedChainCerts);

        if (!result) {
            throw new VerifySignatureException("At least one signature is invalid.");
        }
    }
}
