package com.sparq.application.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.sparq.R;
import com.sparq.application.model.AnswerItem;

import java.util.ArrayList;

/**
 * Created by sarahcs on 2/26/2017.
 */


public class AnswerListAdapter extends RecyclerView.Adapter<AnswerListAdapter.MyViewHolder> {

    private ArrayList<AnswerItem> answers;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView answerText;
        public TextView userText;
        public ImageView userImage;

        public MyViewHolder(View view) {
            super(view);
            answerText = (TextView) view.findViewById(R.id.answer_text);
            userText = (TextView) view.findViewById(R.id.username_text);
            userImage = (ImageView) view.findViewById(R.id.event_image);
        }
    }


    public AnswerListAdapter(ArrayList<AnswerItem> answers) {
        this.answers = answers;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_answer_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AnswerItem answer = answers.get(position);
        holder.answerText.setText(answer.getAnswer());
//        holder.userText.setText(answer.getUser().getUsername());
        holder.userText.setText("Jane Doe");

    }

    @Override
    public int getItemCount() {
        return answers.size();
    }
}
