/*
 * JPDFSigner - Sign PDFs online using smartcards (CX509KeyManager.java)
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

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509KeyManager;
import org.apache.log4j.Logger;

/**
 *
 * @author dan
 */
public class CX509KeyManager extends X509ExtendedKeyManager {

    private X509KeyManager origKeyMan = null;
    public static Logger log = Logger.getLogger(CX509KeyManager.class);

    public CX509KeyManager(X509KeyManager origKeyMan) {
        this.origKeyMan = origKeyMan;
    }

    public String[] getClientAliases(String keyType, Principal[] issuers) {
        return origKeyMan.getClientAliases(keyType, issuers);
    }

    /*
    standardmäßig wird willkürlich(?) ein zertifikat ausgewählt. bei der rubcard, wird das signcert ausgewählt, mit dem
    kein ssh-handshake möglich ist. daher muss die zertifikatauswahl händisch passieren.
    hier wird das zertifikat für die clientauthentifizierung für den upload der pdf ermittelt, die das keyusage-flag "nonRepudiation" nicht gesetzt hat
     */
    public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
        String authAlias = null;
        

//        if (keyType == null || keyType.length == 0) {
//            throw new RuntimeException("No keytypes found. Cannot determine any certificates");
//        }
//        log.info("Determining certificatechains for serverhandshake...");
//        for (int i = 0; i < keyType.length; i++) {
//            String type = keyType[i];
//            log.info("--Keytype found: " + type);
//            log.info("--Determining certificatealiases ...");
//            String[] aliases = origKeyMan.getClientAliases(type, issuers);
//            PrivateKey pk = origKeyMan.getPrivateKey("RUBAUTHCERT");
//            if (aliases == null || aliases.length == 0) {
//                log.info("----No certificatealiases found");
//            } else {
//                for (int j = 0; j < aliases.length; j++) {
//                    String alias = aliases[j];
//                    log.info("----Certificatealias found: " + alias);
//                    log.info("----Determining certificatechain ...");
//                    X509Certificate[] x509Certs = origKeyMan.getCertificateChain(alias);
//                    if (x509Certs == null || x509Certs.length == 0) {
//                        log.info("------No certificatechains found");
//                    } else {
//                        X509Certificate x509Cert = x509Certs[0];
//                        log.info("------Certificatechain found ...");
//                        log.info("------Certificate on top of chain found: " + x509Cert.getSubjectDN().getName());
//                        if (authAlias == null && !x509Cert.getKeyUsage()[1]) {                                                 //keyusage -> index 1 (nonRepudiation) -> wenn false dann handelt es sich um das auth-cert
//                            String choosenAlias = x509Cert.getSubjectDN().getName();
//                            log.info("------USING CERTIFICATE FOR HANDSHAKE WITH ALIAS (Repudiationflag is set): " + choosenAlias);
//                            authAlias = alias;
//                            break;
//                        } else {
//                            String choosenAlias = x509Cert.getSubjectDN().getName();
//                            log.info("------Certificate can not be used for handshakeauthentication (No repudiationflag set): " + choosenAlias);
//                        }
//                    }
//                }
//            }
//        }
//        log.info("Done");

        //--------------------------------alternate version: catches explicite the RUBAUTHCERT certificate by using fixed coded RUBAUTHCERT-Alias

        authAlias = "RUBAUTHCERT";
        log.info("Doing server-client handshake to upload signed document. Using following alias for server-client-authentication: "+authAlias+" (If alias is unknown a SSLHandshakeException: bad_certificate is thrown)");
        return authAlias;
    }

    public String[] getServerAliases(String keyType, Principal[] issuers) {
        return origKeyMan.getServerAliases(keyType, issuers);
    }

    public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
        return origKeyMan.chooseServerAlias(keyType, issuers, socket);
    }

    public X509Certificate[] getCertificateChain(String alias) {
        return origKeyMan.getCertificateChain(alias);
    }

    public PrivateKey getPrivateKey(String alias) {
        return origKeyMan.getPrivateKey(alias);
    }
}
