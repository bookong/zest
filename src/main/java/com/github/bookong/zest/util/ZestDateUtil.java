/**
 * Copyright 2014-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.bookong.zest.util;

import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.testcase.ZestData;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Date processing related functions.
 * 
 * @author Jiang Xu
 */
public class ZestDateUtil {

    private static final ThreadLocal<SimpleDateFormat> TYPE_FULL = new ThreadLocal<>();

    /**
     * Use "yyyy-MM-dd'T'HH:mm:ss.SSSZ" to parse the string representing the time.
     *
     * @param time
     *          String representing time.
     * @return time after parse.
     */
    public static Date parseDate(String time) {
        try {
            return getDateFormatFull().parse(time);
        } catch (Exception e) {
            throw new ZestException(Messages.parseDataDate(time), e);
        }
    }

    /**
     * Format the date as "yyyy-MM-dd'T'HH:mm:ss.SSSZ" .
     * @param time
     *          Time to format.
     * @return formatted string.
     */
    public static String formatDateNormal(Date time) {
        return getDateFormatFull().format(time);
    }

    private static SimpleDateFormat getDateFormatFull() {
        SimpleDateFormat obj = TYPE_FULL.get();
        if (obj == null) {
            obj = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            TYPE_FULL.set(obj);
        }
        return obj;
    }

    /**
     * Calculate the date value to be entered into the database (corrected by {@code currDbTimeDiff}).
     *
     * @param zestData
     *          An object containing unit test case data.
     * @param date
     *          Date object to be converted
     * @return date object after conversion.
     */
    public static Date getDateInZest(ZestData zestData, Date date) {
        if (date == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MILLISECOND, 0);

        if (zestData.isTransferTime()) {
            cal.setTimeInMillis(cal.getTimeInMillis() + zestData.getCurrentTimeDiff());
        }

        return cal.getTime();
    }
}
