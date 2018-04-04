package cn.bmkp.calenderview.weiget;

import java.util.Calendar;

/**
 * Created by wangpan on 2018/3/28.
 */

public class CalendarUtils {

    public static String getWeekName(int i) {
        if (i < 0 || i > 6) {
            throw new IllegalArgumentException("toWeekName error: " + i);
        }
        String weekName = "";
        switch (i) {
            case 0:
                weekName = "周日";
                break;
            case 1:
                weekName = "周一";
                break;
            case 2:
                weekName = "周二";
                break;
            case 3:
                weekName = "周三";
                break;
            case 4:
                weekName = "周四";
                break;
            case 5:
                weekName = "周五";
                break;
            case 6:
                weekName = "周六";
                break;
        }
        return weekName;
    }

    /**
     * 根据起始日期计算总页卡数
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static int getPageCount(DateBean startDate, DateBean endDate) {
        if (startDate == null || endDate == null) {
            return -1;
        }
        return (endDate.getYear() - startDate.getYear()) * 12 + (endDate.getMonth() - startDate.getMonth()) + 1;
    }

    /**
     * 计算指定日期的页卡position
     *
     * @param date
     * @param startDate
     * @param endDate
     * @return
     */
    public static int getDatePosition(DateBean date, DateBean startDate) {
        if (date == null || startDate == null) {
            return -1;
        }
        return (date.getYear() - startDate.getYear()) * 12 + (date.getMonth() - startDate.getMonth()) + 1;
    }
}
