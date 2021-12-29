package com.emmahc.smartchair.activities;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.emmahc.smartchair.R;
import com.emmahc.smartchair.dto.SharedData;

import static com.emmahc.smartchair.activities.MainActivity.mContextMainActivity;

public class UserInfoActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        final SharedData data = mContextMainActivity.getBinder().getData();

        Button btn_fragment3_finish = findViewById(R.id.btn_fragment3_finish);
        final EditText edit_fragment3_age = findViewById(R.id.edit_fragment3_age);
        final EditText edit_fragment3_weight = findViewById(R.id.edit_fragment3_weight);
        final EditText edit_fragment3_height = findViewById(R.id.edit_fragment3_height);
        final RadioGroup radio_group_gender = findViewById(R.id.radio_group_gender);
        edit_fragment3_age.setText(String.format("%s", data.getAge()));
        edit_fragment3_weight.setText(String.format("%d", data.getWeight()));
        edit_fragment3_height.setText(String.format("%d", data.getHeight()));

        switch(data.getGender()) {
            case 0:
                radio_group_gender.check(R.id.radio_group_gender_male);
                break;
            case 1:
                radio_group_gender.check(R.id.radio_group_gender_female);
                break;
        }

        btn_fragment3_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    data.setAge(Integer.parseInt(edit_fragment3_age.getText().toString()));
                } catch(NumberFormatException nfe) {
                    System.out.println("Could not parse age" + nfe);
                }

                try {
                    data.setWeight(Integer.parseInt(edit_fragment3_weight.getText().toString()));
                } catch(NumberFormatException nfe) {
                    System.out.println("Could not parse weight" + nfe);
                }

                try {
                    data.setHeight(Integer.parseInt(edit_fragment3_height.getText().toString()));
                } catch(NumberFormatException nfe) {
                    System.out.println("Could not parse weight" + nfe);
                }

                int checked_gender = radio_group_gender.getCheckedRadioButtonId();

                switch(checked_gender) {
                    case R.id.radio_group_gender_male:
                        data.setGender(0);
                        break;
                    case R.id.radio_group_gender_female:
                        data.setGender(1);
                        break;
                }
                Toast.makeText(UserInfoActivity.this, R.string.user_info, Toast.LENGTH_SHORT).show();
                data.setDataNow(UserInfoActivity.this);
                finish();
            }
        });
    }
}
