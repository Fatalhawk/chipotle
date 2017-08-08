package com.sensei.companion.display;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.sensei.companion.R;
import com.sensei.companion.connection.ConnectManager;

public class PcManager extends AppCompatActivity {

    private final String DEBUG_TAG = "appMonitor";
    private PopupWindow searchWindow;
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //containerLayout = new LinearLayout(this);
        //containerLayout.setOrientation(LinearLayout.VERTICAL);

        //for popup
        View popUpView = getLayoutInflater().inflate(R.layout.activity_pc_manager, (ViewGroup) findViewById(R.id.pc_manager_layout));
        searchWindow = new PopupWindow(popUpView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
        searchWindow.setAnimationStyle(android.R.style.Animation_Dialog);

        searchWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                frameLayout.getForeground().setAlpha (0);
                ((TextView)getPopupWindow().getContentView().findViewById(R.id.searchStatus)).setText("Searching for PC...");
                (getPopupWindow().getContentView().findViewById(R.id.connectButton)).setOnClickListener(null);
            }
        });

        setContentView(R.layout.activity_pc_selection);

        frameLayout = (FrameLayout)findViewById(R.id.pc_selection_frame);
        frameLayout.getForeground().setAlpha(0);

        WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        String wifiName = wifiInfo.getSSID();
        wifiName = wifiName.substring (1, wifiName.length()-1);
        TextView wifiText = (TextView) findViewById(R.id.wifi_textview);
        wifiText.setText ("    Wifi: " + wifiName);
    }

    public PopupWindow getPopupWindow () {
        return searchWindow;
    }


    public void searchForPC (View v) {
        //open popup
        searchWindow.showAtLocation(findViewById(R.id.pc_selection_layout), Gravity.CENTER, 0, 0);
        searchWindow.update(0, 25, 750, 1000);
        frameLayout.getForeground().setAlpha(150);

        //init search for a valid connection
        connect ();
    }

    private void connect () {
        Log.d (DEBUG_TAG, "Starting search...");
        ConnectManager mConnectManager;
        mConnectManager = new ConnectManager();
        mConnectManager.initConnection (this, this);
    }
}
