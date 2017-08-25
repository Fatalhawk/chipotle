package com.sensei.companion.display.activities;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.sensei.companion.R;
import com.sensei.companion.communication.commands.CommandsData;
import com.sensei.companion.communication.connection.ConnectManager;
import com.sensei.companion.communication.connection.MessageHandler;
import com.sensei.companion.communication.messages.CommandMessage;
import com.sensei.companion.display.ScreenSelectorFragment;
import com.sensei.companion.display.testing.DummyChromeTouchbar;
import com.sensei.companion.display.testing.DummyDesktopTouchbar;
import com.sensei.companion.display.testing.DummyWordTouchbar;
import com.sensei.companion.display.testing.ScreenSelectorFragmentTest;
import com.sensei.companion.display.program_managers.*;

import java.util.Hashtable;

public class TouchBarActivity extends AppCompatActivity implements TouchBarFragment.OnTouchbarInteractionListener,
        ScreenSelectorFragment.OnScreenSelectorInteractionListener, ScreenSelectorFragmentTest.OnScreenSelectorInteractionListener {

    private static final Hashtable<CommandsData.Program, Class<? extends TouchBarFragment>> touchbarClass = new Hashtable <> ();

    static {
        touchbarClass.put (CommandsData.Program.WINDOWS, DummyDesktopTouchbar.class);
        touchbarClass.put (CommandsData.Program.CHROME, DummyChromeTouchbar.class);
        touchbarClass.put (CommandsData.Program.MICROSOFT_WORD, DummyWordTouchbar.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touchbar_test);

        MessageHandler.setActivityReference(this);

        MessageHandler.setCurrentProgram(CommandsData.Program.WINDOWS); //TODO: REMOVE LATER
    }

    ///////////////////////////// Methods from implemented classes /////////////////////////////

    @Override
    public void sendMessage (CommandMessage msg) {
        ConnectManager.sendMessageToPC(msg);
    }

    /*
    Call this method with the appropriate screen key (representing the appropriate fragment/touchbar) to change to that
    touchbar.
     */
    @Override
    public void switchScreen(CommandsData.Program screenKey) {
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
    }
}
