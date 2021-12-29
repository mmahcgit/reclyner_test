package com.emmahc.smartchair.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.emmahc.smartchair.BLEModule.BlueToothBinder;
import com.emmahc.smartchair.R;
import com.emmahc.smartchair.ServerAPI.Send_to_Server_measure_history;
import com.emmahc.smartchair.fragments.ChairFragment;
import com.emmahc.smartchair.fragments.Tab3Fragment;
import com.emmahc.smartchair.fragments.Tab4Fragment;
import com.emmahc.smartchair.fragments.Tab5Fragment;
import com.emmahc.smartchair.ui.ZoomOutPageTransformer;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import static com.emmahc.smartchair.GlobalDefines.DeviceInformation.EXTRAS_DEVICE_ADDRESS;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, NavigationView.OnNavigationItemSelectedListener {
    private String TAG = "MainActivity";
    public static HomeActivity mContextMainActivity;
    private BlueToothBinder binder;

    private int PAGES = 3;
    private ViewPager2 viewPager;
    private AlertDialog dialog;
    private BottomNavigationView bottom_navi;
    public FrameLayout loading_frame;

    public Send_to_Server_measure_history history_api = new Send_to_Server_measure_history(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mContextMainActivity = this;
        ScreenSlidePagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(this);
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.registerOnPageChangeCallback(new PageChangeCallback());
        viewPager.setPageTransformer(new ZoomOutPageTransformer());

        final Intent intent = getIntent();
        binder = new BlueToothBinder(this,intent.getStringExtra(EXTRAS_DEVICE_ADDRESS));

        bottom_navi = findViewById(R.id.bottom_navi);
        bottom_navi.inflateMenu(R.menu.bottom_menu);
        bottom_navi.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(!ChairFragment.IsRunningChair){
                    switch(item.getItemId()){
                        case R.id.Main_item:
                            viewPager.setCurrentItem(0);
                            return true;
                        case R.id.History:
                            viewPager.setCurrentItem(1);
                            return true;
                        case R.id.Setting:
                            viewPager.setCurrentItem(2);
                            return true;
                    }
                }else{
                    Toast.makeText(mContextMainActivity, "현재는 페이지를 이동할 수 없습니다.",Toast.LENGTH_SHORT).show();
                    return false;
                }
                return true;
            }
        });
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        NavigationView drawer_navigation = findViewById(R.id.drawer_navigation);
        drawer_navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(!ChairFragment.IsRunningChair){
                    switch(item.getItemId()){
                        case R.id.Main_item:
                            viewPager.setCurrentItem(0);
                            return true;
                        case R.id.History:
                            viewPager.setCurrentItem(1);
                            return true;
                        case R.id.Setting:
                            viewPager.setCurrentItem(2);
                            return true;
                    }
                }else{
                    Toast.makeText(mContextMainActivity, "현재는 페이지를 이동할 수 없습니다.",Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
        loading_frame = findViewById(R.id.loading_frame);
        //1602665525
        history_api.request_History_Data(1602664483796L, System.currentTimeMillis(), "RAW");
    }
    public FrameLayout getLoading_layout(){
        return loading_frame;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(!ChairFragment.IsRunningChair){
            switch(item.getItemId()){
                case R.id.Main_item:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.History:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.Setting:
                    viewPager.setCurrentItem(2);
                    return true;
            }
        }else{
            Toast.makeText(mContextMainActivity, "현재는 페이지를 이동할 수 없습니다.",Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    private class PageChangeCallback extends ViewPager2.OnPageChangeCallback{
        @Override
        public void onPageSelected(int position){
            super.onPageSelected(position);
            int result;
            if(!ChairFragment.IsRunningChair){
                switch(position){
                    case 0:
                        result = R.id.Main_item;
                        break;
                    case 1:
                        result = R.id.History;
                        break;
                    case 2:
                        result = R.id.Setting;
                        break;
                    default:
                        result = R.id.Main_item;
                        break;
                }
                bottom_navi.setSelectedItemId(result);
            }else{
                Toast.makeText(mContextMainActivity, "현재는 페이지를 이동할 수 없습니다.",Toast.LENGTH_SHORT).show();
            }

        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {

        public ScreenSlidePagerAdapter(@NonNull FragmentActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Fragment dummy;
            switch(position){
                case 0:
                    dummy = new Tab5Fragment();
                    return dummy;
                case 1:
                    dummy = new Tab4Fragment();
                    return dummy;
                case 2:
                    dummy = new Tab3Fragment();
                    return dummy;
                default:
                    dummy = new Tab5Fragment();
                    return dummy;
            }
        }

        @Override
        public int getItemCount() {
            return PAGES;
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        binder.onResume();
        if(binder.isBindStatus())
            binder.getTHprocess().onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(binder.isBindStatus())
            binder.getTHprocess().onStop();
        binder.onPause();
    }
    @Override
    protected void onDestroy() {
        binder.unbindService();
        super.onDestroy();
    }
    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() != 0){
            viewPager.setCurrentItem(viewPager.getCurrentItem()-1);
        }
        //뒤로가기가 여기서 잡혀있었음.
        Log.d("hey",String.valueOf(ChairFragment.IsRunningChair));
        if(ChairFragment.IsRunningChair){
            getSupportFragmentManager().beginTransaction().replace(R.id.layout_control_chair_activity, new Tab5Fragment()).commitAllowingStateLoss();
            binder.getTHprocess().onStart();;
            binder.getActtion().stopMeasuring();
        }
    }
    public BlueToothBinder getBinder() {
        return binder;
    }
    public final void action(int sel, Fragment fragment) {
        if(fragment instanceof Tab5Fragment) {
            switch (sel){
                case 0:
                    //기존에는 Tab5Fragmnet의 fragmentContent 프레임아웃 chairFragment를 붙히는 형식으로 돼서
                    //심박수 등 차트 관련 화면이 작게 나왔었는데 이 화면 전체의 프레임아웃인 Tab5_totalFrameOut을 바꾸게 설정
                    getSupportFragmentManager().beginTransaction().replace(R.id.Tab5_totalFrameOut, ChairFragment.newInstance()).commitAllowingStateLoss();
                    Tab5Fragment tab5Fragment = new Tab5Fragment();
                    //측정 시작했을 때, 작동하는 기능들
                    //온도 습도 관련 프로세스 중단
                    binder.getTHprocess().onStop();
                    //측정 시작
                    binder.getActtion().measureStart();
                    break;
                case 1:binder.getActtion().setBack();
                    break;
                //다른건 지금 안쓰는데 case2 만 쓰는중 (온열)
                case 2:binder.getActtion().setHeap();
                    break;
                case 3:binder.getActtion().setBack_heap();
                    break;
                case 4: binder.getActtion().setMoveBack();
                    break;
                case 5: binder.getActtion().setMoveFoot();
                    break;
                case 6: binder.getActtion().setMoving();
                default:break;
            }
        }
    }

}
