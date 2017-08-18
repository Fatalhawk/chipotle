package com.sensei.companion.display;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.sensei.companion.R;
import com.sensei.companion.connection.ConnectManager;

public class PcManager extends AppCompatActivity {

    private PopupWindow pcSelectionPopup;
    private FrameLayout pcManagerFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing PopupWindow
        View popUpView = getLayoutInflater().inflate(R.layout.popup_pc_selection, (ViewGroup) findViewById(R.id.frame_popup_pc_selection));
        pcSelectionPopup = new PopupWindow(popUpView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
        pcSelectionPopup.setAnimationStyle(android.R.style.Animation_Dialog);
        pcSelectionPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                pcManagerFrame.getForeground().setAlpha (0);
                ((TextView)getPopupWindow().getContentView().findViewById(R.id.textview_search_status)).setText("Searching for PC...");
                (getPopupWindow().getContentView().findViewById(R.id.button_connect_pc)).setOnClickListener(null);
            }
        });

        //setting content view of pc manager activity and setting opacity of foreground to 0%
        setContentView(R.layout.activity_pc_manager);
        pcManagerFrame = (FrameLayout)findViewById(R.id.frame_activity_pc_manager);
        pcManagerFrame.getForeground().setAlpha(0);

        //Displaying the wifi network that the phone is currently connected to
        WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        String wifiName = wifiInfo.getSSID();
        wifiName = wifiName.substring (1, wifiName.length()-1);
        TextView wifiText = (TextView) findViewById(R.id.textview_wifi);
        wifiText.setText ("    Wifi: " + wifiName);
    }

    public PopupWindow getPopupWindow () {
        return pcSelectionPopup;
    }

    public void searchForPC (View v) {
        //open popup
        pcSelectionPopup.showAtLocation(findViewById(R.id.layout_pc_manager), Gravity.CENTER, 0, 0);
        pcSelectionPopup.update(0, 25, 750, 1000);
        pcManagerFrame.getForeground().setAlpha(150);

        //init search for a valid connection
        initPcSearch();
    }

    private void initPcSearch() {
        Log.d (AppLauncher.DEBUG_TAG, "Starting search...");
        ConnectManager mConnectManager = new ConnectManager();
        mConnectManager.initConnection (this, this);
    }
}
