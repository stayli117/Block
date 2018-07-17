package net.people.app;

import android.app.Application;
import android.content.Context;

import net.people.WalletLibCore;
import net.people.wallet.tools.Utils;

public class MyApplication extends Application {




    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        Utils.init(this);
        WalletLibCore.init(this);
    }
}
