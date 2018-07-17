package net.people.lifecycle_android;

import android.content.Context;
import android.util.Log;

import net.people.lifecycle_android.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * BreadWallet
 * <p/>
 * Created by Mihail Gutan on <mihail@breadwallet.com> 6/22/17.
 * Copyright (c) 2017 breadwallet LLC
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
public class KVStoreManager {
    private static final String TAG = KVStoreManager.class.getName();

    private static KVStoreManager instance;
    String walletInfoKey = "wallet-info";

    private KVStoreManager() {
    }

    public static KVStoreManager getInstance() {
        if (instance == null) instance = new KVStoreManager();
        return instance;
    }

    public WalletInfo getWalletInfo(Context app) {
        WalletInfo result = new WalletInfo();
        return result;
    }

    public void putWalletInfo(Context app, WalletInfo info) {
        WalletInfo old = getWalletInfo(app);
        if (old == null) old = new WalletInfo(); //create new one if it's null

        //add all the params that we want to change
        if (info.classVersion != 0) old.classVersion = info.classVersion;
        if (info.creationDate != 0) old.creationDate = info.creationDate;
        if (info.name != null) old.name = info.name;

        //sanity check
        if (old.classVersion == 0) old.classVersion = 1;
        if (old.name != null) old.name = "My Bread";

        JSONObject obj = new JSONObject();
        byte[] result;
        try {
            obj.put("classVersion", old.classVersion);
            obj.put("creationDate", old.creationDate);
            obj.put("name", old.name);
            result = obj.toString().getBytes();

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "putWalletInfo: FAILED to create json");
            return;
        }

        if (result.length == 0) {
            Log.e(TAG, "putWalletInfo: FAILED: result is empty");
            return;
        }
        byte[] compressed;


    }

//    public TxMetaData getTxMetaData(Context app, byte[] txHash) {
//        return getTxMetaData(app, txHash, null);
//    }








    //null means no change
    private String getFinalValue(String newVal, String oldVal) {
        if (newVal == null) return null;
        if (oldVal == null) return newVal;
        if (newVal.equals(oldVal)) {
            return null;
        } else {
            return newVal;
        }
    }

    // -1 means no change
    private int getFinalValue(int newVal, int oldVal) {
        if (newVal <= 0) return -1;
        if (oldVal <= 0) return newVal;
        if (newVal == oldVal) {
            return -1;
        } else {
            return newVal;
        }
    }


    // -1 means no change
    private long getFinalValue(long newVal, long oldVal) {
        if (newVal <= 0) return -1;
        if (oldVal <= 0) return newVal;
        if (newVal == oldVal) {
            return -1;
        } else {
            return newVal;
        }
    }

    // -1 means no change
    private double getFinalValue(double newVal, double oldVal) {
        if (newVal <= 0) return -1;
        if (oldVal <= 0) return newVal;
        if (newVal == oldVal) {
            return -1;
        } else {
            return newVal;
        }
    }


    public static String txKey(byte[] txHash) {
        if (Utils.isNullOrEmpty(txHash)) return null;
        String hex = Utils.bytesToHex(CryptoHelper.sha256(txHash));
        if (Utils.isNullOrEmpty(hex)) return null;
        return "txn2-" + hex;
    }
}
