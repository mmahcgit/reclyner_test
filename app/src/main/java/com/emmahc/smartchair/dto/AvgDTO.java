package com.emmahc.smartchair.dto;

import com.emmahc.smartchair.ui.DateType;

/**
 * Created by dev.oni on 2018. 10. 26..
 * Copyright or OutSourcing Source
 * certificate dev.oni
 */

public class AvgDTO {
    private DateType dateType;
    private int enumDay,enumWeek,enumMonth,enumYear,
            appendCount;


    private long     hr, //심박 평균
                  lowHr, //심박 저
                 highHr, //심박 고
                 stress, //스트레스 평균
               pressure, //혈압 평균 SBP+DBP/2
                    SBP, //SBP
                    DBP; //DBP

    public long getSBP() {
        return SBP;
    }

    public void addSBP(int SBP) {
        this.SBP += SBP;
    }

    public long getDBP() {
        return DBP;
    }

    public void addDBP(int DBP) {
        this.DBP += DBP;
    }

    public long getLowHr() {
        return lowHr;
    }

    public void addLowHr(int lowHr) {
        this.lowHr += lowHr;
    }

    public long getHighHr() {
        return highHr;
    }

    public void addHighHr(int highHr) {
        this.highHr += highHr;
    }

    public int getEnumDay() {
        return enumDay;
    }

    public void setEnumDay(int enumDay) {
        this.enumDay = enumDay;
    }

    public int getEnumWeek() {
        return enumWeek;
    }

    public void setEnumWeek(int enumWeek) {
        this.enumWeek = enumWeek;
    }

    public int getEnumMonth() {
        return enumMonth;
    }

    public void setEnumMonth(int enumMonth) {
        this.enumMonth = enumMonth;
    }

    public int getEnumYear() {
        return enumYear;
    }

    public void setEnumYear(int enumYear) {
        this.enumYear = enumYear;
    }

    public DateType getDateType() {
        return dateType;
    }

    public void setDateType(DateType dateType) {
        this.dateType = dateType;
    }

    public long getHr() {
        return hr;
    }

    public void addHr(int hr) {
        this.hr += hr;
    }

    public long getStress() {
        return stress;
    }

    public void addStress(int stress) {
        this.stress += stress;
    }

    public long getPressure() {
        return pressure;
    }

    public void addPressure(int pressure) {
        this.pressure += pressure;
    }

    public void addCount(){
        appendCount++;
    }

    @Override
    public String toString() {
        return "AvgDTO{" +
                "dateType=" + dateType +
                ", enumDay=" + enumDay +
                ", enumWeek=" + enumWeek +
                ", enumMonth=" + enumMonth +
                ", enumYear=" + enumYear +
                ", appendCount=" + appendCount +
                ", hr=" + hr +
                ", lowHr=" + lowHr +
                ", highHr=" + highHr +
                ", stress=" + stress +
                ", pressure=" + pressure +
                ", SBP=" + SBP +
                ", DBP=" + DBP +
                '}';
    }

    public void calc(){
        //데이터가 1개이상이면 appendCount는 0이 아님.
        if(appendCount>0){
            hr = hr/appendCount;
            lowHr = lowHr/appendCount;
            highHr = highHr/appendCount;
            stress = stress/appendCount;
            SBP = SBP/appendCount;
            DBP = DBP/appendCount;
            pressure = pressure/(appendCount*2);
        }
    }
}
