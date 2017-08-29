package com.sensei.companion.display.screen_selector;

import android.support.v7.widget.RecyclerView;
import java.util.List;

import android.view.ViewGroup;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.Button;
import android.content.Context;

import com.sensei.companion.R;


/**
 * Created by rojigangengatharan on 2017-08-05.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<Screen> screensList;

    private Context mContext;

    public MyAdapter(Context context, List<Screen> p_screenList)
    {
        this.screensList = p_screenList;
        mContext = context;

    }


    public Context getContext(){
        return mContext;
    }

    @Override
    public int getItemCount() {
        return screensList.size();

    }


    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View screenView = inflater.inflate(R.layout.dtop_cards, parent, false);

        ViewHolder viewHolder = new ViewHolder(screenView);
        return viewHolder;

    }


    public void onBindViewHolder(ViewHolder holder, int position) {
        //Image Buttons are "infod" here
        Screen si = screensList.get(position);
        System.out.println(si.getName());
        holder.button.setText(si.getName());

        //use bitmaps for imagebutton, for now testing with constant images



        //holder.imageB.setImageResource(R.drawable.ic_desktop_windows_black_24dp);


        //change later when needed
        //search up how all this stuff really works and how to set background color
        //by getting context
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected Button button;

        public ViewHolder(View itemView) {
            super(itemView);
            button = (Button) itemView.findViewById(R.id.ButtonT);
            //be careful with error of having so many imagebuttons, maybe having same names?

        }

    }
}


