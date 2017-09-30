package com.drore.cloud.tdp.utils;

import com.drore.cloud.tdp.exception.BusinessException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 描述:日期时间工具类
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/9/29  18:28.
 */
public class DateTimeUtil {
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    /**
     * 格式化
     *
     * @param pattern
     * @return
     */
    private static DateTimeFormatter getDateFormat(String pattern) {
        return DateTimeFormatter.ofPattern(pattern);
    }

    /**
     * data转string
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String dateToStr(Date date, String pattern) {
        Instant instant = date.toInstant();
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return dateTime.format(getDateFormat(pattern));
    }

    /**
     * string转date
     *
     * @param dateStr
     * @param pattern
     * @return
     */
    public static Date strToDate(String dateStr, String pattern) {
        LocalDateTime localDateTime;
        Instant instant;
        if (dateStr.trim().length() < 10) {
            throw new BusinessException("时间格式错误:" + dateStr);
        }
        if (dateStr.trim().length() == 10) {
            dateStr += " 00:00:00";
            localDateTime = LocalDateTime.parse(dateStr, getDateFormat(YYYY_MM_DD_HH_MM_SS));
            instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        } else {
            localDateTime = LocalDateTime.parse(dateStr.trim(), getDateFormat(pattern));
            instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        }
        return Date.from(instant);
    }

    /**
     * 将毫秒转换为时间字符串
     *
     * @param ms
     * @param pattern
     * @return
     */
    public static String msToDateStr(String ms, String pattern) {
        Instant instant = Instant.ofEpochMilli(Long.valueOf(ms.trim()));
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return dateTime.format(getDateFormat(pattern));
    }

    /**
     * 将时间戳转换为时间字符串
     *
     * @param s
     * @param pattern
     * @return
     */
    public static String stampToDateStr(String s, String pattern) {
        Instant instant = Instant.ofEpochSecond(Long.valueOf(s.trim()));
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return dateTime.format(getDateFormat(pattern));
    }
    /**
     * 将时间字符串转为时间戳
     *
     * @param dateStr
     * @return
     */
    public static String dateStrToStamp(String dateStr) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateStr.trim(), getDateFormat(YYYY_MM_DD_HH_MM_SS));
        long epochSecond = localDateTime.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
        return String.valueOf(epochSecond);
    }

    public static void main(String[] args) {
        System.out.println("dateToStr() = " + dateToStr(new Date(),"yyyy-MM-dd HH:mm:ss"));
        System.out.println("strToDate() = " + strToDate("2017-09-29","yyyy-MM-dd HH"));
        System.out.println("msToDateStr() = " + msToDateStr("323400123111 ","yyyy"));
        System.out.println("stampToDateStr() = " + stampToDateStr("3234001231","yyyy"));
        System.out.println("dateStrToStamp() = " + dateStrToStamp("2017-01-01 00:00:00"));
    }


}
