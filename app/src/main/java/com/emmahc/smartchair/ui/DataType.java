package com.emmahc.smartchair.ui;

public enum DataType {
    HR(1),
    STRESS(2),
    PRESSURE(3);

    private int dt;
    DataType(int i) {
        dt = i;
    }

    public int getDt() {
        return dt;
    }

    public void setDt(int dt) {
        this.dt = dt;
    }

    public static DataType get(int i){
        if(i==0)
            return HR;
        else if(i==1)
            return STRESS;
        else
            return PRESSURE;
    }
}
