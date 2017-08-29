package com.jaaaelu.gzw.neteasy.privatebook;

import com.jaaaelu.gzw.neteasy.common.app.PrivateBookApplication;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Created by Gzw on 2017/8/12 0012.
 */

public class App extends PrivateBookApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        //  初始化数据库
        FlowManager.init(new FlowConfig.Builder(getApplicationContext())
                //  数据库初始化的时候就打开数据库
                .openDatabasesOnInit(true)
                .build());
    }
}
