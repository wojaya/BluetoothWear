package com.example.jaya.bluetoothwear;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ReadBluetoothService extends Service {
    public ReadBluetoothService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
//        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("ReadBtService is Created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        System.out.println("ReadBtService is Started");
        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        System.out.println("Service is Destroyed");
    }

}
