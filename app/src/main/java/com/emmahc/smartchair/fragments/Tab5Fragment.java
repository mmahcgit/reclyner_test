package com.emmahc.smartchair.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.emmahc.smartchair.BLEModule.BLEActionListener;
import com.emmahc.smartchair.GlobalDefines;
import com.emmahc.smartchair.R;
import com.emmahc.smartchair.common.SharedPreferenceManager;
import com.emmahc.smartchair.ui.ColorTextView;
import com.nex3z.togglebuttongroup.SingleSelectToggleGroup;
import com.nex3z.togglebuttongroup.button.CircularToggle;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;


import static com.emmahc.smartchair.activities.HomeActivity.mContextMainActivity;

/**
 * Fragment including 'START' button for start to connect with Smart chair hardware device.
 */


public class Tab5Fragment extends Fragment implements BLEActionListener {
    private ColorTextView temperature_display,humidity_display;

    public Button btnhetoff, btnhetone, btnhettwo, btnhetthree;
    public Button btnmovoff, btnmovone, btnmovtwo, btnmovthree;
    public Button btnmovbackgo, btnmovbackback, btnmovbackbeg;
    public Button btnfootgo, btnfootback, btnfootbeg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContextMainActivity.getBinder().setListener(this);
    }

    //button단에서 리스너를 RepeatListener로 바꾸면 가능
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState ) {
        final View view = inflater.inflate( R.layout.fragment_tab5, container, false );
        temperature_display = view.findViewById(R.id.temperature_display);
        humidity_display = view.findViewById(R.id.humidity_display);
        btnhetoff = view.findViewById(R.id.btnhetoff);
        btnhetone = view.findViewById(R.id.btnhetone);
        btnhettwo = view.findViewById(R.id.btnhettwo);
        btnhetthree = view.findViewById(R.id.btnhetthree);
        btnmovoff = view.findViewById(R.id.btnmovoff);
        btnmovone = view.findViewById(R.id.btnmovone);
        btnmovtwo = view.findViewById(R.id.btnmovtwo);
        btnmovthree = view.findViewById(R.id.btnmovthree);
        //등판
        btnmovbackgo = view.findViewById(R.id.btnmovbackgo);
        btnmovbackback = view.findViewById(R.id.btnmovbackback);
        btnmovbackbeg = view.findViewById(R.id.btnmovbackbeg);
        //발판
        btnfootgo = view.findViewById(R.id.btnfootgo);
        btnfootback = view.findViewById(R.id.btnfootback);
        btnfootbeg = view.findViewById(R.id.btnfootbeg);


        //등판, 발판 조절이
        //ContinousClick기능 필요
        //둘다 앞으로, 뒤로
        //리팩토링
        //Tab5fragment -> MainActivity의 action -> BluetoothBinder
        //data받는 것은 BLEReceiver의 receivePacket
        //if문에서 else로 해놔서 계속 패킷을 받고 있는 상태
//        final byte[] send_data = new byte[5];
        Button.OnClickListener c1 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(view.getId()){
                    case R.id.btnhetoff:
                        byte[] heat_data1 = new byte[5];
                        heat_data1[0] = (byte) 0x5A;
                        heat_data1[1] = (byte) 0x01;
                        heat_data1[2] = (byte) 0x05;
                        heat_data1[3] = (byte) 0xA1;
                        heat_data1[4] = (byte) 0xA5;
                        mContextMainActivity.getBinder().getActtion().moving_Recliner(heat_data1);
                        break;
                    case R.id.btnhetone:
                        byte[] heat_data2 = new byte[5];
                        heat_data2[0] = (byte) 0x5A;
                        heat_data2[1] = (byte) 0x01;
                        heat_data2[2] = (byte) 0x05;
                        heat_data2[3] = (byte) 0xA2;
                        heat_data2[4] = (byte) 0xA5;
                        mContextMainActivity.getBinder().getActtion().moving_Recliner(heat_data2);
                        break;
                    case R.id.btnhettwo:
                        byte[] heat_data3 = new byte[5];
                        heat_data3[0] = (byte) 0x5A;
                        heat_data3[1] = (byte) 0x01;
                        heat_data3[2] = (byte) 0x05;
                        heat_data3[3] = (byte) 0xA3;
                        heat_data3[4] = (byte) 0xA5;
                        mContextMainActivity.getBinder().getActtion().moving_Recliner(heat_data3);
                        break;
                    case R.id.btnhetthree:
                        byte[] heat_data4 = new byte[5];
                        heat_data4[0] = (byte) 0x5A;
                        heat_data4[1] = (byte) 0x01;
                        heat_data4[2] = (byte) 0x05;
                        heat_data4[3] = (byte) 0xA4;
                        heat_data4[4] = (byte) 0xA5;
                        mContextMainActivity.getBinder().getActtion().moving_Recliner(heat_data4);
                        break;

                    case R.id.btnmovoff:
                        byte[] send_data1 = new byte[5];
                        send_data1[0] = (byte)0x5A;
                        send_data1[1] = (byte)0x01;
                        send_data1[2] = (byte)0x11;
                        send_data1[3] = (byte)0xA1;
                        send_data1[4] = (byte)0xA5;
                        mContextMainActivity.getBinder().getActtion().moving_Recliner(send_data1);
                        break;
                    case R.id.btnmovone:
                        byte[] send_data2 = new byte[5];

                        send_data2[0] = (byte)0x5A;
                        send_data2[1] = (byte)0x01;
                        send_data2[2] = (byte)0x11;
                        send_data2[3] = (byte)0xA2;
                        send_data2[4] = (byte)0xA5;
                        mContextMainActivity.getBinder().getActtion().moving_Recliner(send_data2);
                        break;
                    case R.id.btnmovtwo:
                        byte[] send_data3 = new byte[5];

                        send_data3[0] = (byte)0x5A;
                        send_data3[1] = (byte)0x01;
                        send_data3[2] = (byte)0x11;
                        send_data3[3] = (byte)0xA3;
                        send_data3[4] = (byte)0xA5;
                        mContextMainActivity.getBinder().getActtion().moving_Recliner(send_data3);
                        break;
                    case R.id.btnmovthree:
                        byte[] send_data4 = new byte[5];

                        send_data4[0] = (byte)0x5A;
                        send_data4[1] = (byte)0x01;
                        send_data4[2] = (byte)0x11;
                        send_data4[3] = (byte)0xA4;
                        send_data4[4] = (byte)0xA5;
                        mContextMainActivity.getBinder().getActtion().moving_Recliner(send_data4);
                        break;
                    //등판과 발판 원래대로
                    case R.id.btnmovbackbeg:
                        GlobalDefines.State.MOV_BACK_REQ = GlobalDefines.State.MOV_BACK_3;
                        startService(4);
                        Log.e("등판 상태", String.valueOf(GlobalDefines.State.MOV_BACK_REQ));
                        break;
                    case R.id.btnfootbeg:
                        GlobalDefines.State.MOV_FOOT_REQ = GlobalDefines.State.MOV_FOOT_3;
                        startService(5);
                        Log.e("발판 상태", String.valueOf(GlobalDefines.State.MOV_FOOT_REQ));
                        break;
                }
            }
        };

        btnhetoff.setOnClickListener(c1);
        btnhetone.setOnClickListener(c1);
        btnhettwo.setOnClickListener(c1);
        btnhetthree.setOnClickListener(c1);

        btnmovoff.setOnClickListener(c1);
        btnmovone.setOnClickListener(c1);
        btnmovtwo.setOnClickListener(c1);
        btnmovthree.setOnClickListener(c1);

        //등판과 발판 리스너로
        //눌렀을때 움직이게 하고, 손까락을 땠을 때, 멈추게 작동.
        Button.OnTouchListener touch =  new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                byte[] send_data = new byte[5];
                int action = motionEvent.getAction();
                switch (action){
                    //눌렀을때
                    case MotionEvent.ACTION_DOWN:
                        send_data = get_Send_Data(view);
                        mContextMainActivity.getBinder().getActtion().moving_Recliner(send_data);
                        Log.d("send_status","send");
                        break;
                    //멈췄을 때
                    case MotionEvent.ACTION_UP:
                        send_data = get_Stop_Send_Data(view);
                        mContextMainActivity.getBinder().getActtion().moving_Recliner(send_data);
                        Log.d("send_status","send_stop");
                        break;
                }
                return false;
            }
        };
        btnmovbackgo.setOnTouchListener(touch);
        btnmovbackback.setOnTouchListener(touch);
//        btnmovbackbeg.setOnTouchListener(touch);
        btnmovbackbeg.setOnClickListener(c1);


        btnfootgo.setOnTouchListener(touch);
        btnfootback.setOnTouchListener(touch);
        btnfootbeg.setOnClickListener(c1);



        view.findViewById(R.id.btn_fragment5_start).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //측정 시작하면 sharePreferenceManager에 measure_start_check에 start값 넣음
                SharedPreferenceManager.setData_to_SharedPrerence(getContext(), "measure_start_check","true","Boolean");
                startService(0);//  측정

            }
        });

        return view;
    }

    private byte[] get_Send_Data(View view){
        byte[] send_data = new byte[5];
        switch (view.getId()){
            case R.id.btnmovbackgo:
                send_data[0] = (byte)0x5A;
                send_data[1] = (byte)0x01;
                send_data[2] = (byte)0x09;
                send_data[3] = (byte)0xA1;
                send_data[4] = (byte)0xA5;
                break;
            case R.id.btnmovbackback:
                send_data[0] = (byte)0x5A;
                send_data[1] = (byte)0x01;
                send_data[2] = (byte)0x09;
                send_data[3] = (byte)0xA2;
                send_data[4] = (byte)0xA5;
                break;
            case R.id.btnmovbackbeg:
                send_data[0] = (byte)0x5A;
                send_data[1] = (byte)0x01;
                send_data[2] = (byte)0x09;
                send_data[3] = (byte)0xA3;
                send_data[4] = (byte)0xA5;
                break;
            case R.id.btnfootgo:
                send_data[0] = (byte)0x5A;
                send_data[1] = (byte)0x01;
                send_data[2] = (byte)0x10;
                send_data[3] = (byte)0xA1;
                send_data[4] = (byte)0xA5;
                break;
            case R.id.btnfootback:
                send_data[0] = (byte)0x5A;
                send_data[1] = (byte)0x01;
                send_data[2] = (byte)0x10;
                send_data[3] = (byte)0xA2;
                send_data[4] = (byte)0xA5;
                break;
            case R.id.btnfootbeg:
                send_data[0] = (byte)0x5A;
                send_data[1] = (byte)0x01;
                send_data[2] = (byte)0x10;
                send_data[3] = (byte)0xA3;
                send_data[4] = (byte)0xA5;
                break;
        }
        return send_data;
    }
    private byte[] get_Stop_Send_Data(View view){
        byte[] send_data = new byte[5];
        switch (view.getId()){
            //등판
            case R.id.btnmovbackgo:
            case R.id.btnmovbackback:
            case R.id.btnmovbackbeg:
                send_data[0] = (byte)0x5A;
                send_data[1] = (byte)0x01;
                send_data[2] = (byte)0x09;
                send_data[3] = (byte)0xA4;
                send_data[4] = (byte)0xA5;
                break;
            //발판
            case R.id.btnfootgo:
            case R.id.btnfootback:
            case R.id.btnfootbeg:
                send_data[0] = (byte)0x5A;
                send_data[1] = (byte)0x01;
                send_data[2] = (byte)0x10;
                send_data[3] = (byte)0xA4;
                send_data[4] = (byte)0xA5;
                break;
        }
        return send_data;
    }

    private void startService(int sel){
        mContextMainActivity.action(sel,this);
    }


    @Override
    public void onENGModeStart() {

    }

    @Override
    public void onENGModeEnd(boolean b) {

    }

    @Override
    public void onUpdateChart(String chart, float[] x) {

    }

    //            SharedPreferenceManager.setData_to_SharedPrerence(getContext(), "HTime","true","Boolean");
    //            SharedPreferenceManager.setData_to_SharedPrerence(getContext(), "TTime","true","Boolean");
    @Override
    public void onTextUpdate(int id, CharSequence text) {
        ((TextView) getView().findViewById(id)).setText(text);
        if(id==R.id.humidity){//습도
            humidity_display.setText(text);
            int i = Integer.parseInt((String) text);
//            calculatingDialog.cancel();
//            if(mContextMainActivity.getCalculatingDialog().isShowing()){
//                mContextMainActivity.getCalculatingDialog().dismiss();
//            }
            if(mContextMainActivity.getLoading_layout().getVisibility() == View.VISIBLE){
                mContextMainActivity.getLoading_layout().setVisibility(View.GONE);
            }
            if(i>60){
                humidity_display.addTextColor("실내 습도가",0xff5b5b5b);
                humidity_display.addTextColor("높아요",0xff339cff);
            }else if(i<61&&i>39){
                humidity_display.addTextColor("실내 습도가",0xff5b5b5b);
                humidity_display.addTextColor("적정해요",0xff20981D);
            }else{
                humidity_display.addTextColor("실내 습도가",0xff5b5b5b);
                humidity_display.addTextColor("낮아요",0xffe71f22);
            }
            humidity_display.apply();

        }else if(id==R.id.temperature){//온도
            temperature_display.setText(text);
            int i = Integer.parseInt((String) text);

//            calculatingDialog.cancel();
            if(i>28){
                temperature_display.addTextColor("실내가 조금",0xff5b5b5b);
                temperature_display.addTextColor("덥네요",0xffe71f22);
            }else if(i<29&&i>20){
                temperature_display.addTextColor("실내 온도가",0xff5b5b5b);
                temperature_display.addTextColor("적정해요",0xff20981D);
            }else{
                temperature_display.addTextColor("실내 온도가",0xff5b5b5b);
                temperature_display.addTextColor("서늘해요",0xff339cff);
            }
            temperature_display.apply();
        }
        humidity_display.setText();
        temperature_display.setText();
    }

    @Override
    public void onTextColorUpdate(int id, int color) {
        try {
            ((TextView) getView().findViewById(id)).setTextColor(color);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackgroundResourceUpdate(int id, int drawableId) {
        try {
            getView().findViewById(id).setBackgroundColor(drawableId);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
}

