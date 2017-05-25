package com.example.jaya.bluetoothwear;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "IPMCWearATTENTION";
    private static final boolean D = true;
    private static Intent intent = null;

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static String address = "20:16:10:32:18:75";

    private BluetoothAdapter btMyAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream myOutStream = null;

    Button btnSendMsg;
    TextView tvShowMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intent = new Intent(MainActivity.this, ReadBluetoothService.class );

        btnSendMsg = (Button) findViewById(R.id.btnSendMsg);

        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message;
                byte[] msgBuffer;
                try {
                    myOutStream = btSocket.getOutputStream();
                } catch (IOException e) {
                    Log.e(TAG, "ON CREATE: Output stream creation failed.", e);
                }

                message = "IPMC";
                msgBuffer = message.getBytes();

                try {
                    myOutStream.write(msgBuffer);
                } catch (IOException e) {
                    Log.e(TAG, "ON CREATE: Exception during write.", e);
                }

            }
        });

        tvShowMsg = (TextView) findViewById(R.id.tvShowMsg);

        if (D) {
            Log.e(TAG, "+++ ON CREATE +++");
        }

        btMyAdapter = BluetoothAdapter.getDefaultAdapter();

        if (btMyAdapter == null) {
            Toast.makeText(this, "蓝牙设备不可用，请打开蓝牙！", Toast.LENGTH_LONG).show();
            finish();
            return;
        }


        if (!btMyAdapter.isEnabled()) {
            Toast.makeText(this,  "请打开蓝牙并重新运行程序！", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (D) {
            Log.e(TAG, "+++ DONE IN ON CREATE, GOT LOCAL BT ADAPTER +++");
        }
    }

    @Override
    public void onStart() {

        super.onStart();

        if (D) {
            Log.e(TAG, "+++ ON START +++");
        }
    }

    @Override
    public void onResume() {

        super.onResume();

        if (D) {
            Log.e(TAG, "+++ ON RESUME +++");
            Log.e(TAG, "+++ ABOUT TO ATTEMPT CLIENT CONNECT +++");
        }

        startService(intent);

        DisplayToast("正在尝试连接设备，请稍后···");
        BluetoothDevice device = btMyAdapter.getRemoteDevice(address);

        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            DisplayToast("套接字创建失败！");
        }

        DisplayToast("成功连接设备！");
        btMyAdapter.cancelDiscovery();

        try {
            btSocket.connect();
            DisplayToast("成功建立通道！");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                DisplayToast("连接没有建立，无法关闭套接字！");
            }
        }

        // Create a data stream so we can talk to server.

    }

    @Override
    public void onPause() {

        super.onPause();

        if (D) {
            Log.e(TAG, "+++ ON PAUSE +++");
        }

        if (myOutStream != null) {
            try {
                myOutStream.flush();
            } catch (IOException e) {
                Log.e(TAG, "ON PAUSE: Couldn't flush output stream.", e);
            }
        }

        try {
            btSocket.close();
        } catch (IOException e2) {
            DisplayToast("套接字关闭失败！");
        }
    }

    @Override
    public void onStop() {

        super.onStop();

        if (D) {
            Log.e(TAG, "-- ON STOP --");
        }
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        if (D) {
            Log.e(TAG, "--- ON DESTROY ---");
        }

        stopService(intent);

    }

    public void DisplayToast(String str) {
        Toast toast=Toast.makeText(this, str, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 220);
        toast.show();
    }

}
