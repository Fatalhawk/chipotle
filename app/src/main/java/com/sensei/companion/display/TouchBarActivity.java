package com.sensei.companion.display;

import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.sensei.companion.R;
import com.sensei.companion.display.program_managers.*;

import java.util.Hashtable;

public class TouchBarActivity extends AppCompatActivity implements TouchBarFragment.OnTouchbarInteractionListener,
        ScreenSelectorFragment.OnScreenSelectorInteractionListener {

    private final String DEBUG_TAG = "appMonitor";
    private final Hashtable<Integer, Class<? extends TouchBarFragment>> touchbarClass = new Hashtable <> ();
    private int currentScreen;

    public static final int DESKTOP = 0;
    public static final int WORD = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch_bar);
        //TODO: UNCOMMENT THE LINE BELOW WHEN READY TO COMMUNICATE WITH PC
        //ConnectManager.MessageHandler.setActivityReferenceToTouchBar(this);
        initProgramTouchbarCorrespondance();

        currentScreen = 0; //TODO: REMOVE LATER
    }

    private void initProgramTouchbarCorrespondance () {
        //Touchbar fragments corresponding to each windows program
        touchbarClass.put (DESKTOP, GenericProgramManager.class);
        touchbarClass.put (WORD, WordManager.class);
    }

    @Override
    public void sendMessage (String msg) {
        Log.i (DEBUG_TAG, "Test message");
    }

    @Override
    public void switchScreen(int screenKey) {
        if (currentScreen != screenKey) {
            // Create fragment and give it an argument specifying the article it should show
            Class<? extends TouchBarFragment> fragmentClass = touchbarClass.get(screenKey);
            TouchBarFragment newFragment = null;
            try {
                newFragment = fragmentClass.newInstance();
            } catch (Exception e) {
                Log.d(DEBUG_TAG, "Error instantiating fragment");
            }
            //Bundle args = new Bundle();
            //args.putInt(ArticleFragment.ARG_POSITION, position);
            //newFragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, newFragment);
            //add the transaction to the back stack so the user can navigate back
            transaction.addToBackStack(null); //TODO: MIGHT REMOVE LATER
            transaction.commit();

            currentScreen = screenKey;
        }
    }
}
