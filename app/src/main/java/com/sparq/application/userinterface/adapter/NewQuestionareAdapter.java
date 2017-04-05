package com.sparq.application.userinterface.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.sparq.R;
import com.sparq.application.userinterface.model.EventItem;

import java.util.List;

public class NewQuestionareAdapter extends RecyclerView.Adapter<NewQuestionareAdapter.MyViewHolder> {

    private List<EventItem> events;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView eventName;
        public TextView eventDate;
        public ImageView eventImage;

        public MyViewHolder(View view) {
            super(view);
//            eventName = (TextView) view.findViewById(R.id.event_name);
            eventImage = (ImageView) view.findViewById(R.id.event_image);
        }
    }


    public NewQuestionareAdapter(List<EventItem> events) {
        this.events = events;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_new_questionare_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        EventItem event = events.get(position);
        holder.eventName.setText(event.getEventName());
        holder.eventDate.setText(event.getDate().toString());

        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        // generate color based on a key (same key returns the same color), useful for list/grid views
        int color = generator.getColor(event.getEventName());

        TextDrawable drawable = TextDrawable.builder()
                .buildRound(String.valueOf(event.getEventName().charAt(0)), color);
        holder.eventImage.setImageDrawable(drawable);

    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}