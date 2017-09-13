package com.jaaaelu.gzw.neteasy.common;

/**
 * Created by admin on 2017/5/21.
 */

public class Common {

    /**
     * 一些不变的参数
     * 通常用于一些配置
     */
    public interface Constance {
        //  手机号的正则
        String REGEX_PHONE = "[1][3,4,5,7,8][0-9]{9}";
        //  基础网络请求地址
        String API_URL = "https://api.douban.com/v2/book/";

        String CONSUMER_KEY = "jaaaelu";
        String CONSUMER_SECRET = "512b5e7ff8209d74";

        String WE_CHAT_APP_ID = "wx481ec8201cc68e3d";
    }
}
