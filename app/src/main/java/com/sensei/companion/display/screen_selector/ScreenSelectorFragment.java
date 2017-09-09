package com.sensei.companion.display.screen_selector;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
import android.graphics.Bitmap;
import android.widget.TextView;

import com.sensei.companion.R;
import com.sensei.companion.communication.commands.CommandsData;
import com.sensei.companion.communication.commands.SystemCommandReceiver;
import com.sensei.companion.communication.connection.ConnectManager;
import com.sensei.companion.communication.connection.MessageHandler;
import com.sensei.companion.communication.messages.CommandMessage;
import com.sensei.companion.communication.messages.ProgramInfoMessage;
import com.sensei.companion.display.activities.AppLauncher;
import com.sensei.companion.proto.ProtoMessage;

public class ScreenSelectorFragment extends Fragment {
    private static OnScreenSelectorInteractionListener mListener;
    private static List<Screen> screens;
    private static RecyclerAdapter recyclerAdapter;
    private static Hashtable<Integer, Integer> programIdToPosition = new Hashtable<>();
    private static Bitmap tmpImage;

    public static void removeExistingScreen (int screenId){
        int position = programIdToPosition.get(screenId);
        screens.remove(position);
        recyclerAdapter.notifyItemRemoved(position);
    }

    public static void setCurrentScreenNew(ProgramInfoMessage message){
        Screen screen = new Screen(message.getProgramName(), message.getProgramId(), message.getProgramType(), message.getPicture());
        screens.add(0, screen);
        recyclerAdapter.notifyItemInserted(0);
        RecyclerView view = mListener.getRecyclerView();
        view.scrollToPosition(0);
        Log.i (AppLauncher.DEBUG_TAG, screen.getName());
    }

    public static void setCurrentScreenExisting (int position) {
        Screen newCurrent = screens.remove(position);
        screens.add(0, newCurrent);
        recyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tmpImage = BitmapFactory.decodeResource(getContext().getResources(),
                R.drawable.tempdesktop);
        View view1 = inflater.inflate(R.layout.fragment_screen_selector, container, false);
        RecyclerView recyclerView = (RecyclerView) view1.findViewById(R.id.recycler_screens);
        //later change this so that it waits using perhaps a LinkedBlockingQueue for the list of
        //opened programs when the app starts
        screens = createList(3);
        recyclerAdapter = new RecyclerAdapter(screens, recyclerView.getContext());
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        //update screen
        MessageHandler.setCurrentProgram(screens.get(0).getProgram());
        return view1;
    }

    private List<Screen> createList(int size) {
        List<Screen> result = new ArrayList<>();
        Screen ci;
        ci = new Screen("Microsoft Word", 1, CommandsData.Program.MICROSOFT_WORD.toString(), tmpImage);
        result.add (ci);
        ci = new Screen("Chrome", 3, CommandsData.Program.CHROME.toString(), tmpImage);
        result.add (ci);
        return result;
    }

    static class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {
        private List<Screen> screenList;
        private LayoutInflater layoutInflater;

        RecyclerAdapter(List<Screen> screenList, Context parentContext) {
            this.screenList = screenList;
            this.layoutInflater = LayoutInflater.from(parentContext);
        }

        @Override
        public int getItemCount() {
            return screens.size();
        }

        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View screenView = layoutInflater.inflate(R.layout.viewholder_screen, parent, false);
            //The parent is the recyler view --> for getMeasuredWidth/Height to return non-zero #s, must set that to match_parent
            int parentWidthMinusMarg = parent.getMeasuredWidth()-(int)convertDpToPixel(2*8, parent.getContext()); //get rid of left/right margins
            int parentHeightMinusMarg = parent.getMeasuredHeight()-(int)convertDpToPixel(2*4, parent.getContext()); //get rid of bot/top margins
            int imageWidth = (int)(parentWidthMinusMarg-5*convertDpToPixel(4, parent.getContext()))/4; //get rid of left/right margins
            screenView.setMinimumWidth(imageWidth);
            screenView.setMinimumHeight(parentHeightMinusMarg);
            return new MyViewHolder(screenView, imageWidth);
        }

        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            final Screen si = screenList.get(position);
            programIdToPosition.put(si.getProgramId(), position);
            holder.screenNameView.setText(si.getName());
            if (position == 0) {
                holder.image.setBackground(holder.image.getResources().getDrawable(R.drawable.customborder_openscreen));
                int dp2 = (int)convertDpToPixel(2, holder.image.getContext());
                holder.image.setPadding(dp2, dp2, dp2, dp2);
            }
            holder.image.setMaxWidth(holder.imageWidth);
            holder.image.setImageBitmap(si.getScreenImage());
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConnectManager.sendMessageToPC(new CommandMessage(ProtoMessage.CompRequest.CommandInfo.Target.SYSTEM, SystemCommandReceiver.SystemCommand.OPEN.toString(), ""+si.getProgramId()));
                    setCurrentScreenExisting(holder.getAdapterPosition());
                    MessageHandler.setCurrentProgram(si.getProgram());
                    mListener.switchScreen();
                }
            });
            //use bitmaps for imagebutton, for now testing with constant images
            //holder.imageB.setImageResource(R.drawable.ic_desktop_windows_black_24dp);
        }

        private float convertDpToPixel(float dp, Context context){
            Resources resources = context.getResources();
            DisplayMetrics metrics = resources.getDisplayMetrics();
            return dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageButton image;
            TextView screenNameView;
            int imageWidth;

            MyViewHolder(View itemView, final int imageWidth) {
                super(itemView);
                this.imageWidth = imageWidth;
                image = (ImageButton)itemView.findViewById(R.id.ButtonT);
                screenNameView = (TextView)itemView.findViewById(R.id.name_screen);

                //be careful with error of having so many imagebuttons, maybe having same names?
            }
        }
    }

    ////////////////////////////////////fragment stuff /////////////////////////////////////////
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnScreenSelectorInteractionListener) {
            mListener = (OnScreenSelectorInteractionListener) context;
        } else {
            Log.d (AppLauncher.DEBUG_TAG, "must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnScreenSelectorInteractionListener {
        void switchScreen ();
        RecyclerView getRecyclerView();
    }
}
