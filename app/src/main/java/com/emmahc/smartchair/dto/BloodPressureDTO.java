package com.emmahc.smartchair.dto;

import com.emmahc.smartchair.ui.DateType;

/**
 * Created by dev.oni on 2018. 10. 26..
 * Copyright or OutSourcing Source
 * certificate dev.oni
 */

public class BloodPressureDTO {
    private int
             hrHigh     //최고심박
            ,hrMean     //평균심박
            ,hrLow      //최저심박
            ,stress     //스트레스
            ,SBP        //최고혈압
            ,DBP;       //최저혈압

    private DayData dayData;


    public DayData getDayData() {
        return dayData;
    }

    public void setDayData(DayData dayData) {
        this.dayData = dayData;
    }

    public int getHrHigh() {
        return hrHigh;
    }

    public void setHrHigh(int hrHigh) {
        this.hrHigh = hrHigh;
    }

    public int getHrLow() {
        return hrLow;
    }

    public void setHrLow(int hrLow) {
        this.hrLow = hrLow;
    }

    public int getStress() {
        return stress;
    }

    public void setStress(int stress) {
        this.stress = stress;
    }

    public int getSBP() {
        return SBP;
    }

    public void setSBP(int SBP) {
        this.SBP = SBP;
    }

    public int getDBP() {
        return DBP;
    }

    public void setDBP(int DBP) {
        this.DBP = DBP;
    }

    public int getHrMean() {
        return hrMean;
    }

    public void setHrMean(int hrMean) {
        this.hrMean = hrMean;
    }

    @Override
    public String toString() {
        return "BloodPressureDTO{" +
                "hrHigh=" + hrHigh +
                ", hrMean=" + hrMean +
                ", hrLow=" + hrLow +
                ", stress=" + stress +
                ", SBP=" + SBP +
                ", DBP=" + DBP +
                ", dayData=" + dayData +
                '}';
    }
}
