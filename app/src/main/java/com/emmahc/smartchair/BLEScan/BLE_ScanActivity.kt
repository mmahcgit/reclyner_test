package com.emmahc.smartchair.BLEScan

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emmahc.smartchair.GlobalDefines
import com.emmahc.smartchair.R
import com.emmahc.smartchair.activities.DeviceScanActivity
import com.emmahc.smartchair.activities.HomeActivity
import com.emmahc.smartchair.activities.MainActivity
import com.emmahc.smartchair.common.Prefer
import com.emmahc.smartchair.common.SharedPreferenceManager
import com.emmahc.smartchair.dto.BLEScan_dto
import java.util.*

class BLE_ScanActivity : AppCompatActivity() {
    val TAG = "BLE_ScanActivity"
    //검색 시간
    val BLE_SCAN_TIME = 10000L
    //request success
    val REQUEST_ENABLE_BT =200
    //검색 기기 리스트
    var ble_scan_list: ArrayList<BLEScan_dto>? = null
    //이름 없는 기기 리스트
    var ble_scan_unkown_list:ArrayList<BLEScan_dto>? = null

    //adapter
    private lateinit var adapter:BLEScanAdapter
    //unkown adapter
    private lateinit var unknowAdapter: BLEScanAdapter
    //layoutmanager
    private lateinit var lm: LinearLayoutManager
    //recyclerview
    private lateinit var ble_scan_recyclerview: RecyclerView

    //handler
    private lateinit var handler: Handler
    //블루투스 어댑터
    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }
    private val BluetoothAdapter.isDisabled: Boolean
        get() = !isEnabled

    //loading layout
    private lateinit var loading_layout: FrameLayout
    //검색 중인지
    private var isScanning:Boolean = false
    //unkownbutton
    private lateinit var unkownbutton: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_b_l_e__scan)

        //권한 확인
        checkLocationPermission()

        //list
        ble_scan_list = ArrayList()
        ble_scan_unkown_list = ArrayList()

        //리싸이클러뷰
        ble_scan_recyclerview = findViewById(R.id.blescan_recyclerview)
        //어댑터
        adapter = BLEScanAdapter(this, ble_scan_list!!){
            val device = HashMap<String, BluetoothDevice>()
            device[it.device_name!!] = it.device!!
            move_to_MainActivity(it.device_name, it.device_address!!)
        }
        lm = LinearLayoutManager(this)

        handler = Handler()

        loading_layout = findViewById(R.id.loading_layout)
        //로딩 화면 누르면 scan 중단
        loading_layout.setOnClickListener {
            scanLeDevice(false)
        }

        unkownbutton = findViewById(R.id.unkown_device_button)

        unknowAdapter = BLEScanAdapter(this, ble_scan_unkown_list!!){
            val device = HashMap<String, BluetoothDevice>()
            device[it.device_name!!] = it.device!!
            move_to_MainActivity(it.device_name, it.device_address!!)
        }
        bluetoothAdapter?.takeIf { it.isEnabled }?.apply {
            val bluetoothEnableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(bluetoothEnableIntent, REQUEST_ENABLE_BT)
        }
        var unkown_click = 0
        unkownbutton.setOnClickListener {
            if (unkown_click == 0){
                ble_scan_recyclerview.adapter = unknowAdapter
                unkown_click = 1
            }else{
                ble_scan_recyclerview.adapter = adapter
                unkown_click = 0
            }
        }
    }
    //p0 - addres

    private val leScanCallback = BluetoothAdapter.LeScanCallback { p0, p1, p2 ->
        runOnUiThread {
            if(!ble_scan_list?.contains(BLEScan_dto(p0.name, p0.address, p0))!!) {
                if(p0.name != null){
                    ble_scan_list?.add(BLEScan_dto(p0.name, p0.address, p0))
                }

            }
            if(!ble_scan_unkown_list?.contains(BLEScan_dto(p0.name, p0.address, p0))!!){
                if(p0.name == null){
                    ble_scan_unkown_list!!.add(BLEScan_dto("UnknownDevice",p0.address, p0))
                }
            }
            ble_scan_recyclerview.adapter = adapter
            ble_scan_recyclerview.layoutManager = lm
        }
    }
    private fun scanLeDevice(enable: Boolean) {
        Log.d(TAG, "enable value is ${enable}(in scnaLeDevice)")
        when (enable) {
            true -> {
                // Stops scanning after a pre-defined scan period.
                handler.postDelayed({
                    isScanning = false
                    bluetoothAdapter?.stopLeScan(leScanCallback)
                    loading_layout.visibility = View.GONE

                    Log.d(TAG, "stopLeScan (in handler of scnaLeDevice)")
                }, BLE_SCAN_TIME)
                isScanning = true
                bluetoothAdapter?.startLeScan(leScanCallback)
                Log.d(TAG, "startLeScan(in scnaLeDevice)")
            }
            else -> {
                isScanning = false
                bluetoothAdapter?.stopLeScan(leScanCallback)

                loading_layout.visibility = View.GONE
                Log.d(TAG, "stopLeScan(in scnaLeDevice)")
            }
        }
    }
    //위치 권한
    private fun checkLocationPermission(){
        //Array<(out) String!>
        val permission_list = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN)
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                AlertDialog.Builder(this)
                        .setTitle("위치 권한")
                        .setMessage("위치 권한 동의?")
                        .setPositiveButton("수락", DialogInterface.OnClickListener { dialogInterface, i ->
                            ActivityCompat.requestPermissions(this, permission_list, 200)
                        }).create().show()
            }else{
                ActivityCompat.requestPermissions(this, permission_list, 200)
            }

        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //성공적으로 요청이 들어온 경우
        if(requestCode == REQUEST_ENABLE_BT){
            if(resultCode == RESULT_OK){
//                Toast.makeText(this, "블루투스가 활성화되었습니다.",Toast.LENGTH_SHORT).show()
                Log.d(TAG,"resultCode is RESULT_OK(onActivityResult)")
                scanLeDevice(true)

            }else{
                Toast.makeText(this, "블루투스가 활성화에 실패했습니다..", Toast.LENGTH_SHORT).show()
                Log.d(TAG,"resultCode is not RESULT_OK(onActivityResult)")
            }
        }else{
            Toast.makeText(this, "requestCode is not REQUEST_ENABLE_BT(onActivityResult)", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "requestCode is not REQUEST_ENABLE_BT")

        }
    }
    private fun move_to_MainActivity(device_name:String, device_address:String){
        val intent = Intent(this, HomeActivity::class.java)
        SharedPreferenceManager.setData_to_SharedPrerence(this, "now_device_name",device_name, "String")
        SharedPreferenceManager.setData_to_SharedPrerence(this, "now_device_address",device_address, "String")
        intent.putExtra(GlobalDefines.DeviceInformation.EXTRAS_DEVICE_NAME, device_name)
        intent.putExtra(GlobalDefines.DeviceInformation.EXTRAS_DEVICE_ADDRESS, device_address)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }
}