package net.people.platform.tools;

import android.content.Context;
import android.util.Log;

import net.people.platform.entities.TxMetaData;
import net.people.platform.entities.WalletInfo;
import net.people.wallet.tools.SPUtils;
import net.people.wallet.tools.manager.BRReportsManager;
import net.people.wallet.tools.util.BRCompressor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


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
        try {
            compressed = BRCompressor.bz2Compress(result);
        } catch (IOException e) {
            BRReportsManager.reportBug(e);
            return;
        }
//        RemoteKVStore remoteKVStore = RemoteKVStore.getInstance(APIClient.getInstance(app));
//        ReplicatedKVStore kvStore = ReplicatedKVStore.getInstance(app, remoteKVStore);
//        long localVer = kvStore.localVersion(walletInfoKey).version;
//        long removeVer = kvStore.remoteVersion(walletInfoKey);
//        CompletionObject compObj = kvStore.set(localVer, removeVer, walletInfoKey, compressed, System.currentTimeMillis(), 0);
//        if (compObj.err != null) {
//            Log.e(TAG, "putWalletInfo: Error setting value for key: " + walletInfoKey + ", err: " + compObj.err);
//        }

        SPUtils.getInstance().put("WalletInfo", new String(compressed));

    }

    private WalletInfo getWalletInfo(Context app) {

        WalletInfo result = new WalletInfo();

        JSONObject json;

        try {
            String walletInfo = SPUtils.getInstance().getString("WalletInfo");
            byte[] decompressed = BRCompressor.bz2Extract(walletInfo.getBytes());
            if (decompressed == null) {
                Log.e(TAG, "getWalletInfo: decompressed value is null");
                return null;
            }
            json = new JSONObject(new String(decompressed));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        try {
            result.classVersion = json.getInt("classVersion");
            result.creationDate = json.getInt("creationDate");
            result.name = json.getString("name");
//            result.currentCurrency = json.getString("currentCurrency");
            Log.d(TAG, "getWalletInfo: " + result.creationDate + ", name: " + result.name);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "getWalletInfo: FAILED to get json value");
        }

        Log.e(TAG, "getWalletInfo: " + json);
        return result;
    }

//    public TxMetaData getTxMetaData(Context app, byte[] txHash) {
//        return getTxMetaData(app, txHash, null);
//    }


    public TxMetaData valueToMetaData(byte[] value) {
        TxMetaData result = new TxMetaData();
        JSONObject json;
        if (value == null) {
            Log.e(TAG, "valueToMetaData: value is null!");
            return null;
        }
        try {
            byte[] decompressed = BRCompressor.bz2Extract(value);
            if (decompressed == null) {
                Log.e(TAG, "getTxMetaData: decompressed value is null");
                return null;
            }
            json = new JSONObject(new String(decompressed));
        } catch (JSONException e) {

            Log.e(TAG, "valueToMetaData: " + new String(value) + ":", e);
            return null;
        } catch (Exception e) {
            Log.e(TAG, "valueToMetaData: ", e);
            return null;
        }

        try {
            result.classVersion = json.getInt("classVersion");
            result.blockHeight = json.getInt("bh");
            result.exchangeRate = json.getDouble("er");
            result.exchangeCurrency = json.getString("erc");
            result.comment = json.getString("comment");
            result.fee = json.getString("fr");
            result.txSize = json.getInt("s");
            result.creationTime = json.getInt("c");
            result.deviceId = json.getString("dId");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "getTxMetaData: FAILED to get json value");
        }
        return result;
    }


}
