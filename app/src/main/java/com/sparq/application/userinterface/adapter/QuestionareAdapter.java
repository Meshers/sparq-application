package com.sparq.application.userinterface.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sparq.R;
import com.sparq.application.userinterface.model.EventItem;
import com.sparq.application.userinterface.model.Questionare;

import java.util.ArrayList;

/**
 * Created by sarahcs on 2/26/2017.
 */

public class QuestionareAdapter extends RecyclerView.Adapter<QuestionareAdapter.MyViewHolder> {

    private ArrayList<String> options;
    private int type;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView optionName;

        public MyViewHolder(View view) {
            super(view);
            optionName = (TextView) view.findViewById(R.id.option_text);

        }
    }


    public QuestionareAdapter(ArrayList<String> options, int type) {
        this.options = options;
        this.type = type;
    }

    @Override
    public QuestionareAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;

        try{
            switch(type){
                case 1:
                    itemView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_single_choice_answer, parent, false);
                    break;
                case 2:
                    itemView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_multiple_choice_answer, parent, false);
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }
        catch (IllegalArgumentException e){
            Log.e("Illegal format", "no such format of question", e);
        }

        return new QuestionareAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(QuestionareAdapter.MyViewHolder holder, int position) {
        String option = options.get(position);
        holder.optionName.setText(option);

    }

    @Override
    public int getItemCount() {
        return options.size();
    }
}
