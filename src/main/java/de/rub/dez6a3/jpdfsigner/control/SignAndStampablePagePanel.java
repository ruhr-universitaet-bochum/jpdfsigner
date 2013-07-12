/*
 * JPDFSigner - Sign PDFs online using smartcards (SignAndStampablePagePanel.java)
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

import com.sun.pdfview.PagePanel;
import de.rub.dez6a3.jpdfsigner.exceptiontypes.SignerException;
import de.rub.dez6a3.jpdfsigner.control.language.LanguageFactory;
import de.rub.dez6a3.jpdfsigner.model.IDocumentHandler;
import de.rub.dez6a3.jpdfsigner.model.IDocumentSigner;
import de.rub.dez6a3.jpdfsigner.model.IPDFProcessor;
import de.rub.dez6a3.jpdfsigner.view.DecoratedJOptionPane;
import de.rub.dez6a3.jpdfsigner.view.RootGlassPane;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.font.TextAttribute;
import java.awt.geom.RoundRectangle2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.text.AttributedString;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

public class SignAndStampablePagePanel extends PagePanel {

    public static int Auto_Stamp = 0;
    public static int Manual_Stamp = 1;
    private float x = 0;
    private float y = 0;
    private float dragWidth = 0;
    private float dragHeight = 0;
    private float cursorSpacerX = 0;
    private float cursorSpacerY = 0;
    private float oldMulti = 0;
    private boolean onTop = false;
    private boolean stampable = false;
    private boolean firstClick = false;
    private int pressedBtn = 0;
    private int origSize = 0;
    private int size = 0;
    private Dimension maxStamperSize = null;
    private Dimension minStamperSize = null;
    private Font fontBold = null;
    private Font fontRegular = null;
    private IDocumentSigner signer;
    private IPDFProcessor pdfProcessor = null;
    private Configurator config;
    private ByteArrayOutputStream signedPDF = null;
    private boolean fixed = false;
    private Thread thread = null;
    private Rectangle stamperPos = null;
    private int stampOnPage = 1;
    private boolean showWaitAnimation = false;
    private SignAndStampablePagePanel pThis = this;
    private MouseListener ml = null;
    private MouseMotionListener mml = null;
    public static Logger log = Logger.getLogger(SignAndStampablePagePanel.class);

    public SignAndStampablePagePanel(IDocumentSigner s, IPDFProcessor pdfProcessor) {
        signer = s;
        this.pdfProcessor = pdfProcessor;
        config = Configurator.getInstance();
        fontBold = new Font("Arial", Font.BOLD, 12);
        fontRegular = new Font("Arial", Font.PLAIN, 12);
        pThis = this;
    }

    public IPDFProcessor getPdfProcessor() {
        return pdfProcessor;
    }

    public void setPdfProcessor(IPDFProcessor pdfProcessor) {
        this.pdfProcessor = pdfProcessor;
    }

    private void installListeners() {
        if (ml == null) {
            ml = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    pressedBtn = e.getButton();
                    if (e.getButton() != 1) {
                        if (stampable && !fixed) {
                            firstClick = true;
                            onTop = true;
                            dragHeight = minStamperSize.height * getOrigMultiplicator();
                            dragWidth = minStamperSize.width * getOrigMultiplicator();
                            size = getSize().width;
                            cursorSpacerX = 0;
                            cursorSpacerY = 0;
                            x = e.getX();
                            y = e.getY();
                            oldMulti = getOrigMultiplicator();
                            repaint();
                        }
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (stampable && !fixed) {
                        x = e.getX();
                        y = e.getY();
                        repaint();
                        if (e.getButton() != 1) {
                            cursorSpacerX = dragWidth;
                            cursorSpacerY = dragHeight;
                        } else if (e.getButton() == 1 && onTop) {
                            if (!firstClick) {
                                //-131
                                DecoratedJOptionPane.showMessageDialog(null, LanguageFactory.getText(LanguageFactory.SASPP_DRAW_STAMP), LanguageFactory.getText(LanguageFactory.INFO_HL), JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                try {
                                    config.getGUISignView().initSigner(pThis);
                                } catch (Exception ex) {
                                    if (ex.getMessage().equals("sign_error")) {
                                        //-132
                                        DecoratedJOptionPane.showMessageDialog(null, LanguageFactory.getText(LanguageFactory.SASPP_ERROR_DURING_SIGN), LanguageFactory.getText(LanguageFactory.ERROR_HL), JOptionPane.ERROR_MESSAGE);
                                    } else if (ex.getMessage().equals("upload_error")) {
                                        //-133
                                        DecoratedJOptionPane.showMessageDialog(null, LanguageFactory.getText(LanguageFactory.SASPP_ERROR_DURING_UPLOAD), LanguageFactory.getText(LanguageFactory.ERROR_HL), JOptionPane.ERROR_MESSAGE);
                                    } else if (ex.getMessage().equals("server_down")) {
                                        //-134
                                        DecoratedJOptionPane.showMessageDialog(null, LanguageFactory.getText(LanguageFactory.SASPP_UPLOAD_SERVER_NOT_REACHABLE), LanguageFactory.getText(LanguageFactory.ERROR_HL), JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                            }
                        }
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    onTop = false;
                    repaint();
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    onTop = true;
                    repaint();
                }
            };
        }
        if (mml == null) {
            mml = new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (stampable && !fixed) {
                        if (pressedBtn != 1) {
                            if (e.getX() - x >= minStamperSize.width * getOrigMultiplicator() && e.getX() - x <= maxStamperSize.width * getOrigMultiplicator()) {
                                dragWidth = e.getX() - x;
                            } else if (e.getX() - x < minStamperSize.width * getOrigMultiplicator()) {
                                dragWidth = minStamperSize.width * getOrigMultiplicator();
                            } else if (e.getX() - x > maxStamperSize.width * getOrigMultiplicator()) {
                                dragWidth = maxStamperSize.width * getOrigMultiplicator();
                            }
                            if (e.getY() - y >= minStamperSize.height * getOrigMultiplicator() && e.getY() - y <= maxStamperSize.height * getOrigMultiplicator()) {
                                dragHeight = e.getY() - y;
                            } else if (e.getY() - y < minStamperSize.height * getOrigMultiplicator()) {
                                dragHeight = minStamperSize.height * getOrigMultiplicator();
                            } else if (e.getY() - y > maxStamperSize.height * getOrigMultiplicator()) {
                                dragHeight = maxStamperSize.height * getOrigMultiplicator();
                            }
                            repaint();
                        }
                    }
                }

                @Override
                public void mouseMoved(MouseEvent e) {
                    if (stampable && !fixed) {
                        if (x - dragWidth * getPaintMultiplicator() < 0 || y - dragHeight * getPaintMultiplicator() < 0) {
                            onTop = false;
                        } else {
                            onTop = true;
                        }
                        x = e.getX();
                        y = e.getY();
                        repaint();
                    }
                }
            };
        }
        addMouseListener(ml);
        addMouseMotionListener(mml);
    }

    public void upload() {
        IDocumentHandler connector = new DocURLConnector();
        connector.setDestURL(ParamValidator.getInstance().getPostDestination());
        connector.setAction(DocURLConnector.DOCUMENT_TO_DESTURL);

        connector.setSSLContext(GlobalData.uploadSSLContext);
        for (MultiDocumentThumb mdt : config.getMultiDocCtrl().getDocuments()) {
            byte[] signedDocBytes = mdt.getNewDocBytes();
            if (signedDocBytes != null) {
                connector.addFileEntity(new FileEntity(mdt.getNewDocBytes(), mdt.getDocName(), mdt.getDocPostID(), "application/pdf"));
            }
        }
        connector.start();
        while (connector.isRunning()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                log.error(ex);
                DecoratedJOptionPane.showMessageDialog(this, LanguageFactory.getText(LanguageFactory.SASPP_UPLOADMODULE_ERROR), LanguageFactory.getText(LanguageFactory.ERROR_HL), JOptionPane.ERROR_MESSAGE);
                Configurator.closeProgram("-944");
            }
        }
        try {
            connector.throwExceptions();
            String ulResult = connector.getULResult();
            boolean success = false;
            try {
                String[] errorCodes = ulResult.split(",");
                for (String errorCode : errorCodes) {
                    if (errorCode.trim().equals("0")) {
                        success = true;
                        break;
                    }
                }
            } catch (Exception e) {
                log.warn(e);
            }
            config.getGUISignView().setGlassPaneAnimationRunning(false);
            config.getGUISignView().setGlassPaneLoadingText("Signaturvorgang abgeschlossen");
            config.getGUISignView().setGlassPaneInfoText("Bitte beachten Sie die Mitteillungen auf der Folgeseite");
            if (ulResult.getBytes().length <= 255) {    //Errorcode darf nicht größer als 255 bytes sein
                if (success) {
                    DecoratedJOptionPane.showMessageDialog(this, LanguageFactory.getText(LanguageFactory.SASPP_SUCCESS_UPLOAD), LanguageFactory.getText(LanguageFactory.INFO_HL), JOptionPane.INFORMATION_MESSAGE);
                    Configurator.getInstance().closeProgram(ulResult);
                } else {
                    DecoratedJOptionPane.showMessageDialog(this, LanguageFactory.getText(LanguageFactory.SASPP_NO_ZERO_ERRROCODE_IN_RESPONSE), LanguageFactory.getText(LanguageFactory.ERROR_HL), JOptionPane.ERROR_MESSAGE);
                    Configurator.closeProgram(ulResult);
                }
            } else {
                DecoratedJOptionPane.showMessageDialog(this, LanguageFactory.getText(LanguageFactory.SASPP_WRONG_RESPONSE), LanguageFactory.getText(LanguageFactory.WARN_HL), JOptionPane.WARNING_MESSAGE);
                Configurator.closeProgram("-902");
            }
        } catch (FileNotFoundException ex) {
            log.error(ex);
            DecoratedJOptionPane.showMessageDialog(this, LanguageFactory.getText(LanguageFactory.SASPP_NO_RESPONSE), LanguageFactory.getText(LanguageFactory.ERROR_HL), JOptionPane.ERROR_MESSAGE);
            Configurator.closeProgram("-925");
        } catch (UnknownHostException ex) {
            log.error(ex);
            DecoratedJOptionPane.showMessageDialog(this, LanguageFactory.getText(LanguageFactory.SASPP_NO_RESPONSE), LanguageFactory.getText(LanguageFactory.ERROR_HL), JOptionPane.ERROR_MESSAGE);
            Configurator.closeProgram("-925");
        } catch (Exception ex) {
            log.error(ex, ex);
            DecoratedJOptionPane.showMessageDialog(this, LanguageFactory.getText(LanguageFactory.SASPP_GENERAL_ERROR), LanguageFactory.getText(LanguageFactory.ERROR_HL), JOptionPane.ERROR_MESSAGE);
            Configurator.closeProgram("-944");
        }
    }

    public void setStamperPos(Rectangle param, String stampOnPage, boolean visible) {
        if (visible) {
            if (param == null) {
                stamperPos = getStamperPos();
            } else {
                stamperPos = param;
            }
            if (stampOnPage == null) {
                this.stampOnPage = pdfProcessor.getCurrentPageNum();

            } else {
                Integer validatedPageNmbr = 1;
                try {
                    validatedPageNmbr = new Integer(stampOnPage);
                } catch (Exception e) {
                    try {
                        validatedPageNmbr = pdfProcessor.getPageCount();
                    } catch (Exception ex) {
                    }
                }
                this.stampOnPage = validatedPageNmbr;
            }
            log.info("Creating visible signature on page '" + this.stampOnPage + "' with rectangle: " + stamperPos);
        } else {
            stamperPos = null;
            log.info("Creating invisible signature");
        }
    }
    private ArrayList<ByteArrayOutputStream> signedPDFs = new ArrayList<ByteArrayOutputStream>();

    public ArrayList<ByteArrayOutputStream> getSignedPDFs() {
        return signedPDFs;
    }

    public void doSign() throws SignerException {
        try {
            setDocStampable(false);
            SAXBuilder builder = new SAXBuilder();
            RootGlassPane gp = null;
            if (config.getPdfViewer().getGlassPane() instanceof RootGlassPane) {
                gp = (RootGlassPane) config.getPdfViewer().getGlassPane();
            }
            MultiDocumentThumbController mdtCtrl = config.getMultiDocCtrl();
            MultiDocumentThumb[] mdts = mdtCtrl.getDocuments().toArray(new MultiDocumentThumb[0]);
            int i = 1;
            for (MultiDocumentThumb mdt : mdts) {
                if (gp != null) {
                    gp.setLoadingText(LanguageFactory.getText(LanguageFactory.GUISIGNVIEW_LOADINGTXT_WAIT_MSG));
                    gp.setInfoText(LanguageFactory.getText(LanguageFactory.GUISIGNVIEW_INFOTXT_SIGNING) + " " + i + "/" + mdtCtrl.getMultiDocumentCount() + " (" + mdt.getDocName() + ")");
                    i++;
                }

                Document doc = builder.build(new ByteArrayInputStream(mdt.getDocSignSpec().getBytes()));
                Element root = doc.getRootElement();
                Element stampPos = root.getChild("stampPosition");
                String visibleSig = stampPos.getAttributeValue("visibleSignature");
                String x = stampPos.getChildText("x");
                String y = stampPos.getChildText("y");
                String width = stampPos.getChildText("width");
                String height = stampPos.getChildText("height");
                String page = stampPos.getChildText("page");
                config.setPDFFile(mdt.getDocBytes());
                pdfProcessor.loadPDFByteBuffer(ByteBuffer.wrap(mdt.getDocBytes()));
                setStamperPos(new Rectangle(new Integer(x), new Integer(y), new Integer(width), new Integer(height)), page, new Boolean(visibleSig));
                byte[] signedDoc = signer.doSign(mdt.getDocBytes(), stamperPos, stampOnPage).toByteArray();
                mdt.setNewDocBytes(signedDoc);
            }
        } catch (Exception e) {
            log.error(e, e);
            throw new SignerException(e.getMessage());
        }
    }

    public void setOrigSize(int os, int minStamp, int maxStamp) {
        origSize = os;
        maxStamperSize = new Dimension((origSize / 100) * maxStamp, (origSize / 100) * maxStamp);
        minStamperSize = new Dimension((origSize / 100) * minStamp, (origSize / 100) * minStamp);
    }

    public void setDocStampable(boolean bool) {
        if (bool) {
            installListeners();
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        } else {
            if (ml != null) {
                removeMouseListener(ml);
            }
            if (mml != null) {
                removeMouseMotionListener(mml);
            }
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
        stampable = bool;
        repaint();
    }

    public boolean isDocFixed() {
        return fixed;
    }

    public void setDocFixed(boolean bool) {
        fixed = bool;
    }

    public Rectangle getAutoRectangle() {
        int leftx;
        int lefty;
        int rightx;
        int righty;
        leftx = Math.round(new Float((getSize().getWidth() * getOrigMultiplicator()) * new Float("0.08")));
        lefty = Math.round(new Float(((getSize().getHeight() * getOrigMultiplicator()) * new Float("0.08"))));
        rightx = Math.round(new Float((getSize().getWidth() * getOrigMultiplicator()) * new Float("0.3")));
        righty = Math.round(new Float(((getSize().getHeight() * getOrigMultiplicator()) * new Float("0.3"))));
        return new Rectangle(leftx, lefty, rightx, righty);
    }

    private float getPaintMultiplicator() {
        float value;
        if (size == 0) {
            value = 1f;
        } else {
            value = (float) getSize().width / (float) size;
        }
        return value;
    }

    private float getOrigMultiplicator() {
        float value;
        if (origSize == 0) {
            value = 1f;
        } else {
            value = (float) ((getSize().width + getSize().height) / 2f) / (float) origSize;
        }
        return value;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (onTop && stampable && firstClick) {
            paintStamper(g);
        } else if (fixed) {
            paintStamper(g);
        }
    }

    public Rectangle getStamperPos() {
        int leftx;
        int lefty;
        int rightx;
        int righty;
        leftx = Math.round((x / getOrigMultiplicator()) - (dragWidth / oldMulti));
        lefty = Math.round((getHeight() / getOrigMultiplicator()) - (y / getOrigMultiplicator()));
        rightx = Math.round(x / getOrigMultiplicator());
        righty = Math.round((getHeight() / getOrigMultiplicator()) - ((y / getOrigMultiplicator()) - (dragHeight / oldMulti)));
        return new Rectangle(leftx, lefty, rightx, righty);
    }
    int animAngle = 0;
    Thread animThread = null;

    public Thread createAnimationThread() {
        Thread animRunner = new Thread() {
            public void run() {
                while (showWaitAnimation) {
                    animAngle += 1;
                    repaint();
                    try {
                        Thread.sleep(33);
                    } catch (InterruptedException ex) {
                        log.error(ex);
                    }
                }
            }
        };
        return animRunner;
    }

    public void runAnimation(boolean param) {
        showWaitAnimation = param;
        if (param) {
            if (animThread != null) {
                animThread.start();
            } else {
                animThread = createAnimationThread();
                animThread.start();
            }
        } else {
            showWaitAnimation = false;
            animThread = null;
        }
    }

    private void paintAnimation(Graphics g) {
        int segments = 20;
        int spacer = 5;
        int singleAngleWidth = 360 / segments;



        for (int i = 1; i <= segments; i++) {
            if (i % 2 == 0) {
                g.setColor(new Color(0, 0, 0, 10));
            } else {
                g.setColor(new Color(0, 0, 0, 20));
            }
            int arcx = (int) (x - (cursorSpacerX * getPaintMultiplicator())) + spacer;
            int arcy = (int) (y - (cursorSpacerY * getPaintMultiplicator())) + spacer;
            int arcw = (int) (dragWidth * getPaintMultiplicator()) - (spacer * 2);
            int arch = (int) (dragHeight * getPaintMultiplicator()) - (spacer * 2);
            int diameter = 0;
            if (arcw > arch) {
                diameter = arch;
            } else {
                diameter = arcw;
            }
            arcx = arcx + ((arcw / 2) - (diameter / 2));        //calculate center position for animation
            arcy = arcy + ((arch / 2) - (diameter / 2));

            g.fillArc(arcx, arcy, diameter, diameter, singleAngleWidth * i + animAngle, singleAngleWidth);
        }

    }

    private void paintStamper(Graphics g) {
        try {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            float cornerSize = 50;
            float percent;
            if (dragWidth * getPaintMultiplicator() < dragHeight * getPaintMultiplicator()) {
                percent = ((float) dragWidth * getPaintMultiplicator() / (float) maxStamperSize.width * getOrigMultiplicator());
            } else {
                percent = ((float) dragHeight * getPaintMultiplicator() / (float) maxStamperSize.height * getOrigMultiplicator());
            }

            RoundRectangle2D.Float rect = new RoundRectangle2D.Float(
                    x - (cursorSpacerX * getPaintMultiplicator()),
                    y - (cursorSpacerY * getPaintMultiplicator()),
                    dragWidth * getPaintMultiplicator(),
                    dragHeight * getPaintMultiplicator(),
                    Math.round(new Float(cornerSize * percent)),
                    Math.round(new Float(cornerSize * percent)));

            float borderSize = 6.0f;

            RoundRectangle2D.Float rect2 = new RoundRectangle2D.Float(
                    (x - (borderSize * getOrigMultiplicator())) - ((cursorSpacerX) * getPaintMultiplicator()),
                    (y - (borderSize * getOrigMultiplicator())) - ((cursorSpacerY) * getPaintMultiplicator()),
                    (dragWidth * getPaintMultiplicator()) + ((borderSize * getOrigMultiplicator()) * 2),
                    (dragHeight * getPaintMultiplicator()) + ((borderSize * getOrigMultiplicator()) * 2),
                    Math.round(new Float((cornerSize - 3) * percent)),
                    Math.round(new Float((cornerSize - 3) * percent)));


            g2.setColor(new Color(255, 255, 255, 100));
            g2.fill(rect2);
            g2.setColor(new Color(141, 174, 16, 100));
            g2.fill(rect);

            g2.setStroke(new TextStroke("   R   U   H   R   -   U   N   I   V   E   R   S   I   T   Ä   T       B   O   C   H   U   M       * * * *    ", fontRegular.deriveFont(7.0f * getOrigMultiplicator()), false, true));
            g2.setColor(new Color(0, 53, 96, 250));
            g2.draw(rect2);



            g2.setColor(new Color(255, 255, 255, 30));
            float divide;
            if (dragWidth < dragHeight) {
                divide = dragWidth;
            } else {
                divide = dragHeight;
            }

            String stamperLabel = "RUB";
            AttributedString stamperLabelAS = new AttributedString(stamperLabel);
            stamperLabelAS.addAttribute(TextAttribute.FOREGROUND, new Color(255, 255, 255, 30));
            float fontSize;
            fontSize = divide * getPaintMultiplicator() / 1.95f;
            if (fontSize > 100) {
                fontSize = 100f;
            }
            Font fontB = fontBold.deriveFont(fontSize);
            Font fontR = fontRegular.deriveFont(fontSize);
            stamperLabelAS.addAttribute(TextAttribute.FONT, fontB, 0, 2);
            stamperLabelAS.addAttribute(TextAttribute.FONT, fontR, 2, 3);

            FontMetrics fm = getFontMetrics(fontB);
            float fontx = (x - (cursorSpacerX * getPaintMultiplicator())) + (((dragWidth * getPaintMultiplicator()) / 2) - (fm.stringWidth(stamperLabel) / 2));
            float fonty = (y - (cursorSpacerY * getPaintMultiplicator())) + ((dragHeight * getPaintMultiplicator()) / 2) + (fm.getHeight() / 3);
            g2.drawString(stamperLabelAS.getIterator(), fontx, fonty);

            if (showWaitAnimation) {
                paintAnimation(g);  //paints wait-animation
            }

        } catch (Exception e) {
            log.error(e);
        }

    }
    int angle = 0;
}
