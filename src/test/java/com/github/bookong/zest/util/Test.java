package com.github.bookong.zest.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Jiang Xu
 */
public class Test {
    public static void main(String[] args) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        System.out.println(sdf.format(new Date()));
        Date d = sdf.parse("2020-08-09T10:12:00.000+08:00");
        System.out.println(ZestDateUtil.formatDateNormal(d));
    }
}
