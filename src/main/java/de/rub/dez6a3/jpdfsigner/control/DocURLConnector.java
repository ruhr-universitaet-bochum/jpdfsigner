/*
 * JPDFSigner - Sign PDFs online using smartcards (DocURLConnector.java)
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

import de.rub.dez6a3.jpdfsigner.model.IDocumentHandler;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.UUID;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import org.apache.log4j.Logger;

public class DocURLConnector implements IDocumentHandler, Runnable {

    public static int DOCUMENT_TO_DESTURL = 0;
    public static int DOCUMENT_TO_BYTEBUFFER = 1;
    private int choosenAction;
    private URL destURL = null;
    private int connectionTimeout;
    private int progressInPercent;
    private ByteArrayOutputStream pdfInBAOS;
    private Exception exception;
    private String ulResult = "";
    public boolean running;
    private String pdfDestIdentifier;
    private SSLContext sslContext;
    public static Logger log = Logger.getLogger(DocURLConnector.class);
    //Entity Arraylists:
    ArrayList<FileEntity> fileEntitys = new ArrayList<FileEntity>();
    ArrayList<StringEntity> stringEntitiys = new ArrayList<StringEntity>();

    public DocURLConnector() {
        connectionTimeout = 5000;
        exception = null;
        choosenAction = 0;
        progressInPercent = 0;
        running = false;
    }

    public void setDestURL(URL url) {
        destURL = url;
    }

    public String getULResult() {
        return ulResult;
    }

    public void setAction(int param) {
        choosenAction = param;
    }

    public void run() {
        try {
            switch (choosenAction) {
                case 0:
                    uploadPDF();
                    break;
                case 1:
                    downloadPDF();
                    break;
            }
        } catch (Exception e) {
        }
        running = false;
    }

    public int getProgressInPercent() {
        return progressInPercent;
    }

    public void throwExceptions() throws Exception {
        if (exception != null) {
            throw exception;
        }
    }

    public void setRunning(boolean param) {
        running = true;
    }

    public boolean isRunning() {
        return running;
    }

    public ByteArrayOutputStream getPDFInByteBuffer() {
        return pdfInBAOS;
    }

    public void setPDFDestIdentifier(String param) {
        pdfDestIdentifier = param;
    }

    public SSLContext createSSLContext() throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, KeyManagementException {
        TrustManager[] trustManagers = null;
        trustManagers = GlobalData.getTrustAllCertsTrustManager(); //creates trustall-truststore - JUST FOR TESTING!

//        KeyStore keystore = ParamValidator.getInstance().getTrustStore();
//        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//        trustManagerFactory.init(keystore);
//        trustManagers = trustManagerFactory.getTrustManagers();

        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, trustManagers, new SecureRandom());
        return sc;
    }

    private void uploadPDF() {
        BufferedReader rd = null;
        try {
            String boundary = "---------------------------"+UUID.randomUUID().toString().replaceAll("-", "");   //boundary definition
            String entityBoundary = "--" + boundary;   //used for separate every entity from another
            String finalBoundary = "--" + boundary + "--";  //used to indikate end of entitys
            String newLine = "\r\n";

            URLConnection conn = null;
            if (destURL.getProtocol().toLowerCase().equals("http")) {
                log.info("Connecting via Standard Socket");
                conn = (HttpURLConnection) destURL.openConnection();
                HttpURLConnection tConn = (HttpURLConnection) conn;
                tConn.setRequestMethod("POST");
            } else {
                log.info("Connecting via SSLSocket");
                conn = (HttpsURLConnection) destURL.openConnection();
                HttpsURLConnection tConn = (HttpsURLConnection) conn;
                tConn.setRequestMethod("POST");
                tConn.setSSLSocketFactory(sslContext.getSocketFactory());
                tConn.setHostnameVerifier(new HostnameVerifier() {

                    @Override
                    public boolean verify(String string, SSLSession ssls) {
                        return true;
                    }
                });
            }

            conn.setDoOutput(true);
            conn.setRequestProperty("Connection", "close");
            String[] sessionCookie = ParamValidator.getInstance().getSessionID();
            if (sessionCookie != null) {
                if (sessionCookie.length == 2) {
                    conn.setRequestProperty("Cookie", sessionCookie[0] + "=" + sessionCookie[1]);
                }
            }
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            log.info("Connection established");

            DataOutputStream raw = new DataOutputStream(conn.getOutputStream());

            long start = System.currentTimeMillis();
            log.info("Sending request...");
            for (StringEntity stringEntity : stringEntitiys) {
                raw.write(entityBoundary.getBytes());
                raw.write(newLine.getBytes());
                raw.write(stringEntity.getHTTPCode());
                raw.write(newLine.getBytes());
            }
            for (FileEntity fileEntity : fileEntitys) {
                raw.write(entityBoundary.getBytes());
                raw.write(newLine.getBytes());
                raw.write(fileEntity.getHTTPCode());
                raw.write(newLine.getBytes());
            }
            raw.write(finalBoundary.getBytes());
            raw.write(newLine.getBytes());
            raw.flush();
            raw.close();
            log.info("Request send in " + (System.currentTimeMillis() - start) + " ms");

            start = System.currentTimeMillis();

            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            rd = new BufferedReader(in);
            String line = "";
            String response = "";
            log.info("Fetching response...");
            while ((line = rd.readLine()) != null) {
                response += line + "\n";
            }

            log.info("------------------START RESPONSE---------------------");
            log.info(response);
            log.info("------------------END RESPONSE---------------------");
            log.info("Response received in " + (System.currentTimeMillis() - start) + " ms");

            String ecResponse = conn.getHeaderField("jpdfsigner_ec");
            if (ecResponse != null) {
                ulResult = ecResponse;
                log.info("Server jpdfsigner_ec header is: "+ecResponse);
            } else {
                ulResult = "-903";
                log.error("The server responded no 'jpdfsigner_ec'-header (errorcode). It is possible that the server has generated an errorpage. Here is the server's response: \n\n" + response);
            }

        } catch (FileNotFoundException ex) {
            exception = ex;
        } catch (UnknownHostException ex) {
            exception = ex;
        } catch (IOException ex) {
            exception = ex;
        } catch (Exception ex) {
            exception = ex;
        } finally {
            try {
                rd.close();
            } catch (Exception e) {
            }
        }
    }

    private void downloadPDF() {
        try {
            URLConnection connection = destURL.openConnection();
            if (connection instanceof HttpsURLConnection) {
                HttpsURLConnection httpsConn = (HttpsURLConnection) connection;                          //just trusts servers where the hostname cannot be verified
                HostnameVerifier trustAllHV = new HostnameVerifier() {                      //everything else is done in createSSLContext()-method  - JUST FOR TESTING!!
                    // A REFUSED SSL CONNECTION SHOULD NOT BE TRUSTED

                    public boolean verify(String string, SSLSession ssls) {
                        return true;
                    }
                };
                httpsConn.setHostnameVerifier(trustAllHV);

                httpsConn.setSSLSocketFactory(createSSLContext().getSocketFactory());
            }
            InputStream fileIS = connection.getInputStream();
            int fileLength = connection.getContentLength();
            pdfInBAOS = new ByteArrayOutputStream(1024);

            int cs = 0;
            int count = 0;
            do {
                pdfInBAOS.write(cs = fileIS.read());
                count = count + cs;
                progressInPercent = count / fileLength;
            } while (cs != -1);

        } catch (Exception ex) {
            exception = ex;
        }
    }

    public void setSSLContext(SSLContext sslContext) {
        this.sslContext = sslContext;
    }

    public void addFileEntity(FileEntity fe) {
        fileEntitys.add(fe);
    }

    public void addStringEntity(StringEntity se) {
        stringEntitiys.add(se);
    }

    public void start() {
        running = true;
        new Thread(this).start();
    }
}
