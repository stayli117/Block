package net.people.lifecycle_android.block;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import net.people.lifecycle_android.R;
import net.people.lifecycle_android.databinding.BlockFrgBinding;
import net.people.lifecycle_android.databinding.WifiInfoItemBinding;


import java.util.ArrayList;

public class BlockFragment extends Fragment {


    private View root;
    private BlockFrgBinding dataBinding;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        dataBinding = DataBindingUtil.inflate(inflater, R.layout.block_frg, container, false);

        root = dataBinding.getRoot();


        return root;

    }

    public EasyRecyclerView easyRecyclerView;
    private RecyclerArrayAdapter<Block> adapter;

    private static final String TAG = "WifiFragment";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        easyRecyclerView = dataBinding.easyWifi;
        if (adapter == null) {
            adapter = new RecyclerArrayAdapter<Block>(getActivity()) {
                @Override
                public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {

                    return new MyVH(getContext(), R.layout.wifi_info_item, parent);
                }
            };
        }
        easyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        easyRecyclerView.setAdapter(adapter);

        dataBinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                WalletsMaster master = new WalletsMaster();
//                master.generateRandomSeed(getActivity().getApplicationContext());


//                SPUtils utils = SPUtils.getInstance();
//                String previousHash = utils.getString("previousHash");
//                long high = utils.getLong("high");
//                if (TextUtils.isEmpty(previousHash)) {
//                    previousHash = "00000";
//                    high = 1;
//                }
//                final long finalHigh = high;
//                final String finalPreviousHash = previousHash;
//                AsyncTask.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        NoobChain.main(finalHigh, finalPreviousHash);
//                    }
//                });
            }
        });

        NoobChain.getBlockChain().observe(this, new Observer<ArrayList<Block>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Block> blocks) {
                adapter.addAll(blocks);
            }
        });

    }


    class MyVH extends BaseBindingViewHolder<Block, WifiInfoItemBinding> {

        public MyVH(Context context, int resId, ViewGroup parent) {
            super(context, resId, parent);
        }

        @Override
        public WifiInfoItemBinding getBinding() {
            return super.getBinding();
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void setData(Block data) {
            super.setData(data);
            mBinding.llWifi.addView(getTextView("previousHash : " + data.previousHash));
            mBinding.llWifi.addView(getTextView("hash : " + String.valueOf(data.hash)));
            mBinding.llWifi.addView(getTextView("timeStamp : " + String.valueOf(data.timeStamp)));
            mBinding.llWifi.addView(getTextView("------------------------------------------------"));

        }

        @NonNull
        private TextView getTextView(String data) {
            TextView textView = new TextView(getContext());
            textView.setText(data);
            return textView;
        }
    }

    public class BaseBindingViewHolder<M, T extends ViewDataBinding> extends BaseViewHolder<M> {
        protected final T mBinding;

        public BaseBindingViewHolder(Context context, int resId) {
            this((T) DataBindingUtil.inflate(LayoutInflater.from(context), resId, null, false));
        }

        public BaseBindingViewHolder(T binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public BaseBindingViewHolder(Context context, int resId, ViewGroup parent) {
            this((T) DataBindingUtil.inflate(LayoutInflater.from(context), resId, parent, false));
        }

        public T getBinding() {
            return mBinding;
        }
    }


}
