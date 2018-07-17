package net.people.wallet.wallets.etherium;


import android.content.Context;
import android.util.Log;

import net.people.platform.entities.CurrencyEntity;
import net.people.platform.entities.TxUiHolder;
import net.people.wallet.abstracts.BaseAddress;
import net.people.wallet.abstracts.BaseTransaction;
import net.people.wallet.abstracts.BaseWalletManager;
import net.people.wallet.abstracts.OnBalanceChangedListener;
import net.people.wallet.abstracts.OnTxListModified;
import net.people.wallet.abstracts.OnTxStatusUpdatedListener;
import net.people.wallet.abstracts.SyncListener;
import net.people.wallet.configs.WalletSettingsConfiguration;
import net.people.wallet.configs.WalletUiConfiguration;
import net.people.wallet.tools.SPUtils;
import net.people.wallet.tools.Utils;
import net.people.wallet.tools.manager.BRReportsManager;
import net.people.wallet.tools.manager.BRSharedPrefs;
import net.people.wallet.tools.manager.InternetManager;
import net.people.wallet.tools.sqlite.CurrencyDataSource;
import net.people.wallet.tools.threads.executor.BRExecutor;
import net.people.wallet.tools.util.Bip39Reader;
import net.people.wallet.wallets.CryptoTransaction;
import net.people.walletlibcore.BuildConfig;
import net.people.walletlibcore.ethereum.BREthereumAmount;
import net.people.walletlibcore.ethereum.BREthereumLightNode;
import net.people.walletlibcore.ethereum.BREthereumNetwork;
import net.people.walletlibcore.ethereum.BREthereumToken;
import net.people.walletlibcore.ethereum.BREthereumTransaction;
import net.people.walletlibcore.ethereum.BREthereumWallet;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static net.people.wallet.tools.BRConstants.ROUNDING_MODE;


/**
 * BreadWallet
 * <p/>
 * Created by Mihail Gutan on <mihail@breadwallet.com> 3/21/18.
 * Copyright (c) 2018 breadwallet LLC
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
public class WalletEthManager implements BaseWalletManager, BREthereumLightNode.ClientJSON_RPC {
    private static final String TAG = WalletEthManager.class.getSimpleName();

    private static String ISO = "ETH";
    public static final String ETH_SCHEME = "ethereum";

    private static final String mName = "Ethereum";

    private List<OnBalanceChangedListener> balanceListeners = new ArrayList<>();
    private List<OnTxStatusUpdatedListener> txStatusUpdatedListeners = new ArrayList<>();
    private List<SyncListener> syncListeners = new ArrayList<>();
    private List<OnTxListModified> txModifiedListeners = new ArrayList<>();

    private static WalletEthManager instance;
    private WalletUiConfiguration uiConfig;
    private WalletSettingsConfiguration settingsConfig;
    private final BigDecimal MAX_ETH = new BigDecimal("90000000000000000000000000"); // 90m ETH * 18 (WEI)
    private final BigDecimal ONE_ETH = new BigDecimal("1000000000000000000"); //1ETH = 1000000000000000000 WEI
    private BREthereumWallet mWallet;
    BREthereumLightNode.JSON_RPC node;
    private Context mContext;


    private WalletEthManager(final Context app, byte[] ethPubKey, BREthereumNetwork network) {
        uiConfig = new WalletUiConfiguration("#5e70a3", true, true, false, true);
        settingsConfig = new WalletSettingsConfiguration(app, ISO, getFingerprintLimits(app));

        if (Utils.isNullOrEmpty(ethPubKey)) {
            Log.e(TAG, "WalletEthManager: Using the paperKey to create");

            String paperKey = SPUtils.getInstance().getString("zjc");
            if (Utils.isNullOrEmpty(paperKey)) {
                Log.e(TAG, "WalletEthManager: paper key is empty too, no wallet!");
                instance = null;
                return;
            }

            String lang = Locale.getDefault().getLanguage();
            if (lang == null) lang = "en";

            List<String> list = Bip39Reader.bip39List(app, lang);
            String[] words = list.toArray(new String[list.size()]);

            if (words.length % Bip39Reader.WORD_LIST_SIZE != 0) {
                Log.e(TAG, "isPaperKeyValid: " + "The list size should divide by " + Bip39Reader.WORD_LIST_SIZE);
                BRReportsManager.reportBug(new IllegalArgumentException("words.length is not dividable by " + Bip39Reader.WORD_LIST_SIZE), true);
            }

            new BREthereumLightNode.JSON_RPC(this, network, paperKey, words);
            mWallet = node.getWallet();
            ethPubKey = mWallet.getAccount().getPrimaryAddressPublicKey();

            SPUtils.getInstance().put("pubKey", new String(ethPubKey));

        } else {
            Log.e(TAG, "WalletEthManager: Using the pubkey to create");
            new BREthereumLightNode.JSON_RPC(this, network, ethPubKey);
            mWallet = node.getWallet();
        }

        mContext = app;
        mWallet.estimateGasPrice();
        mWallet.setDefaultUnit(BREthereumAmount.Unit.ETHER_WEI);
        BREthereumWallet walletToken = node.createWallet(BREthereumToken.tokenBRD);
        walletToken.setDefaultUnit(BREthereumAmount.Unit.TOKEN_DECIMAL);
        node.connect();


    }

    public synchronized static WalletEthManager getInstance(Context app) {
        if (instance == null) {
            byte[] rawPubKey = SPUtils.getInstance().getString("pubKey").getBytes();

            if (Utils.isNullOrEmpty(rawPubKey)) {
                Log.e(TAG, "getInstance: rawPubKey is null");
                return null;
            }
            byte[] ethPubKey = rawPubKey;


            if (Utils.isNullOrEmpty(ethPubKey)) {
                //check if there is a master key and if not means the wallet isn't created yet
                if (Utils.isNullOrEmpty(rawPubKey)) {
                    return null;
                }
            }
            instance = new WalletEthManager(app, ethPubKey, BuildConfig.BITCOIN_TESTNET ? BREthereumNetwork.testnet : BREthereumNetwork.mainnet);

        }
        return instance;
    }

    private List<BigDecimal> getFingerprintLimits(Context app) {
        List<BigDecimal> result = new ArrayList<>();
        result.add(ONE_ETH.divide(new BigDecimal(100), getMaxDecimalPlaces(app), ROUNDING_MODE));
        result.add(ONE_ETH.divide(new BigDecimal(10), getMaxDecimalPlaces(app), ROUNDING_MODE));
        result.add(ONE_ETH);
        result.add(ONE_ETH.multiply(new BigDecimal(10)));
        result.add(ONE_ETH.multiply(new BigDecimal(100)));
        return result;
    }

    @Override
    public int getForkId() {
        //No need for ETH
        return -1;
    }

    @Override
    public boolean isAddressValid(String address) {
        return !Utils.isNullOrEmpty(address) && address.startsWith("0x");
    }

    @Override
    public byte[] signAndPublishTransaction(BaseTransaction tx, byte[] phrase) {
        CryptoTransaction cryptoTransaction = (CryptoTransaction) tx;
        mWallet.sign(cryptoTransaction.getEtherTx(), new String(phrase));
        mWallet.submit(cryptoTransaction.getEtherTx());
        String hash = tx.getEtherTx().getHash();
        return hash == null ? new byte[0] : hash.getBytes();
    }

    @Override
    public void addBalanceChangedListener(OnBalanceChangedListener listener) {
        if (listener != null && !balanceListeners.contains(listener))
            balanceListeners.add(listener);
    }

    @Override
    public void addTxStatusUpdatedListener(OnTxStatusUpdatedListener list) {
        if (list != null && !txStatusUpdatedListeners.contains(list))
            txStatusUpdatedListeners.add(list);
    }

    @Override
    public void addSyncListeners(SyncListener list) {
        if (list != null && !syncListeners.contains(list))
            syncListeners.add(list);
    }

    @Override
    public void addTxListModifiedListener(OnTxListModified list) {
        if (list != null && !txModifiedListeners.contains(list))
            txModifiedListeners.add(list);
    }

    @Override
    public long getRelayCount(byte[] txHash) {
        //todo implement
        return -1;
    }

    @Override
    public double getSyncProgress(long startHeight) {
        //Not needed for ETH, return fully synced always
        return 1.0;
    }

    @Override
    public double getConnectStatus() {
        //Not needed for ETH, return Connected always
        return 2;
    }

    @Override
    public void connect(Context app) {
        //Not needed for ETH
    }

    @Override
    public void disconnect(Context app) {
        //Not needed for ETH
    }

    @Override
    public boolean useFixedNode(String node, int port) {
        //Not needed for ETH
        return false;
    }

    @Override
    public void rescan() {
        //Not needed for ETH
    }

    @Override
    public BaseTransaction[] getTxs() {
        return (BaseTransaction[]) mWallet.getTransactions();
    }

    @Override
    public BigDecimal getTxFee(BaseTransaction tx) {
        return new BigDecimal(tx.getEtherTx().getGasLimit())
                .multiply(new BigDecimal(tx.getEtherTx().getGasPrice(BREthereumAmount.Unit.ETHER_WEI)));
    }

    @Override
    public BigDecimal getEstimatedFee(BigDecimal amount, String address) {
        BigDecimal fee;
        if (amount == null) return null;
        if (amount.compareTo(new BigDecimal(0)) == 0) {
            fee = new BigDecimal(0);
        } else {
            fee = new BigDecimal(mWallet.transactionEstimatedFee(amount.toPlainString()));
        }
        return fee;
    }

    @Override
    public BigDecimal getFeeForTransactionSize(BigDecimal size) {
        return null;
    }

    @Override
    public BaseAddress getTxAddress(BaseTransaction tx) {
        return null;
    }

    @Override
    public BigDecimal getMaxOutputAmount(Context app) {
        BigDecimal balance = getCachedBalance(app);
        if (balance.compareTo(new BigDecimal(0)) == 0) return new BigDecimal(0);
        BigDecimal fee = new BigDecimal(mWallet.transactionEstimatedFee(balance.toPlainString()));
        if (fee.compareTo(balance) > 0) return new BigDecimal(0);
        return balance.subtract(fee);
    }

    @Override
    public BigDecimal getMinOutputAmount(Context app) {
        return new BigDecimal(1); //1 WEI
    }

    @Override
    public BigDecimal getTransactionAmount(BaseTransaction tx) {
        return new BigDecimal(tx.getEtherTx().getAmount());
    }

    @Override
    public BigDecimal getMinOutputAmountPossible() {
        return new BigDecimal(1); //1 WEI
    }

    @Override
    public void updateFee(Context app) {

    }

    @Override
    public void refreshAddress(Context app) {
        Log.e(TAG, "refreshAddress: start");
        BaseAddress address = getReceiveAddress(app);
        Log.e(TAG, "refreshAddress: end");
        if (Utils.isNullOrEmpty(address.stringify())) {
            Log.e(TAG, "refreshAddress: WARNING, retrieved address:" + address);
        }
        BRSharedPrefs.putReceiveAddress(app, address.stringify(), getIso(app));
    }

    @Override
    public void refreshCachedBalance(final Context app) {
        BRExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
            @Override
            public void run() {
                BigDecimal balance = new BigDecimal(mWallet.getBalance());
                BRSharedPrefs.putCachedBalance(app, ISO, balance);
            }
        });
    }

    @Override
    public List<TxUiHolder> getTxUiHolders(Context app) {
        return null;
    }

    @Override
    public boolean containsAddress(String address) {
        return mWallet.getAccount().getPrimaryAddress().equalsIgnoreCase(address);
    }

    @Override
    public boolean addressIsUsed(String address) {
        //Not needed for ETH
        return false;
    }

    @Override
    public BaseAddress createAddress(String address) {
        return null;
    }

    @Override
    public boolean generateWallet(Context app) {
        //Not needed for ETH
        return false;
    }

    @Override
    public String getSymbol(Context app) {
//        return BRConstants.symbolEther;
        return ISO;
    }

    @Override
    public String getIso(Context app) {
        return ISO;
    }

    @Override
    public String getScheme(Context app) {
        return ETH_SCHEME;
    }

    @Override
    public String getName(Context app) {
        return mName;
    }

    @Override
    public String getDenomination(Context app) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public BaseAddress getReceiveAddress(Context app) {
        return null;
    }

    @Override
    public BaseTransaction createTransaction(BigDecimal amount, String address) {
        BREthereumTransaction tx = mWallet.createTransaction(address, amount.toPlainString(), BREthereumAmount.Unit.ETHER_WEI);
        return new CryptoTransaction(tx);
    }

    @Override
    public String decorateAddress(Context app, String addr) {
        return addr;
    }

    @Override
    public String undecorateAddress(Context app, String addr) {
        return addr;
    }

    @Override
    public int getMaxDecimalPlaces(Context app) {
        return 5;
    }

    @Override
    public BigDecimal getCachedBalance(Context app) {
        return BRSharedPrefs.getCachedBalance(app, getIso(app));
    }

    @Override
    public BigDecimal getTotalSent(Context app) {
        return new BigDecimal(0);
    }

    @Override
    public void wipeData(Context app) {
        Log.e(TAG, "wipeData: ");
    }

    @Override
    public void syncStarted() {
        //Not needed for ETH
    }

    @Override
    public void syncStopped(String error) {
        //Not needed for ETH
    }

    @Override
    public boolean networkIsReachable() {
        Context app = Utils.getContext();
        return InternetManager.getInstance().isConnected(app);
    }

    @Override
    public void setCachedBalance(Context app, BigDecimal balance) {
        BRSharedPrefs.putCachedBalance(app, getIso(app), balance);
        for (OnBalanceChangedListener listener : balanceListeners) {
            if (listener != null) listener.onBalanceChanged(getIso(app), balance);
        }

    }

    @Override
    public BigDecimal getMaxAmount(Context app) {
        return MAX_ETH;
    }

    @Override
    public WalletUiConfiguration getUiConfiguration() {
        return uiConfig;
    }

    @Override
    public WalletSettingsConfiguration getSettingsConfiguration() {
        return settingsConfig;
    }

    @Override
    public BigDecimal getFiatExchangeRate(Context app) {
        BigDecimal fiatData = getFiatForEth(app, new BigDecimal(1), BRSharedPrefs.getPreferredFiatIso(app));
        if (fiatData == null) return null;
        return fiatData; //dollars
    }

    @Override
    public BigDecimal getFiatBalance(Context app) {
        if (app == null) return null;
        return getFiatForSmallestCrypto(app, getCachedBalance(app), null);
    }

    @Override
    public BigDecimal getFiatForSmallestCrypto(Context app, BigDecimal amount, CurrencyEntity ent) {
        if (amount == null || amount.compareTo(new BigDecimal(0)) == 0) return amount;
        String iso = BRSharedPrefs.getPreferredFiatIso(app);
        if (ent != null) {
            //passed in a custom CurrencyEntity
            //get crypto amount
            BigDecimal cryptoAmount = amount.divide(ONE_ETH, 8, ROUNDING_MODE);
            //multiply by fiat rate
            return cryptoAmount.multiply(new BigDecimal(ent.rate));
        }
        //get crypto amount
        BigDecimal cryptoAmount = amount.divide(ONE_ETH, 8, ROUNDING_MODE);

        BigDecimal fiatData = getFiatForEth(app, cryptoAmount, iso);
        if (fiatData == null) return null;
        return fiatData;
    }

    @Override
    public BigDecimal getCryptoForFiat(Context app, BigDecimal fiatAmount) {
        if (fiatAmount == null || fiatAmount.compareTo(new BigDecimal(0)) == 0) return fiatAmount;
        String iso = BRSharedPrefs.getPreferredFiatIso(app);
        return getEthForFiat(app, fiatAmount, iso);

    }

    @Override
    public BigDecimal getCryptoForSmallestCrypto(Context app, BigDecimal amount) {
        if (amount == null || amount.compareTo(new BigDecimal(0)) == 0) return amount;
        return amount.divide(ONE_ETH, 8, ROUNDING_MODE);
    }

    @Override
    public BigDecimal getSmallestCryptoForCrypto(Context app, BigDecimal amount) {
        if (amount == null || amount.compareTo(new BigDecimal(0)) == 0) return amount;
        return amount.multiply(ONE_ETH);
    }

    @Override
    public BigDecimal getSmallestCryptoForFiat(Context app, BigDecimal amount) {
        if (amount == null || amount.compareTo(new BigDecimal(0)) == 0) return amount;
        String iso = BRSharedPrefs.getPreferredFiatIso(app);
        BigDecimal ethAmount = getEthForFiat(app, amount, iso);
        if (ethAmount == null) return null;
        return ethAmount.multiply(ONE_ETH);
    }

    //pass in a eth amount and return the specified amount in fiat
    //ETH rates are in BTC (thus this math)
    private BigDecimal getFiatForEth(Context app, BigDecimal ethAmount, String code) {
        //fiat rate for btc
        CurrencyEntity btcRate = CurrencyDataSource.getInstance(app).getCurrencyByCode(app, "BTC", code);
        //Btc rate for ether
        CurrencyEntity ethBtcRate = CurrencyDataSource.getInstance(app).getCurrencyByCode(app, getIso(app), "BTC");
        if (btcRate == null) {
            Log.e(TAG, "getUsdFromBtc: No USD rates for BTC");
            return null;
        }
        if (ethBtcRate == null) {
            Log.e(TAG, "getUsdFromBtc: No BTC rates for ETH");
            return null;
        }

        return ethAmount.multiply(new BigDecimal(ethBtcRate.rate)).multiply(new BigDecimal(btcRate.rate));
    }

    //pass in a fiat amount and return the specified amount in ETH
    //ETH rates are in BTC (thus this math)
    private BigDecimal getEthForFiat(Context app, BigDecimal fiatAmount, String code) {
        //fiat rate for btc
        CurrencyEntity btcRate = CurrencyDataSource.getInstance(app).getCurrencyByCode(app, "BTC", code);
        //Btc rate for ether
        CurrencyEntity ethBtcRate = CurrencyDataSource.getInstance(app).getCurrencyByCode(app, getIso(app), "BTC");
        if (btcRate == null) {
            Log.e(TAG, "getUsdFromBtc: No USD rates for BTC");
            return null;
        }
        if (ethBtcRate == null) {
            Log.e(TAG, "getUsdFromBtc: No BTC rates for ETH");
            return null;
        }

        return fiatAmount.divide(new BigDecimal(ethBtcRate.rate).multiply(new BigDecimal(btcRate.rate)), 8, ROUNDING_MODE);
    }


    /**
     * The JSON RPC callbacks
     * Implement JSON RPC methods synchronously
     */

    @Override
    public void assignNode(BREthereumLightNode node) {
        this.node = (BREthereumLightNode.JSON_RPC) node;
    }

    @Override
    public void getBalance(final int wid, final String address, final int rid) {


    }

    @Override
    public void getGasPrice(final int wid, final int rid) {

    }

    @Override
    public void getGasEstimate(final int wid, final int tid, final String to, final String amount, final String data, final int rid) {

    }

    @Override
    public void submitTransaction(final int wid, final int tid, final String rawTransaction, final int rid) {

    }

    @Override
    public void getTransactions(final String address, final int id) {

    }
}
