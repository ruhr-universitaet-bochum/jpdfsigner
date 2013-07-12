/*
 * JPDFSigner - Sign PDFs online using smartcards (ICardAccessor.java)
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
package de.rub.dez6a3.jpdfsigner.model;

import de.rub.dez6a3.jpdfsigner.control.CardReader;
import de.rub.dez6a3.jpdfsigner.exceptiontypes.CardAccessorException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Provider;
import java.util.ArrayList;

/**
 *
 * @author dan
 */
public interface ICardAccessor {

    public static long CKF_RNG = 1;
    public static long CKF_WRITE_PROTECTED = 2;
    public static long CKF_LOGIN_REQUIRED = 4;
    public static long CKF_USER_PIN_INITIALIZED = 8;
    public static long CKF_RESTORE_KEY_NOT_NEEDED = 32;
    public static long CKF_CLOCK_ON_TOKEN = 64;
    public static long CKF_PROTECTED_AUTHENTICATION_PATH = 256;
    public static long CKF_DUAL_CRYPTO_OPERATIONS = 512;
    public static long CKF_TOKEN_INITIALIZED = 1024;
    public static long CKF_SECONDARY_AUTHENTICATION = 2048;
    public static long CKF_USER_PIN_COUNT_LOW = 4192;
    public static long CKF_USER_PIN_FINAL_TRY = 8384;
    public static long CKF_USER_PIN_LOCKED = 16768;
    public static long CKF_USER_PIN_TO_BE_CHANGED = 33536;
    public static long CKF_SO_PIN_COUNT_LOW = 67072;
    public static long CKF_SO_PIN_FINAL_TRY = 134144;
    public static long CKF_SO_PIN_LOCKED = 268288;
    public static long CKF_CKF_SO_PIN_TO_BE_CHANGED = 536576;

    public void setProvPath(String provPath);

    public String getProvPath();

    public void load() throws CardAccessorException;

    public void unload() throws CardAccessorException;

    public CardReader getSelectedReader();

    public void setSelectedReader(CardReader selectedReader);

    public String[] getStandardLibPath();

    public ArrayList<CardReader> getReaders() throws CardAccessorException;

    public Provider getProvider();

    public KeyStore getPKCS11KeyStore() throws KeyStoreException;

    public void resetPKCS11KeyStoreAndProvider();

    public long getTokenFlags(CardReader selectedReader) throws CardAccessorException;

    public boolean readTokenFlagsInfo(long flags, long flag) throws CardAccessorException;
}
