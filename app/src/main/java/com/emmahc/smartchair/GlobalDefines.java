package com.emmahc.smartchair;

/**
 * GlobalDefines
 */

public class GlobalDefines {
    public static final class AppInfo {
        public final static String SERVER_VERSION = "Ver1_0_0";
    }

    public static final class Setting {
        public final static Double SAMPLING_FREQ = 250.0;
        public final static int RX_BUFFER_SZIE = 10;
        public final static int MEASURE_TIME = 70; // second
        public final static int DISPLAY_DATA_SIZE = 500;
        public final static int ENG_MODE_CHART_NUMBER_OF_X = SAMPLING_FREQ.intValue() ;
        public final static int ECG_LOW_CUTOFF = 150; // max hr 100
        public final static int ECG_HIGH_CUTOFF = 300; // min hr 50
        public final static int PPG_LOW_CUTOFF = 150; // max hr 100
        public final static int PPG_HIGH_CUTOFF = 300; // min hr 50
        public final static int CORRECT_COUNT_LIMIT = 3;
//        final static int THRESHOLD_PPG_LOW = 60;
//        final static int THRESHOLD_PPG_HIGH = 150;
        public final static int THRESHOLD_WRONG_CONNECTED = 160;
        public final static int ECG_NOT_CONNECTED_COUNT = 5;
        public final static int PPG_NOT_CONNECTED_COUNT = 5;
        public final static int CHART_UPDATE_PERIOD = SAMPLING_FREQ.intValue(); //250
        public final static double LPF_COEFF_PPG = 0.300;
        public final static double LPF_COEFF_ECG = 0.100;
        public final static double HPF_COEFF_ECG = 0.100;
    }

    public static final class Defines {
        public final static int GRAPH_ECG = 1;
        public final static int GRAPH_PPG = 2;
    }

    public static final class BluetoothConnection {
        public final static int REQUEST_CONNECT_DEVIC0E_INSECURE = 2;
        public final static int REQUEST_ENABLE_BT = 3;
    }

    public static final class DeviceInformation {
        public final static String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
        public final static String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    }

    public static final class TabSelect {
        public final static String TAB_SELECT = "TAB_SELECT";
        public final static String FIRST_TAB = "FIRST_TAB";
        public final static String SECOND_TAB = "SECOND_TAB";
    }

    public static final class State {
        public final static int FAN_OFF = 0;
        public final static int FAN_ON = 1;
        public final static int HEATER_OFF = 0;
        public final static int HEATER_1 = 1;
        public final static int HEATER_2 = 2;
        public final static int HEATER_3 = 3;
        public final static int MOV_BACK_1 = 1;
        public final static int MOV_BACK_2 = 2;
        public final static int MOV_BACK_3 = 3;
        public final static int MOV_FOOT_1 = 1;
        public final static int MOV_FOOT_2 = 2;
        public final static int MOV_FOOT_3 = 3;
        public final static int MOVING_OFF = 0;
        public final static int MOVING_1 = 1;
        public final static int MOVING_2 = 2;
        public final static int MOVING_3 = 3;

        public static int HEATER_REQ;
        public static int MOV_BACK_REQ;
        public static int MOV_FOOT_REQ;
        public static int MOVING_REQ ;

        public static int getState_heater(){
            return HEATER_REQ;
        }
        public static void setState_heater(int i){
            HEATER_REQ = i;
        }
    }
}
