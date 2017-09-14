package com.jaaaelu.gzw.neteasy.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Gzw on 2017/9/14 0014.
 */

public class Dateutil {

    public static String getDate() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }
}
