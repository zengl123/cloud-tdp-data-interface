package com.drore.cloud.tdp.utils;

import com.drore.cloud.tdp.exception.BusinessException;

import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

/**
 * 描述:日期时间工具类
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/9/29  18:28.
 */
public class DateTimeUtil {
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYY_MM = "yyyy-MM";


    /**
     * 格式化
     *
     * @param pattern
     * @return
     */
    public static DateTimeFormatter getDateFormat(String pattern) {
        return DateTimeFormatter.ofPattern(pattern);
    }

    //TODO//////////////////////基本转换///////////////////////////////基本转换/////////////////////////////////////////

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getNowTime() {
        DateTimeFormatter f = getDateFormat(YYYY_MM_DD_HH_MM_SS);
        String now = LocalDateTime.now().withNano(0).format(f);
        return now;
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

    //TODO///////////////////////////获取数据////////////获取数据/////////////////////////////////////////////////////////////

    /**
     * 获取周几
     *
     * @param day 0代表当天 负数代表前几天 正数代表后几天
     * @return
     */
    public static String getWeek(int day) {
        LocalDate localDate = LocalDate.now();
        LocalDate date = day >= 0 ? localDate.plusDays(day) : localDate.minusDays(Math.abs(day));
        int week = date.getDayOfWeek().getValue();
        String[] data = {"一", "二", "三", "四", "五", "六", "日"};
        return "周" + data[week - 1];
    }

    /**
     * 获取n秒前/后时间
     *
     * @param time
     * @param second
     * @return 返回格式 yyyy-MM-dd HH:mm:ss
     */
    public static String timeAddMinusSeconds(String time, int second) {
        DateTimeFormatter f = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS);
        LocalDateTime localDateTime = second > 0 ? LocalDateTime.parse(time, f).plusSeconds(second) : LocalDateTime.parse(time, f).minusSeconds(Math.abs(second));
        return localDateTime.format(f);
    }


    /**
     * 获取n分钟前/后时间字符串
     * 返回格式：HH_MM_SS/HH_MM
     *
     * @param time   格式:hh:mm/hh:mm:ss
     * @param minute 分钟
     * @return
     */
    public static String timeAddMinusMinutes(String time, int minute) {
        LocalTime localTime = LocalTime.parse(time.trim());
        LocalTime nowLocalTime = minute >= 0 ? localTime.plusMinutes(minute) : localTime.minusMinutes(Math.abs(minute));
        return nowLocalTime.toString();
    }

    /**
     * 获取n分钟前/后时间字符串
     *
     * @param time
     * @param minute  分钟数
     * @param pattern 格式
     * @return
     */
    public static String timeAddMinusMinutes(String time, int minute, String pattern) {
        DateTimeFormatter f = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime localDateTime = minute > 0 ? LocalDateTime.parse(time, f).plusMinutes(minute) : LocalDateTime.parse(time, f).minusMinutes(Math.abs(minute));
        return localDateTime.format(f);
    }


    /**
     * 获取n小时前/后的时间字符串
     * 返回格式:hh:mm
     *
     * @param time 格式 hh:mm
     * @param hour
     * @return
     */
    public static String timeAddMinusHours(String time, int hour) {
        LocalTime localTime = LocalTime.parse(time.trim());
        LocalTime newTime = hour >= 0 ? localTime.plusHours(hour) : localTime.minusHours(Math.abs(hour));
        return newTime.toString();
    }

    /**
     * 获取n天前/后日期
     * 返回格式：YYYY_MM_DD
     *
     * @param date 格式为yyyy-MM-dd
     * @param day
     * @return
     */
    public static String dateAddMinusDays(String date, int day) {
        LocalDate localDate = LocalDate.parse(date.trim(), getDateFormat(YYYY_MM_DD));
        LocalDate newDate = day >= 0 ? localDate.plusDays(day) : localDate.minusDays(Math.abs(day));
        return newDate.toString();
    }

    /**
     * 获取n天前/后日期
     *
     * @param date    日期
     * @param day     天数
     * @param pattern 格式
     * @return
     */
    public static String dateAddMinusDays(String date, int day, String pattern) {
        DateTimeFormatter f = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime localDateTime = day > 0 ? LocalDateTime.parse(date, f).plusDays(day) : LocalDateTime.parse(date, f).minusDays(Math.abs(day));
        return localDateTime.format(f);
    }

    /**
     * 获取当月的第一天
     *
     * @return
     */
    public static String firstDayOfMonth() {
        LocalDate localDate = LocalDate.now();
        LocalDate date = localDate.with(TemporalAdjusters.firstDayOfMonth());
        return date.format(getDateFormat(YYYY_MM_DD));
    }

    /**
     * 获取本周第一天
     *
     * @return
     */
    public static String firstDayOfWeek() {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setTime(new Date());
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek()); // Monday
        Date time = c.getTime();
        String str = dateToStr(time, YYYY_MM_DD_HH_MM_SS);
        return str;
    }

    /**
     * 获取上个月第一天
     *
     * @return
     */
    public static String firstDayOfLastMonth(String pattern) {
        LocalDateTime localDate = LocalDateTime.now();
        LocalDateTime date = localDate.minusMonths(1).with(TemporalAdjusters.firstDayOfMonth());
        return date.format(getDateFormat(pattern));
    }

    /**
     * 获取下个月第一天
     *
     * @return
     */
    public static String firstDayOfNextMonth() {
        LocalDate localDate = LocalDate.now();
        LocalDate date = localDate.plusMonths(1).with(TemporalAdjusters.firstDayOfMonth());
        return date.format(getDateFormat(YYYY_MM_DD));
    }

    /**
     * 获取指定月份第一天
     *
     * @param month 1-12
     * @return
     */
    public static String getMonthFirstDay(int month) {
        LocalDate localDate = LocalDate.now().withMonth(month).with(TemporalAdjusters.firstDayOfMonth());
        return localDate.format(getDateFormat(YYYY_MM_DD));
    }

    /**
     * 获取当年的第一天
     *
     * @return
     */
    public static String firstDayOfYear() {
        LocalDate localDate = LocalDate.now();
        LocalDate date = localDate.with(TemporalAdjusters.firstDayOfYear());
        return date.format(getDateFormat(YYYY_MM_DD));
    }

    /**
     * 获取某年第一天日期
     *
     * @param year 年份
     * @return Date
     */
    public static String getYearFirstDay(int year) {
        LocalDate localDate = LocalDate.now();
        LocalDate date = localDate.withYear(year).with(TemporalAdjusters.firstDayOfYear());
        return date.format(getDateFormat(YYYY_MM_DD));
    }

    /**
     * 遍历获取月份集合
     *
     * @param startMoth YYYY-MM
     * @param endMonth  YYYY-MM
     * @return
     * @throws ParseException
     */
    public static List<String> listYearMonth(String startMoth, String endMonth) {
        LocalDate start = LocalDate.parse(startMoth + "-01");
        LocalDate end = LocalDate.parse(endMonth + "-01");
        List<String> list = new ArrayList<>();
        if (startMoth.equals(endMonth)) {
            list.add(startMoth);
            return list;
        }
        long m = ChronoUnit.MONTHS.between(start, end);
        for (long i = 0; i <= m; i++) {
            list.add(start.plusMonths(i).format(getDateFormat(YYYY_MM)));
        }
        return list;
    }

    /**
     * 遍历获取两个日期之间天数集合
     *
     * @param startDate 格式yyyy-MM-dd
     * @param endDate   格式yyyy-MM-dd
     * @return
     * @throws Exception
     */
    public static List<String> listDate(String startDate, String endDate) {
        List<String> dateList = new ArrayList<>();
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        long d = ChronoUnit.DAYS.between(start, end);
        dateList.add(start.toString());
        for (long i = 1; i <= d; i++) {
            dateList.add(start.plusDays(i).toString());
        }
        return dateList;
    }

    /**
     * 获取一天指定时间段的各个时间
     *
     * @return
     */
    public static List<String> hourOfDay(int begin, int end) {
        List<String> list = Arrays.asList("00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23");
        List<String> hours = new ArrayList<>();
        for (int i = begin; i <= end; i++) {
            hours.add(list.get(i));
        }
        return hours;
    }
//TODO///////////////////////////比较////////////相差////////////排序/////////////////////////////////////////////////////////////

    /**
     * 比较时间字符串大小
     *
     * @param str1
     * @param str2
     * @return
     */
    public static boolean compareDate(String str1, String str2) {
        long longStr1 = Long.valueOf(str1.replaceAll("[-\\s:]", ""));
        long longStr2 = Long.valueOf(str2.replaceAll("[-\\s:]", ""));
        return longStr1 >= longStr2;
    }

    /**
     * 获取当前时间前5分钟时间(时间间隔5分钟)
     *
     * @param pattern 时间格式
     * @return
     */
    public static String getNowMinuteBefore5(String pattern) {
        LocalDateTime localDateTime = LocalDateTime.now().withNano(0);
        int minute = localDateTime.getMinute();
        minute = Math.round(minute / 5 * 5);//计算10的整数分钟
        String time = localDateTime.withMinute(minute).withSecond(0).minusMinutes(5).format(DateTimeUtil.getDateFormat(pattern));
        return time;
    }

    public static void main(String[] args) {
        //System.out.println("dateToStr() = " + dateToStr(new Date(),"yyyy-MM-dd HH:mm:ss"));
        //System.out.println("strToDate() = " + strToDate("2017-09-29","yyyy-MM-dd HH"));
        //System.out.println("msToDateStr() = " + msToDateStr("323400123111 ","yyyy"));
        //System.out.println("stampToDateStr() = " + stampToDateStr("3234001231","yyyy"));
        //System.out.println("dateStrToStamp() = " + dateStrToStamp("2017-01-01 00:00:00"));
        //System.out.println("timeAddMinusMinutes = " + timeAddMinusMinutes("00:00:00",-5));
        //System.out.println("timeAddMinusHours() = " + timeAddMinusHours("00:00",5));
        //System.out.println("dateAddMinusDays() = " + dateAddMinusDays("2017-01-01", 2));
        //System.out.println("本月第一天：firstDayOfMonth() = " + firstDayOfMonth());
        //System.out.println("上个月第一天：firstDayOfLastMonth() = " + firstDayOfLastMonth());
        //System.out.println("下个月第一天：firstDayOfNextMonth() = " + firstDayOfNextMonth());
        //System.out.println("获取指定月份的第一天：getMonthFirstDay() = " + getMonthFirstDay(11));
        //System.out.println("获取当年的第一天:firstDayOfYear() = " + firstDayOfYear());
        //System.out.println("getYearFirstDay() = " + getYearFirstDay(2017));
        //System.out.println("listYearMonth() = " + listYearMonth("2017-09","2017-12"));
        //System.out.println("遍历获取两个日期之间天数集合:listDate() = " + listDate("2017-09-01", "2017-09-30"));
        //System.out.println("比较时间字符串大小:compareDate() = " + compareDate("2016-09-18", "2017-09-02"));
        //String s = msToDateStr("1507694246000", YYYY_MM_DD_HH_MM_SS);
        //System.out.println("s = " + s);


//        String s = timeAddMinusMinutes("2017/01/09 12:09:10", 1, "yyyy/MM/dd HH:mm:ss");
//        System.out.println("s = " + s);
//        String s1 = timeAddMinusSeconds("2017-01-09 12:09:10", 10);
//
//        System.out.println("s1 = " + s1);
//
//        String s2 = dateAddMinusDays("2017-01-09 12:09:10", 1, "yyyy-MM-dd HH:mm:ss");
//        System.out.println("s2 = " + s2);
//        System.out.println("getNowTime() = " + getNowTime());
        String firstDayOfLastMonth = firstDayOfLastMonth("yyyy/MM/dd 00:00:00");
        System.out.println("firstDayOfLastMonth = " + firstDayOfLastMonth);
    }
}
