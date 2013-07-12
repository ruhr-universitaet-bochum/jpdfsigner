/*
 * JPDFSigner - Sign PDFs online using smartcards (ParamValidator.java)
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

import com.itextpdf.text.pdf.codec.Base64.InputStream;
import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;
import de.rub.dez6a3.jpdfsigner.exceptiontypes.InvalidParamException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.xml.sax.InputSource;
import sun.misc.BASE64Decoder;

/**
 *
 * @author dan
 */
public class ParamValidator {

    private static ParamValidator instance = null;
    private ArrayList<MultiDocumentThumb> sourceContent = new ArrayList<MultiDocumentThumb>();
    private String signatureReason = null;
    private KeyStore trustStore = null;
    private URL postDestination = null;
    private String[] sessionID = null;
    private URL resultUrl = null;
    private String resultTarget = "_self";
    private int applicationMode = 0;
    private boolean embedded = false;
    public static int SIGNATURE_MODE = 0;
    public static int ATTACH_USER_DATA_TO_PDF_MODE = 1;
    public static Logger log = Logger.getLogger(ParamValidator.class);

    public static ParamValidator getInstance() {
        if (instance == null) {
            instance = new ParamValidator();
        }
        return instance;
    }

//---------------------SETTER-----------------------
//--------------------------------------------------
    public void setSourceContent(String param) {
        if (param != null && !param.equals("")) {
            String[] docs = param.split("--"); //Split at every doublel line to get document/pdfspec tuple
            SAXBuilder xmlBuilder = new SAXBuilder();
            XMLOutputter xmlOutputter = new XMLOutputter();
            BASE64Decoder b64Decoder = new BASE64Decoder();
            int curDocNmbr = 1;
            for (String doc : docs) {
                String[] docTuple = doc.split("-");
                byte[] docBytes = null;
                String docName = "Unknown"; //initial value - if there is no name found, this will be used
                String docPostID = null;
                String docSignSpec = null;

                try {
                    for (int i = 0; i < docTuple.length; i++) {
                        String b64CurrParam = docTuple[i];
                        if (i == 0) {
                            docBytes = b64Decoder.decodeBuffer(b64CurrParam);
                        } else if (i == 1) {
                            String decodedCurrParam = null;
                            try {
                                decodedCurrParam = new String(b64Decoder.decodeBuffer(b64CurrParam));
                                docSignSpec = xmlOutputter.outputString(xmlBuilder.build(new ByteArrayInputStream(decodedCurrParam.getBytes())));
                            } catch (IOException ex) {
                                log.error("Cannot decode second parameter. ");
                            } catch (JDOMException e) {
                                docName = decodedCurrParam;
                            }
                        } else if (i == 2) {
                            String decodedCurrParam = null;
                            try {
                                decodedCurrParam = new String(b64Decoder.decodeBuffer(b64CurrParam));
                                docSignSpec = xmlOutputter.outputString(xmlBuilder.build(new ByteArrayInputStream(decodedCurrParam.getBytes())));
                            } catch (IOException ex) {
                                log.error("Cannot decode fourth parameter. " + ex.getMessage());
                            } catch (JDOMException e) {
                                log.error("Cannot parse sign-specification");
                            }
                        } else if (i == 3) {
                        }
                    }

                    docPostID = docName;
                    MultiDocumentThumb mdt = new MultiDocumentThumb();
                    mdt.setDocBytes(docBytes);
                    mdt.setDocName(docName);
                    mdt.setDocPostID(docPostID);
                    if (docSignSpec != null) {
                        mdt.setDocSignSpec(docSignSpec);
                    }
                    mdt.setToolTipText(docName);
                    sourceContent.add(mdt);
                } catch (Exception e) {
                    log.error("Cannot encode document data. Discarding document ...", e);
                }
            }

            log.info("SourceContent set");
        } else {
            log.warn("SourceContent not set");
        }
    }

    public void setSignatureReason(String param) {
        if (param == null || param.equals("")) {
            this.signatureReason = "";
        } else {
            this.signatureReason = param;
        }
    }

    public void setTrustStore() {
        try {
//            byte[] storeBytes = new BASE64Decoder().decodeBuffer(trustStoreB64);
//            ByteArrayInputStream trustStoreIS = new ByteArrayInputStream(storeBytes);
//            ByteArrayInputStream trustStoreIS = getClass().getResource
            trustStore = KeyStore.getInstance("JKS");
            trustStore.load(getClass().getResourceAsStream("/de/rub/dez6a3/jpdfsigner/resources/security/keystore.jks"), "keines".toCharArray());
            log.info("TrustStore set");
        } catch (Exception e) {
            log.warn(e);
            log.info("TrustStore cant be loaded");
        }
    }

    public void setPostDestination(String param) {
        try {
            postDestination = new URL(param);
            log.info("PostDestination set: " + param);
        } catch (Exception e) {
            log.warn(e);
        }
    }

    public void setSessionID(String param) {
        String errorTxt = "SessionID NOT set. Cannot parse. Must be in following format: XXXXX=XXXXX (just two values separated by one equals sign.";
        try {
            String[] sid = param.trim().split("=");
            if (sid.length == 2) {
                if (!sid[0].equals("") && !sid[1].equals("")) {
                    sessionID = sid;
                    log.info("SessionID set. Identifier: \'" + sessionID[0] + "\' - SessionID: \'" + sessionID[1] + "\'");
                } else {
                    log.info(errorTxt);
                }
            } else {
                log.info(errorTxt);
            }
        } catch (Exception e) {
            log.warn(e);
            log.info(errorTxt);
        }
    }

    public void setResultUrl(String param) {
        try {
            resultUrl = new URL(param);
            log.info("ResultUrl set: " + param);
        } catch (Exception e) {
            log.warn(e);
            log.info("ResultUrl cant be set");
        }
    }

    public void setResultTarget(String param) {
        try {
            if (param == null) {
                resultTarget = "_self";
            } else {
                resultTarget = param;
            }
            log.info("ResultTarget set: " + resultTarget);
        } catch (Exception e) {
            log.warn(e);
        }
    }

    public void setApplicationMode(String param) {
        Integer mode = 0;
        try {
            mode = new Integer(param.trim());
        } catch (Exception e) {
            if (param != null && !param.equals("")) {
                log.warn("Cannot parse mode-value to integer. Param is '" + param + "'. Please be sure the value is an integer! - forcing default-applicationmode 0 (Signature_Mode)");
            }
        }
        switch (mode) {
            case 0:
                applicationMode = mode;
                log.info("Applicationmode set to \"Signature_Mode\"");
                break;
            case 1:
                applicationMode = mode;
                log.info("Applicationmode set to \"Attach_User_Data_To_PDF_Mode\"");
                break;
            default:
                log.warn("Unknown applicationmode set: " + mode + " - forcing default-applicationmode 0 (Signature_Mode)");
                applicationMode = 0;
        }
    }

    public void setEmbedded(String param) {
        if (param != null) {
            embedded = new Boolean(param);
        }
        if (embedded) {
            log.info("Embedded set: Running applet embedded");
        } else {
            log.info("Embedded set: Running applet in external frame");
        }
    }

    public void setLogConfig(String param) {
        try {
            byte[] logConfig = new BASE64Decoder().decodeBuffer(param);
            Properties prop = new Properties();
            prop.load(new ByteArrayInputStream(logConfig));
            PropertyConfigurator.configure(prop);
            log.info("Properties-Log4J-Configfile successfully loaded. Logging is enabled.");
        } catch (Exception e) {
            BasicConfigurator.configure();
            log.warn("Cannot decode Base64 encoded Properties-Log4J-Configfile. Log information will be printed to the console! Please set a valid log4j-configuration(*.properties-file) to specify the logging mechanism!!");
        }
    }

//---------------------Getter-----------------------
//--------------------------------------------------
    public ArrayList<MultiDocumentThumb> getSourceContent() {
        return sourceContent;
    }

    public String getSignatureReason() {
        return this.signatureReason;
    }

    public KeyStore getTrustStore() {
        return trustStore;
    }

    public URL getPostDestination() {
        return postDestination;
    }

    public String[] getSessionID() {
        return sessionID;
    }

    public URL getResultUrl() {
        return resultUrl;
    }

    public String getResultTarget() {
        return resultTarget;
    }

    public int getApplicationMode() {
        return applicationMode;
    }

    public boolean getEmbedded() {
        return embedded;
    }
}
