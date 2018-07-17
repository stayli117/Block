package net.people.vrmr.mainactivity.module;

import net.people.vrmr.DataBase.InfoDao;
import net.people.vrmr.DataBase.InfoDao_Impl;
import net.people.vrmr.DataBase.InfoDataBase;
import net.people.MyApplication;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * @author qingchen
 * @date 17-11-12
 */
@Module
public class InfoModule {
    @Provides
    @Named("InfoDao")
    InfoDao provideInfoDao(){
        return new InfoDao_Impl(InfoDataBase.getInstance(MyApplication.getContext()));
    }
}
