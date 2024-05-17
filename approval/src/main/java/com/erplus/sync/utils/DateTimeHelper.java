package com.erplus.sync.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

@Slf4j
public class DateTimeHelper {


    public static final String YEAR_PATTERN = "yyyy";

    public static final String YEAR_MONTH_PATTERN = "yyyy-MM";

    public static final String YEAR_MONTH_DAY_PATTERN = "yyyy-MM-dd";

    public static final String YEAR_MONTH_DAY_HOUR_PATTERN = "yyyy-MM-dd HH";

    public static final String YEAR_MONTH_DAY_HOUR_MINUTE_PATTERN = "yyyy-MM-dd HH:mm";

    public static final String MONTH_DAY_HOUR_MINUTE_SECONDE_PATTERN = "MM-dd HH:mm:ss";

    public static final String HOUR_MINUTE_SECONDE_PATTERN = "HH:mm:ss";

    public static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final long DAY_IN_MILLISECONDS = 86400000L;
    /**
     * 转换成系统日期时间
     */
    public static final int CHANGE_SYSTEM_DATETIME = 1;
    /**
     * 转换成时区日期时间
     */
    public static final int CHANGE_TIMEZONE_DATETIME = 2;

    public static final String DEFAULT_TIME_ZONE = "Asia/Shanghai";

    private static final String[] WEEK_DAYS = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
    /**
     * 字符串 转 日期时间
     *
     * @param value
     * @param pattern
     * @return
     */
    public static Date parse(String value, String pattern) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        if (StringUtils.isEmpty(pattern)) {
            pattern = DEFAULT_PATTERN;
        }
        SimpleDateFormat fmt = new SimpleDateFormat(pattern);
        try {
            return fmt.parse(value);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 字符串 转 日期时间
     *
     * @param value
     * @return
     */
    public static Date parse(String value) {
        return parse(value, DEFAULT_PATTERN);
    }

    /**
     * 日期时间 转 字符串
     *
     * @param date
     * @return
     */
    public static String format(Date date) {
        return format(date, "yyyy-MM-dd");
    }

    /**
     * 日期时间 转 字符串
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String format(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        if (StringUtils.isEmpty(pattern)) {
            pattern = "yyyy-MM-dd";
        }
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    public static void main(String[] args) {
        LocalDateTime date = LocalDateTime.now();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.format(date);
    }

    /**
     * 设置时间
     *
     * @param date
     *            日期 long
     * @param monthes
     *            roll的月份
     * @return 日期 long
     */
    public static final long rollMonth(long date, int monthes) {
        if (monthes == 0) {
            return date;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        if (monthes > 0) {
            int month = calendar.get(2);
            for (int i = 0; i < monthes; i++) {
                if (month == 11) {
                    calendar.roll(1, 1);
                    month = -1;
                }
                month++;
            }
            calendar.roll(2, monthes);
            return calendar.getTimeInMillis();
        }
        for (int i = monthes; i < 0; i++) {
            if (calendar.get(2) == 0) {
                calendar.roll(1, -1);
            }
            calendar.roll(2, 1);
        }
        return calendar.getTimeInMillis();
    }

    /**
     * 日期2 是否大于 日期1
     *
     * @param date1
     *            日期 1
     * @param date2
     *            日期 2
     * @return true表示是
     */
    public static final boolean beforeDate(Date date1, Date date2) {
        if ((date1 == null) || (date2 == null)) {
            return false;
        }
        Calendar calendar = Calendar.getInstance();
        long time1 = getDate(calendar, date1);
        long time2 = getDate(calendar, date2);
        return time1 < time2;
    }

    // 获取date的TimeInMillis
    private static final long getDate(Calendar calendar, Date date) {
        calendar.setTimeInMillis(date.getTime());
        calendar.set(10, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 格式化时间
     *
     * @param l
     * @return
     */
    public static String formatTime(Long l) {

        if (l == null)
            return null;

        int hour = 0;
        int minute = 0;
        int second = 0;
        second = l.intValue();
        if (second >= 60) {
            minute = second / 60;
            second = second % 60;
        }
        if (minute >= 60) {
            hour = minute / 60;
            minute = minute % 60;
        }

        return hour + "时" + minute + "分" + second + "秒";
    }

    /**
     * 解析日期字符串获取秒数
     *
     * @param time
     *            HH:mm:ss
     * @return
     */
    public static long parseDateStr(String time) {
        long l = 0;
        String[] t = time.split(":");
        int hour = Integer.parseInt(t[0]);
        int min = Integer.parseInt(t[1]);
        int sec = Integer.parseInt(t[2]);
        l = hour * 3600 + min * 60 + sec;
        return l;
    }

    /**
     * 默认时区时间转换
     * @param srcTimestamp
     * @param decTimeZone
     * @return
     */
    public static Timestamp conversionTimeZone(Timestamp srcTimestamp, String decTimeZone){
        if (Objects.equals(DEFAULT_TIME_ZONE, decTimeZone)) {
            return srcTimestamp;
        }
        return conversionTimeZone(srcTimestamp, DEFAULT_TIME_ZONE, decTimeZone);
    }

    /**
     * 时区时间转换
     * @return
     */
    public static Timestamp conversionTimeZone(Timestamp srcTimestamp, String srcTimeZone, String decTimeZone){
        if (srcTimestamp == null) {
            return null;
        }
        if (StringUtils.isEmpty(srcTimeZone)
                || StringUtils.isEmpty(decTimeZone)) {
            return srcTimestamp;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(srcTimeZone));
        long srcOffset = calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET);
        calendar.setTimeZone(TimeZone.getTimeZone(decTimeZone));
        long decOffset = calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET);

        return new Timestamp(srcTimestamp.getTime() - (srcOffset - decOffset));
    }

    /**
     * 当前时间时区转换
     * @param decTimeZone
     * @return
     */
    public static Calendar conversionTimeZoneofCurrentTime(String decTimeZone){
        Calendar calendar = Calendar.getInstance();
        Timestamp timestamp = conversionTimeZone(new Timestamp(calendar.getTimeInMillis()), decTimeZone);
        calendar.setTimeInMillis(timestamp.getTime());
        return calendar;
    }

    /**
     * 把原来日期调整到现在的日期，时分秒不变
     * @param timestamp
     * @param currentTime
     * @return
     */
    public static Timestamp changeToCurrentTime(Timestamp timestamp, long currentTime) {
        String date1 = format(timestamp, DEFAULT_PATTERN);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        String date2 = format(calendar.getTime());
        long time = parse(date2 + date1.substring(date1.indexOf(" ")), DEFAULT_PATTERN).getTime();

        return new Timestamp(time);
    }

    public static int getOffsetDays(Date startTime, Date dueTime) {
        long offset = dueTime.getTime() - startTime.getTime();
        if (offset % DAY_IN_MILLISECONDS == 0){
            return (int) (offset / DAY_IN_MILLISECONDS);
        }
        return (int) (offset / DAY_IN_MILLISECONDS + 1);
    }

    /**
     * 获取中文星期
     * @param week
     * @return
     */
    public static String getWeekOfDate(int week) {

        return WEEK_DAYS[week];
    }

    public static Long timeStamp2Long(Timestamp timestamp){
        if (Utils.isNull(timestamp)) {
            return null;
        }
        return timestamp.getTime();
    }

    public static Timestamp long2TimeStamp(Long time){
        if (Utils.isNull(time) || time == 0) {
            return null;
        }

        return new Timestamp(time);
    }

    public static String getCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        return DateTimeHelper.format(calendar.getTime(), DateTimeHelper.YEAR_MONTH_PATTERN);
    }

    public static String getLastMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.MONTH, -1);

        return DateTimeHelper.format(calendar.getTime(), DateTimeHelper.YEAR_MONTH_PATTERN);
    }

    public static String getNextMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.MONTH, 1);

        return DateTimeHelper.format(calendar.getTime(), DateTimeHelper.YEAR_MONTH_PATTERN);
    }
}
