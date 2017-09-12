package com.jaaaelu.gzw.neteasy.privatebook;

import com.evernote.client.android.EvernoteSession;
import com.jaaaelu.gzw.neteasy.common.app.PrivateBookApplication;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import java.util.Locale;

import static com.jaaaelu.gzw.neteasy.common.Common.Constance.CONSUMER_KEY;
import static com.jaaaelu.gzw.neteasy.common.Common.Constance.CONSUMER_SECRET;

/**
 * Created by Gzw on 2017/8/12 0012.
 */

public class App extends PrivateBookApplication {
    private static final boolean SUPPORT_APP_LINKED_NOTEBOOKS = true;
    private static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.PRODUCTION;

    @Override
    public void onCreate() {
        super.onCreate();
        //  初始化数据库
        FlowManager.init(new FlowConfig.Builder(getApplicationContext())
                //  数据库初始化的时候就打开数据库
                .openDatabasesOnInit(true)
                .build());


        new EvernoteSession.Builder(this)
                .setEvernoteService(EVERNOTE_SERVICE)
                .setSupportAppLinkedNotebooks(SUPPORT_APP_LINKED_NOTEBOOKS)
                .setForceAuthenticationInThirdPartyApp(true)
                .setLocale(Locale.SIMPLIFIED_CHINESE)
                .build(CONSUMER_KEY, CONSUMER_SECRET)
                .asSingleton();
    }
}
