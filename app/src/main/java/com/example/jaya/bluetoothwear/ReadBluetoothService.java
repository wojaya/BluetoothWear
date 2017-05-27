package com.example.jaya.bluetoothwear;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class ReadBluetoothService extends Service {

    private static final String TAG = "IPMC<<<<<<<<<<";
    private BluetoothAdapter myBluetoothAdapter = null;
    public BluetoothSocket btSocket = null;
    public OutputStream myOutStream = null;
    public InputStream myInStream = null;

//    In Method beginListenForData();
    boolean stopWorkerTOF = false;
    byte delimiter = 10;
    int readBufferPosition = 0;
    byte[] readBuffer = new byte[1024];
    Handler handler = new Handler();

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static String address = "20:16:10:32:18:75";

    public ReadBluetoothService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
//        return new MyBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("TAG", "ReadBluetoothService is Created");

        CheckBt();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.e("TAG", "ReadBluetoothService is Started");

        Connect();

        writeData("IPMC");
        DisplayToast("已发送字符串：IPMC");

        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.e("TAG", "ReadBluetoothService is Destroyed");

        try {
            btSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Unable to end the bluetoothConnection");
        }
    }


//    public class MyBinder extends Binder {  }


    private void CheckBt() {
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!myBluetoothAdapter.isEnabled()) {
            DisplayToast("Bluetooth Disabled !");
        }

        if (myBluetoothAdapter == null) {
            DisplayToast("Bluetooth null!");
        }
    }


    public void Connect() {

        BluetoothDevice device = myBluetoothAdapter.getRemoteDevice(address);

        Log.e(TAG, "Connecting to ... " + device);

        myBluetoothAdapter.cancelDiscovery();

        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            btSocket.connect();
            Log.e(TAG, "Connection made.");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                Log.e(TAG, "Unable to end the connection");
            }
            Log.e(TAG, "Socket creation failed");
        }

        beginListenForData();
    }

    private void writeData (String data) {
        try {
            myOutStream = btSocket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "Bug BEFORE Sending stuff", e);
        }

        String message = data;
        byte[] msgBuffer = message.getBytes();

        try {
            myOutStream.write(msgBuffer);
        } catch (IOException e) {
            Log.e(TAG, "Bug while sending stuff", e);
        }
    }

    public void beginListenForData() {
        try {
            myInStream = btSocket.getInputStream();
        } catch (IOException e) {
        }

        Thread workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopWorkerTOF)
                {
                    try
                    {
                        int bytesAvailable = myInStream.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            myInStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter)
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;
                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {

                                            DisplayToast("收到蓝牙信息：" + data);

	                                        	/* You also can use Result.setText(data); it won't display multilines
	                                        	*/

                                        }
                                    });
                                }
                                else
                                {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        stopWorkerTOF = true;
                    }
                }
            }
        });

        workerThread.start();

        Log.e("TAG", "InStream is started");
    }

    public void DisplayToast (String str) {
        Toast toast=Toast.makeText(this, str, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 220);
        toast.show();
    }

}
