package net.people;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

import net.people.platform.APIClient;
import net.people.wallet.WalletsMaster;
import net.people.wallet.abstracts.BaseWalletManager;
import net.people.wallet.tools.BRConstants;
import net.people.wallet.tools.Utils;
import net.people.wallet.tools.crypto.Base32;
import net.people.wallet.tools.crypto.CryptoHelper;
import net.people.walletlibcore.BuildConfig;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.compress.utils.CharsetNames.ISO_8859_1;

public class WalletLibCore {
    public static String HOST = "api.breadwallet.com";
    private static Context mContext;
    public static final Map<String, String> mHeaders = new HashMap<>();
    public static final boolean IS_ALPHA = false;

    static {
        System.loadLibrary(BRConstants.NATIVE_LIB_NAME);
    }

    private static WalletLibCore libCore;

    public static void init(Context context) {
        mContext = context;
        Utils.init(context);
        libCore = new WalletLibCore();
    }


    public static WalletLibCore getLibCore() {
        return libCore;
    }

    public static Map<String, String> getBreadHeaders() {
        return mHeaders;
    }

    private WalletLibCore() {
        boolean isTestVersion = APIClient.BREAD_POINT.contains("staging") || APIClient.BREAD_POINT.contains("stage");
        boolean isTestNet = BuildConfig.BITCOIN_TESTNET;
        String lang = getCurrentLocale(mContext);
        mHeaders.put("X-Is-Internal", IS_ALPHA ? "true" : "false");
        mHeaders.put("X-Testflight", isTestVersion ? "true" : "false");
        mHeaders.put("X-Bitcoin-Testnet", isTestNet ? "true" : "false");
        mHeaders.put("Accept-Language", lang);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public String getCurrentLocale(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return ctx.getResources().getConfiguration().getLocales().get(0).getLanguage();
        } else {
            //noinspection deprecation
            return ctx.getResources().getConfiguration().locale.getLanguage();
        }
    }

    public static synchronized String generateWalletId() {

        // First, get the ETH wallet address
        BaseWalletManager ethWallet = WalletsMaster.getInstance(mContext).getWalletByIso(mContext, "ETH");
        String ethAddress = ethWallet.getReceiveAddress(mContext).stringify();

        try {
            byte[] ptext = ethAddress.getBytes(ISO_8859_1);

            // Encode the address to UTF-8
            String ethAddressEncoded = URLEncoder.encode(ethAddress, "UTF-8");

            // Remove the first 2 characters
            ethAddressEncoded = ethAddressEncoded.substring(2, ethAddressEncoded.length());

            // Get the shortened address bytes
            byte[] addressBytes = ethAddressEncoded.getBytes();

            // Run sha256 on the shortened address bytes
            byte[] sha256Address = CryptoHelper.sha256(addressBytes);


            // Get the first 10 bytes
            byte[] firstTenBytes = Arrays.copyOfRange(sha256Address, 0, 10);

            Base32 base32 = new Base32();
            String base32String = base32.encodeOriginal(firstTenBytes);
            base32String = base32String.toLowerCase();

            StringBuilder builder = new StringBuilder();

            Matcher matcher = Pattern.compile(".{1,4}").matcher(base32String);
            List<String> result = new ArrayList<>();
            while (matcher.find()) {
                String piece = base32String.substring(matcher.start(), matcher.end());
                result.add(piece);
                builder.append(piece + " ");
            }

            // Add the wallet ID to the request headers if it's not null or empty
            if (builder.toString() != null && !builder.toString().isEmpty()) {
                mHeaders.put("X-Wallet-ID", builder.toString());
            }

            return builder.toString();


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;

    }
}
