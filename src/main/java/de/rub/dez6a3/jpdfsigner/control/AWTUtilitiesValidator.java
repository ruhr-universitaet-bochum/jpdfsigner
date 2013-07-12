/*
 * JPDFSigner - Sign PDFs online using smartcards (AWTUtilitiesValidator.java)
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

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Shape;
import java.awt.Window;
import java.lang.reflect.Method;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author dan
 */
public class AWTUtilitiesValidator {

    private static AWTUtilitiesValidator instance = null;
    private GraphicsConfiguration translucentGC = null;
    private boolean isTranslucencySupported = false;
    private boolean isAWTUtilitiesAvailable = false;
    public static Logger log = Logger.getLogger(AWTUtilitiesValidator.class);

    private AWTUtilitiesValidator() {
        translucentGC = searchTranslucentGraphicsConfig();

    }

    public static AWTUtilitiesValidator getInstance() {
        if (instance == null) {
            instance = new AWTUtilitiesValidator();
        }
        return instance;
    }

    public GraphicsConfiguration getTranslucentGraphicsConfig() {
        return translucentGC;
    }

    public boolean isTranslucencySupported() {
        return isTranslucencySupported;
    }

    public boolean isAWTUtlitiesAvailable() {
        return isAWTUtilitiesAvailable;
    }

    private Class<?> getAWTUtiltiesClass() throws ClassNotFoundException {
        return Class.forName("com.sun.awt.AWTUtilities");
    }

    public void setWindowOpaque(Window w, boolean b) {
        try {
            Class<?> awtUtilitiesClass = getAWTUtiltiesClass();
            Method mIsTranslucencyCapable = awtUtilitiesClass.getMethod("setWindowOpaque", Window.class, boolean.class);
            mIsTranslucencyCapable.invoke(null, w, b);
        } catch (Exception ex) {
            log.warn(ex.getMessage() + " - This happens when AWTUtilites arent available");
        }
    }

    public void setWindowShape(Window w, Shape s) {
        try {
            Class<?> awtUtilitiesClass = getAWTUtiltiesClass();
            Method mIsTranslucencyCapable = awtUtilitiesClass.getMethod("setWindowShape", Window.class, Shape.class);
            mIsTranslucencyCapable.invoke(null, w, s);
        } catch (Exception ex) {
            log.warn(ex.getMessage() + " - This happens when AWTUtilites arent available");
        }
    }

    private GraphicsConfiguration searchTranslucentGraphicsConfig() {
        log.info("Initializing translucency");
        GraphicsConfiguration gc = null;
        GraphicsConfiguration defaultGC = null;
        try {
            Class<?> awtUtilitiesClass = getAWTUtiltiesClass();
            Method mIsTranslucencyCapable = awtUtilitiesClass.getMethod("isTranslucencyCapable", GraphicsConfiguration.class);
            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice[] devices = env.getScreenDevices();
            Boolean isTranslucent = false;
            for (int i = 0; i < devices.length; i++) {
                GraphicsConfiguration[] configs = devices[i].getConfigurations();
                for (int j = 0; j < configs.length; j++) {
                    if (defaultGC == null) {
                        defaultGC = configs[j];
                    }
                    if (validateOSTranslucencySupport()) {
                        isTranslucent = (Boolean) mIsTranslucencyCapable.invoke(null, configs[j]);
                        if (isTranslucent && !isTranslucencySupported) {
                            log.info("Choosing GraphicsConfiguration with Translucency-Support No.: " + j);
                            isTranslucencySupported = true;
                            gc = configs[j];
                        }
                    } else {
                        gc = defaultGC;
                    }
                }
            }
            if (isTranslucencySupported) {
                log.info("Translucency loaded");
            } else {
                log.info("Can not load translucency");
            }
            isAWTUtilitiesAvailable = true;
        } catch (Throwable e) {
            log.info("No AWTUtilities - Can not load AWTUtilities");
        }
        return gc;
    }

    private boolean validateOSTranslucencySupport() {
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("windows")) {
            return true;
        }

        return false;
    }
}
