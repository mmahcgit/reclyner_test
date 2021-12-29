package com.emmahc.smartchair.BLEModule;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.emmahc.smartchair.BluetoothLeService;
import com.emmahc.smartchair.GlobalDefines;
import com.emmahc.smartchair.R;
import com.emmahc.smartchair.activities.DeviceScanActivity;
import com.emmahc.smartchair.common.Prefer;
import com.emmahc.smartchair.common.SharedPreferenceManager;
import com.emmahc.smartchair.common.WritingDataToTXT;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.emmahc.smartchair.GlobalDefines.State.HEATER_1;
import static com.emmahc.smartchair.GlobalDefines.State.HEATER_2;
import static com.emmahc.smartchair.GlobalDefines.State.HEATER_3;
import static com.emmahc.smartchair.GlobalDefines.State.HEATER_OFF;
import static com.emmahc.smartchair.GlobalDefines.State.MOVING_1;
import static com.emmahc.smartchair.GlobalDefines.State.MOVING_2;
import static com.emmahc.smartchair.GlobalDefines.State.MOVING_3;
import static com.emmahc.smartchair.GlobalDefines.State.MOVING_OFF;
import static com.emmahc.smartchair.GlobalDefines.State.MOV_BACK_1;
import static com.emmahc.smartchair.GlobalDefines.State.MOV_BACK_2;
import static com.emmahc.smartchair.GlobalDefines.State.MOV_BACK_3;
import static com.emmahc.smartchair.GlobalDefines.State.MOV_FOOT_1;
import static com.emmahc.smartchair.GlobalDefines.State.MOV_FOOT_2;
import static com.emmahc.smartchair.GlobalDefines.State.MOV_FOOT_3;
import static com.emmahc.smartchair.activities.MainActivity.mContextMainActivity;

/**
 * Created by dev.oni on 2018. 10. 22..
 * Copyright or OutSourcing Source
 * certificate dev.oni
 */

public class BLEReceiver extends BroadcastReceiver {
    private BlueToothBinder binder;

    private BluetoothGattCharacteristic mNotifyCharacteristic,characteristic;


    private BluetoothGattCharacteristic readCharacteristic;
    private BluetoothGattCharacteristic writeCharacteristic;

    // PPG variables
    int ppg_idx = 0;
    int ppg_rpeak_idx = 0;
    int ppg_rpeak_idx_bef = 0;

    int ppg_correct_count = 0;
    Double ppg_filtered_data = 0.0;
    int ppg_filtered_data_bef_bef = 0;
    int ppg_filtered_data_bef = 0;
    int ppg_filtered_data_cur = 0;
    int ppg_not_connected_count = 0;
    int ppg_not_connected_sum = 0;

    //ECG variables
    int ecg_idx = 0;
    int ecg_rpeak_idx = 0;
    int ecg_rpeak_idx_bef = 0;

    int ecg_correct_count = 0;
    Double ecg_filtered_data = 0.0;
    int ecg_filtered_data_bef_bef = 0;
    int ecg_filtered_data_bef = 0;
    int ecg_filtered_data_cur = 0;
    int ecg_not_connected_count = 0;
    int ecg_not_connected_sum = 0;
    private boolean key_ecg_not_connected;
    public int[] ecg_not_connected_array;

    public ArrayList<Integer> heartRate = new ArrayList<>();

    //ECG 데이터가 짝수
    public float[] even_data_array = new float[250];

    int even_idx = 0;

    Double even_data = 0.0;

    private boolean key_ppg_not_connected;
    boolean measureError;

    public float[] ppg_float;
    public float[] ecg_float;
    public int[] ppg_not_connected_array;


    //PPG 데이터가 홀수
    public float[] odd_data_array = new float[250];

    //생체신호 로우 데이터 및 몸무게 업로드 api에 사용하기 위한 변수
    public String ecg_ppg_total_Data = "";
    public StringBuilder ecg_ppg_total_Data2 = new StringBuilder();
    public StringBuffer ecg_ppg_tData = new StringBuffer();

    //데이터 몇 개 들어오는 지 측정
    private int count_data = 0;

    private int wrongPacketCnt,
            unknownCount;//기존 개발자 논리구조 엉망이라서 차선책입니다.

    //현재 온열단계 저장
    private int heatRate;

    private String data = null;
    public boolean isMeasureRunning;

    private static Integer DataCount = 0;

    public Handler mHandlerForMeasuringEnd;
    public Runnable mRunnable3;

    public int THcount = 0;


    public BLEReceiver(BlueToothBinder binder) {
        this.binder = binder;
    }

    @Override
    public void onReceive(final Context context, Intent intent) {

        final String action = intent.getAction();
        if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
            Log.w("BroadcastReceiver","================= ACTION_GATT_CONNECTED =================");

        }
        else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
            Log.w("BroadcastReceiver","================= ACTION_GATT_DISCONNECTED =================");
//            if(!measureError)
                freek_BLE(context);
//            else
//                binder.getmBluetoothLeService().connect(binder.getmDeviceAddress());

            /**
             * 온열기 제어했는데
             * BLE 단말기 문제로 통신이 잘 안돼서 DISCONNECTED되면 이 Fragment에서 검은화면으로 멈춰버림.
             * Fragment 없애줘야함.
             */
//            ((MainActivity)mContextMainActivity).findViewById(R.id.tabLayout).setVisibility(View.VISIBLE);
//            ((MainActivity)mContextMainActivity).findViewById(R.id.fragmentContent).setClickable(false);
//            getFragmentManager().beginTransaction().remove(ChairFragment.this).commitNow();

        }
        else if(BluetoothLeService.END_API_PROCESS.equals(action)){
            Log.d("API_STATUS","IN receiver");
            String test = SharedPreferenceManager.getString_from_SharedPreference(context,"api_Response_Result");
            Log.d("API_STATUS","SP : "+test);
        }
        else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
            Log.e("BroadcastReceiver","================= ACTION_GATT_SERVICES_DISCOVERED =================");//TODO DEBUG
            // 수정

            //이 부분 수정해서 통신 되나 확인
            String uuid_service = "0000fff0-0000-1000-8000-00805f9b34fb";
            String uuid_rx = "0000fff1-0000-1000-8000-00805f9b34fb";
            String uuid_tx = "0000fff2-0000-1000-8000-00805f9b34fb";

            // Get specific Services & Characteristics by UUID
            for (BluetoothGattService gattService : binder.getmBluetoothLeService().getSupportedGattServices()) {
                List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
                Log.e("getUuid", gattService.getUuid().toString());
                if (gattService.getUuid().toString().equals(uuid_service)) {
                    Log.e("UUID_service",gattService.getUuid().toString() );
                    for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                        if (gattCharacteristic.getUuid().toString().equals(uuid_tx)) {
                            Log.e("UUID_tx",gattCharacteristic.getUuid().toString() );
                            binder.setWriteCharas(gattCharacteristic);
                        } else if (gattCharacteristic.getUuid().toString().equals(uuid_rx)) {
                            Log.e("UUID_rx",gattCharacteristic.getUuid().toString() );
                            binder.setReadCharas(gattCharacteristic);
                        };
                    }
                }
            }

            if (binder.getReadCharas() != null && binder.getWriteCharas() != null) {
                BluetoothGattCharacteristic readCharacteristic = binder.getReadCharas();
                BluetoothGattCharacteristic writeCharacteristic = binder.getWriteCharas();

                final int charaProp = writeCharacteristic.getProperties();
                Log.d("tlqkf", String.valueOf(charaProp));
                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                    // If there is an active notification on a characteristic, clear
                    // it first so it doesn't update the data field on the user interface.
                    if (this.readCharacteristic != null && this.writeCharacteristic != null) {
                        binder.getmBluetoothLeService().setCharacteristicNotification(
                                this.readCharacteristic, this.writeCharacteristic, false);
                    }
                    binder.getmBluetoothLeService().readCharacteristic(readCharacteristic);
                }

                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                   this.readCharacteristic = readCharacteristic;
                   this.writeCharacteristic = writeCharacteristic;
                    binder.getmBluetoothLeService().setCharacteristicNotification(
                            readCharacteristic, writeCharacteristic, true);
                }

            }
        }
        //심장 박동수 데이터 갖고 오는 부분
//        else if(BluetoothLeService.GET_HEART_DATA.equals(action)){
//
//        }
        else if (BluetoothLeService.GET_HEART_DATA.equals(action)) {
            if (binder.getActtion().key_eng_mode) {// For Engineer(ENG) mode
                if (mRunnable3 != null && !isMeasureRunning) {
                    isMeasureRunning = true;
                    mHandlerForMeasuringEnd.postDelayed(mRunnable3, GlobalDefines.Setting.MEASURE_TIME * 1000);

                }
                //데이터가 0~14사이이면 에러나는 거 해결해야됨. -> 데이터 15이상이면 됨.
                //packet에 데이터 들어감
                String packet = intent.getStringExtra(BluetoothLeService.EXTRA_DATA).substring(intent.getStringExtra(BluetoothLeService.EXTRA_DATA).indexOf("\n"));
                Log.d("packet", "packet : "+packet);
                //packet의 첫 번째 부분이 짝수 데이터
                //packet의 두 번째 부분이 홀수 데이터
                packet = packet.substring(1);
                int length = packet.trim().length();
                int number_of_data = (length + 1) / 3;

                int temp_data = 0;

                //ppg_idx - 0에서 249까지 가다가 다시 0으로 바뀜
                for (int i = 0; i < number_of_data; i++) {
                    //temp_data에서 에러 발생
                    try {
                        temp_data = Integer.parseInt(packet.substring((3 * i), (3 * i) + 2), 16);
                        Log.d("tem_data", "origin" + temp_data);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("temp_data", String.valueOf(e));
                        Log.d("temp_data", String.valueOf(temp_data));
                    }
                    count_data++;
                    //파형 이상해지는 이유가 값이 0.00인 애들이 있음.
                    //짝수 데이터
                    if (i % 2 == 0) {
                        try {
                            even_data = even_data - (GlobalDefines.Setting.LPF_COEFF_ECG * (even_data - temp_data));
                            //api서버에 넘기기 위한 코드
                            //여기서 에러 발생하는 듯
//                            ecg_ppg_total_Data2.append(even_data).append(" ;");
                            binder.getEcg_ppg_tData().append(even_data).append(" ;");
//                            ecg_ppg_total_Data += even_data + " ;";
//                            binder.getEcg_ppg_tData().append(even_data + " ;");
//                            ecg_ppg_tData.append(even_data).append(" ;");
                            //                    Log.d("even","${ecg_data_array.size} ${even_idx}")
                            even_data_array[even_idx] = even_data.floatValue();
                            Log.d("ecg_data", "ecg:"+ppg_filtered_data.floatValue());
                            even_idx++;
                            binder.getEcg_data_final()[binder.getActtion().ecg_count] = even_data.floatValue();
                            binder.getActtion().ecg_count++;
                        } catch (Exception e) {
                            try {
//                                binder.getActtion().measuring_end(false);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            Log.e("ECG_DATA_ERROR", "error occurred in BLEReceiver");
                            e.printStackTrace();
                        }
                        if (even_idx >= GlobalDefines.Setting.CHART_UPDATE_PERIOD) {
                            binder.getListener().onUpdateChart("ECG", even_data_array);

//                            ecg_float = new float[250];
                            even_data_array = new float[250];
                            even_idx = 0;
                        }
                        // save 3 points of data for peak detection
                        ecg_filtered_data_bef_bef = ecg_filtered_data_bef;
                        ecg_filtered_data_bef = ecg_filtered_data_cur;
                        ecg_filtered_data_cur = ecg_filtered_data.intValue();

                        // increase R peak index for calculating heart rate
                        ecg_rpeak_idx++;
                        // R peak detection with 3 points
                        if (((ecg_rpeak_idx > GlobalDefines.Setting.ECG_LOW_CUTOFF) & (ecg_rpeak_idx < GlobalDefines.Setting.ECG_HIGH_CUTOFF) & key_ecg_not_connected == false) | (ecg_correct_count >= GlobalDefines.Setting.CORRECT_COUNT_LIMIT)) {
                            ((AppCompatActivity) binder.getmContext()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //측정 파형 화면에서 실시간 HR값 표현을 소숫점 자리 죄외하고 표시
                                    //소수점 안나오게 변경 처리
                                    //실시간 심박수 관련
//                                    binder.getListener().onTextUpdate(R.id.bpm, String.format("%.0f", 1 / (ppg_rpeak_idx / GlobalDefines.Setting.SAMPLING_FREQ) * 60.0) + " bpm");
                                }
                            });
                            binder.getRRIntervalEcg().add(ecg_rpeak_idx);
                            //값들 초기화
                            ecg_rpeak_idx_bef = ecg_rpeak_idx;
                            ecg_rpeak_idx = 0;
                            ecg_correct_count = 0;
                        } else if (ecg_rpeak_idx > GlobalDefines.Setting.ECG_HIGH_CUTOFF) {
                            ecg_rpeak_idx = 0;
                        }


                    }
                    //홀수 데이터
                    //여기 부분에서 홀수인 것만 살린 것임
                    if (i % 2 != 0) {
                        // low pass filtering with first order linear equation
                        //차트 ui순서 특정x - 짝수를 위로 올리자.
                        //activity로 바꾸기.
                        try {
                            ppg_filtered_data = ppg_filtered_data - (GlobalDefines.Setting.LPF_COEFF_PPG * (ppg_filtered_data - temp_data));
                            //api서버에 넘기기 위한 코드
//                            ecg_ppg_total_Data += ppg_filtered_data+"; 0\n";
                            //binder.getEcg_ppg_tData().append(ecg_ppg_total_Data2);
                            binder.getEcg_ppg_tData().append(ppg_filtered_data).append("; 157\n");
//                            ecg_ppg_total_Data2.append(ppg_filtered_data).append(" ;");
//                            binder.getEcg_ppg_tData().append(ppg_filtered_data).append("; 157\n");

//                            ecg_ppg_tData.append(ppg_filtered_data).append("; 0\n");
                            ppg_float[ppg_idx] = ppg_filtered_data.floatValue();
                            Log.d("ppg_data", "ppg:"+ppg_filtered_data.floatValue());
                            ppg_idx++;
                            binder.getPpg_data_final()[binder.getActtion().ppg_count] = ppg_filtered_data.floatValue();//Error
                            binder.getActtion().ppg_count++;
                        } catch (ArrayIndexOutOfBoundsException e) {
                            try {
//                                binder.getActtion().measuring_end(false);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            Log.e("PPG_DATA_ERROR", "error occurred in BLEReceiver");
                            Toast.makeText(context, "측정에 실패했습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                        } catch (NullPointerException e) {
                            try {
//                                binder.getActtion().measuring_end(false);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            Toast.makeText(context, "측정에 실패했습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                        }

                        //차트 어
                        if (ppg_idx >= GlobalDefines.Setting.CHART_UPDATE_PERIOD) {
//                            Log.d("ppg_float_value", String.valueOf(ppg_float));
//                            //PPG 차트에 데이터 넣는 부분
                            binder.getListener().onUpdateChart("PPG", ppg_float);


                            //초기화
                            ppg_idx = 0;
                        }

                        // save 3 points of data for peak detection
                        ppg_filtered_data_bef_bef = ppg_filtered_data_bef;
                        ppg_filtered_data_bef = ppg_filtered_data_cur;
                                ppg_filtered_data_cur = ppg_filtered_data.intValue();

                                // increase R peak index for calculating heart rate
                                ppg_rpeak_idx++;

                                // R peak detection with 3 points
                                if ((ppg_filtered_data_bef > ppg_filtered_data_bef_bef) & (ppg_filtered_data_bef >= ppg_filtered_data_cur)) {
                                    // detect exceptions : min HR, max HR, PPG not connected
                                    if (((ppg_rpeak_idx > GlobalDefines.Setting.PPG_LOW_CUTOFF) & (ppg_rpeak_idx < GlobalDefines.Setting.PPG_HIGH_CUTOFF) & key_ppg_not_connected == false) | (ppg_correct_count >= GlobalDefines.Setting.CORRECT_COUNT_LIMIT)) {
                                        ((AppCompatActivity) binder.getmContext()).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        /**
                                         * 2018-10-30
                                         * 1. 측정 파형 화면에서 실시간 HR 값 표현을 소숫점 자리 제외하고 표시
                                         *
                                         * 소수점 안나오게 변경처리
                                         */
                                        binder.getListener().onTextUpdate(R.id.bpm, String.format("%.0f", 1 / (ppg_rpeak_idx / GlobalDefines.Setting.SAMPLING_FREQ) * 60.0) + " bpm");
                                        binder.getHeartRate().add((ppg_not_connected_sum / GlobalDefines.Setting.PPG_NOT_CONNECTED_COUNT));
                                    }
                                });
                                binder.getRRIntervalPpg().add(ppg_rpeak_idx);
                                // clear idx and count, and save 'ppg_rpeak_idx_bef' for check if next ppg R peak is way too low or high
                                ppg_rpeak_idx_bef = ppg_rpeak_idx;
                                ppg_rpeak_idx = 0;
                                ppg_correct_count = 0;
                            } else if (ppg_rpeak_idx > GlobalDefines.Setting.PPG_HIGH_CUTOFF) {
                                // clear ppg R peak index when ppg R peak index is way too high
                                ppg_rpeak_idx = 0;
                            }

                            // PPG not connected detection
                            if (ppg_not_connected_count >= GlobalDefines.Setting.PPG_NOT_CONNECTED_COUNT) {
                                ppg_not_connected_sum = 0;

                                for (i = 0; i < GlobalDefines.Setting.PPG_NOT_CONNECTED_COUNT; i++) {
                                    // PPG energy summation for detecting PPG not connected
                                    ppg_not_connected_sum += ppg_not_connected_array[i];
                                }

                                // set text if summation of PPG is way too high for PPG WRONG CONNECTED
                                if ((ppg_not_connected_sum / GlobalDefines.Setting.PPG_NOT_CONNECTED_COUNT) > GlobalDefines.Setting.THRESHOLD_WRONG_CONNECTED) {
                                    ((AppCompatActivity) binder.getmContext()).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            binder.getListener().onTextUpdate(R.id.tv_eng_ppg, "PPG WRONG CONNECTED" + "\n" + String.valueOf((ppg_not_connected_sum / GlobalDefines.Setting.PPG_NOT_CONNECTED_COUNT)));
                                            binder.getHeartRate().add((ppg_not_connected_sum / GlobalDefines.Setting.PPG_NOT_CONNECTED_COUNT));
                                        }
                                    });
                                    key_ppg_not_connected = true;
                                } else {
                                    key_ppg_not_connected = false;
                                }

                                ppg_not_connected_count = 0;
                            } else {
                                try {
                                    ppg_not_connected_array[ppg_not_connected_count] = ppg_filtered_data_cur;
                                    ppg_not_connected_count++;
                                } catch (NullPointerException e) {
                                    try {
//                                        binder.getActtion().measuring_end(false);
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                    Toast.makeText(context, "측정에 실패했습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                    //데이터를 마지막 까지 가져왔을 때, txt파일에 인풋데이터들 작성
                }
//                Log.d("input_total_Data",ecg_ppg_total_Data);
//                binder.getEcg_ppg_total_Data().add(ecg_ppg_total_Data);
//                binder.getEcg_ppg_tData().append(ecg_ppg_tData);
//                binder.getEcg_ppg_total_Data2().add(ecg_ppg_total_Data2);
//                binder.getEcg_ppg_tData().append(ecg_ppg_total_Data2);
            }
        }else if(BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)){
                THcount ++;
                //여기로 계속 들어오는중
                // data display
                if (intent.getStringExtra(BluetoothLeService.EXTRA_DATA).indexOf("5A") >= 0) {
                    Log.d("heello",intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                    // get length, function, data from received packet
                    String packet = intent.getStringExtra(BluetoothLeService.EXTRA_DATA).substring(intent.getStringExtra(BluetoothLeService.EXTRA_DATA).indexOf("5A"));
                    Log.d("text",packet);
                    //Log.e("BroadcastReceiver - 2",packet);//TODO DEBUG
                    // 수정 : packet.length 어떻게 할지 생각
                    if (packet.length() > 12 && packet.length() < 16) {
                        int length = Integer.parseInt(packet.substring(3, 5).trim(), 16);
                        int function = Integer.parseInt(packet.substring(6, 8).trim(), 16);
                        String tail = packet.substring(packet.length() - 3, packet.length()).trim();

                        // 수정
                        //                        Toast.makeText(ControlChairActivity.this, packet + "\n" + packet.length() + "\n" + String.valueOf(length), Toast.LENGTH_SHORT).show();

                        // 수정 : 각 모드 돌입했을 때 서로 다른(ENG 일 떄는 Normal, Normal 일 때는 ENG 데이터가 들어오면 WARNING 추가하기

                        /*  For Modes except ENG mode   */
                        if ((packet.length() == 3 * (4 + length)) && (tail.equals("A5"))) {
                            data = "";
                            for (int i = 0; i < Integer.parseInt(String.valueOf(length)); i++) {
                                data += packet.substring(9 + (3 * i), 11 + (3 * i)).trim();

                            }
                            Log.d("datadata",data);
                            Log.d("datadata",Integer.parseInt(data, 16)+"");
                            wrongPacketCnt=0;
                            receivePacket(function);
                        } else {
                            wrongPacketCnt++;
                            if(wrongPacketCnt>10) {
                                //TODO 잘못된패킷 10번이상 계속 일어나면 앱 재실행
                                freek_BLE(context);
                            }
                        }
                    }else{
                        /*new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mContextMainActivity.getBinder().getActtion().measuring_end(false);
                                Toast.makeText(context, "측정에 실패했습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                            }
                        },750);*/
                    }
                    //First Bind State.
                }
                    if(!binder.isBindStatus()){
                        binder.setBindStatus(true);
                        Log.d("BIND", "setBindStatus true");
                        //온도습도 받아오기
                        /**
                         * 테스트결과, Station에 Heap구조가 없어서 FIFO같은 Response 저장장치가 없는것으로 판단.
                         * 한번에 2개의 데이터
                         * {@see getTemperature()}
                         * {@see getHumidity()}
                         *
                         * 를 전송하니까 나중에 보낸거 못받아오고있음.
                         */
                        binder.getActtion().heatRateLastStateGetStart();

                        //온습도 갱신 로직 실행
                        Log.d("BIND", "온도갱신 실행중");
                    }
                    else{
//                    Log.e("ERROR","measure Error");
                    }

        }
    }
    //BLE 연결 종료
    private void freek_BLE(final Context ctx) {
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                //리시브 등록 해제
                binder.onPause();
                //서비스 등록 해제
                binder.unbindService();
                //리시버랑 서비스 등록이 끝난 후, Toast
                Toast t = Toast.makeText(binder.getmContext(),"BLE 연결이 종료되었습니다.", Toast.LENGTH_SHORT);
                t.setGravity(Gravity.CENTER,0,0);
                t.show();
                //toast끝난후 디바이스 스캔activity로 넘어가야하는데, 현재는 디바이스명과 sharedPreference에 등록된 값이랑 같기 때문에
                //블루투스 연결 끝날 때마다 계속 액티비티 잠깐 보여주고 마는 것임.
                Intent intent = new Intent(ctx, DeviceScanActivity.class);
                String prefName_device = SharedPreferenceManager.getString_from_SharedPreference(ctx,"now_device_name");
                String prefName_address = SharedPreferenceManager.getString_from_SharedPreference(ctx,"now_device_address");
                Prefer.deleteDevice_pref(ctx,Prefer.PREF_EXTRAS_DEVICE_NAME,prefName_device);
                Prefer.deleteDevice_pref(ctx,Prefer.PREF_EXTRAS_DEVICE_ADDRESS,prefName_address);
                ctx.startActivity(intent);
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Intent intent = new Intent(ctx, DeviceScanActivity.class);
//                        ctx.startActivity(intent);
//
//                    }
//                },5000);
            }
        });
    }

    public void getmHandlerForMeasuringEnd() {
        if(mHandlerForMeasuringEnd!=null)
            mHandlerForMeasuringEnd.removeCallbacks(mRunnable3);
    }

    //현재 온열단계 리턴 메소드
    public int getHeatRate() {
        return heatRate;
    }

    private void receivePacket(int function){
        Log.d("recievePacket", String.valueOf(function));
        switch (function) {
            case 0x01:              // Normal mode data acquisition
                int ecg_hr = Integer.parseInt(data.substring(0, 2), 16);
                int ppg_hr = Integer.parseInt(data.substring(4, 6), 16);

                binder.getListener().onTextUpdate(R.id.tv_ecg_hr,"ECG\n" + Integer.toString(ecg_hr));
                binder.getListener().onTextUpdate(R.id.tv_ppg_hr,"PPG\n" + Integer.toString(ppg_hr));
                break;

            case 0x03:              // Measure fail
                switch (data) {
                    case "A1":      // Measure fail
                        Toast.makeText(binder.getmContext(), R.string.warning_measure_fail, Toast.LENGTH_SHORT).show();
                        break;

                    default:

//                        Toast.makeText(binder.getmContext(), R.string.warning_wrong_data, Toast.LENGTH_SHORT).show();
                        break;
                }
                break;

            case 0x04:              // FAN
                switch (data) {
                    case "A1":      // FAN_ON
                        binder.getListener().onTextUpdate(R.id.btn_fan,"FAN\nON");
                        binder.getListener().onBackgroundResourceUpdate(R.id.btn_fan,R.drawable.orange_circle);

                        binder.getActtion().state_fan = GlobalDefines.State.FAN_ON;
                        break;

                    case "A2":      // FAN_OFF
                        binder.getListener().onTextUpdate(R.id.btn_fan,"FAN\nOFF");
                        binder.getListener().onBackgroundResourceUpdate(R.id.btn_fan,R.drawable.gray_circle);

                        binder.getActtion().state_fan = GlobalDefines.State.FAN_OFF;
                        break;

                    default:
                        Toast.makeText(binder.getmContext(), R.string.warning_wrong_data, Toast.LENGTH_SHORT).show();
                        break;
                }
                break;

            case 0x05:              // HEATER
                switch (data) {
                    case "A1":      // HEATER_OFF
                        GlobalDefines.State.setState_heater(GlobalDefines.State.HEATER_OFF);
                        Log.e("온열기능", String.valueOf(HEATER_OFF));
                        break;

                    case "A2":      // HEATER_1
                        GlobalDefines.State.setState_heater(GlobalDefines.State.HEATER_1);
                        Log.e("온열기능", String.valueOf(HEATER_1));
                        break;

                    case "A3":      // HEATER_2
                        GlobalDefines.State.setState_heater(GlobalDefines.State.HEATER_2);
                        Log.e("온열기능", String.valueOf(HEATER_2));
                        break;

                    case "A4":      // HEATER_3
                        GlobalDefines.State.setState_heater(GlobalDefines.State.HEATER_3);
                        Log.e("온열기능", String.valueOf(HEATER_3));
                        break;
                    default:
                        Toast.makeText(binder.getmContext(), R.string.warning_wrong_data, Toast.LENGTH_SHORT).show();
                        break;
                }
                break;

            case 0x06:              // ENG mode
                switch (data) {
                    case "A1":      // ENG mode start
//                        binder.getActtion().key_is_measuring = false;
                        binder.getActtion().key_eng_mode = true;
                        Log.d("ENG_mode(A1)","END_MODE(A1)");
//                        Toast.makeText(binder.getmContext(), "ENG mode Start.", Toast.LENGTH_SHORT).show();
                        binder.getListener().onENGModeStart();
                        break;

                    case "A2":      // ENG mode end
                        Log.w("ENG END","ENG END");
//                        binder.getActtion().key_eng_mode = false;

                        break;
                }
                break;

            case 0x07:              // Normal mode
                try {
                    Log.d("Normal_mode","Normal_mode");
                    binder.getListener().onTextUpdate(R.id.temperature, Integer.parseInt(data, 16)+"");

                    binder.getActtion().getHumidity();
                }catch (NullPointerException e){
                    if(mContextMainActivity!=null)
                        binder.setListener((BLEActionListener) mContextMainActivity.getFragment());
                    e.printStackTrace();
                }
                break;
            case 0x08:
                try {
                    binder.getListener().onTextUpdate(R.id.humidity,Integer.parseInt(data, 16)+"");
                }catch (NullPointerException e){
                    if(mContextMainActivity!=null)
                        binder.setListener((BLEActionListener) mContextMainActivity.getFragment());
                    e.printStackTrace();
                }
                break;

//            case 0x09:  // 자세 등판 조절 - function 9
//                switch (data) {
//                    case "A1":      // 자세 등판 앞으로 조절
//                        binder.getActtion().state_back = GlobalDefines.State.MOV_BACK_1;
//                        Log.d("wrong_function","function: "+function + " data: "+data);
//                        Log.e("자세 등판 조절", String.valueOf(MOV_BACK_1));
//                        break;
//
//                    case "A2":      // 자세 등판 뒤로 조절
//                        binder.getActtion().state_back = GlobalDefines.State.MOV_BACK_2;
//                        Log.e("자세 등판 조절", String.valueOf(MOV_BACK_2));
//                        break;
//
//                    case "A3":      // 자세 등판 원래대로
//                        binder.getActtion().state_back = GlobalDefines.State.MOV_BACK_3;
//                        Log.e("자세 등판 조절", String.valueOf(MOV_BACK_3));
//                        break;
//
//                    default:
//                        Toast.makeText(binder.getmContext(), R.string.warning_wrong_data, Toast.LENGTH_SHORT).show();
//                        break;
//                }
//            case 0x10:  // 자세 발판 조절 - function 16
//                switch (data) {
//                    case "A1":      // 자세 발판 앞으로 조절 ;
//                        binder.getActtion().state_foot = GlobalDefines.State.MOV_FOOT_1;
//                        Log.e("자세 발판 조절", String.valueOf(MOV_FOOT_1));
//                        break;
//
//                    case "A2":      // 자세 발판 뒤로 조절
//                        binder.getActtion().state_foot = GlobalDefines.State.MOV_FOOT_2;
//                        Log.e("자세 발판 조절", String.valueOf(MOV_FOOT_2));
//                        break;
//
//                    case "A3":      // 자세 발판 원래대로
//                        binder.getActtion().state_foot = GlobalDefines.State.MOV_FOOT_3;
//                        Log.e("자세 발판 조절", String.valueOf(MOV_FOOT_3));
//                        break;
//
//                    default:
//                        Log.d("state_foot",data);
//                        Toast.makeText(binder.getmContext(), R.string.warning_wrong_data, Toast.LENGTH_SHORT).show();
//                        break;
//                }
//            case 0x11:
//                switch(data){
//                    case "A0":
//                        binder.getActtion().state_mov = GlobalDefines.State.MOVING_OFF;
//                        Log.e("흔들기능", String.valueOf(MOVING_OFF));
//                        break;
//                    case "A1":
//                        binder.getActtion().state_mov = GlobalDefines.State.MOVING_1;
//                        Log.e("흔들기능", String.valueOf(MOVING_1));
//                        break;
//                    case "A2":
//                        binder.getActtion().state_mov = GlobalDefines.State.MOVING_2;
//                        Log.e("흔들기능", String.valueOf(MOVING_2));
//                        break;
//                    case "A3":
//                        binder.getActtion().state_mov = GlobalDefines.State.MOVING_3;
//                        Log.e("흔들기능", String.valueOf(MOVING_3));
//                        break;
//                    default:
//                        Toast.makeText(binder.getmContext(), R.string.warning_wrong_data, Toast.LENGTH_SHORT).show();
//                        break;
//                }
//            default:
//                Log.d("wrong_function", String.valueOf(function));
//                Toast.makeText(binder.getmContext(), R.string.warning_wrong_function, Toast.LENGTH_SHORT).show();
//                break;
        }
    }
    private void writingText(String file_path, String file_name, String contents){
        try{
            File dir = new File(file_path);
            if(!dir.exists()){
                dir.mkdir();
            }
            FileOutputStream fos = new FileOutputStream(dir+"/"+file_name,true);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            writer.write(contents);
            writer.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }



}
