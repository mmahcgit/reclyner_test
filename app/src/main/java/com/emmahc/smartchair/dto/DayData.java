package com.emmahc.smartchair.dto;

import com.emmahc.smartchair.ui.DateType;

/**
 * Created by dev.oni on 2018. 10. 26..
 * Copyright or OutSourcing Source
 * certificate dev.oni
 */

public class DayData{
    private DateType dateType;

    private String year,month,day,week;



    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        int d = Integer.parseInt(day);
        if(d>21)         // 22 ~ 31  4째주
            week = "4";
        else if(d>15)   // 15 ~ 21  3째주
            week = "3";
        else if(d>8)    // 8 ~ 14   2째주
            week = "2";
        else              // 1 ~ 7    1째주
            week = "1";

        d = 0;
        this.day = (String)day;
    }

    public String getWeek() {
        return week;
    }


    public void setDateType(DateType dateType) {
        this.dateType = dateType;
    }

    public DateType getDateType() {
        return dateType;
    }

    @Override
    public String toString() {
        return "DayData{" +
                "dateType=" + dateType +
                ", year='" + year + '\'' +
                ", month='" + month + '\'' +
                ", day='" + day + '\'' +
                ", week='" + week + '\'' +
                '}';
    }
}
