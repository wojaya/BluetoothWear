package com.example.jaya.bluetoothwear;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
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

    private Handler mUiHandler = new Handler();
    private MyWorkerThread mWorkThread;

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

        mWorkThread = new MyWorkerThread("myWorkerThread");
        mWorkThread.start();
        mWorkThread.prepareHandler();

        Runnable initialBtTask = new Runnable() {
            @Override
            public void run() {

                if (!btMyAdapter.isEnabled()) {
                    mUiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            btnConnBt.setEnabled(false);
                            btnCloseBt.setEnabled(false);
                        }
                    });
                } else {
                    mUiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            btnOpenBt.setEnabled(false);
                            btnConnBt.setEnabled(true);
                        }
                    });
                }
            }
        };
        mWorkThread.postTask(initialBtTask);
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

        // TODO: 2017/5/27 service与activity通讯
/*
        IntentFilter filter = new IntentFilter(ReadBluetoothService.ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(testReceiver, filter);
*/
    }

    @Override
    public void onPause() {

        super.onPause();

        // TODO: 2017/5/27 service与activity通讯
/*
        LocalBroadcastManager.getInstance(this).unregisterReceiver(testReceiver);
*/

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

        mWorkThread.quit();
        stopService(intent);
    }

    private void findViewById () {
        btnOpenBt = (Button) findViewById(R.id.btnOpenBt);
        btnCloseBt = (Button) findViewById(R.id.btnCloseBt);
        btnConnBt = (Button) findViewById(R.id.btnConnBt);
    }

    /* 开启蓝牙 */
    public void onEnableButtonClicked (View view) {
        Runnable openBtTask = new Runnable() {
            @Override
            public void run() {
                btMyAdapter.enable();
                mUiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        btnOpenBt.setEnabled(false);
                        btnConnBt.setEnabled(true);
                        btnCloseBt.setEnabled(true);
                    }
                });
            }
        };
        mWorkThread.postTask(openBtTask);
    }


    /* 关闭蓝牙 */
    public void onDisableButtonClicked (View view) {
        Runnable closeBtTask = new Runnable() {
            @Override
            public void run() {
                btMyAdapter.disable();
                mUiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        btnOpenBt.setEnabled(true);
                        btnConnBt.setEnabled(false);
                        btnCloseBt.setEnabled(false);
                    }
                });
            }
        };
        mWorkThread.postTask(closeBtTask);
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

    public class MyWorkerThread extends HandlerThread {
        private Handler mWorkerHandler;

        public MyWorkerThread(String name) {
            super(name);
        }

        public void prepareHandler() {
            mWorkerHandler = new Handler(getLooper());
        }

        public void postTask(Runnable task) {
            mWorkerHandler.post(task);
        }
    }

    // TODO: 2017/5/27 service与activity通讯
/*
    private BroadcastReceiver testReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive (Context context, Intent intent) {
            String result = intent.getStringExtra("result");
            Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
        }
    };
*/

}
