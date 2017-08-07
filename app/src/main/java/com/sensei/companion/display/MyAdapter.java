package com.sensei.companion.display;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import java.util.List;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.ViewGroup;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sensei.companion.R;


/**
 * Created by rojigangengatharan on 2017-08-05.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ScreensViewHolder> {

    private List<Screen> screensList;

    public MyAdapter(List<Screen> screensList) {
        this.screensList = screensList;
    }


    @Override
    public int getItemCount() {
        return screensList.size();

    }


    public ScreensViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dtop_cards,
                parent, false);
        return new ScreensViewHolder(itemView);

    }

    //do we need this override hear? or not @Override

    /**
     * public void onBindViewHolder(ViewHolder holder, int position) {
     * Screens si = screensList.get(position);
     * holder.name.setText(si.name);
     * holder.imageB = si.imageB;
     * <p>
     * holder.imageB.setImageResource(R.drawable.ic_desktop_windows_black_24dp); //change later when needed
     * holder.image_id = si.image_id;
     * <p>
     * }
     **/
    public void onBindViewHolder(ScreensViewHolder holder, int position) {
        Screen si = screensList.get(position);
        holder.name.setText(si.name);
        holder.imageB = si.imageB;

        holder.imageB.setImageResource(R.drawable.ic_desktop_windows_black_24dp); //change later when needed

    }

    public class ScreensViewHolder extends RecyclerView.ViewHolder {
        protected TextView name;
        protected ImageButton imageB;
        protected int image_id;
        protected Image image;


        public ScreensViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.txtView);
            imageB = (ImageButton) v.findViewById(R.id.imageButton);
            //be careful with error of having so many imagebuttons, maybe having same names?
            image_id = R.id.imageButton;


        }

    }
}


