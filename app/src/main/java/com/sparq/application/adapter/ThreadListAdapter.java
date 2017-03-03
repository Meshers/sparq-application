package com.sparq.application.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.sparq.R;
import com.sparq.application.model.ConversationThread;
import com.sparq.application.model.QuizItem;

import java.util.List;

/**
 * Created by sarahcs on 2/24/2017.
 */

public class ThreadListAdapter extends RecyclerView.Adapter<ThreadListAdapter.MyViewHolder>{

    private List<ConversationThread> threads;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView threadName;
        public TextView threadDate;
        public ImageView threadImage;

        public MyViewHolder(View view) {
            super(view);
            threadName = (TextView) view.findViewById(R.id.thread_name);
            threadDate = (TextView) view.findViewById(R.id.thread_date);
            threadImage = (ImageView) view.findViewById(R.id.thread_image);
        }
    }


    public ThreadListAdapter(List<ConversationThread> threads) {
        this.threads = threads;
    }

    @Override
    public ThreadListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_thread_list, parent, false);

        return new ThreadListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ThreadListAdapter.MyViewHolder holder, int position) {
        ConversationThread thread = threads.get(position);
        holder.threadName.setText(thread.getQuestion());
    holder.threadDate.setText(thread.getDate().toString());

        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        // generate color based on a key (same key returns the same color), useful for list/grid views
        int color = generator.getColor(thread.getQuestion());

        TextDrawable drawable = TextDrawable.builder()
                .buildRound(String.valueOf(thread.getQuestion().charAt(0)), color);
        holder.threadImage.setImageDrawable(drawable);

    }

    @Override
    public int getItemCount() {
        return threads.size();
    }
}
