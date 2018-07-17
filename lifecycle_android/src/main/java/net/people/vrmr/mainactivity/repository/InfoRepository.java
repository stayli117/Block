package net.people.vrmr.mainactivity.repository;

import android.arch.lifecycle.LiveData;

import net.people.vrmr.DataBase.InfoDao;
import net.people.vrmr.DataBase.NewsBean;
import net.people.vrmr.bean.InfoBean;
import net.people.vrmr.net.NetWork;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observer;
import rx.schedulers.Schedulers;

/**
 * @author qingchen
 * @date 17-11-10
 */

public class InfoRepository {


    public InfoDao infoDao;

    @Inject
    public InfoRepository(@Named("InfoDao") InfoDao infoDao){
        this.infoDao=infoDao;
    }

    public LiveData<List<NewsBean>> getInfo() {
        updateInfo();
        return infoDao.getAll();
    }

    public void updateInfo() {
        NetWork.getInfo().getInfo().subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(new Observer<InfoBean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                  e.printStackTrace();
            }

            @Override
            public void onNext(InfoBean infoBean) {
                //不能在主线程使用
                Gson gson = new Gson();
                String temp = gson.toJson(infoBean.getNewslist());
                List<NewsBean> list = gson.fromJson(temp, new TypeToken<List<NewsBean>>() {
                }.getType());
                infoDao.insertAll(list);
            }
        });
    }
}
