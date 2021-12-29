package com.emmahc.smartchair.dto;


//생체신호 로우 데이터 및 몸무게 업로드 API에 사용할 DTO
//requset  요소 - weight, rawData
//request 형태
//“weight”: 60.5,
//“recordedTime”: “Channel 1;Channel 2;
//36;56
//36;56
//36;56“
//Channel 1 ->BCG_seat
//Channel 2 ->BCG_back
//이에 사용할 홀,짝 데이터는 1분 10초 정도
public class Raw_weightDto {
    private Double weight;
    private String rawData;
    public Double getWeight() {
        return weight;
    }

    public String getRawData() {
        return rawData;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }

}
