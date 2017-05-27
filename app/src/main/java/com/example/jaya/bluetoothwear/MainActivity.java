package com.example.jaya.bluetoothwear;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "IPMC>>>>>>>>>>";
    private static final boolean D = true;
    private static Intent intent = null;

    Button btnOpenBt, btnCloseBt, btnConnBt;

    private BluetoothAdapter btMyAdapter = BluetoothAdapter.getDefaultAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById();

        if (D) {
            Log.e(TAG, "+++ ON CREATE +++");
        }

        if (!btMyAdapter.isEnabled()) {
            btnConnBt.setEnabled(false);
            btnCloseBt.setEnabled(false);
        } else {
            btnOpenBt.setEnabled(false);
            btnConnBt.setEnabled(true);
        }
    }

    @Override
    public void onStart() {

        super.onStart();

        if (D) {
            Log.e(TAG, "++++++ ON START ++++++");
        }

    }

    @Override
    public void onResume() {

        super.onResume();

        if (D) {
            Log.e(TAG, "++++++ ON RESUME ++++++");
        }

    }

    @Override
    public void onPause() {

        super.onPause();

        if (D) {
            Log.e(TAG, "······· ON PAUSE ·······");
        }

    }

    @Override
    public void onStop() {

        super.onStop();

        if (D) {
            Log.e(TAG, "/////// ON STOP ///////");
        }
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        if (D) {
            Log.e(TAG, "====== ON DESTROY ======");
        }

        stopService(intent);

    }

    private void findViewById () {
        btnOpenBt = (Button) findViewById(R.id.btnOpenBt);
        btnCloseBt = (Button) findViewById(R.id.btnCloseBt);
        btnConnBt = (Button) findViewById(R.id.btnConnBt);
    }

    /* 开启蓝牙 */
    public void onEnableButtonClicked (View view)
    {
        btMyAdapter.enable();

        btnOpenBt.setEnabled(false);
        btnConnBt.setEnabled(true);
        btnCloseBt.setEnabled(true);
    }


    /* 关闭蓝牙 */
    public void onDisableButtonClicked (View view)
    {
        btMyAdapter.disable();

        btnConnBt.setEnabled(false);
        btnOpenBt.setEnabled(true);
        btnCloseBt.setEnabled(false);
    }


    /* 配对 */
    public void onStartDiscoveryButtonClicked (View view)
    {
        intent = new Intent(MainActivity.this, ReadBluetoothService.class );
        startService(intent);
        btnConnBt.setEnabled(false);
    }

    public void DisplayToast (String str) {
        Toast toast=Toast.makeText(this, str, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 220);
        toast.show();
    }
}
