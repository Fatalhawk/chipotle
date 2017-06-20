package com.sensei.companion.connection;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class ConnectService extends Service {

    private final IBinder connectBinder = new MyLocalBinder ();

    public ConnectService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return connectBinder;
    }

    public class MyLocalBinder extends Binder {
        ConnectService getService() {
            return ConnectService.this;
        }
    }
}
