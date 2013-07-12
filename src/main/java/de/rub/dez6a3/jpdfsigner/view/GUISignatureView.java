/*
 * JPDFSigner - Sign PDFs online using smartcards (GUISignatureView.java)
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
package de.rub.dez6a3.jpdfsigner.view;

import com.itextpdf.text.pdf.PdfPKCS7;
import de.rub.dez6a3.jpdfsigner.control.GlobalData;
import de.rub.dez6a3.jpdfsigner.control.language.LanguageFactory;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.ByteArrayInputStream;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import org.apache.log4j.Logger;
import sun.security.pkcs.PKCS7;

/**
 *
 * @author dan
 */
public class GUISignatureView extends JPanel {

    private JTree certsTree = null;
    private DefaultMutableTreeNode rootNode = null;
    private DefaultTreeModel treeModel = null;
    public static Logger log = Logger.getLogger(GUISignatureView.class);

    public GUISignatureView() {
        Dimension tdim = new Dimension(350, 20);
        setPreferredSize(tdim);
        rootNode = new DefaultMutableTreeNode(LanguageFactory.getText(LanguageFactory.GUISIGNATUREVIEW_SIGNATURES));
        treeModel = new DefaultTreeModel(rootNode);
        certsTree = new JTree(treeModel);
        certsTree.putClientProperty("JTree.lineStyle", "None");
        setLayout(new BorderLayout());
        JScrollPane spane = new JScrollPane();
        spane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        add(spane, BorderLayout.CENTER);
        certsTree.setRootVisible(false);
        certsTree.setShowsRootHandles(true);
        spane.setViewportView(certsTree);
        setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, Color.white, new Color(0, 0, 0, 0), new Color(0, 0, 0, 0), new Color(0, 0, 0, 100)));
    }

    @Override
    public void setVisible(boolean param) {
//        if (param) {
//            for (JPanel currBar : barViewControl) {
//                currBar.setVisible(false);
//            }
//        }
        super.setVisible(param);
    }
    int currRev = 0;

    public void updateTreeNodes() {
        try {
            rootNode.removeAllChildren();

            List<X509Certificate[]> allCerts = GlobalData.getSignerChain();
            if (allCerts == null || allCerts.size() == 0) {
                DefaultMutableTreeNode tn = new DefaultMutableTreeNode("Das Dokument enthält keine Signaturen");
                rootNode.add(tn);
            } else {
                for (X509Certificate[] currCerts : allCerts) {
                    currRev = 0;
                    rootNode.add(getNodesFromCertificate(currCerts, 0));
                }
            }
        } catch (Exception e) {
            log.warn("No signatures found.");
        }
        treeModel.reload(rootNode);
        log.info("Signature-tree updated");
    }

    private DefaultMutableTreeNode getNodesFromCertificate(X509Certificate[] currCerts, int depth) {
        DefaultMutableTreeNode currNode = new DefaultMutableTreeNode("Issuer: " + PdfPKCS7.getSubjectFields(currCerts[depth]).getField("CN"));

        List<String> dataList = determineCertsData(currCerts[depth]);
        for (String currData : dataList) {
            currNode.add(new DefaultMutableTreeNode(currData));
        }
        if (depth < (currCerts.length - 1)) {
            currNode.add(getNodesFromCertificate(currCerts, depth + 1));
        } else {
            currNode.add(new DefaultMutableTreeNode("Issuer: Self-Signed"));
        }
        return currNode;
    }

    private List<String> determineCertsData(X509Certificate currCert) {
        HashMap allCertFields = PdfPKCS7.getSubjectFields(currCert).getFields();
        List<String> dataList = new ArrayList<String>();
        Iterator it = allCertFields.keySet().iterator();
        int i = 0;
        while (it.hasNext()) {
            Object currKey = it.next();
            ArrayList<String> currValues = (ArrayList) allCertFields.get(currKey);
            for (int k = 0; k < currValues.size(); k++) {
                if (currValues.size() > 1) {
                    dataList.add((String) currKey + " " + new Integer(k + 1).toString() + "= " + currValues.get(k));
                } else {
                    dataList.add((String) currKey + "= " + currValues.get(k));
                }
            }
            i++;
        }
        return dataList;
    }
}
