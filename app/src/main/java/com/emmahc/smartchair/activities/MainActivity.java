package com.emmahc.smartchair.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import com.emmahc.smartchair.ServerAPI.Send_to_Server_measure_history;
import com.emmahc.smartchair.common.SharedPreferenceManager;
import com.emmahc.smartchair.dto.History_API_Dto;
import com.emmahc.smartchair.fragments.ResultFragment;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.emmahc.smartchair.BLEModule.BlueToothBinder;
import com.emmahc.smartchair.BackButtonExitHandler;
import com.emmahc.smartchair.R;
import com.emmahc.smartchair.fragments.Tab3Fragment;
import com.emmahc.smartchair.fragments.Tab4Fragment;
import com.emmahc.smartchair.fragments.Tab5Fragment;
import com.emmahc.smartchair.fragments.ChairFragment;
import com.emmahc.smartchair.listener.OnFragmentBackPressListener;
import com.navdrawer.SimpleSideDrawer;

import java.util.ArrayList;
import java.util.zip.Inflater;

import static com.emmahc.smartchair.GlobalDefines.DeviceInformation.EXTRAS_DEVICE_ADDRESS;


/**
 * MainActivity including 'Measure' Tab and 'Result' Tab.
 * Measure tab includes start button for connecting with BLE device to get bio-medical signal
 * information from hardware.
 * Result tab includes Heart rate, Blood pressure, and Stress result, also few control buttons.
 */


public class MainActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    public static MainActivity mContextMainActivity;
    private BackButtonExitHandler backButtonExitHandler;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ArrayList<Fragment> fragments;
    private SimpleSideDrawer mSlidingMenu;
    private BlueToothBinder binder;
    private OnFragmentBackPressListener fragmentBackPressListener;

    private Send_to_Server_measure_history request_history;

    private ProgressDialog calculatingDialog;
    private CircularProgressView loading_layout;

    private FrameLayout total_loading_layout;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        setContentView(R.layout.activity_main);

        //bluetooth Information
        final Intent intent = getIntent();
        binder = new BlueToothBinder(this,intent.getStringExtra(EXTRAS_DEVICE_ADDRESS));
        request_history = new Send_to_Server_measure_history(this);
        // Context setting for MainActivity
        mContextMainActivity = this;
        fragments = new ArrayList<>();

        loading_layout = (CircularProgressView) findViewById(R.id.loading_Ble_view);
        loading_layout.startAnimation();
        total_loading_layout = findViewById(R.id.bluetooth_loading_layout);
        initView();
    }

    private void initView() {
        // Handler for back button exit
        backButtonExitHandler = new BackButtonExitHandler(this);

        // Initializing the TabLayout
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ib_ab_menu_style));
//        tabLayout.addTab(tabLayout.newTab().setText("측정"));
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#6cd9ff"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ib_ab_stats_style));
        //TODO 이미지 나인패치필요
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ib_ab_help_style));
        tabLayout.setSelectedTabIndicatorHeight((int) (100 * getResources().getDisplayMetrics().density));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Initializing ViewPager
        viewPager = (ViewPager) findViewById(R.id.pager);

        // Creating TabPagerAdapter adapter
        TabPagerAdapter pagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setBackgroundColor(0xffe2e2e2);


        // Set TabSelectedListener
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        // Set Side drawer for setting
        mSlidingMenu = new SimpleSideDrawer(this);
        mSlidingMenu.setLeftBehindContentView(R.layout.behind_menu_left);
        mSlidingMenu.setMinimumWidth(200);
    }

    public BlueToothBinder getBinder() {
        return binder;
    }

    public void setFragmentBackPressListener(OnFragmentBackPressListener fragmentBackPressListener) {
        this.fragmentBackPressListener = fragmentBackPressListener;
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
        if (!mSlidingMenu.isClosed())
            mSlidingMenu.closeLeftSide();
        else if(fragmentBackPressListener != null)
            fragmentBackPressListener.onBack();
        else
            backButtonExitHandler.onBackPressed();

    }

    public class TabPagerAdapter extends FragmentStatePagerAdapter {
        private int tabCount;

        public TabPagerAdapter(FragmentManager fm, int tabCount) {
            super(fm);
            this.tabCount = tabCount;
        }

        @Override
        public Fragment getItem(int position) {
            //기존에 프래그먼트를 add를 해놔서 위로 올라가는 것임
            switch (position) {
                case 0:
                    Tab5Fragment tab5Fragment = new Tab5Fragment();
                    fragments.add(tab5Fragment);
                    return tab5Fragment;
                case 1:
                    Tab4Fragment tab4Fragment = new Tab4Fragment();
                    fragments.add(tab4Fragment);
                    return tab4Fragment;

                case 2:
                    Tab3Fragment tab3Fragment = new Tab3Fragment();
                    fragments.add(tab3Fragment);
                    return tab3Fragment;

                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return tabCount;
        }
    }

    public Fragment getFragment() {
        return fragments.get(viewPager.getCurrentItem());
    }

    //측정 시작 - sel : 0
    public final void action(int sel, Fragment fragment) {
        if(fragment instanceof Tab5Fragment) {
            switch (sel){
                case 0:
//                    getSupportFragmentManager().beginTransaction().replace()
//                    tabLayout.setVisibility(View.GONE);
//                    //기존에는 Tab5Fragmnet의 fragmentContent 프레임아웃 chairFragment를 붙히는 형식으로 돼서
//                    //심박수 등 차트 관련 화면이 작게 나왔었는데 이 화면 전체의 프레임아웃인 Tab5_totalFrameOut을 바꾸게 설정
//
//                    fragment.getView().findViewById(R.id.fragmentContent).setClickable(true);
//                    getSupportFragmentManager().beginTransaction().replace(R.id.Tab5_totalFrameOut, ChairFragment.newInstance()).commitAllowingStateLoss();

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
    public Send_to_Server_measure_history request_Data_API() {
        return request_history;
    }
    public ProgressDialog getCalculatingDialog() {
        return calculatingDialog;
    }
    public CircularProgressView getLoading_layout() {
        return loading_layout;
    }
    public FrameLayout getTotal_loading_layoutLoading_layout() {
        return total_loading_layout;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void Loading(String action){
        FrameLayout bluetooth_loading_layout = findViewById(R.id.bluetooth_loading_layout);
        CircularProgressView progressView = (CircularProgressView) findViewById(R.id.loading_Ble_view);
        if(action == "stop"){
            bluetooth_loading_layout.setVisibility(View.INVISIBLE);
            progressView.stopAnimation();
        }

    }

}
