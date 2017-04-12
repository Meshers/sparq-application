package com.sparq.application.userinterface.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sparq.R;
import com.sparq.application.userinterface.model.QuestionItem;

import java.util.HashMap;
import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.MyViewHolder> {

    private static int colors[] = {
            R.color.warning,
            R.color.warningDark,
    };

    private HashMap<Integer, QuestionItem> questions;
    private Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView questionName, questionFormat;
        public ImageView deleteOption;
        public LinearLayout questionImage;

        public MyViewHolder(View view) {
            super(view);
            questionName = (TextView) view.findViewById(R.id.question_name);
            questionFormat = (TextView) view.findViewById(R.id.question_format);
            deleteOption = (ImageView) view.findViewById(R.id.question_delete);
            questionImage = (LinearLayout) view.findViewById(R.id.question_image);
        }
    }


    public QuestionAdapter(Context context, HashMap<Integer, QuestionItem> questions) {
        mContext = context;
        this.questions = questions;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_new_questionare_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.questionName.setText(questions.get(position + 1).getQuestion());
        holder.questionFormat.setText(QuestionItem.getFormatAsString(questions.get(position + 1).getFormat()));

        holder.deleteOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questions.remove(position + 1);
                notifyItemRemoved(position);
                notifyDataSetChanged();
            }
        });

        if(position % 2 == 0){
            holder.questionImage.setBackgroundColor(mContext.getResources().getColor(colors[0]));
        }
        else{
            holder.questionImage.setBackgroundColor(mContext.getResources().getColor(colors[1]));
        }
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

}