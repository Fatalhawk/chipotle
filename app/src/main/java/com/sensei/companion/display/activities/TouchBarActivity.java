package com.sensei.companion.display.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;

import com.sensei.companion.R;
import com.sensei.companion.communication.commands.CommandsData;
import com.sensei.companion.communication.connection.ConnectManager;
import com.sensei.companion.communication.connection.MessageHandler;
import com.sensei.companion.communication.messages.CommandMessage;
import com.sensei.companion.display.screen_selector.ScreenSelectorFragment;
import com.sensei.companion.display.testing.DummyChromeTouchbar;
import com.sensei.companion.display.testing.DummyDesktopTouchbar;
import com.sensei.companion.display.program_managers.*;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.Hashtable;

public class TouchBarActivity extends FragmentActivity implements TouchBarFragment.OnTouchbarInteractionListener,
        ScreenSelectorFragment.OnScreenSelectorInteractionListener {

    private static final Hashtable<CommandsData.Program, Class<? extends TouchBarFragment>> touchbarClass = new Hashtable <> ();
    private int numPagerItems = 3;
    private MyPagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private static int popupHeight;
    private static int popupWidth;

    static {
        touchbarClass.put (CommandsData.Program.UNSUPPORTED, DummyDesktopTouchbar.class);
        touchbarClass.put (CommandsData.Program.WINDOWS, DummyDesktopTouchbar.class);
        touchbarClass.put (CommandsData.Program.CHROME, DummyChromeTouchbar.class);
        touchbarClass.put (CommandsData.Program.MICROSOFT_WORD, WordManager.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touchbar);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        //update message handler
        MessageHandler.setActivityReference(this);

        //instantiate view pager and its adapter
        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager)findViewById(R.id.pager_touchbar);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setPageMargin(16);
        CirclePageIndicator circlePageIndicator = (CirclePageIndicator)findViewById(R.id.circle_page_indicator);
        circlePageIndicator.setViewPager(viewPager);
        viewPager.setCurrentItem(1);

        //update view pager screen (to check if the view pager should be updated to only have 2 items)
        switchScreen(ScreenSelectorFragment.getSizeScreenList());

        ViewTreeObserver vto = viewPager.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.i (AppLauncher.DEBUG_TAG, "hi");
                popupHeight = viewPager.getMeasuredHeight();
                popupWidth = viewPager.getMeasuredWidth();
                ViewTreeObserver obs = viewPager.getViewTreeObserver();
                obs.removeOnGlobalLayoutListener(this);
            }
        });
    }

    private static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {
        MyPagerAdapter(FragmentManager fm) {
            super (fm);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            if (numPagerItems == 3) {
                switch (position) {
                    case 0:
                        return new DummyDesktopTouchbar();
                    case 1:
                        Class<? extends TouchBarFragment> fragmentClass = touchbarClass.get(MessageHandler.getCurrentProgram());
                        TouchBarFragment fragment = null;
                        try {
                            fragment = fragmentClass.newInstance();
                        } catch (Exception e) {
                            Log.e(AppLauncher.DEBUG_TAG, "[TouchBarActivity] Error instantiating fragment", e);
                        }
                        return fragment;
                    case 2:
                        return new TrackpadFragment();
                    default:
                        Log.d(AppLauncher.DEBUG_TAG, "[TouchBarActivity] Error - nonexistant fragment position");
                        return null;
                }
            }
            //the following assumes size is 2
            switch (position) {
                case 0:
                    return new DummyDesktopTouchbar();
                case 1:
                    return new TrackpadFragment();
                default:
                    Log.d(AppLauncher.DEBUG_TAG, "[TouchBarActivity] Error - nonexistant fragment position");
                    return null;
            }
        }

        @Override
        public int getCount() {
            return numPagerItems;
        }
    }

    public void showSettings (View v) {
        new DialogFragmentWindow().show(getSupportFragmentManager(), "");
    }

    //////////////////////////////////// Settings Dialog ////////////////////////////////////////

    public static class DialogFragmentWindow extends DialogFragment {
        @Override
        public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_settings_touchbar, container);
            Window window = getDialog().getWindow();
            if (window != null) {
                window.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
                WindowManager.LayoutParams p = window.getAttributes();
                p.y = (int) TouchBarActivity.convertDpToPixel(6, getContext());
                window.setAttributes(p);
            }
            return v;
        }

        @Override
        public void onStart() {
            Window window = getDialog().getWindow();
            //change size
            if (window != null) {
                window.setLayout(popupWidth, popupHeight);
                //set animation
                WindowManager.LayoutParams windowParams = window.getAttributes();
                windowParams.windowAnimations = android.R.style.Animation_Dialog;
                window.setAttributes(windowParams);
            }
            super.onStart();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////// Methods from implemented classes /////////////////////////////

    @Override
    public void sendMessage (CommandMessage msg) {
        ConnectManager.sendMessageToPC(msg);
    }

    @Override
    public RecyclerView getRecyclerView() {
        return (RecyclerView)findViewById(R.id.recycler_screens);
    }

    /*
        Call this method with the appropriate screen key (representing the appropriate fragment/touchbar) to change to that
        touchbar.
         */
    @Override
    public void switchScreen(int sizeList) {
        if (sizeList == 1){
            viewPager.setCurrentItem(0);
            numPagerItems = 2;
        } else {
            numPagerItems = 3;
        }
        pagerAdapter.notifyDataSetChanged();
        Log.i(AppLauncher.DEBUG_TAG, "[TouchBarActivity-switchScreen] " + MessageHandler.getCurrentProgram());
    }
}
