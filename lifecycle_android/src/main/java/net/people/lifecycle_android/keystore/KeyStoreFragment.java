package net.people.lifecycle_android.keystore;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import net.people.lifecycle_android.R;
import net.people.lifecycle_android.databinding.KeystoreBinding;
import net.people.lifecycle_android.keystore.vmld.KeyStoreViewModel;

import java.util.Objects;

public class KeyStoreFragment extends Fragment {


    private KeyStoreViewModel viewModel;
    private KeystoreBinding dataBinding;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        // Security protocol
        // type:OSU Server-only authenticated layer 2 Encryption Network.Used for Hotspot 2.0.

    }




    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.e(TAG, "onActivityCreated: " + viewModel);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.keystore, container, false);
        return dataBinding.getRoot();

    }

    private static final String TAG = "KeyStoreFragment";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.e(TAG, "onViewCreated: ");
        viewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(KeyStoreViewModel.class);

        viewModel.setAuthKey("djbfjakankf".getBytes()).observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {

                if (aBoolean != null && aBoolean) {
                    viewModel.getAuthKey().observe(KeyStoreFragment.this, new Observer<byte[]>() {
                        @Override
                        public void onChanged(@Nullable byte[] bytes) {
                            Log.e(TAG, "onChanged: " + new String(bytes));
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "保存 " + aBoolean, Toast.LENGTH_SHORT).show();
                }

            }
        });


    }


}
