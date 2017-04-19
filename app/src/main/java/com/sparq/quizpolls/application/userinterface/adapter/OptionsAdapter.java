package com.sparq.quizpolls.application.userinterface.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.sparq.quizpolls.R;
import com.sparq.quizpolls.application.userinterface.model.QuestionItem;
import com.sparq.quizpolls.application.userinterface.model.Questionare;

import java.util.ArrayList;

public class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.MyViewHolder> {

    private Questionare.QUESTIONARE_TYPE type;
    private ArrayList<String> options;
    private QuestionItem.FORMAT format;
    private ArrayList<Integer> correctOptions;

    private int lastPosition;
    private RadioButton lastChecked = null;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView optionName;
        public ImageView deleteOption;
        public RadioButton radioButton;
        public CheckBox checkBox;
        public LinearLayout correctAnswerLayout;

        public MyViewHolder(View view) {
            super(view);
            optionName = (TextView) view.findViewById(R.id.option_text);
            deleteOption = (ImageView) view.findViewById(R.id.delete_option);
            radioButton = (RadioButton) view.findViewById(R.id.correct_answer_radio);
            checkBox = (CheckBox) view.findViewById(R.id.correct_answer_check);
            correctAnswerLayout = (LinearLayout) view.findViewById(R.id.correct_answer_layout);
            correctAnswerLayout.setVisibility(View.GONE);

        }
    }


    public OptionsAdapter(Questionare.QUESTIONARE_TYPE type, QuestionItem.FORMAT format, ArrayList<String> options) {
        this.type = type;
        this.format = format;
        this.options = options;

        if(type == Questionare.QUESTIONARE_TYPE.QUIZ){
            correctOptions = new ArrayList<>(0);
        }

        Log.i("HERE", type.toString());
    }

    public void resetData(){
        lastPosition = -1;
        lastChecked = null;
        correctOptions.clear();
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_new_option, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        final String option = options.get(position);

        holder.optionName.setText(options.get(position));

//        holder.deleteOption.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                options.remove(option);
//                notifyDataSetChanged();
//            }
//        });

        holder.deleteOption.setVisibility(View.GONE);


        holder.radioButton.setTag(new Integer(position+1));

        holder.radioButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                RadioButton cb = (RadioButton) v;
                int clickedPos = ((Integer)cb.getTag()).intValue();

                if(cb.isChecked() && clickedPos != lastPosition)
                {
                    if(lastChecked != null && clickedPos != lastPosition)
                    {
                        lastChecked.setChecked(false);
                    }

                    lastChecked = cb;
                    lastPosition = clickedPos;
                    correctOptions.clear();
                    correctOptions.add(clickedPos);
                    notifyDataSetChanged();
                }

                Log.i("HERE", correctOptions.toString());
            }
        });

        holder.checkBox.setTag(new Integer(position+1));

        holder.checkBox.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CheckBox cb = (CheckBox) v;
                int clickedPos = ((Integer)cb.getTag()).intValue();

                if(cb.isChecked()){
                    correctOptions.add(clickedPos);
                }
                else {
                    correctOptions.remove(Integer.valueOf(clickedPos));
                }

                notifyDataSetChanged();

                Log.i("HERE", correctOptions.toString());
            }
        });

        if(type == Questionare.QUESTIONARE_TYPE.QUIZ){
            holder.correctAnswerLayout.setVisibility(View.VISIBLE);

            switch(format){
                case MCQ_SINGLE:
                    holder.checkBox.setVisibility(View.GONE);
                    holder.radioButton.setVisibility(View.VISIBLE);
                    break;
                case MCQ_MULTIPLE:
                    holder.radioButton.setVisibility(View.GONE);
                    holder.checkBox.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        if(options != null) {
            return options.size();
        }
        else{
            return 0;
        }

    }

    public int getChosenAnswerCount(){
        if(correctOptions != null){
            return correctOptions.size();
        }
        else{
            return 0;
        }
    }

    public ArrayList<String> getOptions(){
        return this.options;
    }

    public ArrayList<Integer> getCorrectOptions(){
        return correctOptions;
    }

}