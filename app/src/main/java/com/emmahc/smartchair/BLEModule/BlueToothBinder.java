package com.emmahc.smartchair.BLEModule;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.ProgressBar;

import com.emmahc.smartchair.BluetoothLeService;
import com.emmahc.smartchair.GlobalDefines;
import com.emmahc.smartchair.GlobalVariables;
import com.emmahc.smartchair.ServCom;
import com.emmahc.smartchair.ServerAPI.Calculating_HR;
import com.emmahc.smartchair.ServerAPI.Send_to_Server_RawData_Weight_kotlin;

import com.emmahc.smartchair.dto.SharedData;


import java.util.ArrayList;


import hrv.RRData;


import static android.content.Context.BIND_AUTO_CREATE;


public class BlueToothBinder implements ServCom.onResponseListener{
    private final String TAG = getClass().getSimpleName();
    //정상 연결시, 한번 호출해서 true로 변경시킴.
    //이때 온도랑 습도 전송해서 받아옴.
    private boolean bindStatus;

    private String mDeviceAddress;

    private Context mContext;
    private BluetoothLeService mBluetoothLeService;
    private BLEReceiver receiver;
    private TemperatureHumidityProcess THprocess;
    private ProgressDialog asyncDialog;

    private ProgressDialog calculatingDialog;

    private BluetoothGattCharacteristic readCharas;
    private BluetoothGattCharacteristic writeCharas;

    private Action acttion;
    private BLEActionListener listener;
    private float[] ppg_data_final;
    private float[] ecg_data_final;
    private String hr_mean_final = null;
    private String stress_score_final = null;

    //BLEReceiver의 ecg_ppg_total_Data를 받아올 변수와 함수
    private StringBuffer ecg_ppg_tData = new StringBuffer();
    public StringBuffer getEcg_ppg_tData(){
        return ecg_ppg_tData;
    }

    // HRV Analysis
    private ArrayList<Integer> RRIntervalPpg = new ArrayList<Integer>();
    private ArrayList<Integer> RRIntervalEcg = new ArrayList<Integer>();
    private RRData rrData;

    //혈압
    private ArrayList<Integer> heartRate = new ArrayList<>();
    public ArrayList<Integer> getHeartRate(){
        return heartRate;
    }


    private SharedData data;
    public BlueToothBinder(Context ctx,String bleStationMACAddr) {
        this.mContext = ctx;
        this.mDeviceAddress = bleStationMACAddr;
        this.acttion = new Action();
        this.THprocess = new TemperatureHumidityProcess(this);
        this.data = new SharedData(ctx);
        asyncDialog = new ProgressDialog(ctx);
        asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        asyncDialog.setTitle("잠시만 기다려 주세요...");

        calculatingDialog = new ProgressDialog(ctx);
        calculatingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        calculatingDialog.setMessage("계산중입니다. 잠시만 기다려주세요...");
        Intent gattServiceIntent = new Intent(ctx, BluetoothLeService.class);
        ctx.bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }


    public BluetoothGattCharacteristic getReadCharas() {
        return readCharas;
    }

    public void setReadCharas(BluetoothGattCharacteristic charas) {
        readCharas = charas;
    }

    public BluetoothGattCharacteristic getWriteCharas() {
        return writeCharas;
    }

    public void setWriteCharas(BluetoothGattCharacteristic charas) {
        this.writeCharas = charas;
    }


    Context getmContext() {
        return mContext;
    }

    BluetoothLeService getmBluetoothLeService() {
        return mBluetoothLeService;
    }


    public Action getActtion() {
        return acttion;
    }

    public void setListener(BLEActionListener listener) {
        this.listener = listener;
    }

    public BLEActionListener getListener() {
        return listener;
    }

    public float[] getPpg_data_final() {
        return ppg_data_final;
    }
    public float[] getEcg_data_final() {
        return ecg_data_final;
    }

    //온도 습도 관련 프로세스
    public TemperatureHumidityProcess getTHprocess() {
        return THprocess;
    }

    public ArrayList<Integer> getRRIntervalPpg() {
        return RRIntervalPpg;
    }

    public ArrayList<Integer> getRRIntervalEcg() {
        return RRIntervalEcg;
    }

    public boolean isBindStatus() {
        return bindStatus;
    }

    public void setBindStatus(boolean bindStatus) {
        this.bindStatus = bindStatus;
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            mBluetoothLeService.initialize();
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.GET_HEART_DATA);
        intentFilter.addAction(BluetoothLeService.MOVING_RECLINER);
        return intentFilter;
    }
    public void unbindService(){
        try{
            mContext.unbindService(mServiceConnection);
            mBluetoothLeService = null;
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void onResume(){
        mContext.registerReceiver(receiver = new BLEReceiver(this), makeGattUpdateIntentFilter());
    }
    public void onPause(){
        try{
            //java.lang.IllegalArgumentException: Receiver not registered: com.emmahc.smartchair.BLEModule.BLEReceiver@734f437
            //Mainactivity에서 액티비티 분리(onPause)되면 이 함수가 실행됨
            mContext.unregisterReceiver(receiver);

        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }
    public void measureHandlerOff(){
        receiver.getmHandlerForMeasuringEnd();
    }

    public class Action{
        int ppg_count = 0;
        int ecg_count = 0;
        int state_fan = GlobalDefines.State.FAN_OFF;
        int state_back = GlobalDefines.State.MOV_BACK_REQ;
        int state_foot = GlobalDefines.State.MOV_FOOT_REQ;
        int state_mov = GlobalDefines.State.MOVING_REQ;



        boolean key_eng_mode = false;
        boolean key_is_measuring = false;
        //측정 중단 함수
        public synchronized void stopMeasuring(){
            final byte[] send_Data = new byte[5];
            send_Data[0] = (byte)0x5A;
            send_Data[1] = (byte)0x01;
            send_Data[2] = (byte)0x06;
            send_Data[3] = (byte)0xA2;
            send_Data[4] = (byte)0xA5;
            ((GlobalVariables) mContext.getApplicationContext()).setEcg_ppg_tData(ecg_ppg_tData);
            ecg_ppg_tData = new StringBuffer();
            key_eng_mode = false;
            if(mBluetoothLeService==null) return;
            BluetoothGattCharacteristic characteristic = null;
            if(writeCharas!=null){
                characteristic = writeCharas;
            }
            try{
                if(characteristic != null){
                    mBluetoothLeService.writeCharacteristic(characteristic, send_Data);
                    Log.d("writeCharacteristic","moving_Recliner");
                    Log.d("measuring_status","stop");
                }
            }catch (Exception e){
                Log.d("measuring_status","fail stopping");
            }
        }
        //힙이나 움직이는거 합친 함수
        public synchronized void moving_Recliner(byte[] send_data){
            Log.d("clicked","click");
            if(mBluetoothLeService==null) return;
            BluetoothGattCharacteristic characteristic = null;
            if(writeCharas!=null){
                characteristic = writeCharas;
            }
            if(characteristic != null){
                mBluetoothLeService.writeCharacteristic(characteristic, send_data);
                Log.d("writeCharacteristic","moving_Recliner");
            }
        }
        //움직이는 거 멈추는 함수
        public synchronized void stopMotion(byte[] send_data){
            if(mBluetoothLeService==null) return;
            BluetoothGattCharacteristic characteristic = null;
            if(writeCharas!=null){
                characteristic = writeCharas;
            }
            if (characteristic != null) {
                mBluetoothLeService.writeCharacteristic(characteristic, send_data);
                Log.d("writeCharacteristic","setMoveBack");
                for (final byte b : send_data) {
                    //Log.e("SEND_MEASURE", String.format("%02X ", b & 0xff));
                }
            }
        }
        //측정 시작
        public synchronized void measureStart(){
            //서비스 null이면 아무 값도 없이 리턴
            if(mBluetoothLeService==null) return;
            //ppg데이터 초기화
            ppgDataReset();

            receiver.measureError = false;
            ppg_count = 0;
            ecg_count = 0;
            BluetoothGattCharacteristic characteristic = null;
            if(writeCharas!=null){
                characteristic = writeCharas;
            }


            byte[] send_data = new byte[5];
            //보낼 데이터
            send_data[0] = (byte)0x5A;
            send_data[1] = (byte)0x01;
            send_data[2] = (byte)0x06;
            send_data[3] = (byte)0xA1;
            send_data[4] = (byte)0xA5;
            //1분 10초 정도 데이터를 모아서 서버에 던져야함 - 할것
            if (characteristic != null) {
                mBluetoothLeService.writeCharacteristic(characteristic, send_data);
                Log.d("writeCharacteristic","measure start");
                for (final byte b : send_data) {
                    //Log.e("SEND_MEASURE", String.format("%02X ", b & 0xff));
                    Log.d("send_message", String.valueOf(b));
                }
            }
        }
        //측정 끝
        //코드보면 현재는 이 데이터들을 firebase에 던지는 거였음.
        //((GlobalVariables) mContext.getApplicationContext()).setRRIntervalEcg(RRIntervalEcg);
        //((GlobalVariables) mContext.getApplicationContext()).setRRIntervalPpg(RRIntervalPpg);
        private Send_to_Server_RawData_Weight_kotlin api_class = new Send_to_Server_RawData_Weight_kotlin(mContext.getApplicationContext());
        private Calculating_HR cal_hr = new Calculating_HR();
        public synchronized void measuring_end(boolean b) throws Exception {

            Log.d("measuring_end_process","start");
            if(mBluetoothLeService==null){
                throw new Exception();
            }
            key_is_measuring = true;


            //BluetoothGattCharacteristic초기화
            // writable characteristic
            BluetoothGattCharacteristic characteristic = null;
            if(writeCharas!=null){
                characteristic = writeCharas;
            }

            byte[] send_data = new byte[5];
            //측정 끝낸다는 데이터
            send_data[0] = (byte)0x5A;
            send_data[1] = (byte)0x01;
            send_data[2] = (byte)0x06;
            send_data[3] = (byte)0xA2;
            send_data[4] = (byte)0xA5;

            if (characteristic != null) {
                mBluetoothLeService.writeCharacteristic(characteristic, send_data);
                Log.d("writeCharacteristic","measure end");
                for (final byte c : send_data) {
                    //Log.e("SEND_MEASURE", String.format("%02X ", c & 0xff));
                }
            }
            key_eng_mode = false;
            if(b) {
                calculatingDialog.show();
                ((GlobalVariables) mContext.getApplicationContext()).setRRIntervalEcg(RRIntervalEcg);
                ((GlobalVariables) mContext.getApplicationContext()).setRRIntervalPpg(RRIntervalPpg);
                //
                ((GlobalVariables) mContext.getApplicationContext()).setEcg_ppg_tData(ecg_ppg_tData);

                ((GlobalVariables) mContext.getApplicationContext()).setHeartRate(heartRate);
                Double heartRate_sum = cal_hr.sum_HeartRate(heartRate);

                //api에 보내기 위한 더미 몸무게
                Double weight = 81.0;
                api_class.send_data_to_Server(weight,ecg_ppg_tData.toString(),calculatingDialog, heartRate_sum);
                ecg_ppg_tData = new StringBuffer();
                stopMeasuring();
                return;
            }else{
                measureHandlerOff();
            }

        }


        //마지막 측정된 온열상태 BLE에서 받아오기.
        public synchronized void heatRateLastStateGetStart(){
            if(mBluetoothLeService==null) return;

            BluetoothGattCharacteristic characteristic = null;
            if(writeCharas!=null){
                characteristic = writeCharas;
            }

            byte[] send_data = new byte[5];

            send_data[0] = (byte)0x5A;
            send_data[1] = (byte)0x01;
            send_data[2] = (byte)0x05;
            send_data[3] = (byte)0x01;
            send_data[4] = (byte)0xA5;
            final int get_TH = 1000;
            Log.d("get_TH", String.valueOf(get_TH));
            if (characteristic != null) {
                mBluetoothLeService.writeCharacteristic(characteristic, send_data);
                //TODO 온습도 계속 받아오는 스레드 가동
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getTHprocess().onStart();
                    }
                }, get_TH);
            }
        }

        public synchronized void setBack(){
            if(mBluetoothLeService==null) return;
            BluetoothGattCharacteristic characteristic = null;
            if(writeCharas!=null){
                characteristic = writeCharas;
            }
            byte[] send_data = new byte[5];

            if (state_fan == GlobalDefines.State.FAN_OFF) {
                // send 'FAN_ON' signal for hardware
                send_data[0] = (byte)0x5A;
                send_data[1] = (byte)0x01;
                send_data[2] = (byte)0x04;
                send_data[3] = (byte)0xA1;
                send_data[4] = (byte)0xA5;
            } else {
                // send 'FAN_OFF' signal for hardware
                send_data[0] = (byte)0x5A;
                send_data[1] = (byte)0x01;
                send_data[2] = (byte)0x04;
                send_data[3] = (byte)0xA2;
                send_data[4] = (byte)0xA5;
            }
            if (characteristic != null) {
                mBluetoothLeService.writeCharacteristic(characteristic, send_data);
            }
        }
        //흔들기능
        public synchronized void setMoving(){
            if(mBluetoothLeService==null) return;
            BluetoothGattCharacteristic characteristic = null;
            if(writeCharas!=null){
                characteristic = writeCharas;
            }
            byte[] send_data = new byte[5];

            if(state_mov == GlobalDefines.State.MOVING_OFF){
                send_data[0] = (byte)0x5A;
                send_data[1] = (byte)0x01;
                send_data[2] = (byte)0x11;
                send_data[3] = (byte)0xA0;
                send_data[4] = (byte)0xA5;
            } else if(state_mov == GlobalDefines.State.MOVING_1){
                send_data[0] = (byte)0x5A;
                send_data[1] = (byte)0x01;
                send_data[2] = (byte)0x11;
                send_data[3] = (byte)0xA1;
                send_data[4] = (byte)0xA5;
            } else if(state_mov == GlobalDefines.State.MOVING_2){
                send_data[0] = (byte)0x5A;
                send_data[1] = (byte)0x01;
                send_data[2] = (byte)0x11;
                send_data[3] = (byte)0xA2;
                send_data[4] = (byte)0xA5;
            } else if(state_mov == GlobalDefines.State.MOVING_3){
                send_data[0] = (byte)0x5A;
                send_data[1] = (byte)0x01;
                send_data[2] = (byte)0x11;
                send_data[3] = (byte)0xA3;
                send_data[4] = (byte)0xA5;
            }
            if (characteristic != null) {
                mBluetoothLeService.writeCharacteristic(characteristic, send_data);
            }

        }

        public synchronized void setMoveFoot(){
            if(mBluetoothLeService==null) return;
            BluetoothGattCharacteristic characteristic = null;
            if(writeCharas!=null){
                characteristic = writeCharas;
            }
            byte[] send_data = new byte[5];

            if(state_foot == GlobalDefines.State.MOV_FOOT_1){
                send_data[0] = (byte)0x5A;
                send_data[1] = (byte)0x01;
                send_data[2] = (byte)0x10;
                send_data[3] = (byte)0xA1;
                send_data[4] = (byte)0xA5;
            } else if(state_foot == GlobalDefines.State.MOV_FOOT_2){
                send_data[0] = (byte)0x5A;
                send_data[1] = (byte)0x01;
                send_data[2] = (byte)0x10;
                send_data[3] = (byte)0xA2;
                send_data[4] = (byte)0xA5;
            } else {
                send_data[0] = (byte)0x5A;
                send_data[1] = (byte)0x01;
                send_data[2] = (byte)0x10;
                send_data[3] = (byte)0xA3;
                send_data[4] = (byte)0xA5;
            }
            if (characteristic != null) {
                mBluetoothLeService.writeCharacteristic(characteristic, send_data);
            }
        }

        public synchronized void setMoveBack(){
            if(mBluetoothLeService==null) return;
            BluetoothGattCharacteristic characteristic = null;
            if(writeCharas!=null){
                characteristic = writeCharas;
            }
            byte[] send_data = new byte[5];

                if(state_back == GlobalDefines.State.MOV_BACK_1){
                    // send 'MOVE_BACK_1' signal for hardware
                    send_data[0] = (byte)0x5A;
                    send_data[1] = (byte)0x01;
                    send_data[2] = (byte)0x09;
                    send_data[3] = (byte)0xA1;
                    send_data[4] = (byte)0xA5;
                } else if(state_back == GlobalDefines.State.MOV_BACK_2){
                    // send 'MOVE_BACK_2' signal for hardware
                    send_data[0] = (byte)0x5A;
                    send_data[1] = (byte)0x01;
                    send_data[2] = (byte)0x09;
                    send_data[3] = (byte)0xA2;
                    send_data[4] = (byte)0xA5;
                }  else{
                    // send 'MOVE_BACK_3' signal for hardware
                    send_data[0] = (byte)0x5A;
                    send_data[1] = (byte)0x01;
                    send_data[2] = (byte)0x09;
                    send_data[3] = (byte)0xA3;
                    send_data[4] = (byte)0xA5;
                }
            if (characteristic != null) {
                mBluetoothLeService.writeCharacteristic(characteristic, send_data);
            }
        }


        //TODO 온열 단계변경 REQUEST
        public synchronized void setHeap(){
            if(mBluetoothLeService==null) return;
            BluetoothGattCharacteristic characteristic = null;
            if(writeCharas!=null){
                characteristic = writeCharas;
            }

            byte[] send_data = new byte[5];

            if (GlobalDefines.State.getState_heater() == GlobalDefines.State.HEATER_OFF) {
                // send 'HEATER_off' signal for hardware
                //아래 Hex코드들은 프로토콜들 값임
                //보내고자 하는 프로토콜 값 한줄로
                send_data[0] = (byte)0x5A;
                send_data[1] = (byte)0x01;
                send_data[2] = (byte)0x05;
                send_data[3] = (byte)0xA1;
                send_data[4] = (byte)0xA5;
            } else if (GlobalDefines.State.getState_heater() == GlobalDefines.State.HEATER_1) {
                // send 'HEATER_1' signal for hardware
                send_data[0] = (byte)0x5A;
                send_data[1] = (byte)0x01;
                send_data[2] = (byte)0x05;
                send_data[3] = (byte)0xA2;
                send_data[4] = (byte)0xA5;
            } else if(GlobalDefines.State.getState_heater() == GlobalDefines.State.HEATER_2) {
                // send 'HEATER_2' signal for hardware
                send_data[0] = (byte)0x5A;
                send_data[1] = (byte)0x01;
                send_data[2] = (byte)0x05;
                send_data[3] = (byte)0xA3;
                send_data[4] = (byte)0xA5;
            } else {
                // send 'HEATER_3' signal for hardware
                send_data[0] = (byte)0x5A;
                send_data[1] = (byte)0x01;
                send_data[2] = (byte)0x05;
                send_data[3] = (byte)0xA4;
                send_data[4] = (byte)0xA5;
            }
            if (characteristic != null) {
                mBluetoothLeService.writeCharacteristic(characteristic, send_data);
                Log.d("writeCharacteristic","setHeap");
            }
        }

        public void setBack_heap(){
            if(mBluetoothLeService==null) return;
            setBack();
            setHeap();
        }



        public synchronized void getTemperature(){
            if(mBluetoothLeService==null) return;
            Log.e("Request","getTemperature");
            BluetoothGattCharacteristic characteristic = null;
            if(writeCharas!=null){
                characteristic = writeCharas;
            }

            byte[] send_data = new byte[5];

            send_data[0] = (byte)0x5A;
            send_data[1] = (byte)0x01;
            send_data[2] = (byte)0x07;
            send_data[3] = (byte)0x01;
            send_data[4] = (byte)0xA5;
            if(characteristic!=null) {
                mBluetoothLeService.writeCharacteristic(characteristic, send_data);
            }
        }
        synchronized void getHumidity(){
            if(mBluetoothLeService==null) return;
            Log.e("Request","getHumidity");
            BluetoothGattCharacteristic characteristic = null;
            if(writeCharas!=null){
                characteristic = writeCharas;
            }

            byte[] send_data = new byte[5];

            send_data[0] = (byte)0x5A;
            send_data[1] = (byte)0x01;
            send_data[2] = (byte)0x08;
            send_data[3] = (byte)0x01;
            send_data[4] = (byte)0xA5;
            if(characteristic!=null) {
                mBluetoothLeService.writeCharacteristic(characteristic, send_data);
                Log.d("writeCharacteristic","getHumidity");
               /* for(final byte b : send_data){
                    Log.e("SEND_HUM", String.format("%02X ", b&0xff));
                }*/
            }
        }
        //ppgData 리셋
        //데이터 들어있는 값들 다 없앰.
        private void ppgDataReset(){
            receiver.mRunnable3 = new Runnable() {
                @Override
                public void run() {
                    if (!key_is_measuring) {
                        Log.w("BLEReceiver", "is Finished Measure");
                        try {
                            measuring_end(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            receiver.isMeasureRunning = false;
            receiver.mHandlerForMeasuringEnd = new Handler();
            //PPG
            receiver.ppg_float = new float[GlobalDefines.Setting.CHART_UPDATE_PERIOD];
            receiver.ppg_not_connected_array = new int[GlobalDefines.Setting.PPG_NOT_CONNECTED_COUNT];
            ppg_data_final = new float[GlobalDefines.Setting.SAMPLING_FREQ.intValue()*GlobalDefines.Setting.MEASURE_TIME+1000];
            Log.d("thenumber", String.valueOf(GlobalDefines.Setting.SAMPLING_FREQ.intValue()*GlobalDefines.Setting.MEASURE_TIME+1000));
            //ECG
            receiver.ecg_float = new float[GlobalDefines.Setting.CHART_UPDATE_PERIOD];
//            receiver.ppg_not_connected_array = new int[GlobalDefines.Setting.ECG_NOT_CONNECTED_COUNT];
            ecg_data_final = new float[GlobalDefines.Setting.SAMPLING_FREQ.intValue()*GlobalDefines.Setting.MEASURE_TIME+1000];
            data.resetData();
            key_is_measuring = false;
            int count = 0;
            Log.d("count", String.valueOf(count));
            count++;

        }

    }

    public SharedData getData() {
        return data;
    }

    @Override
    public void onSuccess(boolean onSuccess) {
        listener.onENGModeEnd(onSuccess);
    }
}
