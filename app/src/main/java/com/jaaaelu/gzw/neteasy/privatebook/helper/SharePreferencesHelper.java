package com.jaaaelu.gzw.neteasy.privatebook.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.jaaaelu.gzw.neteasy.privatebook.App;

/**
 * Created by zw on 16/7/8.
 * SharePreferences帮助类
 */
public class SharePreferencesHelper {
    private static final String TAG = "SharePreferencesHelper";
    private static final String TAG_APP_COMMON = "tag_app_common";
    private static final String IS_NEXT_DATE = "is_next_date";
    private static final Context CONTEXT = App.getInstance().getApplicationContext();

    /**
     * 用Sp存String类型数据
     *
     * @param key     键 如:USER_ACCOUNT
     * @param value   值 如:获取到的用户账号
     * @param tagName 存到那个Sp中 如:用户账号密码存到用户信息中
     */
    public static void putString(String key, String value, String tagName) {
        SharedPreferences sp = getSharePreferences(tagName);
        if (sp != null) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(key, value);
            editor.apply();
            editor.commit();
        }
    }

    private static SharedPreferences getSharePreferences(String tagName) {
        return CONTEXT.getSharedPreferences(tagName, Context.MODE_PRIVATE);
    }

    /**
     * 获取Sting
     *
     * @param key     存的时候的键
     * @param tagName 是哪个部分
     * @return 返回获取的到值, 默认值为""
     */
    public static String getString(String key, String tagName) {
        SharedPreferences sp = getSharePreferences(tagName);
        return sp.getString(key, "");
    }

    /**
     * 清除对应部分的存过的数据
     *
     * @param tagName 对应部分 如:USER_INFO
     */
    public static void cleanSomeSp(String tagName) {
        SharedPreferences sp = getSharePreferences(tagName);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }
}
