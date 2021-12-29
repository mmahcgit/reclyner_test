package com.emmahc.smartchair.ui;

public enum DateType {
    DAY(1),
    WEEK(2),
    MONTH(3),
    YEAR(4);

    private int dt;
    DateType(int i) {
        dt = i;
    }

    public int getDt() {
        return dt;
    }

    public void setDt(int dt) {
        this.dt = dt;
    }

    public static DateType get(int i){
        if(i==0)
            return DAY;
        else if(i==1)
            return WEEK;
        else if(i==2)
            return MONTH;
        else
            return YEAR;
    }
}
