package com.sparq.application.userinterface.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import com.sparq.R;
import com.sparq.application.userinterface.model.EventItem;
import com.sparq.application.userinterface.model.QuestionItem;
import com.sparq.application.userinterface.model.Questionare;

import java.util.ArrayList;

/**
 * Created by sarahcs on 2/26/2017.
 */

public class QuestionareAdapter extends RecyclerView.Adapter<QuestionareAdapter.MyViewHolder> {

    private ArrayList<String> options;
    private QuestionItem.FORMAT format;

    private static RadioButton lastChecked = null;
    private int lastCheckedPos = 0;
    private static ArrayList<Integer> choices = new ArrayList<>();

    private int mRowIndex = -1;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView optionName;
        public CheckBox checkBox;
        public RadioButton radioButton;

        public MyViewHolder(View view) {
            super(view);
            optionName = (TextView) view.findViewById(R.id.option_text);

            switch(format){
                case MCQ_SINGLE:
                    radioButton = (RadioButton) view.findViewById(R.id.option_choice);
                    break;
                case MCQ_MULTIPLE:
                    checkBox = (CheckBox) view.findViewById(R.id.option_choice);
                    break;
            }

        }
    }


    public QuestionareAdapter() {

    }

    public void setData(ArrayList<String> options, QuestionItem.FORMAT type) {
        this.options = options;
        this.format = type;
        notifyDataSetChanged();
    }

    public void setRowIndex(int index) {
        mRowIndex = index;
    }

    @Override
    public QuestionareAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;

        try{
            switch(format){
                case MCQ_SINGLE:
                    itemView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_single_choice_answer, parent, false);
                    break;
                case MCQ_MULTIPLE:
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

        switch(format){
            case MCQ_SINGLE:

                holder.radioButton.setTag(new Integer(position+1));

                holder.radioButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        RadioButton cb = (RadioButton) v;
                        int clickedPos = ((Integer)cb.getTag()).intValue();

                        if(cb.isChecked())
                        {
                            if(lastChecked != null)
                            {
                                lastChecked.setChecked(false);
                            }

                            lastChecked = cb;
                            choices.add(clickedPos);
                            Log.i("HERE in func", String.valueOf(lastCheckedPos));
                        }
                        else{
                            lastChecked = null;
                            choices.remove(Integer.valueOf(clickedPos));
                        }

                    }
                });

                break;
            case MCQ_MULTIPLE:

                holder.checkBox.setTag(new Integer(position+1));

                holder.checkBox.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        CheckBox cb = (CheckBox) v;
                        int clickedPos = ((Integer)cb.getTag()).intValue();

                        if(cb.isChecked())
                            choices.add(clickedPos);
                        else
                            choices.remove(Integer.valueOf(clickedPos));
                    }
                });

                break;
        }

    }

    @Override
    public int getItemCount() {
        return options.size();
    }

    public ArrayList<Integer> getChoices(){

        return choices;
    }
}
