package net.people.lifecycle_android.keystore.repo;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.MainThread;
import android.support.annotation.RequiresApi;

import net.people.MyApplication;

import net.people.lifecycle_android.BRKeyStore;


/**
 * （一）使用LiveData，首先建立LiveData数据，一般继承自MutableLiveData。
 * MutableLiveData是LiveData的子类，添加了公共方法setValue和postValue，
 * 方便开发者直接使用。setValue必须在主线程调用。postValue可以在后台线程中调用。
 */
public class KeyStoreRepo {


    public KeyStoreRepo() {

    }

    @MainThread
    @RequiresApi(api = Build.VERSION_CODES.M)
    public LiveData<byte[]> getAuthKey() {
        final MutableLiveData<byte[]> data = new MutableLiveData<byte[]>() {
            @Override
            protected void onActive() {
                super.onActive();
            }

            @Override
            protected void onInactive() {
                super.onInactive();
            }
        };
        data.setValue(BRKeyStore.getAuthKey(MyApplication.getContext()));
        return data;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public LiveData<Boolean> setAuthKey(final byte[] authKey) {
        final MutableLiveData<Boolean> data = new MutableLiveData<>();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                boolean b = BRKeyStore.putAuthKey(authKey, MyApplication.getContext());
                data.postValue(b);
            }
        });
        return data;
    }

}
