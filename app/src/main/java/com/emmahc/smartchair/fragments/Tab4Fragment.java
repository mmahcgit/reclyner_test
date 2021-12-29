package com.emmahc.smartchair.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.emmahc.smartchair.History.history_adapter;
import com.emmahc.smartchair.R;
import com.emmahc.smartchair.ServerAPI.Send_to_Server_measure_history;
import com.emmahc.smartchair.common.SharedPreferenceManager;
import com.emmahc.smartchair.dto.result_history_api_dto;
import com.google.gson.Gson;

import org.honorato.multistatetogglebutton.MultiStateToggleButton;
import org.honorato.multistatetogglebutton.ToggleButton;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static com.emmahc.smartchair.activities.HomeActivity.mContextMainActivity;
public class Tab4Fragment extends Fragment{
    //history_api
    private Send_to_Server_measure_history history_api = new Send_to_Server_measure_history(mContextMainActivity);
    private MultiStateToggleButton day_list;
    private MultiStateToggleButton health_list;
    private history_adapter history_adapter;
    private ArrayList<result_history_api_dto> history_data = new ArrayList<result_history_api_dto>();
    private LinearLayoutManager lm;
    private RecyclerView history_recyclerview;
    public int day_standard = 0;
    public int health_standard = 0;
    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState ) {
        final View view = inflater.inflate( R.layout.history_fragment, container, false );
        day_list = (MultiStateToggleButton)view.findViewById(R.id.Day_list);
                //CharSequence[] texts = new CharSequence[]{"abc", "def"};
        String[] day_list_label = new String[]{"ALL","Day","Week","Month","Year"};
        day_list.setElements(day_list_label);
        day_list.setValue(0);
        health_list = view.findViewById(R.id.health_list);
        String[] health_list_label = new String[]{"ALL","심박수","스트레스","혈압"};
        health_list.setElements(health_list_label);
        health_list.setValue(0);

        history_recyclerview = view.findViewById(R.id.history_recyclerview);

        String raw_history_data = SharedPreferenceManager.getString_from_SharedPreference(mContextMainActivity, "history_api").replace("[","").replace("]","");
        for(String data : raw_history_data.split("\\},")){
            data = data + "}";
            result_history_api_dto dummy_dto = new result_history_api_dto();
            JSONObject json = null;
            try {
                //json형태의 string을 json으로 변경
                json = new JSONObject(data);
                Gson gson = new Gson();
                dummy_dto = gson.fromJson(String.valueOf(json), result_history_api_dto.class);
                history_data.add(dummy_dto);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        history_adapter = new history_adapter(mContextMainActivity, history_data,day_standard, health_standard);
        history_recyclerview.setAdapter(history_adapter);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        day_list.setOnValueChangedListener(new ToggleButton.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                day_standard = value;
                history_adapter = new history_adapter(mContextMainActivity, changeDataPerDate(value, history_data),day_standard, health_standard);
                history_recyclerview.setAdapter(history_adapter);
            }
        });
        health_list.setOnValueChangedListener(new ToggleButton.OnValueChangedListener(){
            @Override
            public void onValueChanged(int value) {
                health_standard = value;
                history_adapter = new history_adapter(mContextMainActivity, history_data,day_standard, health_standard);
                history_recyclerview.setAdapter(history_adapter);
            }
        });
        lm = new LinearLayoutManager(mContextMainActivity);
        history_recyclerview.setLayoutManager(lm);
    }
    //날짜별로 리스트에 담을 데이터 다르게 할 함수
    private ArrayList<result_history_api_dto> changeDataPerDate(int value,ArrayList<result_history_api_dto> data){
        //unix to time
        Send_to_Server_measure_history send_api = new Send_to_Server_measure_history(mContextMainActivity);
        ArrayList<result_history_api_dto> dummy_data = new ArrayList<result_history_api_dto>();
        Calendar cal = Calendar.getInstance();
        //년도
        int year = cal.get(Calendar.YEAR);
        //월
        int month = cal.get(Calendar.MONTH)+1;
        //주
//        int week = cal.get(Calendar.);
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.setMinimalDaysInFirstWeek(7);
        int week = cal.get(Calendar.WEEK_OF_MONTH);

        //날
        int day = cal.get(Calendar.DATE);
        //2019 / 01 / 13 / 16:15:48
        //all
        switch (value){
            //all
            case 0:
                return data;
            //일간
            case 1:
                for (result_history_api_dto row_data : data){
                    String row_data_time = send_api.change_Unix_to_time(String.valueOf(row_data.getRecordedTime()));
                    String day_data = row_data_time.substring(8,10);
                    String year_data = row_data_time.substring(0,4);
                    String month_data = row_data_time.substring(5,7);
                    if(year == Integer.parseInt(year_data)){
                        if (month == Integer.parseInt(month_data)){
                            if(day == Integer.parseInt(day_data)){
                                dummy_data.add(row_data);
                            }

                        }
                    }
                }
                return dummy_data;
            //주간
            case 2:
                for (result_history_api_dto row_data : data){
                    String row_data_time = send_api.change_Unix_to_time(String.valueOf(row_data.getRecordedTime()));
                    String row_day_data = row_data_time.substring(8,10);
                    String year_data = row_data_time.substring(0,4);
                    String month_data = row_data_time.substring(5,7);
                    int day_data = Integer.parseInt(row_day_data);
                    //년도와 월이 동일하다면.
                    if(year == Integer.parseInt(year_data)){
                        if(month == Integer.parseInt(month_data)){
                            if(day == day_data){
                                dummy_data.add(row_data);
                            }
                            if(day > 7){
                                if(day-7 < day_data && day_data < day){
                                    dummy_data.add(row_data);
                                }
                            }else if(day < 7){
                                if(25 <= day_data && day_data <= 31){
                                    dummy_data.add(row_data);
                                }
                            }

                        }
                    }
                }
                return dummy_data;
            //월간
            case 3:
                for (result_history_api_dto row_data : data){
                    String row_data_time = send_api.change_Unix_to_time(String.valueOf(row_data.getRecordedTime()));
                    String year_data = row_data_time.substring(0,4);
                    String month_data = row_data_time.substring(5,7);
                    if (year == Integer.parseInt(year_data)){
                        if(month == Integer.parseInt(month_data)){
                            dummy_data.add(row_data);
                        }
                    }
                }
                return dummy_data;
            //년간
            case 4:
                for (result_history_api_dto row_data : data){
                    String row_data_time = send_api.change_Unix_to_time(String.valueOf(row_data.getRecordedTime()));
                    String year_data = row_data_time.substring(0,4);
                    if(year == Integer.parseInt(year_data)){
                        dummy_data.add(row_data);
                    }
                }
                return dummy_data;
        }
        return dummy_data;
    }
}

