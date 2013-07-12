/*
 * JPDFSigner - Sign PDFs online using smartcards (SunPKCS11CardAccessor.java)
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

import de.rub.dez6a3.jpdfsigner.exceptiontypes.CardAccessorException;
import de.rub.dez6a3.jpdfsigner.model.ICardAccessor;
import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.Map;
import sun.security.pkcs11.SunPKCS11;
import sun.security.pkcs11.wrapper.PKCS11;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.apache.log4j.Logger;
import sun.security.pkcs11.wrapper.CK_C_INITIALIZE_ARGS;
import sun.security.pkcs11.wrapper.CK_SLOT_INFO;

/**
 *
 * @author dan
 */
public class SunPKCS11CardAccessor implements ICardAccessor {

    private Provider provider = null;
    private PKCS11 p11w = null;
    private KeyStore pkcs11KeyStore = null;
    private ArrayList<CardReader> cardReaders = null;
    private String provPath = "";
    private String[] cryptoLibPaths = {"/usr/local/lib/libcvP11.so", "C:\\windows\\system32\\cvP11.dll", "/usr/lib/opensc-pkcs11.so", "C:\\windows\\system32\\opensc-pkcs11.dll"};
    private CardReader selectedReader = null;
    public static Logger log = Logger.getLogger(SunPKCS11CardAccessor.class);

    public void load() throws CardAccessorException {
        try {
            p11w = loadPKCS11Wrapper(provPath);
        } catch (Exception e) {
            throw new CardAccessorException("INSTANCIATE_WRAPPER_ERROR");
        }
    }

    public void unload() throws CardAccessorException {
        try {
            cFinalize();
        } catch (Throwable t) {
            throw new CardAccessorException(t.getMessage());
        }
    }

    public String[] getStandardLibPath() {
        return cryptoLibPaths;
    }

    public CardReader getSelectedReader() {
        return selectedReader;
    }

    public void setSelectedReader(CardReader selectedReader) {
        this.selectedReader = selectedReader;
    }

    public String getProvPath() {
        return provPath;
    }

    public void setProvPath(String provPath) {
        this.provPath = provPath;
    }

    public ArrayList<CardReader> getReaders() throws CardAccessorException {
        cardReaders = new ArrayList<CardReader>();
        try {
            p11w.C_GetSlotList(false);
            log.info("Determining attached cardreaders: ");
            long[] slotList = p11w.C_GetSlotList(false);
            for (long currSlot : slotList) {
                boolean isHWSlot = false;
                boolean isRemDev = false;
                boolean tokenPresent = false;
                long flag = 2;
                if (flag - 4 >= 0) {
                    flag -= 4;
                    isHWSlot = true;
                }
                if (flag - 2 >= 0) {
                    flag -= 2;
                    isRemDev = true;
                }
                if (flag - 1 >= 0) {
                    flag -= 1;
                    tokenPresent = true;
                }
                CK_SLOT_INFO slotInfo = p11w.C_GetSlotInfo(currSlot);
                CardReader reader = new CardReader(currSlot, new String(slotInfo.slotDescription));
                try {
                    reader.setTokenFlags(p11w.C_GetTokenInfo(currSlot).flags);
                    log.info("Tokenflags found for Slot: (Description: " + new String(slotInfo.slotDescription).trim() + ") (ID: " + currSlot + ")");
                } catch (Exception e) {
                    if (e.getMessage().equals("CKR_TOKEN_NOT_PRESENT")) {
                        log.info("No token inserted for Slot: (Description: " + new String(slotInfo.slotDescription).trim() + ") (ID: " + currSlot + ")");
                    } else {
                        log.error(e.getMessage() + " - Cannot read tokenflags");
                    }
                }
                cardReaders.add(reader);
            }
        } catch (Throwable e) {
            throw new CardAccessorException(e.getMessage());
        }
        return cardReaders;
    }

    private void cFinalize() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, sun.security.pkcs11.wrapper.PKCS11Exception, Throwable {
        p11w.C_Finalize(null);
        Field moduleMapField = PKCS11.class.getDeclaredField("moduleMap");
        moduleMapField.setAccessible(true);
        Map<?, ?> moduleMap = (Map<?, ?>) moduleMapField.get(null);
        moduleMap.clear(); // force re-execution of C_Initialize next time
        p11w = null;
    }

    private PKCS11 loadPKCS11Wrapper(String pkcs11Path) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
        try {
            cFinalize();
        } catch (Throwable t) {
        }
        try {
            /*
             * Java 1.6
             */
            Method getInstanceMethod = PKCS11.class.getMethod("getInstance", String.class, String.class, CK_C_INITIALIZE_ARGS.class, Boolean.TYPE);
            CK_C_INITIALIZE_ARGS ck_c_initialize_args = new CK_C_INITIALIZE_ARGS();
            PKCS11 pkcs11 = (PKCS11) getInstanceMethod.invoke(null, pkcs11Path, "C_GetFunctionList", ck_c_initialize_args, false);
            return pkcs11;
        } catch (NoSuchMethodException e) {
            /*
             * Java 1.5
             */
            Method getInstanceMethod = PKCS11.class.getMethod("getInstance", String.class, CK_C_INITIALIZE_ARGS.class, Boolean.TYPE);
            PKCS11 pkcs11 = (PKCS11) getInstanceMethod.invoke(null, pkcs11Path, null, false);
            return pkcs11;
        }

    }

    public Provider getProvider() {
        return provider;
    }
    private String providerName = "PKCS11Provider";

    public KeyStore getPKCS11KeyStore() throws KeyStoreException {

        if (provider == null) {
            String params = "name = " + providerName + "\n"
                    + "slot = " + selectedReader.getSlotID() + "\n"
                    + "library = " + provPath;
            provider = new SunPKCS11(new ByteArrayInputStream(params.getBytes()));
            Security.addProvider(provider);
            pkcs11KeyStore = KeyStore.getInstance("PKCS11", provider);
        }
        return pkcs11KeyStore;
    }

    public void resetPKCS11KeyStoreAndProvider() {
        Security.removeProvider("SunPKCS11-" + providerName);
        pkcs11KeyStore = null;
        provider = null;
    }

    public long getTokenFlags(CardReader selectedReader) throws CardAccessorException {
        long flags = -1;
        try {
            flags = p11w.C_GetTokenInfo(selectedReader.getSlotID()).flags;
        } catch (Exception e) {
            throw new CardAccessorException(e.getMessage());
        }
        return flags;
    }

    public boolean readTokenFlagsInfo(long flags, long flag) throws CardAccessorException {

        long val = ICardAccessor.CKF_CKF_SO_PIN_TO_BE_CHANGED;
        if (flags - val >= 0) {
            flags -= val;
            if (flag == val) {
                return true;
            }
        }

        val = ICardAccessor.CKF_SO_PIN_LOCKED;
        if (flags - val >= 0) {
            flags -= val;
            if (flag == val) {
                return true;
            }
        }

        val = ICardAccessor.CKF_SO_PIN_FINAL_TRY;
        if (flags - val >= 0) {
            flags -= val;
            if (flag == val) {
                return true;
            }
        }

        val = ICardAccessor.CKF_SO_PIN_COUNT_LOW;
        if (flags - val >= 0) {
            flags -= val;
            if (flag == val) {
                return true;
            }
        }

        val = ICardAccessor.CKF_USER_PIN_TO_BE_CHANGED;
        if (flags - val >= 0) {
            flags -= val;
            if (flag == val) {
                return true;
            }
        }

        val = ICardAccessor.CKF_USER_PIN_LOCKED;
        if (flags - val >= 0) {
            flags -= val;
            if (flag == val) {
                return true;
            }
        }

        val = ICardAccessor.CKF_USER_PIN_FINAL_TRY;
        if (flags - val >= 0) {
            flags -= val;
            if (flag == val) {
                return true;
            }
        }

        val = ICardAccessor.CKF_USER_PIN_COUNT_LOW;
        if (flags - val >= 0) {
            flags -= val;
            if (flag == val) {
                return true;
            }
        }

        val = ICardAccessor.CKF_SECONDARY_AUTHENTICATION;
        if (flags - val >= 0) {
            flags -= val;
            if (flag == val) {
                return true;
            }
        }

        val = ICardAccessor.CKF_TOKEN_INITIALIZED;
        if (flags - val >= 0) {
            flags -= val;
            if (flag == val) {
                return true;
            }
        }

        val = ICardAccessor.CKF_DUAL_CRYPTO_OPERATIONS;
        if (flags - val >= 0) {
            flags -= val;
            if (flag == val) {
                return true;
            }
        }

        val = ICardAccessor.CKF_PROTECTED_AUTHENTICATION_PATH;
        if (flags - val >= 0) {
            flags -= val;
            if (flag == val) {
                return true;
            }
        }

        val = ICardAccessor.CKF_CLOCK_ON_TOKEN;
        if (flags - val >= 0) {
            flags -= val;
            if (flag == val) {
                return true;
            }
        }

        val = ICardAccessor.CKF_RESTORE_KEY_NOT_NEEDED;
        if (flags - val >= 0) {
            flags -= val;
            if (flag == val) {
                return true;
            }
        }

        val = ICardAccessor.CKF_USER_PIN_INITIALIZED;
        if (flags - val >= 0) {
            flags -= val;
            if (flag == val) {
                return true;
            }
        }

        val = ICardAccessor.CKF_LOGIN_REQUIRED;
        if (flags - val >= 0) {
            flags -= val;
            if (flag == val) {
                return true;
            }
        }

        val = ICardAccessor.CKF_WRITE_PROTECTED;
        if (flags - val >= 0) {
            flags -= val;
            if (flag == val) {
                return true;
            }
        }

        val = ICardAccessor.CKF_RNG;
        if (flags - val >= 0) {
            flags -= val;
            if (flag == val) {
                return true;
            }
        }

        return false;
//        throw new CardAccessorException("No valid PKCS11-Flag given to check. Please use one of the static attributes of the ICardAccessor interface");
    }
}
