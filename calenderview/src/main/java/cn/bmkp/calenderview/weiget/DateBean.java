package cn.bmkp.calenderview.weiget;

/**
 * Created by wangpan on 2018/3/31.
 */

public class DateBean {

    private int year;
    private int month;
    private int day;
    private String lunar;

    public DateBean() {

    }

    public DateBean(int year, int month) {
        this.year = year;
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getLunar() {
        return lunar;
    }

    public void setLunar(String lunar) {
        this.lunar = lunar;
    }
}
