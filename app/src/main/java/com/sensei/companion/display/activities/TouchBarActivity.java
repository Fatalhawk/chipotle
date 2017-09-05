package com.sensei.companion.display.activities;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;

import com.sensei.companion.R;
import com.sensei.companion.communication.commands.CommandsData;
import com.sensei.companion.communication.connection.ConnectManager;
import com.sensei.companion.communication.connection.MessageHandler;
import com.sensei.companion.communication.messages.CommandMessage;
import com.sensei.companion.display.screen_selector.Screen;
import com.sensei.companion.display.screen_selector.ScreenSelectorFragment;
import com.sensei.companion.display.testing.DummyChromeTouchbar;
import com.sensei.companion.display.testing.DummyDesktopTouchbar;
import com.sensei.companion.display.testing.DummyWordTouchbar;
import com.sensei.companion.display.program_managers.*;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.Hashtable;

public class TouchBarActivity extends FragmentActivity implements TouchBarFragment.OnTouchbarInteractionListener,
        ScreenSelectorFragment.OnScreenSelectorInteractionListener {

    private static final Hashtable<CommandsData.Program, Class<? extends TouchBarFragment>> touchbarClass = new Hashtable <> ();
    private int numPagerItems = 3;
    private MyPagerAdapter pagerAdapter;
    private ViewPager viewPager;

    static {
        touchbarClass.put (CommandsData.Program.UNSUPPORTED, DummyDesktopTouchbar.class);
        touchbarClass.put (CommandsData.Program.WINDOWS, DummyDesktopTouchbar.class);
        touchbarClass.put (CommandsData.Program.CHROME, DummyChromeTouchbar.class);
        touchbarClass.put (CommandsData.Program.MICROSOFT_WORD, DummyWordTouchbar.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touchbar_test);
        //want to run it on Android 2.3 and newer as a "sensorLandscape" configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
        else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        //update message handler
        MessageHandler.setActivityReference(this);
        MessageHandler.setCurrentProgram(CommandsData.Program.WINDOWS); //TODO: REMOVE LATER

        //instantiate view pager and its adapter
        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager)findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setPageMargin(16);
        CirclePageIndicator circlePageIndicator = (CirclePageIndicator)findViewById(R.id.circle_page_indicator);
        circlePageIndicator.setViewPager(viewPager);
        viewPager.setCurrentItem(1);
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
            switch (position) {
                case 0:
                    return new DummyDesktopTouchbar();
                case 1:
                    Class<? extends TouchBarFragment> fragmentClass = touchbarClass.get(MessageHandler.getCurrentProgram());
                    TouchBarFragment fragment = null;
                    try {
                        fragment = fragmentClass.newInstance();
                    } catch (InstantiationException|IllegalAccessException e) {
                        Log.e(AppLauncher.DEBUG_TAG, "[TouchBarActivity] Error instantiating fragment", e);
                    }
                    return fragment;
                case 2:
                    return new WordManager();
                default:
                    Log.d (AppLauncher.DEBUG_TAG, "[TouchBarActivity] Error - nonexistant fragment position");
                    return null;
            }
        }

        @Override
        public int getCount() {
            return numPagerItems;
        }
    }

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
    public void switchScreen() {
        pagerAdapter.notifyDataSetChanged();

        Log.i(AppLauncher.DEBUG_TAG, "[TouchBarActivity-switchScreen] " + MessageHandler.getCurrentProgram());
        /*
        if (MessageHandler.getCurrentProgram() != screenKey) {
            // Create fragment and give it an argument specifying the article it should show
            Class<? extends TouchBarFragment> fragmentClass = touchbarClass.get(screenKey);
            TouchBarFragment newFragment = null;
            try {
                newFragment = fragmentClass.newInstance();
            } catch (Exception e) {
                Log.d(AppLauncher.DEBUG_TAG, "[TouchBarActivity] Error instantiating fragment");
            }
            //Bundle args = new Bundle();
            //args.putInt(ArticleFragment.ARG_POSITION, position);
            //newFragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, newFragment);
            //add the transaction to the back stack so the user can navigate back
            transaction.addToBackStack(null); //TODO: MIGHT REMOVE LATER
            transaction.commit();

            MessageHandler.setCurrentProgram(screenKey);
        }
        */
    }
}
