package net.people.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import net.people.wallet.WalletsMaster;
import net.people.wallet.abstracts.BaseWalletManager;
import net.people.wallet.tools.manager.BRSharedPrefs;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WalletsMaster master = WalletsMaster.getInstance(this);
        boolean b = master.generateRandomSeed(getApplicationContext());

        Toast.makeText(this, b ? "钱包生成成功" : "钱包生成失败", Toast.LENGTH_SHORT).show();


        String paperKey = master.getPaperKey();
        Log.e(TAG, "onCreate: " + paperKey);
        String words[] = paperKey.split(" ");
        int[] ints = master.randomWordsSetUp();
        boolean wordCorrect = master.isWordCorrect(this, true, words[ints[0]]);
        Log.e(TAG, "onCreate: -------> " + wordCorrect);
        if (wordCorrect) {
            Toast.makeText(this, "校验成功1", Toast.LENGTH_SHORT).show();
            boolean correct = master.isWordCorrect(this, false, words[ints[1]]);
            Toast.makeText(this, "校验成功2", Toast.LENGTH_SHORT).show();
        } else {
            Log.e(TAG, "onCreate: -------> ");
        }


        BaseWalletManager manager = master.getWalletByIso(this, "BTC");
        manager.refreshAddress(this); // 刷新地址
        String mReceiveAddress = BRSharedPrefs.getReceiveAddress(this, manager.getIso(this));
        String decorated = manager.decorateAddress(this, mReceiveAddress);

        Toast.makeText(this, "地址" + decorated, Toast.LENGTH_SHORT).show();

    }


}
