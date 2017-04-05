package com.sparq.application.userinterface.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.sparq.R;
import com.sparq.application.userinterface.model.PollItem;

import java.util.List;

/**
 * Created by sarahcs on 2/25/2017.
 */

public class PollListAdapter extends RecyclerView.Adapter<PollListAdapter.MyViewHolder>{

    private Context mContext;
    private List<PollItem> polls;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView pollName;
        public TextView pollDate;
        public ImageView pollImage;
        public ImageView pollStatusImage;

        public MyViewHolder(View view) {
            super(view);
            pollName = (TextView) view.findViewById(R.id.poll_name);
            pollDate = (TextView) view.findViewById(R.id.poll_date);
            pollImage = (ImageView) view.findViewById(R.id.poll_image);
            pollStatusImage = (ImageView) view.findViewById(R.id.poll_status);
        }
    }


    public PollListAdapter(Context mContext, List<PollItem> polls) {
        this.mContext = mContext;
        this.polls = polls;
    }

    @Override
    public PollListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_poll_list, parent, false);

        return new PollListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PollListAdapter.MyViewHolder holder, int position) {
        PollItem poll = polls.get(position);
        holder.pollName.setText(poll.getName());
        holder.pollDate.setText(poll.getDate().toString());

        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        // generate color based on a key (same key returns the same color), useful for list/grid views
        int color = generator.getColor(poll.getName());

        TextDrawable drawable = TextDrawable.builder()
                .buildRound(String.valueOf(poll.getName().charAt(0)), color);
        holder.pollImage.setImageDrawable(drawable);

        switch(poll.getState()){
            case PLAY:
                holder.pollStatusImage.setBackgroundResource(R.drawable.ic_bookmark_play);
                break;
            case PAUSE:
                holder.pollStatusImage.setBackgroundResource(R.drawable.ic_bookmark_pause);
                break;
            case STOP:
                holder.pollStatusImage.setBackgroundResource(R.drawable.ic_bookmark_stop);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return polls.size();
    }
}
