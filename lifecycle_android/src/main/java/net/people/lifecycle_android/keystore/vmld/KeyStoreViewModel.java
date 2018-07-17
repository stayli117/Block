package net.people.lifecycle_android.keystore.vmld;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.os.Build;
import android.support.annotation.RequiresApi;

import net.people.lifecycle_android.keystore.repo.KeyStoreRepo;

public class KeyStoreViewModel extends ViewModel {

    private final KeyStoreRepo repo;

    public KeyStoreViewModel() {
        repo = new KeyStoreRepo();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public LiveData<byte[]> getAuthKey() {
        return repo.getAuthKey();
    }

    private final MutableLiveData<byte[]> addressInput = new MutableLiveData<>();
    private final LiveData<Boolean> postalCode =
            Transformations.switchMap(addressInput, new Function<byte[], LiveData<Boolean>>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public LiveData<Boolean> apply(byte[] input) {
                    return repo.setAuthKey(input);
                }
            });

    @RequiresApi(api = Build.VERSION_CODES.M)
    public LiveData<Boolean> setAuthKey(byte[] authKey) {
        addressInput.setValue(authKey);
        return postalCode;
    }


}
