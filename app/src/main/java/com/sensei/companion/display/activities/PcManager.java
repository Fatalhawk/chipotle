package com.sensei.companion.display.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.sensei.companion.R;
import com.sensei.companion.communication.connection.ConnectManager;
import com.viewpagerindicator.CirclePageIndicator;

public class PcManager extends AppCompatActivity {

    private PopupWindow pcSelectionPopup;
    private static int popupWidth;
    private static int popupHeight;
    private ConstraintLayout pcManagerFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setting content view of pc manager activity and setting opacity of foreground to 0%
        setContentView(R.layout.activity_pc_manager);

        pcManagerFrame = (ConstraintLayout) findViewById(R.id.layout_pc_manager);
        if (Build.VERSION.SDK_INT >= 23) {
            pcManagerFrame.getForeground().setAlpha(0);
        } else {
            Log.i (AppLauncher.DEBUG_TAG, "[PcManager] could not set foreground alpha due to api version");
        }

        //Displaying the wifi network that the phone is currently connected to
        WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        String wifiName = wifiInfo.getSSID();
        wifiName = wifiName.substring (1, wifiName.length()-1);
        final TextView wifiText = (TextView) findViewById(R.id.textview_wifi);
        wifiText.setText ("Wifi: " + wifiName);

        //set width of textview
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int widthScreen = size.x;
        int heightScreen = size.y;
        LayoutParams paramsTxt = wifiText.getLayoutParams();
        paramsTxt.width= (int)(widthScreen*0.75);
        wifiText.setLayoutParams(paramsTxt);

        //set height of monitor icon
        final ImageView monitor = (ImageView)findViewById(R.id.icon_desktop);
        ViewTreeObserver vto = wifiText.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                LayoutParams paramsImg = monitor.getLayoutParams();
                paramsImg.height=wifiText.getMeasuredHeight();
                monitor.setLayoutParams(paramsImg);
                ViewTreeObserver obs = wifiText.getViewTreeObserver();
                obs.removeOnGlobalLayoutListener(this);
            }
        });

        popupWidth = (int)(widthScreen*0.65);
        popupHeight = (int)(heightScreen*0.50);

        //initializing connect PopupWindow
        View connectPopUpView = getLayoutInflater().inflate(R.layout.popup_pc_selection_border, (ViewGroup) findViewById(R.id.layout_popup_pc_selection));
        pcSelectionPopup = new PopupWindow(connectPopUpView, popupWidth, popupHeight);
        pcSelectionPopup.setAnimationStyle(android.R.style.Animation_Dialog);
        pcSelectionPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                pcSelectionPopup.setOutsideTouchable(false);
                pcSelectionPopup.getContentView().findViewById(R.id.button_connect_pc).setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT >= 23) {
                    pcManagerFrame.getForeground().setAlpha(0);
                } else {
                    Log.i (AppLauncher.DEBUG_TAG, "[PcManager] could not set foreground alpha due to api version");
                }
                ((TextView) getConnectPopupWindow().getContentView().findViewById(R.id.textview_search_status)).setText("Searching for PC...");
                (getConnectPopupWindow().getContentView().findViewById(R.id.button_connect_pc)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismissPopup(v);
                    }
                });
                findViewById(R.id.icon_instructions).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showInstructions(v);
                    }
                });
            }
        });
        pcSelectionPopup.setOutsideTouchable(false);
    }

    public PopupWindow getConnectPopupWindow() {
        return pcSelectionPopup;
    }

    public void dismissPopup (View v) {
        pcSelectionPopup.dismiss();
    }

    public void searchForPC (View v) {
        //disable instructions button
        ImageButton button = (ImageButton)findViewById(R.id.icon_instructions);
        button.setOnClickListener(null);

        //open popup
        int yOffSetPixels = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        pcSelectionPopup.showAtLocation(findViewById(R.id.layout_pc_manager), Gravity.CENTER, 0, -1*yOffSetPixels);
        pcSelectionPopup.update();
        if (Build.VERSION.SDK_INT >= 23) {
            pcManagerFrame.getForeground().setAlpha(150);
        } else {
            Log.i (AppLauncher.DEBUG_TAG, "[PcManager] could not set foreground alpha due to api version");
        }

        //init search for a valid connection
        initPcSearch();
    }

    public void showInstructions (View v) {
        if (Build.VERSION.SDK_INT >= 23) {
            pcManagerFrame.getForeground().setAlpha(150);
        } else {
            Log.i (AppLauncher.DEBUG_TAG, "[PcManager] could not set foreground alpha due to api version");
        }
        new DialogFragmentWindow().show(getSupportFragmentManager(), "");
    }

    private void initPcSearch() {
        Log.d (AppLauncher.DEBUG_TAG, "Starting search...");
        ConnectManager mConnectManager = new ConnectManager();
        mConnectManager.initConnection (this, this);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////// Popup classes ////////////////////////////////////////////

    public static class DialogFragmentWindow extends DialogFragment {
        @Override
        public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.popup_instructions, container);
            ViewPager instructionsViewPager = (ViewPager) view.findViewById(R.id.pager_instructions);
            InstructionsPagerAdapter pagerAdapter = new InstructionsPagerAdapter(getChildFragmentManager());
            instructionsViewPager.setAdapter(pagerAdapter);
            CirclePageIndicator circlePageIndicator = (CirclePageIndicator)view.findViewById(R.id.circle_page_indicator);
            circlePageIndicator.setViewPager(instructionsViewPager);
            return view;
        }

        @Override
        public void onStart() {
            Window window = getDialog().getWindow();
            //change size
            if (window != null) {
                window.setLayout(popupWidth, popupHeight);
                //make background transparent to get rid of shadow
                window.setBackgroundDrawableResource(android.R.color.transparent);
                //get rid of shadow and dim
                WindowManager.LayoutParams windowParams = window.getAttributes();
                windowParams.dimAmount = 0;
                windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                //set animation
                windowParams.windowAnimations = android.R.style.Animation_Dialog;
                window.setAttributes(windowParams);
            }
            super.onStart();
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ConstraintLayout pcFrame = (ConstraintLayout)getActivity().findViewById(R.id.layout_pc_manager);
            if (Build.VERSION.SDK_INT >= 23) {
                pcFrame.getForeground().setAlpha(0);
            } else {
                Log.i (AppLauncher.DEBUG_TAG, "[PcManager] could not set foreground alpha due to api version");
            }
            super.onDismiss(dialog);
        }
    }

    private static class InstructionsPagerAdapter extends FragmentPagerAdapter {
        private final int NUMBER_PAGER_ITEMS = 3;

        InstructionsPagerAdapter(FragmentManager fm) {
            super (fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new Instructions1();
                case 1:
                    return new Instructions2();
                case 2:
                    return new Instructions3();
                default:
                    Log.d (AppLauncher.DEBUG_TAG, "[PcManager] Error - nonexistant fragment position");
                    return null;
            }
        }

        @Override
        public int getCount() {
            return NUMBER_PAGER_ITEMS;
        }
    }
}
