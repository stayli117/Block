package net.people.wallet.abstracts;

import android.content.Context;
import android.support.annotation.WorkerThread;

import net.people.platform.entities.CurrencyEntity;
import net.people.platform.entities.TxUiHolder;
import net.people.wallet.configs.WalletSettingsConfiguration;
import net.people.wallet.configs.WalletUiConfiguration;

import java.math.BigDecimal;
import java.util.List;

/**
 * BreadWallet
 * <p/>
 * Created by Mihail Gutan on <mihail@breadwallet.com> 1/22/18.
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
public interface BaseWalletManager {

    /**
     * The methods that are annotated with @WorkerThread might block so can't be called in the UI Thread
     */

    //get the core wallet
    int getForkId();

    boolean isAddressValid(String address);

    @WorkerThread
        //sign and publish the tx using the seed
    byte[] signAndPublishTransaction(BaseTransaction tx, byte[] seed);

    void addBalanceChangedListener(OnBalanceChangedListener list);

    void addTxStatusUpdatedListener(OnTxStatusUpdatedListener list);

    void addSyncListeners(SyncListener list);

    void addTxListModifiedListener(OnTxListModified list);

    @WorkerThread
        //get confirmation number
    long getRelayCount(byte[] txHash);

    @WorkerThread
        //get the syncing progress
    double getSyncProgress(long startHeight);

    @WorkerThread
        //get the connection status 0 - Disconnected, 1 - Connecting, 2 - Connected, 3 - Unknown
    double getConnectStatus();

    @WorkerThread
        //Connect the wallet (PeerManager for Bitcoin)
    void connect(Context app);

    @WorkerThread
        //Disconnect the wallet (PeerManager for Bitcoin)
    void disconnect(Context app);

    @WorkerThread
        //Use a fixed favorite node to connect
    boolean useFixedNode(String node, int port);

    @WorkerThread
        //Rescan the wallet (PeerManager for Bitcoin)
    void rescan();

    @WorkerThread
        //get a list of all the transactions sorted by timestamp (e.g. BRCoreTransaction[] for BTC)
    BaseTransaction[] getTxs();

    //get the transaction fee
    BigDecimal getTxFee(BaseTransaction tx);

    //get the transaction fee
    BigDecimal getEstimatedFee(BigDecimal amount, String address);

    //get the fee for the transaction size
    BigDecimal getFeeForTransactionSize(BigDecimal size);

    //get the transaction to address
    BaseAddress getTxAddress(BaseTransaction tx);

    //get the maximum output amount possible for this wallet
    BigDecimal getMaxOutputAmount(Context app);

    //get the reasonable minimum output amount
    BigDecimal getMinOutputAmount(Context app);

    //get the transaction amount (negative if sent)
    BigDecimal getTransactionAmount(BaseTransaction tx);

    //get the reasonable minimum output amount (not smaller than dust)
    BigDecimal getMinOutputAmountPossible();

    @WorkerThread
        //updates the fee for the current wallet (from an API)
    void updateFee(Context app);

    //get the core address and store it locally
    void refreshAddress(Context app);

    //get the core balance and store it locally
    void refreshCachedBalance(Context app);

    //get a list of all the transactions UI holders sorted by timestamp
    List<TxUiHolder> getTxUiHolders(Context app);

    //return true if this wallet owns this address
    boolean containsAddress(String address);

    //return true if this wallet already used this address
    boolean addressIsUsed(String address);

    //return the new address object
    BaseAddress createAddress(String address);

    @WorkerThread
        //generate the wallet if needed
    boolean generateWallet(Context app);

    //get the currency symbol e.g. Bitcoin - ₿, Ether - Ξ
    String getSymbol(Context app);

    //get the currency denomination e.g. Bitcoin - BTC, Ether - ETH
    String getIso(Context app);

    //get the currency scheme (bitcoin or bitcoincash)
    String getScheme(Context app);

    //get the currency name e.g. Bitcoin
    String getName(Context app);

    //get the currency denomination e.g. BCH, mBCH, Bits
    String getDenomination(Context app);

    @WorkerThread
        //get the wallet's receive address
    BaseAddress getReceiveAddress(Context app);

    BaseTransaction createTransaction(BigDecimal amount, String address);

    //decorate an address to a particular currency, if needed (like BCH address format)
    String decorateAddress(Context app, String addr);

    //convert to raw address to a particular currency, if needed (like BCH address format)
    String undecorateAddress(Context app, String addr);

    //get the number of decimal places to use for this currency
    int getMaxDecimalPlaces(Context app);

    //get the cached balance in the smallest unit:  satoshis.
    BigDecimal getCachedBalance(Context app);

    //get the total amount sent in the smallest crypto unit:  satoshis.
    BigDecimal getTotalSent(Context app);

    //wipe all wallet data
    void wipeData(Context app);

    void syncStarted();

    void syncStopped(String error);

    boolean networkIsReachable();

    /**
     * @param balance - the balance to be saved in the smallest unit.(e.g. satoshis, wei)
     */
    void setCachedBalance(Context app, BigDecimal balance);

    //return the maximum amount for this currency
    BigDecimal getMaxAmount(Context app);

    /**
     * @return - the wallet's Ui configuration
     */
    WalletUiConfiguration getUiConfiguration();
    /**
     * @return - the wallet's Settings configuration (Settings items)
     */
    WalletSettingsConfiguration getSettingsConfiguration();

    /**
     * @return - the wallet's currency exchange rate in the user's favorite fiat currency (e.g. dollars)
     */
    BigDecimal getFiatExchangeRate(Context app);

    /**
     * @return - the total balance amount in the user's favorite fiat currency (e.g. dollars)
     */
    BigDecimal getFiatBalance(Context app);

    /**
     * @param amount - the smallest denomination amount in current wallet's crypto (e.g. Satoshis)
     * @param ent    - provide a currency entity if needed
     * @return - the fiat value of the amount in crypto (e.g. dollars)
     * or null if there is no fiat exchange data from the API yet
     */
    BigDecimal getFiatForSmallestCrypto(Context app, BigDecimal amount, CurrencyEntity ent);

    /**
     * @param amount - the amount in the user's favorite fiat currency (e.g. dollars)
     * @return - the crypto value of the amount in the current favorite denomination (e.g. BTC, mBTC, Bits..)
     * or null if there is no fiat exchange data from the API yet
     */
    BigDecimal getCryptoForFiat(Context app, BigDecimal amount);

    /**
     * @param amount - the smallest denomination amount in crypto (e.g. satoshis)
     * @return - the crypto value of the amount in the current favorite denomination (e.g. BTC, mBTC, Bits..)
     */
    BigDecimal getCryptoForSmallestCrypto(Context app, BigDecimal amount);

    /**
     * @param amount - the crypto value of the amount in the current favorite denomination (e.g. BTC, mBTC, Bits..)
     * @return - the smallest denomination amount in crypto (e.g. satoshis)
     */
    BigDecimal getSmallestCryptoForCrypto(Context app, BigDecimal amount);

    /**
     * @param amount - the fiat amount (e.g. dollars)
     * @return - the crypto value of the amount in the smallest denomination (e.g. satothis)
     * or null if there is no fiat exchange data from the API yet
     */
    BigDecimal getSmallestCryptoForFiat(Context app, BigDecimal amount);


}
