package com.sparq.quizpolls.application.userinterface.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sparq.quizpolls.R;
import com.sparq.quizpolls.application.userinterface.model.QuestionItem;

import java.util.List;

public class NewQuestionareAdapter extends RecyclerView.Adapter<NewQuestionareAdapter.MyViewHolder> {

    private static int colors[] = {
            R.color.colorAccent,
            R.color.colorAccentDark,
    };

    private List<QuestionItem> questions;
    private Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView questionName;
        public TextView questionFormat;
        public LinearLayout questionImage;
        public ImageView questionDelete;

        public MyViewHolder(View view) {
            super(view);
            questionName = (TextView) view.findViewById(R.id.question_name);
            questionFormat = (TextView) view.findViewById(R.id.question_format);
            questionImage = (LinearLayout) view.findViewById(R.id.question_image);
            questionDelete = (ImageView) view.findViewById(R.id.question_delete);
        }
    }


    public NewQuestionareAdapter(Context context, List<QuestionItem> questions) {
        this.questions = questions;
        this.mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_new_questionare_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        QuestionItem question = questions.get(position);
        holder.questionName.setText(question.getQuestion());
        holder.questionFormat.setText(QuestionItem.getFormatAsString(question.getFormat()));

        holder.questionDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questions.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, questions.size());
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