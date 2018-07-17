package net.people.wallet;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.WorkerThread;
import android.util.Log;
import android.util.SparseArray;

import net.people.platform.entities.WalletInfo;
import net.people.wallet.abstracts.BaseWalletManager;
import net.people.wallet.tools.SPUtils;
import net.people.wallet.tools.Utils;
import net.people.wallet.tools.manager.BRReportsManager;
import net.people.wallet.tools.manager.BRSharedPrefs;
import net.people.wallet.tools.security.SmartValidator;
import net.people.wallet.tools.threads.executor.BRExecutor;
import net.people.wallet.tools.util.Bip39Reader;
import net.people.wallet.tools.util.TrustedNode;
import net.people.wallet.wallets.bitcoin.WalletBchManager;
import net.people.wallet.wallets.bitcoin.WalletBitcoinManager;
import net.people.wallet.wallets.etherium.WalletEthManager;
import net.people.walletlibcore.BRCoreKey;
import net.people.walletlibcore.BRCoreMasterPubKey;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class WalletsMaster {


    private static final String TAG = WalletsMaster.class.getName();

    private static WalletsMaster instance;

    private List<BaseWalletManager> mWallets = new ArrayList<>();

    private WalletsMaster(Context app) {
    }

    public synchronized static WalletsMaster getInstance(Context app) {
        if (instance == null) {
            instance = new WalletsMaster(app);
        }


        return instance;
    }


    //return the needed wallet for the iso
    public BaseWalletManager getWalletByIso(Context app, String iso) {
//        Log.d(TAG, "getWalletByIso() Getting wallet by ISO -> " + iso);
        if (Utils.isNullOrEmpty(iso))
            throw new RuntimeException("getWalletByIso with iso = null, Cannot happen!");
        if (iso.equalsIgnoreCase("BTC"))
            return WalletBitcoinManager.getInstance(app);
        if (iso.equalsIgnoreCase("BCH"))
            return WalletBchManager.getInstance(app);
        if (iso.equalsIgnoreCase("ETH"))
            return WalletEthManager.getInstance(app);
        return null;
    }

    public BaseWalletManager getCurrentWallet(Context app) {
        return getWalletByIso(app, BRSharedPrefs.getCurrentWalletIso(app));
    }


    private byte[] createSecureRandomSeed(int numBytes) {
        return new SecureRandom().generateSeed(numBytes);
    }

    private String languageCode;

    // 获取词库
    private String[] getWords(Context ctx) {
        final String[] words;
        languageCode = Locale.getDefault().getLanguage();
        if (languageCode == null) languageCode = "en";
        List<String> list = Bip39Reader.bip39List(ctx, languageCode);
        words = list.toArray(new String[list.size()]);
        return words;
    }


    public String getPaperKey() {
        return SPUtils.getInstance().getString("zjc");
    }


    /**
     * @param ctx 创建钱包
     * @return
     */
    public synchronized boolean generateRandomSeed(final Context ctx) {
        final byte[] randomSeed = createSecureRandomSeed(16); // 获取随机数种子
        final String[] words = getWords(ctx); //  获取词库

        if (words.length != 2048) {
            new IllegalArgumentException("the list is wrong, size: " + words.length).printStackTrace();
            return false;
        }
        if (randomSeed.length != 16)
            throw new NullPointerException("failed to create the seed, seed length is not 128: " + randomSeed.length);


        // SOTP: 2018/6/28 jni 接口生成助记词  随机数  词库
        byte[] paperKeyBytes = BRCoreMasterPubKey.generatePaperKey(randomSeed, words);
        if (paperKeyBytes == null || paperKeyBytes.length == 0) {
            new NullPointerException("failed to encodeSeed").printStackTrace();
            return false;
        }
        // SOTP: 2018/7/4 WalletsMaster 字节数组转换为语言
        String[] splitPhrase = new String(paperKeyBytes).split(" ");
        if (splitPhrase.length != 12) {
            String languageCode = Locale.getDefault().getLanguage();
            if (languageCode == null) languageCode = "en";
            new NullPointerException("phrase does not have 12 words:" + splitPhrase.length + ", lang: " + languageCode).printStackTrace();
            return false;
        }
        boolean success = false;
        try {
            // SOTP: 2018/7/4 WalletsMaster 保存助记词
//            success = BRKeyStore.putPhrase(paperKeyBytes, ctx, BRConstants.PUT_PHRASE_NEW_WALLET_REQUEST_CODE);
            success = SPUtils.getInstance().putWallet("zjc", new String(paperKeyBytes));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        if (!success) return false;
        byte[] phrase;
        try {
            // SOTP: 2018/7/4 WalletsMaster 读取助记词
//            phrase = BRKeyStore.getPhrase(ctx, 0);
            phrase = SPUtils.getInstance().getString("zjc").getBytes();
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve the phrase even though at this point the system auth was asked for sure.");
        }
        if (Utils.isNullOrEmpty(phrase)) throw new NullPointerException("phrase is null!!");
        if (phrase.length == 0) throw new RuntimeException("phrase is empty");

        //// SOTP: 2018/6/28 jni   由助记词 获取种子

        byte[] seed = BRCoreKey.getSeedFromPhrase(phrase);


        if (seed == null || seed.length == 0) throw new RuntimeException("seed is null");

        // SOTP: 2018/6/28 jni   由种子生成私钥
        byte[] authKey = BRCoreKey.getAuthPrivKeyForAPI(seed);


        if (authKey == null || authKey.length == 0) {
            BRReportsManager.reportBug(new IllegalArgumentException("authKey is invalid"), true);
        }
        // SOTP: 2018/6/28 jni   保存私钥
//        BRKeyStore.putAuthKey(authKey, ctx);
        SPUtils.getInstance().put("authKey", new String(authKey));

        long walletCreationTime = (System.currentTimeMillis() / 1000);

        // SOTP: 2018/6/28 jni   保存钱包创建时间
//        BRKeyStore.putWalletCreationTime(walletCreationTime, ctx);
        SPUtils.getInstance().put("walletCreationTime", walletCreationTime);

        final WalletInfo info = new WalletInfo();
        info.creationDate = (int) walletCreationTime;
        BRExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
            @Override
            public void run() {
                // SOTP: 2018/6/28 保存钱包文件
//                KVStoreManager.getInstance().putWalletInfo(ctx, info); //push the creation time to the kv store

            }
        });

        // SOTP: 2018/6/28 WalletsMaster  直接使用助记词生成公钥
        //store the serialized in the KeyStore
        byte[] pubKey = new BRCoreMasterPubKey(paperKeyBytes, true).serialize();

        // SOTP: 2018/7/6 WalletsMaster 生成钱包地址
//        String generatedAddress = new BRCoreMasterPubKey(pubKey, false).getPubKeyAsCoreKey().address();


        // SOTP: 2018/6/28 WalletsMaster  保存公钥
//        BRKeyStore.putMasterPublicKey(pubKey, ctx);
        SPUtils.getInstance().put("pubKey", new String(pubKey));
        return true;

    }


    /**
     * @param context 上下文对象
     * @param first   是否为第一个助记词
     * @param word    助
     * @return 是否校验成功
     */
    public boolean isWordCorrect(Context context, boolean first, String word) {
        if (first) {
            String edit = Bip39Reader.cleanWord(word);
            int i = sparseArrayWords.keyAt(0);
            Log.e(TAG, "isWordCorrect: " + i);
            String s = sparseArrayWords.get(i);
            Log.e(TAG, "isWordCorrect: " + s);
            return SmartValidator.isWordValid(context, edit, languageCode) && edit.equalsIgnoreCase(s);
        } else {
            String edit = Bip39Reader.cleanWord(word);
            return SmartValidator.isWordValid(context, edit, languageCode) && edit.equalsIgnoreCase(sparseArrayWords.get(sparseArrayWords.keyAt(1)));
        }
    }

    private SparseArray<String> sparseArrayWords = new SparseArray<>();

    /**
     * @return 需要校验的助记词索引
     */
    public int[] randomWordsSetUp() {
        String words[] = getPaperKey().split(" ");

        final Random random = new Random();
        int n = random.nextInt(10) + 1;

        sparseArrayWords.append(n, words[n]);

        while (sparseArrayWords.get(n) != null) {
            n = random.nextInt(10) + 1;
        }

        sparseArrayWords.append(n, words[n]);
        int i = sparseArrayWords.keyAt(0);
        int i1 = sparseArrayWords.keyAt(1);

        return new int[]{i, i1};
    }


    /**
     * 恢复钱包
     *
     * @param app
     * @param authAsked
     * @param paperKeyBytes
     */
    // SOTP: 2018/7/4 PostAuth  钱包恢复方法
    public void onRecoverWalletAuth(Activity app, boolean authAsked, String paperKeyBytes) {

        String cleanPhrase = SmartValidator.cleanPaperKey(app, paperKeyBytes);
        if (!SmartValidator.isPaperKeyValid(app, cleanPhrase)) {
            return;
        }

//        WalletsMaster m = WalletsMaster.getInstance(InputWordsActivity.this);
//        m.wipeWalletButKeystore(app);
//        m.wipeKeyStore(app);
        SPUtils.getInstance().clear();
        // SOTP: 2018/7/4 WalletsMaster 擦除原来的钱包的所有信息

        // SOTP: 2018/7/4 InputWordsActivity  设置助记词


        // SOTP: 2018/7/4 InputWordsActivity  恢复


        if (Utils.isNullOrEmpty(paperKeyBytes)) {
            Log.e(TAG, "onRecoverWalletAuth: phraseForKeyStore is null or empty");
            BRReportsManager.reportBug(new NullPointerException("onRecoverWalletAuth: phraseForKeyStore is or empty"));
            return;
        }
        byte[] bytePhrase = new byte[0];

        try {
            boolean success = false;
            try {
                // SOTP: 2018/7/4 PostAuth 保存助记词
//                success = BRKeyStore.putPhrase(phraseForKeyStore.getBytes(),
//                        app, BRConstants.PUT_PHRASE_RECOVERY_WALLET_REQUEST_CODE);
//
                success = SPUtils.getInstance().putWallet("zjc", new String(paperKeyBytes));
            } catch (Exception e) {
                if (authAsked) {
                    Log.e(TAG, "onRecoverWalletAuth: WARNING!!!! LOOP");
                }
                return;
            }

            if (!success) {
                if (authAsked)
                    Log.e(TAG, "onRecoverWalletAuth, !success && authAsked");
            } else {
                if (cleanPhrase.length() != 0) {
                    BRSharedPrefs.putPhraseWroteDown(app, true);

                    // SOTP: 2018/6/28 jni   由助记词 获取种子
                    byte[] seed = BRCoreKey.getSeedFromPhrase(cleanPhrase.getBytes());

                    byte[] authKey = BRCoreKey.getAuthPrivKeyForAPI(seed);
                    // SOTP: 2018/6/28 jni   保存私钥
                    SPUtils.getInstance().put("authKey", new String(authKey));

                    byte[] pubKey = new BRCoreMasterPubKey(cleanPhrase.getBytes(), true).serialize();
                    // SOTP: 2018/6/28 WalletsMaster  保存公钥
                    SPUtils.getInstance().put("pubKey", new String(pubKey));
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            BRReportsManager.reportBug(e);
        } finally {
            Arrays.fill(bytePhrase, (byte) 0);
        }

    }

    public List<BaseWalletManager> getAllWallets() {
        return mWallets;
    }

    @WorkerThread
    public void updateFixedPeer(Context app, BaseWalletManager wm) {
        String node = BRSharedPrefs.getTrustNode(app, wm.getIso(app));
        if (!Utils.isNullOrEmpty(node)) {
            String host = TrustedNode.getNodeHost(node);
            int port = TrustedNode.getNodePort(node);
//        Log.e(TAG, "trust onClick: host:" + host);
//        Log.e(TAG, "trust onClick: port:" + port);
            boolean success = wm.useFixedNode(host, port);
            if (!success) {
                Log.e(TAG, "updateFixedPeer: Failed to updateFixedPeer with input: " + node);
            } else {
                Log.d(TAG, "updateFixedPeer: succeeded");
            }
        }
        wm.connect(app);

    }

    public boolean isIsoCrypto(Context app, String iso) {
        for (BaseWalletManager w : mWallets) {
            if (w.getIso(app).equalsIgnoreCase(iso)) return true;
        }
        return false;
    }

    // 支付的接口

    // 接收的接口

    // 获取 余额的接口


}
