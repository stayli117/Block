package net.people.lifecycle_android;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import net.people.lifecycle_android.databinding.WifiBinding;
import net.people.lifecycle_android.databinding.WifiInfoItemBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WifiFragment extends Fragment {


    private View root;
    private WifiBinding dataBinding;

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

        dataBinding = DataBindingUtil.inflate(inflater, R.layout.wifi, container, false);

        root = dataBinding.getRoot();
        return root;

    }

    public EasyRecyclerView easyRecyclerView;
    private RecyclerArrayAdapter<ScanResult> adapter;

    private static final String TAG = "WifiFragment";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        easyRecyclerView = dataBinding.easyWifi;
        if (adapter == null) {
            adapter = new RecyclerArrayAdapter<ScanResult>(getActivity()) {
                @Override
                public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {


                    return new MyVH(getContext(), R.layout.wifi_info_item, parent);
                }
            };
        }
        easyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        easyRecyclerView.setAdapter(adapter);

        FragmentActivity activity = getActivity();
        if (activity == null) return;
        final WifiManager mWifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (mWifiManager == null) return;

        if (!mWifiManager.isWifiEnabled()) {//wifi未打开 执行打开操作
            mWifiManager.setWifiEnabled(true);//同样的执行关闭操作的话： mWifiManager.setWifiEnabled(false);
        }


        /**
         * 扫描热点,扫描时耗时操作，如果界面中需要展示进度条的话，建议将扫描操作放在子线程中操作
         */

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    boolean b = mWifiManager.startScan();
                    if (b) {
                        // 得到扫描结果
                        List<ScanResult> mWifiList = mWifiManager.getScanResults();
                        // 得到配置好的网络连接,列表中可能出现重复的热点，并且可能是ssid为空的热点，根据需求情况 自行过滤
                        List<WifiConfiguration> mWifiConfiguration = mWifiManager.getConfiguredNetworks();
                        // 查看扫描结果
//            StringBuilder scan = lookUpScan(mWifiList);
//            Log.e(TAG, "onCreate: " + scan);

                        //将搜索到的wifi根据信号从强到弱进行排序

                        List<String> list = new ArrayList<>();
                        final List<ScanResult> results = sortByLevel(mWifiList);

                        for (ScanResult result : results) {
                            list.add(result.SSID);
                        }

                        Log.e(TAG, "onCreate: " + results.size());
                        Log.e(TAG, "onCreate: " + list);
                        dataBinding.easyWifi.post(new Runnable() {
                            @Override
                            public void run() {
                                adapter.clear();
                                adapter.addAll(results);
                            }
                        });
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    //将搜索到的wifi根据信号从强到弱进行排序
    private List<ScanResult> sortByLevel(List<ScanResult> list) {
        Collections.sort(list, new Comparator<ScanResult>() {
            @Override
            public int compare(ScanResult o1, ScanResult o2) {
                return o2.level - o1.level;
            }
        });

        return list;
    }


    class MyVH extends BaseBindingViewHolder<ScanResult, WifiInfoItemBinding> {

        public MyVH(Context context, int resId, ViewGroup parent) {
            super(context, resId, parent);
        }

        @Override
        public WifiInfoItemBinding getBinding() {
            return super.getBinding();
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void setData(ScanResult data) {
            super.setData(data);
            mBinding.llWifi.addView(getTextView(data.SSID));
//            mBinding.llWifi.addView(getTextView(data.BSSID));
//            mBinding.llWifi.addView(getTextView(data.capabilities));
//            mBinding.llWifi.addView(getTextView(String.valueOf(data.centerFreq0)));
//            mBinding.llWifi.addView(getTextView(String.valueOf(data.centerFreq1)));
//            mBinding.llWifi.addView(getTextView(String.valueOf(data.channelWidth)));
            mBinding.llWifi.addView(getTextView(String.valueOf(data.frequency)));
            mBinding.llWifi.addView(getTextView(String.valueOf(data.level)));
//            mBinding.llWifi.addView(getTextView(String.valueOf(data.timestamp)));
//            mBinding.llWifi.addView(getTextView(String.valueOf(data.operatorFriendlyName)));
//            mBinding.llWifi.addView(getTextView(String.valueOf(data.venueName)));
//            mBinding.llWifi.addView(getTextView(String.valueOf(data.describeContents())));
//            mBinding.llWifi.addView(getTextView(String.valueOf(data.is80211mcResponder())));
//            mBinding.llWifi.addView(getTextView(String.valueOf(data.isPasspointNetwork())));
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
